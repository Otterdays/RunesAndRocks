package com.runesandrocks.shared.net

/**
 * Sealed hierarchy for all network packets. Phase 3 protocol.
 * [TRACE: ARCHITECTURE.md]
 */
sealed interface Packet {

    data class Ping(val timestamp: Long = 0L) : Packet
    data class Pong(val timestamp: Long = 0L) : Packet
    
    // Phase 6 Multiplayer Sync
    data class LoginRequest(val username: String = "") : Packet
    data class LoginResponse(val entityId: Int = 0, val success: Boolean = false, val message: String = "") : Packet
    data class SpawnEntity(val entityId: Int = 0, val x: Float = 0f, val y: Float = 0f) : Packet
    data class UnspawnEntity(val entityId: Int = 0) : Packet
    data class RenderState(val entities: Map<Int, Pair<Float, Float>> = emptyMap()) : Packet
    data class MoveRequest(val dx: Float = 0f, val dy: Float = 0f) : Packet
    data class ServerMessage(val text: String = "") : Packet
}
