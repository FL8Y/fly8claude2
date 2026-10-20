package com.fly8.clone.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AppPickerSheet(vm: Fly8ViewModel, onDismiss: () -> Unit) {
    val apps by vm.installedApps.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(Unit) { vm.loadInstalledApps() }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = Fly8Surface) {
        Column(Modifier.fillMaxHeight(0.85f).padding(horizontal = 16.dp)) {
            Text("Choose an app to clone", style = MaterialTheme.typography.titleMedium, color = Fly8Text)
            Spacer(Modifier.height(10.dp))
            Box(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                    .background(Fly8Panel).padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (query.isEmpty()) Text("Search apps", color = Fly8Muted,
                    style = MaterialTheme.typography.bodyMedium)
                BasicTextField(
                    value = query, onValueChange = { query = it },
                    textStyle = TextStyle(color = Fly8Text),
                    cursorBrush = SolidColor(Fly8Primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(10.dp))
            val filtered = apps.filter { it.label.contains(query, true) || it.packageName.contains(query, true) }
            LazyColumn {
                items(filtered, key = { it.packageName }) { app ->
                    Row(
                        Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
                            .clickable { vm.addClone(app); onDismiss() }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Fly8Secondary),
                            contentAlignment = Alignment.Center) {
                            Text(app.label.take(1).uppercase(), color = Fly8Text, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.size(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(app.label, color = Fly8Text, maxLines = 1)
                            Text(app.packageName, color = Fly8Muted,
                                style = MaterialTheme.typography.labelSmall, maxLines = 1)
                        }
                        PillButton("Clone", { vm.addClone(app); onDismiss() })
                    }
                }
            }
        }
    }
}
