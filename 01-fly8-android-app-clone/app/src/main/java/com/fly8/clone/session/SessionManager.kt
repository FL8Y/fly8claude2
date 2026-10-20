package com.fly8.clone.session

import android.content.Context
import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance
import com.fly8.clone.data.LogCategory
import com.fly8.clone.data.RejoinTarget
import com.fly8.clone.data.SessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class RunningSession(
    val instanceId: String,
    val state: SessionState,
    val live: LiveSession?,
    val lastChange: Long = System.currentTimeMillis()
)

/**
 * Tracks which instances are running and owns their Live virtual displays. Operations are
 * serialized per instance so two taps cannot race. State is set from observed results, never
 * optimistically: STARTING → (verified) RUNNING, or ERROR with a real reason.
 */
class SessionManager(private val context: Context, val backend: CloneBackend) {
    private val _sessions = MutableStateFlow<Map<String, RunningSession>>(emptyMap())
    val sessions: StateFlow<Map<String, RunningSession>> = _sessions
    private val gates = HashMap<String, Mutex>()

    private val TRANSITIONAL = setOf(SessionState.STARTING, SessionState.STOPPING, SessionState.RECOVERING)

    private fun gate(id: String): Mutex = synchronized(gates) { gates.getOrPut(id) { Mutex() } }
    private fun set(id: String, state: SessionState, live: LiveSession? = sessionOf(id)?.live) {
        _sessions.value = _sessions.value + (id to RunningSession(id, state, live))
    }
    fun sessionOf(id: String) = _sessions.value[id]
    fun stateOf(id: String) = _sessions.value[id]?.state ?: SessionState.UNKNOWN

    suspend fun start(instance: Instance, withPreview: Boolean): Result<Unit> = gate(instance.id).withLock {
        withContext(Dispatchers.IO) {
            set(instance.id, SessionState.STARTING)
            val live = if (withPreview && backend.capabilities().supportsLivePreview) {
                LiveSession(context, instance.id, instance.display).also { if (!it.open()) { } }
            } else null
            val displayId = live?.displayId?.takeIf { it > 0 } ?: 0
            val launched = backend.launch(instance, displayId)
            if (launched.isFailure) {
                live?.close()
                set(instance.id, SessionState.ERROR, null)
                Fly8.log.add(LogCategory.SESSION, "Start failed: ${instance.label} • ${launched.exceptionOrNull()?.message}", instance.id)
                return@withContext launched
            }
            // Verify the process actually came up before claiming RUNNING.
            val running = waitRunning(instance)
            val finalState = when {
                running && live != null && displayId > 0 -> SessionState.RUNNING
                running -> SessionState.RUNNING
                else -> SessionState.NO_PREVIEW
            }
            set(instance.id, finalState, live)
            Fly8.log.add(LogCategory.SESSION, "Started ${instance.label} (${if (displayId > 0) "Live" else "foreground"})", instance.id)
            Fly8.daemon(context)
            Result.success(Unit)
        }
    }

    suspend fun stop(instance: Instance): Result<Unit> = gate(instance.id).withLock {
        withContext(Dispatchers.IO) {
            set(instance.id, SessionState.STOPPING)
            val r = backend.stop(instance)
            sessionOf(instance.id)?.live?.close()
            set(instance.id, if (r.isSuccess) SessionState.STOPPED else SessionState.ERROR, null)
            Fly8.log.add(LogCategory.SESSION, "Stopped ${instance.label}", instance.id)
            r
        }
    }

    suspend fun restart(instance: Instance): Result<Unit> = gate(instance.id).withLock {
        withContext(Dispatchers.IO) {
            set(instance.id, SessionState.RECOVERING)
            backend.stop(instance)
            sessionOf(instance.id)?.live?.close()
        }
        start(instance, withPreview = sessionOf(instance.id)?.live != null)
    }

    /** Attach/detach a UI surface for an instance's Live preview or full view. */
    fun attachSurface(id: String, surface: android.view.Surface?) {
        sessionOf(id)?.live?.attach(surface)
    }

    fun forwardTouch(id: String, action: Int, w: Int, h: Int, x: Float, y: Float) =
        sessionOf(id)?.live?.forwardTouch(action, w, h, x, y)

    fun sendKey(id: String, keyCode: Int) = sessionOf(id)?.live?.sendKey(keyCode)
    fun sendText(id: String, text: String) = sessionOf(id)?.live?.sendText(text)

    suspend fun rejoin(instance: Instance): Result<Unit> = withContext(Dispatchers.IO) {
        val target = RejoinTarget.normalize(instance.rejoinTarget)
            ?: return@withContext Result.failure(IllegalArgumentException("No valid Roblox rejoin target is set."))
        val displayId = sessionOf(instance.id)?.live?.displayId?.takeIf { it > 0 } ?: 0
        backend.rejoin(instance, target, displayId)
    }

    /** Refresh each instance's state from the device. Never reports STOPPED on hearsay. */
    suspend fun refreshAll(instances: List<Instance>) = withContext(Dispatchers.IO) {
        // If controls are down we cannot know the real state — UNKNOWN, never STOPPED (guide §10).
        if (!com.fly8.clone.control.Controls.state.value.connected) {
            instances.forEach { inst ->
                if (stateOf(inst.id) in TRANSITIONAL) return@forEach
                set(inst.id, SessionState.UNKNOWN, sessionOf(inst.id)?.live)
            }
            return@withContext
        }
        instances.forEach { inst ->
            if (stateOf(inst.id) in TRANSITIONAL) return@forEach
            val running = backend.isRunning(inst)
            val keepLive = sessionOf(inst.id)?.live
            // A black/no-preview tile over a running process must stay RUNNING, not flip to STOPPED.
            if (!running) keepLive?.close()
            set(inst.id, if (running) SessionState.RUNNING else SessionState.STOPPED, if (running) keepLive else null)
        }
    }

    private suspend fun waitRunning(instance: Instance): Boolean = withContext(Dispatchers.IO) {
        repeat(16) {
            if (backend.isRunning(instance)) return@withContext true
            kotlinx.coroutines.delay(500)
        }
        false
    }
}
