package com.fly8.clone.data

/**
 * Strict Roblox rejoin-target parsing. Returns a canonical `roblox://placeId=<id>` deep link, or
 * null. Pure Kotlin (no android.net.Uri) so it is unit-testable and host-exact.
 */
object RejoinTarget {
    private val numeric = Regex("^\\d{1,19}$")
    private val https = Regex("^https://(?:www\\.)?roblox\\.com/games/(\\d{1,19})(?:[/?#].*)?$", RegexOption.IGNORE_CASE)
    private val scheme = Regex(
        "^roblox://(?:experiences/start\\?)?placeId=(\\d{1,19})(?:&.*)?$",
        RegexOption.IGNORE_CASE
    )

    fun normalize(raw: String): String? {
        val value = raw.trim()
        if (value.isEmpty() || value.length > 300 || value.any { it.isWhitespace() }) return null

        if (numeric.matches(value)) return placeId(value)
        https.find(value)?.let { return placeId(it.groupValues[1]) }
        scheme.find(value)?.let { return placeId(it.groupValues[1]) }
        return null
    }

    private fun placeId(digits: String): String? {
        val id = digits.toLongOrNull() ?: return null
        return if (id > 0) "roblox://placeId=$id" else null
    }
}
