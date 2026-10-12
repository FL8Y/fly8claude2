package com.fly8.clone.session

import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.Instance
import com.fly8.clone.data.LogCategory

/**
 * "Open normally?" — the foreground fallback EysClone offers (class t1) when Android
 * rejects the Live (virtual) display. The clone opens on the real screen: no Live preview,
 * no simultaneous background sessions, no Live recovery.
 *
 * FLY8 already has the launch half: Fly8ViewModel.bringToFront() calls
 * sessions.backend.launch(instance, 0) (display 0 = the phone screen). It works for both
 * backends (RoutingBackend picks BlackBox for SpaceKind.VIRTUAL, Android profiles otherwise).
 * What is missing is EysClone's flow: stop the Live session first, explain the trade-off,
 * and offer it when the Live display fails. Earlier drafts called BlackBoxCore directly,
 * which would fail for Android-profile clones; this version goes through the backend.
 */
object OpenNormally {
    const val TITLE = "Open normally?"
    const val EXPLANATION = "Opens this clone in the foreground. Live previews, simultaneous background " +
        "sessions and Live recovery are unavailable in this mode."

    suspend fun open(instance: Instance): Boolean {
        val sessions = Fly8.sessions
        // Stop the Live session first so the app is not split across two displays.
        runCatching { sessions.stopForForeground(instance) }
        val ok = sessions.backend.launch(instance, 0).isSuccess
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
 * UI: in the session card menu add "Open normally" with an AlertDialog(TITLE, EXPLANATION),
 * and show the same dialog when LiveView reports "Could not create a Live display for …".
 */
