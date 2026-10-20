# Question writer spec: original PNLE-style practice items for the offline question bank

You write ORIGINAL multiple-choice items for a Filipino nursing student preparing for the PNLE. The items ship inside the app (no AI needed to practise). They must test CLINICAL JUDGMENT grounded in the app's own study notes, not recall of trivia.

HARD RULES
- NEVER copy, recall or reconstruct real board-exam questions, review-center questions or textbook test banks. Invent your own patients and situations (Philippine settings welcome: barangay health center, district hospital, RHU, PHO).
- Ground every item in the note it belongs to: read that note's body in /home/user/readthischatgpt/FLY8 Study/data/presets/*.json (find by id). The correct answer and every distractor's rationale must follow from facts in the note or universally standard nursing knowledge. Do NOT build an item on any fact the note itself flags in its "Check against your textbook" section as doubtful or garbled, and never invent doses, lab ranges or statistics: ask about priority, sequence, interpretation, safety, teaching, delegation, or comparison instead of numbers unless the number is standard and in the note.
- Not advice for a real patient.

ITEM DESIGN (this is what makes them hard in the right way)
1. ONE decision point per item: 'what should the nurse do FIRST / is the BEST action / needs follow-up / is the PRIORITY / indicates teaching was effective / should the nurse report'. Cue -> pattern -> rank the options. Prefer 2-step reasoning (recognise what the cues mean, then choose the action).
2. Vignette of 2-4 sentences with the cues that matter plus 1 irrelevant detail. The vignette must NOT state or paraphrase the answer.
3. Four options, similar length and grammar. The correct option must NOT be the longest, most specific or most 'textbook-sounding'. No 'all of the above', 'none of the above', no 'always/never' giveaways, no two options that mean the same.
4. Each of the three distractors is a DIFFERENT named error type, chosen from: right action wrong time; right priority wrong client; treats the symptom not the cause; over-intervention; under-reaction/waiting; skips assessment; delegation outside scope; unsafe but common habit; true statement that does not answer the question; plausible detail from a similar condition. State the error type in that option's rationale.
5. The correct-answer position must be balanced across your file (about the same number of A, B, C, D).
6. Difficulty 1-5 (5 hardest). At least 60% of your items must be difficulty 4 or 5: a student who only memorised the note must fail, a student who reasons must pass. Include a few 3s; no 1-2.
7. 'reasoning' = a causal chain in numbered steps: cue -> physiology or principle -> priority rule (ABC, Maslow, safety, assess before act, least invasive first, unstable before stable, acute before chronic) -> why the key beats the runner-up -> one line starting 'Flip:' saying what single change in the scenario would make a different option correct.
8. strategyTag: short label of the deciding strategy, e.g. 'Maslow: oxygenation first', 'Assess before act', 'Unstable before stable', 'Scope of practice: delegate', 'Prevention level: screening = secondary'.

SCHEMA: output ONE JSON file (array) at the OUTPUT path. Each item exactly:
{"id":"<prefix>-<3 digit number>","noteId":"<preset note id the item is grounded in>","area":"NP1|NP2|NP3|NP4|NP5|GEN"(copy the note's area; GEN items may carry any area tag from the note),"topic":"<short topic name>","archetype":"<free short label of the reasoning pattern>","difficulty":4,"vignette":"...","stem":"...","options":["A text","B text","C text","D text"],"answerIndex":0-3,"strategyTag":"...","reasoning":"1. ...\n2. ...","optionRationales":["why A is right/wrong, naming the error type","...","...","..."],"keyFacts":["one or two facts from the note this item tests"],"aiWritten":true,"nurseReviewed":false}
Do not touch the repo or any other file. Write a small python checker outside the repo that verifies: valid JSON, unique ids, 4 options, answerIndex in 0-3, all fields present, noteId exists in the presets, answer positions balanced (each of 0-3 between 20% and 30%), the key option is the strictly longest in at most 20% of items, at least 60% difficulty >= 4, no item says 'all of the above'. Fix every complaint.

CHOOSE the notes: your list file has lines 'bundle | note id | title | priority'. Write about the number of items stated in your assignment, spread across ALL bundles in your list in proportion to size, favouring priority 'high' notes and notes about decisions, priorities, comparisons, complications and teaching. At most 3 items per note.
Final report under 80 words: count, difficulty mix, answer position counts, notes you avoided because of doubtful facts.

## CALIBRATION LESSONS (added after an independent Opus blind solve of our first 100 items: 99/100 keys matched but the reviewer rated mean difficulty 1.7/5 against labels averaging 3.7, and 19 items were 'recall-only', 10 'vignette-gives-away-answer', 2 'looks like a memorised board question')
Why items came out easy: (a) only one client or finding in the vignette was clearly unstable; (b) the key was the textbook-named 'classic' action (epiglottitis: do not examine the throat; previa: no vaginal exam; compartment syndrome: call provider); (c) the vignette restated the definition the answer needs ('compares findings with the goals' = evaluation).
To make a real difficulty 4-5 item:
1. All four options must be LEGITIMATE nursing actions that a competent nurse might do; the key is right because of one specific cue in THIS vignette, and each distractor would be right if a different cue were present.
2. Put TWO competing priority cues in the vignette (e.g. a new finding on a stable-looking client AND an expected abnormal finding on another) so ranking is needed; at difficulty 5 put three.
3. Do NOT use textbook-classic presentations or famous 'never do X' rules as the key; if the topic is a classic, vary the presentation (atypical onset, a complication layered on a stable condition, a post-procedure setting, a delegation twist) so recall of the classic rule alone is not enough, and never write an item that looks like a memorised board question.
4. The vignette must never restate the definition or keyword the answer depends on. Give raw cues (numbers, behaviours, quotes), not labels.
5. At most 1 item in 6 may be 'which client first'; vary shapes: what finding needs follow-up, what response shows teaching worked, what order, what to delegate to whom, what to report, what to clarify with the provider.
6. Rate difficulty honestly: 5 only if you yourself hesitated between two options for a while. A reviewer will re-solve blind and relabel; mislabelled items waste a round.
7. Do not write two items on the same concept inside your file (no more than one item per note unless the notes differ in decision type).
