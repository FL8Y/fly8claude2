package com.fly8.clone.control

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteInput
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import com.fly8.clone.Fly8
import com.fly8.clone.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Pairing assistant. Finds Android's "Pair device with pairing code" endpoint over mDNS and posts
 * a notification with an inline reply field so the code can be typed without leaving Settings.
 */
class PairingService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var search: Job? = null
    private var pairingPort = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); return START_NOT_STICKY
            }
            ACTION_REPLY -> {
                val code = RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_CODE)?.toString()?.trim()
                val port = intent.getIntExtra(EXTRA_PORT, 0).takeIf { it in 1..65535 } ?: pairingPort
                handleCode(port, code.orEmpty())
                return START_NOT_STICKY
            }
        }
        startForeground(
            ID, build("Looking for the pairing code…", "In Wireless debugging, tap Pair device with pairing code.", false),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )
        startSearch()
        return START_NOT_STICKY
    }

    private fun startSearch() {
        search?.cancel()
        search = scope.launch {
            val port = Discovery.findPort(this@PairingService, Discovery.PAIRING, 5 * 60_000L)
            if (port == null) {
                notify(build("Pairing timed out", "Tap Start pairing in FLY8 to try again.", false, ongoing = false))
                stopForeground(STOP_FOREGROUND_DETACH); stopSelf()
                return@launch
            }
            pairingPort = port
            notify(build("Pairing code needed", "Enter the six-digit code shown under Pair with device.", true))
        }
    }

    private fun handleCode(port: Int, code: String) {
        if (port == 0) {
            // Never leave the reply spinner hanging: Android may have restarted this service.
            notify(build("Pairing code expired", "Tap Start pairing in FLY8, then open the pairing dialog again.", false, ongoing = false))
            return
        }
        notify(build("Pairing…", "Checking the code with Android.", false))
        scope.launch {
            val r = Controls.pair(port, code)
            if (r.isSuccess) {
                notify(build("Controls connected", "FLY8 is ready. You can return to the app.", false, ongoing = false))
                stopForeground(STOP_FOREGROUND_DETACH); stopSelf()
            } else {
                notify(build("Pairing failed", Controls.state.value.message, false))
                pairingPort = 0
                startSearch() // Android closes the dialog after a wrong code; wait for a new one.
            }
        }
    }

    private fun notify(n: Notification) = getSystemService(NotificationManager::class.java).notify(ID, n)

    private fun build(title: String, text: String, withInput: Boolean, ongoing: Boolean = true): Notification {
        val b = Notification.Builder(this, Fly8.CHANNEL_PAIRING)
            .setSmallIcon(R.drawable.ic_stat_fly8)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(Notification.BigTextStyle().bigText(text))
            .setOngoing(ongoing)
        if (withInput) {
            val reply = PendingIntent.getService(
                this, 1, Intent(this, PairingService::class.java).setAction(ACTION_REPLY).putExtra(EXTRA_PORT, pairingPort),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            val input = RemoteInput.Builder(KEY_CODE).setLabel("Pairing code").build()
            b.addAction(Notification.Action.Builder(null, "Enter code", reply).addRemoteInput(input).build())
        }
        if (ongoing) {
            val stop = PendingIntent.getService(
                this, 2, Intent(this, PairingService::class.java).setAction(ACTION_STOP), PendingIntent.FLAG_IMMUTABLE
            )
            b.addAction(Notification.Action.Builder(null, "Cancel", stop).build())
        }
        return b.build()
    }

    override fun onDestroy() { scope.cancel(); super.onDestroy() }

    companion object {
        private const val ID = 81
        private const val KEY_CODE = "code"
        private const val EXTRA_PORT = "port"
        private const val ACTION_REPLY = "com.fly8.clone.PAIR_REPLY"
        private const val ACTION_STOP = "com.fly8.clone.PAIR_STOP"
        fun start(context: Context) = context.startForegroundService(Intent(context, PairingService::class.java))
    }
}
