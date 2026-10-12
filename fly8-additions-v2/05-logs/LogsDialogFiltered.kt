package com.fly8.clone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fly8.clone.Fly8
import com.fly8.clone.alerts.DiscordAlertsDialog
import com.fly8.clone.data.EventType
import com.fly8.clone.data.LogCategory
import com.fly8.clone.session.label
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Replacement for FLY8's LogsDialog: adds EysClone's "Filter logs by instance" and
 * "Filter logs by event", plus a Discord alerts button. FLY8's dialog already has
 * Copy and Clear (Fly8ViewModel.copyDiagnostics / clearLogs); Clear is kept here.
 * LogStore already keeps instance + type on every LogEvent, so no storage change.
 * LogStore keeps events NEWEST FIRST (it prepends and caps at LIMIT = 500), so the
 * list is shown in stored order.
 */
@Composable
fun LogsDialogFiltered(onDismiss: () -> Unit) {
    val events by Fly8.log.events.collectAsState()
    val instances by Fly8.store.items.collectAsState()
    var instance by remember { mutableStateOf<String?>(null) }   // null = all instances
    var type by remember { mutableStateOf<EventType?>(null) }    // null = all events
    var confirmClear by remember { mutableStateOf(false) }
    var showAlerts by remember { mutableStateOf(false) }
    val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    val shown = events.filter {   // newest first, as stored
        (instance == null || it.instance == instance) && (type == null || it.type == type)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Activity log") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Picker(
                        label = instance?.let { id -> instances.firstOrNull { it.id == id }?.title ?: "Instance" } ?: "All instances",
                        options = listOf<Pair<String, String?>>("All instances" to null) + instances.map { it.title to it.id },
                        onPick = { instance = it },
                    )
                    Picker(
                        label = type?.label() ?: "All events",
                        options = listOf<Pair<String, EventType?>>("All events" to null) +
                            EventType.entries.map { t -> t.label() to t },
                        onPick = { type = it },
                    )
                }
                if (shown.isEmpty()) Text("No activity yet.")
                LazyColumn(Modifier.heightIn(max = 420.dp)) {
                    items(shown) { e ->
                        Column(Modifier.padding(vertical = 4.dp)) {
                            Text("${fmt.format(Date(e.time))} · ${e.type.name.lowercase()}", style = MaterialTheme.typography.labelSmall)
                            Text(e.message, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Text("Showing the latest ${shown.size} of ${events.size} entries.", style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { TextButton(onDismiss) { Text("Close") } },
        dismissButton = {
            Row {
                TextButton({ showAlerts = true }) { Text("Discord alerts") }
                TextButton({ confirmClear = true }) { Text("Clear logs") }
            }
        },
    )
    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text("Clear local logs?") },
        text = { Text("This removes local history only. Alerts already sent to Discord stay there.") },
        confirmButton = { TextButton({ LogCategory.entries.forEach { Fly8.log.clear(it) }; confirmClear = false }) { Text("Clear") } },
        dismissButton = { TextButton({ confirmClear = false }) { Text("Cancel") } },
    )
    if (showAlerts) DiscordAlertsDialog { showAlerts = false }
}

@Composable
private fun <T> Picker(label: String, options: List<Pair<String, T>>, onPick: (T) -> Unit) {
    var open by remember { mutableStateOf(false) }
    OutlinedButton({ open = true }) { Text(label) }
    DropdownMenu(open, { open = false }) {
        options.forEach { (name, value) -> DropdownMenuItem(text = { Text(name) }, onClick = { onPick(value); open = false }) }
    }
}
