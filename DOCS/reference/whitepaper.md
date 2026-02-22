Building a custom engine for an MMORPG is one of the most punishing, yet rewarding, engineering challenges you can take on. Transitioning from rendering standalone 2D games in HTML5 Canvas or managing procedural environments in Three.js requires a fundamental shift in state management. The client is no longer the source of truth; it is merely a dumb terminal reflecting a chaotic, shared world.

Here is a formal, highly technical whitepaper outlining the exact stack, server architecture, and in-house tooling required to build a scalable, top-down 2D MMORPG using Kotlin and the JVM.

---

# Technical Architecture Whitepaper: Custom Kotlin-Based MMORPG Engine

## 1. Global Stack Overview

The overarching design philosophy is strict decoupling. The project is split into three primary domains: `Client`, `Server`, and `Shared`, managed via a Multi-Module Gradle configuration.

* **Core Languages:** Kotlin (Primary), Java (Legacy interop).
* **Build System:** Gradle (Kotlin DSL).
* **Networking:** Ktor (Asynchronous raw TCP sockets via Coroutines).
* **Serialization:** KryoNet (High-speed binary serialization).
* **Data Persistence:** PostgreSQL (Relational truth) + Redis (In-memory cache).
* **Client Rendering:** LibGDX (Cross-platform OpenGL wrapper).

## 2. The Authoritative Headless Server

The server is a headless console application responsible for validating every single action. If a player attempts to move, the server calculates the physics, checks for collisions, and updates the state.

### 2.1 The Fixed-Timestep Simulation Loop

To ensure deterministic physics and fair combat across different network latencies, the server runs on a strict fixed-timestep loop.

The loop is defined by the Ticks Per Second (TPS). For a 2D MMO, 20 to 30 TPS is standard. The delta time () is calculated as:


The server accumulates real-world time elapsed between CPU cycles. Once the accumulator surpasses the  threshold, the server executes exactly one simulation tick, subtracting  from the accumulator. This prevents the game logic from running faster on better hardware.

### 2.2 Spatial Partitioning (Grid Routing)

An  distance check for every entity against every other entity will instantly crash your server at scale. The world map must be divided into a grid of spatial "chunks" (e.g., 32x32 tiles).

* Entities register themselves to a specific chunk based on their  coordinates.
* When an event occurs (e.g., a monster spawns, a player speaks), the server only routes that network packet to players actively subscribed to that chunk and its immediately adjacent neighbors.

### 2.3 Entity Component System (ECS)

Object-oriented inheritance (e.g., `class Player extends Character extends Entity`) becomes a rigid nightmare in an MMO. Instead, the server uses an ECS.

* **Entities:** Simple integer IDs (e.g., `EntityID: 4012`).
* **Components:** Plain data classes (e.g., `Position(x, y)`, `Health(current, max)`, `Inventory(items)`).
* **Systems:** Logic loops that process components (e.g., the `MovementSystem` iterates over all entities that have both a `Position` and a `Velocity` component).

## 3. Networking & Serialization Protocol

MMORPGs prioritize reliable state over absolute real-time twitch reflexes (unlike an FPS, which uses UDP). TCP is sufficient and ensures packet order, which is critical for inventory management and economy transactions.

* **Packet Structure:** Every packet inherits from a sealed interface in the `Shared` module.
* **Binary Serialization:** Using Kryo, packets are stripped of metadata. Instead of sending a JSON string like `{"type":"move", "x":10, "y":20}`, Kryo registers the packet class to an ID (e.g., `ID 14`) and sends a compact byte array: `[14, 0A, 14]`. This reduces bandwidth costs by over 80%.

## 4. In-House Tooling Requirements

Relying solely on code to build the game world is a bottleneck. You must build internal tools early.

* **Tilemap Editor Integrator:** While you can use a third-party tool like *Tiled* to visually paint your maps, you need a custom Gradle task to parse the `.tmx` files and bake them into a proprietary binary format for the server. The server needs to know collision data (which tiles are walls), but it doesn't need the actual PNG image data.
* **Item & Loot Table Editor:** A simple internal web dashboard (built with Ktor server-side rendering or a lightweight React frontend) to allow non-programmers to tweak monster drop rates, item stats, and spawn locations without recompiling the Java codebase.
* **Asset Packer:** A script that takes hundreds of individual sprite PNGs and packs them into a single Texture Atlas (sprite sheet) to minimize OpenGL draw calls on the client.

## 5. Data Persistence & Economy Integrity

The economy is the most fragile part of an MMO.

* **Redis (The Hot Path):** When a player logs in, their data is pulled from PostgreSQL into Redis. As the player fights and trades, their state updates incredibly fast in the Redis cache.
* **PostgreSQL (The Cold Path):** Every 5 minutes, and explicitly upon logging out, the Redis state is flushed to PostgreSQL.
* **ACID Transactions:** If Player A trades 1,000 gold to Player B, this must be wrapped in a strict database transaction. If the server crashes on step 2, the entire transaction rolls back to prevent money duplication.

---

This architectural framework provides the rigid structure needed to scale.

Would you like to drill down into the schema design for the Entity Component System (ECS), or would you prefer to look at how to implement the binary packet serialization using Kryo?
