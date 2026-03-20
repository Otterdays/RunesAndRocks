<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Server-UI Upgrade Checklist

All items extract real data from the hand-rolled OtterServer internals and surface them in the Admin Web Dashboard. Designed for both human operators and AI-assisted debugging.

---

## Checklist

### Phase A: Real-Time Performance Telemetry

- [x] **1. Tick Duration** — Instrument `TickLoop.kt` to track avg/worst tick duration in ms. Expose via WS + UI card. Shows how much of the 50ms budget each tick consumes. ✅
- [x] **2. Process CPU Load** — Replace broken `systemLoadAverage` (returns -1.0 on Windows) with `com.sun.management.OperatingSystemMXBean.getProcessCpuLoad()`. Show as percentage. ✅
- [x] **3. Network I/O Counters** — Add `packetsSent` / `packetsReceived` AtomicLong counters to `GameServer.kt`. Increment in `broadcast()` and `handleClient()`. Display packets/sec on dashboard. ✅
- [x] **4. Network Byte Counters** — Add `bytesIn` / `bytesOut` counters (or bytes/sec derivatives). Increment when reading/writing packet payloads. Enables bandwidth abuse detection. ✅
- [x] **5. Network Error Counters** — Track `unknownPacketCount` and `codecErrors` (deserialization failures). Surface in UI. Early signal for malformed or malicious packets. ✅
- [x] **6. ECS Task Queue Depth** — Expose `Engine.taskQueue.size` to the admin payload. A growing queue signals incoming lag. UI card with red threshold when >10. ✅

### Phase B: Deep JVM Diagnostics

- [x] **7. GC Pressure Stats** — Pull `GarbageCollectorMXBean` totals (collection count + cumulative pause ms) per collector. Display on dashboard. ✅
- [x] **8. Heap Breakdown** — Use `MemoryPoolMXBeans` to show Eden/Survivor/Old Gen usage individually. ✅
- [x] **9. DB Pool Full Stats** — Expose HikariCP `idleConnections`, `totalConnections`, and `threadsAwaitingConnection` alongside existing active. ✅
- [x] **10. Redis Key Count** — Call `jedis.dbSize()` to show total cached keys. ✅
- [x] **11. Server Log Tail** — Create ring buffer log appender, serve last N lines over WS into a scrolling panel. ✅

### Phase C: World & Game State Inspector

- [x] **12. Live World Map Heatmap** — Render a miniature pixel-map of `world.json` in the Admin UI and overlay live player positions from ECS `Position` components as glowing dots. Pure read-only. Zero cost to the server since you already have the data. ✅
- [ ] **13. ECS Component Inspector** — Admin can click a connected client's row and see their live ECS component state (Position, Velocity) polled from the Engine. REST endpoint `GET /api/clients/{id}/state`.
- [ ] **14. SpatialGrid Chunk Visualizer** — Render the active chunk occupancy as a grid overlay on the minimap. See exactly which chunks are "hot" (holding players) and which are empty.
- [ ] **15. World Inspector Endpoint** — `GET /api/world/inspector` returns sanitized read model: active chunks, entity type counts, player density per chunk. No gameplay impact.
- [ ] **16. ECS Entity Endpoint** — `GET /api/ecs/entities/{id}` returns component state for a given entity. Used by inspector panel and AI context.

### Phase D: Server Controls & Management

- [x] **17. Broadcast Server Message** — A text field + button in the Admin UI that fires a `Packet.ServerMessage(text)` to all connected clients. Displayed as a chat-style notification in the game. ✅
- [x] **18. World Reload Trigger** — Button to hot-reload `world.json` from disk without restarting the server. Queue a task on the ECS thread to re-parse the world and rebuild collision maps. ✅
- [x] **19. Player Save-All** — Button to trigger `PlayerRepository.savePlayer()` for every connected client immediately. Safety net before planned maintenance. ✅
- [x] **20. Graceful Shutdown** — Button that triggers a server MOTD countdown broadcast (`"Server restarting in 30 seconds"`), saves all players, then gracefully calls `TickLoop.stop()` and `GameServer.stop()`. ✅
- [x] **21. Maintenance Mode Toggle** — A flag on `GameServer` that blocks new logins but lets existing players stay connected. Toggleable on the dashboard with one click. ✅
- [x] **22. Control Action Return Codes** — All control endpoints (save-all, maintenance, graceful-restart, broadcast) return explicit `{success, message, timestamp}` and log to Admin Action Audit. UI shows outcome feedback. ✅

### Phase E: Historical Trends (Time-Series)

