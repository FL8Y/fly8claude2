package com.fly8.clone.recovery

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.fly8.clone.Fly8
import com.fly8.clone.data.LogCategory
import com.fly8.clone.data.RejoinTarget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Scheduled recovery using Android alarms (not a busy loop). When it fires it checks real state:
 * a running or unknown clone is left alone; only a confirmed-exited clone is restarted, bounded by
 * the configured interval. Roblox rejoin waits a short delay after launch, like the prototype.
 */
object Recovery {
    private const val REQ = 8100

    fun reschedule(context: Context) {
        val settings = Fly8.settings.recovery.value
        if (!settings.anyEnabled) { cancel(context); return }
        val alarm = context.getSystemService(AlarmManager::class.java) ?: return
        val minutes = (settings.forceRejoinMinutes.takeIf { it > 0 } ?: 15).toLong()
        alarm.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + minutes * 60_000L,
            pending(context)
        )
    }

    fun cancel(context: Context) {
        val alarm = context.getSystemService(AlarmManager::class.java) ?: return
        PendingIntent.getBroadcast(
            context, REQ, Intent(context, RecoveryReceiver::class.java),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )?.let { alarm.cancel(it); it.cancel() }
    }

    private fun pending(context: Context) = PendingIntent.getBroadcast(
        context, REQ, Intent(context, RecoveryReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

class RecoveryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val app = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val settings = Fly8.settings.recovery.value
                if (!settings.anyEnabled) return@launch
                val instances = Fly8.store.items.value.filter { it.recoveryEnabled }
                Fly8.sessions.refreshAll(instances)
                for (inst in instances) {
                    if (Fly8.sessions.backend.isRunning(inst)) continue
                    if (!settings.restartExited && settings.forceRejoinMinutes == 0) continue
                    val r = Fly8.sessions.restart(inst)
                    if (r.isSuccess) {
                        Fly8.log.add(LogCategory.RECOVERY, "Recovery restarted ${inst.label}", inst.id)
                        if (RejoinTarget.normalize(inst.rejoinTarget) != null) {
                            delay((settings.restartIntervalSeconds.coerceIn(10, 120) * 1000L))
                            Fly8.sessions.rejoin(inst)
                        }
                    } else {
                        Fly8.log.add(LogCategory.RECOVERY, "Recovery could not restart ${inst.label}", inst.id)
                    }
                }
            } finally {
                Recovery.reschedule(app)
                pending.finish()
            }
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            Recovery.reschedule(context.applicationContext)
        }
    }
}
