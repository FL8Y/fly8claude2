package com.fly8.clone.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * "Public low-player server" search, as in EysClone 1.4.15 (REA/JADX classes qp, rp, cm):
 *  - GET https://games.roblox.com/v1/games/<placeId>/servers/Public
 *        ?sortOrder=Asc&excludeFullGames=true&limit=100[&cursor=…]
 *  - ONE queue for every instance's search, at least 1.5 s between requests (rp.b = 1.5 s);
 *    HTTP 429 → wait Retry-After (seconds or HTTP date, default 30 s) before any next request.
 *  - Response ≤ 512 KB, ≤ 100 entries; keep servers with 0 ≤ playing < maxPlayers ≤ 100000.
 *  - Walk pages until a server listing 0–1 players is found, for at most 30 s.
 *  - Skip a server another instance is assigned to, or one picked in the last 2 minutes,
 *    so two clones never land in the same "empty" server.
 *  - Nothing found → caller falls back to the normal public place link.
 */
object LowPlayerServers {
    data class Server(val id: String, val playing: Int, val maxPlayers: Int)
    sealed interface Result {
        data class Found(val placeId: String, val server: Server, val pagesSearched: Int) : Result
        data class NotFound(val pagesSearched: Int) : Result
        data class Limited(val retryAfterSec: Int) : Result
        data class Failed(val message: String) : Result
    }

    private const val GAP_MS = 1_500L
    private const val BUDGET_MS = 30_000L
    private const val COOLDOWN_MS = 120_000L
    private const val MAX_BYTES = 512 * 1024
    private val UUID = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

    private val gate = Mutex()
    @Volatile private var nextAllowedAt = 0L      // elapsed-time ms of the next permitted request
    private val recent = LinkedHashMap<String, Long>()          // "place:server" → picked at
    private val assigned = LinkedHashMap<String, String>()      // instanceId → "place:server"

    suspend fun find(placeId: String, instanceId: String): Result = withContext(Dispatchers.IO) {
        val deadline = now() + BUDGET_MS
        val seenCursors = HashSet<String>()
        var cursor = ""
        var pages = 0
        while (now() < deadline && seenCursors.add(cursor)) {
            val page = try {
                gate.withLock {
                    val wait = nextAllowedAt - now()
                    if (wait > 0) {
                        if (now() + wait > deadline) return@withContext Result.NotFound(pages)
                        delay(wait)
                    }
                    nextAllowedAt = now() + GAP_MS
                    fetch(placeId, cursor)
                }
            } catch (e: RateLimited) {
                nextAllowedAt = maxOf(nextAllowedAt, now() + e.seconds * 1000L)
                return@withContext Result.Limited(e.seconds)
            } catch (e: Exception) {
                return@withContext Result.Failed(e.message ?: "Roblox could not provide the public server list. Try again later.")
            }
            pages++
            page.servers.firstOrNull { it.playing <= 1 && claim(placeId, it.id, instanceId) }
                ?.let { return@withContext Result.Found(placeId, it, pages) }
            cursor = page.next ?: break
        }
        Result.NotFound(pages)
    }

    /** Call when the instance leaves that server or stops, so others may use it. */
    @Synchronized fun release(instanceId: String) { assigned.remove(instanceId) }

    @Synchronized private fun claim(place: String, server: String, instanceId: String): Boolean {
        val key = "$place:$server"
        val t = now()
        recent.entries.removeIf { t - it.value >= COOLDOWN_MS }
        if (assigned.any { (id, v) -> id != instanceId && v == key }) return false
        if (assigned[instanceId] != key && recent.containsKey(key)) return false
        recent.remove(key); recent[key] = t
        assigned[instanceId] = key
        while (recent.size > 512) recent.remove(recent.keys.first())
        return true
    }

    private class Page(val servers: List<Server>, val next: String?)
    private class RateLimited(val seconds: Int) : Exception()

    private fun fetch(placeId: String, cursor: String): Page {
        require(placeId.matches(Regex("^[1-9][0-9]{0,18}$")))
        require(cursor.length <= 1024) { "Invalid cursor." }
        val q = "sortOrder=Asc&excludeFullGames=true&limit=100" +
            if (cursor.isEmpty()) "" else "&cursor=" + URLEncoder.encode(cursor, "UTF-8")
        val c = URL("https://games.roblox.com/v1/games/$placeId/servers/Public?$q").openConnection() as HttpURLConnection
        c.connectTimeout = 8_000; c.readTimeout = 12_000; c.useCaches = false
        c.setRequestProperty("Cache-Control", "no-cache, no-store")
        try {
            when (val code = c.responseCode) {
                429 -> throw RateLimited(retryAfter(c.getHeaderField("Retry-After")))
                404 -> throw IllegalStateException("This place is unavailable. Check its place ID.")
                !in 200..299 -> throw IllegalStateException("Roblox could not provide the public server list (HTTP $code).")
            }
            val buf = ByteArray(MAX_BYTES + 1)
            var n = 0
            c.inputStream.use { s -> while (n < buf.size) { val r = s.read(buf, n, buf.size - n); if (r < 0) break; n += r } }
            check(n <= MAX_BYTES) { "The server-list response could not be read safely." }
            val j = JSONObject(String(buf, 0, n))
            val arr = j.getJSONArray("data")
            check(arr.length() <= 100) { "Unexpected server page size" }
            val list = (0 until arr.length()).mapNotNull { i ->
                val o = arr.optJSONObject(i) ?: return@mapNotNull null
                val id = o.optString("id"); val playing = o.optInt("playing", -1); val max = o.optInt("maxPlayers", 0)
                if (UUID.matches(id) && max in 1..100_000 && playing in 0 until max) Server(id, playing, max) else null
            }.distinctBy { it.id }.sortedBy { it.playing }
            val next = j.opt("nextPageCursor").let { if (it is String && it.isNotEmpty() && it.length <= 1024) it else null }
            return Page(list, next)
        } finally { c.disconnect() }
    }

    private fun retryAfter(h: String?): Int {
        val v = h?.trim().orEmpty()
        v.toLongOrNull()?.let { return it.coerceIn(1, 3600).toInt() }
        return runCatching {
            val ms = java.time.ZonedDateTime.parse(v, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli() - System.currentTimeMillis()
            (ms / 1000).coerceIn(1, 3600).toInt()
        }.getOrDefault(30)
    }

    private fun now() = android.os.SystemClock.elapsedRealtime()
}
