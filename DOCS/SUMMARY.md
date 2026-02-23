# Runes & Rocks — Project Summary

**Status:** Phase 8 (Persistence) complete. The backend now integrates a robust Dockerized PostgreSQL and Redis stack via HikariCP, securing all player positional states across reboots. Next is Phase 9: Entities & Spawning.

**Vision:** Custom Kotlin-based MMORPG engine for top-down 2D multiplayer. Server-authoritative; client is a dumb terminal.

---

## Quick Links

| Doc | Purpose |
|-----|---------|
| [SCRATCHPAD.md](SCRATCHPAD.md) | Active tasks, phased roadmap, blockers |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Stack, modules, data flow, Gradle config |
| [STYLE_GUIDE.md](STYLE_GUIDE.md) | Code conventions, trace tags |
| [SBOM.md](SBOM.md) | Package tracking |
| [CHANGELOG.md](CHANGELOG.md) | Version history |
| [My_Thoughts.md](My_Thoughts.md) | Decisions and rationale |
| [reference/](reference/) | Archived brainstorm material |
| [CHAT_SYNOPSIS.md](CHAT_SYNOPSIS.md) | Broad synopsis of Phase 2 + build/doc session |

---

## Tech Stack

- **Language:** Kotlin 1.9.24 (primary; JDK 17)
- **Build:** Gradle 8.13 (Kotlin DSL), multi-module (shared / server / client / android)
- **Networking:** Ktor raw TCP + KryoNet binary serialization
- **Client:** LibGDX (2D, cross-platform)
- **Server:** Headless, fixed-timestep, ECS
- **Persistence:** PostgreSQL (Cold storage) + Redis (Hot cache) + Exposed ORM

---

## Next Steps

1. Phase 9: Entities & Spawning — Expand the ECS to support passive NPC roaming and simple aggro ranges for enemies.

See [SCRATCHPAD.md](SCRATCHPAD.md) for full roadmap.
