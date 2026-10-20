# Digest 1: Tangerine Prep guide, part 1 (practice / MCQ experience)

## 1. What this source is
`tang/guide_part1.txt` (792 lines, first third of the 92-page "Tangerine Prep Complete Guide"): a static decompile read of Tangerine Prep 1.9.0 (PNLE app). Covers app map, feature map, corrections table, Part A (practice hub, setup screens, session player, rationale card, report, history, question browser, mastery tags, My stats, daily goal/streak) and the start of Part B (onboarding, diagnostic, case studies, flashcards, Ask Cleo). Trust: good for UI flow, copy, field names (every claim tagged OBS/INF/SRV; app never run, no server calls). Weak for logic: scoring, adaptive choice, mastery, pass probability, readiness are SERVER-side and unseen. It is about THEIR product; treat as pattern research only.

## 2. Key facts / designs (Part A unless noted)
- **Practice hub**: tiles Custom Exam, AI Exam, Mock Board, Adaptive, Coaching waitlist; offline works only for Custom (AI/Mock/Adaptive say "needs a connection"). (Part A s1)
- **Custom exam setup**: subject tree with tri-state checkboxes (select all / clear / expand); question-count stops [10,25,50,75,100,125] default 25; time-limit stops [5,10,20,30,60,90,120] min default 30; answer reveal toggle "Show as you go" (per_question, default) vs "Show at end". (A s2a)
- **Mock Board**: pick one NP (NP1 Community Health, NP2 Maternal & Child, NP3 Surgery & Oxygenation, NP4 Nutrition & Endocrine, NP5 Mental Health); fixed ~100 items / 120 min (INF); answers only at end; "After exam: Answers & rationales". (A s2b)
- **Player**: one question per page, lettered options, long-press-style option ELIMINATION (strike-through, restorable), Submit, then correct/wrong reveal + sound + success/error haptic (only in per-question mode), CTA "Next Question"/"Finish". No Previous button; revisit via a **question map grid** (answered / current / unanswered cells). (A s3b)
- **Timer is soft**: at zero prompt "Submit now / Continue answering". Leave dialog: End Session / Take A Break (pause, resume later) / Keep Answering. Resume restores answered state. (A s3b, s3a)
- **Rationale card**: Full vs Simplify tab (preference remembered), rationale text, "Clinical pearl", images with credit, sources, verified-reviewer badge, high-yield flag, Bloom level, cohort stats ("Answered correctly %", "Avg time"). Audio narration and video are extras. (A s3c)
- **Report a problem** per question: types Unclear / Too long / Incorrect / Other + notes. Separate from the flag icon. (A s3b)
- **Mastery tag** per question: Learning / Reviewing / Mastered, user tag overrides AI estimate; tagging nudge can be switched off after repeated dismissals. Drives browser chips (Learning red, Reviewing amber, Mastered green, Unanswered grey). (A s7, s6)
- **Results report** (built client-side from session detail, paged): score %, correct/incorrect/unanswered, readiness chip (>=85 "Board-ready momentum", >=75 "Almost there", >=60 "Needs reinforcement", else "Recovery mode"), by-practice-area bars (green >=75, amber >=55, red below), "What to review" concepts from misses, numbered **Exam Review grid** (Correct/Incorrect/Skipped legend, "Show only the N you missed"), Bloom breakdown, AI analysis (diagnosis, key pattern, "where your thinking slips", next steps; test-taking vs knowledge gap vs mixed). (A s4b)
- **History**: list of sessions (type chip, status, score, correct/answered/total, area, date, time); row action Resume if unfinished else Review; no filters, no retake. Empty state "No tests yet". (A s5)
- **Question browser**: search, topics grouped by area with Correct/Wrong/Unseen/Total per topic, "Practice topic" button, mastery-status filter, "TOS mode" regroup by exam blueprint. (A s6)
- **My stats**: passing-chance ring with caveat ("not validated against exam outcomes... not your board rating"), "need N more questions in subject X" evidence rule, per-area accuracy (<60 red, 60-74 amber, >=75 green), weakest-topics-first list with "Tap to practice", 14-day accuracy trend. Daily goal ring (questions today, topics mastered, 7-day pass rate), streak calendar with milestones 3/7/14/30/50/100/200/365. (A s8-9)
- **Case-study item types** (Part B s5.2): `mcq`, `sata` (checkbox set), `matrix` (row x column, one choice per row), `highlight` (tap selectable text segments), `cloze` (sentence tokens with dropdown per blank, `correctIndex`). One shared patient stem for 5 items; partial credit (`earned_units/max_units`) per item; completion screen shows accuracy by Bloom level as a "reasoning ladder". Item fields: seq, type, bloom_level, difficulty, options|tokens|rows|columns|segments, rationale.
- **Diagnostic** (B s2): one MCQ per page with feedback each question, then level bucket from accuracy (>=80 Advanced, >=60 Proficient, >=40 Developing, else Foundational), areas list, "your misses cluster in a few areas"; guest attempts stored locally and submitted later.

