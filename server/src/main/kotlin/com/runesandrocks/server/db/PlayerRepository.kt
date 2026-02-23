package com.runesandrocks.server.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object Players : IntIdTable("players") {
    val username = varchar("username", 50).uniqueIndex()
    val posX = float("pos_x").default(160f)
    val posY = float("pos_y").default(112f)
    // Future: val gold = integer("gold").default(0)
}

class PlayerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PlayerEntity>(Players)
    var username by Players.username
    var posX by Players.posX
    var posY by Players.posY
}

object PlayerRepository {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Finds or creates a player in PostgreSQL and loads them into Redis.
     */
    fun loginPlayer(username: String): PlayerState {
        // 1. Fetch from or create in PostgreSQL
        var pX = 160f
        var pY = 112f
        var playerId = -1
        
        transaction {
            val entity = PlayerEntity.find { Players.username eq username }.firstOrNull()
            if (entity != null) {
                pX = entity.posX
                pY = entity.posY
                playerId = entity.id.value
                logger.info("[DB] Loaded existing player: {} (ID: {})", username, playerId)
            } else {
                val newEntity = PlayerEntity.new {
                    this.username = username
                    this.posX = 160f
                    this.posY = 112f
                }
                pX = newEntity.posX
                pY = newEntity.posY
                playerId = newEntity.id.value
                logger.info("[DB] Generated new player: {} (ID: {})", username, playerId)
            }
        }
        
        // 2. Hydrate Redis hot cache
        try {
            RedisFactory.getClient().use { jedis ->
                jedis.hset("player:$playerId", mapOf(
                    "x" to pX.toString(),
                    "y" to pY.toString(),
                    "username" to username
                ))
            }
        } catch (e: Exception) {
            logger.error("[REDIS] Failed to hydrate player cache!", e)
        }
        
        return PlayerState(playerId, pX, pY)
    }

    /**
     * Commits volatile Redis state back to safe PostgreSQL tables.
     */
    fun savePlayer(playerId: Int) {
        try {
            var x = 0f
            var y = 0f
            var found = false
            
            RedisFactory.getClient().use { jedis ->
                val cached = jedis.hgetAll("player:$playerId")
                if (cached.isNotEmpty()) {
                    x = cached["x"]?.toFloatOrNull() ?: 0f
                    y = cached["y"]?.toFloatOrNull() ?: 0f
                    found = true
                    
                    // Cleanup hot path memory
                    jedis.del("player:$playerId")
                }
            }
            
            if (found) {
                transaction {
                    val p = PlayerEntity.findById(playerId)
                    if (p != null) {
                        p.posX = x
                        p.posY = y
                    }
                }
                logger.debug("[DB] Player $playerId saved securely to database.")
            }
        } catch (e: Exception) {
            logger.error("[PERSISTENCE] Critical save failure for player $playerId!", e)
        }
    }
}

data class PlayerState(val dbId: Int, val x: Float, val y: Float)
