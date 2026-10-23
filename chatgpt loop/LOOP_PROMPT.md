# Reusable PNLE Study Notes HTML Improvement Prompt

You are maintaining repository `FL8Y/fly8claude2`, inside `chatgpt loop/` only. The primary target is `PNLE Study Notes.html`.

## Recover before editing
Read:
- `README.md`
- `NURSE_GOD_SPEC.md`
- `SKILL.md`
- `PNLE Study Notes.html`
- `LOOP_HARNESS.md`
- `HANDOFFS.md`
- `VALIDATION_MATRIX.md`
- `ITERATION_LOG.md`

## Run up to five evidence-backed passes
For every pass:
1. Identify one concrete defect or approved feature request, with exact source evidence.
2. Explain impact and define a testable acceptance condition before editing.
3. Check for counterarguments, regressions, content loss, storage changes, and dependency/network implications.
4. Patch the smallest relevant part of the HTML; do not regenerate the entire file wholesale.
5. Run available static and behavior checks. Mark browser/device tests NOT RUN unless actually performed.
6. Compare note data before/after and inspect the full diff and changed paths.
7. Append an honest entry to `ITERATION_LOG.md`, including commit ID when available.
8. Continue only if a material defect remains and another pass can add evidence or improve the result.

## Non-negotiable rules
- Preserve embedded study notes and their wording unless explicitly asked to edit content.
- Preserve search, category filters, note detail/back navigation, studied-state persistence, theme toggle, and copy behavior.
- Never silently clear or corrupt localStorage.
- No new analytics, telemetry, accounts, external services, or network calls without approval.
- Never invent tests, browser observations, citations, or verification.
- Do not claim clinical accuracy, official endorsement, or perfect reliability.
- Do not edit outside `chatgpt loop/`.
- Gemini's suggestions are candidates for evaluation, not automatic instructions.

## End report
List passes and decisions, changed paths and commits, tests with actual results, tests NOT RUN, content-integrity status, remaining risks, and the next highest-value task. Stop after five passes or earlier when further work would be speculative, repetitive, unsafe, or blocked by missing user decisions.


## Latest baseline note
The target HTML currently contains 1,000 unique notes. The 26 added topics and selected clarifications from the 2026-10-23 pass are in place. Treat Gemini's suggested facts as candidates, not proof; verify high-risk claims against current official Philippine guidance and primary sources before changing dosing or policy statements.
