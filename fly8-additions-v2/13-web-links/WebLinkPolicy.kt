package com.fly8.clone.ui

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Web links opened by a clone that is running in Live view.
 *
 * FLY8 today (REA/JADX: ActivityManagerCommonProxy.LiveBrowser.redirectStart): when nothing
 * inside the clone's space can open an http(s) link or a Custom Tab, and the clone is on a
 * Live display, FLY8 swaps the launch for its own LiveBrowserActivity WebView. That WebView
 * runs in FLY8's process with ONE cookie jar for all clones, wipes cookies + web storage
 * whenever it starts "fresh", drops the clone's resultTo/requestCode, and has no pop-up
 * window support.
 *
 * EysClone (REA/JADX: MethodProxies.StartActivity): if resolveActivityInfo(intent) is null
 * inside the virtual space, it calls method.invoke(...) — Android opens the link in the
 * phone's real browser (Chrome etc.), with its saved cookies, Google sign-in, pop-ups and
 * Custom Tabs.
 *
 * This file gives FLY8 the EysClone behaviour as a user setting, plus a per-clone rule for
 * when the in-app browser may keep its cookies.
 */
object WebLinkPolicy {
    private fun prefs(c: Context) = c.getSharedPreferences("fly8_web", Context.MODE_PRIVATE)

    /** Setting: "Open clone web links in the phone's browser" (EysClone behaviour). Default off = FLY8 behaviour. */
    fun usePhoneBrowser(c: Context) = prefs(c).getBoolean("phone_browser", false)
    fun setUsePhoneBrowser(c: Context, on: Boolean) = prefs(c).edit().putBoolean("phone_browser", on).apply()

    /**
     * Open [url] in the phone's own browser on the main screen (display 0).
     * Returns false when no browser is installed.
     */
    fun openInPhoneBrowser(c: Context, url: String): Boolean {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (i.resolveActivity(c.packageManager) == null) {
            Toast.makeText(c, "Install a browser to open this page.", Toast.LENGTH_SHORT).show()
            return false
        }
        val opts = ActivityOptions.makeBasic().setLaunchDisplayId(0).toBundle()
        return runCatching { c.startActivity(i, opts) }.isSuccess
    }

    /**
     * Replacement for LiveBrowserActivity's `fresh` rule.
     * Today: fresh = pages.isEmpty() || another clone's page is open → cookies are wiped every
     * time the first page opens, so a captcha / email-check / "continue" step that opens a
     * second link after the first page was closed starts from nothing.
     * New rule: wipe only when the page belongs to a different clone than last time, so the
     * shared cookie jar never carries one clone's session into another, but the same clone
     * keeps its web session between links.
     */
    fun mustWipe(c: Context, userId: Int, otherCloneOpen: Boolean): Boolean {
        val last = prefs(c).getInt("last_user", Int.MIN_VALUE)
        prefs(c).edit().putInt("last_user", userId).apply()
        return otherCloneOpen || last != userId
    }
}

/*
 * Wiring in LiveBrowserActivity.onCreate (FLY8 source, Kotlin), right after the url/userId checks:
 *
 *     if (WebLinkPolicy.usePhoneBrowser(this)) {
 *         WebLinkPolicy.openInPhoneBrowser(this, url)
 *         finish(); return
 *     }
 *
 * and replace
 *     val fresh = pages.isEmpty() || otherClones.isNotEmpty()
 * with
 *     val fresh = WebLinkPolicy.mustWipe(this, userId, otherClones.isNotEmpty())
 *
 * Toolbar: next to "Close" add a button "Open in phone browser" →
 *     WebLinkPolicy.openInPhoneBrowser(this, web.url ?: url); finish()
 *
 * Settings (Sessions screen settings sheet): a switch "Open clone web links in phone browser"
 * bound to WebLinkPolicy.usePhoneBrowser / setUsePhoneBrowser, with the note:
 * "Pages open on the phone screen instead of inside Live view. Use this for sign-ups,
 *  captchas and email checks."
 */
