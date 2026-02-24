# Audit: Technical Debt Register

**System:** Runes & Rocks + OtterEngine V1  
**Purpose:** Tracking known issues, TODOs, refactoring candidates  
**Last Updated:** 2026-02-23

---

## Active Debt Items

### High Priority

| ID | Issue | Location | Impact | Resolution |
|----|-------|----------|--------|------------|
| TD-001 | Empty exception handlers swallow errors | `GameServer.kt:67-68,79-80,148-149` | Debugging difficult | Add structured logging |
| TD-002 | No rate limiting on MoveRequest | `GameServer.kt:153-162` | Flooding vulnerability | Add packet throttle |
| TD-003 | Velocity bounds unchecked | `MovementSystem.kt:14-15` | Speed hacking possible | Clamp max velocity |
| TD-017 | PacketCodec payload length unbounded | `PacketCodec.kt:24-26` | OOM if client sends huge length | Cap max payload (e.g. 64KB) |
| TD-018 | Engine task queue unbounded | `Engine.kt:48-49` | MoveRequest flood fills queue, tick lag | Rate limit or bounded queue |

### Medium Priority

| ID | Issue | Location | Impact | Resolution |
|----|-------|----------|--------|------------|
| TD-004 | Diagonal movement faster than cardinal | `MovementSystem.kt` | Gameplay imbalance | Normalize vector |
| TD-005 | Collision stops instead of slides | `MovementSystem.kt` | Feels "sticky" | Implement slide response |
| TD-006 | No connection timeout | `GameServer.kt` | Ghost connections | Add SO_TIMEOUT |
| TD-007 | No keepalive/ping timeout | Protocol level | NAT/firewall drops | Client timeout check |
| TD-008 | Single-threaded ECS | `Engine.kt` | Scale ceiling | Document limit |
| TD-009 | No auto-save during gameplay | `PlayerRepository.kt` | Session loss on crash | Periodic flush task |
| TD-010 | Spatial grid chunk size hardcoded | `SpatialGrid.kt` | Tuning difficult | Configurable |

### Low Priority

| ID | Issue | Location | Impact | Resolution |
|----|-------|----------|--------|------------|
| TD-011 | No tests for NetworkSyncSystem | `server/src/test/` | Coverage gap | Add test class |
| TD-012 | No tests for SpatialGrid | `server/src/test/` | Coverage gap | Add test class |
| TD-013 | Admin dashboard no auth | `AdminRoutes.kt` | Unauthorized access if exposed | Add simple auth |
| TD-014 | Docker ps error swallowed | `AdminRoutes.kt:42-45` | Silent failures | Log warning |
| TD-019 | Duplicate LoginRequest leaks entity | `GameServer.kt:132-151` | Second login overwrites entityId; first entity orphaned | Reject login if already logged in |
| TD-020 | Username no validation | `PlayerRepository.kt:31` | Empty/whitespace accepted | Trim, reject empty, length check |
| TD-015 | Skin resources manually disposed | `MainMenuScreen.kt` | Brittle lifecycle | AssetManager pattern |
| TD-016 | RenderState packet size unbounded | `NetworkSyncSystem.kt` | Large worlds = large packets | Chunk pagination |

## TODO Comments in Code

| File | Line | TODO | Priority |
|------|------|------|----------|
| No explicit TODOs found | — | — | — |

## FIXME Comments in Code

| File | Line | FIXME | Priority |
|------|------|-------|----------|
| No explicit FIXMEs found | — | — | — |

## Refactoring Queue

### Phase 9 Candidates

1. **Extract AI System foundation** — Prepare for NPC movement
2. **Add EntityType component** — Distinguish Player/NPC/Prop
3. **Implement spawn tables** — Zone-based entity spawning

### Phase 10 Candidates

1. **Packet rate limiting infrastructure** — Generic throttle per client
2. **Connection management limits** — Max players, queue system
3. **Admin authentication** — Dashboard login system
4. **Structured logging** — JSON format for production

### Post-Launch Candidates

1. **ECS multi-threading** — Job-based system execution
2. **World sharding** — Horizontal scaling support
3. **Redis cluster support** — HA cache layer
4. **PostgreSQL read replicas** — Scale-out queries

## Completed Debt (Historical)

| Date | Item | Resolution |
|------|------|------------|
| 2026-02 | CVE-2025-68161 Log4j | Forced to 2.25.3+ |
| 2026-02 | Android Skin crash | Renamed resources, added disposal |
| 2026-02 | TickLoop no metrics | Added avg/worst tracking |

---

## Debt Metrics

| Category | Count | Trend |
|----------|-------|-------|
| High Priority | 5 | +2 (PacketCodec, task queue) |
| Medium Priority | 8 | Stable |
| Low Priority | 8 | +2 (duplicate login, username) |
| **Total Active** | **21** | — |

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Technical debt register initialized, 17 items cataloged | Claude |
| 2026-02-23 | Code audit: corrected TD-001 line refs; added TD-017–TD-020 (PacketCodec, task queue, duplicate login, username) | Claude |
