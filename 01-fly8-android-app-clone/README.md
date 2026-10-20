# FLY8

A clean, local-first Android **app session manager / cloner**. FLY8 clones an installed app into an
isolated space, runs it in a Live session on its own virtual display (so it keeps running with the
screen off), and manages recovery, logs and background keep-alive — with the dark FLY8 theme and the
cyber-eye identity.

> Rebuilt from the prototype blueprint. No activation key, no license server, no remote device slot —
> the instance list lives on this device.

## Getting the APK

Every push builds a debug APK on GitHub Actions: open the latest **Build FLY8 APK** run and download
the **FLY8-debug-apk** artifact. It is signed with the public beta key in `keystore/`, so successive
betas install over each other without wiping local data.

## How it works

| Layer | What it does |
|-------|--------------|
| **Controls** (`control/`) | Connects the privileged control path the clone operations need: Wireless debugging (ADB-over-TLS pairing via LibADB/SPAKE2), Root, Shizuku, or computer activation. "Connected" means an authenticated control server actually answered — never just a tapped button. |
| **Control server** (`server/`) | A small `app_process` server started under the shell/root identity. It exposes a **fixed allowlist** of operations (create profile, install-existing, launch on display, stop, …) — no arbitrary shell — bound to `127.0.0.1` behind a per-install token, and forwards touch/keys only to FLY8's own virtual displays. |
| **Session engine** (`session/`) | `CloneBackend` seam with the shipping `NativeCloneBackend` (Android clone-profile / secondary-user primitives) and `LiveSession` (per-clone virtual display for the Live view + screen-off operation). A future in-process virtualization engine can implement the same seam for full prototype parity. |
| **Data** (`data/`) | Versioned, atomic local instance store (corruption is quarantined, never wiped), bounded 100-entry activity log with secret redaction, recovery/app settings, strict Roblox rejoin-target parsing. |
| **Recovery** (`recovery/`) | Alarm-scheduled restart/rejoin (not a busy loop); only restarts a confirmed-exited clone. |
| **UI** (`ui/`) | Jetpack Compose: Dashboard, Sessions grid with Live tiles, full interactive view + Kitchen Keyboard, Connect controls, Advanced recovery, About. |

## Honest scope

- The shipping backend uses Android's own clone profiles / secondary users. The number of instances
  depends on the device — some phones allow only one or two. The prototype's ~50 lightweight in-app
  copies need an in-process virtualization engine (VirtualApp lineage); that is a separate, larger
  effort tracked in `docs/PROTOTYPE_NOTES.md`, and the UI reports the real limit rather than claiming
  unlimited clones.
- Nothing fakes success: a clone is "running" only after its process is observed, and a black/no-preview
  tile over a live process stays **running**, matching the prototype's behaviour rather than being
  "fixed" into a fake screenshot.

## Build locally

```
ANDROID_HOME=/path/to/android-sdk ./gradlew :app:assembleDebug
```

Requires JDK 17 and Android SDK 36.