- [x] **23. TPS Sparkline** — Rolling 60-second array of TPS snapshots in client JS. Render as inline SVG sparkline on the TPS card. ✅
- [x] **24. Memory Trend Chart** — 60-second rolling memory readings as live area chart (plain Canvas). Early warning for memory leaks. ✅
- [x] **25. Packet Rate History** — Plot packets/sec over 60s window. Sudden spike = abuse vector or bug. ✅
- [ ] **26. Tick Duration Histogram** — Track 50th/95th/99th percentile tick durations, not just avg/worst. Exposes jitter. Rolling 60s buffer.
- [x] **27. Task Queue Depth Trend** — Rolling 60s buffer for task queue size. Sparkline shows backpressure buildup. ✅
- [ ] **28. Extended History Buffers** — Add 10-minute rolling buffers for TPS, tickMs, memory, packet rate. Enables longer-term trend analysis without heavy storage.
- [x] **29. Client Join/Leave Timeline** — Ring buffer of connect/disconnect events with timestamps. UI panel or table. Spot connection patterns. ✅
- [ ] **30. Error Rate Tracking** — Exceptions per minute by category (network, db, ecs). Surface in UI as "Errors: 3/min (all network)".

### Phase F: Security & Observability

- [ ] **31. Connection Rate Limiter Readout** — Track connections-per-minute by IP in `GameServer`. Surface the top 5 connecting IPs and their rates. Basic DDoS early-warning.
- [x] **32. Admin Action Audit Log** — Every kick, GC trigger, world reload, or forced save from the Admin UI timestamped and stored in a small in-memory ring buffer. Read-only table on dashboard. ✅
- [x] **33. Failed Login Tracker** — Track `LoginRequest` packets that fail validation. Surface count by IP. Early detection of brute-force or fuzzing. ✅
- [ ] **34. Failed Login by Reason** — Categorize failures (bad username, DB error, etc.). Enables targeted debugging.
- [x] **35. Top Abusive Senders** — Per-client packets/sec. Flag clients exceeding threshold (e.g. 120/sec). UI highlights offenders. ✅
- [ ] **36. Connection Age Percentiles** — P50/P95/P99 session duration. Helps spot disconnect patterns.
- [ ] **37. Security Exposure Panel** — Dedicated UI section: connections/min by IP, failed logins by reason, top senders, connection age. Single-pane security overview.

### Phase G: AI-Assistable Metadata (Self-Describing System)

*Structured data that helps AI assistants diagnose issues without reading source code.*

- [x] **38. SystemPulse Payload** — Versioned contract: `schemaVersion`, `generatedAt`, `source`. Add `GET /api/metrics/pulse` and optionally `GET /api/metrics/history/1m`. Keeps AI and human on same data contract. ✅
- [ ] **39. System Profile Payload** — Add `systemProfile` object to WebSocket: engine version, tick config (target TPS, budget, max ticks), ECS config (chunk size, component types, system types), network config (port, protocol, packet type count), persistence config (pool sizes, cache strategy).
- [x] **40. Health Score Algorithm** — Computed composite scores (0-100): overall, tickHealth, memoryHealth, networkHealth, persistenceHealth. UI card with color-coded gauge. ✅
- [x] **41. Anomalies Array** — `anomalies[]` with `{type, severity, message, suggestedFix, timestamp}`. UI panel lists active anomalies. AI uses `suggestedFix` for actionable advice. ✅
- [ ] **42. ECS Inventory Breakdown** — Per-entity-type counts (player, npc, prop), component counts by type, entity distribution by chunk. Helps spot imbalance.
- [ ] **43. ECS Pressure Indicators** — `ecs.entitiesByComponent`, `ecs.entitiesPerSystem`, `ecs.updateTimeP95`, `ecs.updateTimeP99`. Per-system timing from MovementSystem/NetworkSyncSystem. Answers "network or simulation pressure?"
- [ ] **44. Expanded Client Telemetry** — Per-client: username, entityId, session duration, latencyMs, packetsPerSec, current position (x,y), current chunk, connection state (connecting/login/playing/disconnecting), disconnectReason (when applicable).
- [ ] **45. Structured Event Stream** — New `/ws/events` endpoint emitting `{timestamp, level, category, event, data, suggestion}` for: tick_budget_exceeded, client_kicked, login_fail, redis_fail, db_pool_exhausted, world_reloaded, gc_triggered. AI monitors for actionable alerts.
- [x] **46. Last Tick Timestamp** — `lastTickTime: Long` in ms since epoch. UI shows "Last tick: 12ms ago". AI detects frozen server vs slow ticks. ✅

### Phase H: API Structure & AI Handoff

*Enables seamless human–AI collaboration on incidents.*

- [x] **47. Runbook Mode Snapshot** — `GET /api/debug/handoff` returns compact one-screen object: top 10 anomalies, biggest offenders (clients/chunks), top 5 entities by load, resource bottleneck, suggested next step. Perfect for "paste this to AI" or quick handoff. ✅
- [x] **48. Config & Environment Metadata** — Dashboard header card: engine version, server version, and UI version. Prevents "what version are we debugging?" mid-incident. ✅

### Phase I: Backwards Compatibility & Consolidation

