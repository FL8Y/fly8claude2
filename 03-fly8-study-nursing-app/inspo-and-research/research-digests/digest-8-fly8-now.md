# Digest 8: FLY8 Study as it is NOW

Root R = /home/user/readthischatgpt/FLY8 Study (branch ccr-13ebab09-xygfde, HEAD 7606c0e). All claims OBSERVED by reading files or running commands this session unless marked.

## 1. What this source is
Expo SDK 58 / RN 0.88 / TypeScript Android app, 92 files under src/, 26 test files. HANDOFF.md (2026-10-16) is stale (says 227 tests, 8 tabs, "Prompt Maker not built", API-key only); CLAUDE.md root status paragraph is also older than the code. Trust code over both. Corrections: `npx vitest run` gave 26 files, **410 passed** (OBSERVED, 5 s). `.github/workflows/` is at the repo ROOT (not in FLY8 Study): `study-app.yml` (typecheck, test, `expo export` android) and `study-app-apk.yml` (npm test, expo prebuild, gradle assembleRelease, upload artifact `fly8-study-apk`; triggers on push to the branch for its paths). One APK run was recorded as green in HANDOFF (run 38953572183) but that APK predates quiz kinds, PNLE prompt, manual mode and pool; the current-HEAD build was triggered ("Trigger APK build with provider pool") but I did NOT verify its result (UNKNOWN). Never run on a device (DEVICE_CHECKLIST.md exists, unticked).

## 2. Precise inventory

### 2a. Exists and is covered by unit tests (logic only; no UI/device test)
- **Navigation shell** (App.tsx): 5-tab bar Home / Study / Learn / Review / More; More lists Library, Mind map, Tutor, Prompt Maker (greyed "Coming soon", NOT built), Settings. Indigo theme, light/dark (tests/theme.test.ts).
- **Content**: 974 preset notes in 14+ JSON files, grouped by `data/presets/bundles.json` into **21 bundles** (verified: chn 73, comm 52, ob 59, pedia 53, cardio 46, respi 20, hema 19, onco 21, periop 22, fer 49, burns 26, gi 35, endo 31, neuro 49, senses 18, ortho 42, psych 78, emerg 10, fund 141, pharm 60, plmar 70 = 974), area-tagged NP1..NP5/GEN. `visuals.json`: 115 SVG diagrams {noteId, caption, svg}. Install via PresetPacks; note reader NoteReader; sharded storage (src/core/shards.ts, tests/shards).
- **Question model** (src/core/types.ts): `Question` = stem, 4 choices, ONE answerIndex, rationale, plus kinds choice/typing/cloze/matching/ordering and PNLE extras (vignette, strategy, area, archetype, optionRationales, reasoning). **No select-all-that-apply, no difficulty field, no per-question id stability across sessions.**
- **Grading** (src/core/grading.ts, 17 tests), seeded option shuffle, hint = half points.
- **Quiz engine** (src/core/quiz.ts): pop session 1-5 Qs, module quiz 10/20/30/50 via `moduleBlueprint` (weak-topic weighted), `summarizeSession` (correct/total/perTopic/weakTopicIds), hard round (max 5 missed), `remediationPlan` + `parseFixTopics` (weak -> generate fix topics loop). Diagnostic 1-50 gate (`canTeach`; lessons locked until done).
- **Mastery/gamification** (src/core/mastery.ts): per-topic mastery+confidence from attempt events, `planOrder`, `weakTopics`, points 10/correct, streak, 6 ranks/levels, `recommendNext` (Home "Today's focus"). Events are append-only `AttemptEvent{questionId, topicId, correct, at, kind, hinted}`.
- **PNLE layer**: `src/pnle/systemPrompt.ts` (long exam-style prompt), retriever picks 3-5 exemplars from bank (100 gold + 31 archetype worked examples), `requests.ts` builds diagnostic/pop/module requests, `parse.ts`, `topicBuilder.ts` (one-topic coverage-map prompt + parser; NOT wired to a screen). Gold: 100 MCQs, 20 per NP area, difficulty 2:10 / 3:28 / 4:41 / 5:21, all `aiWritten:true, nurseReviewed:false`; archetypes.json = 31 types. Last commits "harder gold items" fixed the 30 flagged items in part; no re-verification recorded (UNKNOWN).
- **AI access** (src/ai): manual copy-paste (`manual.ts`, ManualExchange/ManualHost UI, `batch.ts` splits to ~10 Qs), providers openai/anthropic/gemini/openrouter/groq/custom, **pool** with fallback/spread, cooldowns, vision-capability skip, manual last resort (`pool.ts`), Quick Setup (paste free keys, auto-detect, test), FreeAiGuide. Heavily tested (providers 34, quickSetup 20, manual 24).
- **Learning content**: Lessons (mnemonic, key facts, traps, sanitized AI SVG, Wikimedia with credit), mind map (Graph), tutor (notes-first chat), Obsidian import/export (24 tests), photo/PDF import via AI vision, readability + provenance + AI double-check, backup export/import (never the key), Learn tab with title/text search.
- **Study UI**: StudyHub (diagnostic, pop, module, hard round, fix topics), Quiz.tsx + QuizFeedback (strategy chip, per-option rationales, collapsed reasoning, Explain), quizInputs for all 5 kinds. Dashboard: points, level ring, streak, weak topics, due-card count.

