package com.fly8.clone.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * In-app "How to use", "Terms and Conditions" and "Privacy Policy" pages read from
 * assets/help/ (EysClone ships text files under assets/customer-info; FLY8 has only notices).
 *
 * Add these files to app/src/main/assets/help/ with YOUR OWN text:
 *   how-to-use.txt, terms.txt, privacy.txt
 */
enum class Doc(val title: String, val asset: String) {
    HOW_TO("How to use", "help/how-to-use.txt"),
    TERMS("Terms and Conditions", "help/terms.txt"),
    PRIVACY("Privacy Policy", "help/privacy.txt"),
}

fun readDoc(context: Context, doc: Doc): String =
    runCatching { context.assets.open(doc.asset).bufferedReader().use { it.readText() } }
        .getOrDefault("This document could not be opened.")

@Composable
fun DocDialog(doc: Doc, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val text = remember(doc) { readDoc(context, doc) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(doc.title) },
        text = { Column(Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState())) { Text(text) } },
        confirmButton = { TextButton(onDismiss) { Text("Close") } },
    )
}
