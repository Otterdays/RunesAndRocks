package com.runesandrocks.server

import com.runesandrocks.shared.Shared
import com.runesandrocks.server.admin.AdminServer
import com.runesandrocks.server.loop.TickLoop
import com.runesandrocks.server.network.GameServer
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ServerLauncher")

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: GameServer.DEFAULT_PORT
    logger.info("[SERVER] Runes & Rocks Server v{}", Shared.VERSION)
    logger.info("[SERVER] Binding to port {}", port)

    val engine = com.runesandrocks.server.ecs.Engine()
    
    val server = GameServer(port = port, engine = engine)
    server.start()
    
    val mapStream = object {}.javaClass.getResourceAsStream("/world.json")
    val worldMap = com.runesandrocks.server.world.WorldMap(mapStream?.bufferedReader()?.use { it.readText() } ?: "{}")

    engine.addSystem(com.runesandrocks.server.ecs.MovementSystem(engine, worldMap))

    engine.addSystem(com.runesandrocks.server.ecs.NetworkSyncSystem(engine, server))

    val loop = TickLoop(ticksPerSecond = 20) {
        // Phase 4 & 6: ECS systems run here.
        engine.update(1f / 20f)
    }
    val admin = AdminServer(gameServer = server, tickLoop = loop, ecsEngine = engine)
    admin.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        logger.info("[SERVER] Shutdown signal received")
        server.stop()
        admin.stop()
        loop.stop()
    })

    loop.start()
}
