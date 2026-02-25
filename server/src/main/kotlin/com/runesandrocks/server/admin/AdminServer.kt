package com.runesandrocks.server.admin

import com.runesandrocks.server.loop.TickLoop
import com.runesandrocks.server.network.GameServer
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import org.slf4j.LoggerFactory

/**
 * Ktor HTTP server for admin dashboard. Binds to 127.0.0.1 by default.
 * [TRACE: ARCHITECTURE.md]
 */
class AdminServer(
    private val gameServer: GameServer,
    private val tickLoop: TickLoop,
    private val host: String = "127.0.0.1",
    private val port: Int = DEFAULT_ADMIN_PORT,
    private val ecsEngine: com.runesandrocks.server.ecs.Engine? = null,
    private val worldMap: com.runesandrocks.server.world.WorldMap? = null
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var serverEngine: io.ktor.server.engine.EmbeddedServer<*, *>? = null

    fun start() {
        serverEngine = embeddedServer(CIO, host = host, port = port) {
            install(ContentNegotiation) {
                jackson()
            }
            install(CallLogging)
            install(WebSockets) {
                pingPeriodMillis = 15000
                timeoutMillis = 15000
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            adminRoutes(gameServer, tickLoop, ecsEngine, worldMap)
        }.start(wait = false)
        logger.info("[ADMIN] Dashboard at http://{}:{}/", host, port)
    }

    fun stop() {
        serverEngine?.stop(1000, 2000)
        serverEngine = null
        logger.info("[ADMIN] Dashboard stopped")
    }

    companion object {
        const val DEFAULT_ADMIN_PORT = 8080
    }
}
