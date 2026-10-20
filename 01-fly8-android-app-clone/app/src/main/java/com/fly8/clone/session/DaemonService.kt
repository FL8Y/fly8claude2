package com.fly8.clone.session

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.PowerManager
import com.fly8.clone.Fly8
import com.fly8.clone.R

/**
 * Foreground keep-alive service. While clones are meant to be running it holds a foreground
 * notification (so Android does not reclaim FLY8) and a partial wake lock (so guest work continues
 * with the screen off). This reproduces the prototype's AFK behaviour through legitimate Android
 * mechanisms; it is only active while at least one session is wanted, and stops otherwise.
 */
class DaemonService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            releaseWake(); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf(); return START_NOT_STICKY
        }
        startForeground(ID, notification(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        if (Fly8.settings.app.value.keepAwake && wakeLock == null) {
            wakeLock = getSystemService(PowerManager::class.java)
                .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "fly8:sessions")
                .apply { setReferenceCounted(false); acquire() }
        }
        return START_STICKY
    }

    private fun notification(): Notification =
        Notification.Builder(this, Fly8.CHANNEL_SESSIONS)
            .setSmallIcon(R.drawable.ic_stat_fly8)
            .setContentTitle("FLY8 sessions running")
            .setContentText("Keeping your clones alive in the background.")
            .setOngoing(true)
            .build()

    private fun releaseWake() {
        runCatching { if (wakeLock?.isHeld == true) wakeLock?.release() }
        wakeLock = null
    }

    override fun onDestroy() { releaseWake(); super.onDestroy() }

    companion object {
        private const val ID = 71
        const val ACTION_STOP = "com.fly8.clone.DAEMON_STOP"
        fun start(context: Context) =
            context.startForegroundService(Intent(context, DaemonService::class.java))
        fun stop(context: Context) =
            context.startService(Intent(context, DaemonService::class.java).setAction(ACTION_STOP))
    }
}
