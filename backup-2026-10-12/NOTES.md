# FLY8 feature additions: backup (2026-10-12)

Kotlin/Java code that adds EysClone-style features to FLY8. It compiles against
FLY8 1.0.0-beta01's real classes (Kotlin 2.1 + Compose, Android API 34) with 0 errors.
It has not been run on a device yet.

| Folder | Feature |
|---|---|
| 00-shared | SessionManager helper extensions |
| 02-discord-alerts | Discord webhook alerts + settings dialog |
| 03-recovery | Public-IP-change rejoin, recovery notifications |
| 04-updates | Update checker / required-update gate |
| 05-logs | Log dialog with instance/event filters, clear logs |
| 06-live-view | Fit/Fill/rotate preview, Live menu, Retry preview, refresh presets, Open normally |
| 07-diagnostics | Export/share support report, clear history |
| 08-help | How to use / Terms / Privacy pages |
| 09-shortcuts-folders-share | Home-screen shortcuts, clone folders, Share to clone |
| 10-build | Android 10 (minSdk 29) + x86_64 build changes |
| 11-xposed | Xposed module support skeleton (Pine) |
| 12-manifest | Manifest entries for the new activities |

## Next steps
1. Copy each folder's files into FLY8's source tree (package names already match).
2. Apply the wiring notes at the bottom of each file (LogStore.add hooks, manifest entries).
3. Build, run Android Lint (NewApi), then test each feature on a phone.
4. Point UpdateChecker.MANIFEST_URL at your own update.json.
