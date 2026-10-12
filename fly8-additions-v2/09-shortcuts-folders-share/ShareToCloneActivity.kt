package com.fly8.clone.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance
import com.fly8.clone.data.SpaceKind
import top.niunaijun.blackbox.BlackBoxCore

/**
 * "Share to app in FLY8": appears in Android's share sheet. The user picks a clone and the
 * shared text or file goes to that cloned app.
 *
 * Modelled on EysClone's ShareBridgeActivity (REA/JADX): it accepts only ACTION_SEND and
 * builds a NEW intent carrying only EXTRA_TEXT, EXTRA_SUBJECT and a content:// EXTRA_STREAM
 * (+ ClipData "Shared item"), instead of forwarding the sender's whole intent. That keeps
 * a malicious sender from smuggling extras or a component into the clone.
 *
 * Only FLY8-engine clones (SpaceKind.VIRTUAL) can receive: they run inside FLY8's own uid,
 * so the read grant FLY8 receives for the shared URI also covers them. Android-profile
 * clones are separate Android users and are skipped.
 */
class ShareToCloneActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val src = intent
        if (src?.action != Intent.ACTION_SEND) { finish(); return }
        val out = cleanIntent(src) ?: run { finish(); return }
        val candidates = Fly8.store.items.value.filter { it.space == SpaceKind.VIRTUAL && canReceive(it, out) }
        if (candidates.isEmpty()) {
            Toast.makeText(this, "No clone can open this item.", Toast.LENGTH_SHORT).show()
            finish(); return
        }
        AlertDialog.Builder(this)
            .setTitle("Share to app in FLY8")
            .setItems(candidates.map { it.title }.toTypedArray()) { _, which -> deliver(candidates[which], out) }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun cleanIntent(src: Intent): Intent? {
        val out = Intent(Intent.ACTION_SEND).setType(src.type ?: "text/plain")
        src.getCharSequenceExtra(Intent.EXTRA_TEXT)?.let { out.putExtra(Intent.EXTRA_TEXT, it) }
        src.getStringExtra(Intent.EXTRA_SUBJECT)?.let { out.putExtra(Intent.EXTRA_SUBJECT, it) }
        @Suppress("DEPRECATION") // typed overload is API 33+
        val uri = src.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        if (uri != null && uri.scheme == "content") {
            out.putExtra(Intent.EXTRA_STREAM, uri)
            out.clipData = ClipData.newRawUri("Shared item", uri)
            out.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return if (out.extras == null) null else out
    }

    private fun canReceive(i: Instance, probe: Intent): Boolean = runCatching {
        BlackBoxCore.getBPackageManager().resolveActivity(Intent(probe).setPackage(i.packageName), 0, probe.type, i.userId) != null
    }.getOrDefault(false)

    private fun deliver(target: Instance, base: Intent) {
        val out = Intent(base).setPackage(target.packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val ok = runCatching { BlackBoxCore.get().startActivity(out, target.userId) }.isSuccess
        if (!ok) Toast.makeText(this, "Share failed, please try again.", Toast.LENGTH_SHORT).show()
        finish()
    }
}
