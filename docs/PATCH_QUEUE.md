# FLY8 patch queue

Fixes waiting for the next FLY8 patch, in the order they should go in.
Status: `queued` → `in progress` → `in build` → `verified on device` → `shipped`.

| ID | Status | Title | Target | Spec |
|---|---|---|---|---|
| PQ-001 | queued | Fresh account sign-up fails in Live view | Next patch | [PQ-001 spec](patch-queue/PQ-001_FLY8_Fresh_Account_Bug_Reference_Fix.docx) |

## PQ-001 · Fresh account sign-up fails in Live view

- **Reported:** 12 Oct 2026. A fresh account can be created in the reference build but not in FLY8.
- **Probable cause (not yet confirmed on a device):** when a clone in Live view opens a web
  link nothing in its space can handle, FLY8 sends it to its own in-app browser
  (`ActivityManagerCommonProxy.LiveBrowser.redirectStart` → `LiveBrowserActivity`). That browser
  wipes cookies and web storage on each fresh start, shares one cookie jar across clones,
  drops the clone's resultTo/requestCode and has no pop-up support. The reference build
  passes such links to the phone's browser instead.
- **Fix:** `fly8-additions-v2/13-web-links/WebLinkPolicy.kt` (compiles against FLY8
  1.0.0-beta01 classes, 0 errors).
  1. Setting "Open clone web links in phone browser" (default off) → hand the link to the
     phone's browser and close.
  2. "Open in phone browser" button in the FLY8 web page toolbar.
  3. Replace the `fresh` rule with `WebLinkPolicy.mustWipe(...)`, so one clone keeps its web session.
- **Before merging:** run the confirmation test (spec section 4). Try sign-up via
  "Open app" (no Live view); if it works there, the Live-view browser path is confirmed.
- **Done when:** a fresh account can be created in a Live-view clone with the setting on;
  clones still never see each other's web session; Lint clean.
- **Out of scope:** anything that changes how Roblox detects cloned environments.
