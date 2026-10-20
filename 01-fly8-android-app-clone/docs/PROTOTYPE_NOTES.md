# Prototype reference notes (FLY8 Cloner 1.3.76)

Static-analysis notes on the beta prototype APK, supplied by the owner. These are reference
evidence for behaviour, not source to copy. Items marked *(review)* are interpretations that
still need confirming from a decompile or on a device.

## Identity
- Package `com.fly8.cloner`, version 1.3.76. Earlier reference build: 1.3.59 (EysClone).
- minSdk ~21, targetSdk 34, compileSdk 34.
- Observed running on an Android 12 cloud phone (Redfinger) and on an Android 16 tablet.

## Architecture: in-process virtualization (VirtualApp / VirtualXposed lineage)
- `com.lody.virtual.*` and `io.virtualapp.*` packages.
- Cloned apps are **not** installed on the system. They run as guest processes inside the host
  app's own UID, using APK and resources shared with the real install plus per-instance data
  directories. This is why clones are lightweight and why there is no Android user limit
  (unlike native clone profiles or secondary users).
- Native libraries (arm64-v8a, armeabi-v7a, x86_64):
  - `libva++.so`: VirtualApp core (syscall/IO redirection, native hooks).
  - `libepic.so`: Epic ART method-hook framework (tiann/epic).
  - `libspake2.so`: SPAKE2 for Wireless debugging (ADB TLS) pairing via LibADB *(review: the
    credits list LibADB + SPAKE2 together, which points at ADB pairing rather than daemon IPC)*.
- Stub components declared in the manifest: `StubActivity$C0..C49`, `StubDialog$C0..C49`,
  `StubCP$C0..C49`, and so on. Guest activities, dialogs and providers are redirected onto these
  pre-declared slots, giving roughly 50 concurrent guest process slots.
- `BinderProvider` (`com.lody.virtual.server` and `com.fly8.cloner.virtual.service`) hosts the
  virtual system services (virtual PackageManager, ActivityManager and so on) over Binder IPC.
- `DaemonService` and `DaemonJobService` keep the host, and therefore every guest, alive.
- Bundled `assets/XposedInstaller_3.1.5.apk_` (purpose and licence still to be reviewed).

## Screen-off / AFK behaviour (owner observation)
Clones keep running with the physical screen off, similar in spirit to a "virtual master" style
manager. Consistent with the architecture above:
- guests live inside the host process, which a daemon/foreground service keeps alive;
- the Live view / previews indicate guest UI is rendered off the main screen (virtual displays),
  so it does not depend on the physical display being on *(review)*;
- battery-optimisation exemption and background allowances keep Doze from suspending it.

## Permissions
About 80 declared, mostly forwarded to guest apps (location, camera, mic, contacts, SMS, call log,
storage), plus host needs: `SYSTEM_ALERT_WINDOW`, `PACKAGE_USAGE_STATS`,
`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`, `MANAGE_EXTERNAL_STORAGE`, `POST_NOTIFICATIONS`,
foreground-service types.

## Compatibility risks
- Android 12: background limits can kill the daemon; tighter hidden-API rules can break some hooks.
- Android 14–16: ART hooking (Epic) and hidden-API access are progressively restricted, so some
  guests may fail to launch. The owner reports the prototype does work on their Android 16
  tablet, so the shipped fork must carry modern-Android patches. Those patches are not public
  upstream VirtualApp.

## Consequences for FLY8
- Native profiles/secondary users cannot reproduce this. Secondary users do not render (the old
  FLY8 "Recents card but no UI" bug) and profile counts are capped ("Cannot add more users").
- Parity on instance count and lightweight clones requires an in-process virtualization engine,
  with its licence (VirtualApp: Apache-2.0 for the open-source version; Epic: Apache-2.0) and
  modern-Android compatibility verified first.
