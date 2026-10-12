# Research index (branch ccr-10cd0087-enq2oi, 12 Oct 2026)

Start here in a new session. Nothing below needs the old container.

## FLY8 vs EysClone
- `docs/FLY8_vs_EysClone_REA_Report.docx`: full REA comparison and port guide (ordered 12 → 00).
- `fly8-additions-v2/`: corrected feature code; compiles against FLY8 1.0.0-beta01 classes with 0 errors. Start with `NOTES.md`.
  - `13-web-links/WebLinkPolicy.kt`: fresh-account sign-up fix (web links in Live view).
  - `14-join-modes/`: EysClone 1.4.15 joining modes (public low-player / private server), plus `ControlServer.patch`.
- `docs/FLY8_Fresh_Account_Bug.docx`: why sign-up fails in FLY8 and the fix.
- `docs/EysClone_1.4.15_Joining_Modes.docx`: the 1.4.15 changes and how to add them to FLY8.
- `docs/PATCH_QUEUE.md`: PQ-001 (fresh-account fix) queued for the next patch.
- Original v1 backup: branch `ccr-13987e0a-m0db90`, `backup-2026-10-12/`.

## Pokémon Lazarus (Roblox reference project)
- `research/lazarus/NOTES.md`: ROM layout, storyline summary, gyms, status and to-do list.
- `research/lazarus/data/`: species, moves, items, abilities, types, trainers, locations (JSON).
- `research/lazarus/scripts/`: decoder, extractor, and 454 original creature names (`names.py`).
- Next: label the extra forms, rename branded terms, export Roblox Luau and JSON, write the .docx. Re-upload the 7z first.

## Tool setup (to redo in a fresh container)
- REA CLI: `npm i -g rea-agents` (6.4.0). Skill: `readthischatgpt/.claude/skills/rea`.
- JADX engine for REA: build `github.com/1013503897/jadx-headless-mcp` at commit 5844800a with Gradle,
  switching `jvmToolchain(17)` to 21. Set `REA_JADX_MCP_JAR`.
- apktool 2.12.1 from Maven (needs Google Maven for smali). Set `REA_APKTOOL_COMMAND`.
- pwntools 4.15 venv. Set `REA_PWNTOOLS_PYTHON`.
- Ghidra downloads are blocked by the network policy (`github.com` release assets).
