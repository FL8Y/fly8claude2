package com.fly8.clone.control

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.net.NetworkInterface
import kotlin.coroutines.resume

/** mDNS discovery of this device's own Wireless debugging endpoints. */
object Discovery {
    const val PAIRING = "_adb-tls-pairing._tcp."
    const val CONNECT = "_adb-tls-connect._tcp."

    // IPv6 too: some Android builds resolve the adb service to the device's IPv6 address only.
    private fun localAddresses(): Set<String> = runCatching {
        NetworkInterface.getNetworkInterfaces().toList().flatMap { it.inetAddresses.toList() }
            .mapNotNull { it.hostAddress?.let(::stripScope) }.toSet()
    }.getOrDefault(emptySet())

    private fun stripScope(address: String) = address.substringBefore('%')

    /** Returns the port of the first endpoint of [type] that belongs to this device, or null. */
    suspend fun findPort(context: Context, type: String, timeoutMs: Long): Int? {
        val nsd = context.getSystemService(NsdManager::class.java) ?: return null
        val mine = localAddresses()
        return withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                var done = false
                lateinit var listener: NsdManager.DiscoveryListener
                fun finish(port: Int?) {
                    synchronized(this@Discovery) {
                        if (done) return
                        done = true
                    }
                    runCatching { nsd.stopServiceDiscovery(listener) }
                    if (cont.isActive) cont.resume(port)
                }
                listener = object : NsdManager.DiscoveryListener {
                    override fun onStartDiscoveryFailed(t: String, e: Int) = finish(null)
                    override fun onStopDiscoveryFailed(t: String, e: Int) = Unit
                    override fun onDiscoveryStarted(t: String) = Unit
                    override fun onDiscoveryStopped(t: String) = Unit
                    override fun onServiceLost(s: NsdServiceInfo) = Unit

                    @Suppress("DEPRECATION")
                    override fun onServiceFound(s: NsdServiceInfo) {
                        nsd.resolveService(s, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(i: NsdServiceInfo, e: Int) = Unit
                            override fun onServiceResolved(i: NsdServiceInfo) {
                                val host = i.host?.hostAddress?.let(::stripScope) ?: ""
                                if (mine.isEmpty() || host in mine || host.startsWith("127.") || host == "::1") {
                                    finish(i.port)
                                }
                            }
                        })
                    }
                }
                cont.invokeOnCancellation {
                    synchronized(this@Discovery) { if (done) return@invokeOnCancellation; done = true }
                    runCatching { nsd.stopServiceDiscovery(listener) }
                }
                nsd.discoverServices(type, NsdManager.PROTOCOL_DNS_SD, listener)
            }
        }
    }
}
