package com.fly8.clone.session

import android.content.Context
import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.Instance
import com.fly8.clone.data.JoinLinks
import com.fly8.clone.data.JoinMode
import com.fly8.clone.data.JoinSettingsStore
import com.fly8.clone.data.LogCategory
import com.fly8.clone.data.RejoinTarget
import com.fly8.clone.data.SessionState
import kotlinx.coroutines.delay

/**
 * Decides where each instance joins, following its saved joining mode (EysClone 1.4.15:
 * "Launch All, Auto Recovery, and scheduled rejoin follow each instance's saved mode").
 *
 *  PUBLIC_LOW : search a public server listing 0–1 players; if none, the normal public place.
 *  PRIVATE    : always the saved invitation. Never falls back to a public server; a failed
 *               private join waits and retries the same invitation (EysClone pq.u →
 *               "Private-server join pending · waiting to retry").
 *  Scheduled force rejoin in PRIVATE mode may use the instance's "Public game override"
 *  when one is set ("Force rejoin fallback · normal public place"); otherwise private.
 *
 * Wiring (FLY8 source, Kotlin):
 *  1. SessionManager.rejoin — replace
 *         val target = RejoinTarget.effective(instance.rejoinTarget, settings.recovery.value.sharedTarget)
 *     with
 *         val target = JoinPlanner.destination(instance, forced = false)?.link
 *  2. Recovery.forceRejoinIfDue / recoverOne / restartExited and NetworkWatch.rejoin — replace
 *     the eligibility check RejoinTarget.forClone(...) != null with JoinPlanner.hasDestination(instance).
 *  3. Fly8.onCreate: JoinPlanner.init(this).  InstanceStore.remove(id): JoinPlanner.forget(id).
 *  4. ControlServer.ROBLOX_URI — widen to JoinLinks.ALLOWED (see ControlServer.patch), or
 *     Android-profile clones reject the new links with "invalid rejoin target".
 */
object JoinPlanner {
    data class Destination(val link: String, val label: String)

    private lateinit var store: JoinSettingsStore
    fun init(context: Context) { store = JoinSettingsStore(context.applicationContext) }
    fun settings() = store
    fun forget(instanceId: String) { store.forget(instanceId); LowPlayerServers.release(instanceId) }

    /** FLY8's existing public target for this instance (own place id, else shared). */
    private fun publicPlace(i: Instance): String? =
        RejoinTarget.forClone(i.packageName, i.rejoinTarget, Fly8.settings.recovery.value.sharedTarget)
            ?.let { JoinLinks.placeId(it) }

    private fun privateInvite(i: Instance): String? =
        store.get(i.id).privateInvite.ifEmpty { store.defaultInvite }.takeIf { it.isNotEmpty() }

    /** Whether recovery / rejoin has somewhere to send this instance. */
    fun hasDestination(i: Instance): Boolean = when (store.get(i.id).mode) {
        JoinMode.PRIVATE -> privateInvite(i) != null
        JoinMode.PUBLIC_LOW -> publicPlace(i) != null
    }

    /** One-line preview for cards and settings ("Destination · …"). No network. */
    fun preview(i: Instance): String {
        val s = store.get(i.id)
        return when (s.mode) {
            JoinMode.PRIVATE -> if (privateInvite(i) != null) "Destination · Private server · saved invitation" else "Needs private-server invitation"
            JoinMode.PUBLIC_LOW -> publicPlace(i)?.let { "Destination · Public low-player · place $it" } ?: "Needs public game link"
        }
    }

    /**
     * Final link for this join. [forced] = scheduled force rejoin.
     * Returns null when the instance has no usable destination.
     */
    suspend fun destination(i: Instance, forced: Boolean): Destination? {
        val s = store.get(i.id)
        if (s.mode == JoinMode.PRIVATE) {
            if (forced) JoinLinks.placeId(s.publicOverride)?.let { p ->
                return Destination(JoinLinks.publicLink(p)!!, "Force rejoin fallback · normal public place")
            }
            return privateInvite(i)?.let { Destination(it, "Private server") }
        }
        val place = publicPlace(i) ?: return null
        return when (val r = LowPlayerServers.find(place, i.id)) {
            is LowPlayerServers.Result.Found -> {
                log(i, "Low-player server selected · ${r.server.playing} player(s) · ${r.pagesSearched} page(s) searched", EventType.INFO)
                Destination(JoinLinks.serverLink(place, r.server.id)!!, "Public low-player · ${r.server.playing}/${r.server.maxPlayers}")
            }
            is LowPlayerServers.Result.Limited -> {
                log(i, "Roblox is limiting server searches · normal public place used", EventType.INFO)
                Destination(JoinLinks.publicLink(place)!!, "Public place")
            }
            else -> {
                log(i, "No fresh public server with 0–1 listed players available · normal public place used", EventType.INFO)
                Destination(JoinLinks.publicLink(place)!!, "Public place")
            }
        }
    }

    /**
     * Rejoin following the saved mode. PRIVATE retries the same invitation (10 s, 30 s, 60 s,
     * then every 120 s, up to [maxAttempts]) and never switches to public.
     */
    suspend fun rejoinFollowingMode(i: Instance, maxAttempts: Int = 6): Boolean {
        val private = store.get(i.id).mode == JoinMode.PRIVATE
        val waits = longArrayOf(10_000, 30_000, 60_000)
        repeat(if (private) maxAttempts else 1) { n ->
            if (n > 0) {
                log(i, "Private-server join waiting to retry", EventType.REJOIN)
                delay(waits.getOrElse(n - 1) { 120_000 })
                if (!Fly8.sessions.isWantedRunning(i.id) || Fly8.sessions.isManuallyStopped(i.id)) return false
            }
            if (Fly8.sessions.rejoin(i, true).isSuccess) {
                log(i, if (private) "Private-server join requested" else "Join requested", EventType.REJOIN)
                return true
            }
        }
        log(i, if (private) "Private-server join failed · check the invitation link" else "Join could not be sent", EventType.ERROR)
        return false
    }

    /**
     * "Launch All" following each instance's mode: start stopped instances 10 s apart
     * (EysClone: "Start all stopped instances, 10 seconds apart"), wait until running,
     * then join its destination.
     */
    suspend fun launchAllFollowingModes(gapMs: Long = 10_000) {
        val sessions = Fly8.sessions
        val stopped = Fly8.store.items.value.filter { sessions.sessionOf(it.id) == null }
        stopped.forEachIndexed { n, i ->
            if (n > 0) delay(gapMs)
            if (sessions.start(i, true).isFailure) return@forEachIndexed
            if (awaitRunning(i) && hasDestination(i)) rejoinFollowingMode(i)
        }
    }

    /** SessionManager.waitRunning is private in FLY8; poll the public state instead (max 60 s). */
    private suspend fun awaitRunning(i: Instance, timeoutMs: Long = 60_000): Boolean {
        val end = android.os.SystemClock.elapsedRealtime() + timeoutMs
        while (android.os.SystemClock.elapsedRealtime() < end) {
            val st = Fly8.sessions.stateOf(i.id)
            if (st == SessionState.RUNNING || st == SessionState.NO_PREVIEW) return true
            if (Fly8.sessions.isManuallyStopped(i.id)) return false
            delay(1_000)
        }
        return false
    }

    private fun log(i: Instance, msg: String, type: EventType) = Fly8.log.add(LogCategory.RECOVERY, msg, i.id, type)
}
