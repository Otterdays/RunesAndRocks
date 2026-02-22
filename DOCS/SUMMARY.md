# Runes & Rocks — Project Summary

**Status:** Phase 6 complete. First Multiplayer Sync is active. Connected clients now have a direct real-time reflection of their ECS Entity movements processed strictly by the server. Next is Phase 7: World Foundation.

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

- **Language:** Kotlin 2.3.0 (primary; JDK 17–25)
- **Build:** Gradle (Kotlin DSL), multi-module (shared / server / client)
- **Networking:** Ktor raw TCP + KryoNet binary serialization
- **Client:** LibGDX (2D, cross-platform)
- **Server:** Headless, fixed-timestep, ECS
- **Persistence:** PostgreSQL + Redis

---

## Next Steps

1. Phase 7 (Part 2): Spatial Chunk Subscription — Send sync packets only to physically relevant entities locally to save extreme bandwidth.

See [SCRATCHPAD.md](SCRATCHPAD.md) for full roadmap.
