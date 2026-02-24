package com.runesandrocks.server.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesandrocks.server.loop.TickLoop
import com.runesandrocks.server.network.GameServer
import com.runesandrocks.server.db.DatabaseFactory
import com.runesandrocks.server.db.RedisFactory
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
import java.util.concurrent.TimeUnit

data class DockerContainerInfo(
    val name: String,
    val status: String
)

fun getDockerContainers(): List<DockerContainerInfo> {
    try {
        val process = ProcessBuilder("docker", "ps", "--format", "{{.Names}}|{{.Status}}")
            .redirectErrorStream(true)
            .start()
        
        val hasFinished = process.waitFor(1, TimeUnit.SECONDS)
        if (hasFinished && process.exitValue() == 0) {
            val output = process.inputStream.bufferedReader().readText()
            return output.trim().lines()
                .filter { it.isNotBlank() }
                .map { 
                    val parts = it.split("|", limit = 2)
                    DockerContainerInfo(parts[0], if (parts.size > 1) parts[1] else "Unknown")
                }
        }
    } catch (e: Exception) {
        // Docker not accessible from this environment
    }
    return emptyList()
}

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
    val entityCount: Int,
    val dbActiveConns: Int,
    val dbIdleConns: Int,
    val dbTotalConns: Int,
    val redisAlive: Boolean,
    val dockerContainers: List<DockerContainerInfo>,
    val avgTickMs: Double,
    val worstTickMs: Double,
    val tickBudgetMs: Double
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
            
            val dbPool = DatabaseFactory.dataSource?.hikariPoolMXBean
            val redisAlive = try { RedisFactory.getClient().ping() == "PONG" } catch(e: Exception) { false }

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
                    entityCount = ecsEngine?.entityCount ?: 0,
                    dbActiveConns = dbPool?.activeConnections ?: 0,
                    dbIdleConns = dbPool?.idleConnections ?: 0,
                    dbTotalConns = dbPool?.totalConnections ?: 0,
                    redisAlive = redisAlive,
                    dockerContainers = getDockerContainers(),
                    avgTickMs = tickLoop.getAvgTickMs(),
                    worstTickMs = tickLoop.getWorstTickMs(),
                    tickBudgetMs = tickLoop.getTickBudgetMs()
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

        post("/api/actions/gc") {
            System.gc()
            call.respond(HttpStatusCode.OK, "Garbage collection requested")
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
                    "clients" to gameServer.getClients(),
                    "dbActiveConns" to (DatabaseFactory.dataSource?.hikariPoolMXBean?.activeConnections ?: 0),
                    "redisAlive" to try { RedisFactory.getClient().ping() == "PONG" } catch(e: Exception) { false },
                    "dockerContainers" to getDockerContainers(),
                    "avgTickMs" to tickLoop.getAvgTickMs(),
                    "worstTickMs" to tickLoop.getWorstTickMs(),
                    "tickBudgetMs" to tickLoop.getTickBudgetMs()
                )
                send(Frame.Text(mapper.writeValueAsString(snapshot)))
                delay(1000)
            }
        }
    }
}
