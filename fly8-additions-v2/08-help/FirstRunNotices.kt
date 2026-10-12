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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Two small EysClone screens FLY8 lacks:
 *  - Welcome / terms notice shown once ("Welcome to EysClone", "Get started",
 *    "Thank you for using EysClone. By continuing to use the app, you agree to its Terms
 *    and Conditions."; stored as "seen_terms", REA/JADX class nb).
 *  - "What's new" after an update.
 * Text comes from assets/help/ (see DocsScreen.kt). Put your own wording there.
 */
object FirstRunNotices {
    private fun prefs(c: Context) = c.getSharedPreferences("fly8_notices", Context.MODE_PRIVATE)

    fun needsWelcome(c: Context) = !prefs(c).getBoolean("seen_terms", false)
    fun markWelcomed(c: Context) = prefs(c).edit().putBoolean("seen_terms", true).apply()

    /** True once per installed versionCode, but never on the very first install. */
    fun needsWhatsNew(c: Context): Boolean {
        @Suppress("DEPRECATION")
        val code = c.packageManager.getPackageInfo(c.packageName, 0).longVersionCode
        val seen = prefs(c).getLong("whats_new", -1)
        if (seen == -1L) { prefs(c).edit().putLong("whats_new", code).apply(); return false }
        return seen != code
    }

    fun markWhatsNew(c: Context) {
        @Suppress("DEPRECATION")
        val code = c.packageManager.getPackageInfo(c.packageName, 0).longVersionCode
        prefs(c).edit().putLong("whats_new", code).apply()
    }

    fun whatsNewText(c: Context): String =
        runCatching { c.assets.open("help/whats-new.txt").bufferedReader().use { it.readText() } }.getOrDefault("Bug fixes and improvements.")
}

/** Call at the top of Fly8App(): shows the welcome first, then What's new if due. */
@Composable
fun FirstRunGate() {
    val context = LocalContext.current
    var welcome by remember { mutableStateOf(FirstRunNotices.needsWelcome(context)) }
    var whatsNew by remember { mutableStateOf(!welcome && FirstRunNotices.needsWhatsNew(context)) }
    var doc by remember { mutableStateOf<Doc?>(null) }

    if (welcome) AlertDialog(
        onDismissRequest = {},
        title = { Text("Welcome to FLY8") },
        text = {
            Column {
                Text("By continuing to use the app, you agree to its Terms and Conditions and Privacy Policy.")
                TextButton({ doc = Doc.TERMS }) { Text("Terms and Conditions") }
                TextButton({ doc = Doc.PRIVACY }) { Text("Privacy Policy") }
            }
        },
        confirmButton = { TextButton({ FirstRunNotices.markWelcomed(context); welcome = false }) { Text("Get started") } },
    )
    if (whatsNew) AlertDialog(
        onDismissRequest = { FirstRunNotices.markWhatsNew(context); whatsNew = false },
        title = { Text("What’s new") },
        text = {
            Column(Modifier.heightIn(max = 420.dp).verticalScroll(rememberScrollState())) {
                Text(remember { FirstRunNotices.whatsNewText(context) })
            }
        },
        confirmButton = { TextButton({ FirstRunNotices.markWhatsNew(context); whatsNew = false }) { Text("OK") } },
    )
    doc?.let { d -> DocDialog(d) { doc = null } }
}
