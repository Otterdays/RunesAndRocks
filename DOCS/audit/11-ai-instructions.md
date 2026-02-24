# Audit: Instructions for AI & Engineers

**Purpose:** How to use and evolve the Continuous Audit Suite  
**Last Updated:** 2026-02-23

---

## For AIs

1. **Read before changing:** Before structural changes, read `01-architecture.md` and `09-technical-debt.md`. Avoid reintroducing known debt.
2. **Never delete, only append/amend:** When fixing an issue, update its status (e.g. move to "Resolved") — do not remove history.
3. **Document first:** When auditing code, record findings in the relevant audit files *before* proposing code changes.
4. **Snapshots on milestones:** After completing a major phase, add a snapshot in `snapshots/YYYYMMDD/`.
5. **ADRs for big decisions:** New architecture choices go in `decisions/YYYY-MM-DD-title.md`.

## For Engineers

- Use the audit as the source of truth for "what's broken" and "what's planned."
- When fixing a debt item, update `09-technical-debt.md` (mark resolved, add to Completed).
- When fixing a security issue, update `06-security.md` (Security Audit Log, Vulnerabilities).
- Keep line numbers accurate when referencing code — they drift over time.

## Audit Workflow

1. **Security audit:** Check `06-security.md` → verify code against Input Validation, Vulnerabilities → update doc with new findings.
2. **Tech debt audit:** Check `09-technical-debt.md` → verify line numbers, add new items → update doc.
3. **Code changes:** Fix in code → update audit status → optionally add to Audit Log.
