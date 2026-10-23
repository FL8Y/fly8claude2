# PNLE Study Notes HTML — Validation Matrix

Status is NOT RUN until the check is actually performed. A source-code read is not equivalent to browser testing.

| ID | Area | Check | Expected result | Status |
|---|---|---|---|---|
| APP-01 | HTML | Parse document with an available HTML parser | No fatal parse errors; expected document structure exists | NOT RUN |
| APP-02 | JavaScript | Extract inline scripts and run syntax check | No syntax errors | NOT RUN |
| APP-03 | Content integrity | Compare embedded note records before/after edits | No unexpected note loss or text changes | PASS — 974 baseline records retained; 26 new records added; 1,000 unique IDs in the saved HTML data |
| APP-04 | IDs | Inventory note IDs | IDs are present and unique | PASS — 1,000 unique IDs confirmed by JSON validation |
| APP-05 | Search | Search by title and body text | Correct matches; no-result state is clear | NOT RUN |
| APP-06 | Filters | Test All, NP1–NP5, Core individually and in combination with search | Results match documented filter semantics | NOT RUN |
| APP-07 | Navigation | Open note, return to list, use hash/back/forward | No dead end or incorrect note state | NOT RUN |
| APP-08 | Persistence | Mark/unmark studied note and reload | State persists as intended | NOT RUN |
| APP-09 | Storage failure | Malformed/unavailable localStorage | App degrades safely; no uncaught fatal error | NOT RUN |
| APP-10 | Theme | Toggle light/dark repeatedly | Theme changes predictably and remains readable | NOT RUN |
| APP-11 | Copy | Copy note with clipboard available and fallback path | Clear success/failure feedback; no crash | NOT RUN |
| APP-12 | Accessibility | Keyboard-only navigation and accessible names | Core workflows operable; focus and states visible | NOT RUN |
| APP-13 | Responsive | Narrow mobile viewport and long note | No critical clipping, overlap, or horizontal overflow | NOT RUN |
| APP-14 | Performance | Large search corpus and repeated filter changes | Interaction remains responsive on target devices | NOT RUN |
| APP-15 | Network/offline | Load with external font network unavailable | Core reading remains usable; remote dependency is transparent | NOT RUN |
| APP-16 | Security | Review dynamic rendering and external requests | No newly introduced unsafe HTML, telemetry, or unapproved network access | NOT RUN |
| APP-17 | Feature request | Triage supplied Gemini suggestions | Each suggestion gets KEEP / MODIFY / DEFER / REJECT with reason | NOT RUN |
| APP-18 | Scope | Review changed paths | Changes remain inside `chatgpt loop/` | PASS — only `chatgpt loop/PNLE Study Notes.html` and harness documentation were changed in this pass |
| APP-19 | History | Inspect iteration log and commit references | Every material edit has a truthful entry | NOT RUN |
| APP-20 | Release honesty | Review final report | No unsupported claims of runtime testing, perfect reliability, or clinical validation | NOT RUN |

## Latest recorded checks (Iteration 3)\n- Embedded JSON parses successfully; 1,000 notes, 21 groups, and 1,000 unique IDs.\n- All notes in the `fer` group use NP3; all notes in the `burns` group use NP5; no remaining group/header area mismatches were found.\n- JavaScript syntax check (`node --check`) and HTML parser passed on the locally generated candidate matching the app-shell change. The exact GitHub blob was verified for embedded data/counts, but was not executed in a browser.\n- Browser/device interaction tests, visual review of diagrams, source-by-source clinical validation, and current DOH vaccine/medication schedules remain NOT RUN.\n\n## Static checks
Record the exact tool/command, output summary, and limitations. If no parser, browser, or target device is available, mark the corresponding check NOT RUN.

## Release decision
- **PASS:** all critical checks were executed and passed, and remaining non-critical gaps are disclosed.
- **CONDITIONAL:** no known critical failure, but meaningful checks remain NOT RUN.
- **FAIL:** a critical check fails or content integrity/scope is violated.

This matrix assesses app behavior and engineering discipline; it does not validate the clinical accuracy of the study notes or imply PRC/official endorsement.
