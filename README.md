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

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [Documentation](#-documentation)
- [Contributing](#-contributing)

---

## Overview

**Runes & Rocks** is the premier game. It runs on **OtterEngine** — a bespoke, bare-metal MMORPG engine (server, ECS, networking, persistence) built from the ground up for massive multiplayer loads across **Desktop** and **Android**. Every simulation tick, collision, and state update runs server-side on OtterEngine; the client is a visual terminal.

| Feature | Description |
|---------|-------------|
| **Server-Authoritative** | 100% of physics, collisions, and state calculations happen on the secure headless server. Client is a dumb terminal. |
| **Custom ECS** | OtterEngine data-oriented Kotlin ECS with efficient matrices, spatial chunking, and deterministic tick loops. |
| **Fixed-Timestep** | Deterministic gameplay at precisely 20 TPS, independent of network or I/O lag. |
| **Raw TCP + Kryo** | High-speed binary serialization over Ktor TCP sockets — no HTTP overhead. |
| **Godot-Ready Maps** | Server parses standard JSON 2D tilemaps (Godot/Tiled) and builds collision boundaries at runtime. |
| **Dockerized Persistence** | PostgreSQL (cold storage) + Redis (hot cache) via HikariCP and Jedis. |

---

## Architecture

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
├── shared/          # Packet protocol, Kryo, shared types
├── server/          # Game server, ECS, persistence
├── client/          # Desktop LibGDX client
├── android/         # Android LibGDX client
├── DOCS/            # Project documentation
├── docker-compose.yml
├── launch_backend.bat
├── launch_client.bat
└── build.gradle.kts
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
