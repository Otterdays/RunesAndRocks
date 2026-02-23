package com.runesandrocks.server.network

import com.runesandrocks.shared.net.Packet
import com.runesandrocks.shared.net.PacketCodec
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameServerTest {

    private lateinit var server: GameServer
    private val testPort = 19876

    @BeforeEach
    fun setUp() {
        server = GameServer(port = testPort, engine = com.runesandrocks.server.ecs.Engine(), grid = com.runesandrocks.server.ecs.SpatialGrid(32f))
        server.start()
        Thread.sleep(300)
    }

    @AfterEach
    fun tearDown() {
        server.stop()
    }

    @Test
    fun `server accepts connection and tracks client`() = runBlocking {
        val selector = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selector).tcp().connect("127.0.0.1", testPort)

        delay(200)
        assertEquals(1, server.connectedCount, "Server should track 1 connected client")

        socket.close()
        selector.close()
    }

    @Test
    fun `ping returns pong with same timestamp`() = runBlocking {
        val selector = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selector).tcp().connect("127.0.0.1", testPort)
        val read = socket.openReadChannel()
        val write = socket.openWriteChannel(autoFlush = false)

        val sentTimestamp = System.nanoTime()
        PacketCodec.write(write, Packet.Ping(sentTimestamp))

        val packet = PacketCodec.read(read)
        assertTrue(packet is Packet.Pong, "Server should reply with Pong")
        assertEquals(sentTimestamp, (packet as Packet.Pong).timestamp,
            "Pong should echo the original timestamp")

        socket.close()
        selector.close()
    }

    @Test
    fun `multiple pings return multiple pongs`() = runBlocking {
        val selector = SelectorManager(Dispatchers.IO)
        val socket = aSocket(selector).tcp().connect("127.0.0.1", testPort)
        val read = socket.openReadChannel()
        val write = socket.openWriteChannel(autoFlush = false)

        repeat(3) { i ->
            val ts = System.nanoTime() + i
            PacketCodec.write(write, Packet.Ping(ts))
            val packet = PacketCodec.read(read)
            assertTrue(packet is Packet.Pong)
            assertEquals(ts, (packet as Packet.Pong).timestamp)
        }

        socket.close()
        selector.close()
    }
}
