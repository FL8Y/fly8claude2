# Digest 7: Blueprint, master-plan indexes, preset-topic hierarchy

## 1. What these sources are
Planning documents (README, MASTER_LINKS_INDEX, PROJECT_MANAGER_BRIEF) plus the "App Master Plan Blue Print" folder (12 files, ~250 KB: requirements R-001..R-034, architecture, roadmap, DECISION_LOG, QA council, traceability) and `nursing preset topics/`. OBSERVED: they are planning text only, written before any build; most requirements are status "Planned"; nothing was run on a device. Trustworthy as intent and as a requirements checklist, not as current state. Stale in places: the DECISION_LOG in this copy ends at ADR-007/OPEN-007 and contains NO DEC-005 (the repo CLAUDE.md says DEC-005 authorized Expo; that entry is missing here), and the blueprint README still says "do not build yet".

## 2. Key facts
- Requirements table R-001..R-034: `App Master Plan Blue Print/PRODUCT_REQUIREMENTS_AND_UX.md` section 2. Groups: import/library/graph (R-001..003, 010), diagnostic/mastery/plan (R-004..006, 033, 034), lessons/media/tutor (R-007..009, 011, 020), quizzes (R-012..016), rewards/dashboard (R-017..019), AI key/errors/cost (R-021..023, 031), flashcards/SRS (R-024..026), persistence/privacy/a11y/clinical/test (R-027..030, 032).
- Learning-model section (PRD section 5): diagnostic is a strict gate (1-50 Qs) before teaching; pop quiz 1-5 Qs; module quiz uses a versioned coverage blueprint; dedupe by concept-family, not prompt text; separate evidence streams (diagnostic, pop, module, recall).
- Provisional mastery policy (PRD section 5): 30-day half-life recency weight 2^(-age/30); "Insufficient evidence" until >=5 distinct concept families over >=2 sessions; confidence is separate from accuracy; labelled provisional.
- Deterministic plan order (PRD section 5, R-034): safety/source flags, uncovered/prereqs, below-evidence floor, lowest recency-weighted score, oldest success, stable topic ID. AI may explain but not override.
- Rewards: idempotent events, no duplicate points on retry (R-017/018); timezone-safe streaks.
- Feedback rule: rationale per option; unsupported facts labelled "AI-added / needs review", never a fake citation (PRD section 5, R-030).
- Architecture: ADR-001 one canonical content model; ADR-002 append-only attempt/review events; ADR-003 schema + semantic validation of AI output; ADR-005 BYO-key threat model (`TECHNICAL_ARCHITECTURE_AND_CONTRACTS.md` sections 4-5, 13). Section 8 has the conceptual data model; section 11 the mastery/reward contracts.
- Blueprint baseline stance (ADR-007/OPEN-002): Kotlin + Jetpack Compose, native Android, local-first, no backend. PM brief section 7 lists Expo vs native as an open owner decision; repo CLAUDE.md says Expo was chosen.
- PM brief section 2/3: Tangerine Prep taxonomy NP1-NP5 > practice area > topic > TOS section; item types MCQ, SATA, matrix, highlight, cloze with partial credit; modes custom/AI-curated/mock/adaptive/case study; diagnostic plus passing-probability. Scoring/adaptive/SRS/AI are server-side with unknown formulas (OBSERVED by that research).
- PM brief section 4: reference code already written and unit-tested: `App 4.../product/practice-engine` (partial-credit scoring mcq/SATA/matrix/cloze, Rasch adaptive selection, readiness, timezone streaks, energy cap; 10/10 tests), `App 3.../product/ai-next-quiz` (schema validator, buffer/refill; never run live), `app 2.../product/srs-scheduler` (FSRS-5; 10/10), `apkg-builder`, `ankidroid-bridge` (never run on device). Content Authoring Kit validates the PNLE question-maker prompt rules (11/11 tests).
- PM brief section 6 risks: AnkiDroid GPL-3/AGPL; Gizmo and Tangerine proprietary (ideas only); clinical accuracy needs expert review; Tangerine bundles ~28.6k exam-passer names (do not take).
- Preset hierarchy (OBSERVED, `nursing preset topics/Anki Deck Topic Hierarchy`, "PNLE TOP 1 (By Vinz)"): 7 subjects, 637 nested topic dirs per README (my count: 645 dirs including the root and 7 subject dirs, 1003 files). It is a folder scaffold; each subject has a RESEARCH TOPIC GUIDE, RESEARCH LOG and OBSIDIAN MIND MAP (research outputs not verified here). `question maker prompt/` holds the PNLE research-standard prompt (sections A-D+: concepts, assessment, management, nursing reasoning).

## 3. REUSE
- Requirements R-004..R-020, R-027..R-033 as the acceptance checklist, with "flashcard" wording replaced by MCQ.
- Mastery policy, deterministic plan ordering, concept-family dedupe, event-ledger idea (ADR-002), idempotent rewards: all stack-neutral and match FLY8's diagnostic/points/streak work.
- `practice-engine` (TypeScript, same language as FLY8): partial-credit SATA scoring, Rasch adaptive selection, readiness/passing-probability, timezone streak. Direct fit for MCQ + SATA + "Tangerine-like" mock/adaptive exam. Re-implement or port with tests; it is the owner's own code.
- `ai-next-quiz` validator ideas for rejecting bad generated MCQs.
- NP1-NP5 > area > topic > TOS taxonomy as the question tag schema (FLY8 already has `pnleAreaNames`).
- `Content Authoring Kit` validator for question-prompt rules, if it fits FLY8 gold-item format.

