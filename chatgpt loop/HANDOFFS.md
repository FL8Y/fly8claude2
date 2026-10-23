# App Review and Implementation Handoffs

All tasks are limited to `chatgpt loop/`. Handoff outputs are recommendations until the lead validates them.

## Universal packet
- Objective and user-visible outcome.
- Exact in-scope file(s) and current revision.
- Evidence-backed issue or candidate feature.
- Minimal proposed change.
- Acceptance criteria and reproducible test steps.
- Content-integrity checks required.
- Tests actually run and not run.
- Risks, dependencies, assumptions, and unresolved decisions.
- Stop condition.

## Handoff A — HTML reliability audit
Inspect parsing/rendering, event handlers, empty states, error handling, route transitions, and mobile layout. Return prioritized findings with source locations and tests; do not edit unless explicitly assigned.

## Handoff B — Data integrity audit
Inventory embedded notes and stable IDs. Check duplicate IDs, missing fields, malformed content, search text coverage, and persistence behavior. Never rewrite the note corpus as a side effect.

## Handoff C — Accessibility audit
Check semantics, labels, keyboard operation, focus visibility, status announcements, contrast, and touch-target usability. Distinguish source inspection from actual assistive-technology testing.

## Handoff D — Feature triage
Evaluate Gemini/user suggestions for value, scope, complexity, privacy, performance, offline implications, and regression risk. Return KEEP / MODIFY / DEFER / REJECT with rationale. Do not implement unapproved changes.

## Handoff E — Implementation
Make only the approved change. Add or update a focused regression check, preserve note data and storage compatibility, run available tests, inspect the diff, and append a log entry.

## Handoff F — Release readiness
Evaluate each validation criterion as PASS / FAIL / NOT RUN with evidence. Do not call the app production-ready or clinically validated based on static review alone.
