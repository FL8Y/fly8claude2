# Lordahn's Tracker — Batch-by-Batch Source Verification Ledger

- Source HTML SHA-256: `3e3a51a02916e4a5b3c01d2cfdae6fb9c0d0b17f456cc02cb901e9cdcf0080b5`
- Output HTML SHA-256: `b2b0e01980b59e86fbc10c9d5c69da5550f4c1014a718412ca47bd52f4bc483d`
- Baseline: 1000 notes; 1000 unique IDs; 21 groups.
- Accountability: 74/74 = **100.0%**.
- Independently evidence-checked: 33/74 = **44.6%**.
- Implementation: 29/29 approved/eligible changes applied or confirmed unnecessary = **100.0%**.
- Source limitation: exact textbook pages, external master-plan/source-index files, and selected technical DOH/laboratory manuals were not attached or page-verified; affected claims remain explicitly unverified/blocked.

## Batch 1

Processed findings: 4. Applied: 0. Rejected: 0. Already correct: 2. Awaiting evidence/blocked: 0.

### GEM-001 — Pinggang Pinoy adult food-plate proportions
- **Report location:** Report pp. 10–11, master table row np1-nutrition
- **HTML destination:** `np1-nutrition; np1-health-promotion`
- **Original claim:** “Current national food-guide model” needs checking.
- **Proposed correction:** Keep adult plate as ~½ Glow, ~⅓ Go, ~⅙ Grow plus water; do not apply adult proportions to every age group.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing nutrition and health-promotion notes already give the proportions and link the official FNRI-DOST explanation. The report’s proposed correction is present; textbook pages were not attached.
- **Gemini source cited:** DOST-FNRI Pinggang Pinoy; CHN Sumile 3rd Ed. 2024 named by report
- **Independent sources:** https://www.fnri.dost.gov.ph/index.php/programs-and-projects/news-and-announcement/183-fnri-dost-launches-
- **Final disposition:** No text patch needed; existing note retains official link.
- **Second-pass review:** PASSED

### GEM-002 — Health Belief Model vs Pender Health Promotion Model
- **Report location:** Report pp. 10–11, master table row np1-nutrition
- **HTML destination:** `np1-health-promotion; gen-fa-health-models`
- **Original claim:** Report flags model details as varying by book.
- **Proposed correction:** Distinguish HBM constructs from Pender HPM constructs without reducing either to a slogan or claiming all editions are identical.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing health-promotion note separates HBM susceptibility/severity/benefits/barriers/cues (and sometimes self-efficacy) from Pender prior behaviour, benefits/barriers, self-efficacy, affect and influences. Primary textbooks were inaccessible.
- **Gemini source cited:** Kozier & Erb / CHN Sumile named by report; HBM and Pender theory summaries used for concept check
- **Independent sources:** https://www.ncbi.nlm.nih.gov/books/NBK606120/; https://nursingtheory.org/theories-and-models/pender-health-promotion-model
- **Final disposition:** Existing note is sufficiently qualified; no change needed.
- **Second-pass review:** PASSED

### GEM-003 — Three-level vs five-stage prevention framework
- **Report location:** Report pp. 10–11, master table row np1-three-levels-of-prevention
- **HTML destination:** `np1-three-levels-of-prevention; np1-x-epi-concepts`
- **Original claim:** Some books group disability limitation with secondary prevention; others place limiting disability with tertiary.
- **Proposed correction:** Preserve both recognized classroom mappings and follow the model explicitly named by the question/reference.
- **Verdict / implementation:** `CONFLICTING EVIDENCE` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The report itself describes different mappings. The HTML explicitly distinguishes the five-stage Leavell–Clark model from three broad nursing levels and warns against claiming one mapping is universal.
- **Gemini source cited:** Maglaya 5e / Kozier & Erb named by report; DOH Omnibus Health Guidelines (2023) cited by report
- **Independent sources:** https://openstax.org/books/population-health/pages/15-3-theories-and-models
- **Final disposition:** Keep the existing framework note and caveat; no universal mapping imposed.
- **Second-pass review:** PASSED

### GEM-004 — ELEMENTS mnemonic for Philippine PHC
- **Report location:** Report pp. 11–12, master table row np1-elements-phc
- **HTML destination:** `np1-elements-of-phc; np1-phc-elements`
- **Original claim:** Exact labels/order and whether treatment includes emergency services are flagged as uncertain.
- **Proposed correction:** Teach the common ELEMENTS eight-service mnemonic, but distinguish course mnemonics from verbatim WHO/LOI statutory wording.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing notes list the mnemonic but already say labels/grouping vary and do not present it as a universal verbatim WHO list. The attached report did not provide page-numbered textbook excerpts.
- **Gemini source cited:** Maglaya 5e / CHN Sumile named by report; LOI 949; Alma-Ata Declaration
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/34/26121
- **Final disposition:** Existing qualification meets the evidence; no additional claim added.
- **Second-pass review:** PASSED

## Batch 2

Processed findings: 4. Applied: 3. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 1.

### GEM-005 — TB initial diagnostic test
- **Report location:** Report pp. 12–13, master table row np1-endemic-diseases—Tuberculosis
- **HTML destination:** `np1-cd-tb-dx; np3-cr-tb-diagnosis; np1-endemic-diseases`
- **Original claim:** “Sputum AFB smear is confirmatory”; outdated generic testing workflow.
- **Proposed correction:** Where indicated by the current NTP algorithm, use a WHO-recommended rapid molecular NAAT (e.g., Xpert MTB/RIF/Ultra) as the initial diagnostic test; smear/culture have roles but an AFB smear alone should not be taught as universally confirmatory.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Official DOH/WHO sources support rapid molecular diagnosis in the applicable diagnostic algorithms. The inaccurate “AFB smear: confirmatory” wording was replaced; regimen selection remains deferred to DOH NTP algorithms.
- **Gemini source cited:** DOH NTP Manual of Procedures 6th ed. (2021); WHO consolidated TB guidance (2025)
- **Independent sources:** https://ntp.doh.gov.ph/download/ntp-mop-6th-edition/; https://tbksp.who.int/sites/default/files/2025-08/9789240110991-eng.pdf
- **Final disposition:** Applied to NP1 and NP3 diagnostic notes with linked official sources.
- **Second-pass review:** PASSED

### GEM-006 — TB specimen count, culture and regimen categories/durations
- **Report location:** Report pp. 12–13, master table row np1-endemic-diseases—Tuberculosis
- **HTML destination:** `np1-endemic-diseases; np1-cd-tb-tx (search); np3-cr-tb-drugs`
- **Original claim:** Report specifies Xpert, two-specimen DSSM within three days, culture gold standard, abolishment of Category II and exact Category I/Ia durations.
- **Proposed correction:** Do not hard-code every regimen detail until the current NTP table for disease site, susceptibility, prior treatment and patient group is checked page-by-page.
- **Verdict / implementation:** `PARTIALLY VALID` / `PENDING EVIDENCE`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The general need to follow NTP testing/regimen algorithms is verified, but the precise specimen timing and all category/duration assertions were not independently page-checked against a current full regimen table. Existing legacy schedules are explicitly caveated rather than silently promoted.
- **Gemini source cited:** DOH NTP Manual of Procedures 6th ed. (2021) cited by report; WHO TB guidance (2025)
- **Independent sources:** https://ntp.doh.gov.ph/download/ntp-mop-6th-edition/; https://tbksp.who.int/sites/default/files/2025-08/9789240110991-eng.pdf
- **Final disposition:** Exact dose/regimen claims remain pending full current NTP table verification; retained only as legacy review text with a strengthened warning.
- **Second-pass review:** PASSED

