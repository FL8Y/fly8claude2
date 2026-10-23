# HTML App Improvement Harness

## Goal
Improve `PNLE Study Notes.html` through small, evidence-backed changes without losing study content or breaking core workflows.

## Per-session budget
Maximum five iterations per session. Each iteration must produce a testable change or new evidence. Never loop indefinitely or make edits just to satisfy a loop counter.

## One iteration
1. **Recover:** read the current HTML, this harness, app baseline, validation matrix, and iteration log.
2. **Inspect:** identify one concrete defect or user-approved improvement; inspect relevant code and data.
3. **Plan:** state expected benefit, risk, exact change, and acceptance test.
4. **Patch:** make the smallest targeted change. Avoid full-file regeneration.
5. **Check content:** compare embedded note count/IDs/text before and after; unexpected changes block acceptance.
6. **Test:** run syntax/static checks and any available interaction tests. Browser-only checks remain NOT RUN if no browser was used.
7. **Review:** inspect diff, scope, accessibility, storage compatibility, and dependency changes.
8. **Log:** append an entry to `ITERATION_LOG.md` with actual evidence and commit ID.
9. **Decide:** ACCEPT, REVISE, REJECT, or BLOCKED; then reassess the next highest-value issue.

## Priority order
P0: data loss, crash, unsafe rendering, corrupted note content, broken navigation.
P1: search/filter correctness, persistence robustness, copy behavior, responsive layout, keyboard accessibility.
P2: performance, maintainability, clearer feedback and empty states.
P3: cosmetic polish and optional features.

## Required test categories
- HTML structure and JavaScript syntax.
- Data integrity: count, stable IDs, duplicates, non-empty required fields.
- Search and filter combination.
- Note open/back/hash navigation.
- Studied-state save/load and malformed/unavailable localStorage.
- Theme toggle and copy fallback behavior.
- Keyboard navigation, accessible names, focus visibility.
- Narrow viewport and long-content layout.
- Dependency/network behavior, including offline expectations.

## Stop conditions
Stop when:
- five iterations have been attempted in the current session;
- two consecutive passes find no material defect and add no useful evidence;
- all critical acceptance criteria pass and remaining work is cosmetic/speculative;
- a required user decision is missing;
- a critical regression cannot be confidently fixed;
- tool limitations prevent honest validation.

## Required end-of-session report
- Iterations attempted and decisions.
- Files changed and commit IDs.
- Tests actually run with results.
- Tests NOT RUN and why.
- Content-integrity result.
- Remaining risks and next highest-value task.


## Latest pass result
Iteration 3 added 26 topics and updated selected notes. The data JSON and unique IDs were checked; the candidate HTML passed a local HTML parser and JavaScript syntax check. The full app has not been browser-tested, and clinical claims remain subject to source-level review. Prioritize reviewing the highest-risk medication, immunization, infectious-disease and legal statements before any claim of release readiness.
