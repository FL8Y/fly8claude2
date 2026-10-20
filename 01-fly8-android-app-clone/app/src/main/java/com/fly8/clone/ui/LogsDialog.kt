package com.fly8.clone.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LogsDialog(vm: Fly8ViewModel, onDismiss: () -> Unit) {
    val logs by vm.logs.collectAsStateWithLifecycle()
    val fmt = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Fly8Surface,
        title = { Text("Activity log", color = Fly8Text) },
        confirmButton = { TextButton(onDismiss) { Text("Close", color = Fly8Primary) } },
        dismissButton = {
            Row {
                TextButton({ vm.copyDiagnostics() }) { Text("Copy report", color = Fly8Primary) }
                TextButton({ vm.clearLogs() }) { Text("Clear", color = Fly8Muted) }
            }
        },
        text = {
            if (logs.isEmpty()) {
                Text("No activity yet.", color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 380.dp)) {
                    items(logs) { e ->
                        Column(Modifier.padding(vertical = 6.dp)) {
                            Text(
                                "${fmt.format(Date(e.time))} · ${e.category.name.lowercase()}",
                                color = Fly8Muted, style = MaterialTheme.typography.labelSmall
                            )
                            Text(e.message, color = Fly8Text, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    )
}
