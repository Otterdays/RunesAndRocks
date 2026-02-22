package com.runesandrocks.shared.net

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Central registry for packet type IDs and Kryo serialization.
 * ThreadLocal Kryo since instances are not thread-safe.
 * [TRACE: ARCHITECTURE.md]
 */
object PacketRegistry {

    private const val PING_ID: Byte = 0x01
    private const val PONG_ID: Byte = 0x02
    private const val LOGIN_REQ_ID: Byte = 0x03
    private const val LOGIN_RES_ID: Byte = 0x04
    private const val SPAWN_ENT_ID: Byte = 0x05
    private const val UNSPAWN_ENT_ID: Byte = 0x06
    private const val RENDER_STATE_ID: Byte = 0x07
    private const val MOVE_REQ_ID: Byte = 0x08

    private val kryoLocal = ThreadLocal.withInitial { createKryo() }

    private val idToClass = mapOf(
        PING_ID to Packet.Ping::class.java,
        PONG_ID to Packet.Pong::class.java,
        LOGIN_REQ_ID to Packet.LoginRequest::class.java,
        LOGIN_RES_ID to Packet.LoginResponse::class.java,
        SPAWN_ENT_ID to Packet.SpawnEntity::class.java,
        UNSPAWN_ENT_ID to Packet.UnspawnEntity::class.java,
        RENDER_STATE_ID to Packet.RenderState::class.java,
        MOVE_REQ_ID to Packet.MoveRequest::class.java
    )

    private val classToId = mapOf(
        Packet.Ping::class.java to PING_ID,
        Packet.Pong::class.java to PONG_ID,
        Packet.LoginRequest::class.java to LOGIN_REQ_ID,
        Packet.LoginResponse::class.java to LOGIN_RES_ID,
        Packet.SpawnEntity::class.java to SPAWN_ENT_ID,
        Packet.UnspawnEntity::class.java to UNSPAWN_ENT_ID,
        Packet.RenderState::class.java to RENDER_STATE_ID,
        Packet.MoveRequest::class.java to MOVE_REQ_ID
    )

    private fun createKryo(): Kryo {
        val k = Kryo()
        k.register(Packet.Ping::class.java)
        k.register(Packet.Pong::class.java)
        k.register(Packet.LoginRequest::class.java)
        k.register(Packet.LoginResponse::class.java)
        k.register(Packet.SpawnEntity::class.java)
        k.register(Packet.UnspawnEntity::class.java)
        k.register(Packet.RenderState::class.java)
        k.register(Packet.MoveRequest::class.java)
        k.register(Pair::class.java)
        return k
    }

    fun serializePayload(packet: Packet): Pair<Byte, ByteArray> {
        val typeId = classToId[packet.javaClass]
            ?: throw IllegalArgumentException("Unregistered packet type: ${packet.javaClass}")
        val baos = ByteArrayOutputStream()
        val output = Output(baos)
        kryoLocal.get().writeObject(output, packet)
        output.flush()
        val bytes = baos.toByteArray()
        return typeId to bytes
    }

    fun deserializePayload(typeId: Byte, payload: ByteArray): Packet {
        val clazz = idToClass[typeId]
            ?: throw IllegalArgumentException("Unknown packet type ID: 0x${String.format("%02X", typeId)}")
        val input = Input(ByteArrayInputStream(payload))
        @Suppress("UNCHECKED_CAST")
        return kryoLocal.get().readObject(input, clazz) as Packet
    }
}
