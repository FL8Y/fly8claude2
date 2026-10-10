# Recovery bundle notes

This folder documents the locally prepared recovery archive. The complete archive is currently attached in the ChatGPT conversation as `FLY8_Prototype_Recovery.zip`; the GitHub connector available for this operation only supports UTF-8 text-file writes, so it cannot upload that 15 MB ZIP or the DOCX as binary assets directly.

## Package contents
- `01_original/`: exact APK copy
- `02_apk_extracted/`: all entries extracted from the APK ZIP container
- `03_fly8_relevant/`: selected manifest/DEX/resources/legal notices/native libraries
- `04_analysis/`: inventory, hashes, string dumps, and decompilation status
- `05_blueprint/`: reconstruction blueprint DOCX

## Limitations
This is static APK extraction, not full source decompilation. The DEX is preserved but no readable Kotlin/Java source was generated. Use JADX/apktool in a trusted environment for the next pass.

## Guardrails
- Rebuild the useful behaviors in FLY8; do not bundle the old APK into the new app.
- No activation keys, license gates, device-slot server, or mandatory licensing backend.
- Audit VirtualApp/Xposed-related dependencies and licenses before reuse.
- Keep local state durable and offline-capable; don't silently delete records when a bridge/server is unavailable.
- Verify feature behavior on device; do not claim full 1:1 parity from strings alone.
