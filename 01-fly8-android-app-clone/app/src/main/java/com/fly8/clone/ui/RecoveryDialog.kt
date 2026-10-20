package com.fly8.clone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fly8.clone.data.RecoverySettings

@Composable
fun RecoveryDialog(vm: Fly8ViewModel, onDismiss: () -> Unit) {
    var s by remember { mutableStateOf(vm.recovery.value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Fly8Surface,
        title = { Text("Advanced recovery", color = Fly8Text) },
        confirmButton = { TextButton({ vm.saveRecovery(s); onDismiss() }) { Text("Save", color = Fly8Primary) } },
        dismissButton = { TextButton(onDismiss) { Text("Cancel", color = Fly8Muted) } },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                ToggleRow("Restart exited or unresponsive games", s.restartExited) { s = s.copy(restartExited = it) }
                ToggleRow("Rejoin after network changes", s.rejoinOnNetworkChange) { s = s.copy(rejoinOnNetworkChange = it) }
                ToggleRow("Rejoin after public IP changes", s.rejoinOnPublicIpChange) { s = s.copy(rejoinOnPublicIpChange = it) }

                Spacer(Modifier.height(10.dp))
                SectionLabel("Scheduled force rejoin")
                OptionChips(
                    options = RecoverySettings.FORCE_REJOIN_OPTIONS.map { if (it == 0) "Off" else "Every $it min" },
                    selectedIndex = RecoverySettings.FORCE_REJOIN_OPTIONS.indexOf(s.forceRejoinMinutes).coerceAtLeast(0)
                ) { s = s.copy(forceRejoinMinutes = RecoverySettings.FORCE_REJOIN_OPTIONS[it]) }
                Text(
                    "Restarts running instances and rejoins their saved targets on this schedule.",
                    color = Fly8Muted, style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(Modifier.height(12.dp))
                ToggleRow("Recover prolonged unchanged screens", s.recoverUnchanged) { s = s.copy(recoverUnchanged = it) }
                if (s.recoverUnchanged) {
                    SectionLabel("Unchanged screen wait")
                    OptionChips(
                        options = RecoverySettings.UNCHANGED_WAIT_OPTIONS.map { "$it min" },
                        selectedIndex = RecoverySettings.UNCHANGED_WAIT_OPTIONS.indexOf(s.unchangedWaitMinutes).coerceAtLeast(1)
                    ) { s = s.copy(unchangedWaitMinutes = RecoverySettings.UNCHANGED_WAIT_OPTIONS[it]) }
                    Text(
                        "A still menu or loading screen can look stalled. Preview repair is tried first; a " +
                            "missing preview alone never restarts the game.",
                        color = Fly8Muted, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                SectionLabel("Restart interval · 10 to 120 seconds")
                NumberField(s.restartIntervalSeconds.toString()) {
                    s = s.copy(restartIntervalSeconds = (it.toIntOrNull() ?: 20).coerceIn(10, 120))
                }

                Spacer(Modifier.height(10.dp))
                SectionLabel("Shared target · optional fallback")
                TextFieldBox(s.sharedTarget, "Roblox game link or place ID") { s = s.copy(sharedTarget = it) }
            }
        }
    )
}

@Composable
private fun ToggleRow(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Fly8Text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(
            checked = checked, onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Fly8Text, checkedTrackColor = Fly8Primary,
                uncheckedThumbColor = Fly8Muted, uncheckedTrackColor = Fly8Secondary
            )
        )
    }
}

@Composable
private fun OptionChips(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEachIndexed { i, label ->
            val sel = i == selectedIndex
            Text(
                label, color = if (sel) Fly8Text else Fly8Muted,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.clip(RoundedCornerShape(100))
                    .background(if (sel) Fly8Primary else Fly8Panel)
                    .clickable { onSelect(i) }.padding(horizontal = 10.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun NumberField(value: String, onChange: (String) -> Unit) =
    TextFieldBox(value, "20") { onChange(it.filter(Char::isDigit)) }

@Composable
private fun TextFieldBox(value: String, placeholder: String, onChange: (String) -> Unit) {
    Box(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Fly8Panel)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        if (value.isEmpty()) Text(placeholder, color = Fly8Muted, style = MaterialTheme.typography.bodyMedium)
        BasicTextField(
            value = value, onValueChange = onChange,
            textStyle = TextStyle(color = Fly8Text),
            cursorBrush = SolidColor(Fly8Primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