## 3. REUSE (ideas/patterns, build original)
- Session model: `{id, type: practice|mock|diagnostic|topic, questionIds, revealMode: perQuestion|atEnd, timeLimit?, status: created|in_progress|paused|completed|abandoned, startedAt, pausedAt, attempts[{qid, selected, correct, seconds}]}`. Pause/resume, soft timer, leave dialog.
- Custom-exam builder screen (tree of subject > topic, count slider, time slider, reveal toggle) fed by FLY8's 21 subject bundles; offline by default.
- Player extras worth taking: option elimination, question-map grid, reveal-mode toggle, "report a problem" (store locally, could feed the owner's gold-item fixes).
- Item-type schema for beyond-MCQ: sata first (the exam has it), then matrix/cloze later; JSON shape: `{type, stem, options|rows+columns|tokens, key, rationale, bloom, strategy}`; SATA scoring needs a rule (all-or-nothing vs partial; UNKNOWN what PNLE uses).
- Results page recipe: score, readiness chip bands, per-area bars, miss-concepts "What to review", numbered review grid with missed-only filter. All computable client-side from attempts.
- Rationale card with per-option explanation plus "Clinical pearl" and sources; map onto FLY8's strategy chip (Maslow/ABC). Simple/Full toggle is cheap with an AI-written short version.
- Local question mastery tag (Learning/Reviewing/Mastered) with user override, used as filter and weak-topic signal; stats screen with weakest-topics-first and 14-day trend; a "need N more answers" honesty rule before showing any readiness estimate; wording "estimate, not your board rating".
- History list with Resume/Review and an empty state; question browser with topic Correct/Wrong/Unseen counts.

## 4. DO NOT REUSE
- Any server logic: adaptive "Chev", passing-probability model, rank ladder, cohort accuracy/time stats (needs many users), energy/paywall system, Versus battles, social, voice rooms, WebSockets.
- Their copy (readiness phrases, Cleo lines), names, art, sounds, the embedded exam-passer names, their question/rationale content. Use own wording for chips.
- Flashcards (SRS section, "Flashcard" button on rationale) conflict with owner's no-flashcards decision. Skip the "Explain It", library/flashcard packs.
- Premium audio/video rationale: out of scope. AI exam analysis is an LLM call; if wanted, build it with FLY8's provider pool, not their prompts (not visible anyway).
- Copying readiness thresholds verbatim is fine as generic numbers but they are unvalidated; label as app heuristics.

## 5. Conflicts / overlaps with FLY8 Study
- FLY8 already has: diagnostic gate, pop/module quizzes with hints and points, weakness loop, tutor, lessons, strategy chip, 100 gold MCQs, streaks. Overlap is large; Tangerine adds the exam-style SESSION layer: configurable custom exam, timed mock by NP, review grid, history, per-topic stats, question map, elimination, soft timer.
- FLY8 module quiz has typing/cloze/matching/ordering types; Tangerine's list is mcq/sata/matrix/highlight/cloze. FLY8 lacks SATA as a first-class type (check) and has extras (typing, ordering) that are not PNLE-like; keep MCQ+SATA as the default pool.
- Diagnostic: Tangerine shows feedback per question; FLY8 gate is before teaching (compatible). Level buckets are similar to FLY8's mastery plan.
- Tangerine NP naming (NP4 Nutrition & Endocrine, NP5 Mental Health) is slightly different from FLY8's research (NP5 psychiatric + emergency); FLY8 research file stays authoritative, both second-hand.
- Mastery: FLY8 tracks per-topic mastery; per-QUESTION tags are new.
- Their "Show at end" mode vs FLY8 instant feedback: offer both.

## 6. Open questions for the owner
1. Should "Mock Board" (long timed NP-style exam, answers only at the end) be a headline mode, given only 100 gold items exist today?
2. Select-all-that-apply: include from the start (needs a scoring rule: all-or-nothing vs partial)?
3. Do you want a "readiness estimate" at all, or just accuracy per area (their estimate is unvalidated and needs lots of answers)?
4. Keep FLY8's typing/ordering/matching quiz types, or restrict to MCQ + SATA to match the exam?
5. Per-question "Learning / Reviewing / Mastered" tags: manual by you, automatic from answers, or both?