- [ ] **49. Backwards-Compatible WS** — Keep `/ws/live` unchanged for existing consumers. New fields additive only. Document schema in `DOCS/audit/` or API spec.
- [ ] **50. Unified Metrics Endpoint** — Consider `GET /api/metrics` returning full SystemPulse + history snapshot. Single call for dashboard bootstrap.

---

## Priority Order

| Phase | Items | When |
|-------|-------|------|
| **A (1–6)** | Real-time performance: CPU, network counters, ECS queue | Now — critical visibility |
| **B (7–11)** | JVM deep dive: GC, heap, DB pool, Redis, log tail | Soon |
| **C (12–16)** | World/ECS inspector: heatmap, endpoints, chunk viz | High fun factor, low cost |
| **D (17–22)** | Server controls + return codes & audit | Before real players |
| **E (23–30)** | Historical trends: sparklines, histograms, buffers | After baseline established |
| **F (31–37)** | Security: rate limits, failed logins, exposure panel | Before public access |
| **G (38–46)** | AI metadata: SystemPulse, health, anomalies, events | As needed for AI-assisted debug |
| **H (47–48)** | Runbook handoff, config metadata | When collaborating with AI |
| **I (49–50)** | API consolidation, backwards compat | Before breaking changes |

---

## Implementation Hooks (Quick Reference)

| Item | Primary File(s) | Insertion Point |
|------|-----------------|-----------------|
| 3–5 | `GameServer.kt` | `broadcast()`, `handleClient()`, packet read/write paths |
| 6, 42, 43 | `Engine.kt`, `AdminRoutes.kt` | Expose queue size, component maps; per-system timing in System.update() |
| 15–16 | `AdminRoutes.kt`, `WorldMap.kt`, `Engine.kt` | New REST routes, read-only queries |
| 38–41, 47 | `AdminRoutes.kt` | New endpoints, health computation service |
| 45 | `AdminRoutes.kt`, various | WebSocket route + event emission from TickLoop, GameServer, etc. |

---

## UI Architecture & UX Enhancements

*Broader design recommendations for layout, control, and data collectability. Complements the checklist above.*

### Layout & Scalability

- [x] **Tabbed Navigation** — Replace single long scroll with tabs: `[ Overview | Network I/O | Docker | Server for Dummies ]`. Prevents clutter as checklist items grow. ✅
- [ ] **Chart Library** — Add lightweight charting (Chart.js or plain Canvas) for trend visualization. Raw numbers are hard to parse during incidents.
- [ ] **Global Health Header** — Expand `ws-status` to a system-wide "Health Score" (0–100) based on Phase G algorithm. Dashboard header turns amber/red when DB pool maxed, TPS drops, etc.

### Command Palette (Direct Control)

*Front-and-center controls so you don't need SSH during emergencies.*

- [x] **Maintenance Mode Toggle** — One-click block new logins; existing players stay. (See Phase D item 21.) ✅
- [x] **Force Save All** — Instant flush of all player states to DB. (See Phase D item 19.) ✅
- [x] **Broadcast Warning** — Input field to send red text to all clients (e.g. "Server restarting in 5m!"). (See Phase D item 17.) ✅
- [x] **Graceful Restart** — MOTD countdown, save all, then stop TickLoop + GameServer. (See Phase D item 20.) ✅

### AI Diagnostic & Data Collectability

*Maximize paste-to-AI handoff.*

- [x] **Copy Diagnostic Snapshot Button** — Prominent header button. Calls `GET /api/debug/handoff`, fetches dense JSON, copies to clipboard. One click → paste to AI. ✅
  - *Payload should include:* Last 50 log warnings/errors, 60s TPS/Memory history, top 5 expensive ECS systems, active DB queries, engine/build version.
- [ ] **Event Log Feed** — Scrolling terminal-like panel connected to `/ws/events`. Shows stream: `[14:02:01] INFO: Player 'Thor' logged in. [14:02:05] WARN: MovementSystem tick took 45ms`. Copy-pasteable for AI context.

### Deep Game & Engine Visibility

- [ ] **ECS Entity Breakdown** — Pie chart or table: `500 Players, 2000 NPCs, 7500 Projectiles`. (See Phase G item 42.)
- [ ] **Top Talkers / Network Abuse** — Table of clients by packets/sec. Red flag when IP exceeds threshold (e.g. 500/sec). (See Phase F item 35.)
- [x] **World Heatmap** — 2D Canvas plotting player `(x,y)` as dots. Lag + 400 dots in one corner = spatial bottleneck. (See Phase C item 12.) ✅

### Recommended Implementation Order

1. ~~**Chart.js + Sparklines**~~ — 60s TPS and Memory trends (Phase E items 23–24). ✅ Done (pure SVG).
2. ~~**Command Palette**~~ — API routes + UI buttons for Maintenance, Broadcast, Force Save. ✅ Done. Graceful Restart pending.
3. **AI Diagnostic Button** — Implement `/api/debug/handoff` and header "Copy Snapshot" button (Phase H item 47).
