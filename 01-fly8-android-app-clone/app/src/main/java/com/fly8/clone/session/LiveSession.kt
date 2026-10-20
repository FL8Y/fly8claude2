package com.fly8.clone.session

import android.content.Context
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.os.SystemClock
import android.view.Surface
import com.fly8.clone.control.Controls
import com.fly8.clone.data.DisplaySpec

/**
 * A Live session renders one clone onto a private virtual display that exists independently of the
 * phone's physical screen, which is why a clone keeps running (and can be viewed) with the screen
 * off. A [Surface] from the UI (preview tile or full view) can be attached for output; FLY8's
 * touches are mapped to the virtual display's own coordinates and forwarded through the control
 * server. Nothing here renders a fake frame — if no surface is attached the display simply has no
 * consumer, exactly like the prototype's black Recent-Apps card over a still-running clone.
 */
class LiveSession(
    context: Context,
    val instanceId: String,
    val spec: DisplaySpec
) {
    private val displayManager = context.getSystemService(DisplayManager::class.java)
    private var virtualDisplay: VirtualDisplay? = null
    private var headlessReader: ImageReader? = null

    val displayId: Int get() = virtualDisplay?.display?.displayId ?: -1

    /** Create the virtual display. Starts headless (no visible consumer) until a surface attaches. */
    fun open(): Boolean {
        if (virtualDisplay != null) return true
        // A 1x1 ImageReader keeps the display valid while no UI surface is attached.
        val reader = ImageReader.newInstance(spec.width, spec.height, PixelFormat.RGBA_8888, 2)
        val flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or
            DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
        virtualDisplay = runCatching {
            displayManager.createVirtualDisplay(
                "FLY8-$instanceId", spec.width, spec.height, spec.dpi, reader.surface, flags
            )
        }.getOrNull()
        headlessReader = reader
        return virtualDisplay != null
    }

    /** Point the clone's output at a UI surface (preview tile or full view). */
    fun attach(surface: Surface?) {
        virtualDisplay?.surface = surface ?: headlessReader?.surface
    }

    fun resize(spec: DisplaySpec) {
        virtualDisplay?.resize(spec.width, spec.height, spec.dpi)
    }

    /** Forward a down/move/up touch in surface coordinates, scaled to the virtual display. */
    fun forwardTouch(action: Int, surfaceWidth: Int, surfaceHeight: Int, x: Float, y: Float) {
        val id = displayId
        if (id <= 0 || surfaceWidth == 0 || surfaceHeight == 0) return
        val vx = (x / surfaceWidth * spec.width).coerceIn(0f, spec.width.toFloat())
        val vy = (y / surfaceHeight * spec.height).coerceIn(0f, spec.height.toFloat())
        if (action == 0) downTime = SystemClock.uptimeMillis()
        Controls.sendInput("TOUCH $id $action 0 $downTime 1 0:${vx.toInt()}:${vy.toInt()}")
    }

    fun sendKey(keyCode: Int) {
        if (displayId > 0) Controls.sendInput("KEY $displayId $keyCode")
    }

    fun sendText(text: String) {
        if (displayId > 0 && text.isNotEmpty()) {
            val enc = android.util.Base64.encodeToString(text.toByteArray(), android.util.Base64.NO_WRAP)
            Controls.sendInput("TEXT $displayId $enc")
        }
    }

    fun close() {
        runCatching { virtualDisplay?.release() }
        runCatching { headlessReader?.close() }
        virtualDisplay = null
        headlessReader = null
    }

    private var downTime = 0L
}
