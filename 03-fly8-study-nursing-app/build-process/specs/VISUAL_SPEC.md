# Visual author spec: schematic diagrams for nursing study notes

You draw SIMPLE, CLEAR, LABELLED SVG diagrams that sit above selected study notes in a phone app (FLY8 Study, PNLE nursing exam). They are schematics, not medical illustrations: flowcharts, decision trees, pyramids, timelines, comparison boxes, simple anatomy-style sketches built from basic shapes (e.g. heart chambers as rounded boxes with arrows, nephron as labelled stacked segments), body-region outlines made of simple shapes, scales, staircases, cycles.

Inputs: your list file (D/out3/notes-<id>.txt, lines "bundle | note id | title | priority") and the note texts in /home/user/readthischatgpt/FLY8 Study/data/presets/*.json (find a note by its id; read the body of each note you illustrate and draw ONLY what the note says; do not add facts that are not in the note).

Choose 12-18 notes from your list where a picture helps most (high priority first; processes, priority orders, classifications, comparisons, cycles, staging, pathways, algorithms). Skip notes where a picture adds nothing. One diagram per note.

HARD TECHNICAL RULES (the app sanitizes and drops anything else; test with the python checker below):
- A single <svg> root with viewBox="0 0 360 H" (width 360, H between 200 and 520). No width/height on the root.
- Allowed elements ONLY: svg, g, rect, circle, ellipse, line, polyline, polygon, path, text, tspan. Allowed attributes ONLY: x y cx cy r rx ry x1 y1 x2 y2 width height stroke-width opacity fill-opacity stroke-opacity font-size fill stroke d points transform viewBox stroke-dasharray stroke-linecap stroke-linejoin text-anchor font-weight. NO style, class, id, defs, marker, use, image, font-family, filter, gradient, url(). Arrowheads: draw them as small polygons.
- Max 20000 characters and max 250 elements per SVG; nesting depth max 8. Text content max 110 characters per <text>, plain letters/digits/punctuation, no & < >. Use several <text> elements for several lines. font-size 11-15, never below 11. Keep lines short (about 34 characters max per line at font-size 12 on a 360 wide canvas); never let text overflow its box.
- The app has light and dark themes and the SVG does not adapt: ALWAYS draw your own background first: <rect x="0" y="0" width="360" height="H" rx="12" fill="#FFFFFF"/> and use dark text fill="#1B2330". Use a restrained palette (indigo #4F46E5 / #ECEDFF for key steps, green #116D33 / #E3F4E8 for safe or correct, amber #A8530A / #FFF1DC for caution, red #BE1E2D / #FDE7E9 for danger or stop, grey #586078 / #EEF0F6 for neutral). Colour must never be the only cue: also label (number the steps, write STOP, etc.).
- Layout top to bottom, one column, large readable text, a bold title line at the top (font-size 15, font-weight bold). Boxes at least 28 high. Arrows between steps. Leave 12 px margins.

OUTPUT: one JSON file at the OUTPUT path: an array of {"noteId":"<id>","caption":"<one sentence saying what the diagram shows, plain words>","svg":"<the svg string>"}. Do not touch the repo or other files. Validate with this checker (copy it into your own scratch file outside the repo and run it on your output; fix every complaint):
python3 -I - <<'PY'
import json,re,sys
f=sys.argv[1]
ok={'svg','g','rect','circle','ellipse','line','polyline','polygon','path','text','tspan'}
attrs={'x','y','cx','cy','r','rx','ry','x1','y1','x2','y2','width','height','stroke-width','opacity','fill-opacity','stroke-opacity','font-size','fill','stroke','d','points','transform','viewBox','stroke-dasharray','stroke-linecap','stroke-linejoin','text-anchor','font-weight'}
for v in json.load(open(f)):
    s=v['svg']; p=[]
    if len(s)>20000: p.append('too long')
    tags=re.findall(r'<\s*([a-zA-Z]+)',s)
    if len(tags)>250: p.append('too many elements')
    p+= ['bad tag '+t for t in set(tags) if t not in ok]
    for a in set(re.findall(r'\s([a-zA-Z:-]+)\s*=',s)):
        if a not in attrs: p.append('bad attr '+a)
    for t in re.findall(r'>([^<]+)</text>',s):
        if len(t.strip())>110 or re.search(r'[&]',t): p.append('bad text: '+t[:30])
    if 'viewBox="0 0 360' not in s: p.append('viewBox')
    print(v['noteId'], 'OK' if not p else p)
PY
(run as: python3 -I /path/to/your_check.py output.json - put the code in a file and read sys.argv[1])

Final report under 80 words: number of diagrams, notes you chose, anything you could not draw well.