### GEM-007 — Dengue clinical case classification
- **Report location:** Report pp. 12–13, master table row np1-endemic-diseases—Dengue Fever
- **HTML destination:** `np1-cd-dengue; np1-cd-x-mosquito-compared; np1-endemic-diseases`
- **Original claim:** Legacy “toxic/hemorrhagic stage” classification.
- **Proposed correction:** Use dengue without warning signs, dengue with warning signs, and severe dengue; describe febrile, critical and recovery as clinical phases rather than severity categories.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The official cited AO supports the three-category classification; the uploaded HTML already used it in a general endemic note but retained old stage labels in two dedicated notes, which were corrected.
- **Gemini source cited:** DOH AO 2012-0006, Revised Dengue Clinical Case Management Guidelines
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/11/50697
- **Final disposition:** Updated affected dengue and mosquito-comparison notes; source linked adjacent to text.
- **Second-pass review:** PASSED

### GEM-008 — Dengue “Herman’s sign is pathognomonic”, fixed 20% hematocrit threshold and testing overclaims
- **Report location:** Report pp. 12–13 and embedded infographic page 9; row np1-endemic-diseases—Dengue
- **HTML destination:** `np1-cd-dengue; np1-cd-x-mosquito-compared; embedded source image on report p. 9`
- **Original claim:** The source table/graphic repeats “Herman’s sign” and older fixed markers as diagnostic shorthand.
- **Proposed correction:** Do not label the rash pattern pathognomonic; select NS1/NAAT/IgM testing by illness day and use CBC/hematocrit trends with clinical assessment rather than a universal single-number diagnostic rule. Avoid routine rapid fluid replacement and unsubstantiated ice-bag advice.
- **Verdict / implementation:** `PARTIALLY VALID` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The warning-sign/classification correction is supported; “dark foods” has no cited clinical basis and is rejected separately as GEM-035. The dedicated dengue notes contained overconfident diagnosis and treatment shorthand; these were replaced with cautious, clinically contextual wording.
- **Gemini source cited:** DOH AO 2012-0006; current clinical testing principles. The image itself also states “Avoid Aspirin & Dark Foods.”
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/11/50697
- **Final disposition:** Applied correction to the disease note and comparison note; unsupported food restriction was not copied.
- **Second-pass review:** PASSED

## Batch 3

Processed findings: 5. Applied: 1. Rejected: 0. Already correct: 3. Awaiting evidence/blocked: 1.

### GEM-009 — Maternal mortality indicator denominator and multiplier
- **Report location:** Report p. 11, row np1-vital-statistics & np1-x-epi-concepts
- **HTML destination:** `np1-vital-statistics`
- **Original claim:** Report says MMR is a ratio but also gives ×1,000 “or” ×100,000.
- **Proposed correction:** For the WHO MMR indicator, use maternal deaths / live births ×100,000 and call it a ratio; keep local textbook label “rate” only as a terminology note, not a different multiplier for the same indicator.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The existing note uses ×100,000 live births and explains ratio-vs-rate terminology. It explicitly rejects treating ×1,000 and ×100,000 as interchangeable.
- **Gemini source cited:** WHO indicator metadata; DOH FHSIS Manual named by report
- **Independent sources:** https://www.who.int/data/gho/indicator-metadata-registry/imr-details/26
- **Final disposition:** No edit needed; WHO-supported formula is already present.
- **Second-pass review:** PASSED

### GEM-010 — FHSIS form names and reporting schedule
- **Report location:** Report p. 11, row np1-vital-statistics & np1-x-epi-concepts
- **HTML destination:** `np1-vital-statistics; np1-x-epi-concepts`
- **Original claim:** ITR, TCL, Summary Tables and monthly/quarterly consolidation M1/M2/Q1/Q2 are asserted.
- **Proposed correction:** Verify exact current form labels and reporting schedule against the applicable DOH FHSIS manual version before adding operational detail.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The source manual/version was not supplied and the report does not give page/table references. These current operational labels were not inserted as certified facts.
- **Gemini source cited:** DOH FHSIS Manual of Operations; Maglaya 5e named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** No change; source access/page verification needed.
- **Second-pass review:** PASSED

### GEM-011 — RA 9262 protection-order duration and issuer
- **Report location:** Report p. 13, row np1-vawc
- **HTML destination:** `np1-vawc`
- **Original claim:** BPO/TPO/PPO durations and issuers flagged for verification; report adds a 24-hour Family Court assertion.
- **Proposed correction:** BPO = 15 days; TPO = 30 days; PPO remains until revoked by court; do not add a blanket 24-hour Family Court deadline absent text support.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note gives the statutory durations and explicitly says §§14–16 do not state a general Family Court 24-hour rule.
- **Gemini source cited:** RA 9262 §§14–16, LawPhil
- **Independent sources:** https://lawphil.net/statutes/repacts/ra2004/ra_9262_2004.html
- **Final disposition:** Existing statutory wording retained; incorrect 24-hour claim excluded.
- **Second-pass review:** PASSED

### GEM-012 — Child-protection law RA 7610 vs Local Government Code RA 7160
- **Report location:** Report pp. 5–8 and 13, correction to TOS law number and row np1-vawc
- **HTML destination:** `np1-vawc; NP2 legal-topic search anchor`
- **Original claim:** Report flags RA 7160 as an incorrect pediatric child-protection citation.
- **Proposed correction:** Use RA 7610 for the Special Protection of Children Against Abuse, Exploitation and Discrimination Act; RA 7160 is the Local Government Code.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The uploaded VAWC note already cites RA 7610 and links LawPhil; no app note correction was required. The separate spreadsheet mentioned in the report was not supplied.
- **Gemini source cited:** RA 7610, LawPhil; report identifies tracker typo
- **Independent sources:** https://lawphil.net/statutes/repacts/ra1992/ra_7610_1992.html
- **Final disposition:** In-app legal correction already present; external TOS workbook not changed because it was not supplied.
- **Second-pass review:** PASSED

### GEM-013 — RA 11648 statutory-rape threshold
- **Report location:** Report p. 13, row np1-vawc
- **HTML destination:** `np1-vawc`
- **Original claim:** Report states threshold raised to under 16 years.
- **Proposed correction:** Explain the under-16 rule with the statutory close-in-age exception (age difference not more than 3 years; consensual, non-abusive and non-exploitative) and exclusion for victims under 13.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The statutory amendment states the general under-16 threshold and the narrow close-in-age exception, which does not apply when the victim is under 13. The existing note warned to check exceptions but did not state them.
- **Gemini source cited:** RA 8353 as amended by RA 11648 (2022), LawPhil
- **Independent sources:** https://www.lawphil.net/statutes/repacts/ra2022/ra_11648_2022.html
- **Final disposition:** Added a compact statutory qualification and direct link to the legal text.
- **Second-pass review:** PASSED

## Batch 4

Processed findings: 7. Applied: 1. Rejected: 0. Already correct: 2. Awaiting evidence/blocked: 2.

### GEM-014 — Water supply Level I/II/III numerical criteria
- **Report location:** Report p. 14, master table row “np1-approved-water-toilet” / water supply specs
- **HTML destination:** `np1-water-toilet; np1-x-water-toilet-specs`
- **Original claim:** Numbers for household counts, reach, yield and faucet ratio vary among reviewer texts.
- **Proposed correction:** Keep as examination/review conventions with explicit caveat; do not present numbers as universal current engineering specifications without page-checking the governing implementation source.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Both water/toilet notes already label figures as legacy review material and state that current DOH/local criteria were not certified.
- **Gemini source cited:** PD 856; Maglaya 5e and CHN Sumile named by report
- **Independent sources:** https://lawphil.net/statutes/presdecs/pd1975/pd_856_1975.html
- **Final disposition:** No replacement of numerical material; existing boundary caveats preserved.
- **Second-pass review:** PASSED

### GEM-015 — Two-minute rolling boil
- **Report location:** Report p. 14 and embedded infographic p. 9, water boil time
- **HTML destination:** `np1-water-toilet; np1-x-water-toilet-specs; source infographic p. 9`
- **Original claim:** Report graphic and text state rolling boil for two minutes.
- **Proposed correction:** Do not promote the two-minute claim as universal unless current Philippine public-health guidance supports it; leave it clearly marked legacy/unverified.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The HTML already marks the boiling time as legacy material and explicitly notes that advice differs by source/altitude. No authoritative current Philippine technical rule was page-verified.
- **Gemini source cited:** PD 856; report invokes DOH Four Rights but gives no precise authoritative page reference
- **Independent sources:** https://lawphil.net/statutes/presdecs/pd1975/pd_856_1975.html
- **Final disposition:** Not changed; not promoted to established fact.
- **Second-pass review:** PASSED

