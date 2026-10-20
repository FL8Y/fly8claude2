# Gap-closer spec (round 2): make the notes cover EVERYTHING in the handout

Context: round 1 already turned this handout slice into study notes for the FLY8 Study app (PNLE nursing exam). The owner wants EVERY topic in the handout covered, nothing skipped, and notes deep enough to study from. You close the gaps.

Inputs (all read-only): the handout text slice(s) you are given in D/src/; D/all_titles.txt (format "AREA | Title | id": every existing note); the existing note files in /home/user/readthischatgpt/FLY8 Study/data/presets/*.json (grep them); D/out2/cand-<group>.txt (heuristic list of handout headings that look unmatched; it is noisy: many are sub-headings or noise).

Steps:
1. Read the handout slice(s) fully. Build the list of real topics (diseases, drugs, procedures, programs, laws, theories, tables of facts, mnemonics).
2. For each topic, check whether the existing notes cover it in substance (grep titles and bodies in data/presets). Use the candidate file as hints.
3. Write NEW notes for every topic that is missing or only mentioned in a line or two. Also write a replacement for any existing note of YOUR OWN pack (listed below) that is clearly too thin for its handout topic: same id and title, richer body.
4. Handout tables and lists of facts (lab values, schedules, classifications, drug lists, stages) must not be lost: turn them into bullets inside the right note.

Writing rules (same as round 1): own words, never copy sentences, no review-center branding, no markdown tables, no H1, "##" sections: What it is / Must-know facts / Assessment / Priority nursing actions (priority order, say WHY: ABC, Maslow, safety, assess first) / Treatment and medications (general terms) / Complications and red flags / Client teaching / How the exam asks about it / Memory aid (only if genuinely useful) / Related topics / Check against your textbook (anything doubtful, garbled or beyond the handout). Add a short "## How to reason about it" section where it helps: cause -> finding -> priority action, and the trap the exam sets. Never invent doses, lab ranges or statistics: keep numbers only if the handout has them, and list doubtful ones under the check section. Not advice for a real patient. Do not recall real board-exam questions.

Schema: JSON array of {"id","area","title","body","tags":[],"priority":"high|medium|low","outlineRefs":[handout heading],"aiWritten":true,"nurseReviewed":false}. New ids: "<your prefix>-x-<slug>" and unique; new titles must not equal any title in all_titles.txt (case-insensitive). Replacement notes keep their existing id and title. [[Links]]: only to titles in all_titles.txt or your own new notes; every note should link to 2+ others. Validate JSON and links with a tiny python check before finishing.

Output: ONE file at the OUTPUT path (valid JSON array). Do not touch the repo or other files. Final report under 100 words: new notes count, replaced notes count, topics you judged already covered, anything missing from the handout text itself.
