package com.runesandrocks.server.ecs

import com.runesandrocks.server.network.GameServer
import com.runesandrocks.shared.net.Packet

class NetworkSyncSystem(engine: Engine, private val gameServer: GameServer) : System(engine) {
    override fun update(delta: Float) {
        val entities = engine.getEntitiesWith(Position::class)
        val state = entities.associateWith { id ->
            val pos = engine.getComponent(id, Position::class)!!
            Pair(pos.x, pos.y)
        }
        
        if (state.isNotEmpty()) {
            val packet = Packet.RenderState(state)
            gameServer.broadcast(packet)
        }
    }
}
