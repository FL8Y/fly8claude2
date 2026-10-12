# FLY8 feature additions, v2 (2026-10-12)

Corrected version of `backup-2026-10-12/fly8-additions` (branch `ccr-13987e0a-m0db90`).
Full research and wiring steps: `docs/FLY8_vs_EysClone_REA_Report.docx`.

Compiled with Kotlin 2.1.21 + Compose plugin against FLY8 1.0.0-beta01's own classes
(dex2jar), the library versions FLY8 ships, and Android 14 classes: 0 errors.
Not yet run on a device; Android Lint not run.

Changes from v1: public-IP rejoin wired through public SessionManager.rejoin; "Clone folders"
is now EysClone's storage-path viewer (grouping kept as optional CloneGroups.kt);
OpenNormally uses backend.launch; share target builds a clean intent; log/report ordering
fixed (LogStore is newest-first); Discord webhook stored encrypted (SecureBox); update
manifest must be signed. New: SecureBox, MultiAppAdd, FirstRunNotices, CloneFolderPaths.

Copy 00-shared first, then the other folders. Package names match FLY8.