### GEM-016 — Toilet-facility Level I/II/III grouping
- **Report location:** Report p. 14, water/toilet specifications
- **HTML destination:** `np1-water-toilet; np1-x-water-toilet-specs`
- **Original claim:** Report assigns non-water-carriage and selected low-water toilets to I, septic tank/vault carriage to II, and sewerage/treatment connection to III.
- **Proposed correction:** Retain as a textbook classification with a caveat because source grouping and implementation details need current local sanitary verification.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The notes already preserve the exam convention and warn that groupings differ among sources; exact current implementation criteria remain unverified.
- **Gemini source cited:** PD 856 and community-health textbooks named by report
- **Independent sources:** https://lawphil.net/statutes/presdecs/pd1975/pd_856_1975.html
- **Final disposition:** No universal standard claimed; caveat remains.
- **Second-pass review:** PASSED

### GEM-017 — Environmental statute numbers and years
- **Report location:** Report p. 14, environmental health laws row
- **HTML destination:** `np1-x-environmental-health-laws; np1-waste-food-laws`
- **Original claim:** PD 856, RA 6969, RA 8749, RA 9003 and RA 9275 listed with years/titles.
- **Proposed correction:** Confirm statutes by primary law; maintain correct numbers/titles without adding unsupported detail.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note lists the five statute numbers and names consistently. This audit did not change law text or infer additional color-coding rules from the statutes.
- **Gemini source cited:** LawPhil statutes; CHN Sumile/Maglaya named by report
- **Independent sources:** https://lawphil.net/statutes/presdecs/pd1975/pd_856_1975.html
- **Final disposition:** No in-app correction needed for the listed law identifiers.
- **Second-pass review:** PASSED

### GEM-018 — DOH healthcare-waste bag/container color system
- **Report location:** Report p. 14, environmental health laws row
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** Report gives specific black/green/yellow/yellow-black/orange/red mappings.
- **Proposed correction:** Do not hard-code the entire color scheme as official until the exact relevant table in the cited current DOH Health-Care Waste Management Manual is checked.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The uploaded note explicitly says those mappings were not independently verified against a specific current manual table. The actual manual file/page was not supplied or opened in this session.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** No color table added; existing uncertainty notice retained.
- **Second-pass review:** PASSED

### GEM-019 — Annual FECT stool screening for food-handler health certificates
- **Report location:** Report p. 14, np1-x-food-safety row
- **HTML destination:** `np1-x-food-safety`
- **Original claim:** AO 2006-0001 directs annual stool testing and FECT rather than direct fecal smear.
- **Proposed correction:** Retain the annual FECT statement with the actual official AO link adjacent to the text; do not add unsupported specimen-preparation minutiae.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Official AO states stool examination is mandatory for food handlers applying for health certificates, FECT is the procedure of choice, and examinations occur once a year. The existing note paraphrased the order but had no link directly beside the sentence.
- **Gemini source cited:** DOH AO 2006-0001, Supreme Court E-Library
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/10/54970
- **Final disposition:** Added direct AO link next to the FECT claim.
- **Second-pass review:** PASSED

### GEM-020 — Food cooking and holding temperatures
- **Report location:** Report p. 14, food safety row
- **HTML destination:** `np1-x-food-safety; np1-waste-food-laws`
- **Original claim:** Report gives 70°C cooking/reheating; cold ≤10°C or hot ≥60°C and max two hours at room temp.
- **Proposed correction:** Prefer WHO Five Keys values: thoroughly cook (70°C benchmark), refrigerate preferably below 5°C, keep hot food >60°C, and do not leave cooked food at room temperature >2 h; older ≤10°C cold-hold limit is not the preferred WHO benchmark.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing food-safety note already has the WHO benchmark and warns that 10°C is an older limit. It links the WHO poster.
- **Gemini source cited:** WHO Five Keys to Safer Food poster; DOH AO 2006-0001 for FECT only
- **Independent sources:** https://www.who.int/docs/default-source/food-safety/five-keys-to-safer-food-poster/5keys-en.pdf
- **Final disposition:** No change needed to temperature values; keep sources distinct from the AO FECT claim.
- **Second-pass review:** PASSED

## Batch 5

Processed findings: 8. Applied: 1. Rejected: 2. Already correct: 2. Awaiting evidence/blocked: 1.

### GEM-021 — Sentrong Sigla origin, cited administrative orders and four pillars
- **Report location:** Report p. 15, np1-sentrong-sigla row
- **HTML destination:** `np1-sentrong-sigla`
- **Original claim:** Report attributes four pillars and founding history to DOH AO 29 s.1999 and AO 45-B s.2000.
- **Proposed correction:** Do not state the four-pillar list/history as official until a source that actually establishes it is obtained; the cited E-Library document is titled PMAC Policy.
- **Verdict / implementation:** `UNVERIFIED` / `NOT NEEDED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The current note explicitly marks the claimed four pillars/founding attribution/validity as unresolved and says the cited AO 45-B page is a PMAC policy document, not proof of the claim.
- **Gemini source cited:** DOH AO 29 s.1999 / AO 45-B s.2000 cited by report; E-Library item inspected
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/10/40839
- **Final disposition:** Do not import the claimed pillars; existing unresolved notice retained.
- **Second-pass review:** REVIEWED FOR SAFETY; CLAIM REMAINS UNVERIFIED; NOT PROMOTED INTO NOTES.

### GEM-022 — Lymphatic filariasis organisms; Loa loa confusion
- **Report location:** Report p. 15, np1-cd-filariasis row
- **HTML destination:** `np1-cd-filariasis`
- **Original claim:** Report says Philippine lymphatic filariasis is caused by W. bancrofti and B. malayi, not African eye worm Loa loa.
- **Proposed correction:** Keep W. bancrofti and B. malayi as the commonly taught Philippine organisms; explicitly distinguish globally recognized B. timori where relevant and do not identify Loa loa as local LF.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note correctly distinguishes local teaching from global species and explicitly says Loa loa causes loiasis, with the WHO source linked.
- **Gemini source cited:** WHO lymphatic filariasis fact sheet; CHN Sumile named by report
- **Independent sources:** https://www.who.int/news-room/fact-sheets/detail/lymphatic-filariasis
- **Final disposition:** Existing note is sufficient; local vector details left cautious.
- **Second-pass review:** PASSED

### GEM-023 — Local vector species, nighttime diagnosis, and fixed MDA dose/month
- **Report location:** Report p. 15, np1-cd-filariasis row
- **HTML destination:** `np1-cd-filariasis`
- **Original claim:** Report asserts Aedes poecilus / Anopheles minimus flavirostris, testing windows and DEC + albendazole dose annually in November.
- **Proposed correction:** Verify current Philippine program circular and eligibility before inserting vector-by-location or MDA dosing/schedule as universal.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The WHO source supports basic disease facts but not all precise local vectors, collection hours, DEC dose and campaign timing. The DOH implementation document was not available for page-level confirmation; the HTML prudently avoids a fixed dose.
- **Gemini source cited:** DOH National Filariasis Elimination Program/AO 2018-0016 cited by report; WHO fact sheet
- **Independent sources:** https://www.who.int/news-room/fact-sheets/detail/lymphatic-filariasis
- **Final disposition:** No fixed MDA schedule or local vector assertion added.
- **Second-pass review:** PASSED

### GEM-024 — Routine isolation for leptospirosis
- **Report location:** Report p. 15, np1-cd-lepto row
- **HTML destination:** `np1-cd-lepto`
- **Original claim:** Report says remove “isolate patient”; use Standard Precautions.
- **Proposed correction:** Use Standard Precautions/PPE appropriate to body-fluid exposure; do not recommend routine strict isolation absent an additional indication.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note already says standard precautions are generally appropriate and routine strict isolation is not generally required.
- **Gemini source cited:** CHN Sumile / Understanding Pathophysiology named by report; standard infection-control practice
- **Independent sources:** https://pia.gov.ph/news/doh-warns-against-self-medicating-with-doxycycline/
- **Final disposition:** No change needed for isolation wording.
- **Second-pass review:** PASSED

### GEM-025 — Risk-tier doxycycline prophylaxis dose table
- **Report location:** Report p. 15, np1-cd-lepto row
- **HTML destination:** `np1-cd-lepto`
- **Original claim:** Report assigns single-dose, 3–5-day, and weekly 200 mg regimens by flood-exposure category.
- **Proposed correction:** Reject fixed self-directed dosing as a universal instruction; prophylaxis depends on current public-health guidance and patient/exposure assessment.
- **Verdict / implementation:** `REJECT` / `REJECTED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** A fixed risk-tier table was not independently corroborated in a current official clinical guideline and could be misapplied. The HTML uses individualized clinician/public-health assessment and does not give a self-medication dose.
- **Gemini source cited:** DOH public warning on doxycycline self-medication; PIA 2025 republished DOH statement
- **Independent sources:** https://pia.gov.ph/news/doh-warns-against-self-medicating-with-doxycycline/
- **Final disposition:** Risk-tier dosing not copied into study notes; cautious clinical wording retained.
- **Second-pass review:** PASSED

