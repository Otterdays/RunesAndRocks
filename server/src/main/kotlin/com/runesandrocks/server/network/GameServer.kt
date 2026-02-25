package com.runesandrocks.server.network

import com.runesandrocks.server.ecs.Engine
import com.runesandrocks.server.ecs.Position
import com.runesandrocks.server.ecs.Velocity
import com.runesandrocks.shared.net.Packet
import com.runesandrocks.shared.net.PacketCodec
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicInteger

import com.runesandrocks.server.ecs.SpatialGrid

import com.runesandrocks.server.db.PlayerRepository

/**
 * TCP game server using Ktor raw sockets. Accepts connections,
 * tracks clients, and handles ping/pong for Phase 2.
 * [TRACE: ARCHITECTURE.md]
 */
class GameServer(
    private val host: String = "0.0.0.0",
    private val port: Int = DEFAULT_PORT,
    private val engine: Engine,
    private val grid: SpatialGrid
) {
    val gamePort: Int get() = port
    private val logger = LoggerFactory.getLogger(javaClass)
    private val nextClientId = AtomicLong(1)
    private val clients = ConcurrentHashMap<Long, ClientConnection>()

    val packetsSent = AtomicLong(0)
    val packetsReceived = AtomicLong(0)
    val bytesIn = AtomicLong(0)
    val bytesOut = AtomicLong(0)
    val unknownPacketCount = AtomicLong(0)
    val codecErrors = AtomicLong(0)

    @Volatile
    var maintenanceMode = false

    // Security: failed logins by IP (cumulative)
    private val failedLoginsByIp = ConcurrentHashMap<String, AtomicInteger>()
    fun getFailedLogins(): Map<String, Int> = failedLoginsByIp.mapValues { it.value.get() }

    // Security: per-client packet counters for abuse detection
    private val clientPacketCounts = ConcurrentHashMap<Long, AtomicLong>()
    fun getTopSenders(limit: Int = 10): List<Pair<Long, Long>> =
        clientPacketCounts.entries
            .sortedByDescending { it.value.get() }
            .take(limit)
            .map { it.key to it.value.get() }

    private lateinit var selectorManager: SelectorManager
    private lateinit var serverSocket: ServerSocket
    private lateinit var scope: CoroutineScope

    val connectedCount: Int get() = clients.size

    fun getClients(): List<ClientInfo> =
        clients.values.map { ClientInfo(it.id, it.socket.remoteAddress.toString(), it.connectedAt) }
        
    fun getActiveConnections(): List<ClientConnection> = clients.values.toList()

    fun kickClient(id: Long): Boolean {
        val conn = clients.remove(id) ?: return false
        conn.socket.close()
        logger.info("[NET] Client {} kicked by admin", id)
        // Ensure entity is cleaned up on forced kick
        conn.entityId?.let { eId ->
            engine.queueTask { 
                engine.destroyEntity(eId) 
                grid.remove(eId)
            }
        }
        return true
    }
    
    fun broadcast(packet: Packet) {
        if (!::scope.isInitialized) return
        scope.launch {
            clients.values.forEach { conn ->
                try {
                    val written = PacketCodec.write(conn.writeChannel, packet)
                    packetsSent.incrementAndGet()
                    bytesOut.addAndGet(written.toLong())
                } catch (e: Exception) {
                    // Handled by client disconnect logic
                }
            }
        }
    }
    
    fun sendToClient(conn: ClientConnection, packet: Packet) {
        if (!::scope.isInitialized) return
        scope.launch {
            try {
                val written = PacketCodec.write(conn.writeChannel, packet)
                packetsSent.incrementAndGet()
                bytesOut.addAndGet(written.toLong())
            } catch (e: Exception) {
                // Handled by client disconnect logic
            }
        }
    }

    fun start() {
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        selectorManager = SelectorManager(Dispatchers.IO)

        scope.launch {
            serverSocket = aSocket(selectorManager).tcp().bind(host, port)
            logger.info("[NET] Server listening on {}:{}", host, port)

            while (isActive) {
                val socket = serverSocket.accept()
                val clientId = nextClientId.getAndIncrement()
                val address = socket.remoteAddress.toString()
                logger.info("[NET] Client connected: id={} addr={}", clientId, address)

                val conn = ClientConnection(
                    id = clientId,
                    socket = socket,
                    readChannel = socket.openReadChannel(),
                    writeChannel = socket.openWriteChannel(autoFlush = false),
                    connectedAt = System.currentTimeMillis()
                )
                clients[clientId] = conn

                launch { handleClient(conn) }
            }
        }
    }

    fun stop() {
        logger.info("[NET] Shutting down server...")
        clients.values.forEach { it.socket.close() }
        clients.clear()
        if (::serverSocket.isInitialized) serverSocket.close()
        if (::selectorManager.isInitialized) selectorManager.close()
        scope.cancel()
        logger.info("[NET] Server stopped")
    }

    private suspend fun handleClient(conn: ClientConnection) {
        try {
            while (true) {
                val (packet, bytesRead) = try {
                    PacketCodec.readWithSize(conn.readChannel)
                } catch (e: Exception) {
                    codecErrors.incrementAndGet()
                    throw e
                }
                packetsReceived.incrementAndGet()
                bytesIn.addAndGet(bytesRead.toLong())
                clientPacketCounts.getOrPut(conn.id) { AtomicLong(0) }.incrementAndGet()

                when (packet) {
                    is Packet.Ping -> {
                        val written = PacketCodec.write(conn.writeChannel, Packet.Pong(packet.timestamp))
                        packetsSent.incrementAndGet()
                        bytesOut.addAndGet(written.toLong())
                        logger.debug("[NET] Ping from client {}, pong sent", conn.id)
                    }
                    is Packet.LoginRequest -> {
                        val remoteIp = conn.socket.remoteAddress.toString()
                        if (maintenanceMode) {
                            logger.info("[NET] Client {} login rejected (maintenance mode)", conn.id)
                            failedLoginsByIp.getOrPut(remoteIp) { AtomicInteger(0) }.incrementAndGet()
                            scope.launch {
                                try {
                                    val written = PacketCodec.write(
                                        conn.writeChannel,
                                        Packet.LoginResponse(0, false, "Server is in maintenance mode. Try again later.")
                                    )
                                    packetsSent.incrementAndGet()
                                    bytesOut.addAndGet(written.toLong())
                                } catch (_: Exception) {}
                            }
                        } else {
                            logger.info("[NET] Client {} requested login with username: {}", conn.id, packet.username)

                            val playerState = try {
                                PlayerRepository.loginPlayer(packet.username)
                            } catch (e: Exception) {
                                failedLoginsByIp.getOrPut(remoteIp) { AtomicInteger(0) }.incrementAndGet()
                                logger.error("[NET] Login DB failure for client {}: {}", conn.id, e.message)
                                scope.launch {
                                    try { PacketCodec.write(conn.writeChannel, Packet.LoginResponse(0, false, "Login failed. Please try again.")) } catch (_: Exception) {}
                                }
                                null
                            }
                            if (playerState != null) {
                                conn.dbId = playerState.dbId
                                engine.queueTask {
                                    val entityId = engine.createEntity()
                                    engine.addComponent(entityId, Position(playerState.x, playerState.y))
                                    engine.addComponent(entityId, Velocity(0f, 0f))
                                    conn.entityId = entityId
                                    scope.launch {
                                        try {
                                            val written = PacketCodec.write(
                                                conn.writeChannel,
                                                Packet.LoginResponse(entityId, true, "Welcome to the world")
                                            )
                                            packetsSent.incrementAndGet()
                                            bytesOut.addAndGet(written.toLong())
                                            broadcast(Packet.SpawnEntity(entityId, playerState.x, playerState.y))
                                        } catch (_: Exception) {}
                                    }
                                }
                            }
                        }
                    }
                    is Packet.MoveRequest -> {
                        conn.entityId?.let { eId ->
                            engine.queueTask {
                                val vel = engine.getComponent(eId, Velocity::class)
                                if (vel != null) {
                                    vel.dx = packet.dx
                                    vel.dy = packet.dy
                                }
                            }
                        }
                    }
                    else -> {
                        unknownPacketCount.incrementAndGet()
                        logger.warn("[NET] Unknown packet type {} from client {}",
                            packet::class.simpleName, conn.id)
                    }
                }
            }
        } catch (e: Exception) {
            logger.info("[NET] Client {} disconnected: {}", conn.id, e.message)
        } finally {
            clients.remove(conn.id)
            clientPacketCounts.remove(conn.id)
            conn.socket.close()
            conn.entityId?.let { eId ->
                engine.queueTask { 
                    engine.destroyEntity(eId) 
                    grid.remove(eId)
                }
                broadcast(Packet.UnspawnEntity(eId))
            }
            if (conn.dbId != -1) {
                PlayerRepository.savePlayer(conn.dbId)
            }
        }
    }

    companion object {
        const val DEFAULT_PORT = 25565
    }
}

data class ClientConnection(
    val id: Long,
    val socket: Socket,
    val readChannel: ByteReadChannel,
    val writeChannel: ByteWriteChannel,
    val connectedAt: Long,
    var entityId: Int? = null,
    var dbId: Int = -1
)

data class ClientInfo(val id: Long, val address: String, val connectedAt: Long)
