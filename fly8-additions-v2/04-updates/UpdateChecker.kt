package com.fly8.clone.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyFactory
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

/**
 * "Check for updates" + "Update required" gate (EysClone has both; FLY8 has neither).
 *
 * EysClone (class yb, update path) does not trust the JSON it downloads. It requires:
 *   { "keyId": "...", "payload": <base64url JSON>, "signature": <base64url, 86 chars> }
 * and verifies SHA256withECDSA over the payload string with a P-256 public key built into
 * the APK, then checks sequence (no rollback), issuedAt/expiresAt (no stale copies) and an
 * allow-list of download URLs. This class does the same, so a hijacked host or a
 * man-in-the-middle cannot push a fake "required update" link.
 *
 * Payload JSON (before signing):
 *   { "latest": "1.1.0", "latestCode": 12, "minimumCode": 9,
 *     "url": "https://github.com/<you>/<repo>/releases/latest",
 *     "notes": "Fixes Roblox typing in Live view",
 *     "sequence": 3, "issuedAt": 1760000000, "expiresAt": 1767000000 }
 *
 * Make a key pair once (keep private.pem OFF the phone and out of git):
 *   openssl ecparam -name prime256v1 -genkey -noout -out private.pem
 *   openssl ec -in private.pem -pubout -outform DER | base64 -w0      -> PUBLIC_KEY below
 * Sign each release (payload.json is the JSON above, minified):
 *   P=$(base64 -w0 payload.json | tr '+/' '-_' | tr -d '=')
 *   printf %s "$P" > p.txt
 *   S=$(openssl dgst -sha256 -sign private.pem p.txt | base64 -w0 | tr '+/' '-_' | tr -d '=')
 *   printf '{"keyId":"fly8-update-1","payload":"%s","signature":"%s"}' "$P" "$S" > update.json
 *   openssl writes a DER signature, so RAW_SIGNATURE = false. (EysClone stores raw r||s,
 *   64 bytes / 86 base64url chars; set RAW_SIGNATURE = true if you produce that format.)
 */
object UpdateChecker {
    /** Your own signed manifest location (GitHub raw URL, Pages, any static host). */
    const val MANIFEST_URL = "https://example.invalid/fly8/update.json"
    const val KEY_ID = "fly8-update-1"
    /** Base64 (standard) X.509 SubjectPublicKeyInfo of your P-256 key. */
    const val PUBLIC_KEY = "REPLACE_WITH_YOUR_PUBLIC_KEY"
    private const val RAW_SIGNATURE = false
    private val ALLOWED_DOWNLOADS = listOf("https://github.com/")
    private const val MAX_BYTES = 16 * 1024

    data class Info(val latest: String, val latestCode: Long, val minimumCode: Long, val url: String, val notes: String)

    sealed interface Result {
        data class UpToDate(val installed: String) : Result
        data class Available(val info: Info, val required: Boolean) : Result
        data class Failed(val reason: String) : Result
    }

    @Suppress("DEPRECATION")
    fun installedCode(context: Context): Long = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode

    @Suppress("DEPRECATION")
    fun installedName(context: Context): String = context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "?"

