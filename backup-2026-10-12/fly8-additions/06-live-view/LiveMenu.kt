package com.fly8.clone.ui

import android.view.KeyEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.fly8.clone.Fly8
import com.fly8.clone.session.liveOf
import kotlinx.coroutines.launch

/**
 * Live (full view) menu: Back in game, Display options, Session options (with Retry
 * preview, Restart, Stop, Diagnostics) and Hide controls.
 *
 * LiveSession.retryPreview() ALREADY EXISTS in FLY8 (used automatically by
 * PreviewControl); this simply gives the user a button for it.
 */
@Composable
fun LiveMenu(
    instanceId: String,
    previewOptions: PreviewOptions,
    onPreviewOptions: (PreviewOptions) -> Unit,
    controlsHidden: Boolean,
    onHideControls: (Boolean) -> Unit,
    onOpenDiagnostics: () -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit,
) {
    var open by remember { mutableStateOf(false) }
    var sessionMenu by remember { mutableStateOf(false) }
    var display by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Box {
        IconButton({ open = true }) { Icon(Icons.Default.MoreVert, "Live options") }
        DropdownMenu(open, { open = false }) {
            DropdownMenuItem(text = { Text("Back in game") }, onClick = {
                Fly8.sessions.sendKey(instanceId, KeyEvent.KEYCODE_BACK); open = false
            })
            DropdownMenuItem(text = { Text("Display options") }, onClick = { display = true; open = false })
            DropdownMenuItem(text = { Text("Session options") }, onClick = { sessionMenu = true; open = false })
            DropdownMenuItem(text = { Text(if (controlsHidden) "Show controls" else "Hide controls") },
                onClick = { onHideControls(!controlsHidden); open = false })
        }
        DropdownMenu(sessionMenu, { sessionMenu = false }) {
            DropdownMenuItem(text = { Text("Retry preview") }, onClick = {
                sessionMenu = false
                scope.launch {
                    val live = Fly8.sessions.liveOf(instanceId)
                    val ok = live?.retryPreview() ?: false
                    note = if (ok) "Preview refreshed" else "Preview still stale · stop and relaunch this session"
                }
            })
            DropdownMenuItem(text = { Text("Restart session") }, onClick = { sessionMenu = false; onRestart() })
            DropdownMenuItem(text = { Text("Stop session") }, onClick = { sessionMenu = false; onStop() })
            DropdownMenuItem(text = { Text("Open diagnostics") }, onClick = { sessionMenu = false; onOpenDiagnostics() })
        }
    }
    if (display) DisplayOptionsDialog(previewOptions, onPreviewOptions) { display = false }
}

/*
 * liveOf() is in 00-shared/SessionExtensions.kt. SessionManager.sendKey(id, keyCode) is already public.
 *
 * Hide controls: wrap the toolbar/keyboard row of LiveView in `if (!controlsHidden) { ... }`
 * and show a small translucent handle at the edge that calls onHideControls(false).
 */
