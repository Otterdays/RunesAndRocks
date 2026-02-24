# Server-UI Upgrade Checklist

All items extract real data from the hand-rolled OtterServer internals and surface them in the Admin Web Dashboard.

---

## Checklist

### Phase A: Real-Time Performance Telemetry

- [x] **1. Tick Duration** — Instrument `TickLoop.kt` to track avg/worst tick duration in ms. Expose via WS + UI card. Shows how much of the 50ms budget each tick consumes. ✅
- [ ] **2. Process CPU Load** — Replace broken `systemLoadAverage` (returns -1.0 on Windows) with `com.sun.management.OperatingSystemMXBean.getProcessCpuLoad()`. Show as percentage.
- [ ] **3. Network I/O** — Add `packetsSent` / `packetsReceived` AtomicLong counters to `GameServer.kt`. Increment in `broadcast()` and `handleClient()`. Display packets/sec on dashboard.
- [ ] **4. ECS Task Queue Depth** — Expose `Engine.taskQueue.size` to the admin payload. A growing queue signals incoming lag.

### Phase B: Deep JVM Diagnostics

- [ ] **5. GC Pressure Stats** — Pull `GarbageCollectorMXBean` totals (collection count + cumulative pause ms) per collector. Display on dashboard.
- [ ] **6. Heap Breakdown** — Use `MemoryPoolMXBeans` to show Eden/Survivor/Old Gen usage individually.
- [ ] **7. DB Pool Full Stats** — Expose HikariCP `idleConnections`, `totalConnections`, and `threadsAwaitingConnection` alongside existing active.
- [ ] **8. Redis Key Count** — Call `jedis.dbSize()` to show total cached keys.
- [ ] **9. Server Log Tail** — Create ring buffer log appender, serve last N lines over WS into a scrolling panel.

### Phase C: World & Game State Inspector

- [ ] **10. Live World Map Heatmap** — Render a miniature pixel-map of `world.json` in the Admin UI and overlay live player positions from ECS `Position` components as glowing dots. Pure read-only. Zero cost to the server since you already have the data. Insanely powerful.
- [ ] **11. ECS Component Inspector** — Admin can click a connected client's row and see their live ECS component state (Position, Velocity) polled from the Engine. No game code change needed. Just a new REST endpoint `GET /api/clients/{id}/state`.
- [ ] **12. SpatialGrid Chunk Visualizer** — Render the active chunk occupancy as a grid overlay on the minimap. See exactly which chunks are "hot" (holding players) and which are empty. Great for debugging sync radius.

### Phase D: Server Controls & Management

- [ ] **13. Broadcast Server Message** — A text field + button in the Admin UI that fires a `Packet.ServerMessage(text)` to all connected clients. Displayed as a chat-style notification in the game. Classic moderation tool.
- [ ] **14. World Reload Trigger** — Button to hot-reload `world.json` from disk without restarting the server. Queue a task on the ECS engine thread to re-parse the world and rebuild collision maps.
- [ ] **15. Player Save-All** — Button to trigger `PlayerRepository.savePlayer()` for every connected client immediately. Safety net before planned maintenance.
- [ ] **16. Graceful Shutdown** — Button that triggers a server MOTD countdown broadcast (`"Server restarting in 30 seconds"`), saves all players, then gracefully calls `TickLoop.stop()` and `GameServer.stop()`. Far better than just killing the process.
- [ ] **17. Maintenance Mode Toggle** — A flag on `GameServer` that blocks new logins but lets existing players stay connected. Toggleable on the dashboard with one click.

### Phase E: Historical Trends (Time-Series)

- [ ] **18. TPS Sparkline** — Keep a rolling 60-second array of TPS snapshots in the client JS. Render as an inline SVG sparkline on the TPS card. Immediately see if TPS is trending down before it crashes.
- [ ] **19. Memory Trend Chart** — Same pattern as above: 60-second rolling memory readings rendered as a live area chart using plain Canvas (no libs needed). Early warning for memory leaks.
- [ ] **20. Packet Rate History** — Once counters are in (Step 3), plot packets/sec over time. A sudden spike is a sign of a client sending malformed or repeated packets (potential abuse vector).

### Phase F: Security & Observability

- [ ] **21. Connection Rate Limiter Readout** — Track connections-per-minute by IP in `GameServer`. Surface the top 5 connecting IPs and their rates. Basic DDoS early-warning.
- [ ] **22. Admin Action Audit Log** — Every kick, GC trigger, world reload, or forced save from the Admin UI should be timestamped and stored in a small in-memory ring buffer. Show it as a read-only table on the dashboard. Accountability trail.
- [ ] **23. Failed Login Tracker** — Track `LoginRequest` packets that fail validation (bad username, etc.). Surface count by IP. Early detection of brute-force or fuzzing attempts.

---

## Priority Order

| Phase | When |
|-------|------|
| **A (1–4)** | Now — critical real-time performance visibility |
| **B (5–9)** | Soon — JVM health deep dive |
| **C (10–12)** | High fun factor, low cost — game-state inspector |
| **D (13–17)** | Before any real players — server management tooling |
| **E (18–20)** | After initial performance baseline is established |
| **F (21–23)** | Before public access — basic security posture |
