# SCRATCHPAD

Active tasks, blockers, and phased roadmap. Compact at 500 lines; never delete.

---

## Active Tasks

- [x] Phase 0: Tooling and Skeleton — complete
- [x] Phase 1: Server Heartbeat (fixed-timestep loop) — complete
- [x] Phase 2: Networking Foundation — complete
- [x] Phase 3: Shared Protocol (sealed packets, Kryo, registry) — complete
- [x] Admin Web Dashboard — complete
- [x] Phase 4: ECS Core built and integrated — complete
- [x] Phase 5: Client Window — complete
- [x] Phase 6: First Multiplayer Sync — complete

## Blockers

- (none)

## Future Considerations (Observability & Ops)

- **Metrics Export:** Implement Prometheus/Grafana export instead of purely custom WS payloads for heavier production loads.
- **Detailed Player Tools:** Admin dashboard should allow inspection of specific player states (inventory, location, health) via ECS querying.
- **Server Health History:** Storing historical CPU/Memory/TPS spikes instead of just current live snapshot.
- **Structured Logging:** Moving from standard SLF4J strings to structured JSON logging (Logback layout) when deploying in Docker.

## Last 5 Actions

1. 2026-02: **MainMenuScreen Skin fix:** Fixed Android crash (`No Drawable registered with name: default`). Renamed Skin resources to distinct names (`font`, `button-up`, etc.); added Pixmap disposal.
2. 2026-02: **PlayerRepository.kt:** Added Exposed 1.0.0 `eq` import for `Players.username eq username` query.
3. 2026-02: **Server Admin WebUI Enhancement:** CallLogging, HikariCP/Redis metrics, GC trigger button.
4. 2026-02: **Exposed 1.0.0** — Migrated from 0.50.1; updated imports to `org.jetbrains.exposed.v1.*` in DatabaseFactory and PlayerRepository.
5. 2026-02: **Android build fixed:** Migrated to AGP 9 built-in Kotlin.

---

## Phased Roadmap

Distilled from `tasklist_1.md`. Each phase has concrete acceptance criteria.

### Phase 0: Tooling and Skeleton

**Goal:** Git init, Gradle multi-module compiles, CI-ready, DOCS complete.

| Task | Acceptance Criteria |
|------|---------------------|
| Git init | Repo initialized, .gitignore for Kotlin/Gradle |
| Gradle multi-module | `shared`, `server`, `client` modules exist and compile |
| DOCS complete | All DOCS files populated, reference material archived |
| CI-ready | Build script/task runs `./gradlew build` successfully |

---

### Phase 1: Server Heartbeat

**Goal:** Fixed-timestep loop running at 20 TPS, unit tested, logging.

| Task | Acceptance Criteria |
|------|---------------------|
| Fixed-timestep loop | Server runs at 20 TPS, accumulator logic correct |
| Unit test | Loop tick count matches expected over N seconds |
| Logger | "[SERVER] Started on port X" and tick-rate logging |

---

### Phase 2: Networking Foundation

**Goal:** Ktor TCP server accepts connections; client connects; ping/pong round-trip.

| Task | Acceptance Criteria |
|------|---------------------|
| Server listener | Ktor TCP server binds to port, accepts connections |
| Client socket | Client connects to server IP:port |
| Connection log | Server logs "[SERVER] Client connected: IP" |
| Ping/Pong | Client sends PingPacket, server replies PongPacket, client prints latency |

---

### Phase 3: Shared Protocol

**Goal:** Sealed packet interface in shared module, KryoNet serialization, packet registry.

| Task | Acceptance Criteria |
|------|---------------------|
| Sealed interface | All packets extend shared `Packet` (or sealed hierarchy) |
| KryoNet | Packets serialize/deserialize to bytes |
| Registry | Packet IDs registered, client and server agree on mapping |

---

### Phase 4: ECS Core

**Goal:** Entity/Component/System framework, MovementSystem, basic spatial grid.

| Task | Acceptance Criteria |
|------|---------------------|
| ECS framework | Entity IDs, component storage, system iteration |
| MovementSystem | Processes Position + Velocity, updates positions |
| Spatial grid | Chunks (e.g. 32×32), entities register by coordinates |

---

### Phase 5: Client Window

**Goal:** LibGDX desktop window, sprite rendering, receives server state.

| Task | Acceptance Criteria |
|------|---------------------|
| LibGDX window | Desktop window opens, black screen, FPS counter |
| Client socket | Connects to server, receives packets |
| Sprite load | Single 16×16 sprite loads from assets |

---

### Phase 6: First Multiplayer

**Goal:** Login packet, server spawns entity, client renders player at server-authoritative position.

| Task | Acceptance Criteria |
|------|---------------------|
| LoginRequest | Client sends on connect |
| Server spawn | Server creates player at (0,0), sends SpawnPlayerPacket |
| Client render | Client draws player sprite at packet coordinates |

---

### Phase 7: World Foundation

**Goal:** Tilemap loading, collision, spatial partitioning / chunk subscription.

| Task | Acceptance Criteria |
|------|---------------------|
| [x] Tilemap | Parse .json map format on server, load structural arrays |
| [x] Collision | Movement logic safely halted at solid layout grid indices |
| [x] Chunk subscription | Packets routed only to players in locally relevant spatial grids |

---

### Phase 8: Persistence

**Goal:** PostgreSQL schema, Redis hot cache, save/load player state.

| Task | Acceptance Criteria |
|------|---------------------|
| [x] PostgreSQL | Schema for players, inventory, etc. |
| [x] Redis | Hot path on login, flush on logout + every 5 min |
| [x] Save/load | Player state survives disconnect |

---

### Phase 9: Entities & Spawning

**Goal:** Allow the ECS to manage non-player actors, like passive roaming NPCs and aggressive enemies.

| Task | Acceptance Criteria |
|------|---------------------|
| [ ] Non-player types | ECS distinguishes `Player` from `NPC` cleanly |
| [ ] Entity spawner | Server organically dictates random spawns based on zone layout |
| [ ] Basic AI | Simple AI system component drives automated roaming logic |

---

### Phase 9: Game Systems

**Goal:** Combat, inventory, NPC/AI, loot tables.

| Task | Acceptance Criteria |
|------|---------------------|
| Combat | Damage, death, respawn |
| Inventory | Items, equipment, trading |
| NPC/AI | Basic AI, spawn tables |
| Loot | Drop rates, item stats |

---

### Phase 10: Infrastructure

**Goal:** Docker, monitoring, anti-cheat, evaluate Nakama for auth/matchmaking.

| Task | Acceptance Criteria |
|------|---------------------|
| Docker | Server runs in container |
| Monitoring | Logs, metrics (e.g. Prometheus) |
| Anti-cheat | Rate limiting, validation |
| Nakama eval | Decision doc on adopting Nakama for auth/matchmaking |
