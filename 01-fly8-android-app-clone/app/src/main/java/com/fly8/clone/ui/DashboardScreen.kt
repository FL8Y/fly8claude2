package com.fly8.clone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(vm: Fly8ViewModel, onConnect: () -> Unit) {
    val control by vm.controlState.collectAsStateWithLifecycle()
    val stats by vm.stats.collectAsStateWithLifecycle()
    val instances by vm.instances.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val logs by vm.logs.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTitle("Dashboard")

        // Identity / connect card.
        Panel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                        .then(Modifier).background(
                            Brush.linearGradient(listOf(Fly8Primary, Fly8Secondary))
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("F8", color = Fly8Text, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.size(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("FLY8", style = MaterialTheme.typography.titleMedium, color = Fly8Text)
                    Text(vm.capabilities.name + " backend", color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
                }
                StatusChip(
                    if (control.connected) "Connected" else "Offline",
                    if (control.connected) Fly8Good else Fly8Muted
                )
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Fly8Line.copy(alpha = 0.5f)))
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.TouchApp, null, tint = Fly8Primary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(10.dp))
                Text(
                    "Connect controls",
                    Modifier.weight(1f).clip(RoundedCornerShape(8.dp)),
                    color = Fly8Text
                )
                PillButton("Open", onConnect)
            }
        }

        // Stats card.
        Panel {
            Row(Modifier.fillMaxWidth()) {
                StatCell("Running apps", stats.runningApps.toString(), Modifier.weight(1f))
                StatCell("Chipset", stats.chipset.ifBlank { "—" }, Modifier.weight(1f))
            }
            Spacer(Modifier.height(14.dp))
            SectionLabel("Processing")
            KeyValue("Preview updates", String.format("%.1f/s · %d active", stats.updatesPerSec, stats.runningApps))
            KeyValue("Previews", if (control.connected) "sampled on demand" else "paused")
            Spacer(Modifier.height(14.dp))
            Text("Storage", color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            Text(
                String.format("%.2f GB / %.2f GB used", stats.storageUsedGb, stats.storageTotalGb),
                color = Fly8Text, fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Meter(if (stats.storageTotalGb > 0) stats.storageUsedGb / stats.storageTotalGb else 0f, Fly8Primary)
            Spacer(Modifier.height(12.dp))
            Text("RAM", color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            Text(
                String.format("%.2f GB / %.2f GB available", stats.ramAvailGb, stats.ramTotalGb),
                color = Fly8Text, fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(6.dp))
            Meter(if (stats.ramTotalGb > 0) 1f - stats.ramAvailGb / stats.ramTotalGb else 0f, Fly8Good)
        }

        // Roblox auto recovery card.
        Panel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Shield, null, tint = Fly8Good, modifier = Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Roblox Auto Recovery", style = MaterialTheme.typography.titleMedium, color = Fly8Text)
            }
            Spacer(Modifier.height(6.dp))
            val robloxCount = instances.count { it.isRoblox }
            Text(
                if (robloxCount == 0) "Add Roblox from Sessions to set up recovery."
                else "$robloxCount Roblox instance(s) · recovery " +
                    (if (instances.any { it.isRoblox && it.recoveryEnabled }) "on" else "off"),
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall
            )
        }

        // Recovery activity.
        Panel {
            Text("Recovery activity", style = MaterialTheme.typography.titleMedium, color = Fly8Text)
            Spacer(Modifier.height(8.dp))
            if (logs.isEmpty()) {
                Text("No recovery activity yet.", color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            } else {
                logs.take(4).forEach {
                    Text("• ${it.message}", color = Fly8Muted, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
        Spacer(Modifier.height(60.dp))
    }
}

@Composable
private fun StatCell(label: String, value: String, modifier: Modifier) {
    Column(modifier) {
        Text(label, color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
        Text(value, color = Fly8Text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}
