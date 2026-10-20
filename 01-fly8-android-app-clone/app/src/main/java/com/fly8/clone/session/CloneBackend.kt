package com.fly8.clone.session

import com.fly8.clone.data.Instance
import com.fly8.clone.data.SpaceKind

/** A place a clone can live, discovered from the real device (never invented). */
data class Space(val userId: Int, val kind: SpaceKind, val label: String)

/** What the active backend can actually do on this device. The UI reflects these honestly. */
data class BackendCapabilities(
    val name: String,
    val maxInstances: Int?,         // null = unknown/engine-defined
    val supportsLivePreview: Boolean,
    val supportsMultiplePerPackage: Boolean,
    val note: String
)

/**
 * Backend seam. The native-profile backend ships today; a future in-process virtualization
 * engine (VirtualApp lineage) can implement this same interface for 1:1 prototype parity without
 * touching the UI. Every call verifies its result rather than reporting optimistic success.
 */
interface CloneBackend {
    fun capabilities(): BackendCapabilities

    /** Spaces the device currently exposes (main user, clone profile, …). */
    fun spaces(): List<Space>

    /** Make a new isolated instance of [packageName]; returns the chosen userId. */
    fun create(packageName: String, label: String): Result<Int>

    /** Launch [instance] onto [displayId] (0 = default display, >0 = a FLY8 Live virtual display). */
    fun launch(instance: Instance, displayId: Int): Result<Unit>

    fun stop(instance: Instance): Result<Unit>
    fun clearData(instance: Instance): Result<Unit>
    fun remove(instance: Instance): Result<Unit>
    fun isRunning(instance: Instance): Boolean

    /** Launch a validated Roblox rejoin deep link inside the instance. */
    fun rejoin(instance: Instance, normalizedTarget: String, displayId: Int): Result<Unit>
}
