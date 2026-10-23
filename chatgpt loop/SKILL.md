---
name: pnle-study-notes-html-improvement
version: 2.0.0
description: Bounded, test-driven maintenance of the PNLE Study Notes single-file HTML app.
---

# PNLE Study Notes — Operational Instructions

## Mission
Reliably improve `PNLE Study Notes.html` while preserving its embedded study content and existing user data. This is a single-file web app project, not a nursing-research-agent project.

## Required recovery
Before every edit, read:
- `README.md`
- `NURSE_GOD_SPEC.md` (the current app baseline/spec despite its legacy filename)
- `PNLE Study Notes.html`
- `LOOP_HARNESS.md`
- `VALIDATION_MATRIX.md`
- `ITERATION_LOG.md`

Inspect repository-level instructions for constraints, but do not edit outside `chatgpt loop/`.

## Invariants
1. Preserve all embedded notes and their wording unless the user explicitly requests content changes.
2. Keep the app usable on mobile screens and with touch input.
3. Preserve search, category filters, note opening/back navigation, studied-state persistence, theme toggle, and copy-note behavior unless a deliberate, documented change is approved.
4. Never silently clear or migrate `localStorage` data. Any storage schema change needs a migration plan and regression test.
5. Do not add remote dependencies, analytics, telemetry, accounts, or network calls without explicit approval.
6. Do not treat study notes as authoritative clinical advice; the app is for study and review.
7. Do not claim the app was run, tested, or clinically reviewed unless that actually happened.
8. Make the smallest coherent change and keep all edits within this directory.
9. Keep the source as a single HTML file unless the user authorizes a structural refactor.
10. Each material edit must be logged in `ITERATION_LOG.md` with exact paths, commit ID when available, tests actually run, tests not run, risks, and next step.

## Per-pass procedure
1. Inspect current HTML and git history/context available through the tools.
2. State one evidence-backed defect or measurable opportunity.
3. Define an acceptance check before editing.
4. Make a narrow patch; do not regenerate the entire HTML wholesale.
5. Validate syntax and behavior using available tools. If a browser is unavailable, clearly mark browser-only tests NOT RUN.
6. Compare before/after note data and confirm no unexpected deletions or content changes.
7. Inspect the final diff and verify scope.
8. Append the result to the iteration log.
9. Continue only if another pass can materially improve reliability or add new evidence; maximum five passes per session.

## Do not
- fabricate test results or browser observations;
- claim zero defects or perfect reliability;
- delete, summarize, reorder, or rewrite embedded notes just to make edits easier;
- use repeated reviews as a substitute for evidence;
- modify app files outside `chatgpt loop/`;
- claim clinical validation or official PNLE endorsement.


## Current project state
The current data target is 1,000 notes across 21 groups. The latest pass added 26 requested topics, updated selected high-impact study clarifications, normalized the `fer` and `burns` area tags, and enabled clickable HTTPS references. Continue auditing the actual HTML; do not assume every claim in the supplied Gemini audit is correct or that a linked reference verifies every sentence.