### GEM-026 — Rabies epidemiological “sporadic” label and 14-day animal observation
- **Report location:** Report p. 15, np1-cd-rabies row
- **HTML destination:** `np1-cd-rabies`
- **Original claim:** Report says Philippine CHN texts call rabies sporadic and dog/cat observation is 14 days.
- **Proposed correction:** Use local epidemiologic data for “sporadic”; do not treat animal-observation duration as universal across WHO and Philippine protocols. Never delay human exposure assessment/PEP.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The HTML distinguishes WHO general 10-day dog/cat observation from applicable local Philippine protocol, instructs that PEP assessment must not wait, and avoids asserting a universal epidemiologic label.
- **Gemini source cited:** RA 9482; DOH AO 2018-0013 cited by report; WHO rabies guidance
- **Independent sources:** https://www.who.int/news-room/fact-sheets/detail/rabies; https://lawphil.net/statutes/repacts/ra2007/ra_9482_2007.html
- **Final disposition:** Existing cautious note retained; exact current local observation protocol remains jurisdiction-dependent.
- **Second-pass review:** PASSED

### GEM-027 — “Patients should not be bathed / no running water visible” as blanket advice
- **Report location:** Report p. 15 and expanded section; rabies clinical management
- **HTML destination:** `np1-cd-rabies`
- **Original claim:** Report explains hydrophobia and treats avoidance of water cues as a directive.
- **Proposed correction:** Reject as general rabies-exposure first-aid advice; distinguish stimulus minimization in a clinically managed symptomatic patient from care for an otherwise well bite victim.
- **Verdict / implementation:** `REJECT` / `REJECTED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The report wording risks confusing symptomatic hydrophobia management with bite first aid. The existing note explicitly states this is not routine advice for bite victims.
- **Gemini source cited:** WHO rabies first-aid/PEP guidance
- **Independent sources:** https://www.who.int/news-room/fact-sheets/detail/rabies
- **Final disposition:** Rejected as a general nursing instruction; no change to first-aid advice.
- **Second-pass review:** PASSED

### GEM-028 — Wound washing, RA 9482 and animal observation
- **Report location:** Report p. 15 and embedded infographic p. 9, rabies
- **HTML destination:** `np1-cd-rabies`
- **Original claim:** Report says immediate wound washing and cites RA 9482; embedded graphic does not show this item.
- **Proposed correction:** Prioritize immediate 15-minute washing with soap/running water and urgent ABTC assessment; tie legal background to RA 9482 and do not delay PEP while observing an animal.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note gives the WHO 15-minute first-aid recommendation. Added direct RA link to keep the statutory anchor traceable beside the local-law topic.
- **Gemini source cited:** WHO rabies guidance; RA 9482
- **Independent sources:** https://www.who.int/news-room/fact-sheets/detail/rabies; https://lawphil.net/statutes/repacts/ra2007/ra_9482_2007.html
- **Final disposition:** Added direct RA 9482 link; WHO first-aid wording already present.
- **Second-pass review:** PASSED

## Batch 6

Processed findings: 12. Applied: 2. Rejected: 1. Already correct: 1. Awaiting evidence/blocked: 2.

### GEM-029 — Pediatric age-group boundaries vs Piaget/Kohlberg stages
- **Report location:** Report p. 16, developmental boundaries row np2-pd-tof
- **HTML destination:** `np2-pd-age-theories; np2-pd-gd-principles`
- **Original claim:** Report says overlapping 2–3, 4–7, 7–10, >12 values belong to Piaget/Kohlberg sub-stages rather than standard age groups.
- **Proposed correction:** Separate broad pediatric age-group bands from theory-stage ages; note that exact boundaries differ by source and the listed theory ages are approximate, not diagnostic cutoffs.
- **Verdict / implementation:** `PARTIALLY VALID` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The actual note gave conflicting preschool and school-age ranges. It was normalized to a common nursing teaching convention while explicitly flagging variation between editions and keeping Piaget/Kohlberg ages separate.
- **Gemini source cited:** Silbert–Flagg Maternal & Child Health Nursing named by report (PDF not supplied); common pediatric age-group convention
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** Updated internally inconsistent age-group labels with a qualified convention; textbook-specific differences still flagged.
- **Second-pass review:** PASSED

### GEM-030 — Tetralogy of Fallot defects and acute Tet-spell response
- **Report location:** Report p. 16, np2-pd-tof row
- **HTML destination:** `np2-pd-tof`
- **Original claim:** Report states four defects and prioritizes knee-chest to increase systemic vascular resistance.
- **Proposed correction:** Keep the four defects and knee-chest/squat positioning as the core exam concept; acute management also needs calm, oxygen and escalation/ordered care.
- **Verdict / implementation:** `ALREADY CORRECT` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing note correctly lists pulmonary stenosis, RV hypertrophy, overriding aorta and VSD, and prioritizes knee-chest positioning. The source textbook PDF was inaccessible; no unsupported medication order was added.
- **Gemini source cited:** StatPearls/NCBI and AHA/AAP cardiac guidance named by report
- **Independent sources:** https://www.ncbi.nlm.nih.gov/books/NBK513288/
- **Final disposition:** No change needed to the core TOF teaching.
- **Second-pass review:** PASSED

### GEM-031 — Enhanced TOS subject-domain mapping
- **Report location:** Report pp. 5–7, structural realignment with Enhanced TOS
- **HTML destination:** `App area/group structure; NP1–NP5 navigation; official TOS source`
- **Original claim:** Report states BON Res. 10 s.2025 maps NP1 to community health; NP2 to mother/adolescent and growth/development; other NP domains cover specialties.
- **Proposed correction:** Use the official PRC Enhanced TOS as the controlling source for curriculum-domain labels; do not reassign the entire existing 1,000-note bank without a note-by-note mapping pass.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Official PRC resolution is accessible and supports a new enhanced TOS. The uploaded HTML has 21 groups and explicit NP assignments; changing all note areas would be a separate curriculum mapping project, not justified by this clinical-fix report alone.
- **Gemini source cited:** PRC Enhanced TOS, BON Resolution No. 10 s.2025
- **Independent sources:** https://www.prc.gov.ph/sites/default/files/2025-10%20%20published%20Enhanced%20TOS%20nuse.pdf
- **Final disposition:** Recorded in audit ledger; no bulk note reassignment performed.
- **Second-pass review:** PASSED

### GEM-032 — 2026 PNLE calendar shift
- **Report location:** Report p. 7, structural realignment
- **HTML destination:** `Footer/audit source section; PRC 2026 calendar`
- **Original claim:** Report says the calendar transitioned from May/November cycle to February/August sittings under PRC Resolution 2113 s.2025.
- **Proposed correction:** Check against current official PRC schedule; include explicit 2026 dates only as date-specific context, not a permanent rule.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The current PRC schedule listed Nurses on February 26–27 and August 29–30, 2026 at time of verification. Exam dates can be revised by further advisories and do not belong in static clinical notes.
- **Gemini source cited:** PRC Resolution No. 2113 s.2025, 2026 schedule
- **Independent sources:** https://prc.gov.ph/2026-schedule-examination
- **Final disposition:** Logged as verified context; not hard-coded into study notes.
- **Second-pass review:** PASSED

### GEM-033 — RA 9173 §15 passing threshold and repeat-subject rule
- **Report location:** Report p. 7, Nursing Act pass rule
- **HTML destination:** `Audit/legal context (not a clinical note); RA 9173 source`
- **Original claim:** Report states GWA ≥75%, no NP subject below 60%; a <60 result despite overall 75% requires retaking only the failed subject(s).
- **Proposed correction:** Use statutory wording: ≥75% general average, no subject below 60%; if the general average is ≥75% but a subject is <60%, repeat only the subject(s) below 60 and obtain ≥75% in each repeated subject.
- **Verdict / implementation:** `VERIFIED CORRECTION` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The official text of §15 confirms the general-average floor, per-subject floor and repeated-subject rule. It is relevant to exam administration but not a clinical note correction.
- **Gemini source cited:** RA 9173 §15, LawPhil
- **Independent sources:** https://lawphil.net/statutes/repacts/ra2002/ra_9173_2002.html
- **Final disposition:** Documented; no change to note content.
- **Second-pass review:** PASSED

### GEM-034 — Report’s claimed 974-note corpus vs uploaded file
- **Report location:** Report pp. 1–8 vs supplied HTML baseline
- **HTML destination:** `Uploaded HTML data block; all note IDs/groups`
- **Original claim:** Report repeatedly treats 974 notes as the original collection.
- **Proposed correction:** Use the uploaded HTML as baseline; record actual note count, IDs, groups and counts without replacing it with the older repo HTML or deleting notes.
- **Verdict / implementation:** `CONFLICTING EVIDENCE` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The supplied app has 1,000 notes, 1,000 unique non-empty IDs and 21 groups. The report describes an earlier 974-note corpus and is not a reliable structural baseline for this newer file.
- **Gemini source cited:** Direct count of supplied HTML versus report narrative
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** No note deletion/reversion. Starting baseline is the uploaded 1,000-note HTML.
- **Second-pass review:** PASSED

### GEM-035 — Dengue infographic “Avoid Aspirin & Dark Foods”
- **Report location:** Embedded image on report p. 9; not a table row
- **HTML destination:** `Embedded image page 9; np1-cd-dengue`
- **Original claim:** The embedded infographic pairs aspirin avoidance with a “dark foods” restriction.
- **Proposed correction:** Reject “avoid dark foods” as unsupported clinical advice; retain medication cautions only where authoritative dengue guidance supports them.
- **Verdict / implementation:** `REJECT` / `REJECTED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The graphic is a non-native image inside the source DOCX. Its “dark foods” instruction is not included in the app and was not imported.
- **Gemini source cited:** The graphic cites DOH AO 2012-0006 for dengue classification but gives no source for “dark foods”; the AO/WHO materials reviewed do not establish this as a standard recommendation.
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/11/50697
- **Final disposition:** No unsupported diet restriction added.
- **Second-pass review:** PASSED

