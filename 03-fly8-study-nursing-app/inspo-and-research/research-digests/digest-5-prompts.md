# Digest 5: owner prompt projects vs FLY8 built-in prompt

## 1. What these sources are
- OBSERVED: `PNLE_Question_Prompt_Maker/` (5 files, ~35 KB) is the owner's canonical question-maker prompt, for a chat model, with a fill-in user template. Never run against a live model (COMPLETION_CHECKLIST: "Live API execution: NOT TESTED"; no nurse review). Only 7 PRC program/TOS URLs are cited; the author did not read full papers.
- OBSERVED: `notes/` is a separate image-to-study-notes project (textbook page photo -> Markdown saved to `notes/generated/`). One generated file exists (`generated/fundamentals/pain-management-page-101.md`). Tests B, C, E, F and nurse review are not done; tests A, D, G passed.
- OBSERVED: the file `here is your guiding prompt` (54 KB) is NOT about nursing. It is an Android app-clone repair brief (Shizuku, wireless ADB, VirtualApp, SPAKE2) for the old FLY8 Clone app (the other CLAUDE.md in the workspace). Ignore it for the merged nursing app. The two START_HERE files and NOTES_MAKER_SOURCE_OF_TRUTH.md are only session-recovery pointers (the "DETAILED" one is a redirect to notes/).
- Trust: both prompts are well-reasoned process documents, not validated products. Anything they say about PRC structure is second-hand.

