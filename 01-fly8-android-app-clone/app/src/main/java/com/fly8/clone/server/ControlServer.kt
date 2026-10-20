package com.fly8.clone.server

import android.os.SystemClock
import android.util.Base64
import android.view.InputDevice
import android.view.InputEvent
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

/**
 * FLY8 control server, started under the shell (or root) identity via `app_process`.
 *
 * Security model:
 *  - listens on 127.0.0.1 only and requires the per-install token held privately by FLY8;
 *  - there is NO arbitrary shell: only the fixed operations in [Ops] exist, each argument is
 *    validated against a strict pattern and commands run as argv (never through `sh -c`);
 *  - input injection is limited to secondary (virtual) displays, never the main display 0.
 *
 * Protocol (one line per request; first line `AUTH <token>`):
 *   PING                          -> OK <version> <uid>
 *   OP <name> <args...>           -> OK <exitCode> <base64 output> | ERR <base64 reason>
 *   TOUCH <display> <action> <idx> <downTime> <n> <id:x:y>...   (no reply)
 *   KEY <display> <keyCode>                                     (no reply)
 *   TEXT <display> <base64 text>                                (no reply)
 *   QUIT                          -> OK bye
 */
object ControlServer {
    const val VERSION = 4
    const val PORT = 39281

    @JvmStatic
    fun main(args: Array<String>) {
        val token = args.getOrNull(0)?.takeIf { it.length >= 32 } ?: run {
            System.err.println("usage: ControlServer <token>")
            exitProcess(2)
        }
        val server = try {
            ServerSocket(PORT, 8, InetAddress.getByName("127.0.0.1"))
        } catch (t: Throwable) {
            System.err.println("FLY8 control server already running: ${t.message}")
            exitProcess(0)
        }
        while (true) {
            val socket = try { server.accept() } catch (t: Throwable) { continue }
            Thread({ serve(socket, token) }, "fly8-client").apply { isDaemon = true }.start()
        }
    }

    private fun serve(socket: Socket, token: String) {
        socket.use { s ->
            s.tcpNoDelay = true
            s.soTimeout = 10_000
            val reader = BufferedReader(InputStreamReader(s.getInputStream(), Charsets.UTF_8))
            val auth = runCatching { reader.readLine() }.getOrNull() ?: return
            val given = auth.removePrefix("AUTH ").toByteArray()
            if (!auth.startsWith("AUTH ") || !MessageDigest.isEqual(given, token.toByteArray())) return
            s.soTimeout = 0
            val out = s.getOutputStream()
            reply(out, "OK auth")
            while (true) {
                val line = runCatching { reader.readLine() }.getOrNull() ?: break
                if (line.length > 4096) break
                try {
                    handle(line, out)
                } catch (t: Throwable) {
                    if (line.startsWith("OP") || line.startsWith("PING")) reply(out, "ERR " + b64(t.message ?: "error"))
                }
            }
        }
    }

    private fun reply(out: OutputStream, text: String) {
        synchronized(out) { out.write((text + "\n").toByteArray()); out.flush() }
    }

    private fun handle(line: String, out: OutputStream) {
        val p = line.split(' ')
        when (p[0]) {
            "PING" -> reply(out, "OK $VERSION ${android.os.Process.myUid()}")
            "OP" -> {
                val argv = Ops.build(p[1], p.drop(2))
                val (code, output) = run(argv, Ops.timeoutFor(p[1]))
                reply(out, "OK $code " + b64(output))
            }
            "TOUCH" -> Input.touch(p)
            "KEY" -> Input.key(Ops.display(p[1]), p[2].toInt().coerceIn(1, 400))
            "TEXT" -> Input.text(Ops.display(p[1]), String(Base64.decode(p[2], Base64.NO_WRAP), Charsets.UTF_8).take(500))
            "QUIT" -> { reply(out, "OK bye"); exitProcess(0) }
        }
    }

    private fun run(argv: List<String>, timeoutMs: Long): Pair<Int, String> {
        val proc = ProcessBuilder(argv).redirectErrorStream(true).start()
        val buf = ByteArrayOutputStream()
        val pump = Thread {
            val b = ByteArray(8192)
            val input = proc.inputStream
            while (true) {
                val n = runCatching { input.read(b) }.getOrDefault(-1)
                if (n < 0) break
                if (buf.size() < 256 * 1024) buf.write(b, 0, n)
            }
        }.apply { start() }
        if (!proc.waitFor(timeoutMs, TimeUnit.MILLISECONDS)) {
            proc.destroyForcibly(); pump.join(500)
            return 124 to "Operation timed out."
        }
        pump.join(1_000)
        return proc.exitValue() to buf.toString("UTF-8").trim()
    }

    private fun b64(s: String) = Base64.encodeToString(s.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
}

/** The complete, fixed list of privileged operations FLY8 can request. */
internal object Ops {
    private val PKG = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
    private val COMPONENT = Regex("^[A-Za-z][A-Za-z0-9_.]*/[A-Za-z0-9_.$]+$")
    private val ROBLOX_URI = Regex("^roblox://placeId=\\d{1,19}$")
    private val USER = Regex("^\\d{1,4}$")
    private val PROFILE_TYPES = mapOf(
        "CLONE" to "android.os.usertype.profile.CLONE",
        "PRIVATE" to "android.os.usertype.profile.PRIVATE"
    )