### GEM-036 — Long-note length/15-heading schema and dual-source requirement
- **Report location:** Report pp. 1–4 and 17–35, repository governance/expanded-note spec
- **HTML destination:** `All notes; report’s “00 MASTER PLAN v2.2” external Drive references`
- **Original claim:** Report describes mandatory 3,500–9,000 characters per topic, parser limit 12,000, and strict 15-heading disease template.
- **Proposed correction:** Do not rewrite all notes to this schema unless the master plan/schema files are available and the task is authorized as a corpus expansion; supplied HTML is a functioning 1,000-note app with many short concept notes.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The stated parser ceiling and exact heading schema could not be verified because the cited master plan, merge tool and source index were not supplied. Forcing a corpus-wide expansion would risk truncation and erase current content patterns.
- **Gemini source cited:** Report cites external Drive master plans and source index, not attached files
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** No bulk rewrite; architecture claim remains blocked by unavailable reference files.
- **Second-pass review:** PASSED

### GEM-037 — Proposed long-form TB note with detailed regimen and clinical specifics
- **Report location:** Report pp. 17–25 and expanded note 1, Tuberculosis
- **HTML destination:** `np1-endemic-diseases; np1-cd-tb-dx; np1-cd-tb-tx/search; np3-cr-tb-diagnosis; np3-cr-tb-drugs`
- **Original claim:** Report proposes a large structured TB disease note with regimen/dosing and operational guidance.
- **Proposed correction:** Do not paste the whole expanded disease note without page-level review against a current DOH regimen table and the named textbook editions.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** Only the diagnostic claim was patched from public DOH/WHO evidence. The complete proposed 15-heading expansion contains more details than could be independently verified against all claimed shelf sources in this pass.
- **Gemini source cited:** CHN Sumile 3rd Ed. 2024 and Maglaya 5e named by report but not attached; DOH NTP MOP accessible only at landing/PDF source
- **Independent sources:** https://ntp.doh.gov.ph/download/ntp-mop-6th-edition/; https://tbksp.who.int/sites/default/files/2025-08/9789240110991-eng.pdf
- **Final disposition:** Full expansion not imported; individual unresolved regimen claims stay qualified.
- **Second-pass review:** REVIEWED FOR SAFETY; CLAIM REMAINS UNVERIFIED; NOT PROMOTED INTO NOTES.

### GEM-038 — Proposed long-form dengue note and fluid-management detail
- **Report location:** Report pp. 25–30 and expanded note 2, Dengue
- **HTML destination:** `np1-cd-dengue; np1-cd-x-mosquito-compared`
- **Original claim:** Report proposes a full dengue chapter including signs, thresholds, fluids and drug/nursing management.
- **Proposed correction:** Integrate only the diagnosis/classification and immediate safety corrections supported by sources; do not bulk-insert all dose/rate details without a current clinical guideline review.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** The core correction was applied directly to existing dengue notes. The remainder of the proposed long-form note is not needed for this correction batch and was not comprehensively verified to current dosing/monitoring details.
- **Gemini source cited:** DOH AO 2012-0006; full textbook PDFs cited by report were not attached
- **Independent sources:** https://elibrary.judiciary.gov.ph/thebookshelf/showdocs/11/50697
- **Final disposition:** Do not duplicate the full proposed chapter; existing note corrected in place.
- **Second-pass review:** PASSED

