package com.fly8.clone.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.UUID

/**
 * "Clone folders": user-named groups of instances (e.g. "Farm A", "Alt accounts").
 * Stored separately from instances.json so FLY8's InstanceStore schema stays at 1.
 * The Sessions screen can show one section per folder and run "Start all" per folder.
 */
data class CloneFolder(val id: String, val name: String, val instanceIds: List<String>)

class FolderStore(context: Context) {
    private val file = File(context.filesDir, "folders.json")
    private val _folders = MutableStateFlow(load())
    val folders: StateFlow<List<CloneFolder>> = _folders

    private fun load(): List<CloneFolder> = runCatching {
        val arr = JSONArray(file.readText())
        (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            val ids = o.getJSONArray("ids")
            CloneFolder(o.getString("id"), o.getString("name"), (0 until ids.length()).map { ids.getString(it) })
        }
    }.getOrDefault(emptyList())

    @Synchronized private fun save(list: List<CloneFolder>) {
        val arr = JSONArray()
        list.forEach { f -> arr.put(JSONObject().put("id", f.id).put("name", f.name).put("ids", JSONArray(f.instanceIds))) }
        val tmp = File(file.parentFile, ".folders.tmp")
        tmp.writeText(arr.toString())
        if (!tmp.renameTo(file)) { file.writeText(arr.toString()); tmp.delete() }
        _folders.value = list
    }

    fun create(name: String) = save(_folders.value + CloneFolder(UUID.randomUUID().toString(), name.trim().take(40), emptyList()))
    fun rename(id: String, name: String) = save(_folders.value.map { if (it.id == id) it.copy(name = name.trim().take(40)) else it })
    fun delete(id: String) = save(_folders.value.filterNot { it.id == id })

    /** Moves an instance into a folder (an instance belongs to at most one folder). null = no folder. */
    fun move(instanceId: String, folderId: String?) = save(_folders.value.map { f ->
        val without = f.instanceIds - instanceId
        if (f.id == folderId) f.copy(instanceIds = without + instanceId) else f.copy(instanceIds = without)
    })

    /** Call from InstanceStore.remove(id) so folders never point at deleted clones. */
    fun forget(instanceId: String) = save(_folders.value.map { it.copy(instanceIds = it.instanceIds - instanceId) })

    fun folderOf(instanceId: String) = _folders.value.firstOrNull { instanceId in it.instanceIds }
}
