# Runes & Rocks — Project Summary

**Game:** Runes And Rocks (premier game). **Engine:** OtterEngine V1 (custom server, ECS, networking, persistence).

> **NEW:** [Continuous Audit System](audit/) now available — comprehensive system state, security posture, and technical debt tracking.

---

**2026-02-24:** Server UI Upgrade batch complete — Quick Wins (#11 Log Tail, #25/#27 sparklines, #46 Last Tick), Security (#33 Failed Login Tracker, #35 Top Senders), AI Handoff (#38 SystemPulse, #40 Health Score, #47 Copy Snapshot). Dashboard now has 8 tabs including Security, Logs, Controls, System.

**Status:** Phase 8 (Persistence) complete. Server UI Upgrade Phases A–F largely complete (50-item checklist). Backend integrates Dockerized PostgreSQL and Redis via HikariCP. Next: Phase 9 (Entities & Spawning) or remaining SERVER_UI_UPGRADE items.

**Vision:** Top-down 2D multiplayer on OtterEngine. Server-authoritative; client is a dumb terminal.

---

## Quick Links

| Doc | Purpose |
|-----|---------|
| [audit/](audit/) | **Continuous system audit** — architecture, security, technical debt |
| [AI_CONTEXT.md](AI_CONTEXT.md) | **Primary Start Guide for fellow AIs** |
| [SCRATCHPAD.md](SCRATCHPAD.md) | Active tasks, phased roadmap, blockers |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Stack, modules, data flow, Gradle config |
| [OTTERMAP_PLAN.md](OTTERMAP_PLAN.md) | Full world editor + asset pipeline plan |
| [SERVER_UI_UPGRADE.md](SERVER_UI_UPGRADE.md) | 50-item Admin Dashboard upgrade checklist (phases A–I) |
| [STYLE_GUIDE.md](STYLE_GUIDE.md) | Code conventions, trace tags |
| [SBOM.md](SBOM.md) | Package tracking |
| [CHANGELOG.md](CHANGELOG.md) | Version history |
| [My_Thoughts.md](My_Thoughts.md) | Decisions and rationale |
| [journal/](journal/) | **Day-to-day check-ins** — dated improvement notes and pass-over observations |
| [reference/](reference/) | Archived brainstorm material |
| [sbom_update_list.md](sbom_update_list.md) | Dependency modernization checklist |

---

## Tech Stack

- **Language:** Kotlin 2.3.10 (primary; JDK 21)
- **Build:** Gradle 9.3.1 (Kotlin DSL), AGP 9.0.1, multi-module (shared / server / client / android)
- **Networking:** Ktor 3.4.0 raw TCP + Kryo binary serialization
- **Client:** LibGDX 1.14.0 (2D, cross-platform)
- **Server:** Headless, fixed-timestep, ECS
- **Persistence:** PostgreSQL 42.7.10 (cold storage) + Redis via Jedis 7.3.0 (hot cache) + Exposed 1.0.0 ORM + HikariCP 7.0.2
- **Logging:** Logback 1.5.6 (ring-buffer appender for admin log tail)

---

## Next Steps

1. Phase 9: Entities & Spawning — Expand the ECS to support passive NPC roaming and simple aggro ranges for enemies.

See [SCRATCHPAD.md](SCRATCHPAD.md) for full roadmap.
