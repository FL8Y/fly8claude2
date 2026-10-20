package com.fly8.clone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fly8.clone.data.Instance
import com.fly8.clone.data.SessionState

@Composable
fun SessionsScreen(vm: Fly8ViewModel, onConnect: () -> Unit) {
    val control by vm.controlState.collectAsStateWithLifecycle()
    val instances by vm.instances.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val stats by vm.stats.collectAsStateWithLifecycle()
    var picking by remember { mutableStateOf(false) }
    var fullView by remember { mutableStateOf<Instance?>(null) }

    if (fullView != null) {
        FullViewScreen(vm, fullView!!, onBack = { fullView = null })
        return
    }
    if (picking) {
        AppPickerSheet(vm, onDismiss = { picking = false })
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        ScreenTitle("Sessions") {
            Icon(
                Icons.Filled.PlayArrow, "Start all", tint = Fly8Primary,
                modifier = Modifier.size(26.dp).clip(RoundedCornerShape(100))
                    .clickable { if (control.connected) vm.startAll(true) else onConnect() }.padding(2.dp)
            )
            Spacer(Modifier.size(12.dp))
            Icon(
                Icons.Outlined.Add, "Add clone", tint = Fly8Text,
                modifier = Modifier.size(26.dp).clip(RoundedCornerShape(100))
                    .clickable { picking = true }.padding(2.dp)
            )
        }
        val running = sessions.values.count { it.state == SessionState.RUNNING }
        Text(
            "${instances.size} sessions · running $running · ${if (control.connected) "controls on" else "controls off"}",
            color = Fly8Muted, style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(12.dp))

        if (!control.connected) {
            Panel(onClick = onConnect) {
                Text("Connect your controls", style = MaterialTheme.typography.titleMedium, color = Fly8Text)
                Spacer(Modifier.height(4.dp))
                Text("Connect FLY8 controls to start and control sessions.", color = Fly8Muted,
                    style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(12.dp))
                PillButton("Connect controls", onConnect)
            }
            Spacer(Modifier.height(12.dp))
        }

        if (instances.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.TouchApp, null, tint = Fly8Muted, modifier = Modifier.size(40.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("No clones yet", color = Fly8Text, style = MaterialTheme.typography.titleMedium)
                    Text("Tap + to clone an installed app.", color = Fly8Muted,
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(170.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
            ) {
                items(instances, key = { it.id }) { inst ->
                    SessionCard(
                        instance = inst,
                        state = sessions[inst.id]?.state ?: SessionState.STOPPED,
                        onOpen = { fullView = inst },
                        onStart = { vm.start(inst, true) },
                        onStop = { vm.stop(inst) },
                        onRestart = { vm.restart(inst) },
                        onRejoin = { vm.rejoin(inst) },
                        onClear = { vm.clearData(inst) },
                        onRemove = { vm.remove(inst) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    instance: Instance,
    state: SessionState,
    onOpen: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit,
    onRejoin: () -> Unit,
    onClear: () -> Unit,
    onRemove: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    val running = state == SessionState.RUNNING || state == SessionState.NO_PREVIEW
    Box(
        Modifier.clip(RoundedCornerShape(16.dp)).background(Fly8Panel.copy(alpha = 0.55f))
            .clickable { if (running) onOpen() else onStart() }.padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("#${instance.number}", color = Fly8Muted, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f))
                Box {
                    Icon(Icons.Filled.MoreVert, "Menu", tint = Fly8Muted,
                        modifier = Modifier.size(18.dp).clickable { menu = true })
                    DropdownMenu(menu, { menu = false }) {
                        if (running) {
                            DropdownMenuItem({ Text("Stop") }, { menu = false; onStop() })
                            DropdownMenuItem({ Text("Restart") }, { menu = false; onRestart() })
                        } else {
                            DropdownMenuItem({ Text("Start") }, { menu = false; onStart() })
                        }
                        if (instance.isRoblox) DropdownMenuItem({ Text("Rejoin game") }, { menu = false; onRejoin() })
                        DropdownMenuItem({ Text("Clear data") }, { menu = false; onClear() })
                        DropdownMenuItem({ Text("Remove") }, { menu = false; onRemove() })
                    }
                }
            }
            Text(instance.label, color = Fly8Text, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().aspectRatio(0.72f).clip(RoundedCornerShape(10.dp))) {
                // A running clone with no attached preview surface stays "running" — do not fake a frame.
                if (state == SessionState.STARTING || state == SessionState.RECOVERING) {
                    ShimmerBox(Modifier.fillMaxSize())
                } else if (running) {
                    Box(Modifier.fillMaxSize().background(Color.Black)) {
                        LivePreview(instance, Modifier.fillMaxSize())
                    }
                } else {
                    Box(Modifier.fillMaxSize().background(Fly8Secondary.copy(alpha = 0.3f)))
                }
            }
            Spacer(Modifier.height(8.dp))
            val (label, color) = when (state) {
                SessionState.RUNNING -> "Live view" to Fly8Good
                SessionState.NO_PREVIEW -> "Running · no preview" to Fly8Good
                SessionState.STARTING -> "Starting…" to Fly8Primary
                SessionState.RECOVERING -> "Recovering…" to Fly8Primary
                SessionState.ERROR -> "Error · tap to retry" to Fly8Pink
                else -> "Stopped · tap to start" to Fly8Muted
            }
            Text(label, color = color, style = MaterialTheme.typography.labelSmall)
        }
    }
}