    suspend fun check(context: Context, manifestUrl: String = MANIFEST_URL): Result = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences("fly8_update", Context.MODE_PRIVATE)
        try {
            val c = URL(manifestUrl).openConnection() as HttpURLConnection
            c.connectTimeout = 8_000; c.readTimeout = 8_000
            val body = try {
                val code = c.responseCode
                if (code == 429) return@withContext Result.Failed("Update checks are temporarily limited. Please try again later.")
                if (code !in 200..299) return@withContext Result.Failed("Update service could not be reached (HTTP $code)")
                // InputStream.readNBytes is API 33+; FLY8 supports API 30+, so read manually.
                val buf = ByteArray(MAX_BYTES + 1)
                var n = 0
                c.inputStream.use { s -> while (n < buf.size) { val r = s.read(buf, n, buf.size - n); if (r < 0) break; n += r } }
                val bytes = buf.copyOf(n)
                if (bytes.size > MAX_BYTES) return@withContext Result.Failed("Update information is too large.")
                String(bytes)
            } finally { c.disconnect() }

            val env = JSONObject(body)
            if (env.optString("keyId") != KEY_ID) return@withContext Result.Failed("Update information could not be verified.")
            val payloadB64 = env.getString("payload")
            val sigB64 = env.getString("signature")
            if (!payloadB64.matches(Regex("[A-Za-z0-9_-]+"))) return@withContext Result.Failed("Update information could not be verified.")
            val flags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            val sig = Base64.decode(sigB64, flags)
            val ok = Signature.getInstance("SHA256withECDSA").run {
                initVerify(KeyFactory.getInstance("EC").generatePublic(X509EncodedKeySpec(Base64.decode(PUBLIC_KEY, Base64.DEFAULT))))
                update(payloadB64.toByteArray())
                verify(if (RAW_SIGNATURE) rawToDer(sig) else sig)
            }
            if (!ok) return@withContext Result.Failed("Update information could not be verified.")

            val p = JSONObject(String(Base64.decode(payloadB64, flags)))
            val now = System.currentTimeMillis() / 1000
            val seq = p.getLong("sequence")
            if (seq < prefs.getLong("seq", 0)) return@withContext Result.Failed("The published update information is older than the saved policy.")
            if (p.getLong("issuedAt") > now + 600) return@withContext Result.Failed("Check your device's automatic date and time, then retry.")
            if (p.getLong("expiresAt") < now) return@withContext Result.Failed("The update information has expired, or your device's date is incorrect.")
            val info = Info(p.getString("latest"), p.getLong("latestCode"), p.optLong("minimumCode", 0), p.getString("url"), p.optString("notes"))
            if (info.minimumCode > info.latestCode) return@withContext Result.Failed("Minimum supported version exceeds latest version")
            if (ALLOWED_DOWNLOADS.none { info.url.startsWith(it) }) return@withContext Result.Failed("Use the official FLY8 download location")
            prefs.edit().putLong("seq", seq).putLong("min", info.minimumCode).apply()

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

    /** Last verified minimum, so the "Update required" gate also works offline. */
    fun requiredOffline(context: Context): Boolean =
        installedCode(context) < context.getSharedPreferences("fly8_update", Context.MODE_PRIVATE).getLong("min", 0)

    fun openDownload(context: Context, info: Info) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse(info.url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (i.resolveActivity(context.packageManager) != null) context.startActivity(i)
    }

    /** 64-byte r||s -> ASN.1 DER SEQUENCE{INTEGER r, INTEGER s} for java.security.Signature. */
    private fun rawToDer(raw: ByteArray): ByteArray {
        require(raw.size == 64) { "bad signature" }
        fun int(b: ByteArray): ByteArray {
            var v = b.dropWhile { it == 0.toByte() }.toByteArray()
            if (v.isEmpty()) v = byteArrayOf(0)
            if (v[0] < 0) v = byteArrayOf(0) + v
            return byteArrayOf(0x02, v.size.toByte()) + v
        }
        val body = int(raw.copyOfRange(0, 32)) + int(raw.copyOfRange(32, 64))
        return byteArrayOf(0x30, body.size.toByte()) + body
    }
}

/*
 * UI (AboutScreen): add a row "Check for updates" that calls UpdateChecker.check(context)
 * and shows: "You're up to date" | "New version X — Download update" | the error text.
 * Required update: in MainActivity, if Result.Available(required = true) or
 * UpdateChecker.requiredOffline(context), show a full-screen card
 * "Update FLY8 before adding instances" with only the Download button.
 * Tip in the dialog: install the update over the existing app (do not uninstall) to keep clones.
 */
