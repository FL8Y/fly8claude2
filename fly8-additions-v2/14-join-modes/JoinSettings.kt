package com.fly8.clone.data

import android.content.Context

/**
 * Per-instance joining mode (EysClone 1.4.15: "Joining mode" in Edit game).
 *
 * EysClone stores, per instance key (REA/JADX classes sq, za; SharedPreferences):
 *   joinMode.<key>   "public" | "private"           (default public)
 *   target.<key>     the instance's own link
 *   forcePlace.<key> public place used by scheduled force rejoin ("Public game override")
 *   target           Default game (used by instances without their own link)
 * FLY8 keeps its own Instance.rejoinTarget (place id only) and RecoverySettings.sharedTarget.
 * This store adds only what FLY8 lacks, keyed by Instance.id, so FLY8's schema stays as is.
 */
enum class JoinMode { PUBLIC_LOW, PRIVATE }

data class JoinSettings(
    val mode: JoinMode = JoinMode.PUBLIC_LOW,
    /** Private-server invitation as a deep link (JoinLinks.privateLink), or "" to use Default game's. */
    val privateInvite: String = "",
    /** Optional public place used by scheduled force rejoin when the mode is PRIVATE. */
    val publicOverride: String = "",
)

class JoinSettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("fly8_join", Context.MODE_PRIVATE)

    fun get(instanceId: String) = JoinSettings(
        mode = if (prefs.getString("mode.$instanceId", "public") == "private") JoinMode.PRIVATE else JoinMode.PUBLIC_LOW,
        privateInvite = prefs.getString("private.$instanceId", "") ?: "",
        publicOverride = prefs.getString("override.$instanceId", "") ?: "",
    )

    /**
     * Save from the "Edit game" sheet. Returns an error message, or null on success.
     * Same checks and wording as EysClone 1.4.15.
     */
    fun save(instanceId: String, mode: JoinMode, rawInvite: String, rawOverride: String, hasDefaultInvite: Boolean): String? {
        val invite = if (rawInvite.isBlank()) "" else JoinLinks.privateLink(rawInvite)
            ?: return "Enter a private-server invitation link. Public game links and place IDs cannot be used in Private server mode."
        if (mode == JoinMode.PRIVATE && invite.isEmpty() && !hasDefaultInvite)
            return "Enter a private-server invitation here or in Default game."
        val override = if (rawOverride.isBlank()) "" else JoinLinks.placeId(rawOverride)
            ?: return "Use a public Roblox game link or numeric place ID."
        prefs.edit()
            .putString("mode.$instanceId", if (mode == JoinMode.PRIVATE) "private" else "public")
            .putString("private.$instanceId", invite)
            .putString("override.$instanceId", override)
            .apply()
        return null
    }

    /** Default game's private invitation (used by PRIVATE instances without their own). */
    var defaultInvite: String
        get() = prefs.getString("private.default", "") ?: ""
        set(v) { prefs.edit().putString("private.default", v).apply() }

    fun forget(instanceId: String) =
        prefs.edit().remove("mode.$instanceId").remove("private.$instanceId").remove("override.$instanceId").apply()
}
