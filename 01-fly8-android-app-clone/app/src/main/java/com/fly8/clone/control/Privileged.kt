package com.fly8.clone.control

import android.content.pm.PackageManager
import android.os.ParcelFileDescriptor
import moe.shizuku.server.IShizukuService
import rikka.shizuku.Shizuku
import java.util.concurrent.TimeUnit

/** One-shot launchers for the optional Root and Shizuku paths. */
object Privileged {
    fun runAsRoot(command: String): ExecResult = try {
        val p = ProcessBuilder("su", "-c", command).redirectErrorStream(true).start()
        val out = StringBuilder()
        val t = Thread { p.inputStream.bufferedReader().forEachLine { out.appendLine(it) } }.apply { start() }
        if (!p.waitFor(20, TimeUnit.SECONDS)) {
            p.destroyForcibly(); ExecResult(124, "Root request timed out. Approve FLY8 in your root manager.")
        } else { t.join(500); ExecResult(p.exitValue(), out.toString().trim()) }
    } catch (e: Exception) {
        ExecResult(-1, "Root access is unavailable on this device (${e.javaClass.simpleName}).")
    }

    fun shizukuStatus(): String? = try {
        when {
            !Shizuku.pingBinder() -> "Shizuku is not running. Start it in the Shizuku app first."
            Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED -> "permission"
            else -> null
        }
    } catch (t: Throwable) { "Shizuku is not installed." }

    fun runWithShizuku(command: String): ExecResult = try {
        val service = IShizukuService.Stub.asInterface(Shizuku.getBinder())
        val remote = service.newProcess(arrayOf("sh", "-c", command), null, null)
        val out = ParcelFileDescriptor.AutoCloseInputStream(remote.inputStream).bufferedReader().readText()
        ExecResult(remote.waitFor(), out.trim())
    } catch (t: Throwable) {
        ExecResult(-1, "Shizuku command failed: ${t.message ?: t.javaClass.simpleName}")
    }
}
