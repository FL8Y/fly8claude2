# Lordahn's Tracker — Batch 17 checkpoint
Date: 2026-10-24

## Local artifact
- `Lordahns_Tracker_Audited.html`
- SHA-256: `8cefd50f5f53c9346611fe81a6335130593eb1718dbc9d766308e65304b01251`
- Size: 2,641,281 bytes.
- Validation passed: 1,000 notes, 1,000 unique IDs, no duplicates, 21 groups, original area/group counts and 141 SVG-bearing notes retained. HTML, embedded JSON and Node.js JavaScript syntax checks pass.

## Batch 17 dispositions
- GEM-144 — VALID ADDITION / APPLIED: the primary malaria note now includes smear microscopy/RDT and current local protocol-dependent treatment guidance, without a made-up dose or universal regimen.
- QA-005 — VERIFIED CORRECTION / APPLIED: removed a contradictory universal chloroquine quiz shortcut from the linked mosquito-comparison note, matching the primary malaria note and CDC Yellow Book 2026 destination-specific prevention guidance.
- GEM-145 — UNVERIFIED / BLOCKED: Kato-Katz and exact praziquantel administration claims remain source-limited; an in-note warning directs readers to the current programme/product protocol.
- GEM-146 — UNVERIFIED / APPLIED as a safety warning: added a prominent “historical handout—not a current prescribing regimen” notice to the legacy anti-TB dose/duration table. Actual regimen and doses remain unverified and must follow the current NTP and susceptibility-directed pathway.

## Source URLs
- WHO consolidated malaria guidance: https://www.who.int/teams/global-malaria-programme/guidelines-for-malaria
- CDC Yellow Book 2026 malaria guidance: https://wwwnc.cdc.gov/travel/yellowbook/2026/infections-diseases/malaria
- DOH NTP Manual of Procedures: https://ntp.doh.gov.ph/download/ntp-mop-6th-edition/
- WHO TB diagnostic guidance: https://www.who.int/publications/i/item/9789240107984

## Repository limitation
This progress checkpoint was committed. The updated standalone HTML itself has still not been pushed: the large existing HTML blob is not retrievable through the available GitHub fetch action, and direct Git network access is unavailable in the environment.