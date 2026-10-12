package com.fly8.clone.alerts

import android.content.Context
import com.fly8.clone.Fly8
import com.fly8.clone.data.EventType
import com.fly8.clone.data.SecureBox
import com.fly8.clone.data.LogEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Discord webhook alerts. FLY8 already stores AppSettings.discordWebhook ("a_discord")
 * but nothing reads it back (REA trace-android-references: the only caller of
 * getDiscordWebhook is SettingsStore.saveApp). This class is the missing sender.
 *
 * Features: per-event-type filter, device nickname, test message, Discord rate-limit
 * handling (HTTP 429 + retry_after), bounded queue, delivery status for the UI.
 *
 * Storage: like EysClone (class yb), the webhook is kept encrypted with an Android
 * Keystore AES-GCM key (SecureBox), not in plain SharedPreferences. On first start the
 * old plain "a_discord" value is moved into the encrypted box and blanked.
 *
 * Wiring: in Fly8.onCreate: DiscordAlerts.init(this)
 *         in LogStore.add(...) after appending: DiscordAlerts.offer(event)
 */
object DiscordAlerts {
    data class Config(
        val url: String = "",
        val enabled: Boolean = false,
        val nickname: String = "",
        val types: Set<EventType> = setOf(EventType.CRASH, EventType.RECOVERY, EventType.REJOIN, EventType.RESTART),
    )

    sealed interface Status {
        data object Off : Status
        data object Ready : Status
        data class Sending(val pending: Int) : Status
        data class Delivered(val at: Long) : Status
        data class RateLimited(val retryMs: Long) : Status
        data class Failed(val reason: String) : Status
    }

    private const val MAX_QUEUE = 50
    private val WEBHOOK = Regex("^https://(?:ptb\\.|canary\\.)?discord(?:app)?\\.com/api/webhooks/\\d+/[A-Za-z0-9_-]+$")

    private lateinit var box: SecureBox
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val queue = Channel<String>(MAX_QUEUE)
    private val _status = MutableStateFlow<Status>(Status.Off)
    val status: StateFlow<Status> = _status
    @Volatile var config = Config(); private set

    fun init(context: Context) {
        box = SecureBox(context, "fly8-discord-alerts-v1", "discord-alerts.bin")
        config = load()
        migrateLegacy()
        _status.value = if (config.enabled && config.url.isNotBlank()) Status.Ready else Status.Off
        scope.launch { pump() }
    }

    fun isValidUrl(url: String) = WEBHOOK.matches(url.trim())

    private fun load(): Config {
        val j = box.read()?.let { runCatching { JSONObject(it) }.getOrNull() } ?: return Config()
        val arr = j.optJSONArray("types")
        val types = if (arr == null) Config().types else
            (0 until arr.length()).mapNotNull { i -> EventType.entries.firstOrNull { it.name == arr.optString(i) } }.toSet()
        return Config(j.optString("url"), j.optBoolean("enabled"), j.optString("nick"), types)
    }

    private fun persist(c: Config): Boolean = box.write(
        JSONObject().put("url", c.url).put("enabled", c.enabled).put("nick", c.nickname)
            .put("types", org.json.JSONArray(c.types.map { it.name })).toString()
    )

    /** FLY8 1.0.0-beta01 saved the URL in plain prefs ("a_discord"). Move it once, then blank it. */
    private fun migrateLegacy() {
        val app = Fly8.settings.app.value
        val legacy = app.discordWebhook.trim()
        if (legacy.isEmpty()) return
        if (config.url.isBlank() && isValidUrl(legacy)) {
            config = config.copy(url = legacy)
            if (!persist(config)) return
        }
        Fly8.settings.saveApp(app.copy(discordWebhook = ""))
    }

    /** Save settings. An empty url keeps the stored one (the UI never shows the saved URL). */
    fun save(newUrl: String?, enabled: Boolean, nickname: String, types: Set<EventType>): Boolean {
        val url = newUrl?.trim().takeUnless { it.isNullOrEmpty() } ?: config.url
        if (enabled && !isValidUrl(url)) return false
        val next = Config(url, enabled, nickname.take(40), types)
        if (!persist(next)) return false
        config = next
        if (!enabled) while (queue.tryReceive().isSuccess) { /* drop pending alerts */ }
        _status.value = if (enabled) Status.Ready else Status.Off
        return true
    }

    fun removeWebhook() {
        box.delete()
        config = config.copy(url = "", enabled = false)
        while (queue.tryReceive().isSuccess) { }
        _status.value = Status.Off
    }

    /** Called for every log event. Cheap when alerts are off. */
    fun offer(e: LogEvent) {
        val c = config
        if (!c.enabled || c.url.isBlank() || e.type !in c.types) return
        val who = if (c.nickname.isNotBlank()) "**${c.nickname}** · " else ""
        val inst = e.instance?.let { " · instance $it" } ?: ""
        queue.trySend("$who${e.type.name.lowercase()}$inst\n${redact(e.message).take(1800)}")
    }

    /** Sends even when automatic alerts are off. Returns the result for the UI. */
    suspend fun sendTest(url: String?, nickname: String): Result<Unit> = withContext(Dispatchers.IO) {
        val target = url?.trim().takeUnless { it.isNullOrEmpty() } ?: config.url
        if (!isValidUrl(target)) return@withContext Result.failure(IllegalArgumentException("Webhook URL is not a Discord webhook"))
        post(target, "FLY8 test alert${if (nickname.isNotBlank()) " from **$nickname**" else ""} ✅")
    }

    private suspend fun pump() {
        for (msg in queue) {
            var attempt = 0
            while (true) {
                val c = config
                if (!c.enabled || c.url.isBlank()) break
                _status.value = Status.Sending(1)
                val r = post(c.url, msg)
                if (r.isSuccess) { _status.value = Status.Delivered(System.currentTimeMillis()); break }
                val ex = r.exceptionOrNull()
                if (ex is RateLimit) { _status.value = Status.RateLimited(ex.retryMs); delay(ex.retryMs); continue }
                if (++attempt >= 3) { _status.value = Status.Failed(ex?.message ?: "error"); break }
                delay(2_000L * attempt)
            }
            delay(1_100) // stay well under Discord's 30 msgs/min per webhook
        }
    }

    private class RateLimit(val retryMs: Long) : Exception("rate limited")

    private fun post(url: String, content: String): Result<Unit> = runCatching {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 10_000
            readTimeout = 10_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
        }
        try {
            val body = JSONObject().put("content", content).put("username", "FLY8")
                .put("allowed_mentions", JSONObject().put("parse", org.json.JSONArray()))
            conn.outputStream.use { it.write(body.toString().toByteArray()) }
            when (val code = conn.responseCode) {
                in 200..299 -> Unit
                429 -> {
                    val txt = conn.errorStream?.bufferedReader()?.readText().orEmpty()
                    val secs = runCatching { JSONObject(txt).optDouble("retry_after", 5.0) }.getOrDefault(5.0)
                    throw RateLimit((secs * 1000).toLong().coerceIn(1_000, 120_000))
                }
                401, 403, 404 -> throw IllegalStateException("Webhook unavailable (HTTP $code); check or replace it")
                else -> throw IllegalStateException("HTTP $code")
            }
        } finally {
            conn.disconnect()
        }
    }

    private val SECRET = Regex("(?i)(pairing code|code|token|password|key)\\s*[:=]\\s*\\S+")
    private fun redact(s: String) = SECRET.replace(s) { it.groupValues[1] + ": [hidden]" }
}
