package com.fly8.clone.session

import com.fly8.clone.control.Controls
import com.fly8.clone.data.Instance
import com.fly8.clone.data.SpaceKind

/**
 * Clones using Android's own profile/secondary-user primitives through the FLY8 control server.
 * This is the real, shipping backend: it creates genuine isolated instances up to the device's
 * profile limit. It does NOT claim the prototype's 50-slot in-process virtualization — the
 * capability note says so, and the UI surfaces that limit instead of pretending otherwise.
 */
class NativeCloneBackend : CloneBackend {

    override fun capabilities() = BackendCapabilities(
        name = "Native profiles",
        maxInstances = null, // device/OEM decides; discovered at runtime
        supportsLivePreview = true,
        supportsMultiplePerPackage = true,
        note = "Uses Android clone profiles and secondary users. The number of instances depends on " +
            "your device; some phones allow only one or two. Lightweight in-app virtualization (for " +
            "many copies of one app) is a separate engine tracked in docs/PROTOTYPE_NOTES.md."
    )

    override fun spaces(): List<Space> {
        val r = Controls.op("USERS")
        if (!r.ok) return listOf(Space(0, SpaceKind.MAIN, "Main"))
        return parseUsers(r.output)
    }

    override fun create(packageName: String, label: String): Result<Int> {
        // Reuse an existing FLY8 space that does not already hold this package.
        val existing = spaces().firstOrNull { it.kind != SpaceKind.MAIN &&
            Controls.op("HAS_PKG", it.userId.toString(), packageName).output.lineSequence()
                .none { line -> line.trim() == "package:$packageName" } }
        if (existing != null) {
            val installed = Controls.op("INSTALL", existing.userId.toString(), packageName)
            if (installed.ok) return Result.success(existing.userId)
        }

        // Create a fresh profile. Clone profile first (shares the package list), then secondary user.
        for (type in listOf("CLONE", "PRIVATE")) {
            val created = Controls.op("CREATE_PROFILE", type)
            val id = Regex("id (\\d+)|id=(\\d+)|user id (\\d+)")
                .find(created.output)?.groupValues?.firstOrNull { it.isNotEmpty() && it.toIntOrNull() != null }
                ?.toIntOrNull()
                ?: Regex("\\b(\\d{1,4})\\b").find(created.output.substringAfterLast("id"))?.value?.toIntOrNull()
            if (created.ok && id != null && id > 0) {
                Controls.op("START_USER", id.toString())
                val install = Controls.op("INSTALL", id.toString(), packageName)
                if (install.ok) return Result.success(id)
                // Transactional rollback: remove ONLY the profile this attempt created, so a
                // failed install never leaves an orphan user behind (guide §9).
                Controls.op("REMOVE_USER", id.toString())
                return Result.failure(IllegalStateException(
                    "Created a space but could not install $packageName into it, so the space was " +
                        "rolled back: ${install.output.take(160)}"
                ))
            }
        }
        return Result.failure(IllegalStateException(
            "This device would not create another isolated space. Many phones cap the number of " +
                "profiles/users. Remove an existing instance or use a device that allows more."
        ))
    }

    override fun launch(instance: Instance, displayId: Int): Result<Unit> {
        val comp = resolveLauncher(instance) ?: return Result.failure(
            IllegalStateException("${instance.label} has no launchable activity in its space.")
        )
        val r = Controls.op("LAUNCH", instance.userId.toString(), displayId.toString(), comp)
        return if (r.ok) Result.success(Unit) else Result.failure(IllegalStateException(r.output.ifBlank { "Launch failed." }))
    }

    override fun rejoin(instance: Instance, normalizedTarget: String, displayId: Int): Result<Unit> {
        val r = Controls.op("VIEW", instance.userId.toString(), displayId.toString(), instance.packageName, normalizedTarget)
        return if (r.ok) Result.success(Unit) else Result.failure(IllegalStateException(r.output.ifBlank { "Rejoin failed." }))
    }

    override fun stop(instance: Instance): Result<Unit> = unitOp("STOP", instance.userId.toString(), instance.packageName)
    override fun clearData(instance: Instance): Result<Unit> = unitOp("CLEAR", instance.userId.toString(), instance.packageName)

    override fun remove(instance: Instance): Result<Unit> =
        unitOp("UNINSTALL", instance.userId.toString(), instance.packageName)

    override fun isRunning(instance: Instance): Boolean {
        val r = Controls.op("PS")
        if (!r.ok) return false
        // Guest processes are named "<userId>:<package>" or run under user uXXa by Android.
        val needle = instance.packageName
        return r.output.lineSequence().any { it.contains(needle) }
    }

    private fun unitOp(name: String, vararg args: String): Result<Unit> {
        val r = Controls.op(name, *args)
        return if (r.ok) Result.success(Unit) else Result.failure(IllegalStateException(r.output.ifBlank { "$name failed." }))
    }

    private fun resolveLauncher(instance: Instance): String? {
        // Ask the device's package manager, as the instance's user, for the real launcher component.
        val r = Controls.op("RESOLVE", instance.userId.toString(), instance.packageName)
        if (!r.ok) return null
        val component = r.output.lineSequence()
            .map { it.trim() }
            .lastOrNull { it.contains('/') && it.startsWith(instance.packageName) }
        return component
    }

    companion object {
        private fun parseUsers(output: String): List<Space> {
            // Lines look like: "UserInfo{10:FLY8 clone:1410} running"
            return Regex("UserInfo\\{(\\d+):([^:}]*):([0-9a-fA-F]+)}")
                .findAll(output).mapNotNull { m ->
                    val id = m.groupValues[1].toIntOrNull() ?: return@mapNotNull null
                    val name = m.groupValues[2]
                    val flags = m.groupValues[3].toLongOrNull(16) ?: 0
                    val kind = when {
                        id == 0 -> SpaceKind.MAIN
                        flags and 0x00001000L != 0L -> SpaceKind.CLONE_PROFILE
                        name.contains("Private", true) -> SpaceKind.PRIVATE_PROFILE
                        else -> SpaceKind.OTHER_PROFILE
                    }
                    Space(id, kind, name.ifBlank { "User $id" })
                }.toList().ifEmpty { listOf(Space(0, SpaceKind.MAIN, "Main")) }
        }
    }
}
