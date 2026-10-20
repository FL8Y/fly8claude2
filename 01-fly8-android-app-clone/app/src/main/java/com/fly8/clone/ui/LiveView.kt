package com.fly8.clone.ui

import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance

/** Output-only preview tile. Binds its SurfaceView to the clone's Live virtual display. */
@Composable
fun LivePreview(instance: Instance, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            SurfaceView(ctx).apply {
                holder.addCallback(object : SurfaceHolder.Callback {
                    override fun surfaceCreated(h: SurfaceHolder) =
                        Fly8.sessions.attachSurface(instance.id, h.surface)
                    override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, ht: Int) =
                        Fly8.sessions.attachSurface(instance.id, h.surface)
                    override fun surfaceDestroyed(h: SurfaceHolder) =
                        Fly8.sessions.attachSurface(instance.id, null)
                })
            }
        }
    )
}

/** Full, interactive view of one clone: output surface + touch forwarding + the Kitchen Keyboard. */
@Composable
fun FullViewScreen(vm: Fly8ViewModel, instance: Instance, onBack: () -> Unit) {
    var keyboard by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().background(Fly8Background)) {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Fly8Text,
                modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(Modifier.size(12.dp))
            Text("#${instance.number} · ${instance.label}", color = Fly8Text,
                style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Text(if (keyboard) "Hide keys" else "Keyboard", color = Fly8Primary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.clip(RoundedCornerShape(100))
                    .background(Fly8Primary.copy(alpha = 0.15f))
                    .clickable { keyboard = !keyboard }.padding(horizontal = 12.dp, vertical = 6.dp))
        }

        Box(Modifier.weight(1f).fillMaxWidth().background(Color.Black)) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    SurfaceView(ctx).apply {
                        holder.addCallback(object : SurfaceHolder.Callback {
                            override fun surfaceCreated(h: SurfaceHolder) =
                                Fly8.sessions.attachSurface(instance.id, h.surface)
                            override fun surfaceChanged(h: SurfaceHolder, f: Int, w: Int, ht: Int) =
                                Fly8.sessions.attachSurface(instance.id, h.surface)
                            override fun surfaceDestroyed(h: SurfaceHolder) =
                                Fly8.sessions.attachSurface(instance.id, null)
                        })
                        setOnTouchListener { v, e ->
                            val action = when (e.actionMasked) {
                                MotionEvent.ACTION_DOWN -> 0
                                MotionEvent.ACTION_UP -> 1
                                MotionEvent.ACTION_MOVE -> 2
                                MotionEvent.ACTION_CANCEL -> 3
                                else -> return@setOnTouchListener true
                            }
                            Fly8.sessions.forwardTouch(instance.id, action, v.width, v.height, e.x, e.y)
                            true
                        }
                    }
                }
            )
        }

        if (keyboard) KitchenKeyboard(
            onKey = { Fly8.sessions.sendText(instance.id, it) },
            onEnter = { Fly8.sessions.sendKey(instance.id, 66) },     // KEYCODE_ENTER
            onBackspace = { Fly8.sessions.sendKey(instance.id, 67) }, // KEYCODE_DEL
            onBack = { Fly8.sessions.sendKey(instance.id, 4) }        // KEYCODE_BACK
        )
    }
}

/** The FLY8 Kitchen Keyboard — a compact text bar for the active field in a clone. */
@Composable
private fun KitchenKeyboard(
    onKey: (String) -> Unit,
    onEnter: () -> Unit,
    onBackspace: () -> Unit,
    onBack: () -> Unit
) {
    val rows = listOf("1234567890", "qwertyuiop", "asdfghjkl", "zxcvbnm")
    Column(Modifier.fillMaxWidth().background(Fly8Surface).padding(8.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                row.forEach { ch -> Key(ch.toString(), Modifier.weight(1f)) { onKey(ch.toString()) } }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            WideKey("space", Modifier.weight(1f)) { onKey(" ") }
            WideKey("⌫", Modifier.size(56.dp, 40.dp), onBackspace)
            WideKey("⏎", Modifier.size(56.dp, 40.dp), onEnter)
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Fly8Secondary)
                .clickable { onBack() }, contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Fly8Text, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Key(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(40.dp).clip(RoundedCornerShape(8.dp)).background(Fly8Panel)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) { Text(label, color = Fly8Text, style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun WideKey(label: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.height(40.dp).clip(RoundedCornerShape(8.dp)).background(Fly8Secondary)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) { Text(label, color = Fly8Text, style = MaterialTheme.typography.bodySmall) }
}
