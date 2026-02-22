# Changelog

All notable changes. [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format.

---

## [Unreleased]

### Changed

- Build infrastructure: Upgraded Gradle from 8.5 to 8.13 and Android Gradle Plugin (AGP) from 8.1.1 to 8.13.2 via Android Studio Upgrade Assistant.

### Added

- Phase 7 (Part 2) complete: Spatial Chunk Subscription. Instantiated `SpatialGrid` inside the `ServerLauncher` assigning chunks of 512x512 pixels. Completely rewrote `NetworkSyncSystem` to spatially partition entity updates, routing absolute `RenderState` packets strictly to connected clients sharing a grid or an adjacent 8-way partition chunk. This effectively solves O(n^2) network latency bloat issues.

- Phase 7 (Part 1) complete: World Foundation. Server now parses Godot-style JSON tilemaps (`WorldMap.kt`), and `MovementSystem` calculates logical collisions based on radius checking the solid tiles. Client (`ClientLauncher`) uses `ShapeRenderer` to natively render the array output locally mirroring the server bounds.
- Phase 6 complete: First Multiplayer Sync. `NetworkSyncSystem` efficiently broadcasts position updates to connected clients utilizing a lightweight thread-safe queuing model (`engine.queueTask`) to ensure strictly protected ECS mutations. The `ClientLauncher` renders multiple remote entity sprites synced with `lastLatencyMs` via `ConcurrentHashMap`, responding instantly to user WASD input tracking server-sided velocities.
- Phase 5 complete: `ClientLauncher` replaced with `Lwjgl3Application` for full desktop rendering. Migrated `GameClient` socket interactions (startListening, latency read) seamlessly into the window app running in the background while LibGDX renders "badlogicsmall.jpg" sprite placeholder at 60 FPS in front. Added Client-to-Server `LoginRequest` auto-sent upon initial TCP handshake.
- Phase 4 complete: Object-oriented Entity Component System (ECS Core). Implemented `Engine`, `Component`, `System`, `MovementSystem`, and `SpatialGrid` for chunking. Integrated ECS `update()` directly into the 20 TPS server loop (`ServerLauncher`, `TickLoop`). Server tracks active Entity ID allocations map.
- Admin Web Dashboard glow-up: Admin API and WebSocket now feed live `Entities` count tracked by the ECS engine, surfacing it perfectly on the UI dashboard inline with clients and TPS.
- Phase 3 complete: sealed interface Packet (Ping/Pong), PacketRegistry (Kryo 5.6.0 serialize/deserialize, ThreadLocal, stable type IDs), PacketCodec.write/read generic API, PacketType.kt removed. GameServer/GameClient/GameServerTest migrated to typed packets. PacketRegistryTest (round-trip, unknown type ID).
- Phase 2 complete: Ktor 3.4.0 TCP GameServer (bind, accept, ClientConnection), GameClient (connect, sendPing, latency), shared PacketType/PacketCodec (ping/pong wire format), ServerLauncher/ClientLauncher integration, GameServerTest (accept + ping/pong). ktor-io 3.4.0 in shared; ktor-network + slf4j-simple on client.
- gradlew.bat: quoted `set "VAR=value"` for DIRNAME/APP_HOME so paths with `&` (e.g. runes-&-rocks) work on Windows.
- Phase 1 complete: fixed-timestep loop (TickLoop) at 20 TPS, accumulator logic, unit test (tick count over 2s), SLF4J logging ("[SERVER] Started on port X", tick-rate every 5s)
- Kotlin version 1.9.24 aligned with JDK 17 support (per `README.md` specifications)
- JUnit Platform launcher for server tests (Gradle 9.x); SBOM updated
- Phase 0 complete: Git init, Gradle multi-module (shared/server/client), placeholder entry points, launch_backend.bat
- DOCS structure (SUMMARY, SCRATCHPAD, ARCHITECTURE, STYLE_GUIDE, SBOM, CHANGELOG, My_Thoughts)
- Phased roadmap with acceptance criteria
- Archived brainstorm material in DOCS/reference/
- Root README with tech stack and doc links

---

## [0.0.0] — 2025-02-21

### Added

- Initial documentation scaffold
- Architecture decision: custom Kotlin engine (Ktor, KryoNet, LibGDX)
- Distilled content from 6 planning docs into structured ARCHITECTURE, SCRATCHPAD, My_Thoughts
