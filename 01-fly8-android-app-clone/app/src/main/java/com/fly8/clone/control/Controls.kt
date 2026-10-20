package com.fly8.clone.control

import android.content.Context
import com.fly8.clone.Fly8
import com.fly8.clone.data.LogCategory
import com.fly8.clone.server.ControlServer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

enum class ConnectionMethod(val label: String) {
    WIRELESS("Wireless debugging"), ROOT("Root"), SHIZUKU("Shizuku"), COMPUTER("Computer activation")
}

data class ControlState(
    val connected: Boolean = false,
    val busy: Boolean = false,
    val method: ConnectionMethod? = null,
    val serverUid: Int? = null,
    val message: String = DEFAULT_MESSAGE
)

const val DEFAULT_MESSAGE =
    "Connect to start and control your clones. Reconnect after a device restart or if Android stops the app."

/**
 * Single source of truth for the control connection. "Connected" means the control server
 * answered an authenticated PING — never just that a button was tapped.
 */
object Controls {
    private lateinit var app: Context
    private val _state = MutableStateFlow(ControlState())
    val state: StateFlow<ControlState> = _state
    private var client: ControlClient? = null
    private var input: ControlClient? = null
    // One serialized connection owner: concurrent taps, recovery alarms and UI refreshes must
    // never open overlapping pair/connect sessions (guide §5).
    private val connectGate = kotlinx.coroutines.sync.Mutex()

    fun init(context: Context) { app = context.applicationContext }

    private fun token() = ServerLauncher.token(app)
    private fun prefs() = app.getSharedPreferences("fly8_controls", Context.MODE_PRIVATE)
    val paired get() = prefs().getBoolean("paired", false)
    private val lastMethod
        get() = runCatching { ConnectionMethod.valueOf(prefs().getString("method", "")!!) }.getOrNull()

    @Synchronized
    private fun clientOrNew() = client ?: ControlClient(token()).also { client = it }

    /** Checks whether a server is already running (for example after FLY8 itself restarted). */
    suspend fun refresh(): Boolean = withContext(Dispatchers.IO) {
        val c = clientOrNew()
        var version = c.ping()
        if (version != null && version != ControlServer.VERSION) {
            // A server from an older FLY8 build is still running; replace it.
            runCatching { c.request("QUIT", 1_000) }
            c.close()
            version = null
        }
        val ok = version != null
        val method = _state.value.method ?: lastMethod
        val was = _state.value.connected
        _state.value = _state.value.copy(
            connected = ok, busy = false, method = method,
            message = when {
                ok -> "Controls connected · ${method?.label ?: "active"}"
                was -> "Controls disconnected. Android may have stopped them — reconnect to continue."
                else -> _state.value.message
            }
        )
        ok
    }

    private suspend fun awaitServer(): Boolean {
        repeat(24) { if (refresh()) return true; delay(250) }
        return false
    }

    /** Short cause for Support details; the pairing code itself is never part of an exception. */
    private fun describe(t: Throwable): String {
        val root = generateSequence(t) { it.cause }.last()
        return root.javaClass.simpleName + (root.message?.let { ": ${it.take(120)}" } ?: "")
    }

    private fun setBusy(msg: String) { _state.value = _state.value.copy(busy = true, message = msg) }

    private fun fail(msg: String): Result<Unit> {
        _state.value = _state.value.copy(busy = false, connected = false, message = msg)
        Fly8.log.add(LogCategory.CONTROLS, msg)
        return Result.failure(IllegalStateException(msg))
    }

    private suspend fun finishConnect(method: ConnectionMethod): Result<Unit> {
        _state.value = _state.value.copy(method = method)
        if (!awaitServer()) {
            return fail("${method.label} responded, but the FLY8 control server did not start. Open Support details.")
        }
        prefs().edit().putString("method", method.name).apply()
        Fly8.log.add(LogCategory.CONTROLS, "Controls connected via ${method.label}")
        keepFly8Alive()
        return Result.success(Unit)
    }

    /** Pair with the code Android shows. The code is used once and never stored or logged. */
    suspend fun pair(port: Int, code: String): Result<Unit> = withContext(Dispatchers.IO) {
        connectGate.withLock {
            if (!(code.length == 6 && code.all { it.isDigit() })) return@withContext fail("Enter the six-digit pairing code.")
            if (port !in 1..65535) return@withContext fail("The pairing port is invalid.")
            setBusy("Pairing…")
            val error = runCatching { AdbManager.get(app).pair("127.0.0.1", port, code) }.exceptionOrNull()
            if (error != null) {
                return@withContext fail(
                    "Wireless debugging pairing failed (${describe(error)}). Check that the pairing dialog is still " +
                        "open and the code is correct."
                )
            }
            prefs().edit().putBoolean("paired", true).apply()
            Fly8.log.add(LogCategory.CONTROLS, "Wireless debugging paired")
            connectWirelessLocked(null)
        }
    }

