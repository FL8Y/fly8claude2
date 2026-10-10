# FLY8 Prototype Recovery Workspace

This repository is the working drop for reconstructing the FLY8 prototype into the independently buildable FLY8 app.

## Start here
- `docs/FLY8_Prototype_Reconstruction_Blueprint.docx` — reconstruction contract and acceptance checklist.
- `recovery/README_FIRST.txt` — inventory, extraction limits, and next steps from the recovery package.

## Important status
The APK is compiled output, not the original Android Studio project. The recovery bundle is a static extraction and inventory, **not a full JADX source decompilation**. Do not describe it as recovered Kotlin/Java source.

## Product requirements
- Work in this repository; audit before editing.
- No activation key, licensing screen, device-slot server, or mandatory licensing backend.
- Keep local configuration durable and usable offline.
- Preserve the existing backend seam and verify whether native Android profiles can meet the required prototype behavior before choosing a virtualization engine.
- Keep wireless debugging as the normal path; Shizuku/Root remain explicit optional methods.
- Respect third-party licenses; do not blindly ship the embedded Xposed installer or compiled libraries.

## Recovery files
The original APK is intentionally not committed in this initial workspace. Add it on a separate branch if desired, and avoid committing signing secrets, private keys, or user data.