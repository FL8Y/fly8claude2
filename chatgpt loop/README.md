# PNLE Study Notes — HTML App Improvement Loop

This workspace now focuses on the single app artifact: [`PNLE Study Notes.html`](PNLE%20Study%20Notes.html). Preserve the original file as the baseline and improve it in small, testable passes.

## Start here
1. [App baseline and requirements](NURSE_GOD_SPEC.md) — scope, current observed behavior, and invariants.
2. [Operational agent instructions](SKILL.md) — rules for inspecting and editing the HTML.
3. [Iteration harness](LOOP_HARNESS.md) — finite inspect → plan → edit → test → log loop.
4. [Reusable loop prompt](LOOP_PROMPT.md) — copy-ready instructions for each session.
5. [Handoffs](HANDOFFS.md) — scoped review and implementation tasks.
6. [Validation matrix](VALIDATION_MATRIX.md) — acceptance checks for app behavior and data integrity.
7. [Iteration log](ITERATION_LOG.md) — append-only record of real edits and tests.

## Project rule
The HTML file is the source of truth. Keep each improvement focused on reliability, usability, accessibility, data integrity, or maintainability. Do not add speculative features or replace the app with a different stack without explicit approval.

## Current baseline
The supplied HTML was imported as UTF-8 text without intentional edits. It appears to be a self-contained single-page PNLE notes reader with search, subject/category filters, studied-state tracking via `localStorage`, a theme toggle, note-detail navigation, and copy-note behavior. These are initial static observations, not proof that runtime behavior works.

## Loop policy
Run up to five evidence-backed passes per session. Make a narrow change, run available checks, inspect the diff, and append results to the log. Never claim tests passed unless they were actually run. Stop when the next pass would be cosmetic/speculative, when two consecutive passes yield no material improvement, or when a blocker requires user input.

## Scope
Work only within `chatgpt loop/`. Do not edit other repository files, add external services, or introduce patient-identifiable data. Keep a backup or recoverable Git history before replacing the HTML.
