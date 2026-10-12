package com.fly8.clone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fly8.clone.data.Instance
import com.fly8.clone.data.JoinLinks
import com.fly8.clone.data.JoinMode
import com.fly8.clone.session.JoinPlanner

/**
 * "Edit game › Joining mode" (EysClone 1.4.15). Flow from the release notes:
 * Edit game > Private server > paste invitation > Done > Save changes.
 * The public game link stays in FLY8's existing InstanceSettingsDialog field
 * (Instance.rejoinTarget); this dialog adds the mode, the invitation and the override.
 */
@Composable
fun EditGameJoinModeDialog(instance: Instance, onDismiss: () -> Unit) {
    val store = JoinPlanner.settings()
    val saved = remember(instance.id) { store.get(instance.id) }
    var mode by remember { mutableStateOf(saved.mode) }
    var invite by remember { mutableStateOf(saved.privateInvite) }
    var override by remember { mutableStateOf(saved.publicOverride) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Joining mode") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Choose how this instance joins. Both apply to launches, recovery, and scheduled rejoin.")
                ModeRow(mode == JoinMode.PUBLIC_LOW, "Public low-player server",
                    "Public servers listing 0–1 players. Results can change; fewer players does not mean lower ping.") { mode = JoinMode.PUBLIC_LOW }
                ModeRow(mode == JoinMode.PRIVATE, "Private server",
                    "Opens the saved private invitation directly. Your account must have access to that server.") { mode = JoinMode.PRIVATE }
                if (mode == JoinMode.PRIVATE) {
                    OutlinedTextField(invite, { invite = it; error = null }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Private-server invitation") },
                        placeholder = { Text("https://www.roblox.com/share?code=…&type=Server") })
                    OutlinedTextField(override, { override = it; error = null }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Public game override (optional)") },
                        supportingText = { Text("Used only by scheduled force rejoin.") })
                } else {
                    Text("Uses this instance's game link, or Default game.", style = MaterialTheme.typography.bodySmall)
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            TextButton({
                val hasDefault = store.defaultInvite.isNotEmpty()
                error = store.save(instance.id, mode, if (mode == JoinMode.PRIVATE) invite else "", if (mode == JoinMode.PRIVATE) override else "", hasDefault)
                if (error == null) onDismiss()
            }) { Text("Save changes") }
        },
        dismissButton = { TextButton(onDismiss) { Text("Cancel") } },
    )
}

@Composable
private fun ModeRow(selected: Boolean, title: String, help: String, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        RadioButton(selected, onClick)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(help, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** True when [raw] looks like a private invitation, to warn users who paste one in Public mode. */
fun looksPrivate(raw: String) = JoinLinks.privateLink(raw) != null
