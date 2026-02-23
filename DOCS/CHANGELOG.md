# Changelog

All notable changes. [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) format.

---

## [Unreleased]

### Added
- 2026-02: **Server Admin WebUI Enhancements:**
  - Integrated `CallLogging` for Ktor admin routes to improve HTTP request observability.
  - Added real-time database connection pool metrics (Active, Idle, Total) from HikariCP.
  - Added real-time Redis cache connection heartbeat (ONLINE/OFFLINE ping status).
  - Added new `/api/actions/gc` endpoint and a button on the dashboard to manually trigger garbage collection for debugging memory pressure.

### Changed

- 2026-02: **MainMenuScreen Skin fix (Android):** Resolved `GdxRuntimeException: No Drawable registered with name: default` crash. Renamed Skin resources to distinct names (`font`, `button-up`, `background`, `cursor`) to avoid overwrites; added Pixmap disposal after Texture creation.
- 2026-02: **PlayerRepository.kt Exposed 1.0.0:** Added `import org.jetbrains.exposed.v1.core.eq` for `Players.username eq username` query (eq is now a top-level function in Exposed 1.0).
- 2026-02: **README structure:** Added exclusive sections — "Runes & Rocks — The Game" (game vision, identity) and "OtterEngine — The Engine" (engine-only section with framing intro). Renamed "Engine Components" → "OtterEngine". Architecture now clarifies: "Runes & Rocks (the game) runs entirely on OtterEngine (the engine)."
- 2026-02: **Classification:** Runes And Rocks = premier game; OtterEngine V1 = custom engine (OtterServer = server, OtterCore = shared). README, SUMMARY, ARCHITECTURE updated.
- 2026-02: **Exposed ORM 1.0.0** — Migrated from 0.50.1. Updated all imports to `org.jetbrains.exposed.v1.*` in `DatabaseFactory.kt` (`Database`, `SchemaUtils`, `transaction` → `v1.jdbc`) and `PlayerRepository.kt` (`IntEntity`, `IntEntityClass` → `v1.dao`; `EntityID`, `IntIdTable` → `v1.core.dao.id`; `transaction` → `v1.jdbc.transactions`).
- 2026-02: **Gradle wrapper** — Bumped from 9.2.1 to 9.3.1. SBOM: noted JUnit 6.0.3 availability; added Exposed 1.0.0 migration notes (import paths, SqlExpressionBuilder deprecation, transaction API changes).
- 2026-02: **Android build fixed** — Migrated to AGP 9 built-in Kotlin: removed deprecated `kotlin("android")` plugin, `android.builtInKotlin=false`, `android.newDsl=false` from gradle.properties; added explicit `gdx` dependency; replaced deprecated `srcDirs` with `directories` in android sourceSets. `:android:assembleDebug` now succeeds.
- 2026-02: **Android libgdx fix** — Registered a `copyAndroidNatives` Gradle task in `android/build.gradle.kts` to correctly extract `.so` native libraries from `gdx-platform` jars into `jniLibs.srcDir("libs")`. Fixed runtime `java.lang.UnsatisfiedLinkError: dlopen failed: library "libgdx.so" not found` crash on android.
- 2026-02: **GUI Main Menu** — Refactored the `ClientLauncher` to extend `Game` instead of `ApplicationAdapter`. Constructed a functional `MainMenuScreen` via `Scene2D` and a `GameScreen`. Users can now input a target username before hopping into the world.
- 2026-02: SBOM modernization — Kotlin 2.3.10, LibGDX 1.14.0, Exposed 0.50.1, HikariCP 7.0.2, Jedis 7.3.0, PostgreSQL 42.7.10, slf4j-simple 2.0.17, junit-jupiter 5.12.2. RedisFactory migrated from JedisPool to JedisPooled (GenericObjectPoolConfig). Log4j CVE-2025-68161 mitigation via `resolutionStrategy.force` in root build.gradle.kts.
- Build infrastructure: Upgraded Gradle to 9.2.1 and Android Gradle Plugin (AGP) to 9.0.1 via Android Studio Upgrade Assistant.
- Build infrastructure: Upgraded Gradle from 8.5 to 8.13 and Android Gradle Plugin (AGP) from 8.1.1 to 8.13.2 via Android Studio Upgrade Assistant.

### Added

- Phase 8 complete: Persistence Layer. Dockerized PostgreSQL and Redis via `docker-compose.yml`. Configured the server build with Exposed ORM, HikariCP, and Jedis. Built `PlayerRepository.kt` to securely hydrate `PlayerState` properties natively into the ECS `redis` hot-cache via `hset` upon `LoginRequest`, perfectly restoring previously mapped (x,y) coordinates per username on successful socket linkage. Position is synchronously re-saved back to PostgreSQL SQL mapping safely upon Ktor client disconnect cleanly stopping the memory leakage issue on `kill`.

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
