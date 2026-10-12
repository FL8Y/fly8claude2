package com.fly8.clone.session

import android.content.Context
import android.net.ConnectivityManager
import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.LogCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
 * "Rejoin after public IP changes". FLY8 saves RecoverySettings.rejoinOnPublicIpChange
 * ("r_ip") and shows the switch, but nothing reads it (REA trace-android-references:
 * the only caller of getRejoinOnPublicIpChange is SettingsStore.saveRecovery).
 *
 * EysClone (classes vp / m1) asks https://api.ipify.org over the CURRENT network, reads at
 * most 128 bytes, accepts only [0-9a-fA-F:.]{3,64}, and checks at most every 15 s.
 * This version does the same, with a 30 s default interval and two fallback endpoints.
 *
 * Privacy: OFF unless the user turns the switch on. Sends no identifiers.
 */
object PublicIpWatch {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private var settingsJob: Job? = null
    @Volatile private var lastIp: String? = null

    private val ENDPOINTS = listOf("https://api.ipify.org", "https://icanhazip.com", "https://ifconfig.me/ip")
    private val IP = Regex("^[0-9a-fA-F:.]{3,64}$")

    /** NetworkWatch's rejoin spacing in FLY8 is 15 s ("rejoin it, 15 s apart"). */
    private const val GAP_MS = 15_000L

    /**
     * Call once from Fly8.onCreate (or DaemonService.onCreate). Follows the saved switch:
     * starts polling when it is turned on, stops when it is turned off.
     */
    fun attach(context: Context) {
        if (settingsJob != null) return
        val app = context.applicationContext
        settingsJob = scope.launch {
            Fly8.settings.recovery.distinctUntilChangedBy { it.rejoinOnPublicIpChange }.collect { s ->
                if (s.rejoinOnPublicIpChange) start(app) else stop()
            }
        }
    }

    fun start(context: Context, intervalMs: Long = 30_000) {
        stop()
        val cm = context.getSystemService(ConnectivityManager::class.java)
        job = scope.launch {
            while (isActive) {
                val ip = fetch(cm)
                val old = lastIp
                if (ip != null) {
                    lastIp = ip
                    if (old != null && old != ip) rejoinInGame()
                }
                delay(intervalMs.coerceAtLeast(GAP_MS))
            }
        }
    }

    fun stop() { job?.cancel(); job = null; lastIp = null }

    private suspend fun rejoinInGame() {
        val sessions = Fly8.sessions
        val targets = sessions.inGameInstances().filter { it.recoveryEnabled }
        if (targets.isEmpty()) return
        Fly8.log.add(LogCategory.RECOVERY, "Public IP changed; rejoining ${targets.size} game(s)", null, EventType.NETWORK)
        targets.forEachIndexed { n, inst ->
            if (n > 0) delay(GAP_MS)
            // onlyIfWanted = true: skip clones the user stopped in the meantime.
            val r = sessions.rejoin(inst, true)
            Fly8.log.add(
                LogCategory.RECOVERY,
                if (r.isSuccess) "Rejoin attempted after IP change" else "Could not rejoin after IP change",
                inst.id, if (r.isSuccess) EventType.REJOIN else EventType.ERROR,
            )
        }
    }

    /** One request on the active network; at most 128 bytes are read. */
    private fun fetch(cm: ConnectivityManager?): String? {
        val net = cm?.activeNetwork ?: return null
        for (e in ENDPOINTS) {
            val ip = runCatching {
                val c = net.openConnection(URL(e)) as HttpURLConnection
                c.connectTimeout = 5_000; c.readTimeout = 5_000; c.useCaches = false
                try {
                    if (c.responseCode !in 200..299) return@runCatching null
                    val buf = ByteArray(129)
                    var n = 0
                    c.inputStream.use { s -> while (n < buf.size) { val r = s.read(buf, n, buf.size - n); if (r < 0) break; n += r } }
                    if (n > 128) null else String(buf, 0, n).trim()
                } finally { c.disconnect() }
            }.getOrNull()
            if (ip != null && IP.matches(ip)) return ip
        }
        return null
    }
}
