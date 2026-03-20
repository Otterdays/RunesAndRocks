<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

This is a great approach. Having a clean, granular technical spec ready to hand off to a junior developer is the best way to keep a massive project like a custom engine from derailing early on.

Here is a fleshed-out, highly structured technical brief that you can drop directly into your workspace (like that `tasklist_1.md` file) for your junior dev to start working through. It covers the specific architectural patterns required for a responsive, Kotlin-based authoritative server.

---

### 🛠️ Junior Dev Task Breakdown: Engine Core Foundation

#### 1. The Server Heartbeat (Fixed-Timestep Loop)

For tight top-down combat and movement mechanics to feel fair, the server cannot rely on variable frame rates. It must execute logic predictably.

* **Implement the Fixed Rate:** The server's game world must be updated at a predefined, fixed rate, such as 20 or 60 ticks per second.
* **Create the Time Accumulator:** Implement an accumulator variable to track the real time elapsed between loop cycles; the simulation must consume this accumulated time in discrete, fixed steps.
* **The While-Loop Logic:** Structure the core loop to continuously check if the accumulated time exceeds the fixed delta time ().
* **Tick the Engine:** If the accumulator is greater than the delta, run the server's `update()` logic and subtract the delta from the accumulator.
* *Note:* The server loop is purely mathematical. It simulates the state but does not render a single pixel.

#### 2. Asynchronous Networking (Ktor Raw Sockets)

We are leveraging Kotlin's coroutines and Ktor's raw TCP sockets to handle thousands of concurrent connections without blocking the main thread.

* **Initialize the Server Listener:** Create a `SelectorManager` using `Dispatchers.IO` and bind a TCP socket to our designated server port.
* **Initialize the Client Connection:** The client (whether compiling for Android or desktop) will use the exact same `SelectorManager` setup, but call `connect()` targeting the server's IP address.
* **Establish Data Channels:** Once the server accepts a connection, open asynchronous read and write channels (`ByteReadChannel` and `ByteWriteChannel`) to handle the incoming and outgoing byte streams.
* **Configure Flushing:** For immediate packet dispatch during early development, ensure `autoFlush = true` is set on the `ByteWriteChannel`.

#### 3. Client-Server Data Synchronization

To prevent spaghetti code as the game scales, the visual client must neatly interpret the raw data packets sent by the server.

* **Isolate Network Data:** Separate the entities and properties that require network replication from those that do not.
* **Batch Decoding:** When the client receives a stream of packets from the server, decode the messages into a single batch first.
* **Apply the State:** Iterate through the local entities, process each component that requires a network update, and fetch the corresponding message payload from the decoded batch to apply the new state.

---

Would you like me to write out a quick interface template for the Network Packet structure to add to the `Shared` module notes next?
