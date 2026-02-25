package com.runesandrocks.server.ecs

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

data class ChunkCoord(val cx: Int, val cy: Int)

class SpatialGrid(private val chunkSize: Float = 32f) {
    private val chunks = ConcurrentHashMap<ChunkCoord, CopyOnWriteArraySet<EntityId>>()
    private val entityChunks = ConcurrentHashMap<EntityId, ChunkCoord>()

    fun getChunk(x: Float, y: Float): ChunkCoord {
        return ChunkCoord((x / chunkSize).toInt(), (y / chunkSize).toInt())
    }

    fun addOrUpdate(entity: EntityId, x: Float, y: Float) {
        val newChunk = getChunk(x, y)
        val oldChunk = entityChunks[entity]

        if (oldChunk != newChunk) {
            if (oldChunk != null) {
                removeFromChunk(entity, oldChunk)
            }
            addToChunk(entity, newChunk)
        }
    }

    fun remove(entity: EntityId) {
        val oldChunk = entityChunks.remove(entity)
        if (oldChunk != null) {
            removeFromChunk(entity, oldChunk)
        }
    }

    private fun addToChunk(entity: EntityId, chunk: ChunkCoord) {
        entityChunks[entity] = chunk
        chunks.computeIfAbsent(chunk) { CopyOnWriteArraySet() }.add(entity)
    }

    private fun removeFromChunk(entity: EntityId, chunk: ChunkCoord) {
        val set = chunks[chunk]
        set?.remove(entity)
        if (set != null && set.isEmpty()) {
            chunks.remove(chunk, set)
        }
    }

    fun getEntitiesInChunk(chunk: ChunkCoord): Set<EntityId> {
        return chunks[chunk] ?: emptySet()
    }

    fun getRelevantChunks(chunk: ChunkCoord): List<ChunkCoord> {
        val list = mutableListOf<ChunkCoord>()
        for (dx in -1..1) {
            for (dy in -1..1) {
                list.add(ChunkCoord(chunk.cx + dx, chunk.cy + dy))
            }
        }
        return list
    }

    fun getOccupiedChunks(): Map<ChunkCoord, Int> {
        return chunks.mapValues { it.value.size }
    }
}
