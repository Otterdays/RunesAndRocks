# Audit: OtterEngine V1

**System:** Custom Kotlin MMORPG Engine  
**Version:** 1.0  
**Scope:** ECS, TickLoop, Systems, Spatial Grid  
**Last Updated:** 2026-02-23

---

## Entity Component System (ECS)

### Implementation Pattern

**Entity Storage:** Integer IDs (autoincrement from 1)
```kotlin
typealias EntityId = Int
private var nextEntityId: EntityId = 1
private val activeEntities = mutableSetOf<EntityId>()
```

**Component Storage:** Type-indexed sparse maps
```kotlin
private val components = mutableMapOf<KClass<out Component>, MutableMap<EntityId, Component>>()
```

**System Registration:** Ordered list, executed sequentially
```kotlin
private val systems = mutableListOf<System>()
```

### Component Inventory

| Component | Fields | Mutable | Used By |
|-----------|--------|---------|---------|
| `Position` | `x: Float, y: Float` | Yes | MovementSystem, NetworkSyncSystem, SpatialGrid |
| `Velocity` | `dx: Float, dy: Float` | Yes | MovementSystem |

### System Inventory

| System | Target Components | Phase | Responsibility |
|--------|-------------------|-------|----------------|
| `MovementSystem` | Position + Velocity | Update | Apply velocity, collision detection |
| `NetworkSyncSystem` | Position | Update | Visibility culling, broadcast RenderState |

### ECS Performance Characteristics

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|-----------------|------------------|-------|
| `createEntity()` | O(1) | O(1) | Auto-increment ID |
| `destroyEntity()` | O(C) | O(C) | C = component types, removes from all maps |
| `addComponent()` | O(1) | O(1) | Hash map insertion |
| `getComponent()` | O(1) | O(1) | Hash map lookup |
| `getEntitiesWith()` | O(min(N₁, N₂, ...)) | O(K) | K = smallest component set |

### Thread Safety Model

ECS runs single-threaded on TickLoop thread. Thread safety achieved via:

```kotlin
// Network thread queues, ECS thread drains
private val taskQueue = java.util.concurrent.ConcurrentLinkedQueue<() -> Unit>()

fun update(delta: Float) {
    while (true) {
        val task = taskQueue.poll() ?: break
        task()  // Execute on ECS thread
    }
    systems.forEach { it.update(delta) }
}
```

**Concurrency Rule:** All component mutation happens on ECS thread only. Network threads enqueue tasks.

---

## TickLoop

### Fixed-Timestep Implementation

```
Real time elapsed ──▶ Accumulator (capped at 5 ticks)
                          │
        ┌─────────────────┼─────────────────┐
        ↓                 ↓                 ↓
   Accumulator      Run update()      Subtract delta
   >= delta?         (max 5x)          from accumulator
```

### Configuration

| Property | Value | Rationale |
|----------|-------|-----------|
| Target TPS | 20 | 50ms per tick, network-friendly |
| Delta | 0.05s | 1/TPS |
| Max Accumulator | 0.25s | Spiral guard (5 ticks max) |
| Log Interval | 5 seconds | Human-readable TPS reports |

### Tick Duration Tracking

Metrics captured per-tick and snapshot every 5 seconds:

```kotlin
// Per tick
val tickStart = System.nanoTime()
onTick()
val tickElapsed = System.nanoTime() - tickStart

// Rolling window (5-second snapshots)
tickDurationSumNs += tickElapsed
tickDurationCount++
if (tickElapsed > worstTickNs) worstTickNs = tickElapsed
```

**Current Baseline (Phase 8):**
- Target budget: 50ms
- Typical observed: <1ms (near-empty world)
- Worst observed: ~2ms (with 2 players, no NPCs)

### Sleep Scheduling

```kotlin
private fun sleepRemainder(delta: Double, acc: Double) {
    val sleepTime = (delta - acc) * 1000
    if (sleepTime > 1) {
        Thread.sleep(sleepTime.toLong())
    }
}
```

CPU-efficient: sleeps instead of busy-spinning when ahead of schedule.

---

## MovementSystem

### Algorithm

1. Query all entities with Position + Velocity
2. For each entity:
   - Calculate proposed new position: `pos.x + vel.dx`, `pos.y + vel.dy`
   - 4-point hitbox collision check at corners (radius 6 units)
   - If any corner collides with solid tile → cancel velocity component
   - If clear → apply position update
   - Update spatial grid registration

### Collision Detection

```kotlin
private fun wouldCollide(x: Float, y: Float): Boolean {
    val r = 6f  // Hitbox radius
    // Check all 4 corners
    return world.isSolid(x - r, y - r) ||
           world.isSolid(x + r, y - r) ||
           world.isSolid(x - r, y + r) ||
           world.isSolid(x + r, y + r)
}
```

**Limitations:**
- No sliding response (player stops completely on collision)
- No continuous collision detection (fast movement can tunnel)
- Single hitbox radius for all entities

### Spatial Grid Integration

Entities re-register to grid after movement:
```kotlin
grid.update(entity, newX, newY)
```

---

## NetworkSyncSystem

### Visibility Culling (9-Chunk Window)

```
┌─────┬─────┬─────┐
│  -1 │  0  │ +1  │  ← y-1
├─────┼─────┼─────┤
│  -1 │PLAYER│ +1 │  ← y
├─────┼─────┼─────┤
│  -1 │  0  │ +1  │  ← y+1
└─────┴─────┴─────┘
   x-1   x   x+1
```

For each player entity:
1. Get current chunk from SpatialGrid
2. Query 3x3 chunk window (9 chunks total)
3. Collect all entities in those chunks
4. Build `RenderState` packet with positions
5. Send to client's connection

### Broadcast Behavior

| Event | Broadcast Scope | Packet |
|-------|-----------------|--------|
| Player login | All clients | `SpawnEntity` |
| Player disconnect | All clients | `UnspawnEntity` |
| Player movement | Chunk window only | `RenderState` |

---

## SpatialGrid

### Implementation

```kotlin
class SpatialGrid(private val chunkSize: Float = 32f) {
    private val chunks = ConcurrentHashMap<ChunkCoord, MutableSet<EntityId>>()
    private val entityLocations = ConcurrentHashMap<EntityId, ChunkCoord>()
}

data class ChunkCoord(val cx: Int, val cy: Int)
```

### Operations

| Operation | Complexity | Thread Safety |
|-----------|------------|---------------|
| `register(entity, x, y)` | O(1) | ConcurrentHashMap |
| `update(entity, x, y)` | O(1) | Atomic remove + add |
| `remove(entity)` | O(1) | ConcurrentHashMap |
| `getChunk(x, y)` | O(1) | Pure calculation |
| `queryChunks(chunks)` | O(N) | CopyOnWriteArraySet |

---

## Engine Gaps & Technical Debt

| Issue | Severity | Notes |
|-------|----------|-------|
| Velocity applied raw, no acceleration | Low | Snappy but unrealistic |
| No diagonal speed normalization | Low | Diagonal movement faster |
| Collision stops instead of slides | Low | Feels "sticky" |
| No entity type distinction | Medium | Players vs NPCs same path |
| No AI system | Medium | Phase 9 addresses this |
| Systems run in registration order | Low | No priority/phase concept |

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Baseline ECS, TickLoop, Systems captured | Claude |
