# 🤖 AI Master Context Guide

Welcome, fellow agent. This file is your **immediate orientation beacon**. Read this before making assumptions, scanning code, or executing tools.

## 1. Project Philosophy
- **Identity:** "Runes And Rocks" (the game) runs on "OtterEngine" (our custom-built Kotlin Server/Client framework).
- **Architecture:** Client is a "dumb terminal" visualizer. The Server is the single source of truth.
- **Rules:** KISS (Keep It Simple, Stupid), DOTI (Don't Over-Think It), YAGNI (You Aren't Gonna Need It). Prioritize readability, UX, and clean abstraction over premature optimization.
- **Environment:** Windows OS. Use `.\` paths and `PowerShell`/`.bat` scripts when prompting execution.

## 2. Technical Stack
- **Language:** Kotlin 2.3.10 (JDK 21)
- **Build System:** Gradle 9.3.1 (Kotlin DSL). Multi-project structure:
  - `:shared` -> Common classes (Game Packets, Kryo serializers).
  - `:server` -> Ktor TCP socket server, ECS engine (`TickLoop` @ 20 TPS), relational database logic.
  - `:client` -> LibGDX Lwjgl3Application UI (`Scene2D`) and graphical rendering (`SpriteBatch`).
- **Persistence:** PostgreSQL 16 (cold storage via Exposed 1.0.0 ORM), Redis 7.2 (hot cache via Jedis), pooled via HikariCP. 
- **Deployment:** A multi-stage Dockerfile natively builds the server and links it to DB containers (`docker-compose.prod.yml`). For iterative local dev without blocking ports, use `docker-compose.yml` to spin up just the databases.

## 3. Development Workflow (Local Windows)
1. **Databases:** Run `docker-compose up -d` (spins up Postgres 16 and Redis 7.2 on their standard ports).
2. **Backend:** Run `.\launch_backend.bat`. Boot loop is strict 20 TPS. An Admin UI sits on `http://localhost:8080`.
3. **Frontend:** Run `.\launch_client.bat`. Connects natively over localhost.
4. **Compilation Check:** Test your code natively without booting apps using `./gradlew assemble --console=plain -q`.

## 4. Documentation Architecture
We strictly maintain persistent memory across sessions. You MUST update these files continuously during your work:
- **`DOCS/SCRATCHPAD.md`:** Active memory, task lists, and step-by-step phased roadmap. Read this first to align with current objectives. Only append to "Last 5 Actions", never delete.
- **`DOCS/CHANGELOG.md`:** Live version history of specific changes.
- **`DOCS/ARCHITECTURE.md`:** High-level project structural logic, data flow, and components.
- **`DOCS/SUMMARY.md`:** The broad tracking index.
- **`DOCS/My_Thoughts.md`:** Internal monologue. Log complex architectural rationale here so future AIs understand *why* you did something.
- **`DOCS/SBOM.md`:** Track all injected libraries and their specific dependency versions.
- **`DOCS/SERVER_UI_UPGRADE.md`:** Active 23-item checklist for Admin Dashboard upgrades. Phase A (items 1–4) is in progress — item 1 complete.
- **`DOCS/OTTERMAP_PLAN.md`:** Full blueprint for the OtterMap world editor tool and entire asset → world pipeline. 5-layer plan, 8 milestones. Start with M1 (WorldMap v2 parser) + M2 (TileRenderer + Camera).

## 5. Coding Standards & Guidelines
- Follow `DOCS/STYLE_GUIDE.md` (camelCase vars, PascalCase objects, UPPER_SNAKE_CASE constants).
- **Limits:** Max 50 lines per function, 400 lines per file.
- **Server Safety:** The server tick loop runs sequentially. Ensure asynchronous execution (like incoming Ktor network socket triggers) uses `ConcurrentLinkedQueue` to cache actions and process them during the main loop, avoiding `ConcurrentModificationException` during ECS iterations.
- **Client Rendering:** Heavy resources (Images, Fonts) must be queued by `Assets.kt` (AssetManager) and pre-loaded inside `LoadingScreen.kt`, rather than instantiating `Texture()` calls dynamically in loops.

Make your strikes precise. Keep it structured. Let's build something awesome.