## 4. DO NOT REUSE / now obsolete
- R-024, R-025, R-026 (flashcards, spaced repetition, weak cards recur sooner), R-019 "due reviews", journey 3.6 "Review flashcards", ADR-004, OPEN-005 (FSRS), ADR-006 (AnkiDroid interop), `srs-scheduler`, `apkg-builder`, `ankidroid-bridge`, App 2 research. Owner says no flashcards. Weak-topic resurfacing should become MCQ re-selection (the mastery/plan engine already does this), not card scheduling.
- Server-side assumptions (PM brief: "keep scoring and AI keys on the server", "build our own backend"): conflicts with local-first/no-backend and zero-cost owner.
- Monetisation/pesos tiers, social/battles/leaderboards (P4), ~28.6k passer names, any Tangerine text/art/branding.
- GPL/AGPL AnkiDroid source.

## 5. CONFLICTS with FLY8 Study and with new owner direction
- Flashcards/FSRS/Anki: blueprint treats them as core (R-024..026, P2). FLY8 built SM-2-lite cards plus "Focus score"; owner now says no flashcards. Remove or hide; keep the Focus idea only as an MCQ weak-spot selector.
- Stack: blueprint baseline Kotlin/Compose; FLY8 is Expo/React Native + TypeScript. Keep Expo (already built, 200+ tests, owner vibe-codes); mark the blueprint ADR-007/OPEN-002 superseded and add the missing DEC-005 entry.
- Offline-first/no backend: consistent with FLY8. Blueprint wants secure key storage and a "threat model" (R-022); FLY8 owner said security is not a concern, so keep a light version only.
- BYO paid API key (R-021): superseded by free-provider pool + manual copy-paste. R-023/R-031 (error handling, rate limits) still apply and matter more with free tiers.
- Blueprint says "implementation not authorized / do not build" (README, DEC-003): stale.
- Quiz counts: pop quiz 1-5, diagnostic 1-50 match FLY8. Tangerine-like needs a mock board exam, adaptive exam, timed mode, SATA, case studies: not in blueprint requirements beyond item types; FLY8 has no SATA or partial credit yet.
- Blueprint requires import of user material (R-001), Obsidian-style graph (R-010): FLY8 has both; the owner direction may deprioritise them.

### Mapping 636 topics to FLY8's 21 bundles and NP1-NP5 (INFERRED; I did not see FLY8's 21 bundle list or the 14-pack names beyond CLAUDE.md)
Top-level subject counts (dirs incl. nested): 01 Community Health 78 (CHN/PHC 29, Elements of PHC 47, Mental Health Program 1); 02 Fundamentals 23 (Oxygenation 6, Theorists 4, Specimen Collection 4, Aging 3, 5 singles); 03 Med-Surg 296 (MSK 46, Emergency/Disaster 42, Cancer 31, Special Senses 26, Renal 24, Endocrine 21, Neuro 21, Respiratory 16, GI 15, Periop 15, Immunology 11, Hematology 10, Fluids/Acid-Base 9, Cardiovascular 8); 04 OB 45 (Complications 14, Pregnancy 14, Others 8, Labor 7, Postpartum 1); 05 PALMER 81 (Research A 28, Research B 33, Leadership 10, Professional Adjustment 9); 06 Pediatrics 73 (Growth & Dev 13, Renal 10, CV 9, GI 8, Neuro 8, Cancers 7, Heme 5, Integumentary 5, Resp 4, MSK 3); 07 Psychiatric 47 (Parts 1-3 and Extra, uninformative names).
Proposed mapping to NP areas: 01 -> NP1 (Community Health); 04 OB + 06 Pediatrics Growth & Dev -> NP2; Med-Surg splits NP3 (Periop, Respiratory, Cardiovascular, Hematology, Immunology, Fluids/Acid-Base, Cancer, Renal for fluids) and NP4 (GI, Endocrine, Neuro, Special Senses, MSK); Pediatric systems follow the matching adult system (NP3/NP4) or NP2; 07 Psychiatric + Emergency/Disaster -> NP5; 02 Fundamentals, 05 PALMER (Leadership, Professional Adjustment, Research) -> cross-cutting "GEN" like FLY8's laws/management/research and fundamentals bundles. Use the 636 as a topic-level tag tree under each bundle (deepest-leaf = topic id), not as separate bundles. Caveats: "PALMER" is an unexplained acronym; "Part 1/2/3" for psych need inspection; unverified that these map one-to-one onto FLY8's 974 notes.

## 6. Open questions for the owner
1. Confirm flashcards are fully removed (including the Focus score and due-card count on Home), or kept as a hidden "review mistakes" MCQ mode?
2. Should the app add a timed mock board exam, adaptive exam and passing-probability estimate like Tangerine, and is SATA with partial credit required in v1?
3. Use the 636-topic "PNLE TOP 1" hierarchy as the app's official topic tree (tags under the 21 bundles), or keep FLY8's current bundles only?
4. Keep Expo/React Native (blueprint says Kotlin) and record this formally as DEC-005 plus a "no flashcards/MCQ-first" decision?
5. Keep the diagnostic gate (must test before teaching) strict, or optional, in the Tangerine-like flow?
