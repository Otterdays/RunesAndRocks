package com.runesandrocks.client.network

import com.runesandrocks.shared.net.Packet
import com.runesandrocks.shared.net.PacketCodec
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap

/**
 * TCP game client using Ktor raw sockets. Connects to server,
 * sends pings, measures round-trip latency.
 * [TRACE: ARCHITECTURE.md]
 */
class GameClient(
    private val host: String = "127.0.0.1",
    private val port: Int = DEFAULT_PORT
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private lateinit var selectorManager: SelectorManager
    private lateinit var socket: Socket
    private lateinit var readChannel: ByteReadChannel
    private lateinit var writeChannel: ByteWriteChannel
    private lateinit var scope: CoroutineScope

    @Volatile
    var connected = false
        private set
    @Volatile
    var lastLatencyMs = -1L
        private set
        
    @Volatile
    var myEntityId: Int = -1
        private set

    val entities = ConcurrentHashMap<Int, Pair<Float, Float>>()

    suspend fun connect() {
        selectorManager = SelectorManager(Dispatchers.IO)
        socket = aSocket(selectorManager).tcp().connect(host, port)
        readChannel = socket.openReadChannel()
        writeChannel = socket.openWriteChannel(autoFlush = false)
        connected = true
        logger.info("[CLIENT] Connected to {}:{}", host, port)
    }

    fun startListening() {
        scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch { listenLoop() }
    }

    suspend fun sendPing() {
        val timestamp = System.nanoTime()
        PacketCodec.write(writeChannel, Packet.Ping(timestamp))
        logger.debug("[CLIENT] Ping sent (ts={})", timestamp)
    }

    suspend fun sendLogin(username: String) {
        PacketCodec.write(writeChannel, Packet.LoginRequest(username))
        logger.info("[CLIENT] Login request sent for '{}'", username)
    }
    
    suspend fun sendMove(dx: Float, dy: Float) {
        if (!connected) return
        try {
            PacketCodec.write(writeChannel, Packet.MoveRequest(dx, dy))
        } catch (e: Exception) {}
    }

    fun disconnect() {
        connected = false
        if (::socket.isInitialized) socket.close()
        if (::selectorManager.isInitialized) selectorManager.close()
        if (::scope.isInitialized) scope.cancel()
        logger.info("[CLIENT] Disconnected")
    }

    private suspend fun listenLoop() {
        try {
            while (true) {
                val packet = PacketCodec.read(readChannel)
                when (packet) {
                    is Packet.Pong -> {
                        lastLatencyMs = (System.nanoTime() - packet.timestamp) / 1_000_000
                        logger.debug("[CLIENT] Pong received — latency: {}ms", lastLatencyMs)
                    }
                    is Packet.LoginResponse -> {
                        myEntityId = packet.entityId
                        logger.info("[CLIENT] Login Response: success={}, entityId={}", packet.success, packet.entityId)
                    }
                    is Packet.RenderState -> {
                        // Soft sync all entities positions smoothly (prevents jitter missing spawned entities)
                        packet.entities.forEach { (id, pos) ->
                            entities[id] = pos
                        }
                        
                        // Prune lost entities. 
                        val serverKeys = packet.entities.keys
                        entities.keys.retainAll(serverKeys)
                    }
                    is Packet.SpawnEntity -> {
                        entities[packet.entityId] = Pair(packet.x, packet.y)
                    }
                    is Packet.UnspawnEntity -> {
                        entities.remove(packet.entityId)
                    }
                    else -> {
                        logger.warn("[CLIENT] Unknown packet type {}", packet::class.simpleName)
                    }
                }
            }
        } catch (e: Exception) {
            if (connected) {
                logger.info("[CLIENT] Connection lost: {}", e.message)
            }
            connected = false
        }
    }

    companion object {
        const val DEFAULT_PORT = 25565
    }
}
