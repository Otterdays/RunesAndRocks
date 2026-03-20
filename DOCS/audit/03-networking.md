<!-- PRESERVATION RULE: Never delete or replace content. Append or annotate only. -->

# Audit: Networking Layer

**System:** OtterCore Protocol + Ktor TCP  
**Scope:** Packet hierarchy, serialization, transport, client lifecycle  
**Last Updated:** 2026-02-23

---

## Protocol Stack

```
Application (Packets)
       ↓
Kryo Serialization
       ↓
Length-Prefixed Framing [typeId:1B][length:4B][payload:NB]
       ↓
Ktor Raw TCP
       ↓
OS Network Stack
```

## Packet Hierarchy

```kotlin
sealed interface Packet {
    data class Ping(val timestamp: Long) : Packet
    data class Pong(val timestamp: Long) : Packet
    data class LoginRequest(val username: String) : Packet
    data class LoginResponse(val entityId: Int, val success: Boolean, val message: String) : Packet
    data class SpawnEntity(val entityId: Int, val x: Float, val y: Float) : Packet
    data class UnspawnEntity(val entityId: Int) : Packet
    data class RenderState(val positions: Map<Int, Pair<Float, Float>>) : Packet
    data class MoveRequest(val dx: Float, val dy: Float) : Packet
}
```

## Packet Registry

| Packet | ID | Direction | Frequency | Size (approx) |
|--------|----|-----------|-----------|---------------|
| `Ping` | 0 | C→S | 1/second | ~16 bytes |
| `Pong` | 1 | S→C | 1/second | ~16 bytes |
| `LoginRequest` | 2 | C→S | Once per session | ~32 bytes + username |
| `LoginResponse` | 3 | S→C | Once per session | ~24 bytes |
| `SpawnEntity` | 4 | S→C | On login/spawn | ~20 bytes |
| `UnspawnEntity` | 5 | S→C | On disconnect | ~16 bytes |
| `RenderState` | 6 | S→C | 20/second (per player) | ~16 + 12*N entities |
| `MoveRequest` | 7 | C→S | Per input (~60/sec max) | ~16 bytes |

**Kryo Registration:** `Packet::class` and all sealed subclasses registered in `PacketRegistry.init()`.

## Binary Framing (PacketCodec)

### Write Path

```kotlin
fun write(channel: ByteWriteChannel, packet: Packet) {
    val bytes = PacketRegistry.serialize(packet)
    val typeId = PacketRegistry.getId(packet::class)
    
    channel.writeByte(typeId.toByte())      // 1 byte
    channel.writeInt(bytes.size)             // 4 bytes (big-endian)
    channel.writeFully(bytes)                // N bytes
    channel.flush()
}
```

### Read Path

```kotlin
suspend fun read(channel: ByteReadChannel): Packet {
    val typeId = channel.readByte().toInt()
    val length = channel.readInt()
    val bytes = ByteArray(length)
    channel.readFully(bytes)
    return PacketRegistry.deserialize(typeId, bytes)
}
```

**Buffer Size:** 4-byte length field limits payload to ~2GB (sufficient for game packets).

## Connection Lifecycle

```
┌─────────┐    ┌─────────┐    ┌─────────┐    ┌─────────┐
│  INIT   │───▶│ CONNECT │───▶│  LOGIN  │───▶│  PLAY   │
└─────────┘    └─────────┘    └─────────┘    └─────────┘
                    │                               │
                    ▼                               ▼
               ┌─────────┐                   ┌─────────┐
               │DISCONNECT│                   │DISCONNECT│
               └─────────┘                   └─────────┘
```

### State Transitions

| Transition | Trigger | Server Action |
|------------|---------|---------------|
| `INIT → CONNECT` | TCP accept | Create ClientConnection, add to clients map |
| `CONNECT → LOGIN` | Receive LoginRequest | DB lookup, ECS spawn, broadcast SpawnEntity |
| `LOGIN → PLAY` | Send LoginResponse | Ready for game packets |
| `PLAY → DISCONNECT` | Socket error/EOF | Save to DB, cleanup ECS, broadcast UnspawnEntity |

## Client Connection State

```kotlin
data class ClientConnection(
    val id: Long,                          // Connection ID (monotonic)
    val socket: Socket,
    val readChannel: ByteReadChannel,
    val writeChannel: ByteWriteChannel,
    val connectedAt: Long,                  // Timestamp
    var entityId: Int? = null,             // ECS entity (null until login)
    var dbId: Int = -1                     // PostgreSQL player ID
)
```

## Server-Side Connection Management

```kotlin
private val clients = ConcurrentHashMap<Long, ClientConnection>()
private val nextClientId = AtomicLong(1)
```

| Metric | Type | Thread Safety |
|--------|------|---------------|
| `connectedCount` | Int (computed) | `ConcurrentHashMap.size` |
| `nextClientId` | AtomicLong | CAS increment |
| `clients` | ConcurrentHashMap | Lock-free reads |

## Security Surface

| Vector | Current State | Risk |
|--------|--------------|------|
| Packet flooding | No rate limiting | **Medium** — client could spam MoveRequest |
| Payload size | 4-byte length limit | Low — max 2GB, but single-allocation |
| Connection limit | No hard cap | Low — memory bound |
| Kryo deserialization | Default configuration | Low — classes registered, not polymorphic |
| Username validation | No sanitization | Low — SQL injection prevented by Exposed |

**Planned Hardening:**
- Per-client packet rate limiting (Phase 10)
- Connection limit configuration
- Payload size sanity check (<1MB)

## Latency Characteristics

| Measurement | Expected | Notes |
|-------------|----------|-------|
| Ping/Pong RTT | <5ms (localhost) | Local development |
| Ping/Pong RTT | 20-100ms (WAN) | Typical player internet |
| Server tick processing | <2ms | Observed with 2 players |
| RenderState broadcast | Immediate | Sent during sync system |

## Known Issues

| Issue | Location | Impact |
|-------|----------|--------|
| Empty catch blocks | GameServer.kt:149, 165, 180 | Errors silently swallowed |
| No connection timeout | Socket config | Dead connections persist |
| No keepalive | TCP default | NAT/firewall may drop idle |

---

## Audit Trail

| Date | Entry | Author |
|------|-------|--------|
| 2026-02-23 | Baseline networking audit | Claude |
