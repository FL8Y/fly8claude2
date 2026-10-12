package com.fly8.clone.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance
import top.niunaijun.blackbox.BlackBoxCore

/**
 * "Share to App in FLY8": appears in Android's share sheet. The user picks a clone and
 * the shared text/file is delivered to that cloned app (EysClone: ShareBridgeActivity /
 * ListAppActivity with ACTION_SEND for any type; FLY8 only handles VIEW/INSTALL of APKs).
 *
 * Manifest entry: see 12-manifest/AndroidManifest-additions.xml.
 */
class ShareToCloneActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val incoming = intent
        val candidates = Fly8.store.items.value.filter { canReceive(it, incoming) }
        if (candidates.isEmpty()) { finish(); return }
        AlertDialog.Builder(this)
            .setTitle("Share to app in FLY8")
            .setItems(candidates.map { it.title }.toTypedArray()) { _, which -> deliver(candidates[which], incoming) }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun canReceive(i: Instance, src: Intent): Boolean = runCatching {
        val probe = Intent(src.action).setType(src.type).setPackage(i.packageName)
        BlackBoxCore.getBPackageManager().resolveActivity(probe, 0, src.type, i.userId) != null
    }.getOrDefault(false)

    private fun deliver(target: Instance, src: Intent) {
        val out = Intent(src).setComponent(null).setPackage(target.packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        // Files arrive as content:// URIs owned by the sender; grant read so the clone can open them.
        out.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        @Suppress("DEPRECATION") // typed overload is API 33+; FLY8 supports API 29/30+
        (src.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let { grantUriPermission(packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
        val ok = runCatching { BlackBoxCore.get().startActivity(out, target.userId) }.isSuccess
        if (!ok) android.widget.Toast.makeText(this, "Share failed, please try again.", android.widget.Toast.LENGTH_SHORT).show()
        finish()
    }
}