    private fun pkg(s: String) = s.also { require(PKG.matches(it) && it.length <= 200) { "invalid package" } }
    private fun user(s: String) = s.also { require(USER.matches(it)) { "invalid user" } }
    private fun secondaryUser(s: String) = user(s).also { require(it != "0") { "main user not allowed" } }
    fun display(s: String): Int = s.toInt().also { require(it in 1..99_999) { "only virtual displays" } }

    fun timeoutFor(op: String): Long = when (op) {
        "CREATE_PROFILE", "START_USER", "INSTALL" -> 60_000
        else -> 15_000
    }

    fun build(op: String, a: List<String>): List<String> = when (op) {
        "USERS" -> listOf("pm", "list", "users")
        "CREATE_PROFILE" -> listOf(
            "pm", "create-user", "--profileOf", "0", "--user-type",
            PROFILE_TYPES[a[0]] ?: error("invalid profile type"), "FLY8 ${a[0].lowercase()}"
        )
        "START_USER" -> listOf("am", "start-user", "-w", secondaryUser(a[0]))
        "HAS_PKG" -> listOf("pm", "list", "packages", "--user", user(a[0]), pkg(a[1]))
        "RESOLVE" -> listOf(
            "cmd", "package", "resolve-activity", "--brief", "--user", user(a[0]),
            "-a", "android.intent.action.MAIN", "-c", "android.intent.category.LAUNCHER", pkg(a[1])
        )
        "INSTALL" -> listOf("pm", "install-existing", "--user", secondaryUser(a[0]), pkg(a[1]))
        "UNINSTALL" -> listOf("pm", "uninstall", "--user", secondaryUser(a[0]), pkg(a[1]))
        // Removes a whole secondary user/profile. Never user 0. Used only for rollback and remove.
        "REMOVE_USER" -> listOf("pm", "remove-user", secondaryUser(a[0]))
        "LAUNCH" -> {
            require(COMPONENT.matches(a[2])) { "invalid component" }
            listOf("am", "start", "--user", user(a[0]), "--display", display(a[1]).toString(),
                "-f", "0x10000000", "-n", a[2])
        }
        "VIEW" -> {
            require(ROBLOX_URI.matches(a[3])) { "invalid rejoin target" }
            listOf("am", "start", "--user", user(a[0]), "--display", display(a[1]).toString(),
                "-a", "android.intent.action.VIEW", "-d", a[3], "-p", pkg(a[2]), "-f", "0x10000000")
        }
        "STOP" -> listOf("am", "force-stop", "--user", user(a[0]), pkg(a[1]))
        "CLEAR" -> listOf("pm", "clear", "--user", user(a[0]), pkg(a[1]))
        "PS" -> listOf("ps", "-A", "-o", "USER,PID,NAME")
        "KEEP_ALIVE" -> listOf("cmd", "deviceidle", "whitelist", "+" + pkg(a[0]))
        "ALLOW_BACKGROUND" -> listOf("cmd", "appops", "set", pkg(a[0]), "RUN_ANY_IN_BACKGROUND", "allow")
        else -> error("unknown operation")
    }
}

/** Display-aware input injection through the hidden InputManager API (shell has INJECT_EVENTS). */
private object Input {
    private val manager: Any by lazy {
        runCatching {
            Class.forName("android.hardware.input.InputManagerGlobal").getMethod("getInstance").invoke(null)
        }.getOrNull() ?: Class.forName("android.hardware.input.InputManager").getMethod("getInstance").invoke(null)!!
    }
    private val inject by lazy {
        manager.javaClass.getMethod("injectInputEvent", InputEvent::class.java, Int::class.javaPrimitiveType)
    }
    private val setDisplayId by lazy {
        InputEvent::class.java.getMethod("setDisplayId", Int::class.javaPrimitiveType)
    }

    private fun send(event: InputEvent, displayId: Int) {
        setDisplayId.invoke(event, displayId)
        inject.invoke(manager, event, 0) // INJECT_INPUT_EVENT_MODE_ASYNC
    }

    fun touch(p: List<String>) {
        val display = Ops.display(p[1])
        val action = p[2].toInt().coerceIn(0, 6)
        val index = p[3].toInt().coerceIn(0, 9)
        val downTime = p[4].toLong()
        val count = p[5].toInt().coerceIn(1, 10)
        val props = Array(count) { MotionEvent.PointerProperties() }
        val coords = Array(count) { MotionEvent.PointerCoords() }
        for (i in 0 until count) {
            val (id, x, y) = p[6 + i].split(':')
            props[i].id = id.toInt()
            props[i].toolType = MotionEvent.TOOL_TYPE_FINGER
            coords[i].x = x.toFloat()
            coords[i].y = y.toFloat()
            coords[i].pressure = 1f
            coords[i].size = 1f
        }
        val fullAction = action or (index shl MotionEvent.ACTION_POINTER_INDEX_SHIFT)
        val event = MotionEvent.obtain(
            downTime, SystemClock.uptimeMillis(), fullAction, count, props, coords,
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0
        )
        send(event, display)
        event.recycle()
    }

    fun key(display: Int, code: Int) {
        val now = SystemClock.uptimeMillis()
        send(KeyEvent(now, now, KeyEvent.ACTION_DOWN, code, 0), display)
        send(KeyEvent(now, now, KeyEvent.ACTION_UP, code, 0), display)
    }

    fun text(display: Int, text: String) {
        val map = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD)
        for (ch in text) {
            map.getEvents(charArrayOf(ch))?.forEach { send(it, display) }
        }
    }
}
