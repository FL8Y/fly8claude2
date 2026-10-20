# Digest 3: Tangerine Prep guide, last third (UI system, client rules, kit, take/limits)

## 1. What this source is
`tang/guide_part3.txt` (793 lines) is the tail of a 92-page reverse-engineering report on the Tangerine Prep Android app (React Native/Hermes decompile). It covers: the component library, per-screen layouts, motion/sound/haptics, accessibility, client-side functions (Part G), a "client kit" of original TypeScript, "what to take", and limits. The guide itself says the app was never run: UI comes from code and strings, not screenshots. Constants are labelled O (observed literal) or I (inferred). Scoring, adaptive selection, passing probability, ranking, planner rebalancing, AI prompts and content pipeline are server-side and NOT visible (guide sec. 8). The kit says its code is original, with 16 node tests passing, but "not run inside an app". Reasonably trustworthy for client constants, weak on anything algorithmic.

## 2. Key facts and designs
- Layout (sec. 3): Home = header chips (streak, energy, D-day countdown) -> big "Practice" hero card -> "Your Schedule" card -> "Study Tools" grid -> 6-tab bar. INF.
- Practice setup is a vertical list of mode cards (AI Exam, Custom, Adaptive, Mock Board, Coaching); online-only modes are disabled with "needs a connection" copy. OBS.
- Session player: header (back, title, timer 13/600 tabular, flag) -> progress bar -> stem card -> A-D option rows -> chunky Submit/Next -> inline correct/wrong + rationale panel, "Ask Cleo", report-a-problem sheet. A question-navigator sheet is a grid of 48x48 cells with legend Current/Answered/Not answered/Flagged.
- Option row spec (L327334): border 1.5, radius 10, 28x28 letter chip (A-D), 15/22 text. States: default, selected, eliminated (animated 2px strike-through, long-press toggle, UI-only, not persisted), correct (#15803D on #F0FDF4), wrong (#B91C1C on #FEF2F2) with solid chip plus check/x glyph. SATA, matrix and cloze variants exist (SataOptions, MatrixTable, ClozeSheet).
- Results screen: score ring + badge, per-subject bars, "Concepts to review" card, rationale summary, review grid, CTA "Retry weak topics". Stats screen: "Estimated passing chance" gated by "More evidence needed" plus an "About this estimate" explainer; 14-day accuracy line; "Topics, weakest first / Tap to practice".
- Plan calendar: month grid with today and exam-day markers; task statuses Scheduled / Needs attention / Due today / Up next / Completed; "learning path" to exam day.
- Design system: Duolingo-style "lip" buttons (face sinks on press, haptic default), hue tokens {base, lip, text}, white cards with warm border and lip, pill chips, 44px min tap. Hand-rolled ring/gauge/bar/line charts from react-native-svg with a data-table fallback; no chart lib, no Reanimated, no Lottie; own bottom sheets (Modal + Animated + PanResponder).
- Feedback: paired haptic+sound cues (selection, nextQuestion, correct/incorrect, warning) behind one `sounds_enabled` setting; central `useReducedMotion` hook; skeleton pulse 750 ms; confetti on good runs.
- Guide's own cautions: light-only theme (no dark mode), English-only UI, two overlapping palettes and 3-4 duplicate button implementations, colour-only states, gamification overload and 30+ launch modals for a high-stakes exam; recommends a quiet "focus mode".
- Rank ladder (O): 9 tiers Student II, Student I, Scholar III/II/I, Topnotcher III/II/I, Apex; stars 3,3,3,3,3,4,4,4,0; tier starts at points 0,4,8,12,16,20,25,30,35 (computed, I); season 90 days. Win/loss star rules are the kit author's own design, not observed. Battle/Versus is the social driver, so rank is battle-based.
- Streak: milestones [3,7,14,30,50,100,200,365] (O); celebration shown once per day; day boundary on a fixed UTC+8 clock (offset 28800000 ms, `msUntilNextManilaDay`). Streak count itself is server-side.
- Timers: `remainingSeconds` = total - floor((pausedAt ?? now) - startedAt), clamped, pause freezes it; 50 s/question estimate; battle clock-skew correction (250 ms tick).
- Labels (O): readiness >=85 "Board-ready momentum", >=75 "Almost there", >=60 "Needs reinforcement", else "Recovery mode"; case readiness 80/60; difficulty label 40/60/80; diagnostic buckets 40/60/80 (level names are the kit's); accuracy colours >=75 green, >=60 amber, else red; forecast needs >=5 answers overall, >=3 per subject. Bloom's six levels fold into recall / apply / judgment.
- Networking/offline (O): base-URL failover list; retry on network error and 502/503/504; 12 s timeout; 401 -> single-flight refresh once; identical in-flight GETs coalesced; backoff 500 ms doubling, cap 8 s; outbox outcomes sent (2xx) / retry (network, 408, 429, 5xx) / discard (other 4xx); 24 h prefetch interval and 24 h offline grace window; resumable chunked bundle download with version+index check; keep the old library if the new bundle has no answerable questions; sync status idle/syncing/success/failed re-checked every 60 s.
- Offline SQLite schema: questions, question_options (is_correct stored locally so grading works offline), local_sessions, local_attempts, outbox (UNIQUE kind+client_id), meta. Local score = correct / answered x 100.

## 3. REUSE (ideas, original code)
- `session.ts` pure functions (remainingSeconds with pause, estimateMinutes, m:ss format): drop-in for timed/mock-board mode. Skip skew functions.
- `labels.ts` thresholds and `hasEnoughEvidence` / `subjectHasEvidence` gate: a passing-readiness estimate shown only after enough answers; reword the labels in your own words.
- Day-boundary clock (`manila.ts`) in the owner's fixed timezone for streaks and daily goals; streak milestones.
- Rank ladder as a points -> tier/stars function (tierOf/starsOf/TIER_STARTS). Re-theme with original names; award stars for quiz performance (accuracy thresholds, streaks, flawless runs), not battles.
- Option-row design: letter chip, border state, strike-through elimination, check/x glyph in addition to colour. Single shared Option component with a SATA variant (checkbox semantics). Question-navigator sheet (current/answered/flagged). Segmented progress bar. Results layout (ring, subject bars, concepts to review, retry weak topics). Weakest-first topic list.
- Paired haptic+sound cue helper behind one toggle; central reduce-motion hook; skeleton pulse; honest-stats copy.
- Outbox concept ONLY if a backend/sync is ever added: `classify(status)` and `drain` are small and tested. Local attempts table (question id, selected option, correct, seconds, order) maps to FLY8's attempt log.
- Bloom folding (recall/apply/judgment) as a tag on MCQs; useful in rationale and weakness reports.

## 4. DO NOT REUSE
- Everything server-side or unknown: adaptive selection, passing probability, planner rebalancing, AI prompts, content pipeline; energy/lives, premium/IAP, entitlements, paywall sheets, telemetry, push, auth tokens, base-URL failover, token refresh, idempotency keys: no backend in FLY8, so none of the http.ts/keys.ts kit applies.
- Battles, Versus, Townhall, DMs, calls, mascot "Cleo", tangerine art, stickers, crests, sounds, copy and exact palette/fonts: proprietary brand; build original.
- Passer rosters / PNLE badge roster (personal data), cheat-sheet content, screen-capture protection.
- Flashcard helpers (`flashcards.ts`, flashcard tables): conflicts with the owner's NO flashcards decision.
- Shipping answer keys offline is a stated risk to the guide's app; for a single-user personal app it is acceptable and already how FLY8 works.
- Combo cap and forecast middle thresholds are placeholders in the kit, not real data.

## 5. CONFLICTS / overlaps with FLY8 Study
- FLY8 already has points/streaks, diagnostic, pop/module quizzes, planner (planned), lessons, tutor, bookmarks and search; Tangerine adds only presentation ideas for these.
- Rank ladder vs FLY8 levels/ranks (owner plan item 7): pick one scheme; the ladder can simply be the FLY8 rank table.
- Tangerine's readiness labels overlap FLY8 mastery/diagnostic bands (FLY8 mastery thresholds are not read here: UNKNOWN); reconcile to one scale.
- Dark mode: guide flags light-only as a flaw; FLY8's indigo new UI is unverified for dark mode (UNKNOWN).
- Offline: FLY8 is already local-first, so the 24 h window, prefetch and chunk download solve a problem FLY8 does not have (content ships with the app or is stored locally).
- FLY8's AI provider failover (free-AI pool, manual mode) is the analogue of the base-URL failover; the retry/timeout constants (12 s, retry set) are a reasonable template.
- Tangerine's NP1-NP5 chip colours match FLY8's NP1-NP5 area structure; a hue per NP area is a cheap scannability win.

## 6. OPEN QUESTIONS for the owner
1. Rank ladder: keep FLY8's current levels, or adopt a tiered ladder with stars earned from quiz results (no social battles)?
2. Add a readiness estimate ("board-ready" bands) shown only after enough answered questions, with an honest "not a prediction" note?
3. Timed mock-board mode (about 50 s per question, pause allowed) or untimed only?
4. Quiet "focus mode" (no confetti, sound or haptics) and dark mode as defaults for night study?
5. Do you want sound and haptic feedback at all, given the guide's overload warning?
