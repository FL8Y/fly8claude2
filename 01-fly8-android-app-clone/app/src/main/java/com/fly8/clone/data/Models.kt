package com.fly8.clone.data

import org.json.JSONObject

/** Where an instance lives. Only spaces Android renders for the current user are offered. */
enum class SpaceKind { MAIN, CLONE_PROFILE, PRIVATE_PROFILE, OTHER_PROFILE }

/** Runtime state of a session. RUNNING is set only after the process is observed alive. */
enum class SessionState { UNKNOWN, STARTING, RUNNING, NO_PREVIEW, RECOVERING, STOPPING, STOPPED, ERROR }

data class DisplaySpec(val width: Int = 1280, val height: Int = 720, val dpi: Int = 240) {
    val landscape get() = width >= height
    fun rotated() = copy(width = height, height = width)
    fun toJson() = JSONObject().put("w", width).put("h", height).put("dpi", dpi)

    companion object {
        val PRESETS = listOf(
            "Low · 960×540" to DisplaySpec(960, 540, 200),
            "Balanced · 1280×720" to DisplaySpec(1280, 720, 240),
            "Sharp · 1600×900" to DisplaySpec(1600, 900, 280)
        )

        fun fromJson(o: JSONObject?): DisplaySpec = if (o == null) DisplaySpec() else DisplaySpec(
            o.optInt("w", 1280).coerceIn(320, 2560),
            o.optInt("h", 720).coerceIn(320, 2560),
            o.optInt("dpi", 240).coerceIn(120, 640)
        )
    }
}

data class Instance(
    val id: String,
    val number: Int,
    val packageName: String,
    val label: String,
    val userId: Int,
    val space: SpaceKind,
    val display: DisplaySpec = DisplaySpec(),
    val rejoinTarget: String = "",
    val recoveryEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isRoblox get() = packageName.startsWith("com.roblox.")
    val title get() = label

    fun toJson(): JSONObject = JSONObject()
        .put("id", id).put("number", number).put("packageName", packageName).put("label", label)
        .put("userId", userId).put("space", space.name).put("display", display.toJson())
        .put("rejoinTarget", rejoinTarget).put("recoveryEnabled", recoveryEnabled)
        .put("createdAt", createdAt)

    companion object {
        fun fromJson(o: JSONObject): Instance? {
            val pkg = o.optString("packageName")
            if (!Validators.isPackageName(pkg)) return null
            return Instance(
                id = o.optString("id").ifBlank { return null },
                number = o.optInt("number", 1),
                packageName = pkg,
                label = o.optString("label", pkg),
                userId = o.optInt("userId", 0).coerceAtLeast(0),
                space = runCatching { SpaceKind.valueOf(o.optString("space")) }.getOrDefault(SpaceKind.MAIN),
                display = DisplaySpec.fromJson(o.optJSONObject("display")),
                rejoinTarget = o.optString("rejoinTarget", ""),
                recoveryEnabled = o.optBoolean("recoveryEnabled", true),
                createdAt = o.optLong("createdAt", System.currentTimeMillis())
            )
        }
    }
}

data class RecoverySettings(
    val restartExited: Boolean = false,
    val rejoinOnNetworkChange: Boolean = false,
    val rejoinOnPublicIpChange: Boolean = false,
    val forceRejoinMinutes: Int = 0,
    val recoverUnchanged: Boolean = false,
    val unchangedWaitMinutes: Int = 5,
    val restartIntervalSeconds: Int = 20,
    val sharedTarget: String = ""
) {
    val anyEnabled get() = restartExited || rejoinOnNetworkChange || rejoinOnPublicIpChange ||
        forceRejoinMinutes > 0 || recoverUnchanged

    companion object {
        val FORCE_REJOIN_OPTIONS = listOf(0, 5, 10, 15, 30, 60)
        val UNCHANGED_WAIT_OPTIONS = listOf(2, 5, 10, 15, 30)
    }
}

object Validators {
    private val pkg = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z][A-Za-z0-9_]*)+$")
    fun isPackageName(s: String) = s.length in 3..200 && pkg.matches(s)
    fun isPort(p: Int) = p in 1..65535
    fun isPairingCode(s: String) = s.length == 6 && s.all { it.isDigit() }
    /** Single-quote a value for `sh -c`. */
    fun quote(s: String) = "'" + s.replace("'", "'\\''") + "'"
}
