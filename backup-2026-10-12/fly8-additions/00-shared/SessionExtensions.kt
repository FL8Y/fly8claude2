package com.fly8.clone.session

import com.fly8.clone.data.Instance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Small helpers used by the new UI code. They only call SessionManager / LiveSession
 * methods that already exist and are public in FLY8 1.0.0-beta01
 * (sessionOf, start, stop, RunningSession.live, LiveSession.close).
 */
private val helperScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

/** The Live session of an instance, or null when it is not running in Live view. */
fun SessionManager.liveOf(id: String): LiveSession? = sessionOf(id)?.live

/** Start an instance from outside the UI (home-screen shortcut, share sheet). */
fun SessionManager.startFromShortcut(instance: Instance) {
    helperScope.launch { start(instance, false) }
}

/** Stop the instance's session so it can be reopened on the real screen. */
suspend fun SessionManager.stopForForeground(instance: Instance) {
    if (sessionOf(instance.id) != null) stop(instance)
}

/** Lowercase enum name with a capital first letter, e.g. CRASH -> "Crash". */
fun Enum<*>.label(): String = name.lowercase().replaceFirstChar(Char::titlecase)
