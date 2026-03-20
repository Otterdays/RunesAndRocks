<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# My Thoughts

Decisions, rationale, and considered alternatives. Append only; never delete.

---

## 2026-02-25: Admin UI as a First-Class Citizen

### Decision

**Chosen: Building a comprehensive, zero-dependency admin dashboard injected directly into the game server binary.**

### Rationale

In the era of AI-assisted development, observability is king. By choosing to build a massive admin dashboard directly into `OtterServer` (using Ktor WebSockets and pure Vanilla HTML/JS with zero external Node/NPM build steps), we essentially created a "mission control" that is always available. It serves two distinct purposes: 1) It allows human operators to understand the real-time state of the MMO (ECS pressure, memory leaks, connection spikes) without needing to configure Grafana/Prometheus immediately. 2) It acts as a flawless Runbook snapshot mechanism. By generating `/api/debug/handoff` schemas, we can literally copy-paste the exact live state of the server into an LLM session to get pinpoint debugging advice. The Anomaly Engine explicitly feeds off this, automatically flagging degrading internal systems with suggested fixes. 

---

## 2026-02-22: Server Hosting Strategy (Alpha Phase)

### Decision

**Chosen: Local Self-Hosting (Intel N150 Mini PC) over Cloud Providers (AWS/GCP) for initial testing.**

### Rationale

The custom Kotlin ECS engine with a 20 TPS fixed-timestep loop and Spatial Grid chunking is exceptionally lightweight. A mini PC with an Intel N150, 16GB RAM, and 2TB NVMe is vastly over-specced for a test server. The JVM will likely consume < 2GB of RAM, and the remaining overhead leaves plenty of room for Dockerized PostgreSQL and Redis. This local setup allows rapid iteration, zero hosting costs during development, and easy direct-to-metal deployment. We only need to port-forward `25565` TCP on the home router. AWS migration is only necessary once player counts exceed home bandwidth capabilities or require global edge routing.

## 2026-02-22: Spatial Chunk Localized Sync

### Decision

**Chosen: Adjacency-based Grid Routing vs Global Data Propagation.**

### Rationale

By implementing Phase 7's `SpatialGrid` natively into the `NetworkSyncSystem`, we completely eliminate 90% of `RenderState` packet latency. If there are 500 connections active, iterating and sending one packet containing 499 coordinate references over TCP causes the loop accumulator to severely stutter (O(N^2)). The current `SpatialGrid` maps bounds using chunks. Now the `NetworkSyncSystem` specifically isolates exactly which Chunk a given Client's mapped entity stands in, grabs the 8 surrounding Chunks, and ONLY maps and sends those local positional updates down the wire. Since the client array implicitly trims missing elements from the UI tree when a packet is dropped natively via `entities.keys.retainAll(serverKeys)` in LibGDX, we get completely organic "fog of war" pop-ins and outs without needing to explicitly fire `UnspawnEntity` packets.

## 2026-02-22: World JSON Layouts

### Decision

**Chosen: External generic JSON map layouts over hardcoded world chunks.**

### Rationale

By implementing a `WorldMap.kt` parser that reads `world.json` on the server headless and draws the array via `ShapeRenderer` on the client, we decouple map creation from game logic. Instead of hardcoding bounds into systems, we've set the exact interface foundation for an external Godot-like World Builder tool moving forward.

---

## 2026-02-22: ECS Foundation Initialized

### Decision

**Chosen: Custom Kotlin ECS over external libraries.**

### Rationale

For Phase 4, keeping the ECS (Entity Component System) closely knitted into our architecture rather than using a third-party complex library like Ashley (LibGDX's default) allows us to stay lean without having to strip out functionality we'll never use on the server-authoritative side. We're holding state data (Position, Velocity) as tiny data structures mapped internally by component classes. Next task: syncing via packets over Kryo!

---

## 2025-02-21: Custom Kotlin Engine vs. Nakama + Agones

### Decision

**Chosen: Custom Kotlin engine** (Ktor, KryoNet, LibGDX, hand-rolled server).

### Considered Alternative: Nakama + Agones

The alternative stack (documented in `grok.md`) proposed:

- **Nakama** for auth, realtime sockets, matchmaking, chat, leaderboards, storage
- **Agones** on Kubernetes for zone server orchestration
- **Protocol Buffers** for serialization
- **Kotlin zone servers** talking to Nakama via gRPC

Benefits: Battle-tested at 2M+ CCU, minimal custom infra, fast time-to-scale.

### Why We Chose Custom Kotlin

1. **Engine-building goal.** We are building an MMORPG *engine*, not a lobby game. Nakama excels at session-based multiplayer. For a persistent open world with authoritative tick-based simulation, ECS, spatial partitioning, and custom combat, we would still need custom zone servers. Nakama becomes a thin wrapper around the hard parts.

2. **Learning vs. abstracting.** The project aims to understand fixed-timestep loops, ECS, binary serialization, spatial chunking. Nakama hides these. Custom implementation teaches the fundamentals.

3. **Kotlin/JVM is a good fit.** Coroutines for async networking, JVM for long-running processes, LibGDX for 2D, KryoNet for fast binary serialization. The stack is proven for this use case.

4. **Operational simplicity early.** Nakama + Agones + K8s + CockroachDB adds significant infra before any game mechanic exists. We can add Nakama later for auth/matchmaking/leaderboards once the core game works.

### What to Borrow Later (from Nakama approach)

- **Protobuf over Kryo** — Better schema evolution; consider when packet format stabilizes.
- **Separation of concerns** — Keep auth, persistence, matchmaking cleanly separated so Nakama can be plugged in later if desired.
- **Containerization** — Docker for the server is sensible, but not day-one.
- **Agones/K8s** — When horizontal scaling is needed, Agones is the right pattern for zone servers.

### Future Re-evaluation

When we reach Phase 10 (Infrastructure), re-evaluate whether to adopt Nakama for auth, matchmaking, and leaderboards. By then we will have a working game and can make an informed choice.
