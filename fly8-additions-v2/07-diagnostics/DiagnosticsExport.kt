package com.fly8.clone.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.fly8.clone.Fly8
import com.fly8.clone.data.LogCategory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Support report: Copy (FLY8 has it: Fly8ViewModel.copyDiagnostics), Export to a file and
 * Share (missing; EysClone DiagnosticsActivity "Export report"), and "Clear diagnostic
 * history". The report holds stages and device metadata, never pairing codes or webhook
 * URLs: LogStore.redact() already replaces webhook URLs, "code/token/password: x" pairs
 * and any six-digit number before an event is stored.
 */
object SupportReport {
    fun build(context: Context): String {
        val pm = context.packageManager
        val pi = pm.getPackageInfo(context.packageName, 0)
        val sb = StringBuilder()
        sb.appendLine("FLY8 support report")
        sb.appendLine("Version: ${pi.versionName} (${pi.longVersionCode})")
        sb.appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        sb.appendLine("ABIs: ${Build.SUPPORTED_ABIS.joinToString()}")
        sb.appendLine("Generated: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US).format(Date())}")
        sb.appendLine()
        sb.appendLine("Instances:")
        Fly8.store.items.value.forEach {
            sb.appendLine("  #${it.number} ${it.packageName} space=${it.space} user=${it.userId} recovery=${it.recoveryEnabled}")
        }
        sb.appendLine()
        sb.appendLine("Recent events (oldest first):")
        // LogStore stores newest first: take the 200 newest, then print them oldest -> newest.
        Fly8.log.events.value.take(200).asReversed().forEach {
            sb.appendLine("  ${it.time} ${it.category}/${it.type} [${it.instance ?: "-"}] ${it.message}")
        }
        return sb.toString()
    }

    fun fileName() = "fly8-report-" + SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date()) + ".txt"

    fun write(context: Context, uri: Uri): Boolean = runCatching {
        context.contentResolver.openOutputStream(uri, "wt")!!.use { it.write(build(context).toByteArray()) }
    }.isSuccess

    fun share(activity: Activity) {
        val send = Intent(Intent.ACTION_SEND).setType("text/plain")
            .putExtra(Intent.EXTRA_SUBJECT, "FLY8 support report")
            .putExtra(Intent.EXTRA_TEXT, build(activity))
        activity.startActivity(Intent.createChooser(send, "Share report"))
    }

    fun clearHistory() = LogCategory.entries.forEach { Fly8.log.clear(it) }
}

/** Drop-in buttons for the diagnostics / About screen. */
@Composable
fun ExportReportButton(onResult: (String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        onResult(if (SupportReport.write(context, uri)) "Report exported." else "Could not export. Use Copy report instead.")
    }
    TextButton({ launcher.launch(SupportReport.fileName()) }) { Text("Export report") }
}
