package com.runesandrocks.server.db

import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import org.slf4j.LoggerFactory

object RedisFactory {
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var pool: JedisPool

    fun init() {
        logger.info("[REDIS] Initializing Jedis connection pool...")
        val host = System.getenv("REDIS_HOST") ?: "localhost"
        val port = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379
        
        val poolConfig = JedisPoolConfig().apply {
            maxTotal = 20
            maxIdle = 10
            minIdle = 2
            testOnBorrow = true
        }
        
        pool = JedisPool(poolConfig, host, port)
        
        // Test connection
        pool.resource.use { jedis ->
            val pong = jedis.ping()
            logger.info("[REDIS] Heartbeat test: {}", pong)
        }
    }

    fun getClient() = pool.resource
    
    fun close() {
        if (::pool.isInitialized) {
            pool.close()
            logger.info("[REDIS] Connection pool closed.")
        }
    }
}
