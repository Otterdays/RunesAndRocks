<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Runes & Rocks — Continuous Audit Trail

**Purpose:** Living documentation of the server, engine, model, and all subsystems. This audit grows continuously and tracks architectural decisions, performance baselines, security posture, and technical debt.

**Last Updated:** 2026-02-23  
**Audit Version:** 1.0.0  
**Auditor:** Claude (AI Assistant)

---

## Quick Navigation

| Section | Description |
|---------|-------------|
| [01-architecture.md](01-architecture.md) | System structure, data flow, module boundaries |
| [02-engine.md](02-engine.md) | OtterEngine V1 — ECS, TickLoop, Systems deep-dive |
| [03-networking.md](03-networking.md) | Protocol, packet pipeline, serialization |
| [04-persistence.md](04-persistence.md) | PostgreSQL, Redis, data lifecycle |
| [05-client.md](05-client.md) | Desktop & Android clients, rendering, input |
| [06-security.md](06-security.md) | CVEs, attack surface, hardening status |
| [07-performance.md](07-performance.md) | Benchmarks, tick budgets, memory profiles |
| [08-dependencies.md](08-dependencies.md) | SBOM, transitive deps, license audit |
| [09-technical-debt.md](09-technical-debt.md) | Known issues, TODOs, refactoring queue |
| [10-changelog.md](10-changelog.md) | Audit trail of significant changes |
| [11-ai-instructions.md](11-ai-instructions.md) | How to use and evolve the audit (for AIs & engineers) |
| [decisions/](decisions/) | ADRs (Architecture Decision Records) |
| [snapshots/](snapshots/) | Point-in-time system snapshots |

---

## Audit Status Overview

### Critical Systems

| System | Status | Risk Level | Last Verified |
|--------|--------|------------|---------------|
| ECS Core | Operational | Low | 2026-02-23 |
| TickLoop | Operational | Low | 2026-02-23 |
| GameServer (TCP) | Operational | Medium | 2026-02-23 |
| Admin Dashboard | Operational | Low | 2026-02-23 |
| PostgreSQL Persistence | Operational | Low | 2026-02-23 |
| Redis Hot Cache | Operational | Low | 2026-02-23 |
| Spatial Grid | Operational | Low | 2026-02-23 |
| MovementSystem | Operational | Low | 2026-02-23 |
| NetworkSyncSystem | Operational | Medium | 2026-02-23 |

### Risk Level Definitions

- **Low:** Battle-tested, tested, no known issues
- **Medium:** Operational but needs monitoring (networking, sync)
- **High:** New code, untested edge cases, or complex interactions
- **Critical:** Blocking, unstable, or security-sensitive

---

## Current Baseline Metrics

Captured at audit initialization:

```
Target TPS: 20 (50ms budget per tick)
Current Codebase: ~2,500 lines Kotlin (production)
Test Coverage: ECS, TickLoop, GameServer, PacketRegistry
Entities: Component-based (Position, Velocity)
Systems: MovementSystem, NetworkSyncSystem
Max Concurrent Players (tested): 2 (manual)
World Size: 100x100 tiles (default world.json)
```

---

## Audit Log

| Date | Event | Auditor |
|------|-------|---------|
| 2026-02-23 | Audit system initialized; Phase 8 complete baseline captured | Claude |
| 2026-02-23 | Added 11-ai-instructions.md; security + tech-debt follow-up audit | Claude |

---

## How to Update This Audit

1. **After significant changes:** Update relevant section file
2. **New ADR:** Add to `decisions/` with format `YYYY-MM-DD-title.md`
3. **Milestone snapshots:** Create in `snapshots/YYYYMMDD/` with full system state
4. **Security events:** Immediate update to `06-security.md`
5. **Performance regressions:** Log to `07-performance.md` with before/after

**Golden Rule:** Never delete or replace — always append with dated entries.
