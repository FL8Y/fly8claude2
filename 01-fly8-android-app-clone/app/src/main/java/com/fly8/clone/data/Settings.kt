package com.fly8.clone.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class AppSettings(
    val previewFps: Int = 2,
    val gridColumns: Int = 0, // 0 = adaptive
    val keepAwake: Boolean = true,
    val defaultDisplay: DisplaySpec = DisplaySpec()
)

class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("fly8_settings", Context.MODE_PRIVATE)
    private val _recovery = MutableStateFlow(loadRecovery())
    val recovery: StateFlow<RecoverySettings> = _recovery
    private val _app = MutableStateFlow(loadApp())
    val app: StateFlow<AppSettings> = _app

    private fun loadRecovery() = RecoverySettings(
        restartExited = prefs.getBoolean("r_exit", false),
        rejoinOnNetworkChange = prefs.getBoolean("r_net", false),
        rejoinOnPublicIpChange = prefs.getBoolean("r_ip", false),
        forceRejoinMinutes = prefs.getInt("r_force", 0),
        recoverUnchanged = prefs.getBoolean("r_unchanged", false),
        unchangedWaitMinutes = prefs.getInt("r_wait", 5),
        restartIntervalSeconds = prefs.getInt("r_interval", 20).coerceIn(10, 120),
        sharedTarget = prefs.getString("r_target", "") ?: ""
    )

    fun saveRecovery(s: RecoverySettings) {
        prefs.edit().putBoolean("r_exit", s.restartExited).putBoolean("r_net", s.rejoinOnNetworkChange)
            .putBoolean("r_ip", s.rejoinOnPublicIpChange).putInt("r_force", s.forceRejoinMinutes)
            .putBoolean("r_unchanged", s.recoverUnchanged).putInt("r_wait", s.unchangedWaitMinutes)
            .putInt("r_interval", s.restartIntervalSeconds.coerceIn(10, 120))
            .putString("r_target", s.sharedTarget).apply()
        _recovery.value = s
    }

    private fun loadApp() = AppSettings(
        previewFps = prefs.getInt("a_fps", 2).coerceIn(1, 10),
        gridColumns = prefs.getInt("a_cols", 0).coerceIn(0, 4),
        keepAwake = prefs.getBoolean("a_awake", true),
        defaultDisplay = DisplaySpec(
            prefs.getInt("a_dw", 1280), prefs.getInt("a_dh", 720), prefs.getInt("a_ddpi", 240)
        )
    )

    fun saveApp(s: AppSettings) {
        prefs.edit().putInt("a_fps", s.previewFps).putInt("a_cols", s.gridColumns)
            .putBoolean("a_awake", s.keepAwake).putInt("a_dw", s.defaultDisplay.width)
            .putInt("a_dh", s.defaultDisplay.height).putInt("a_ddpi", s.defaultDisplay.dpi).apply()
        _app.value = s
    }
}
