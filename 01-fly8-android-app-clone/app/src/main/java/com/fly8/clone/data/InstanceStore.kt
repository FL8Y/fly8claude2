package com.fly8.clone.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * Durable, local-first instance list. Stored as a versioned JSON envelope written atomically.
 * An unreadable file is quarantined (never overwritten with an empty list).
 */
class InstanceStore(context: Context) {
    private val file = File(context.filesDir, "instances.json")
    private val _items = MutableStateFlow(load())
    val items: StateFlow<List<Instance>> = _items

    var corruptionNotice: String? = null
        private set

    private fun load(): List<Instance> {
        if (!file.exists()) return emptyList()
        return try {
            val root = JSONObject(file.readText())
            val arr = root.optJSONArray("items") ?: JSONArray()
            (0 until arr.length()).mapNotNull { Instance.fromJson(arr.getJSONObject(it)) }
        } catch (t: Throwable) {
            val backup = File(file.parentFile, "instances.corrupt-${System.currentTimeMillis()}.json")
            runCatching { file.copyTo(backup, overwrite = false) }
            corruptionNotice = "Saved instances could not be read. A copy was kept as ${backup.name}."
            emptyList()
        }
    }

    @Synchronized
    private fun save(list: List<Instance>) {
        val root = JSONObject().put("schemaVersion", SCHEMA)
            .put("items", JSONArray().apply { list.forEach { put(it.toJson()) } })
        val tmp = File(file.parentFile, "instances.json.tmp")
        tmp.writeText(root.toString())
        if (!tmp.renameTo(file)) { file.writeText(root.toString()); tmp.delete() }
        _items.value = list
    }

    fun get(id: String) = _items.value.firstOrNull { it.id == id }

    fun add(packageName: String, label: String, userId: Int, space: SpaceKind): Instance {
        val list = _items.value
        val number = (list.maxOfOrNull { it.number } ?: 0) + 1
        val inst = Instance(
            id = UUID.randomUUID().toString(), number = number, packageName = packageName,
            label = label, userId = userId, space = space
        )
        save(list + inst)
        return inst
    }

    fun update(id: String, transform: (Instance) -> Instance) {
        save(_items.value.map { if (it.id == id) transform(it) else it })
    }

    fun remove(id: String) = save(_items.value.filterNot { it.id == id })

    companion object { const val SCHEMA = 1 }
}
