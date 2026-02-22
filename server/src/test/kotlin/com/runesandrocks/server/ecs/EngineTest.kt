package com.runesandrocks.server.ecs

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EngineTest {

    @Test
    fun testEntityCreationAndDestruction() {
        val engine = Engine()
        assertEquals(0, engine.entityCount)
        
        val e1 = engine.createEntity()
        val e2 = engine.createEntity()
        assertEquals(2, engine.entityCount)
        
        engine.destroyEntity(e1)
        assertEquals(1, engine.entityCount)
    }

    @Test
    fun testComponentManagement() {
        val engine = Engine()
        val e1 = engine.createEntity()
        
        engine.addComponent(e1, Position(10f, 20f))
        
        val pos = engine.getComponent(e1, Position::class)
        assertNotNull(pos)
        assertEquals(10f, pos?.x)
        assertEquals(20f, pos?.y)
        
        engine.removeComponent(e1, Position::class)
        assertNull(engine.getComponent(e1, Position::class))
    }

    @Test
    fun testMovementSystem() {
        val engine = Engine()
        val emptyMap = com.runesandrocks.server.world.WorldMap("{ \"width\":2, \"height\":2, \"tileSize\":16, \"tiles\": [0,0,0,0] }")
        engine.addSystem(MovementSystem(engine, emptyMap))
        
        val e1 = engine.createEntity()
        engine.addComponent(e1, Position(8f, 8f)) // Safe spawn
        engine.addComponent(e1, Velocity(1f, 0f))
        
        engine.update(1f) // 1 second update
        
        val pos = engine.getComponent(e1, Position::class)!!
        assertEquals(9f, pos.x)
        assertEquals(8f, pos.y)
    }

    @Test
    fun testSpatialGrid() {
        val grid = SpatialGrid(32f)
        val e1 = 1
        
        // At 0,0 this is chunks 0,0
        grid.addOrUpdate(e1, 0f, 0f)
        assertTrue(grid.getEntitiesInChunk(ChunkCoord(0, 0)).contains(e1))
        
        // Move to next chunk
        grid.addOrUpdate(e1, 40f, 0f)
        assertFalse(grid.getEntitiesInChunk(ChunkCoord(0, 0)).contains(e1))
        assertTrue(grid.getEntitiesInChunk(ChunkCoord(1, 0)).contains(e1))
        
        grid.remove(e1)
        assertFalse(grid.getEntitiesInChunk(ChunkCoord(1, 0)).contains(e1))
    }
}
