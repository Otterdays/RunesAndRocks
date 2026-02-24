# Audit: System Architecture

**System:** Runes & Rocks + OtterEngine V1  
**Scope:** Module boundaries, data flow, deployment topology  
**Last Updated:** 2026-02-23

---

## Module Dependency Graph

```
shared (OtterCore)
  ↑      ↑
  |      |
server   client ←── android
(OtterServer)    (depends on client)
```

## Module Responsibilities

| Module | Lines (approx) | Purpose | Tech |
|--------|----------------|---------|------|
| `shared/` | 300 | Packet protocol, serialization, shared types | Kryo, Ktor-io |
| `server/` | 1,200 | Authoritative game server, ECS, persistence | Ktor, Exposed, Jedis |
| `client/` | 800 | Desktop rendering, networking | LibGDX LWJGL3 |
| `android/` | 100 | Mobile entry point | LibGDX Android |

## Data Flow Audit

### Login Flow
```
Client                     Server                     Database
  |                          |                           |
  |── LoginRequest ─────────▶|                           |
  |                          |── findOrCreatePlayer() ───▶|
  |                          |◀──────── player row ───────|
  |                          |── hydrate Redis ───────▶|
  |                          |                           |
  |                          |── ECS.createEntity()      |
  |                          |── add Position, Velocity  |
  |◀── LoginResponse ────────|                           |
  |◀── SpawnEntity (broadcast)|                          |
```

### Movement Flow
```
Client                     Server                     Broadcast
  |                          |                           |
  |── MoveRequest(dx,dy) ───▶|                          |
  |                          |── set Velocity component  |
  |                          | (ECS thread)               |
  |                          |                           |
  |                          |── MovementSystem.apply()  |
  |                          |── collision check         |
  |                          |── update Position         |
  |                          |                           |
  |                          |── NetworkSyncSystem     |
  |                          |   visibility culling      |
  |                          |── build RenderState       |
  |◀── RenderState ──────────|                           |
  |◀── (other clients) ─────|── broadcast to chunk ─────▶|
```

### Disconnect Flow
```
Client                     Server                     Database
  |                          |                           |
  |── disconnect ─────────────▶|                          |
  |                          |── remove from clients map |
  |                          |── ECS.destroyEntity()     |
  |                          |── broadcast Unspawn       |
  |                          |── savePlayer() ─────────▶|
  |                          |   (Redis → PostgreSQL)    |
  |                          |◀──────── OK ──────────────|
  |                          |── Redis cleanup           |
```

## Thread Model Audit

| Thread Pool | Purpose | Owner |
|-------------|---------|-------|
| `Dispatchers.IO` | TCP socket accept/read/write | GameServer |
| `Dispatchers.IO` | Admin HTTP/WebSocket | AdminServer |
| Main thread | TickLoop (blocking) | TickLoop |
| ECS thread | All game logic, system updates | TickLoop.onTick |
| HikariCP pool | Database connections | DatabaseFactory |
| Jedis pool | Redis connections | RedisFactory |

**Thread Safety Mechanisms:**
- `ConcurrentLinkedQueue<() -> Unit>` for network → ECS task handoff
- `ConcurrentHashMap` for client connection storage
- `CopyOnWriteArraySet` for spatial grid entity sets

## Deployment Topology

### Development (Local)
```
Host Machine
├── Docker: postgres:16-alpine (port 5432)
├── Docker: redis:7.2-alpine (port 6379)
├── JVM: GameServer (port 25565)
└── JVM: AdminServer (port 8080)
```

### Production (Docker)
```
Docker Network
├── Container: server (multi-stage Alpine, JDK 21 → JRE 21)
│   ├── Exposed: 25565 (game)
│   └── Exposed: 8080 (admin)
├── Container: postgres:16-alpine
│   └── Volume: persistent data
└── Container: redis:7.2-alpine
    └── Volume: persistent cache
```

## Configuration Surface

| Config | Environment Variable | Default | Location |
|--------|---------------------|---------|----------|
| Game port | `PORT` | 25565 | GameServer |
| Admin port | `ADMIN_PORT` | 8080 | AdminServer |
| TPS | (hardcoded) | 20 | TickLoop |
| JDBC URL | `JDBC_URL` | localhost:5432 | DatabaseFactory |
| DB User | `DB_USER` | postgres | DatabaseFactory |
| DB Pass | `DB_PASS` | postgres | DatabaseFactory |
| Redis Host | `REDIS_HOST` | localhost | RedisFactory |
| Redis Port | `REDIS_PORT` | 6379 | RedisFactory |

## Known Architectural Gaps

| Gap | Risk | Planned Resolution |
|-----|------|-------------------|
| No rate limiting on MoveRequest | Medium | Phase 9/10 — add packet rate limiting |
| Single-threaded ECS | Low-Medium | Currently sufficient for target scale |
| No horizontal scaling | Medium | World sharding not implemented |
| Docker socket access for admin | Low | Read-only `docker ps` only |

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Baseline architecture captured at Phase 8 complete | Claude |