    suspend fun connectWireless(manualPort: Int? = null): Result<Unit> = withContext(Dispatchers.IO) {
        connectGate.withLock { connectWirelessLocked(manualPort) }
    }

    private suspend fun connectWirelessLocked(manualPort: Int?): Result<Unit> {
        if (refresh()) return Result.success(Unit)
        setBusy("Searching for Wireless debugging…")
        val adb = AdbManager.get(app)
        val port = manualPort ?: Discovery.findPort(app, Discovery.CONNECT, 8_000)
            ?: return fail(
                "Could not find Wireless debugging. Enable it in Developer options, stay on Wi‑Fi, then try again."
            )
        val attempt = runCatching { adb.isConnected || adb.connect("127.0.0.1", port) }
        if (attempt.getOrDefault(false) != true) {
            val why = attempt.exceptionOrNull()?.let { " (${describe(it)})" }.orEmpty()
            return fail(
                if (paired) "Wireless debugging refused the connection$why. If Android forgot this pairing, pair again."
                else "This device is not paired yet. Tap Start pairing first."
            )
        }
        val (code, out) = runCatching { adb.shell(ServerLauncher.command(app)) }
            .getOrElse { -1 to (it.message ?: it.javaClass.simpleName) }
        if (code != 0) return fail("Could not start controls over Wireless debugging: ${out.take(160)}")
        return finishConnect(ConnectionMethod.WIRELESS)
    }

    suspend fun connectRoot(): Result<Unit> = withContext(Dispatchers.IO) {
        connectGate.withLock {
            setBusy("Requesting root access…")
            val r = Privileged.runAsRoot(ServerLauncher.command(app))
            if (!r.ok) return@withContext fail(r.output.ifBlank { "Root access was denied." })
            finishConnect(ConnectionMethod.ROOT)
        }
    }

    suspend fun connectShizuku(): Result<Unit> = withContext(Dispatchers.IO) {
        connectGate.withLock {
            when (val status = Privileged.shizukuStatus()) {
                null -> Unit
                "permission" -> return@withContext fail("Grant FLY8 permission in Shizuku, then tap Connect with Shizuku again.")
                else -> return@withContext fail(status)
            }
            setBusy("Starting controls with Shizuku…")
            val r = Privileged.runWithShizuku(ServerLauncher.command(app))
            if (!r.ok) return@withContext fail(r.output.ifBlank { "Shizuku could not start FLY8 controls." })
            finishConnect(ConnectionMethod.SHIZUKU)
        }
    }

    /** Waits while the user runs the copied activation command on a computer. */
    suspend fun awaitComputerActivation(): Result<Unit> = withContext(Dispatchers.IO) {
        setBusy("Waiting for the activation command from your computer…")
        repeat(180) {
            if (refresh()) return@withContext connectGate.withLock { finishConnect(ConnectionMethod.COMPUTER) }
            setBusy("Waiting for the activation command from your computer…")
            delay(1_000)
        }
        fail("No activation detected. Run the copied command on the computer, then try again.")
    }

    suspend fun disconnect() = withContext(Dispatchers.IO) {
        runCatching { clientOrNew().request("QUIT", 1_000) }
        client?.close(); input?.close(); client = null; input = null
        runCatching { AdbManager.get(app).disconnect() }
        _state.value = ControlState(message = "Controls disconnected.")
        Fly8.log.add(LogCategory.CONTROLS, "Controls disconnected by user")
    }

    /** Blocking allowlisted operation through the control server. Call off the main thread. */
    fun op(name: String, vararg args: String): ExecResult {
        if (!_state.value.connected) return ExecResult(-2, "Controls are disconnected. Connect controls first.")
        return try {
            clientOrNew().op(name, *args)
        } catch (t: IllegalArgumentException) {
            ExecResult(-3, "Rejected invalid request: ${t.message}")
        } catch (t: Throwable) {
            _state.value = _state.value.copy(connected = false, message = "Controls disconnected. Reconnect to continue.")
            ExecResult(-2, "Controls disconnected (${t.javaClass.simpleName}).")
        }
    }

    /** Fire-and-forget input line (touch, key, text) on a dedicated channel. */
    fun sendInput(line: String) {
        if (!_state.value.connected) return
        val c = synchronized(this) { input ?: ControlClient(token()).also { input = it } }
        c.send(line)
    }

    /** Keeps FLY8 out of Doze/background restrictions so Live sessions survive screen-off. */
    private fun keepFly8Alive() {
        op("KEEP_ALIVE", app.packageName)
        op("ALLOW_BACKGROUND", app.packageName)
    }
}