### 2b. Exists but should be REMOVED or REPURPOSED under "no flashcards"
- `src/srs/*` (cards.ts, scheduler.ts SM-2-lite, state.ts, store.ts, focus.ts; tests/srs 24 + focus 8 = 32 tests) and `src/ui/Review.tsx` (277 lines, Anki-style decks, Again/Hard/Good/Easy). REMOVE the card/scheduler/Review UI; the bottom-bar **Review tab slot** should become the MCQ "Practice/Review mistakes" tab.
- REPURPOSE `focus.ts` scoring idea (lapses, recent misses, low ease, weak topic, overdue) into per-question/per-topic **missed-question re-queue** driven by AttemptEvents (the scheduling ideas port; the card object does not).
- Wiring to cut: `countDueCards` in Dashboard.tsx, `SRS_KEY` in backupIO.ts (keep reading old backups harmlessly), cards mention in Tutor/Lesson/Settings/Library (grep: srs/Card/focus hits in ~15 UI files, many are just the UI `Card` component, so check by hand).
- `kind` typing/cloze/matching/ordering: PNLE is MCQ-only; keep code (tested) but default prompts to choice only; they are not harmful.
- Onboarding/API-key-first framing: app still opens on an AI-provider onboarding; for a quiz-first app, AI should be optional.

### 2c. GAPS versus a Tangerine-style MCQ practice app (all OBSERVED absent unless noted)
1. **Offline MCQ bank**: no question bank except 100 gold items used only as *prompt exemplars*; every pop/module/diagnostic quiz needs a live AI or manual paste round trip. Generated questions are **not saved** (HANDOFF: "quiz bank deferred"). Queued ~530 new MCQs, bank engine, auto-cards, planner, fact-check list are NOT in repo (confirmed: no such files).
2. **Modes**: only pop/module/diagnostic/hard-round. Missing: topic practice from bank, mixed/random, timed mock exam, weak-areas mode, review-mistakes, bookmarked, unseen-only, tutor/learn mode with immediate vs end-of-test feedback toggle.
3. **Select-all-that-apply**: unsupported (single answerIndex). Needs `answerIndexes[]`, partial/all-or-nothing grading, UI checkboxes.
4. **Results and history**: `SessionResult` computed on screen then discarded (HANDOFF: "module-result persistence" gap); only per-answer events persist; no session list, no review-after-the-fact, no per-question explanation replay.
5. **Stats**: no accuracy-over-time, per-subject/NP-area bars, time-per-question (events have no duration), questions-done counters, streak calendar. Only Home tiles.
6. **Diagnostic**: exists but AI-generated, choice-only, gate-locks lessons, NOT resumable after app kill (session lives in React state, StudyHub.tsx); scoring feeds `planOrder` only; no area-level breakdown report.
7. **Readiness**: none. No predicted pass %, no NP1-NP5 readiness, no weighting by exam blueprint, no countdown/exam date.
8. **Adaptive selection**: partial (`moduleBlueprint` weights weak topics by mastery); no difficulty adaptation (Question has no difficulty), no unseen-first, no spaced re-ask of missed items beyond the card system.
9. **Bookmarks/flags/notes on a question**: absent. Search exists for notes only, not questions.
10. **Planner / study schedule**: planned, not built; Prompt Maker greyed out.
11. **Session resume**, folder picker (Study uses `state.folders[0]`), mixed-kind diagnostic: open (HANDOFF section 8, confirmed by StudyHub.tsx).
12. **Question quality**: nothing nurse-reviewed; gold items AI-written; difficulty labels inflated (T5 finding in HANDOFF).

### 2d. Not restyled (still import legacy `c, sp` from kit instead of `useTheme()`; OBSERVED by grep)
Graph, Lesson, Library (via kit usage), NoteReader, ObsidianImport, Onboarding, PresetPacks, ProviderEditor, ProviderPool, QuickSetup, Review, Settings, Topics, Tutor, FreeAiGuide, ManualExchange. New-style: Dashboard, StudyHub, Quiz, QuizFeedback, More, kit.

## 3. Reuse for the merged app
Question/AttemptEvent model, grading, mastery/planOrder/points/ranks, quiz summarizing, PNLE systemPrompt + archetypes + gold + retriever, manual copy-paste + pool + quickSetup, presets + visuals + reader, sharded storage, backup, theme/kit, 410-test harness, CI workflows.

## 4. Do not reuse / remove
Flashcard stack above; the "Review" tab as flashcards; stale HANDOFF claims; gold items as trusted exam content (unreviewed).

## 5. Open questions for the owner
1. Keep the AI-generated quiz path as an optional "make more questions" feature, or ship bank-only?
2. Is Tutor/lessons/mind map/Obsidian still wanted, or trim to notes + MCQ + stats?
3. Accept AI-written, un-nurse-reviewed questions with a visible "AI-written" badge?
4. Exam date known (for readiness countdown)?
5. SATA required in v1?
