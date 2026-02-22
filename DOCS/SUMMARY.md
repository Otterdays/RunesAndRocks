# Runes & Rocks — Project Summary

**Status:** Phase 7 (World Foundation) complete. First Multiplayer Sync is active, and Spatial Chunk Subscription is successfully implemented routing packets strictly via grid adjacency. Next is Phase 8: Persistence.

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
- **Persistence:** PostgreSQL + Redis

---

## Next Steps

1. Phase 8: Persistence — Scaffold PostgreSQL schema for player saves, and configure Redis caching for the hot path.

See [SCRATCHPAD.md](SCRATCHPAD.md) for full roadmap.
