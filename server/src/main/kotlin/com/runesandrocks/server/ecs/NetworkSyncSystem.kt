package com.runesandrocks.server.ecs

import com.runesandrocks.server.network.GameServer
import com.runesandrocks.shared.net.Packet

class NetworkSyncSystem(
    engine: Engine, 
    private val gameServer: GameServer,
    private val grid: SpatialGrid
) : System(engine) {
    override fun update(delta: Float) {
        val allEntities = engine.getEntitiesWith(Position::class)
        
        // 1. Reconcile Grid for moving entities
        for (id in allEntities) {
            val pos = engine.getComponent(id, Position::class)!!
            grid.addOrUpdate(id, pos.x, pos.y)
        }
        
        // 2. Broadcast relevant patches to each connected client purely within their 9 active grid chunks
        for (conn in gameServer.getActiveConnections()) {
            val myId = conn.entityId ?: continue
            val myPos = engine.getComponent(myId, Position::class) ?: continue
            
            val myChunk = grid.getChunk(myPos.x, myPos.y)
            val relevantChunks = grid.getRelevantChunks(myChunk)
            
            val state = mutableMapOf<Int, Pair<Float, Float>>()
            
            for (chunk in relevantChunks) {
                for (otherId in grid.getEntitiesInChunk(chunk)) {
                    val pos = engine.getComponent(otherId, Position::class)
                    if (pos != null) {
                        state[otherId] = Pair(pos.x, pos.y)
                    } else {
                        // Might be dead / out of sync with ECS arrays
                        grid.remove(otherId)
                    }
                }
            }
            
            if (state.isNotEmpty()) {
                val packet = Packet.RenderState(state)
                gameServer.sendToClient(conn, packet)
            }
        }
    }
}
