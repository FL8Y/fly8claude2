# PNLE Study Notes HTML — Validation Matrix

Status is NOT RUN until the check is actually performed. A source-code read is not equivalent to browser testing.

| ID | Area | Check | Expected result | Status |
|---|---|---|---|---|
| APP-01 | HTML | Parse document with an available HTML parser | No fatal parse errors; expected document structure exists | NOT RUN |
| APP-02 | JavaScript | Parse the updated inline app script with V8 `new Function` | No syntax errors | PASS — syntax parser accepted the updated inline script; browser execution was not tested |
| APP-03 | Content integrity | Compare embedded note JSON against baseline blob | No unexpected note loss or text changes | PASS — independently compared baseline blob `9369e034a32a5131bd7fda5d1db3611c3ab8d45a` with current HTML; JSON block byte-for-byte identical; 1,000 records retained; zero additions/removals or text changes |
| APP-04 | IDs | Inventory IDs and verify group/area assignments | IDs are present and unique; all area/group metadata valid | PASS — independently rechecked 1,000 unique IDs, required fields, valid group references, and zero group-area mismatches |
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
| APP-18 | Scope | Review changed paths | Changes remain inside `chatgpt loop/` | PASS — only the HTML app, iteration log, and validation matrix were changed in this pass |
| APP-19 | History | Inspect iteration log and commit references | Every material edit has a truthful entry | PASS — iteration 5 records the implementation, baseline blob, HTML commit, actual checks, and tests not run |
| APP-20 | Release honesty | Review final report | No unsupported claims of runtime testing, perfect reliability, or clinical validation | NOT RUN |

## Previously recorded checks (Iteration 3)\n- Embedded JSON parses successfully; 1,000 notes, 21 groups, and 1,000 unique IDs.\n- All notes in the `fer` group use NP3; all notes in the `burns` group use NP5; no remaining group/header area mismatches were found.\n- JavaScript syntax check (`node --check`) and HTML parser passed on the locally generated candidate matching the app-shell change. The exact GitHub blob was verified for embedded data/counts, but was not executed in a browser.\n- Browser/device interaction tests, visual review of diagrams, source-by-source clinical validation, and current DOH vaccine/medication schedules remain NOT RUN.\n\n## Latest recorded checks (Iteration 5)
- GitHub source HTML was fully retrieved; the pre-edit HTML blob was `9369e034a32a5131bd7fda5d1db3611c3ab8d45a`.
- Embedded data JSON compared byte-for-byte before/after; 1000 notes, 21 topic groups, 1000 unique IDs, and 141 diagrams remain.
- Existing note-to-group references are valid; no note/group area mismatches remain.
- Inline app JavaScript passed the V8 syntax parser.
- Static filter fixtures returned 95, 50, 60, 9 matches for renal/NP3/fluids-renal, maternal/NP2/obstetrics, and rabies/NP1/communicable disease queries. This checked the combined-filter logic against the embedded data, not live DOM controls.
- Visible branding and in-head browser title were checked in source; remote font stylesheet dependencies were removed.
- **NOT RUN:** actual browser/device interaction, screenshot/visual layout inspection, console inspection, browser storage persistence test, full HTML parser, keyboard-only audit, external-link validation, and clinical verification.

## Static checks
Record the exact tool/command, output summary, and limitations. If no parser, browser, or target device is available, mark the corresponding check NOT RUN.

## Release decision
- **PASS:** all critical checks were executed and passed, and remaining non-critical gaps are disclosed.
- **CONDITIONAL:** no known critical failure, but meaningful checks remain NOT RUN.
- **FAIL:** a critical check fails or content integrity/scope is violated.

This matrix assesses app behavior and engineering discipline; it does not validate the clinical accuracy of the study notes or imply PRC/official endorsement.


## Independent verification audit (2026-10-23)
- **PASS:** Embedded JSON is byte-for-byte identical to baseline blob `9369e034a32a5131bd7fda5d1db3611c3ab8d45a`; 1,000 notes, 21 groups, 1,000 unique IDs, 141 diagrams.
- **PASS:** Required note fields present; zero dangling group references and zero area/group mismatches.
- **PASS:** V8 `new Function` accepted the inline app script; source checks found the in-head document title, visible branding, mobile viewport/styles, no remote Google Fonts stylesheet, bookmark and studied-state keys, collapsible topic-group logic, breadcrumbs, and safe external-link attributes.
- **PASS (data-level fixture only):** renal + NP3 + fluids/renal = 50; maternal + NP2 + obstetrics = 60; rabies + NP1 + communicable disease = 9; note-ID lookup = 1; deliberate no-match = 0.
- **NOT RUN:** actual browser/device interaction, screenshots/layout inspection, browser console, live DOM search/filter flows, navigation/history clicks, persistence reload, clipboard permissions/fallback, keyboard-only accessibility, HTML conformance parser, external-link audit, clinical validation.
- **Release status:** CONDITIONAL until browser/device tests are performed. Data-level fixtures do not prove that live controls work.
