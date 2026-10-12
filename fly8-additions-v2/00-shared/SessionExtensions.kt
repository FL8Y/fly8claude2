package com.fly8.clone.session

import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance
import com.fly8.clone.data.SessionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Small helpers used by the new code. They only call SessionManager / LiveSession
 * members that are public in FLY8 1.0.0-beta01 (checked with REA inspect-android-class):
 * sessionOf, start(instance, withPreview), stop, stateOf, isWantedRunning,
 * isManuallyStopped, RunningSession.live, backend.launch.
 */
private val helperScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

/** The Live session of an instance, or null when it is not running in Live view. */
fun SessionManager.liveOf(id: String): LiveSession? = sessionOf(id)?.live

/**
 * Start an instance from outside the UI (home-screen shortcut, share sheet).
 * withPreview = true matches the Sessions screen's Start button (SessionsScreen calls start(instance, true)).
 */
fun SessionManager.startFromShortcut(instance: Instance, withPreview: Boolean = true) {
    helperScope.launch { start(instance, withPreview) }
}

/** Stop the instance's session so it can be reopened on the real screen. */
suspend fun SessionManager.stopForForeground(instance: Instance) {
    if (sessionOf(instance.id) != null) stop(instance)
}

/**
 * Instances that are "in a game": same rule as NetworkWatch.remember() in FLY8
 * (wanted running, not manually stopped, state RUNNING or NO_PREVIEW).
 * NetworkWatch's own list is private, so this repeats the rule with public calls.
 */
fun SessionManager.inGameInstances(): List<Instance> = Fly8.store.items.value.filter { i ->
    isWantedRunning(i.id) && !isManuallyStopped(i.id) &&
        stateOf(i.id).let { it == SessionState.RUNNING || it == SessionState.NO_PREVIEW }
}

/** Lowercase enum name with a capital first letter, e.g. CRASH -> "Crash". */
fun Enum<*>.label(): String = name.lowercase().replaceFirstChar(Char::titlecase)
