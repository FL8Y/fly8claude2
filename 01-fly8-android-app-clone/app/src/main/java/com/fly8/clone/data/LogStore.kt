package com.fly8.clone.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

enum class LogCategory { RECOVERY, SESSION, CONTROLS, SYSTEM }

data class LogEvent(val time: Long, val category: LogCategory, val message: String, val instance: String? = null)

/** Bounded (latest 100) local activity log. Messages are redacted before they are stored. */
class LogStore(context: Context) {
    private val file = File(context.filesDir, "activity.json")
    private val _events = MutableStateFlow(load())
    val events: StateFlow<List<LogEvent>> = _events

    private fun load(): List<LogEvent> = runCatching {
        val arr = JSONArray(file.readText())
        (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            LogEvent(
                o.getLong("t"),
                runCatching { LogCategory.valueOf(o.getString("c")) }.getOrDefault(LogCategory.SYSTEM),
                o.getString("m"), o.optString("i").ifBlank { null }
            )
        }
    }.getOrDefault(emptyList())

    @Synchronized
    fun add(category: LogCategory, message: String, instance: String? = null) {
        val event = LogEvent(System.currentTimeMillis(), category, redact(message), instance)
        val list = (listOf(event) + _events.value).take(LIMIT)
        _events.value = list
        runCatching {
            file.writeText(JSONArray().apply {
                list.forEach {
                    put(JSONObject().put("t", it.time).put("c", it.category.name).put("m", it.message)
                        .put("i", it.instance ?: ""))
                }
            }.toString())
        }
    }

    @Synchronized
    fun clear(category: LogCategory? = null) {
        _events.value = if (category == null) emptyList() else _events.value.filterNot { it.category == category }
        runCatching { file.writeText(JSONArray().toString()) }
    }

    companion object {
        const val LIMIT = 100
        private val secret = Regex("(?i)(pairing code|code|token|password|webhook)\\s*[:=]\\s*\\S+")
        private val sixDigits = Regex("\\b\\d{6}\\b")
        fun redact(raw: String): String = raw
            .replace(secret) { it.groupValues[1] + "=[redacted]" }
            .replace(sixDigits, "[redacted]")
            .take(400)
    }
}
