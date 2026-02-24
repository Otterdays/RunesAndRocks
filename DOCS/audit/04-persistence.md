# Audit: Persistence Layer

**System:** PostgreSQL + Redis Two-Tier Storage  
**Scope:** Data lifecycle, connection pooling, caching strategy  
**Last Updated:** 2026-02-23

---

## Two-Tier Architecture

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   Client    │────────▶│    Redis    │◀───────▶│ PostgreSQL  │
│   Action    │         │  (Hot Cache)│         │ (Cold Store)│
└─────────────┘         └─────────────┘         └─────────────┘
        │                                           │
        │                                           │
        ▼                                           ▼
   Login: Hydrate                              Durability
   Logout: Flush
   Periodic: Sync
```

## PostgreSQL (Cold Storage)

### Schema

```kotlin
object Players : Table("players") {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 64).uniqueIndex()
    val posX = float("pos_x")
    val posY = float("pos_y")
    override val primaryKey = PrimaryKey(id)
}
```

**Current Fields:**
- `id`: Auto-increment primary key
- `username`: Unique, 64 chars max
- `posX`, `posY`: Float world coordinates

### Connection Pool (HikariCP)

| Property | Value |
|----------|-------|
| Pool name | `runes-pg-pool` |
| Minimum idle | 2 |
| Maximum pool size | 10 |
| Connection timeout | 30 seconds |
| Idle timeout | 10 minutes |
| Max lifetime | 30 minutes |

### PlayerRepository Operations

| Operation | Flow | Latency |
|-----------|------|---------|
| `loginPlayer(username)` | SELECT or INSERT → hydrate Redis | ~10-50ms |
| `savePlayer(dbId)` | Redis → UPDATE → Redis cleanup | ~10-50ms |

## Redis (Hot Cache)

### Connection Pool (JedisPooled)

| Property | Value |
|----------|-------|
| Host | `REDIS_HOST` env (default: localhost) |
| Port | `REDIS_PORT` env (default: 6379) |
| Pool max total | 20 |
| Pool min idle | 10 |
| Pool max idle | 10 |

### Key Structure

| Pattern | Type | TTL | Purpose |
|---------|------|-----|---------|
| `player:{dbId}` | Hash | None (session-scoped) | Player state cache |

### Hash Fields (player:{id})

```
HGETALL player:1
1) "x"
2) "125.5"
3) "y"
4) "200.0"
5) "username"
6) "PlayerName"
```

## Data Lifecycle

### Login Flow

```
Client ──▶ Server ──▶ loginPlayer(username)
                              │
                              ├──▶ SELECT * FROM players WHERE username = ?
                              │    Found? Return : Insert new row
                              │
                              ├──▶ HSET player:{dbId} x {posX} y {posY} username {username}
                              │
                              └──▶ Return PlayerState(dbId, x, y)
```

### Live Session

```
During gameplay:
├── Position updates happen in ECS only (fast, in-memory)
├── Redis cache unchanged (position saved on logout)
└── No DB writes during gameplay (performance)
```

### Logout/Disconnect Flow

```
Socket disconnect ──▶ savePlayer(dbId)
                           │
                           ├──▶ HGET player:{dbId} x y
                           │
                           ├──▶ UPDATE players SET pos_x = ?, pos_y = ? WHERE id = ?
                           │
                           └──▶ DEL player:{dbId}
```

## Persistence Health Checks

| Check | Endpoint | Status |
|-------|----------|--------|
| PostgreSQL connectivity | Admin dashboard | Active/Idle connection count from HikariCP MXBean |
| Redis connectivity | Admin dashboard | `Jedis.ping() == "PONG"` |

## Durability Guarantees

| Scenario | Guarantee | Notes |
|----------|-----------|-------|
| Clean disconnect | Position saved to DB | savePlayer() called |
| Crash disconnect | Position at last logout | No auto-save during gameplay |
| DB unavailable | Login fails | Redis alone not sufficient |
| Redis unavailable | Login slower (direct DB) | No caching, but functional |

## Current Limitations

| Limitation | Impact | Resolution |
|------------|--------|------------|
| No auto-save during gameplay | Up to 1 session lost on crash | Add periodic flush every 5 min |
| Position only (no inventory) | Data loss for other systems | Expand schema in future phases |
| Single Redis instance | No HA on cache layer | Cluster mode for production |
| No transactions | Potential inconsistency | Add Redis→DB transaction wrapper |

## Connection Metrics Baseline

Captured at audit time (idle server, 0 players):

```
PostgreSQL Pool: 0 active, 2 idle, 2 total
Redis Pool: 1 active (ping check), 10 idle
```

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Baseline persistence audit at Phase 8 | Claude |
