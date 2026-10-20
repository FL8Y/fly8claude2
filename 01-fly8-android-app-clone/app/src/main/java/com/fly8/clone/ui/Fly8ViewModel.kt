package com.fly8.clone.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fly8.clone.Fly8
import com.fly8.clone.control.Controls
import com.fly8.clone.control.PairingService
import com.fly8.clone.control.ServerLauncher
import com.fly8.clone.data.AppSettings
import com.fly8.clone.data.Instance
import com.fly8.clone.data.LogCategory
import com.fly8.clone.data.RecoverySettings
import com.fly8.clone.data.RejoinTarget
import com.fly8.clone.data.SpaceKind
import com.fly8.clone.recovery.Recovery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InstalledApp(val packageName: String, val label: String)

private fun chipsetName(): String =
    if (Build.VERSION.SDK_INT >= 31) Build.SOC_MODEL?.takeIf { it.isNotBlank() } ?: Build.HARDWARE
    else Build.HARDWARE

data class DeviceStats(
    val runningApps: Int = 0,
    val chipset: String = chipsetName(),
    val storageUsedGb: Float = 0f,
    val storageTotalGb: Float = 0f,
    val ramAvailGb: Float = 0f,
    val ramTotalGb: Float = 0f,
    val updatesPerSec: Float = 0f
)

class Fly8ViewModel(app: Application) : AndroidViewModel(app) {
    val controlState = Controls.state
    val instances = Fly8.store.items
    val sessions = Fly8.sessions.sessions
    val logs = Fly8.log.events
    val recovery = Fly8.settings.recovery
    val appSettings = Fly8.settings.app
    val capabilities = Fly8.sessions.backend.capabilities()

