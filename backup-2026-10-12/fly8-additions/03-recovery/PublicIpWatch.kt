package com.fly8.clone.session

import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.LogCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

/**
 * "Rejoin after public IP changes". FLY8 saves RecoverySettings.rejoinOnPublicIpChange
 * ("r_ip") but nothing reads it (REA trace_android_references: no caller), so the
 * switch does nothing today.
 *
 * Mobile data and some Wi-Fi routers change the public IP without Android reporting a
 * network change, which disconnects game servers. This watcher polls the public IP and,
 * when it changes, calls the same rejoin path NetworkWatch uses.
 *
 * Privacy: this is FLY8's only outbound request besides Discord/updates. It is OFF unless
 * the user enables the switch, and it sends no identifiers.
 */
object PublicIpWatch {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    @Volatile private var lastIp: String? = null

    /** Plain-text "what is my IP" endpoints; first success wins. */
    private val ENDPOINTS = listOf("https://api.ipify.org", "https://icanhazip.com", "https://ifconfig.me/ip")
    private val IPV4_OR_V6 = Regex("^[0-9a-fA-F:.]{3,45}$")

    /**
     * @param intervalMs how often to check (default 60 s, min 30 s)
     * @param onChange called with (old, new); call SessionManager rejoin for in-game instances
     */
    fun start(intervalMs: Long = 60_000, onChange: suspend (String, String) -> Unit) {
        stop()
        job = scope.launch {
            while (isActive) {
                val ip = fetch()
                val old = lastIp
                if (ip != null) {
                    lastIp = ip
                    if (old != null && old != ip) {
                        Fly8.log.add(LogCategory.RECOVERY, "Public IP changed; rejoining running games", null, EventType.NETWORK)
                        runCatching { onChange(old, ip) }
                    }
                }
                delay(intervalMs.coerceAtLeast(30_000))
            }
        }
    }

    fun stop() { job?.cancel(); job = null }

    private fun fetch(): String? {
        for (e in ENDPOINTS) {
            val ip = runCatching {
                val c = URL(e).openConnection() as HttpURLConnection
                c.connectTimeout = 5_000; c.readTimeout = 5_000
                try { c.inputStream.bufferedReader().readText().trim() } finally { c.disconnect() }
            }.getOrNull()
            if (ip != null && IPV4_OR_V6.matches(ip)) return ip
        }
        return null
    }
}

/*
 * Wiring (Fly8 / DaemonService):
 *   Fly8.settings.recovery.collect { s ->
 *       if (s.rejoinOnPublicIpChange) PublicIpWatch.start { _, _ ->
 *           val ids = Fly8.sessions.inGameInstanceIds()     // the same list NetworkWatch.rejoin uses
 *           NetworkWatch.rejoin(ids, 0L)
 *       } else PublicIpWatch.stop()
 *   }
 */
