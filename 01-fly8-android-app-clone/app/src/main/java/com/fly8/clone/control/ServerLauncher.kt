package com.fly8.clone.control

import android.content.Context
import com.fly8.clone.server.ControlServer
import java.io.File
import java.security.SecureRandom

/** Builds the fixed, reviewed command that starts the control server under shell/root identity. */
object ServerLauncher {
    fun token(context: Context): String {
        val f = File(context.filesDir, "ctl_token")
        f.takeIf { it.exists() }?.readText()?.trim()?.takeIf { it.length >= 32 }?.let { return it }
        val bytes = ByteArray(24).also { SecureRandom().nextBytes(it) }
        val t = bytes.joinToString("") { "%02x".format(it) }
        f.writeText(t)
        return t
    }

    /** Command used from inside FLY8 (ADB stream, su, Shizuku). */
    fun command(context: Context): String {
        val apk = context.applicationInfo.sourceDir
        return launch("'$apk'", token(context))
    }

    private fun launch(apkExpr: String, token: String) =
        "CLASSPATH=$apkExpr nohup app_process /system/bin --nice-name=fly8_ctl " +
            "${ControlServer::class.java.name} $token >/dev/null 2>&1 &"

    /** Script for the computer activation path; written to FLY8's external files directory. */
    fun writeActivationScript(context: Context): File? {
        val dir = context.getExternalFilesDir(null) ?: return null
        val script = File(dir, "start.sh")
        script.writeText(
            "#!/system/bin/sh\n" +
                "apk=\$(pm path ${context.packageName} | head -n 1 | cut -d: -f2)\n" +
                launch("\"\$apk\"", token(context)) + "\n" +
                "echo 'FLY8 controls started. Return to FLY8.'\n"
        )
        return script
    }

    fun activationCommand(context: Context): String =
        "adb shell sh /sdcard/Android/data/${context.packageName}/files/start.sh"
}
