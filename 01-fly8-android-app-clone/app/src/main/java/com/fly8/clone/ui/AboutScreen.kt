package com.fly8.clone.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fly8.clone.BuildConfig

@Composable
fun AboutScreen(vm: Fly8ViewModel) {
    var showRecovery by remember { mutableStateOf(false) }
    var showNotices by remember { mutableStateOf(false) }
    var showLogs by remember { mutableStateOf(false) }

    if (showRecovery) RecoveryDialog(vm) { showRecovery = false }
    if (showNotices) NoticesDialog { showNotices = false }
    if (showLogs) LogsDialog(vm) { showLogs = false }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        ScreenTitle("About")

        Panel {
            Text("FLY8", style = MaterialTheme.typography.titleLarge, color = Fly8Text)
            Text("Version ${BuildConfig.VERSION_NAME}", color = Fly8Muted,
                style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(8.dp))
            Text(
                "A clean, local-first app session manager. Clone an installed app, run it in an " +
                    "isolated space, and keep sessions alive in the background. No activation, no " +
                    "license server — your instance list lives on this device.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall
            )
        }

        Panel {
            SectionLabel("Settings")
            AboutRow("Advanced recovery") { showRecovery = true }
            AboutRow("Activity log") { showLogs = true }
            AboutRow("Open-source notices") { showNotices = true }
        }

        Panel {
            SectionLabel("Backend")
            Text(vm.capabilities.name, color = Fly8Text, modifier = Modifier.padding(bottom = 4.dp))
            Text(vm.capabilities.note, color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
        }

        Panel {
            SectionLabel("Privacy")
            Text(
                "FLY8 keeps everything on this device. It makes no network calls of its own except " +
                    "Wireless debugging on your local network. Pairing codes and keys are never stored " +
                    "in logs.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.height(60.dp))
    }
}

@Composable
private fun AboutRow(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = Fly8Text,
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp)
    )
}

@Composable
private fun NoticesDialog(onDismiss: () -> Unit) {
    val notices = listOf(
        "LibADB — Apache 2.0",
        "sun-security-android — GPL/BSD (ADB TLS)",
        "SPAKE2 — used via LibADB for pairing",
        "Conscrypt — Apache 2.0",
        "Shizuku API — Apache 2.0 (optional)",
        "AndroidX / Jetpack Compose — Apache 2.0"
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close", color = Fly8Primary) } },
        containerColor = Fly8Surface,
        title = { Text("Open-source notices", color = Fly8Text) },
        text = {
            Column {
                notices.forEach {
                    Text(it, color = Fly8Muted, style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 6.dp))
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Built with third-party components; their licences and notices remain applicable. " +
                        "In-app virtualization, when added, will carry VirtualApp's Apache 2.0 notice.",
                    color = Fly8Muted, style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}
