package com.runesandrocks.shared.net

import io.ktor.utils.io.*

/**
 * Length-prefixed binary codec: [typeId:1][length:4][payload:N].
 * Payload is Kryo-serialized packet. Phase 3 protocol.
 * [TRACE: ARCHITECTURE.md]
 */
object PacketCodec {

    const val HEADER_SIZE = 5 // 1 byte type + 4 bytes payload length

    suspend fun write(channel: ByteWriteChannel, packet: Packet): Int {
        val (typeId, payload) = PacketRegistry.serializePayload(packet)
        channel.writeByte(typeId)
        channel.writeInt(payload.size)
        channel.writeFully(payload, 0, payload.size)
        channel.flush()
        return HEADER_SIZE + payload.size
    }

    suspend fun read(channel: ByteReadChannel): Packet {
        val typeId = channel.readByte()
        val length = channel.readInt()
        val payload = ByteArray(length)
        channel.readFully(payload, 0, length)
        return PacketRegistry.deserializePayload(typeId, payload)
    }

    suspend fun readWithSize(channel: ByteReadChannel): Pair<Packet, Int> {
        val typeId = channel.readByte()
        val length = channel.readInt()
        val payload = ByteArray(length)
        channel.readFully(payload, 0, length)
        return Pair(PacketRegistry.deserializePayload(typeId, payload), HEADER_SIZE + length)
    }
}
