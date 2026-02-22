package com.runesandrocks.server.ecs

import com.runesandrocks.server.world.WorldMap

class MovementSystem(engine: Engine, private val map: WorldMap) : System(engine) {
    override fun update(delta: Float) {
        val entities = engine.getEntitiesWith(Position::class, Velocity::class)
        for (entity in entities) {
            val position = engine.getComponent(entity, Position::class)!!
            val velocity = engine.getComponent(entity, Velocity::class)!!

            if (velocity.dx == 0f && velocity.dy == 0f) continue

            val futureX = position.x + (velocity.dx * delta)
            val futureY = position.y + (velocity.dy * delta)
            
            // Check corner bounds on map
            val radius = 6f // simple hitbox matching 16x16 dummy bounds roughly
            if (!map.isSolid(futureX - radius, futureY - radius) &&
                !map.isSolid(futureX + radius, futureY - radius) &&
                !map.isSolid(futureX - radius, futureY + radius) &&
                !map.isSolid(futureX + radius, futureY + radius)) {
                
                position.x = futureX
                position.y = futureY
            } else {
                // Wipe velocity if blocked
                velocity.dx = 0f
                velocity.dy = 0f
            }
        }
    }
}
