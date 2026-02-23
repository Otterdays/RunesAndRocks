<div align="center">

<img src="client/src/main/resources/player.png" width="128" alt="Hero Sprite — Runes & Rocks">

# Runes & Rocks

**Our premier game** — top-down 2D multiplayer adventuring, powered by **OtterEngine** (custom Kotlin MMORPG engine, V1).

*Server-authoritative • ECS-driven • Cross-platform (Desktop + Android)*

---

### Tech & Dependencies

[![Kotlin](https://img.shields.io/badge/Kotlin-2.3.10-7F52FF?logo=kotlin&logoColor=white)](#)
[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](#)
[![Gradle](https://img.shields.io/badge/Gradle-9.3.1-02303A?logo=gradle&logoColor=white)](#)
[![AGP](https://img.shields.io/badge/AGP-9.0.1-3DDC84?logo=android&logoColor=white)](#)

[![LibGDX](https://img.shields.io/badge/LibGDX-1.14.0-E74C3C?logo=libgdx)](#)
[![Ktor](https://img.shields.io/badge/Ktor-3.4.0-000000?logo=ktor)](#)
[![Kryo](https://img.shields.io/badge/Kryo-5.6.2-8B0000)](#)
[![Exposed](https://img.shields.io/badge/Exposed-1.0.0-000000?logo=kotlin)](#)

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.10-336791?logo=postgresql&logoColor=white)](#)
[![Redis](https://img.shields.io/badge/Jedis-7.3.0-DC382D?logo=redis&logoColor=white)](#)
[![HikariCP](https://img.shields.io/badge/HikariCP-7.0.2-000000)](#)
[![JUnit](https://img.shields.io/badge/JUnit-5.12.2-25A162?logo=junit5&logoColor=white)](#)

[![License](https://img.shields.io/badge/License-MIT-green.svg)](#)

</div>

---

## Table of Contents

- [Overview](#overview)
- [Runes & Rocks — The Game](#runes--rocks--the-game)
- [Architecture](#architecture)
- [OtterEngine — The Engine](#otterengine--the-engine)
  - [OtterCore — Shared Protocol Layer](#ottercore--shared-protocol-layer)
  - [OtterServer — Authoritative Game Server](#otterserver--authoritative-game-server)
    - [Entity Component System (ECS)](#entity-component-system-ecs)
    - [Fixed-Timestep TickLoop](#fixed-timestep-tickloop)
    - [Networking & Packet Pipeline](#networking--packet-pipeline)
    - [Persistence — PostgreSQL + Redis](#persistence--postgresql--redis)
    - [Admin Web Dashboard](#admin-web-dashboard)
    - [World & Collision](#world--collision)
  - [Desktop Client](#desktop-client)
  - [Android Client](#android-client)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Documentation](#documentation)
- [Contributing](#contributing)

---

## Overview

This repository contains **Runes & Rocks** (the premier game) and **OtterEngine** (the custom Kotlin MMORPG engine that powers it). Server-authoritative, ECS-driven, cross-platform — Desktop and Android.

| Feature | Description |
|---------|-------------|
| **Server-Authoritative** | 100% of physics, collisions, and state calculations happen on the secure headless server. Client is a dumb terminal. |
| **Custom ECS** | OtterEngine data-oriented Kotlin ECS with efficient matrices, spatial chunking, and deterministic tick loops. |
| **Fixed-Timestep** | Deterministic gameplay at precisely 20 TPS, independent of network or I/O lag. |
| **Raw TCP + Kryo** | High-speed binary serialization over Ktor TCP sockets — no HTTP overhead. |
| **Godot-Ready Maps** | Server parses standard JSON 2D tilemaps (Godot/Tiled) and builds collision boundaries at runtime. |
| **Dockerized Persistence** | PostgreSQL (cold storage) + Redis (hot cache) via HikariCP and Jedis. |

---

## Runes & Rocks — The Game

**Runes & Rocks** is the premier game built on OtterEngine. Top-down 2D multiplayer adventuring — server-authoritative, with every tick, collision, and state update computed on the server. The client is a visual terminal; the engine does the work. Vision: massive multiplayer loads across Desktop and Android.

---

## Architecture

*Runes & Rocks (the game) runs entirely on OtterEngine (the engine).*

```
┌─────────────────────────────────────────────────────────────────┐
│  RUNES & ROCKS (game)  │  OtterEngine (custom engine, V1)        │
├─────────────────────────────────────────────────────────────────┤
│  shared/     │  OtterCore: packet protocol, Kryo codec, types    │
│  server/     │  OtterServer: Ktor TCP, ECS, TickLoop, DB          │
│  client/     │  LibGDX LWJGL3, socket client, rendering          │
│  android/    │  LibGDX Android backend, same client logic         │
└─────────────────────────────────────────────────────────────────┘
```

| Module | Tech | Purpose |
|--------|------|---------|
| **shared** (OtterCore) | Kryo, Ktor-io, LibGDX | Packet definitions, binary codec, shared types |
| **server** (OtterServer) | Ktor, Exposed, HikariCP, Jedis | TCP game server, ECS, persistence, admin API |
| **client** | LibGDX LWJGL3 | Desktop 60 FPS OpenGL rendering |
| **android** | LibGDX Android | Mobile client, same game logic |

---

## OtterEngine — The Engine

**OtterEngine** is the custom Kotlin MMORPG engine (V1) powering Runes & Rocks. Built from the ground up for server-authoritative multiplayer: headless, deterministic, and fully authoritative. The engine consists of four components — OtterCore, OtterServer, and the Desktop and Android clients.

---

### OtterCore — Shared Protocol Layer

> `shared/` — The contract between client and server. Zero game logic, zero rendering — just types, packets, and binary codec.

| Feature | Detail |
|---------|--------|
| **Sealed Packet Hierarchy** | Type-safe `Packet` interface with `Ping`, `Pong`, `LoginRequest`, `LoginResponse`, `SpawnEntity`, `UnspawnEntity`, `RenderState`, `MoveRequest` |
| **Binary Codec** | Length-prefixed binary protocol: `[typeId: 1B][length: 4B][payload: NB]` — compact, zero-copy-friendly framing |
| **Kryo Serialization** | ThreadLocal Kryo instances with registered packet class → ID mapping for high-speed binary encoding (~80% smaller than JSON) |
| **Packet Registry** | Bidirectional `classToId` / `idToClass` maps with centralized registration — add a packet in one place, both sides see it |
| **Shared Math** | LibGDX math types (vectors, enums) shared across all modules for consistent world coordinates |

**Packet flow:**

```
Client                          Server
  │  MoveRequest ──────────────▶  │
  │                                │  (ECS processes movement)
  │  ◀────────────── RenderState  │
  │  ◀──────────── SpawnEntity    │
```

---

### OtterServer — Authoritative Game Server

> `server/` — The brain. Headless, deterministic, and fully authoritative. Nothing reaches the client without server validation.

#### Entity Component System (ECS)

The heart of OtterEngine. Data-oriented, no deep inheritance.

| Concept | Implementation |
|---------|---------------|
| **Entities** | Integer IDs managed by `Engine` — `createEntity()` / `destroyEntity()` |
| **Components** | Data classes stored in `Map<KClass<Component>, Map<EntityId, Component>>` |
| **Systems** | Abstract `System` class with `update(delta)`, registered to run each tick |
| **Task Queue** | Thread-safe `queueTask()` for network-thread → ECS-thread operations |
| **Querying** | `getEntitiesWith(vararg KClass)` returns all entities matching a component set |

**Built-in components:**

| Component | Fields | Purpose |
|-----------|--------|---------|
| `Position` | `x: Float, y: Float` | World coordinates |
| `Velocity` | `dx: Float, dy: Float` | Movement vector per tick |

**Built-in systems:**

| System | Runs On | Purpose |
|--------|---------|---------|
| `MovementSystem` | Entities with `Position` + `Velocity` | Applies velocity, 4-point hitbox collision against `WorldMap.isSolid()`, stops on collision |
| `NetworkSyncSystem` | Entities with `Position` | Per-client visibility culling via `SpatialGrid` (9-chunk 3x3 window), broadcasts `RenderState` packets, prunes dead entities |

**Spatial partitioning** — `SpatialGrid` divides the world into chunks (default 32 units). Entities register to their chunk. Visibility queries return the 3x3 grid around a player. Thread-safe via `ConcurrentHashMap` + `CopyOnWriteArraySet`.

---

#### Fixed-Timestep TickLoop

Deterministic game loop running at **20 TPS** (configurable).

| Property | Value |
|----------|-------|
| **Target TPS** | 20 (50ms per tick) |
| **Pattern** | Accumulator — accumulates real elapsed time, runs fixed `update()` ticks |
| **Spiral Guard** | Max 5 ticks per frame to prevent death spirals on lag spikes |
| **Metrics** | Live TPS tracking, uptime, total tick count |
| **Logging** | Reports actual TPS every 5 seconds |
| **Sleep** | Sleeps remainder time between ticks to avoid busy-spinning |

```
┌─ Real time elapsed ──▶ Accumulate
│
├─ Accumulator >= 50ms? ──▶ Run update(delta) ──▶ Subtract 50ms
│                                                   │
│                           (repeat up to 5x)  ◀────┘
│
└─ Accumulator < 50ms ──▶ Sleep remaining ──▶ Loop
```

---

#### Networking & Packet Pipeline

Raw TCP via Ktor sockets — no HTTP, no REST, no overhead.

| Feature | Detail |
|---------|--------|
| **Transport** | Ktor raw TCP on port `25565` (configurable via `PORT` env) |
| **Connections** | `ConcurrentHashMap<Long, ClientConnection>` with unique IDs |
| **Client Lifecycle** | `connect → login → play → disconnect` with full entity cleanup |
| **Login Flow** | `LoginRequest` → create/load player entity from DB → `LoginResponse` with entity ID |
| **Movement** | `MoveRequest` → server sets entity `Velocity` → `MovementSystem` validates and applies |
| **Latency** | `Ping` / `Pong` round-trip measurement |
| **Broadcasting** | `broadcast()` to all, `sendToClient()` to one |
| **Admin** | `kickClient()` for moderation |

**`ClientConnection` holds:**
- Ktor socket + read/write byte channels
- Unique connection ID
- Associated ECS entity ID
- PostgreSQL player ID

---

#### Persistence — PostgreSQL + Redis

Two-tier storage: hot cache for live sessions, cold storage for durability.

```
Login ──▶ PostgreSQL (find/create) ──▶ Redis (hydrate cache)
                                           │
                                    Live session reads/writes
                                           │
Logout / Flush ──▶ Redis ──▶ PostgreSQL (commit) ──▶ Redis (cleanup)
```

| Layer | Tech | Config | Purpose |
|-------|------|--------|---------|
| **Cold Storage** | PostgreSQL 42.7.10 + Exposed 1.0.0 ORM | `JDBC_URL`, `DB_USER`, `DB_PASS` env vars | `Players` table: `id`, `username` (unique), `posX`, `posY`. Auto-created via `SchemaUtils` |
| **Hot Cache** | Redis via Jedis 7.3.0 (`JedisPooled`) | `REDIS_HOST`, `REDIS_PORT` env vars | `player:{id}` hash with position data. Pool: max 20, idle 10-2 |
| **Connection Pool** | HikariCP 7.0.2 | Managed by `DatabaseFactory` | Zero-overhead JDBC pooling |

**`PlayerRepository` operations:**
- `loginPlayer(username)` — Find or create in PostgreSQL, hydrate Redis cache
- `savePlayer(playerId)` — Read Redis → commit to PostgreSQL → cleanup Redis

---

#### Admin Web Dashboard

Live server monitoring on `http://localhost:8080`.

| Feature | Detail |
|---------|--------|
| **Server** | Ktor HTTP on port `8080`, bound to `127.0.0.1` (local only) |
| **Live Metrics** | WebSocket (`/ws/live`) pushes TPS, uptime, memory, threads, CPU, entity count every 1 second |
| **REST API** | `GET /api/status`, `GET /api/clients`, `POST /api/clients/{id}/kick`, `GET /api/config` |
| **Dashboard UI** | Dark-themed HTML/CSS/JS with animated background, real-time updating metric cards |
| **Client Table** | Live list of connected players with kick button for moderation |
| **Serialization** | Jackson JSON via Ktor content negotiation |

---

#### World & Collision

| Feature | Detail |
|---------|--------|
| **Map Format** | JSON tilemap (`world.json`): `width`, `height`, `tileSize`, flat `tiles[]` array |
| **Collision** | `WorldMap.isSolid(x, y)` — tile lookup by world coordinates. `0` = walkable, non-zero = solid |
| **Boundaries** | Out-of-bounds treated as solid walls |
| **Hitbox** | 4-point corner checks (radius 6 units) in `MovementSystem` for smooth collision response |
| **Compatibility** | Standard JSON format works with Godot/Tiled map editors |

---

### Desktop Client

> `client/` — A thin rendering terminal. No game logic — just draw what the server says.

| Feature | Detail |
|---------|--------|
| **Framework** | LibGDX 1.14.0 with LWJGL3 backend |
| **Window** | 800x600, VSync, 60 FPS target |
| **Rendering** | Tile-based map from `world.json` + entity sprites from server state |
| **Player Sprite** | `player.png` (16x16) |
| **Input** | WASD → `MoveRequest` packets sent to server |
| **HUD** | FPS counter, connection status overlay |
| **Networking** | `GameClient` — Ktor TCP, coroutine-based async I/O |
| **Entity Sync** | `ConcurrentHashMap<EntityId, Pair<Float, Float>>` updated from `RenderState` packets |
| **Auto-Prune** | Entities not in server state are removed locally |

---

### Android Client

> `android/` — Same game, mobile form factor. Shares all logic from the desktop client module.

| Feature | Detail |
|---------|--------|
| **Entry Point** | `AndroidLauncher` extends `AndroidApplication`, wraps `ClientLauncher` |
| **Shared Code** | Depends on `:client` (excludes desktop-only LWJGL3 deps) |
| **Natives** | `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64` |
| **Emulator** | Default host `10.0.2.2` (Android emulator loopback to host machine) |
| **Target SDK** | 35 (compileSdk 35, minSdk 24) |
| **Permissions** | `INTERNET` |
| **Orientation** | Landscape, fullscreen, no title bar |
| **Sensors** | Accelerometer/compass disabled (2D game, not needed) |

---

## Quick Start

### Prerequisites

- **JDK 21** (or 17+)
- **Docker** (for PostgreSQL + Redis)
- **Gradle 9.3.1** (wrapper included)

### 1. Start the databases

```bash
docker-compose up -d
```

### 2. Launch the server

```bash
launch_backend.bat
```

Or via Gradle:

```bash
./gradlew :server:run
```

**Admin dashboard:** [http://localhost:8080/](http://localhost:8080/) — live TPS, clients, ECS stats.

### 3. Launch the desktop client

```bash
launch_client.bat
```

Or:

```bash
./gradlew :client:run
```

*Open multiple client windows to see real-time multiplayer sync.*

### 4. Android (optional)

1. Open project in **Android Studio**
2. Set **JDK 17** in File → Project Structure → SDK (AGP 9.0.1 requirement)
3. Gradle sync (uses wrapper 9.3.1)
4. Run **android** configuration on emulator or device

---

## Project Structure

```
runesandrocks/
├── shared/              # OtterCore — packet protocol, Kryo codec, shared types
│   └── src/main/kotlin/com/runesandrocks/shared/net/
│       ├── Packet.kt          # Sealed packet hierarchy
│       ├── PacketRegistry.kt  # Type ID registry + Kryo serialization
│       └── PacketCodec.kt     # Length-prefixed binary framing
├── server/              # OtterServer — authoritative game server
│   └── src/main/kotlin/com/runesandrocks/server/
│       ├── ServerLauncher.kt       # Entry point, wires all systems
│       ├── ecs/                    # Entity Component System
│       │   ├── Engine.kt           # Core ECS engine
│       │   ├── Components.kt       # Position, Velocity
│       │   ├── System.kt           # Abstract system base
│       │   ├── MovementSystem.kt   # Physics + collision
│       │   ├── NetworkSyncSystem.kt# Visibility + broadcast
│       │   └── SpatialGrid.kt     # Chunk-based partitioning
│       ├── loop/TickLoop.kt        # Fixed-timestep game loop
│       ├── network/GameServer.kt   # Ktor TCP server
│       ├── db/                     # Persistence layer
│       │   ├── DatabaseFactory.kt  # PostgreSQL + HikariCP
│       │   ├── RedisFactory.kt     # Jedis pool
│       │   └── PlayerRepository.kt # Two-tier CRUD
│       ├── admin/                  # Web dashboard
│       │   ├── AdminServer.kt      # Ktor HTTP + WebSocket
│       │   └── AdminRoutes.kt      # REST + live metrics
│       └── world/WorldMap.kt       # Tilemap + collision
├── client/              # Desktop LibGDX client
│   └── src/main/kotlin/com/runesandrocks/client/
│       ├── ClientLauncher.kt       # LibGDX app, rendering, input
│       └── network/GameClient.kt   # Ktor TCP client
├── android/             # Android LibGDX client
│   └── src/main/kotlin/.../AndroidLauncher.kt
├── DOCS/                # Project documentation
├── docker-compose.yml
├── launch_backend.bat
├── launch_client.bat
└── build.gradle.kts
```

---

## Testing

| Suite | Location | Focus |
|-------|----------|-------|
| **EngineTest** | `server/src/test/.../ecs/EngineTest.kt` | Entity lifecycle, component add/remove/query, system registration |
| **TickLoopTest** | `server/src/test/.../loop/TickLoopTest.kt` | Fixed-timestep accuracy, accumulator behavior |
| **GameServerTest** | `server/src/test/.../network/GameServerTest.kt` | Connection handling, packet routing |
| **PacketRegistryTest** | `server/src/test/.../shared/net/PacketRegistryTest.kt` | Serialization round-trips, ID mapping |

**Framework:** JUnit 5 (Jupiter) via Gradle JUnit Platform. Priority: unit tests on core ECS and network logic. Integration and load tests deferred until deployment phase.

```bash
./gradlew :server:test
```

---

## Documentation

| Document | Purpose |
|----------|---------|
| [**SUMMARY.md**](DOCS/SUMMARY.md) | High-level status, tech stack, quick links |
| [**SCRATCHPAD.md**](DOCS/SCRATCHPAD.md) | Active roadmap, phased tasks, blockers |
| [**ARCHITECTURE.md**](DOCS/ARCHITECTURE.md) | ECS, networking, data flow |
| [**SBOM.md**](DOCS/SBOM.md) | Security Bill of Materials (dependencies) |
| [**CHANGELOG.md**](DOCS/CHANGELOG.md) | Version history |
| [**My_Thoughts.md**](DOCS/My_Thoughts.md) | Design decisions and rationale |

---

## Contributing

We follow **K.I.S.S.**, **YAGNI**, and **DRY**. See [STYLE_GUIDE.md](DOCS/STYLE_GUIDE.md) for conventions.

---

<p align="center">
  <i>Built with passion.</i>
</p>
