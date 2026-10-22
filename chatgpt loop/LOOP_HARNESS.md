# ChatGPT Loop — Improvement Harness

## Objective
Repeatedly improve the NURSE GOD skill in this directory until it satisfies measurable acceptance criteria or a justified stopping condition is reached. This is an iterative engineering process, not an infinite loop and not a promise of self-modification between sessions.

## Scope lock
Permitted paths:
- `chatgpt loop/**`

Forbidden without explicit new authorization:
- Any path outside `chatgpt loop/`
- Secrets, credentials, patient-identifying data, or private records
- Changes to unrelated application code, workflows, repository configuration, or sibling projects

## Roles (can be simulated; do not misrepresent as separate agents)
- **Lead/Integrator:** owns scope, merges only validated improvements, maintains source of truth.
- **Research-methods reviewer:** checks question design, search reproducibility, appraisal, synthesis.
- **Philippine-authority reviewer:** checks jurisdiction, currentness, official-source hierarchy, and distinction between law/policy/evidence.
- **Safety/ethics/privacy reviewer:** checks clinical boundaries, human gates, REC/DPO escalation, sensitive data.
- **Adversarial skeptic:** tries to falsify claims and finds loopholes, ambiguity, overclaims, and failure cases.
- **Test/QA reviewer:** maps each requirement to a test and checks evidence for pass/fail.
- **Documentation/handoff reviewer:** ensures portable use, clear setup, useful outputs, and complete handoffs.

A role is a perspective, not proof of independent verification. Only actual distinct tools or people count as independent reviewers, and their work must be documented.

## One iteration
1. **Recover:** read README, SKILL, source spec, validation matrix, handoffs, and latest iteration log.
2. **Baseline:** identify current commit/revision and inspect existing tests/results; do not assume earlier checks still pass.
3. **Find a defect:** identify one concrete ambiguity, contradiction, missing safety gate, untestable requirement, failure mode, or usability problem. Cite the exact section and explain the consequence.
4. **Prioritize:** score impact (1–5), likelihood (1–5), and fix cost (1–5). Prefer high impact × likelihood with reasonable fix cost. Safety/integrity defects outrank style.
5. **Propose:** state intended change, affected files, non-goals, regression risk, and acceptance test before editing.
6. **Implement narrowly:** edit only necessary files in this folder. Preserve the user-authored baseline unless a clearly documented correction is necessary.
7. **Challenge:** run the skeptic pass against the proposed change. Ask what new failure it might create and whether a simpler fix exists.
8. **Test:** run every applicable test available. If a test cannot run, mark NOT RUN and explain why. Static inspection is not a runtime test.
9. **Regression:** check safety constraints, scope lock, links/citation rules, claim statuses, tool honesty, and output profiles.
10. **Accept/reject:** accept only if acceptance criteria pass and no higher-risk regression is introduced. Otherwise revise once or revert the change.
11. **Record:** append date, iteration ID, defect, change, tests, evidence, outcome, and residual risk to ITERATION_LOG.md.
12. **Handoff:** create a task packet if another executor must continue; include exact files, objective, constraints, tests, and stop condition.
13. **Reassess:** identify the next highest-value defect. Do not keep repeating the same review without new evidence.

## Iteration budget and anti-loop
Default run: 5 iterations per execution session, then reassess. A user may authorize another batch. Never run indefinitely without reporting progress or consuming new evidence.

Stop immediately when:
- The user’s scope is unclear or an edit would cross the scope lock.
- A high-consequence decision lacks required authority/evidence.
- A test exposes a safety regression that cannot be corrected confidently.
- Tool failure prevents safe writes or validation.
- Two consecutive iterations find no material defect and make no evidence-backed improvement.
- All critical acceptance criteria pass and the next pass is cosmetic or speculative.
- Iteration limit is reached.

Do not count rewording, repeated panel agreement, or repeated model generations as improvement unless it produces a testable benefit. A change is not an improvement merely because it is longer.

## Acceptance criteria
The project is releasable only when:
1. User-provided domain requirements are retained or deviations are documented.
2. The skill is portable and independent of Ruflo/framework-specific assumptions.
3. Clinical, ethics, privacy, legal, and licensure boundaries are explicit.
4. Research search claims are reproducible and tool-honest.
5. Claim status and corroboration rules are operational.
6. Philippine authority currentness and jurisdiction are addressed.
7. Research design/appraisal and skill-measurement distinctions are present.
8. Simulated panel cannot masquerade as evidence.
9. Output size adapts to task complexity.
10. Tests cover high-risk failure modes and all results are truthfully reported.
11. Handoffs have clear ownership, inputs, deliverables, and acceptance criteria.
12. Every change is traceable in the iteration log.

## Required iteration report
For each pass, return:
- Iteration number and goal
- Evidence-backed defect
- Change made (paths and rationale)
- Tests run and exact results
- Tests not run and why
- Risk/regression check
- Decision: ACCEPT / REVISE / REJECT
- Remaining defects and next pass

## Handoff protocol
At session end, hand off:
- Current objective and scope lock
- Current state/revision and modified files
- Accepted changes and why
- Failed tests and unresolved risks
- Exact next action
- Required evidence for acceptance
- Stop condition

## No false completion
Do not claim the loop passed unless the acceptance matrix was actually evaluated. Do not claim the skill was externally tested unless it was run in that environment. A clean document review is not proof of clinical correctness or official regulatory currentness.