    val stats = MutableStateFlow(DeviceStats())
    val installedApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    val snackbar = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch { Controls.refresh() }
        refreshStats()
        viewModelScope.launch {
            while (true) {
                if (Controls.state.value.connected) {
                    Fly8.sessions.refreshAll(Fly8.store.items.value)
                }
                delay(5_000)
            }
        }
    }

    // ---- Controls ----
    fun connectWireless() = launchResult { Controls.connectWireless() }
    fun connectRoot() = launchResult { Controls.connectRoot() }
    fun connectShizuku() = launchResult { Controls.connectShizuku() }
    fun connectManual(port: Int) = launchResult { Controls.connectWireless(port) }
    fun disconnectControls() = viewModelScope.launch { Controls.disconnect() }
    fun startPairing() = PairingService.start(getApplication())
    fun awaitComputerActivation() = launchResult { Controls.awaitComputerActivation() }

    fun openWirelessSettings() = openSettings(
        if (Build.VERSION.SDK_INT >= 30) "android.settings.APPLICATION_DEVELOPMENT_SETTINGS"
        else Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS
    )

    fun openNotificationSettings() {
        val i = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, getApplication<Application>().packageName)
            .putExtra(Settings.EXTRA_CHANNEL_ID, Fly8.CHANNEL_PAIRING)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { getApplication<Application>().startActivity(i) }
    }

    fun activationCommand(): String {
        ServerLauncher.writeActivationScript(getApplication())
        return ServerLauncher.activationCommand(getApplication())
    }

    private fun openSettings(action: String) {
        runCatching {
            getApplication<Application>().startActivity(
                Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }.onFailure { snackbar.value = "Could not open Developer options on this device." }
    }

    // ---- Apps & instances ----
    fun loadInstalledApps() = viewModelScope.launch(Dispatchers.IO) {
        val pm = getApplication<Application>().packageManager
        val main = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(main, 0)
            .mapNotNull {
                val pkg = it.activityInfo?.packageName ?: return@mapNotNull null
                if (pkg == getApplication<Application>().packageName) return@mapNotNull null
                InstalledApp(pkg, it.loadLabel(pm).toString())
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
        installedApps.value = apps
    }

    fun addClone(appItem: InstalledApp) = viewModelScope.launch(Dispatchers.IO) {
        if (!Controls.state.value.connected) { snackbar.value = "Connect controls first."; return@launch }
        snackbar.value = "Creating a clone of ${appItem.label}…"
        val created = Fly8.sessions.backend.create(appItem.packageName, appItem.label)
        created.onSuccess { userId ->
            val space = Fly8.sessions.backend.spaces().firstOrNull { it.userId == userId }?.kind ?: SpaceKind.OTHER_PROFILE
            Fly8.store.add(appItem.packageName, appItem.label, userId, space)
            Fly8.log.add(LogCategory.SESSION, "Added clone: ${appItem.label}")
            snackbar.value = "${appItem.label} added."
        }.onFailure {
            Fly8.log.add(LogCategory.SESSION, "Add failed: ${appItem.label} • ${it.message}")
            snackbar.value = it.message
        }
    }

    fun start(instance: Instance, live: Boolean) = launchResult { Fly8.sessions.start(instance, live) }
    fun stop(instance: Instance) = launchResult { Fly8.sessions.stop(instance) }
    fun restart(instance: Instance) = launchResult { Fly8.sessions.restart(instance) }
    fun rejoin(instance: Instance) = launchResult { Fly8.sessions.rejoin(instance) }

    fun startAll(live: Boolean) = viewModelScope.launch {
        Fly8.store.items.value.forEach { Fly8.sessions.start(it, live) }
    }
    fun stopAll() = viewModelScope.launch { Fly8.store.items.value.forEach { Fly8.sessions.stop(it) } }

    fun clearData(instance: Instance) = launchResult { Fly8.sessions.backend.clearData(instance) }
    fun remove(instance: Instance) = viewModelScope.launch(Dispatchers.IO) {
        Fly8.sessions.backend.remove(instance)
        Fly8.store.remove(instance.id)
        snackbar.value = "${instance.label} removed."
    }

    fun setRejoinTarget(instance: Instance, raw: String) {
        val normalized = if (raw.isBlank()) "" else RejoinTarget.normalize(raw)
        if (raw.isNotBlank() && normalized == null) { snackbar.value = "That is not a valid Roblox link or place ID."; return }
        Fly8.store.update(instance.id) { it.copy(rejoinTarget = raw) }
    }

    fun setRecoveryEnabled(instance: Instance, enabled: Boolean) {
        Fly8.store.update(instance.id) { it.copy(recoveryEnabled = enabled) }
        Recovery.reschedule(getApplication())
    }

    // ---- Settings ----
    fun saveRecovery(s: RecoverySettings) { Fly8.settings.saveRecovery(s); Recovery.reschedule(getApplication()) }
    fun saveAppSettings(s: AppSettings) = Fly8.settings.saveApp(s)
    fun clearLogs() = Fly8.log.clear()

    /** Copy a support report to the clipboard. Device + state + recent stages, never secrets. */
    fun copyDiagnostics() {
        val ctx = getApplication<Application>()
        val cs = controlState.value
        val sb = StringBuilder()
        sb.appendLine("FLY8 ${com.fly8.clone.BuildConfig.VERSION_NAME}")
        sb.appendLine("Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        sb.appendLine("${Build.MANUFACTURER} ${Build.MODEL} · ${chipsetName()}")
        sb.appendLine("Controls: ${cs.method?.label ?: "none"} · ${if (cs.connected) "connected" else "disconnected"}")
        sb.appendLine("Backend: ${capabilities.name}")
        sb.appendLine("Instances: ${instances.value.size}")
        sb.appendLine("--- recent activity (redacted) ---")
        logs.value.take(40).forEach { sb.appendLine("${it.category.name.lowercase()}: ${it.message}") }
        ctx.getSystemService(ClipboardManager::class.java)
            .setPrimaryClip(ClipData.newPlainText("FLY8 diagnostics", sb.toString()))
        snackbar.value = "Diagnostics copied (no secrets included)."
    }

    // ---- Stats ----
    fun refreshStats() = viewModelScope.launch(Dispatchers.IO) {
        val ctx = getApplication<Application>()
        val am = ctx.getSystemService(android.app.ActivityManager::class.java)
        val mem = android.app.ActivityManager.MemoryInfo().also { am.getMemoryInfo(it) }
        val dataDir = ctx.dataDir
        val total = dataDir.totalSpace.toFloat() / 1e9f
        val free = dataDir.freeSpace.toFloat() / 1e9f
        stats.value = DeviceStats(
            runningApps = sessions.value.values.count { it.state.name == "RUNNING" },
            storageUsedGb = total - free,
            storageTotalGb = total,
            ramAvailGb = mem.availMem.toFloat() / 1e9f,
            ramTotalGb = mem.totalMem.toFloat() / 1e9f
        )
    }

    private fun launchResult(block: suspend () -> Result<Unit>) = viewModelScope.launch {
        val r = withContext(Dispatchers.Default) { block() }
        r.onFailure { snackbar.value = it.message }
        refreshStats()
    }

    fun dismissSnackbar() { snackbar.value = null }
}