## 2. Key facts and designs
1. Owner Question Maker (`MASTER_SYSTEM_PROMPT.txt`, 17 KB, plain text) is a 28-item skill menu: priority/triage, first/next, clinical judgment, delegation, pharmacology, calculations, SATA (only if asked), case clusters (3-5 linked items), mechanism-to-bedside chains, Statement I/II, numbered-proposition combos, EXCEPT, ordered sequence, misconception checks, teach-back, clinical analogy.
2. It adds a 12-step silent item workflow and a final QC gate (exact count, one defensible answer, answer positions spread, no fabricated citations, no copied exam items). Rationale output: correct answer, rationale, why each other option is weaker, competency tested, cognitive demand (Bloom label), optional "source note".
3. It stresses Philippine context (barangay health station, RHU, referral levels, Philippine laws/DOH) and target-sitting alignment: use the exact NP labels of the chosen PRC exam program (Aug 2026 program has finer NP groupings; Resolution 10 s.2025 Enhanced TOS applies from Nov 2025).
4. Its user template (`USER_PROMPT_TEMPLATE.txt`) exposes: topic, count (default 10), sitting/year, NP label, difficulty, learner level, logic type, clinical setting, answer mode (answers after each / exam mode with key at end), output (plain text / JSON).
5. FLY8 prompt (`src/pnle/systemPrompt.ts`, 11 sections, JSON-only): ROLE, STRUCTURE (NP1-5 + "any"), ITEM ANATOMY (one decision, two reasoning steps, vignette rules, 4 options, option-length rule, random key position), HIERARCHY (Maslow, ABC, nursing process, least invasive, delegation, communication, infection control, named exceptions), DISTRACTORS (11 named error types, no two the same per item), RATIONALE (causal chain + "Flip:" line + 4 `optionRationales` + `strategy` tag + "Beyond your notes" marker), DIFFICULTY 1-5, VARIETY, SAFETY, PROCESS (self-check, target weakness by error type), SCHEMA.
6. FLY8 schema per question: topic, vignette, stem, choices[4], answerIndex, rationale, optionRationales[4], strategy, area, archetype, difficulty. App discards empty rationale. Single-best only: no SATA, no statement/EXCEPT/sequence/calculation/case-cluster shape.
7. Notes maker's central rule: three layers, A source notes / B explanation / C external cross-check, never silently "correct" the source, `[unclear in image]` instead of guessing, drug doses/units are exact-transcription targets, no invented normal values (`notes/MASTER_SYSTEM_PROMPT.txt`).
8. Notes maker chooses representation per content: outline, comparison table, causal flow chain (trigger -> change -> organ effect -> manifestation), timeline, stepwise checklist, Cornell cue/notes, concept map with verb-labelled links; mnemonics only if accurate. Disease template = 13 modules (definition ... nursing interventions with rationale ... red flags), used only where they fit.
9. Notes maker default output is 10 headed sections incl. "Retrieval check" (3-8 original questions) and "Coverage and uncertainty audit"; flashcards only "on request" (compatible with the owner's no-flashcards decision if left off).
10. Process rules: save to file then re-fetch to verify, page ledger (upload sequence vs printed page), anti-loop (stop after 2 no-progress actions), done-vocabulary. These are repo-workflow rules for ChatGPT/Claude agents, not app features.
11. Research basis (`notes/RESEARCH_AND_DESIGN.md`): retrieval + spacing (Trumble 2023 review), Cornell notes, AACN 5Cs concept map. Reasonable, but not proof that the notes help.

## 3. REUSE
- From owner Question Maker into FLY8 prompt: (a) extra item formats as optional "shape" modules: Statement I/II, numbered-proposition combos, EXCEPT, ordered sequence, case-cluster, calculation, teach-back/misconception, SATA (owner wants SATA where the source supports it); (b) Philippine setting list (RHU, barangay, referral levels) as a context parameter; (c) Bloom label and "competency tested" as optional metadata; (d) the 12-step workflow and QC gate as the self-check list (FLY8 PROCESS has 7 checks; add exact-count, key-position spread, scope/calc/unit check); (e) `USER_PROMPT_TEMPLATE.txt` fields as the Prompt Maker screen's form (topic, count, sitting, NP, difficulty, skill, setting, answer mode).
- From notes maker into note generation/import: the A/B/C source-vs-explanation-vs-external layering, `[unclear in image]` rule, exact-transcription rule for doses/ranges/units, content-type to representation chooser, the 13-module disease template and drug/procedure templates, causal-chain mechanism lines, coverage/uncertainty audit. Use as the "transcribe photo" and "make notes" prompt for manual mode.
- Keep from FLY8: hierarchy, named distractor error types, Flip line, optionRationales, strategy tag, 1-5 difficulty, notes-first + "Beyond your notes" marking, JSON schema (the app parses it).

## 4. DO NOT REUSE
- `here is your guiding prompt` and anything about Shizuku/ADB/VirtualApp: wrong product.
- Repo-session machinery (SESSION_PROTOCOL, PAGE_TRACKER, WORK_QUEUE, "re-fetch from GitHub"): agent workflow, not app logic.
- Owner prompt's "browse official PRC documents" instruction: the in-app model usually cannot browse; FLY8's approach (stated guess, no quoted official wording) is safer. The owner prompt's own Aug 2026 NP list is also second-hand (INFERRED).
- Owner prompt's plain-text-first output: unparseable; use JSON.
- Flashcard option in notes maker: owner decision is no flashcards.
- Notes maker's "cite reliable sources" for layer C: model-written URLs get fabricated (the sample note contains WHO/ACS links, UNKNOWN whether checked). Replace with "Check against your textbook" and no URLs.
- Never imitate recalled/leaked items: both prompts already forbid it; keep.

## 5. Comparison and conflicts with FLY8
- Owner prompt does better: breadth of exam formats (SATA, statement combos, EXCEPT, sequences, clusters, calculations); explicit Philippine/community context; target-sitting selection; user-facing controls; case clusters with evolving cues; a rule that format variety is subordinate to quality; honest evidence limits.
- FLY8 prompt does better: machine-parseable schema with validation; concrete reasoning hierarchy and exceptions; named distractor error types (owner says only "plausible"); rationale with per-option explanations and a transferable Flip line; strategy tag for the UI chip; numeric difficulty; "notes first" grounding and weakness targeting via error type; length-bias and key-position rules (owner: "rotate"); tested (word-count budget, section tests). It is also shorter and splittable by section, which matters for ChatGPT Go limits.
- Conflicts: (1) FLY8 area tags NP1-5 vs owner "use exact label of target sitting" (older sittings use NP III-V "Parts A-C"; Aug 2026 finer labels). Resolve with an `area` plus optional free-text `sittingLabel`. (2) Owner says output only after QC "silently"; FLY8 enforces it by schema. (3) FLY8 forbids invented lab values/doses; owner allows calculations: allow calculations only with all numbers given in the stem and units shown, checked in code.
- Notes format vs FLY8 preset JSON (`id, area, title, body Markdown, tags, priority, outlineRefs, aiWritten, nurseReviewed`; validator in `src/presets/index.ts`): OBSERVED sample body = `## What it is`, `## Must-know structures`, `## How the exam asks about it`, `## Related topics` with `[[wikilinks]]`. The notes-maker output is page-faithful (10 sections, page numbers, tracker, source layer) and has no id/area/priority/tags. Mapping: Core idea -> What it is; Detailed source notes -> Must-know; High-value terms table -> inside body; Nursing relevance + Common confusions -> How the exam asks; Retrieval check -> drop or feed question bank; Coverage audit -> not stored (or `outlineRefs`). Add a small wrapper instruction: output a JSON note object with the preset fields; `aiWritten:true`, `nurseReviewed:false`; `[[Title]]` links only to existing titles; unreadable text kept as `[unclear]`. A user-photographed note is source-faithful (the owner's own material), preset notes are AI-written general knowledge: they need different labels.

## Proposed single prompt set (one shared core + modules)
1. `core` (FLY8 ROLE/SAFETY/HIERARCHY/DISTRACTORS/RATIONALE/DIFFICULTY, plus owner QC gate and Philippine context).
2. `shape` modules appended on demand: single-best (default), SATA, statement-combo, EXCEPT, sequence, calculation, case-cluster.
3. `request` template from the owner's user template (the Prompt Maker form).
4. `notesFromPhoto` and `notesFromText` prompts from the notes maker, emitting preset-shaped JSON.
5. Separate note-quality check prompt (A/B/C layering) for the existing AI double-check.
Keep all items flagged aiWritten / nurseReviewed:false.

## 6. Open questions for the owner
1. SATA and statement-combination items: include them in the default mix, or only when asked?
2. Should the app ask "which exam sitting" (Nov 2025 vs Aug 2026 labels), or keep one NP1-5 map?
3. Should photographed notes be stored as the owner's source-faithful notes (separate from AI-written presets), with A/B/C layers visible in the note?
4. Output in manual copy-paste mode: JSON only (safer to parse), or readable text that the app converts?
5. Is a nurse or instructor available to spot-check answer keys, since neither prompt has been tested?
