package com.runesandrocks.server.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesandrocks.server.loop.TickLoop
import com.runesandrocks.server.network.GameServer
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import java.io.File
import java.lang.management.ManagementFactory

data class StatusResponse(
    val tps: Double,
    val uptimeMs: Long,
    val connectedCount: Int,
    val memoryUsedMb: Long,
    val memoryMaxMb: Long,
    val threads: Int,
    val cpuCores: Int,
    val cpuLoadAvg: Double,
    val gamePort: Int,
    val entityCount: Int
)

data class ConfigResponse(
    val gamePort: Int,
    val adminPort: Int,
    val ticksPerSecond: Int
)

fun Application.adminRoutes(gameServer: GameServer, tickLoop: TickLoop, ecsEngine: com.runesandrocks.server.ecs.Engine?) {
    routing {
        staticResources("/admin", "admin")
        // Serve JUnit test reports directly
        staticFiles("/tests", File("server/build/reports/tests/test"))

        get("/") {
            call.respondRedirect("/admin/index.html")
        }

        get("/api/status") {
            val runtime = Runtime.getRuntime()
            val memoryUsedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val osBean = ManagementFactory.getOperatingSystemMXBean()
            val threadBean = ManagementFactory.getThreadMXBean()
            call.respond(
                StatusResponse(
                    tps = tickLoop.getCurrentTps(),
                    uptimeMs = tickLoop.getUptime(),
                    connectedCount = gameServer.connectedCount,
                    memoryUsedMb = memoryUsedMb,
                    memoryMaxMb = runtime.maxMemory() / (1024 * 1024),
                    threads = threadBean.threadCount,
                    cpuCores = runtime.availableProcessors(),
                    cpuLoadAvg = osBean.systemLoadAverage,
                    gamePort = gameServer.gamePort,
                    entityCount = ecsEngine?.entityCount ?: 0
                )
            )
        }

        get("/api/clients") {
            call.respond(gameServer.getClients())
        }

        post("/api/clients/{id}/kick") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@post
            }
            val kicked = gameServer.kickClient(id)
            if (kicked) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        get("/api/config") {
            call.respond(
                ConfigResponse(
                    gamePort = gameServer.gamePort,
                    adminPort = AdminServer.DEFAULT_ADMIN_PORT,
                    ticksPerSecond = TickLoop.DEFAULT_TPS
                )
            )
        }

        webSocket("/ws/live") {
            val mapper = jacksonObjectMapper()
            val osBean = ManagementFactory.getOperatingSystemMXBean()
            val threadBean = ManagementFactory.getThreadMXBean()
            while (true) {
                val runtime = Runtime.getRuntime()
                val memoryUsedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
                val snapshot = mapOf(
                    "tps" to tickLoop.getCurrentTps(),
                    "uptimeMs" to tickLoop.getUptime(),
                    "connectedCount" to gameServer.connectedCount,
                    "memoryUsedMb" to memoryUsedMb,
                    "memoryMaxMb" to runtime.maxMemory() / (1024 * 1024),
                    "threads" to threadBean.threadCount,
                    "cpuCores" to runtime.availableProcessors(),
                    "cpuLoadAvg" to osBean.systemLoadAverage,
                    "entities" to (ecsEngine?.entityCount ?: 0),
                    "clients" to gameServer.getClients()
                )
                send(Frame.Text(mapper.writeValueAsString(snapshot)))
                delay(1000)
            }
        }
    }
}
