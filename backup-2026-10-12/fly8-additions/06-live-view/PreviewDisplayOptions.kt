package com.fly8.clone.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale

/**
 * Display options for a Live preview / full view (EysClone: Fit to screen, Fill screen,
 * Rotate preview image, Reset preview rotation, Game orientation).
 *
 * FLY8 renders the clone picture inside LiveView/SessionCard. Apply [previewTransform]
 * to that composable and use [mapTouch] before calling LiveSession.forwardTouch so taps
 * still land on the right pixel after rotation / cropping.
 */
enum class PreviewScale(val label: String) { FIT("Fit to screen"), FILL("Fill screen") }

data class PreviewOptions(val scale: PreviewScale = PreviewScale.FIT, val rotation: Int = 0) {
    val contentScale get() = if (scale == PreviewScale.FILL) ContentScale.Crop else ContentScale.Fit
}

object PreviewOptionsStore {
    private fun prefs(c: Context) = c.getSharedPreferences("fly8_preview", Context.MODE_PRIVATE)
    fun load(c: Context, instanceId: String) = PreviewOptions(
        PreviewScale.entries[prefs(c).getInt("$instanceId.scale", 0).coerceIn(0, 1)],
        prefs(c).getInt("$instanceId.rot", 0),
    )
    fun save(c: Context, instanceId: String, o: PreviewOptions) =
        prefs(c).edit().putInt("$instanceId.scale", o.scale.ordinal).putInt("$instanceId.rot", o.rotation).apply()
}

/** Rotates the preview image by 0/90/180/270 degrees. */
fun Modifier.previewTransform(o: PreviewOptions): Modifier = graphicsLayer { rotationZ = o.rotation.toFloat() }

/**
 * Maps a touch on the rotated, scaled view back to display coordinates.
 * viewW/viewH: size of the composable. dispW/dispH: virtual display size.
 */
fun mapTouch(o: PreviewOptions, x: Float, y: Float, viewW: Float, viewH: Float, dispW: Float, dispH: Float): Offset? {
    // 1. undo rotation around the view centre
    val cx = viewW / 2f; val cy = viewH / 2f
    var px = x - cx; var py = y - cy
    when (((o.rotation % 360) + 360) % 360) {
        90 -> { val t = px; px = py; py = -t }
        180 -> { px = -px; py = -py }
        270 -> { val t = px; px = -py; py = t }
    }
    val rotated = o.rotation % 180 != 0
    val effW = if (rotated) viewH else viewW
    val effH = if (rotated) viewW else viewH
    // 2. undo Fit / Fill scaling (both keep aspect ratio, centred)
    val s = if (o.scale == PreviewScale.FILL) maxOf(effW / dispW, effH / dispH) else minOf(effW / dispW, effH / dispH)
    val dx = (px / s) + dispW / 2f
    val dy = (py / s) + dispH / 2f
    return if (dx in 0f..dispW && dy in 0f..dispH) Offset(dx, dy) else null  // tap on letterbox: ignore
}

@Composable
fun DisplayOptionsDialog(current: PreviewOptions, onChange: (PreviewOptions) -> Unit, onDismiss: () -> Unit) {
    var o by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Display options") },
        text = {
            Column {
                PreviewScale.entries.forEach { s ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(o.scale == s, { o = o.copy(scale = s) }); Text(s.label)
                    }
                }
                TextButton({ o = o.copy(rotation = (o.rotation + 90) % 360) }) { Text("Rotate preview image (${o.rotation}°)") }
                TextButton({ o = o.copy(rotation = 0) }) { Text("Reset preview rotation") }
            }
        },
        confirmButton = { TextButton({ onChange(o); onDismiss() }) { Text("Save") } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}
