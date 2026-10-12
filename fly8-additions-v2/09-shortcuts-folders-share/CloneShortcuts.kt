package com.fly8.clone.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.Bitmap
import android.graphics.drawable.Icon
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import com.fly8.clone.Fly8
import com.fly8.clone.data.Instance
import com.fly8.clone.session.startFromShortcut

/**
 * "Create shortcut" for a clone (EysClone has it; FLY8 does not).
 * The home-screen icon opens ShortcutLaunchActivity, which starts that instance.
 *
 * Manifest entry: see 12-manifest/AndroidManifest-additions.xml.
 */
object CloneShortcuts {
    const val EXTRA_INSTANCE = "fly8.instance"

    fun create(context: Context, instance: Instance): String {
        val sm = context.getSystemService(ShortcutManager::class.java)
        if (sm == null || !sm.isRequestPinShortcutSupported) return "Unable to create shortcut"
        if (sm.pinnedShortcuts.any { it.id == "clone-${instance.id}" }) return "This shortcut already exists"
        val icon: Bitmap = runCatching {
            context.packageManager.getApplicationIcon(instance.packageName).toBitmap(192, 192)
        }.getOrElse { context.packageManager.getApplicationIcon(context.packageName).toBitmap(192, 192) }
        val launch = Intent(context, ShortcutLaunchActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .putExtra(EXTRA_INSTANCE, instance.id)
        val info = ShortcutInfo.Builder(context, "clone-${instance.id}")
            .setShortLabel("${instance.label} ${instance.number}".take(24))
            .setLongLabel(instance.title)
            .setIcon(Icon.createWithBitmap(icon))
            .setIntent(launch)
            .build()
        return if (sm.requestPinShortcut(info, null)) "Shortcut created!" else "Unable to create shortcut"
    }
}

class ShortcutLaunchActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra(CloneShortcuts.EXTRA_INSTANCE)
        val inst = id?.let { Fly8.store.get(it) }
        if (inst != null) {
            // Same entry point the Sessions screen uses to start an instance.
            Fly8.sessions.startFromShortcut(inst)
        }
        finish()
    }
}

// startFromShortcut() is in 00-shared/SessionExtensions.kt.