### GEM-039 — Proposed long-form VAWC/child-protection expansion
- **Report location:** Report pp. 30–31 and expanded note 3, VAWC/statutory care
- **HTML destination:** `np1-vawc`
- **Original claim:** Report proposes detailed VAWC law and nursing-response material.
- **Proposed correction:** Keep supported statutory content and add the missing RA 11648 exception; do not import unsupported timing/legal assertions or duplicate the current detailed note.
- **Verdict / implementation:** `PARTIALLY VALID` / `NOT NEEDED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** Existing VAWC note already includes protection-order durations, child-protection statute and nursing priorities. The 24-hour assertion was excluded and the statutory-rape exception was added; wholesale replacement is not needed.
- **Gemini source cited:** RA 9262 §§14–16, RA 7610, RA 11648; full textbook PDF not attached
- **Independent sources:** https://lawphil.net/statutes/repacts/ra2004/ra_9262_2004.html; https://lawphil.net/statutes/repacts/ra1992/ra_7610_1992.html; https://www.lawphil.net/statutes/repacts/ra2022/ra_11648_2022.html
- **Final disposition:** Supported details integrated; unsupported expansion claims excluded.
- **Second-pass review:** PASSED

### GEM-040 — Legacy weekly chloroquine as universal malaria prophylaxis in related comparison note
- **Report location:** Second-pass cross-check prompted by report’s locally endemic disease corrections
- **HTML destination:** `np1-cd-x-mosquito-compared`
- **Original claim:** The connected mosquito-borne disease note says “chloroquine prophylaxis weekly” and treats it as a fixed exam cue.
- **Proposed correction:** Add a warning that antimalarial prevention depends on destination, resistance, patient factors and current advice; do not memorize weekly chloroquine as universal.
- **Verdict / implementation:** `OUTDATED` / `APPLIED`
- **Verification status:** INDEPENDENTLY VERIFIED
- **Evidence review:** This was a related high-risk outdated medication claim found during the required neighboring-note/second-pass review. Current prophylaxis recommendations vary by destination and drug resistance; the note now says to follow current advice.
- **Gemini source cited:** CDC malaria prophylaxis guidance; adjacent updated np1-endemic-diseases text
- **Independent sources:** https://www.cdc.gov/malaria/travelers/drugs.html
- **Final disposition:** Removed the unqualified “chloroquine prophylaxis weekly” shorthand from the actual note. It now states that prophylaxis is destination-specific and chloroquine is appropriate only where local guidance confirms sensitivity and recommends it.
- **Second-pass review:** PASSED after exact stale sentence was removed; CDC travel guidance link retained.

## Batch 7

Processed findings: 5. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 5.

### GEM-041 — TB sputum specimen storage temperature and maximum storage interval
- **Report location:** Report section “Expanded Note 1: Pulmonary and Extrapulmonary Tuberculosis,” diagnostic data
- **HTML destination:** `np1-cd-tb-dx; np3-cr-tb-diagnosis; np3-cr-tb-drugs`
- **Original claim:** The expanded note states that unprocessed specimens may be stored at 2–10 °C for up to 7 days.
- **Proposed correction:** Confirm the exact specimen type, transport medium, temperature and duration against the current DOH/NTRL collection-and-transport manual before teaching a numeric storage limit.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The cited specimen handling guidance was not available for line/page-level inspection; the exact universal temperature/time rule was not confirmed. No numeric storage rule was inserted.
- **Gemini source cited:** NTRL specimen collection/storage manual and DOH NTP MOP named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; preserve existing general specimen-collection caution until the precise official protocol is verified.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-042 — CSF specimen handling for suspected tuberculous meningitis
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” diagnostic data
- **HTML destination:** `np3-cr-tb-diagnosis; np3-cr-tb-drugs`
- **Original claim:** The report says CSF for TB culture must never be refrigerated.
- **Proposed correction:** Verify specimen-specific handling with the receiving laboratory and current official TB specimen-transport instructions; do not generalize without a primary source.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** This is a high-risk, specimen-specific instruction and the referenced manual was not supplied or directly inspected.
- **Gemini source cited:** NTRL specimen collection/storage manual named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; not added to the clinical notes.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-043 — Gastric aspirate collection cut-off and processing interval
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” diagnostic data
- **HTML destination:** `np3-cr-tb-diagnosis; np3-cr-tb-drugs`
- **Original claim:** The report says gastric aspirates must reach the reference laboratory before 2:00 PM and be processed within 4 hours of early-morning collection.
- **Proposed correction:** Verify the exact schedule and receiving laboratory rule with a current primary specimen-collection manual before including fixed clock-time requirements.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact 2:00 PM cut-off and four-hour interval were not independently confirmed from the cited manual.
- **Gemini source cited:** NTRL specimen collection/storage manual named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no exact transport deadline added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-044 — Mantoux test technique and wheal/read timing
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” diagnostic data
- **HTML destination:** `np3-cr-tb-diagnosis; np1-cd-tb-dx`
- **Original claim:** The report specifies 0.1 mL of 5 TU PPD, a 6–10 mm wheal, and reading at 48–72 hours.
- **Proposed correction:** Verify the exact test-product technique and reading window against an authoritative current diagnostic source; distinguish administration technique from interpretation thresholds.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** Exact administration details were not page-checked in the named textbooks or a current product/protocol source.
- **Gemini source cited:** Kozier & Erb/CHN Sumile named by report; TB diagnostic guidance
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the app keeps the general distinction between exposure testing and active-disease diagnosis.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-045 — Mantoux induration cut-offs by risk group
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” diagnostic data
- **HTML destination:** `np3-cr-tb-diagnosis; np1-cd-tb-dx`
- **Original claim:** The report gives ≥10 mm for immunocompetent hosts and ≥5 mm for HIV-positive clients, and interprets a positive result as prior exposure/latent infection rather than active disease.
- **Proposed correction:** Do not import a two-threshold-only rule without current risk-stratified guidance; test cut-offs vary by population/risk and a positive TST does not alone diagnose active TB.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact local threshold table was not confirmed and the listed groups do not exhaust all risk-based interpretation categories.
- **Gemini source cited:** TB diagnostic guidance and the named nursing textbooks cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; retain existing caution that TST indicates infection/exposure, not proof of active TB.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 8

Processed findings: 3. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 3.

### GEM-046 — Exact airflow range for negative-pressure TB isolation rooms
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” medical management
- **HTML destination:** `np3-cr-tb-diagnosis; np3-cr-tb-drugs`
- **Original claim:** The report prescribes 6–12 air exchanges per hour plus HEPA filtration.
- **Proposed correction:** Use applicable current facility-engineering and infection-prevention standards for the room type; do not present one airflow range as a universal requirement without the governing specification.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The numeric range was not validated against the current Philippine facility or infection-control standard during this pass.
- **Gemini source cited:** Kozier & Erb/DOH infection-prevention guidance referenced by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no engineering threshold inserted into the study note.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-047 — Exact 6-month Category I drug-susceptible TB regimen scope
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” pharmacologic care
- **HTML destination:** `np1-endemic-diseases; np3-cr-tb-drugs`
- **Original claim:** The report states 2HRZE/4HR for 6 months for new or retreatment PTB and most EPTB.
- **Proposed correction:** Do not hard-code a regimen as universal; verify current DOH algorithm by disease site, prior treatment, resistance results, age/pregnancy and other patient factors.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The broad need for algorithm-based treatment is clear, but exact current applicability and scope were not verified against all current regimen tables.
- **Gemini source cited:** DOH NTP MOP 6th ed. (2021), WHO TB guidance; textbook sources named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED pending a current, page-level NTP regimen review; the existing note has an explicit algorithm caveat.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-048 — Exact 12-month CNS/bone/joint EPTB regimen
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” pharmacologic care
- **HTML destination:** `np1-endemic-diseases; np3-cr-tb-drugs`
- **Original claim:** The report states 2HRZE/10HR for 12 months for TB of the CNS, bones or joints.
- **Proposed correction:** Confirm disease-site-specific duration in the current Philippine NTP regimen table before asserting a fixed 12-month schedule.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The precise indication and regimen were not checked against a current complete regimen table.
- **Gemini source cited:** DOH NTP MOP 6th ed. (2021) and WHO TB treatment guidance named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no fixed CNS/bone/joint duration was newly added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 9

Processed findings: 6. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 6.

### GEM-049 — Drug-resistant TB regimen durations
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” pharmacologic care
- **HTML destination:** `np3-cr-tb-drugs; np1-endemic-diseases`
- **Original claim:** The report states SSTR for 9–11 months or CTR for 20–24 months.
- **Proposed correction:** Avoid presenting these durations as current universal options without checking the latest eligibility-based drug-resistant TB protocols.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** Contemporary drug-resistant TB regimens and eligibility have changed; the two duration ranges were not verified against current guidance.
- **Gemini source cited:** WHO and DOH TB treatment guidance named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no regimen-duration shortcut added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-050 — Rifampicin red-orange body-fluid discoloration and contact-lens staining
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” RIPE toxicity mnemonic
- **HTML destination:** `np3-cr-tb-drugs`
- **Original claim:** The report states rifampicin turns urine, sweat, saliva and tears red-orange and may permanently stain soft contact lenses.
- **Proposed correction:** Verify the exact official product-label counseling wording before importing the contact-lens permanence claim.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact counseling details and permanent-staining wording were not checked against the current product label.
- **Gemini source cited:** Official rifampicin prescribing information was not supplied with the report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; not added as an asserted drug-label warning.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-051 — Rifampicin induction of hepatic enzymes and hormonal contraceptive interaction
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” RIPE toxicity mnemonic
- **HTML destination:** `np3-cr-tb-drugs`
- **Original claim:** The report states rifampicin induces CYP450 enzymes and inactivates oral contraceptives.
- **Proposed correction:** Check the current official drug label and contraception interaction advice before making a categorical claim about every oral contraceptive or contraceptive method.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The specific interaction and scope across contraceptive formulations were not page-checked in the current label.
- **Gemini source cited:** Official rifampicin prescribing information was not supplied with the report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no detailed interaction table added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-052 — Isoniazid pyridoxine supplementation dose
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” RIPE toxicity mnemonic
- **HTML destination:** `np3-cr-tb-drugs`
- **Original claim:** The report specifies pyridoxine 10–50 mg/day to manage or prevent peripheral neuropathy.
- **Proposed correction:** Do not provide one universal B6 dose without checking current NTP protocol, indication and patient-specific risk.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The dosage range was not verified against current local protocol and individual risk indications.
- **Gemini source cited:** Official isoniazid prescribing information and current NTP regimen guidance not supplied for page-level check
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no fixed B6 dose added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-053 — Pyrazinamide hepatotoxicity and hyperuricemia/gout
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” RIPE toxicity mnemonic
- **HTML destination:** `np3-cr-tb-drugs`
- **Original claim:** The report presents hepatic toxicity and urate retention/gout as pyrazinamide adverse effects.
- **Proposed correction:** Keep adverse-effect advice tied to an authoritative drug reference and do not convert a possible adverse effect into a diagnosis or automatic discontinuation rule.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The drug-label statement and monitoring instruction were not independently checked; the expansion was not imported.
- **Gemini source cited:** Official pyrazinamide prescribing information was not supplied for this pass
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no new adverse-effect table added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-054 — Ethambutol optic neuritis and under-six contraindication
- **Report location:** Report section “Expanded Note 1: Tuberculosis,” RIPE toxicity mnemonic
- **HTML destination:** `np3-cr-tb-drugs`
- **Original claim:** The report states ethambutol causes dose-dependent optic neuritis and is contraindicated in children under 6.
- **Proposed correction:** Verify current product labeling and the pediatric specialist/NTP guidance before stating an absolute age contraindication.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The optic-toxicity caution is plausible, but the absolute under-six contraindication needs product- and protocol-specific verification.
- **Gemini source cited:** Official ethambutol prescribing information and current pediatric NTP guidance not supplied for page-level check
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no absolute age contraindication added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 10

Processed findings: 5. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 5.

### GEM-055 — Hematocrit rise of ≥20% described as pathognomonic
- **Report location:** Report section “Expanded Note 2: Dengue Hemorrhagic Fever / Severe Dengue,” exam traps
- **HTML destination:** `np1-cd-dengue; np1-cd-x-mosquito-compared`
- **Original claim:** The report calls hemoconcentration ≥20% the pathognomonic laboratory indicator of plasma leakage.
- **Proposed correction:** Do not present a single fixed hematocrit percentage as pathognomonic or sufficient for diagnosis; interpret serial values with baseline, hydration, clinical status and current protocol.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The expanded note uses categorical “pathognomonic” language. The current study note has deliberately removed a fixed single-threshold rule; the exact ≥20% assertion was not imported.
- **Gemini source cited:** DOH AO 2012-0006 cited by report; exact threshold not page-verified in this pass
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; app retains the safer serial-trend/clinical-context caveat.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-056 — Platelet threshold ≤100,000/mm³ presented as bleeding-tendency indicator
- **Report location:** Report section “Expanded Note 2: Dengue,” exam traps
- **HTML destination:** `np1-cd-dengue; np1-endemic-diseases`
- **Original claim:** The report equates platelets ≤100,000/mm³ with bleeding tendency as an exam shortcut.
- **Proposed correction:** Do not use platelet count alone to predict bleeding severity or make treatment decisions; assess bleeding, hemodynamics, hematocrit trends and the full clinical picture.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The threshold may appear in legacy classification schemes but the reported shorthand oversimplifies individual bleeding risk.
- **Gemini source cited:** DOH dengue guidance cited by report; exact cutoff not page-verified in this pass
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the note already cautions against using one lab value alone.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-057 — Fever lasting ≥2 days as a universal early-consultation trigger
- **Report location:** Report section “Expanded Note 2: Dengue,” Philippine context
- **HTML destination:** `np1-cd-dengue`
- **Original claim:** The report embeds a specific fever-duration trigger in the local prevention campaign.
- **Proposed correction:** Prefer current DOH/public-health messages and teach urgent assessment for warning signs or clinical deterioration rather than presenting one fever-duration threshold as universal.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The specific local campaign wording and its current applicability were not independently confirmed.
- **Gemini source cited:** DOH dengue health-promotion circular referenced by report but not supplied
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; not added as a universal consultation rule.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-058 — Exact Enhanced 4S/5S activities, weekly slogan, DEET and fogging advice
- **Report location:** Report section “Expanded Note 2: Dengue,” Philippine context
- **HTML destination:** `np1-cd-dengue; np1-cd-x-mosquito-compared`
- **Original claim:** The report lists “Taob, Taktak, Tuyo, Takip,” DEET, and a specific fogging criterion.
- **Proposed correction:** Check the current DOH campaign and outbreak-control circular before treating these exact slogans/thresholds as the current official protocol.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** Exact campaign wording and the fogging trigger were not checked against a current official issuance.
- **Gemini source cited:** DOH Enhanced 4S/5S references cited by report but not supplied
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no campaign-specific rules were inserted.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-059 — Tepid sponge baths at fever ≥38.5 °C
- **Report location:** Report section “Expanded Note 2: Dengue,” pharmacologic care
- **HTML destination:** `np1-cd-dengue`
- **Original claim:** The report recommends tepid sponge baths alongside paracetamol at a specified temperature.
- **Proposed correction:** Do not import a temperature-triggered cooling procedure without current pediatric/clinical guidance; avoid routine cooling measures that cause distress and follow applicable fever-management protocol.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact threshold and appropriateness of tepid sponging were not verified from the named primary sources.
- **Gemini source cited:** DOH/WHO fever-management guidance not directly inspected for this precise recommendation
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the app does not add this procedure or trigger.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 11

Processed findings: 4. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 4.

### GEM-060 — Pre/post-fluid-bolus Hct/platelet timing and transfusion trigger
- **Report location:** Report section “Expanded Note 2: Dengue,” collaborative management
- **HTML destination:** `np1-cd-dengue`
- **Original claim:** The report says Hct/platelets should be measured before and after fluid boluses and a falling Hct with instability signals occult hemorrhage requiring immediate transfusion.
- **Proposed correction:** Escalate instability urgently, but tie fluid-bolus, repeat-lab and transfusion decisions to the current dengue protocol and a clinician’s hemodynamic assessment; do not teach a single lab change as an automatic transfusion order.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** This is a high-risk resuscitation claim; the precise sequence and transfusion trigger were not independently validated in this pass.
- **Gemini source cited:** DOH dengue clinical management guideline cited by report; exact algorithm not page-verified
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the app instead emphasizes protocol-driven fluids and reassessment.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-061 — D-E-N-G-U-E mnemonic as the exact official list of warning signs
- **Report location:** Report section “Expanded Note 2: Dengue,” quick memory aid
- **HTML destination:** `np1-cd-dengue`
- **Original claim:** The report presents a custom mnemonic intended to encode seven warning signs.
- **Proposed correction:** Treat mnemonics as study aids only; check every item against the governing clinical guideline and avoid labeling a constructed mnemonic as an official government artifact.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The study note already lists warning signs in plain language; the exact mnemonic was not independently verified or needed for correction.
- **Gemini source cited:** DOH AO 2012-0006 cited by report; mnemonic not sourced separately
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; warning signs remain presented as clinical concepts rather than this unsourced mnemonic.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-062 — TOF hypercyanotic-spell explanation of knee-chest positioning
- **Report location:** Report section “Expanded Note 2: Dengue,” clinical management
- **HTML destination:** `np2-pd-tof`
- **Original claim:** The report attributes the benefit to femoral/iliac compression raising SVR and claims LV pressure then forces blood across the stenotic pulmonary valve.
- **Proposed correction:** Keep the standard immediate positioning intervention distinct from a detailed pressure-flow explanation; verify the mechanism against a pediatric cardiology source before teaching that exact causal sequence.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The positioning intervention is already in the HTML; the proposed detailed ventricular-pressure explanation was not independently validated.
- **Gemini source cited:** AHA/AAP or specialty pediatric cardiology source named by report but not supplied/inspected
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the existing position cue remains, and the unverified causal explanation was not added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-063 — Fixed feeding interval/nipple/20–30-minute limit for a 2-month-old with heart failure
- **Report location:** Report section “Expanded Note 2: Dengue” / pediatric cardiology passage, CHF feeding sentence
- **HTML destination:** `np2-pd-tof; pediatric CHF topic search anchor`
- **Original claim:** The report recommends a soft preemie nipple, small feeds every 3 hours, and finishing in 20–30 minutes.
- **Proposed correction:** Do not impose a fixed nipple or feeding interval for an individual infant without assessment and a current pediatric/cardiology feeding plan.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact nipple type, schedule and duration need individualized assessment and were not confirmed in the inaccessible textbook/source.
- **Gemini source cited:** JoAnne Silbert-Flagg MCH nursing and pediatric cardiology guidance named by report but not supplied
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no fixed infant feeding schedule added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 12

Processed findings: 6. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 6.

### GEM-064 — Black waste bag mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps black to non-infectious dry waste.
- **Proposed correction:** Check the exact current Philippine DOH Health-Care Waste Management Manual table before inserting the colour code into the study notes.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The manual’s exact category table was not attached or verified in this pass.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no black-bag mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-065 — Green waste bag mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps green to non-infectious wet/biodegradable waste.
- **Proposed correction:** Check the exact current Philippine DOH waste-colour table before adding this mapping.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact source table and local facility adoption were not verified.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no green-bag mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-066 — Yellow waste bag mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps yellow to infectious/pathological waste.
- **Proposed correction:** Verify the precise segregation category and any subcategory exceptions in the current governing manual before adding the rule.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The category was not independently checked against the primary table.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no yellow-bag mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-067 — Yellow with black band mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps yellow with black band to chemical/pharmaceutical/heavy-metal waste.
- **Proposed correction:** Verify the precise waste classes and colour-band rules in the applicable current manual; do not merge different hazardous subtypes without the governing table.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact colour-band mapping was not directly verified.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no yellow/black-band mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-068 — Orange waste bag mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps orange to radioactive waste.
- **Proposed correction:** Confirm the applicable current radioactive-waste handling and container labelling guidance before teaching a colour-only rule.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact mapping was not verified against a primary manual.
- **Gemini source cited:** DOH Health-Care Waste Management Manual and radiation safety guidance cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no orange-bag mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-069 — Red/puncture-proof sharps container mapping
- **Report location:** Report master table, environmental laws/waste section
- **HTML destination:** `np1-waste-food-laws`
- **Original claim:** The report maps red/puncture-proof containers to sharps.
- **Proposed correction:** Verify the current container standard and waste category labels from the relevant DOH manual; colour, container design and disposal requirements must not be conflated.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The precise red-colour rule and its relation to puncture-proof sharps containers were not confirmed.
- **Gemini source cited:** DOH Health-Care Waste Management Manual, 4th ed. (2020) cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no red/sharps mapping added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

## Batch 13

Processed findings: 5. Applied: 0. Rejected: 0. Already correct: 0. Awaiting evidence/blocked: 5.

### GEM-070 — Exact stool/formalin/ether quantities for FECT
- **Report location:** Report section “Expanded Note 1 / Structured Merge Tool Patch Specification,” food-handler screening
- **HTML destination:** `np1-x-food-safety`
- **Original claim:** The report specifies 1.0–1.5 g stool, 10 mL of 10% formalin, and 3 mL ether.
- **Proposed correction:** Do not copy laboratory reagent volumes into a nursing note without checking the exact method/protocol and laboratory safety requirements.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The linked AO source supports a food-handler screening procedure generally, but the exact reagent quantities were not independently checked in a laboratory procedure.
- **Gemini source cited:** DOH AO 2006-0001 and technical FECT procedure named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the note keeps the verified annual-screening/FECT distinction without reagent quantities.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-071 — FECT sample storage, refrigeration and stool-to-formalin ratio
- **Report location:** Report section “Expanded Note 1 / Structured Merge Tool Patch Specification,” food-handler screening
- **HTML destination:** `np1-x-food-safety`
- **Original claim:** The report says delayed samples can be stored at 4 °C (never frozen) or preserved in 10% formalin at a 1:3 stool-to-formalin ratio.
- **Proposed correction:** Confirm storage/transport details with the actual laboratory SOP or official parasitology manual before presenting them as universal.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact storage temperature, freeze restriction and ratio were not verified from an accessible primary laboratory protocol.
- **Gemini source cited:** DOH AO 2006-0001 and technical FECT procedure named by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no exact sample-storage recipe added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-072 — Specific Philippine LF vector species and local breeding claims
- **Report location:** Report section “Expanded Note 1: Lymphatic Filariasis,” local epidemiology
- **HTML destination:** `np1-cd-filariasis`
- **Original claim:** The report names Aedes poecilus and Anopheles minimus flavirostris and associates a vector with abaca/banana leaf axils and selected localities.
- **Proposed correction:** Require a current authoritative Philippine programme/vector source before importing species-by-location or breeding-site claims into the standard note.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The precise vector/location assertion was not cross-checked against a current official Philippine vector map/manual.
- **Gemini source cited:** DOH National Filariasis Elimination Program AO 2018-0016 and nursing texts cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no place-specific vector claim added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-073 — Exact nocturnal blood examination collection window
- **Report location:** Report section “Expanded Note 1: Lymphatic Filariasis,” diagnostic data
- **HTML destination:** `np1-cd-filariasis`
- **Original claim:** The report says NBE is collected from 8:00 PM to 4:00 AM and mentions a daytime ICT alternative.
- **Proposed correction:** Verify exact collection hours and local test algorithms with current DOH/laboratory protocol before presenting a fixed universal window.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact local time window and test selection were not independently verified.
- **Gemini source cited:** DOH National Filariasis Elimination Program AO 2018-0016 cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; no exact testing window added.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.

### GEM-074 — Fixed DEC 6 mg/kg + albendazole 400 mg every November
- **Report location:** Report section “Expanded Note 1: Lymphatic Filariasis,” treatment/MDA
- **HTML destination:** `np1-cd-filariasis`
- **Original claim:** The report gives a fixed combined MDA dose and month as universal.
- **Proposed correction:** Do not add population-wide drug doses or a fixed campaign month without the current national/area-specific MDA protocol and eligibility criteria.
- **Verdict / implementation:** `UNVERIFIED` / `BLOCKED`
- **Verification status:** UNVERIFIED / SOURCE ACCESS LIMITED
- **Evidence review:** The exact dose, eligible population and November campaign timing were not checked against a current official programme circular.
- **Gemini source cited:** DOH National Filariasis Elimination Program AO 2018-0016 and WHO guidance cited by report
- **Independent sources:** None verified for the exact claim in this pass.
- **Final disposition:** BLOCKED; the app continues to avoid a fixed self-treatment/MDA dosing instruction.
- **Second-pass review:** SECOND REVIEW COMPLETE: claim intentionally withheld because exact authoritative source/protocol was not page-verified.
