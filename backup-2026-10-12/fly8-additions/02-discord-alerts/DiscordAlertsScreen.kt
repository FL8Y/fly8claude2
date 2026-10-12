package com.fly8.clone.alerts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fly8.clone.data.EventType
import com.fly8.clone.session.label
import kotlinx.coroutines.launch

/** "Logs → Discord alerts" dialog. Show it from the Logs dialog header. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DiscordAlertsDialog(onDismiss: () -> Unit) {
    val cfg = DiscordAlerts.config
    val status by DiscordAlerts.status.collectAsState()
    var url by remember { mutableStateOf("") }  // never prefilled: saved URL stays hidden
    var nick by remember { mutableStateOf(cfg.nickname) }
    var enabled by remember { mutableStateOf(cfg.enabled) }
    var types by remember { mutableStateOf(cfg.types) }
    var note by remember { mutableStateOf<String?>(null) }
    var confirmRemove by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Discord alerts") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Paste a webhook from Discord: Edit Channel → Integrations → Webhooks. Use a private channel.")
                OutlinedTextField(
                    value = url, onValueChange = { url = it }, singleLine = true,
                    label = { Text(if (cfg.url.isNotBlank()) "Webhook URL (saved · leave empty to keep)" else "Webhook URL") },
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(nick, { nick = it.take(40) }, singleLine = true,
                    label = { Text("Device nickname") }, modifier = Modifier.fillMaxWidth())
                Text("Send alerts for")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    EventType.entries.forEach { t ->
                        FilterChip(selected = t in types, onClick = { types = if (t in types) types - t else types + t },
                            label = { Text(t.label()) })
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Automatic alerts", Modifier.weight(1f)); Switch(enabled, { enabled = it })
                }
                Text(
                    when (val s = status) {
                        DiscordAlerts.Status.Off -> "Alerts off"
                        DiscordAlerts.Status.Ready -> "Ready for selected events"
                        is DiscordAlerts.Status.Sending -> "Sending alert…"
                        is DiscordAlerts.Status.Delivered -> "Last alert delivered"
                        is DiscordAlerts.Status.RateLimited -> "Discord rate limit; waiting"
                        is DiscordAlerts.Status.Failed -> "Alert not delivered: ${s.reason}"
                    }
                )
                note?.let { Text(it) }
                Text("Alerts are best effort. Restart and rejoin messages describe attempts, not confirmed joins.")
                if (cfg.url.isNotBlank()) TextButton({ confirmRemove = true }) { Text("Remove webhook") }
            }
        },
        confirmButton = {
            TextButton({
                if (enabled && types.isEmpty()) { note = "Select at least one event type."; return@TextButton }
                if (enabled && url.isBlank() && cfg.url.isBlank()) { note = "Add a webhook URL before enabling alerts."; return@TextButton }
                note = if (DiscordAlerts.save(url, enabled, nick, types)) "Saved." else "Could not save settings. Alerts are paused. Try again."
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton({
                if (url.isBlank() && cfg.url.isBlank()) { note = "Add a webhook URL first."; return@TextButton }
                note = "Saving and sending test…"
                DiscordAlerts.save(url, enabled, nick, types)
                scope.launch {
                    note = DiscordAlerts.sendTest(url, nick).fold({ "Test delivered" }, { "Test failed: ${it.message}" })
                }
            }) { Text("Send test") }
        },
    )
    if (confirmRemove) AlertDialog(
        onDismissRequest = { confirmRemove = false },
        title = { Text("Remove Discord webhook?") },
        text = { Text("Turns off alerts and removes this app's saved URL. The webhook itself remains in Discord.") },
        confirmButton = { TextButton({ DiscordAlerts.removeWebhook(); confirmRemove = false }) { Text("Remove") } },
        dismissButton = { TextButton({ confirmRemove = false }) { Text("Cancel") } },
    )
}
