package com.fly8.clone.ui

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConnectScreen(vm: Fly8ViewModel, onBack: () -> Unit) {
    val control by vm.controlState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showManual by remember { mutableStateOf(false) }
    var manualPort by remember { mutableStateOf("") }

    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Fly8Text,
                modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(Modifier.size(12.dp))
            Text("Connect controls", style = MaterialTheme.typography.titleLarge, color = Fly8Text)
        }

        // Status banner.
        Panel {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (control.busy) {
                    CircularProgressIndicator(Modifier.size(18.dp), color = Fly8Primary, strokeWidth = 2.dp)
                    Spacer(Modifier.size(10.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        if (control.connected) "Controls connected" else "Controls disconnected",
                        color = if (control.connected) Fly8Good else Fly8Text,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(control.message, color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
                }
                if (control.connected) PillButton("Disconnect", { vm.disconnectControls() })
            }
        }

        // Wireless debugging.
        Panel {
            SectionLabel("Wireless debugging")
            Text(
                "Enable Wireless debugging, then tap Pair device with pairing code. Enter the code in the FLY8 notification while that dialog stays open.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(12.dp))
            ActionRow("Start pairing") { vm.startPairing() }
            ActionRow("Connect paired device") { vm.connectWireless() }
            ActionRow("Wireless debugging settings") { vm.openWirelessSettings() }
            ActionRow("Notification settings") { vm.openNotificationSettings() }
            ActionRow(if (showManual) "Hide manual setup" else "Manual setup") { showManual = !showManual }
            Reveal(showManual) {
                Column(Modifier.padding(top = 8.dp)) {
                    Text("Connection port", color = Fly8Muted, style = MaterialTheme.typography.labelSmall)
                    Box(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Fly8Panel)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        if (manualPort.isEmpty()) Text("e.g. 37555", color = Fly8Muted)
                        BasicTextField(
                            value = manualPort, onValueChange = { manualPort = it.filter(Char::isDigit).take(5) },
                            textStyle = TextStyle(color = Fly8Text),
                            cursorBrush = SolidColor(Fly8Primary),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    PillButton("Connect to port", {
                        manualPort.toIntOrNull()?.let { vm.connectManual(it) }
                    }, enabled = manualPort.toIntOrNull() in 1..65535)
                }
            }
        }

        // Root.
        Panel {
            SectionLabel("Root access")
            Text("For devices that are already rooted. Approve FLY8 in your root manager.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            ActionRow("Connect with root") { vm.connectRoot() }
        }

        // Shizuku.
        Panel {
            SectionLabel("Shizuku (optional)")
            Text("Use Shizuku as an alternative connection method. FLY8 never requires it.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            ActionRow("Connect with Shizuku") { vm.connectShizuku() }
        }

        // Computer activation.
        Panel {
            SectionLabel("Computer activation")
            Text("Enable USB debugging, connect to a computer, and run the copied command. Repeat after a device restart.",
                color = Fly8Muted, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(10.dp))
            ActionRow("Copy activation command") {
                val cmd = vm.activationCommand()
                val cm = context.getSystemService(ClipboardManager::class.java)
                cm.setPrimaryClip(ClipData.newPlainText("FLY8 activation", cmd))
                vm.awaitComputerActivation()
            }
        }

        PillButton("Back to Sessions", onBack)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun ActionRow(text: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Fly8Text, modifier = Modifier.weight(1f))
        Text("›", color = Fly8Muted, style = MaterialTheme.typography.titleMedium)
    }
}
