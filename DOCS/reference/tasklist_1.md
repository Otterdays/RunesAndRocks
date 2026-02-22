To build a custom MMORPG engine, your absolute first step before writing any game logic is setting up a scalable project architecture. If you mix your server code with your client code, the project will quickly become unmaintainable.

Since you are using Java/Kotlin, I highly recommend using **IntelliJ IDEA** and a build tool like **Gradle**.

Here is your phase-one checklist to get the skeleton of your engine up and running in your IDE.

### Phase 1: Project Setup & Architecture

The goal here is to create a single project with three distinct, isolated modules.

* [ ] **Initialize a Multi-Module Gradle Project:** Create a new Gradle project. You will use this to manage dependencies across your client and server.
* [ ] **Create the `Shared` Module:** This module contains code that both the client and server need to know about.
* *What goes here:* Math utilities (Vector2D), Enums (ItemTypes, Directions), and most importantly, your **Network Packet definitions** (e.g., `LoginRequestPacket`, `PlayerMovePacket`).


* [ ] **Create the `Server` Module:** This is a pure console application. It will depend on the `Shared` module.
* *What goes here:* The authoritative game loop, database connections, pathfinding algorithms, and socket listeners. **No rendering code is allowed here.**


* [ ] **Create the `Client` Module:** This is your visual game. It will depend on the `Shared` module. If you use LibGDX, this is where it lives.
* *What goes here:* Window management, OpenGL/Vulkan rendering, asset loading (sprites, sounds), and input handling (keyboard/mouse/touch).



---

### Phase 2: The Server Foundation (The Heartbeat)

Before you can draw a player, the server needs to exist and tick.

* [ ] **Implement the Game Loop:** Write a classic fixed-timestep loop in the server. It should tick at a specific rate (e.g., 20 or 30 ticks per second) to update entity states consistently, regardless of how fast the server hardware is.
* [ ] **Set up the Networking Library:** Add Ktor or Netty to your server's dependencies.
* [ ] **Open a Socket:** Write the code to start a TCP server that listens on a specific port (e.g., 8080) and accepts incoming connections.
* [ ] **Create a Basic Logger:** Ensure you can print out "[SERVER] Started on port 8080" and "[SERVER] Client connected: IP Address" so you know things are working.

---

### Phase 3: The Client Foundation (The Window)

Now you need a blank canvas that can talk to the server.

* [ ] **Initialize the App/Window:** Set up your basic desktop or Android entry point (using LibGDX or your own LWJGL wrapper). Get a window to open with a black screen and an FPS counter.
* [ ] **Implement the Client Network Socket:** Add the client-side networking code to connect to your server's IP address and port.
* [ ] **Establish the Connection:** Run the server, then run the client. Verify that the server's console logs the new connection.

---

### Phase 4: The "Hello World" of Multiplayer (Data Serialization)

To make the client and server talk efficiently, they need a shared language. You shouldn't send raw strings over the wire; you need to serialize data into bytes. **KryoNet** is a very popular, high-speed serialization library for Java/Kotlin game devs.

* [ ] **Define a Packet:** In your `Shared` module, create a simple data class like `class PingPacket(val timeSent: Long)`.
* [ ] **Send from Client:** Program the client to send the `PingPacket` to the server as soon as it connects.
* [ ] **Receive on Server:** Write a listener on the server that catches the `PingPacket`, reads the timestamp, and replies with a `PongPacket`.
* [ ] **Receive on Client:** Have the client catch the `PongPacket` and print out the latency (ping) to the console.

---

### Phase 5: The First Visuals

Once data is flowing, you can start drawing.

* [ ] **Load an Asset:** Put a single 16x16 sprite (your player) in your client's asset folder and load it into memory.
* [ ] **Send a Login Packet:** Have the client send a `LoginRequest` packet.
* [ ] **Spawn an Entity:** The server receives the login, creates a player object in memory at coordinates (X: 0, Y: 0), and replies with a `SpawnPlayerPacket` containing those coordinates.
* [ ] **Render:** The client receives the `SpawnPlayerPacket` and finally draws the sprite at the center of the screen.
