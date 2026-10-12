package com.fly8.clone.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * "Check for updates" + optional "required update" gate (EysClone has both; FLY8 has neither).
 *
 * Source: a small JSON file YOU host (GitHub release asset, GitHub Pages, any static host):
 *   {
 *     "latest": "1.1.0", "latest_code": 12,
 *     "minimum_code": 9,                       // below this the app shows a blocking screen
 *     "url": "https://github.com/<you>/<repo>/releases/latest",
 *     "notes": "Fixes Roblox typing in Live view"
 *   }
 * The check only runs when the user taps the button (or once a day if you enable it).
 */
object UpdateChecker {
    /** Change to your own manifest URL. */
    const val MANIFEST_URL = "https://example.invalid/fly8/update.json"

    data class Info(val latest: String, val latestCode: Long, val minimumCode: Long, val url: String, val notes: String)

    sealed interface Result {
        data class UpToDate(val installed: String) : Result
        data class Available(val info: Info, val required: Boolean) : Result
        data class Failed(val reason: String) : Result
    }

    // PackageInfoFlags is API 33+; the int overload works on every supported version.
    @Suppress("DEPRECATION")
    fun installedCode(context: Context): Long =
        context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode

    @Suppress("DEPRECATION")
    fun installedName(context: Context): String =
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"

    suspend fun check(context: Context, manifestUrl: String = MANIFEST_URL): Result = withContext(Dispatchers.IO) {
        try {
            val c = URL(manifestUrl).openConnection() as HttpURLConnection
            c.connectTimeout = 8_000; c.readTimeout = 8_000
            val code = c.responseCode
            if (code == 429) return@withContext Result.Failed("Update checks are temporarily limited. Please try again later.")
            if (code !in 200..299) return@withContext Result.Failed("Update service could not be reached (HTTP $code)")
            val j = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            c.disconnect()
            val info = Info(
                latest = j.getString("latest"),
                latestCode = j.getLong("latest_code"),
                minimumCode = j.optLong("minimum_code", 0),
                url = j.getString("url"),
                notes = j.optString("notes"),
            )
            require(info.url.startsWith("https://")) { "Use an https download location" }
            val mine = installedCode(context)
            when {
                mine < info.minimumCode -> Result.Available(info, required = true)
                mine < info.latestCode -> Result.Available(info, required = false)
                else -> Result.UpToDate(installedName(context))
            }
        } catch (t: Throwable) {
            Result.Failed("Update service could not be reached (${t.javaClass.simpleName})")
        }
    }

    fun openDownload(context: Context, info: Info) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(info.url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (i.resolveActivity(context.packageManager) != null) context.startActivity(i)
    }
}

/*
 * UI (AboutScreen): add a row "Check for updates" that calls UpdateChecker.check(context)
 * and shows: "Latest version is installed." | "New version: X — Download update" | the error.
 * Required update: in MainActivity, if Result.Available(required = true), show a full-screen
 * card "Update FLY8 before adding instances" with only the Download button.
 * Tip shown in the dialog: install the update over the existing app (do not uninstall) to keep clones.
 */
