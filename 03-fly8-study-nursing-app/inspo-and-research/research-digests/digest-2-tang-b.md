# Digest 2: Tangerine Prep guide, part 2 (D/tang/guide_part2.txt)

## 1. What this source is
792 lines, the middle of the "Complete Guide". Despite the brief, it is NOT mostly user-journey prose. It is static-analysis notes from a decompiled Android build (Hermes JS, strings dump; "app never run"). It covers: Library tail (folders, sources, create/refine/search/study, lines 1-59), Part D (pre-built content pipeline, monetisation, analytics, 60-394), Part E (navigation, settings, social, battles, 395-650), Part F (design tokens, 651-793). Onboarding tour, diagnostic, study plan, cheat sheets and case studies appear only in fragments (Home schedule card, 4-step coach marks, case-study item types, cheat-sheet gating). Trust: line refs (L=) tagged OBS (seen in code) vs INF (inferred); it says all server logic (grading, scheduling, ranking, generation) is unseen. Good for UX patterns and data shapes; not for algorithms.

## 2. Key facts and designs
- **Question object (Part D 1.1, lines 66-94), OBS.** Fields: stem/body, options[{id, option_text, option_order, is_correct}], rationale_summary AND simplified_rationale (two depths), answer_sources (citations), rationale_images[] with per-figure credit/licence/public-domain flag, difficulty, bloom_level (remembering..creating), practice_area (NP1..NP5), topic_id, is_published, is_verified, is_high_yield, verified_by, timestamps. No content hash.
- **Verified-reviewer badge (1.3).** Tiers "RN Verified" / "RN Topnotcher Verified", "High Yield" chip, "Reviewed by" card with name/date. Trust signal for exam questions.
- **Report-a-question loop (1.4).** Four reasons: unclear, too_long, incorrect, other, plus free notes. "too_long" exists because LLM stems run long.
- **Case studies (1.2, line 121).** Multi-step "situations": header {practice_area, pnle_np, tos_section, stem}; items typed mcq, sata, matrix, cloze, highlight; each has bloom_level, difficulty, rationale. Aligned to the PNLE table of specifications (TOS). Only 2 free.
- **Offline bundle (1.2).** Manifest (version, chunk_count, counts) then resumable chunk download into staging tables, then atomic swap; 24 h refresh; opaque version string, no delta. SQLite rows stored as JSON payload text.
- **Authoring pipeline (1.7, INF).** TOS blueprint, LLM draft, LLM tag/Bloom/rewrite passes, human RN verification, publish flag, user reports. Model-lab compares models on tag_generation, blooms_check, question_rewrite.
- **Library (2.4-2.9).** Sources (files, photos, pasted text up to 160,000 chars) are read once and reused; AI generates cards, questions, Explain-It essays or cheat sheets from ticked sources plus optional focus text and count. Draft is reviewed and edited before saving. Refine flow: select items, give an instruction, preview the revision, Apply or Undo.
- **Question item shape in Library (line 31).** {prompt, options[{id, body, correct}], sections}.
- **Practice UX.** Option eliminate/restore, "Next Question", "random" vs "due" modes, session resume, post-answer tagger (Learning / Reviewing / Mastered) that shapes later practice (line 527).
- **Explain-It (line 48-49).** Free-text or voice answer graded by LLM against a rubric: score, "Your understanding", key points, "Not included", model answer, per-criterion feedback, 12,000-char cap.
- **Home schedule card (1.5).** Task types Learn / Recall / Review / Assignment / Timed mock / Daily AI / Cheat sheet / Flashcards / Case study; states Today / Up next / Needs attention / Completed; "No exam date set" state; exam-date countdown ("D-day"); daily goal ring ("X / Y questions today") and insight labels (Recovery mode / Needs reinforcement / Almost there / Board-ready momentum).
- **Gating levers (Part 2.4-2.5).** Energy (20/day free), weekly AI credits, timed cheat-sheet preview, performance locked ("Unlock your forecast"), review rationales locked on free. FLY8 needs none of this (single user, no paywall).
- **Design (Part F).** Warm light-only palette (orange #F97316 brand, warm ink #221A12, correct #15803D on #F0FDF4, incorrect #B91C1C on #FEF2F2), DM Sans + Fraunces serif headings, 44 px min tap, "lip" 3D buttons, 8/12/16 spacing scale, tinted shadows. Shell: 5 tabs + More; streak chip, calendar of active days.
- **Social/battles (Part E).** Friends, DMs, groups, Townhall feed, 1v1/Battle Royale, ranks; all server-side with WebSockets.

## 3. REUSE (ideas/patterns only, original implementation)
- **Rationale depth toggle**: store `rationaleShort` + `rationaleSimple` per MCQ; add `sources[]` and `highYield` flag to the FLY8 question type (src/core question model).
- **Bloom level + TOS tag** (`bloom`, `pnleArea`, `tosSection`) on each item; supports filters and weakness reporting. Fits existing `pnleAreaNames`.
- **Report-an-item** button (unclear / too long / wrong / other + note) saved locally and exportable; feeds the pending nurse-review loop (`nurseReviewed:false` stays visible; a local "RN-checked" badge only if the owner sets it herself).
- **Case-study item types**: SATA and cloze/matrix are MCQ-family and fit the owner's rule. Build "case set" = one vignette + 3-6 chained items (mcq, sata, cloze-dropdown). Do not copy the sample content.
- **Library-from-sources pipeline**: ticked notes + focus text + count produces MCQ draft, review/edit, then save; Refine (instruction on selected items, preview, apply/undo). Map onto FLY8 manual copy-paste prompt mode and Prompt Maker.
- **Option elimination** (strike-through), session resume, "random vs weak-first" modes, in-session "tagger" (Learning/Reviewing/Mastered) feeding mastery.
- **Exam-date driven plan**: exam date, countdown, daily goal ring, task-row states for the planned planner.
- **Resumable chunked import** with staging + atomic swap, for loading large preset/question packs without corrupting state (FLY8 already shards AsyncStorage; this adds resume).
- **Per-figure credit/licence** on images (FLY8 already does it for Wikimedia).
- **Explain-It**: "explain in your own words" graded by AI rubric; text only, via copy-paste prompt. Optional, not MCQ, so flag to owner.
- **Design tokens**: warm light theme, correct/incorrect colour pairs, 44 px tap targets, big tinted cards.

## 4. DO NOT REUSE
- Brand, name "Cleo", copy strings, mascot, crests, sticker art, fonts "AnthropicSerif", sample situations (e.g. the warehouse-collapse case), PNLE roster/badges and Hall of Fame (real people).
- Server-dependent: grading, rank math, matchmaking, energy/credit metering, notification manifest, subscription/IAP/PayMongo checkout, vouchers, analytics events (114+311 names), staff labs, rationale video/audio (premium TTS), AI-generated dummy-tier claims ("35% higher passing probability" is unsupported marketing).
- Social stack (friends, DMs, groups, battles, Townhall, calls): single-user app, out of scope.
- Screenshot protection, paywalls, energy gating.
- A wrong lesson from section 1.6: the sample "np5-gpt41-raw-output" shows unedited LLM output shipped as test data and has a practice_area/pnle_np mismatch. Keep FLY8's verification discipline.

## 5. Flashcard-related features (owner rejects flashcards) and MCQ-only replacements
| Tangerine feature | Where | MCQ-only equivalent |
|---|---|---|
| Flashcard materials in Library (front/back), "cards" kind | 2.5-2.6, lines 30,33 | Generate "questions" kind only; drop cards from kind picker |
| Folder flashcard study, ratings again/hard/learning/remembered, "due" scheduling | 2.9, line 46 | "Due" = MCQ re-ask queue: missed or hinted questions return after 1, 3, 7 days; a right answer pushes the next date out |
| 100 personal card cap, offline flashcard folders/packs, flashcard tour | 2.10, 1.2 | None needed |
| "Flashcards" task type in schedule | 1.5 | "Recall quiz" = 5 MCQs from earlier topics |
| Cleo-saved flashcards from chat | line 53 | "Save to question bank" from tutor: tutor turns a Q&A into a MCQ |
| flashcard_local_progress, edit_flashcard | 1.2, 2.9 | Per-question attempt log, edit-question sheet |
| Cloze/recall practice | case-study cloze | Cloze-dropdown MCQ (pick the missing term from 4) replaces fill-the-card recall |
| Explain-It | 2.9 | Not a flashcard but not MCQ; offer as optional "Explain" after a missed item, or replace by "which explanation is best" MCQ |

FLY8's own SM-2-lite/Focus score (src/srs) can be retargeted from cards to question IDs, so spaced repetition survives without flashcards.

## 6. CONFLICTS / overlaps with FLY8 Study
- Already have: PNLE system prompt and area tags, diagnostic gate, pop/module quizzes, tutor, lessons with credited images, points/streak, backup, preset notes/diagrams, manual copy-paste, Obsidian import. Tangerine's Library = FLY8's import + Prompt Maker (FLY8 is offline and keyless, a stronger fit).
- FLY8 module quiz is 5 types (typing, cloze, matching, ordering, choice); owner wants MCQ/SATA first, so de-emphasise typing/ordering.
- FLY8 has flashcards + deck list + Focus session in `FLY8 Study/src/srs`; these must be hidden or retargeted (see section 5).
- FLY8 gold items lack `bloom`, `highYield`, `sources`, `simplified rationale`; additive schema migration.
- Home dashboard and planner (planned) can adopt the schedule card and exam-date countdown.
- Tangerine's 2-tier verification (RN/topnotcher) conflicts with FLY8 honesty rule: nothing is nurse-reviewed, so use "AI-written / unreviewed" labels.

## 7. OPEN QUESTIONS for the owner
1. Include SATA and multi-step case sets (Tangerine-style) as a core practice mode, or only single-best MCQ at first?
2. Keep an optional "Explain it in your own words" (AI-graded by pasting into ChatGPT) even though it is not MCQ?
3. Should missed MCQs come back automatically on a spaced schedule (replacing flashcard spaced repetition), and are 1/3/7 days fine?
4. Do you want "report this question" + "RN checked by me" badges, so you can mark items after a real nurse or your instructor checks them?
5. Is an exam date + daily question goal + countdown on Home wanted in the first version, or after the planner?
