# PNLE Study Notes HTML — App Baseline and Requirements

> This file replaces the former NURSE GOD research-skill scope for this workspace. The filename is retained to avoid a disruptive rename; this document now governs the HTML app project.

## Source of truth
- Main artifact: [`PNLE Study Notes.html`](PNLE%20Study%20Notes.html)
- Baseline: the user-supplied HTML imported into this directory on 2026-10-23.
- Change rule: preserve the embedded notes and existing interactions unless an approved improvement explicitly requires a change.

## Observed baseline
Static inspection of the supplied source indicates:
- Document title: `PNLE Study Notes`.
- Single-file HTML with embedded CSS and JavaScript.
- Search input with placeholder “Search all notes”.
- Category/subject controls labelled All, NP1, NP2, NP3, NP4, NP5, and Core.
- Home/list view and note-detail view with back navigation.
- Studied-state UI and persistence through `localStorage` key `pnle-studied`.
- Theme toggle.
- Copy-note action.
- Hash-change routing.
- External Google Fonts stylesheet/preconnects; therefore, full offline independence is not established by source inspection.

These are source observations only. They do not establish that all controls work correctly in a browser.

## Goals
1. Make the app reliable on mobile and desktop.
2. Prevent note loss, incorrect studied-state behavior, and navigation dead ends.
3. Preserve all original note content and identifiers.
4. Improve search/filter correctness and empty/error states.
5. Improve keyboard and assistive-technology accessibility.
6. Keep rendering safe for note content; escape untrusted text and avoid unsafe HTML injection.
7. Make behavior testable and changes auditable without introducing unnecessary frameworks.
8. Keep the single-file delivery model unless the user authorizes a different architecture.

## Acceptance principles
- **Content integrity:** embedded note records before and after a code change must match except for an explicitly approved content change.
- **State integrity:** marking/unmarking notes persists predictably; invalid or unavailable storage degrades gracefully without crashing.
- **Navigation integrity:** opening a note, returning to the list, and browser back/forward behavior remain coherent.
- **Search/filter integrity:** search and category filters combine consistently, including no-result cases.
- **Accessibility:** interactive controls have clear names, keyboard access, visible focus, and state semantics.
- **Dependency transparency:** remote fonts or other remote assets are documented; do not claim full offline operation while remote assets remain.
- **Security:** no new unsafe HTML insertion, external data exfiltration, secrets, analytics, or unapproved network calls.
- **Truthful QA:** distinguish static inspection, automated checks, and actual browser/device testing.

## Feature request intake
When Gemini suggestions or user requests arrive:
1. Record each request as a candidate, not an automatic requirement.
2. Evaluate benefit, complexity, regression risk, privacy, offline behavior, and fit with existing app.
3. Group duplicates and identify conflicts.
4. Prioritize reliability/data safety before cosmetic additions.
5. Ask the user only when a decision materially changes scope or user-visible behavior.
6. Do not implement a suggestion merely because another AI recommended it.

## Current status
Baseline import is the first step. No feature request from Gemini has been evaluated in this iteration because its recommendations have not yet been supplied to this workspace. Runtime behavior and the embedded notes inventory still require explicit validation.
