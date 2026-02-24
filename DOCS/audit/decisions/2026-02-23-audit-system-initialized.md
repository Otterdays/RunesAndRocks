# ADR: Audit System Initialization

**Date:** 2026-02-23  
**Status:** Accepted  
**Decider:** Claude (AI Assistant)  
**Audience:** Future developers, AI assistants, project maintainers

---

## Context

The Runes & Rocks project reached Phase 8 completion with significant complexity:
- 4 modules (shared, server, client, android)
- ~2,500 lines of production Kotlin code
- Custom ECS engine
- Multi-tier persistence (PostgreSQL + Redis)
- Cross-platform client (Desktop + Android)
- Real-time multiplayer networking

**Problem:** Knowledge was distributed across SCRATCHPAD, ARCHITECTURE.md, README, and source code. No unified system for tracking system state, technical debt, security posture, or architectural decisions over time.

**Trigger:** User request for "an audit. the beginning of an audit of everything. every growing, updated, continuous database audit and history."

---

## Decision

Create a continuous audit system under `DOCS/audit/` with the following structure:

```
DOCS/audit/
├── README.md                 # Entry point, navigation, status overview
├── 01-architecture.md        # Module boundaries, data flow, deployment
├── 02-engine.md              # ECS, TickLoop, Systems deep-dive
├── 03-networking.md          # Protocol, packets, serialization
├── 04-persistence.md         # PostgreSQL, Redis, data lifecycle
├── 05-client.md              # Desktop & Android clients
├── 06-security.md            # CVEs, attack surface, hardening
├── 07-performance.md         # Benchmarks, metrics, baselines
├── 08-dependencies.md        # SBOM, license audit
├── 09-technical-debt.md      # Known issues, TODOs, refactoring queue
├── 10-changelog.md           # Audit trail of changes
├── decisions/                # ADRs (Architecture Decision Records)
└── snapshots/                # Point-in-time full system snapshots
```

---

## Consequences

### Positive

- **Single source of truth:** All system knowledge in one location
- **Temporal tracking:** Dated entries show evolution over time
- **Onboarding acceleration:** New contributors/AI assistants can understand system state quickly
- **Risk visibility:** Security gaps and technical debt explicitly cataloged
- **Decision history:** ADRs capture why architectural choices were made
- **Baseline metrics:** Performance characteristics captured for regression detection

### Negative

- **Maintenance burden:** Requires updating after significant changes
- **Staleness risk:** Can become outdated if not maintained
- **Duplication potential:** Some overlap with existing SCRATCHPAD.md

### Mitigations

- Update policy: "During work, not after. Checkpoint every 15-20 min."
- Golden rule: "Never delete or replace existing content. Append, amend, or annotate only."
- Cross-reference with SCRATCHPAD for active tasks vs. audit for system state

---

## Alternatives Considered

| Alternative | Pros | Cons | Decision |
|-------------|------|------|----------|
| Extend SCRATCHPAD.md | Single file | Would become unwieldy at this scale | Rejected |
| Wiki/external docs | Rich features | External dependency, not version-controlled | Rejected |
| Code comments only | Always in sync | Hard to get big picture | Rejected |
| **Dedicated audit folder** | Organized, version-controlled, structured | Maintenance overhead | **Accepted** |

---

## Implementation

**Files Created:**
- `DOCS/audit/README.md` (1.0.0)
- `DOCS/audit/01-architecture.md`
- `DOCS/audit/02-engine.md`
- `DOCS/audit/03-networking.md`
- `DOCS/audit/04-persistence.md`
- `DOCS/audit/06-security.md`
- `DOCS/audit/09-technical-debt.md`
- `DOCS/audit/decisions/2026-02-23-audit-system-initialized.md` (this ADR)
- `DOCS/audit/snapshots/` (directory ready for use)

---

## References

- `DOCS/SCRATCHPAD.md` — Active tasks and roadmap
- `DOCS/ARCHITECTURE.md` — High-level system design
- `DOCS/SUMMARY.md` — Project status overview
- User rules document — "Never delete or replace existing content. Append, amend, or annotate only."
