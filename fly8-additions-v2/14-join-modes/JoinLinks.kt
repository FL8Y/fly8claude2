package com.fly8.clone.data

import java.net.URI
import java.net.URLDecoder

/**
 * Roblox join links for the two joining modes added in EysClone 1.4.15.
 *
 * EysClone 1.4.15 (REA/JADX class za, methods w / z):
 *  - Public place:  digits | https://(www.)roblox.com/games/<id>/… | roblox://placeId=<id>
 *                   → roblox://placeId=<id>
 *  - Private invite: https://(www.)roblox.com/share?code=<c>&type=Server
 *                   → roblox://navigation/share_links?code=<c>&type=Server
 *                   https://(www.)roblox.com/games/<id>/…?privateServerLinkCode=<c>
 *                   → roblox://placeId=<id>&privateServerLinkCode=<c>
 *    Code must match [A-Za-z0-9_-]{1,256}; duplicate query keys are rejected; a share link
 *    must be type=Server. Public links and place IDs are refused in Private mode.
 *  - Low-player server: roblox://placeId=<id>&gameInstanceId=<uuid>  (ActiveDisplayService)
 *
 * FLY8 today (RejoinTarget.normalize) keeps only the place id, so a private invitation is
 * silently turned into a normal public join. These helpers keep the full link.
 * The same three patterns must be allowed by FLY8's ControlServer (see ControlServer.patch).
 */
object JoinLinks {
    private val CODE = Regex("^[A-Za-z0-9_-]{1,256}$")
    private val UUID = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
    private val PLACE = Regex("^[1-9][0-9]{0,18}$")

    /** Exact forms FLY8 may open. Mirror these in ControlServer.ROBLOX_URI. */
    val ALLOWED = Regex(
        "^roblox://placeId=[1-9][0-9]{0,18}" +
            "(?:&gameInstanceId=[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}" +
            "|&privateServerLinkCode=[A-Za-z0-9_-]{1,256})?$" +
            "|^roblox://navigation/share_links\\?code=[A-Za-z0-9_-]{1,256}&type=Server$"
    )

    /** Place id from a public link or number, or null. Does not accept private invitations. */
    fun placeId(raw: String): String? {
        val s = raw.trim()
        if (s.isEmpty() || s.length > 2048 || s.any(Char::isWhitespace)) return null
        if (PLACE.matches(s)) return s
        Regex("^roblox://placeId=([1-9][0-9]{0,18})(?:&.*)?$").find(s)?.let { return it.groupValues[1] }
        val uri = runCatching { URI(s) }.getOrNull() ?: return null
        if (!isRobloxHttps(uri)) return null
        val path = uri.path ?: return null
        if (!path.startsWith("/games/")) return null
        return path.removePrefix("/games/").substringBefore('/').takeIf { PLACE.matches(it) }
    }

    /** Public destination for a place: roblox://placeId=<id>. */
    fun publicLink(placeId: String): String? = placeId.takeIf { PLACE.matches(it) }?.let { "roblox://placeId=$it" }

    /** A specific public server (low-player result). */
    fun serverLink(placeId: String, serverId: String): String? =
        if (PLACE.matches(placeId) && UUID.matches(serverId)) "roblox://placeId=$placeId&gameInstanceId=$serverId" else null

    /**
     * Private-server invitation → deep link, or null when [raw] is not a private invitation.
     * Accepts the two web forms above and the two deep-link forms.
     */
    fun privateLink(raw: String): String? {
        val s = raw.trim()
        if (s.isEmpty() || s.length > 2048 || s.any(Char::isWhitespace)) return null
        if (s.startsWith("roblox://")) return s.takeIf { ALLOWED.matches(it) && ("privateServerLinkCode=" in it || "share_links" in it) }
        val uri = runCatching { URI(s) }.getOrNull() ?: return null
        if (!isRobloxHttps(uri)) return null
        val q = query(uri.rawQuery) ?: return null
        return when {
            uri.path == "/share" -> {
                val code = q["code"]
                if (code != null && CODE.matches(code) && q["type"].equals("Server", ignoreCase = true))
                    "roblox://navigation/share_links?code=$code&type=Server" else null
            }
            uri.path?.startsWith("/games/") == true -> {
                val place = placeId(s)
                val code = q["privateServerLinkCode"]
                if (place != null && code != null && CODE.matches(code)) "roblox://placeId=$place&privateServerLinkCode=$code" else null
            }
            else -> null
        }
    }

    private fun isRobloxHttps(u: URI) = u.scheme.equals("https", true) && u.userInfo == null && u.port == -1 &&
        (u.host.equals("roblox.com", true) || u.host.equals("www.roblox.com", true))

    /** Decoded query map; null when a key repeats (EysClone rejects duplicates). */
    private fun query(raw: String?): Map<String, String>? {
        if (raw.isNullOrEmpty()) return emptyMap()
        val out = HashMap<String, String>()
        for (part in raw.split('&')) {
            val kv = part.split('=', limit = 2)
            if (kv.size != 2) continue
            val k = URLDecoder.decode(kv[0], "UTF-8")
            if (out.put(k, URLDecoder.decode(kv[1], "UTF-8")) != null) return null
        }
        return out
    }
}
