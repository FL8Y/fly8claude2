package com.fly8.clone.recovery

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fly8.clone.MainActivity
import com.fly8.clone.data.EventType
import com.fly8.clone.data.LogEvent

/**
 * "Recovery notifications". AppSettings.recoveryNotifications ("a_rnotify") is saved
 * but never read (REA: only caller is SettingsStore.saveApp). This posts a grouped, low-noise notification for crash, recovery,
 * restart and rejoin events when the setting is on.
 *
 * Wiring: in LogStore.add(...) after appending:
 *     if (Fly8.settings.app.value.recoveryNotifications) RecoveryNotifier.post(context, event)
 */
object RecoveryNotifier {
    private const val CHANNEL = "recovery"
    private const val GROUP = "fly8.recovery"
    private val TYPES = setOf(EventType.CRASH, EventType.RECOVERY, EventType.RESTART, EventType.REJOIN)
    private var nextId = 5000

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = context.getSystemService(NotificationManager::class.java)
        if (nm.getNotificationChannel(CHANNEL) == null) {
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL, "Recovery events", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Crashes, restarts and rejoin attempts of your clones"
                }
            )
        }
    }

    fun post(context: Context, e: LogEvent) {
        if (e.type !in TYPES) return
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        ensureChannel(context)
        val open = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val title = when (e.type) {
            EventType.CRASH -> "Clone crashed"
            EventType.RESTART -> "Clone restarted"
            EventType.REJOIN -> "Rejoin attempted"
            else -> "Recovery"
        }
        val n = NotificationCompat.Builder(context, CHANNEL)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle(title)
            .setContentText(e.message.take(200))
            .setStyle(NotificationCompat.BigTextStyle().bigText(e.message.take(1000)))
            .setGroup(GROUP)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(open)
            .build()
        NotificationManagerCompat.from(context).notify(nextId++.also { if (nextId > 5100) nextId = 5000 }, n)
    }
}
