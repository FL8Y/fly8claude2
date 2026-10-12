package com.fly8.clone.session

import android.content.Context
import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.Instance
import com.fly8.clone.data.LogCategory
import top.niunaijun.blackbox.BlackBoxCore

/**
 * "Open normally?" — the foreground fallback EysClone offers when a phone rejects the
 * Live (virtual) display. The clone opens on the real screen: no Live preview, no
 * simultaneous background sessions, no Live recovery.
 */
object OpenNormally {
    const val EXPLANATION = "Opens this clone in the foreground. Live previews, simultaneous background " +
        "sessions and Live recovery are unavailable in this mode."

    suspend fun open(context: Context, instance: Instance): Boolean {
        // Stop the Live session first so the app is not split across two displays.
        runCatching { Fly8.sessions.stopForForeground(instance) }
        val ok = runCatching { BlackBoxCore.get().launchApk(instance.packageName, instance.userId) }.getOrDefault(false)
        Fly8.log.add(
            LogCategory.SESSION,
            if (ok) "Opened ${instance.title} normally (foreground, no Live view)" else "Open normally failed for ${instance.title}",
            instance.id, if (ok) EventType.LAUNCH else EventType.ERROR,
        )
        return ok
    }
}

/*
 * stopForForeground() is in 00-shared/SessionExtensions.kt.
 * UI: in the session card menu add "Open normally" with a confirm dialog showing EXPLANATION.
 */
