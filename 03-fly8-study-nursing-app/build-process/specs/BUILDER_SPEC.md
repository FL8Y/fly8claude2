# Builder spec: PDF text -> preset notes (read fully, then work)

You are converting ONE slice of a nursing review handout (plain text, already extracted) into study notes for the FLY8 Study app (Philippine nursing licensure exam, PNLE). Write notes in YOUR OWN WORDS: condense, reorganise, explain why. Never copy sentences, never keep the review center's branding/headers/footers/ads, never mention a review center. Facts must come from the text (or be well-established textbook knowledge); if the handout looks wrong or you add something it lacks, say so in a "## Check against your textbook" section. Never invent doses, lab ranges or statistics: if a number is in the handout keep it, and list it under the check section if you doubt it.

## Output
One JSON file (an array) at the OUTPUT path you are given. Do NOT touch the git repo or any other file. Valid JSON only (escape quotes, use \n inside strings). Validate with: python3 -I -c "import json,sys;json.load(open(sys.argv[1]))" <file>.

Each note:
{"id":"<pack>-<slug>","area":"NP1|NP2|NP3|NP4|NP5|GEN","title":"<unique, specific>","body":"<Markdown>","tags":["..."],"priority":"high|medium|low","outlineRefs":["<handout heading it came from>"],"aiWritten":true,"nurseReviewed":false}

- id: your pack prefix + slug, unique. title: unique, specific, no "Part 2" style names; titles must not equal any title in existing_titles.txt.
- area: use the area rule in your assignment. GEN = runs through all five exams (fundamentals, pharmacology, laws/management/research).
- One note per exam-sized concept (a disease, a drug class, a procedure, a program, a law, a developmental stage...), roughly 180-450 words; split big chapters into several notes, merge tiny ones. Cover EVERYTHING in your slice: every disease/topic/heading in the handout must appear in some note (list them in outlineRefs). Skip pure filler.
- body = Markdown, no H1. Use "##" sections that fit the topic, e.g. What it is / Must-know facts / Assessment findings / Priority nursing actions (in priority order, say why: ABC, Maslow, safety, assess first) / Treatment and medications (general terms) / Complications and red flags / Client teaching / How the exam asks about it / Memory aid (only if genuinely useful, say if made up) / Related topics / Check against your textbook. Drop sections that do not apply. NO markdown tables (use bullets), no HTML, no images. Mnemonics from the handout may be kept only if short and widely known.
- Priority: high = frequently tested or life-threatening.
- Links: use [[Exact Note Title]] to connect notes. Link ONLY to titles that exist in your own file or in existing_titles.txt (same folder as this spec; format "AREA | Title"). Every note should link to 2+ others where sensible. A link may use [[Title|shown text]]. Check every link resolves before finishing (write a tiny python check).
- This is exam study support, not advice for a real patient. Do not copy or recall real board-exam questions.

## Finish
Report in under 120 words: file path, number of notes, total words, handout headings you deliberately skipped, any facts you doubted. Nothing else.
