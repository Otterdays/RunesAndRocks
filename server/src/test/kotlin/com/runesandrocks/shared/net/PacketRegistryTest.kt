package com.runesandrocks.shared.net

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PacketRegistryTest {

    @Test
    fun `Ping round-trip serializes and deserializes`() {
        val original = Packet.Ping(12345L)
        val (typeId, payload) = PacketRegistry.serializePayload(original)
        val restored = PacketRegistry.deserializePayload(typeId, payload)
        assertEquals(original, restored)
        assertTrue(restored is Packet.Ping)
        assertEquals(12345L, (restored as Packet.Ping).timestamp)
    }

    @Test
    fun `Pong round-trip serializes and deserializes`() {
        val original = Packet.Pong(67890L)
        val (typeId, payload) = PacketRegistry.serializePayload(original)
        val restored = PacketRegistry.deserializePayload(typeId, payload)
        assertEquals(original, restored)
        assertTrue(restored is Packet.Pong)
        assertEquals(67890L, (restored as Packet.Pong).timestamp)
    }

    @Test
    fun `unknown type ID throws`() {
        val unknownId: Byte = 0x7F
        val payload = ByteArray(0)
        assertThrows(IllegalArgumentException::class.java) {
            PacketRegistry.deserializePayload(unknownId, payload)
        }
    }
}
