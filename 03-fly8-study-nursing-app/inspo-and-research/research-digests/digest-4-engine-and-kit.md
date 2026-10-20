# Digest 4: practice engine + Content Authoring Kit
R = mainview/Nursing app master plan. Paths below are relative to R.

## 1. What these sources are
Static reverse-engineering of Tangerine Prep 1.9.0 (`App 4 tangerine prep inspired research/`), a PNLE/NCLEX-style app, plus the independent "Content Authoring Kit" (JSON schemas, Python validator, 6 companion prompts, topic-tree builder). Evidence labels (OBS/INF) are used honestly: the real app's scoring, adaptive selection, SRS, ranking and AI are server-side and NOT visible, so the engine is an original design "informed by" it. Trust: high for what was seen in strings/routes; formulas are ours. The Kit's own README says its sample items are unverified (`needs_review`).

## 2. Key facts
- OBSERVED: item types are mcq, sata, matrix, highlight, cloze; case studies ("situations") are one vignette with 2+ linked items scored in credits (earned/max). `research/learning_features.md` s.9, `Content Authoring Kit/research/tangerine_questions_topics_prompts.md`.
- OBSERVED: "AI exam" is curation from the stored bank, not question writing (Cleo says so). Kit research s.0. Fits FLY8: the bank is the product; AI only picks/explains.
- OBSERVED: exam types practice/adaptive/mock/battle/ai; passing rule text "weighted avg >= 75%, no subject < 60%" (a design hint, not the official PNLE rule; PNLE passing is UNKNOWN to these sources).
- OBSERVED: free-tier "energy" (20, regen ~50 min), Manila-day streak clock, rank ladder of tiers+stars. Server formulas UNKNOWN.
- Engine `product/practice-engine/src/engine.ts` (~110 lines, pure TS, no deps): `scoreItem` (SATA +1/-1, floor 0, out of #correct; matrix and cloze per-row/blank), `pCorrect/updateAbility` (Rasch 1PL, SGD step 0.4, theta clamped -4..4), `pickNext` (unseen item with |difficulty - theta| minus 0.5 bonus for weak areas), `readiness` (P(correct) on a 0-logit item, as %), `dayKey/recordActivity/activeStreak` (IANA timezone, e.g. Asia/Manila), `spendEnergy`.
- Kit schema `schemas/question.schema.json` (JSON Schema 2020-12): mcq with options `[{label A-D,text,why_not}]`, `answer` letter, `rationale` (>=40 chars), optional `rationale_simplified`, `hint`, `images[]`; sata/matrix/cloze/highlight; `situation` wrapper. Required `meta`: nursing_practice NP1-5, tos_content_area, topic, cognitive_domain, difficulty (easy/moderate/difficult), evidence_status (verified_source/established_knowledge/needs_review), optional topic_id, competency, nursing_process_phase, sources, tags.
- Validator `tools/validate_content.py` (python + jsonschema): banned "all/none of the above" and combined options, duplicate options/stems/ids, option length ratio >3x, absolute-word warning, answer-key skew (>40% one letter), correct-is-longest (>50%), unknown topic_id, and `--publish` that fails anything still `needs_review`.
- `tools/build_topic_tree.py` + `samples/topic-tree.generated.json`: 7 subjects, 636 nested topics, ids as slug paths; NP mapping PROVISIONAL.
- Companion prompts `prompts/01` (JSON-only output wrapper), `02` (case-study clusters), `03` (second-pass reviewer), `04` (simplified rationale + hints), `05` (exam-set curation from existing items), `06` (flashcards: skip).
- Test status (I ran them myself, Node v22.22, python3): practice-engine `node --experimental-strip-types --test test/engine.test.ts` = 10/10 pass; client-kit (Tangerine Prep Complete Guide/product/client-kit) 16/16 pass; Kit `python3 -m unittest tests.test_kit` = 11/11 OK (jsonschema installed). All unit tests only; never run on a device or in a React Native bundle; no integration with FLY8.

## 3. REUSE
1. `scoreItem` for SATA (+/- rule, floor 0). Directly gives the owner's select-all-that-apply with partial credit; port into FLY8 `src/core` as a new scorer. Keep mcq as 1/0.
2. Rasch `updateAbility` + `pickNext` + weak-area bonus for an adaptive "Smart practice" mode and for ordering diagnostic follow-ups; maintain theta per area (and overall). Needs numeric difficulty: map FLY8 1-5 to logits, e.g. (d-3)*0.8, then recalibrate from observed accuracy later.
3. `readiness` as a labelled estimate ("practice-readiness", not a pass prediction), plus the "no subject below 60%" per-area floor idea shown on the dashboard.
4. `dayKey`/streak with fixed IANA timezone (Asia/Manila) so streaks do not break by phone timezone; FLY8 streak should adopt it.
5. Validator rules as a TypeScript import gate for pasted/AI-generated items (see s.5-6): banned option forms, duplicates, key skew, longest-answer clue, topic id existence, publish gate tied to FLY8 `nurseReviewed`.
6. Prompt 01 (JSON output), 03 (reviewer), 04 (hints/simplified rationale) as templates for the existing Prompt Maker; prompt 05 for bank-curation. Rewrite wording in FLY8 voice and keep the PNLE Maslow/ABC strategy tag.
7. Topic tree (`topic-tree.generated.json`) as an area/topic id list for the `topic` field, after checking against FLY8's 21 bundles.

## 4. DO NOT REUSE
- Energy cap/paywall (`spendEnergy`): the owner wants a free personal app. Rank ladder, battles, apex stakes, Tiers client-kit (`rank.ts`): social/server-driven, not in the 10-point plan (a points/level system already exists).
- `06_flashcards_from_notes.md`, client-kit `flashcards.ts`, Anki export: conflicts with "no flashcards".
- Any Tangerine content, strings, names, exam-passer list, decompiled code (the research folder rightly excludes them). Passing-chance claims ("35% higher") are marketing, not evidence.
- Matrix/highlight/cloze: only if time allows; FLY8 already has cloze/matching/ordering in the module quiz (typing, cloze, matching, ordering).

## 5. CONFLICTS and schema differences (FLY8 -> Kit)
| FLY8 | Kit | Note |
|---|---|---|
| `answerIndex` 0-3 | `answer` letter A-D | trivial map; SATA uses `answers[]` index list (min 2, 5-8 options) which FLY8 lacks |
| `options[4]` strings | `options[{label,text,why_not}]` | map; label = "ABCD"[i] |
| `optionRationales[4]` (learner-visible per-option why) | `why_not` (INTERNAL, hidden) + single `rationale` | FLY8 is richer for learners; map `why_not` <-> `optionRationales`, but answer slot is empty in Kit |
| `reasoning` | `rationale` (>=40 chars) | rename; Kit adds `rationale_simplified`, `hint` |
| `vignette` + `stem` | `stem` only; `situation` cluster wraps 2+ items | FLY8 vignette is per-item; cluster = shared vignette across items (new concept) |
| `difficulty` 1-5 | easy/moderate/difficult | lossy: 1-2 easy, 3 moderate, 4-5 difficult; keep numeric in an extension field |
| `area` (PNLE area, np1..np5-ish) | `meta.nursing_practice` NP1-5 + `tos_content_area` | map; Kit's NP mapping is provisional, FLY8's research.md is also INFERRED |
| `topic`, `noteId` | `meta.topic`, `topic_id` (slug path) | add topic_id; noteId has no Kit equivalent (keep) |
| `archetype`, `strategyTag` (Maslow/ABC) | none (`tags[]`, `nursing_process_phase`, `cognitive_domain`) | keep as extension; map strategyTag->tags; Kit's phase/Bloom domain is extra to add |
| `keyFacts` | none | keep extension |
| `aiWritten`, `nurseReviewed` booleans | `evidence_status` 3-way + `sources[]` | map: nurseReviewed true -> established_knowledge/verified_source else needs_review; aiWritten has no Kit field (keep) |
| `id` | `id` (topic-slug-NNN) | compatible; Kit forbids unknown fields only in `meta` and `situation`, not item root |
| (none) | `images[]` with alt/credit/license | useful for FLY8 diagrams (115 SVGs) |

Overlaps: FLY8 already has 100 gold MCQs with stronger distractor rationales, diagnostic, pop/module quizzes, streaks/points. The Kit's validator is Python (build-time); FLY8 is TS (on-device), so rules must be ported, not shelled out.

## 6. Migration path (PROPOSED, nothing built)
1. Adopt a superset "FLY8 v2" item: keep all FLY8 fields; add `type: "mcq"|"sata"`, `answers?: number[]` (SATA), `rationaleSimple?`, `hint?`, `topicId?`, `evidence: "needs_review"|...`, `sources?`, `images?`, optional `clusterId`. Existing 100 gold items stay valid (default type mcq).
2. Write adapters `fromKit(item)` / `toKit(item)` (letter<->index, options objects, difficulty buckets) plus a TS port of the validator rules; run on every Prompt Maker import and on the gold bank (a sanity check, expect key-skew and length warnings).
3. Port `scoreItem` (mcq+sata only) and add SATA to the quiz screen; store per-answer credits not just correct/wrong.
4. Add `theta` per area to saved state (sharded storage) and an adaptive pick mode; show readiness with an honest "estimate" label.
5. Tune Prompt Maker output to prompt 01 + FLY8 extensions; reviewer prompt 03 for a second pass.
6. Later: situation clusters (shared vignette, 2-5 items, credit totals).

## 7. Open questions for the owner
1. Include SATA now (needs 5+ options, partial credit) or MCQ-only until later? PNLE itself is mostly 4-option single answer (UNKNOWN from these sources).
2. Should the adaptive mode ("hardest-fit questions") replace the fixed diagnostic, or sit beside it?
3. Is a readiness percentage wanted, given it is an unvalidated estimate and could mislead before the exam?
4. Do case-study clusters (one patient, linked questions) matter enough to build now?
5. Keep streaks on Philippine time (Asia/Manila) regardless of the phone clock?
