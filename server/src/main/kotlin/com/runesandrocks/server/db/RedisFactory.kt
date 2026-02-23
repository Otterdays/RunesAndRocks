package com.runesandrocks.server.db

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.Connection
import redis.clients.jedis.JedisPooled
import org.slf4j.LoggerFactory

object RedisFactory {
    private val logger = LoggerFactory.getLogger(javaClass)
    private lateinit var jedis: JedisPooled

    fun init() {
        logger.info("[REDIS] Initializing Jedis connection pool...")
        val host = System.getenv("REDIS_HOST") ?: "localhost"
        val port = System.getenv("REDIS_PORT")?.toIntOrNull() ?: 6379

        val poolConfig = GenericObjectPoolConfig<Connection>().apply {
            maxTotal = 20
            maxIdle = 10
            minIdle = 2
            setTestOnBorrow(true)
        }

        jedis = JedisPooled.builder()
            .poolConfig(poolConfig)
            .hostAndPort(host, port)
            .build()

        val pong = jedis.ping()
        logger.info("[REDIS] Heartbeat test: {}", pong)
    }

    fun getClient() = jedis

    fun close() {
        if (::jedis.isInitialized) {
            jedis.close()
            logger.info("[REDIS] Connection pool closed.")
        }
    }
}
