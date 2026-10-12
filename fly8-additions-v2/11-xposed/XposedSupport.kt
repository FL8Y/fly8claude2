package com.fly8.clone.xposed

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import java.io.File

/**
 * Xposed module support inside clones (EysClone: bundled XposedInstaller + Epic/Exposed
 * hooking engine + "Manage Xposed Module" screen; FLY8: none).
 *
 * Approach for FLY8: use the open-source Pine hooking framework, which includes an
 * Xposed-API compatibility layer, instead of shipping a modified Xposed Installer.
 *
 *   dependencies {
 *       implementation("top.canyie.pine:core:0.3.0")
 *       implementation("top.canyie.pine:xposed:0.2.0")   // XposedBridge API on top of Pine
 *   }
 *   (Maven Central today: top.canyie.pine:core 0.3.0, top.canyie.pine:xposed 0.2.0;
 *    arm/arm64/x86/x86_64)
 *
 * Flow:
 *  1. Host: user installs a module APK into a clone space (it is just an app) and ticks it in
 *     "Manage Xposed modules"; ModuleRegistry stores (userId -> list of module APK paths).
 *  2. Guest: FLY8's engine already has the right hook point. In
 *     top.niunaijun.blackbox.app.BActivityThread.handleBindApplication (FLY8 source, JADX line ~414)
 *     the engine calls onBeforeCreateApplication(packageName, processName, packageContext)
 *     right BEFORE LoadedApk.makeApplication(...). Call XposedSupport.onGuestBind(...) from
 *     onBeforeCreateApplication with packageContext.classLoader and applicationInfo. It loads
 *     each enabled module once per process and dispatches handleLoadPackage.
 *     FLY8's engine already reserves a config file for this: BEnvironment.getXPModuleConf()
 *     (system/xposed-module.conf), and NativeCore.hideXposed() exists; nothing loads modules yet.
 *
 * Warning shown in UI (same as EysClone): "Xposed may be unstable on Android 14+. Enable at your own risk."
 */
object XposedSupport {
    const val WARNING = "Xposed may be unstable on Android 14+. Enable at your own risk."
    @Volatile private var loaded = false

    /** Guest side. Pine classes are referenced reflectively so FLY8 builds without the dependency. */
    fun onGuestBind(
        packageName: String,
        processName: String,
        appInfo: ApplicationInfo,
        classLoader: ClassLoader,
        enabledModules: List<File>,
        isFirstApp: Boolean,
    ) {
        if (enabledModules.isEmpty()) return
        val pineXposed = runCatching { Class.forName("top.canyie.pine.xposed.PineXposed") }.getOrNull() ?: return
        if (!loaded) {
            val load = pineXposed.getMethod("loadModule", File::class.java)
            enabledModules.filter { it.isFile }.forEach { apk -> runCatching { load.invoke(null, apk) } }
            loaded = true
        }
        runCatching {
            pineXposed.getMethod(
                "onPackageLoad", String::class.java, String::class.java, ApplicationInfo::class.java,
                Boolean::class.javaPrimitiveType, ClassLoader::class.java,
            ).invoke(null, packageName, processName, appInfo, isFirstApp, classLoader)
        }
    }

    fun unstableHere() = Build.VERSION.SDK_INT >= 34
}

/** Host-side registry of enabled modules per clone space. */
class ModuleRegistry(context: Context) {
    private val prefs = context.getSharedPreferences("fly8_xposed", Context.MODE_PRIVATE)
    fun enabled(userId: Int): Set<String> = prefs.getStringSet("u$userId", emptySet()) ?: emptySet()
    fun setEnabled(userId: Int, modulePackage: String, on: Boolean) {
        val s = enabled(userId).toMutableSet().apply { if (on) add(modulePackage) else remove(modulePackage) }
        prefs.edit().putStringSet("u$userId", s).apply()
    }
    /** A package is an Xposed module when its manifest has meta-data "xposedmodule" = true. */
    fun isModule(info: ApplicationInfo) = info.metaData?.getBoolean("xposedmodule", false) == true
}
