package com.fly8.clone.ui

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.fly8.clone.data.Instance
import com.fly8.clone.data.SpaceKind
import top.niunaijun.blackbox.core.env.BEnvironment

/**
 * "Clone folders" as EysClone implements it (REA/JADX: DashboardActivity → ve case 6 →
 * m1 case 3 → j1 case 10): pick an instance, see where its files live, "Copy paths",
 * and "Storage access" when All-files access is missing. EysClone shows
 * "Shared files" (the clone's virtual sdcard) and "App files · app-specific".
 *
 * FLY8 equivalents, from BEnvironment in FLY8's engine:
 *   shared files      = getExternalUserDir(userId)               …/storage/emulated/<user>/
 *   app-specific      = getExternalDataDir(packageName, userId)  …/Android/data/<pkg>
 *   private app data  = getDataDir(packageName, userId)          (inside FLY8's private storage)
 * Android-profile clones (SpaceKind != VIRTUAL) are separate Android users; their files
 * are only reachable from inside that profile.
 */
object CloneFolderPaths {
    fun describe(i: Instance): String =
        if (i.space != SpaceKind.VIRTUAL) {
            "${i.title}\n\nThis clone is an Android ${i.space.name.lowercase().replace('_', ' ')} (user ${i.userId}). " +
                "Its files are inside that profile; open them with a file manager running in the profile."
        } else runCatching {
            "${i.packageName} · Instance #${i.number}\n\n" +
                "Shared files\n${BEnvironment.getExternalUserDir(i.userId).absolutePath}\n\n" +
                "App files · app-specific\n${BEnvironment.getExternalDataDir(i.packageName, i.userId).absolutePath}\n\n" +
                "Private app data (not visible to file managers)\n${BEnvironment.getDataDir(i.packageName, i.userId).absolutePath}"
        }.getOrElse { "Could not read this clone's folders. Make sure FLY8's engine is ready and try again." }

    fun copy(context: Context, text: String) {
        context.getSystemService(ClipboardManager::class.java)?.setPrimaryClip(ClipData.newPlainText("Clone folders", text))
    }

    /** Same checks as EysClone: All-files access on API 30+, WRITE_EXTERNAL_STORAGE on Android 10. */
    fun needsStorageAccess(context: Context) =
        if (Build.VERSION.SDK_INT >= 30) !Environment.isExternalStorageManager()
        else context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED

    fun openStorageAccess(context: Context) {
        val action = if (Build.VERSION.SDK_INT >= 30) Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
        else Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val i = Intent(action, Uri.parse("package:" + context.packageName)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { context.startActivity(i) }
    }
}

@Composable
fun CloneFoldersDialog(instance: Instance, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val text = remember(instance.id) { CloneFolderPaths.describe(instance) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clone folders") },
        text = { Column(Modifier.verticalScroll(rememberScrollState())) { SelectionContainer { Text(text) } } },
        confirmButton = { TextButton(onDismiss) { Text("Close") } },
        dismissButton = {
            Column {
                TextButton({ CloneFolderPaths.copy(context, text) }) { Text("Copy paths") }
                if (CloneFolderPaths.needsStorageAccess(context)) TextButton({ CloneFolderPaths.openStorageAccess(context) }) { Text("Storage access") }
            }
        },
    )
}
