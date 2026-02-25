package com.runesandrocks.server.admin

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.runesandrocks.server.loop.TickLoop
import com.runesandrocks.server.network.GameServer
import com.runesandrocks.server.db.DatabaseFactory
import com.runesandrocks.server.db.RedisFactory
import com.runesandrocks.shared.net.Packet
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.http.content.staticFiles
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.management.ManagementFactory
import java.util.concurrent.ConcurrentLinkedDeque
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
    val processCpuPct: Double,
    val gamePort: Int,
    val entityCount: Int,
    val taskQueueDepth: Int,
    val dbActiveConns: Int,
    val dbIdleConns: Int,
    val dbTotalConns: Int,
    val redisAlive: Boolean,
    val dockerContainers: List<DockerContainerInfo>,
    val avgTickMs: Double,
    val worstTickMs: Double,
    val tickBudgetMs: Double,
    val packetsSent: Long,
    val packetsReceived: Long,
    val bytesIn: Long,
    val bytesOut: Long,
    val unknownPackets: Long,
    val codecErrors: Long,
    val serverVersion: String,
    val engineVersion: String,
    val uiVersion: String
)

data class GcCollectorInfo(
    val name: String,
    val collectionCount: Long,
    val collectionTimeMs: Long
)

data class MemoryPoolInfo(
    val name: String,
    val usedMb: Long,
    val maxMb: Long
)

data class AuditEntry(
    val timestamp: Long,
    val action: String,
    val result: String,
    val message: String
)

data class ActionResult(
    val success: Boolean,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

object AdminAudit {
    private const val MAX_ENTRIES = 100
    private val entries = ConcurrentLinkedDeque<AuditEntry>()

    fun log(action: String, result: String, message: String) {
        entries.addFirst(AuditEntry(System.currentTimeMillis(), action, result, message))
        while (entries.size > MAX_ENTRIES) entries.removeLast()
    }

    fun recent(limit: Int = 50): List<AuditEntry> =
        entries.take(limit)
}

object ServerLogBuffer {
    private const val MAX_LINES = 200
    val lines = ConcurrentLinkedDeque<String>()

    fun append(line: String) {
        lines.addFirst(line)
        while (lines.size > MAX_LINES) lines.removeLast()
    }

    fun recent(limit: Int = 100): List<String> = lines.take(limit)
}

data class ConfigResponse(
    val gamePort: Int,
    val adminPort: Int,
    val ticksPerSecond: Int
)

data class HealthScore(
    val overall: Int,
    val tickHealth: Int,
    val memoryHealth: Int,
    val networkHealth: Int,
    val persistenceHealth: Int
)

data class Anomaly(
    val type: String,
    val severity: String, // info, warning, critical
    val message: String,
    val suggestedFix: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class SystemPulse(
    val schemaVersion: Int = 1,
    val generatedAt: Long,
    val source: String = "OtterServer",
    val serverVersion: String,
    val engineVersion: String,
    val uiVersion: String,
    val tps: Double,
    val uptimeMs: Long,
    val connectedCount: Int,
    val memoryUsedMb: Long,
    val memoryMaxMb: Long,
    val processCpuPct: Double,
    val avgTickMs: Double,
    val worstTickMs: Double,
    val tickBudgetMs: Double,
    val packetsSentPerSec: Long,
    val packetsReceivedPerSec: Long,
    val bytesInPerSec: Long,
    val bytesOutPerSec: Long,
    val unknownPackets: Long,
    val codecErrors: Long,
    val dbActiveConns: Int,
    val dbIdleConns: Int,
    val dbTotalConns: Int,
    val dbThreadsAwaiting: Int,
    val redisAlive: Boolean,
    val redisKeyCount: Long,
    val taskQueueDepth: Int,
    val entityCount: Int,
    val maintenanceMode: Boolean,
    val health: HealthScore,
    val recentLogs: List<String>,
    val failedLoginsByIp: Map<String, Int>,
    val auditLog: List<AuditEntry>,
    val anomalies: List<Anomaly>
)

private fun computeHealth(
    tps: Double, targetTps: Int,
    memUsed: Long, memMax: Long,
    codecErrors: Long, unknownPackets: Long,
    dbActive: Int, dbAwaiting: Int,
    redisAlive: Boolean
): HealthScore {
    val tickHealth = when {
        tps >= targetTps * 0.95 -> 100
        tps >= targetTps * 0.75 -> 70
        tps >= targetTps * 0.5  -> 40
        else -> 10
    }
    val memPct = if (memMax > 0) memUsed.toDouble() / memMax else 0.0
    val memoryHealth = when {
        memPct < 0.6  -> 100
        memPct < 0.8  -> 70
        memPct < 0.95 -> 40
        else -> 10
    }
    val networkHealth = when {
        codecErrors == 0L && unknownPackets == 0L -> 100
        codecErrors < 5  && unknownPackets < 10   -> 80
        codecErrors < 20 -> 50
        else -> 20
    }
    val persistenceHealth = when {
        redisAlive && dbAwaiting == 0 && dbActive < 8 -> 100
        dbAwaiting == 0 && dbActive < 8 -> 75
        dbAwaiting > 0 -> 30
        else -> 50
    }
    val overall = (tickHealth + memoryHealth + networkHealth + persistenceHealth) / 4
    return HealthScore(overall, tickHealth, memoryHealth, networkHealth, persistenceHealth)
}

private fun computeAnomalies(
    tps: Double, targetTps: Int,
    memUsed: Long, memMax: Long,
    codecErrors: Long,
    dbAwaiting: Int,
    redisAlive: Boolean,
    taskQueueDepth: Int
): List<Anomaly> {
    val anomalies = mutableListOf<Anomaly>()
    
    if (tps < targetTps * 0.75) {
        anomalies.add(Anomaly(
            type = "Tick Lag",
            severity = "critical",
            message = "Server TPS dropped to ${String.format("%.1f", tps)} (Target: $targetTps)",
            suggestedFix = "Check ECS logic, especially MovementSystem or NetworkSyncSystem. A large chunk of entities may be updating simultaneously."
        ))
    }
    
    if (memMax > 0 && (memUsed.toDouble() / memMax) > 0.85) {
        anomalies.add(Anomaly(
            type = "Memory Pressure",
            severity = "warning",
            message = "JVM Heap is at ${((memUsed.toDouble() / memMax) * 100).toInt()}% capacity.",
            suggestedFix = "Trigger Garbage Collection. If memory does not drop, you may have a memory leak in active connection tracking or ECS components."
        ))
    }
    
    if (!redisAlive) {
        anomalies.add(Anomaly(
            type = "Persistence",
            severity = "critical",
            message = "Redis Cache is unreachable.",
            suggestedFix = "Restart Redis via Docker (docker-compose up -d redis). Session caching is offline, forcing DB reads."
        ))
    }
    
    if (dbAwaiting > 0) {
        anomalies.add(Anomaly(
            type = "Database",
            severity = "critical",
            message = "Database connection pool exhausted ($dbAwaiting threads waiting).",
            suggestedFix = "Query backlog. Check Postgres logs for slow queries, or increase HikariCP pool size if load is expected."
        ))
    }
    
    if (taskQueueDepth > 10) {
        anomalies.add(Anomaly(
            type = "Backpressure",
            severity = "warning",
            message = "ECS Task Queue is backing up (Depth: $taskQueueDepth).",
            suggestedFix = "Network events are arriving faster than the game loop can drain them. Check for network spam or slow task execution."
        ))
    }
    
    if (codecErrors > 5) {
        anomalies.add(Anomaly(
            type = "Network Security",
            severity = "warning",
            message = "Multiple packet codec deserialization errors detected ($codecErrors).",
            suggestedFix = "A client is sending garbage or using an outdated protocol. Check Top Senders for suspicious IPs."
        ))
    }
    
    return anomalies
}

private fun gcCollectors(): List<GcCollectorInfo> =
    ManagementFactory.getGarbageCollectorMXBeans().map {
        GcCollectorInfo(it.name, it.collectionCount, it.collectionTime)
    }

private fun memoryPools(): List<MemoryPoolInfo> =
    ManagementFactory.getMemoryPoolMXBeans().map {
        val usage = it.usage
        MemoryPoolInfo(
            it.name,
            usage.used / (1024 * 1024),
            if (usage.max > 0) usage.max / (1024 * 1024) else -1
        )
    }

private fun redisKeyCount(): Long = try {
    RedisFactory.getClient().dbSize()
} catch (_: Exception) { -1 }

private fun processCpuPercent(): Double {
    val bean = ManagementFactory.getOperatingSystemMXBean()
    return if (bean is com.sun.management.OperatingSystemMXBean) {
        val load = bean.processCpuLoad
        if (load < 0) -1.0 else load * 100.0
    } else -1.0
}

fun Application.adminRoutes(gameServer: GameServer, tickLoop: TickLoop, ecsEngine: com.runesandrocks.server.ecs.Engine?, worldMap: com.runesandrocks.server.world.WorldMap? = null) {
    routing {
        staticResources("/admin", "admin")
        // Serve JUnit test reports directly
        staticFiles("/tests", File("server/build/reports/tests/test"))

        get("/") {
            call.respondRedirect("/admin/index.html")
        }

        get("/api/health") {
            val redisUp = try { RedisFactory.getClient().ping() == "PONG" } catch (_: Exception) { false }
            val dbUp = DatabaseFactory.dataSource != null
            val healthy = redisUp && dbUp
            call.respond(mapOf(
                "status" to (if (healthy) "healthy" else "degraded"),
                "redis" to redisUp,
                "database" to dbUp,
                "timestamp" to System.currentTimeMillis()
            ))
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
                    processCpuPct = processCpuPercent(),
                    gamePort = gameServer.gamePort,
                    entityCount = ecsEngine?.entityCount ?: 0,
                    taskQueueDepth = ecsEngine?.taskQueueDepth ?: 0,
                    dbActiveConns = dbPool?.activeConnections ?: 0,
                    dbIdleConns = dbPool?.idleConnections ?: 0,
                    dbTotalConns = dbPool?.totalConnections ?: 0,
                    redisAlive = redisAlive,
                    dockerContainers = getDockerContainers(),
                    avgTickMs = tickLoop.getAvgTickMs(),
                    worstTickMs = tickLoop.getWorstTickMs(),
                    tickBudgetMs = tickLoop.getTickBudgetMs(),
                    packetsSent = gameServer.packetsSent.get(),
                    packetsReceived = gameServer.packetsReceived.get(),
                    bytesIn = gameServer.bytesIn.get(),
                    bytesOut = gameServer.bytesOut.get(),
                    unknownPackets = gameServer.unknownPacketCount.get(),
                    codecErrors = gameServer.codecErrors.get(),
                    serverVersion = com.runesandrocks.shared.Shared.VERSION,
                    engineVersion = com.runesandrocks.shared.Shared.ENGINE_VERSION,
                    uiVersion = com.runesandrocks.shared.Shared.SERVER_UI_VERSION
                )
            )
        }

        get("/api/clients") {
            call.respond(gameServer.getClients())
        }

        post("/api/clients/{id}/kick") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, ActionResult(false, "Invalid client ID"))
                return@post
            }
            val kicked = gameServer.kickClient(id)
            if (kicked) {
                val result = ActionResult(true, "Kicked client $id")
                AdminAudit.log("kick", "success", result.message)
                call.respond(result)
            } else {
                val result = ActionResult(false, "Client $id not found")
                AdminAudit.log("kick", "failed", result.message)
                call.respond(HttpStatusCode.NotFound, result)
            }
        }

        post("/api/actions/gc") {
            System.gc()
            val result = ActionResult(true, "Garbage collection requested")
            AdminAudit.log("gc", "success", result.message)
            call.respond(result)
        }

        post("/api/actions/broadcast") {
            val body = call.receiveText()
            val parsed = try { jacksonObjectMapper().readTree(body) } catch (_: Exception) { null }
            val text = parsed?.get("message")?.asText()
            if (text.isNullOrBlank()) {
                val result = ActionResult(false, "Missing or empty 'message' field")
                call.respond(HttpStatusCode.BadRequest, result)
                return@post
            }
            gameServer.broadcast(Packet.ServerMessage(text))
            val result = ActionResult(true, "Broadcast sent: $text")
            AdminAudit.log("broadcast", "success", result.message)
            call.respond(result)
        }

        post("/api/actions/save-all") {
            val connections = gameServer.getActiveConnections()
            var saved = 0
            connections.forEach { conn ->
                if (conn.dbId != -1) {
                    try {
                        com.runesandrocks.server.db.PlayerRepository.savePlayer(conn.dbId)
                        saved++
                    } catch (_: Exception) {}
                }
            }
            val result = ActionResult(true, "Saved $saved / ${connections.size} players")
            AdminAudit.log("save-all", "success", result.message)
            call.respond(result)
        }

        post("/api/actions/maintenance") {
            val body = call.receiveText()
            val parsed = try { jacksonObjectMapper().readTree(body) } catch (_: Exception) { null }
            val enabled = parsed?.get("enabled")?.asBoolean() ?: !gameServer.maintenanceMode
            gameServer.maintenanceMode = enabled
            val state = if (enabled) "ON" else "OFF"
            val result = ActionResult(true, "Maintenance mode: $state")
            AdminAudit.log("maintenance", "success", result.message)
            call.respond(result)
        }

        post("/api/actions/shutdown") {
            val result = ActionResult(true, "Graceful shutdown initiated (30s countdown)")
            AdminAudit.log("shutdown", "success", result.message)
            call.respond(result)

            kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                gameServer.broadcast(Packet.ServerMessage("Server restarting in 30 seconds..."))
                var remaining = 30
                while (remaining > 0) {
                    if (remaining == 20 || remaining == 10 || remaining <= 5) {
                        gameServer.broadcast(Packet.ServerMessage("Server restarting in $remaining seconds!"))
                    }
                    kotlinx.coroutines.delay(1000)
                    remaining--
                }
                
                gameServer.broadcast(Packet.ServerMessage("Server shutting down NOW. Disconnecting..."))
                kotlinx.coroutines.delay(500)
                
                val connections = gameServer.getActiveConnections()
                connections.forEach { conn ->
                    if (conn.dbId != -1) {
                        try {
                            com.runesandrocks.server.db.PlayerRepository.savePlayer(conn.dbId)
                        } catch (_: Exception) {}
                    }
                }
                
                gameServer.stop()
                tickLoop.stop()
                kotlinx.coroutines.delay(500)
                kotlin.system.exitProcess(0)
            }
        }

        post("/api/actions/reload-world") {
            if (worldMap == null) {
                val result = ActionResult(false, "WorldMap reference is not available")
                call.respond(HttpStatusCode.NotImplemented, result)
                return@post
            }
            
            // Re-read world content from resources or a file in working directory
            val fileStream = object {}.javaClass.getResourceAsStream("/world.json")
            if (fileStream == null) {
                val result = ActionResult(false, "world.json not found in resources")
                AdminAudit.log("reload-world", "failed", result.message)
                call.respond(HttpStatusCode.NotFound, result)
            } else {
                val json = fileStream.bufferedReader().use { it.readText() }
                ecsEngine?.queueTask {
                    try {
                        worldMap.reload(json)
                        gameServer.broadcast(Packet.ServerMessage("World map has been hot-reloaded!"))
                    } catch (e: Exception) {}
                }
                val result = ActionResult(true, "World reload queued")
                AdminAudit.log("reload-world", "success", result.message)
                call.respond(result)
            }
        }

        get("/api/world/positions") {
            if (ecsEngine == null) {
                call.respond(emptyMap<String, Any>())
                return@get
            }
            val entities = ecsEngine.getEntitiesWith(com.runesandrocks.server.ecs.Position::class)
            val posList = entities.mapNotNull { id ->
                val pos = ecsEngine.getComponent(id, com.runesandrocks.server.ecs.Position::class)
                if (pos != null) mapOf("id" to id.toFloat(), "x" to pos.x, "y" to pos.y) else null
            }
            call.respond(mapOf(
                "width" to (worldMap?.width ?: 0),
                "height" to (worldMap?.height ?: 0),
                "tileSize" to (worldMap?.tileSize ?: 16),
                "entities" to posList
            ))
        }


        get("/api/audit") {
            call.respond(AdminAudit.recent())
        }

        get("/api/logs") {
            call.respond(ServerLogBuffer.recent())
        }

        get("/api/connections/timeline") {
            call.respond(gameServer.getConnectionEvents())
        }

        get("/api/metrics/pulse") {
            val runtime = Runtime.getRuntime()
            val memUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val memMax = runtime.maxMemory() / (1024 * 1024)
            val dbPool = DatabaseFactory.dataSource?.hikariPoolMXBean
            val redisAlive = try { RedisFactory.getClient().ping() == "PONG" } catch (_: Exception) { false }
            val health = computeHealth(
                tickLoop.getCurrentTps(), TickLoop.DEFAULT_TPS,
                memUsed, memMax,
                gameServer.codecErrors.get(), gameServer.unknownPacketCount.get(),
                dbPool?.activeConnections ?: 0, dbPool?.threadsAwaitingConnection ?: 0,
                redisAlive
            )
            call.respond(SystemPulse(
                generatedAt = System.currentTimeMillis(),
                tps = tickLoop.getCurrentTps(),
                uptimeMs = tickLoop.getUptime(),
                connectedCount = gameServer.connectedCount,
                memoryUsedMb = memUsed,
                memoryMaxMb = memMax,
                processCpuPct = processCpuPercent(),
                avgTickMs = tickLoop.getAvgTickMs(),
                worstTickMs = tickLoop.getWorstTickMs(),
                tickBudgetMs = tickLoop.getTickBudgetMs(),
                packetsSentPerSec = 0L,
                packetsReceivedPerSec = 0L,
                bytesInPerSec = 0L,
                bytesOutPerSec = 0L,
                unknownPackets = gameServer.unknownPacketCount.get(),
                codecErrors = gameServer.codecErrors.get(),
                dbActiveConns = dbPool?.activeConnections ?: 0,
                dbIdleConns = dbPool?.idleConnections ?: 0,
                dbTotalConns = dbPool?.totalConnections ?: 0,
                dbThreadsAwaiting = dbPool?.threadsAwaitingConnection ?: 0,
                redisAlive = redisAlive,
                redisKeyCount = redisKeyCount(),
                taskQueueDepth = ecsEngine?.taskQueueDepth ?: 0,
                entityCount = ecsEngine?.entityCount ?: 0,
                maintenanceMode = gameServer.maintenanceMode,
                health = health,
                recentLogs = ServerLogBuffer.recent(50),
                failedLoginsByIp = gameServer.getFailedLogins(),
                auditLog = AdminAudit.recent(20),
                anomalies = computeAnomalies(
                    tickLoop.getCurrentTps(), TickLoop.DEFAULT_TPS,
                    memUsed, memMax,
                    gameServer.codecErrors.get(),
                    dbPool?.threadsAwaitingConnection ?: 0,
                    redisAlive,
                    ecsEngine?.taskQueueDepth ?: 0
                ),
                serverVersion = com.runesandrocks.shared.Shared.VERSION,
                engineVersion = com.runesandrocks.shared.Shared.ENGINE_VERSION,
                uiVersion = com.runesandrocks.shared.Shared.SERVER_UI_VERSION
            ))
        }

        get("/api/debug/handoff") {
            val runtime = Runtime.getRuntime()
            val memUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            val memMax = runtime.maxMemory() / (1024 * 1024)
            val dbPool = DatabaseFactory.dataSource?.hikariPoolMXBean
            val redisAlive = try { RedisFactory.getClient().ping() == "PONG" } catch (_: Exception) { false }
            val health = computeHealth(
                tickLoop.getCurrentTps(), TickLoop.DEFAULT_TPS,
                memUsed, memMax,
                gameServer.codecErrors.get(), gameServer.unknownPacketCount.get(),
                dbPool?.activeConnections ?: 0, dbPool?.threadsAwaitingConnection ?: 0,
                redisAlive
            )
            val handoff = mapOf(
                "schemaVersion" to 1,
                "generatedAt" to System.currentTimeMillis(),
                "source" to "OtterServer /api/debug/handoff",
                "health" to health,
                "tps" to tickLoop.getCurrentTps(),
                "uptimeMs" to tickLoop.getUptime(),
                "connectedCount" to gameServer.connectedCount,
                "memoryUsedMb" to memUsed,
                "memoryMaxMb" to memMax,
                "processCpuPct" to processCpuPercent(),
                "avgTickMs" to tickLoop.getAvgTickMs(),
                "worstTickMs" to tickLoop.getWorstTickMs(),
                "tickBudgetMs" to tickLoop.getTickBudgetMs(),
                "entityCount" to (ecsEngine?.entityCount ?: 0),
                "taskQueueDepth" to (ecsEngine?.taskQueueDepth ?: 0),
                "maintenanceMode" to gameServer.maintenanceMode,
                "unknownPackets" to gameServer.unknownPacketCount.get(),
                "codecErrors" to gameServer.codecErrors.get(),
                "dbActiveConns" to (dbPool?.activeConnections ?: 0),
                "dbThreadsAwaiting" to (dbPool?.threadsAwaitingConnection ?: 0),
                "redisAlive" to redisAlive,
                "failedLoginsByIp" to gameServer.getFailedLogins(),
                "topSenders" to gameServer.getTopSenders(5),
                "gcCollectors" to gcCollectors(),
                "memoryPools" to memoryPools(),
                "recentLogs" to ServerLogBuffer.recent(50),
                "auditLog" to AdminAudit.recent(20)
            )
            call.respond(handoff)
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

            var prevPacketsSent = 0L
            var prevPacketsReceived = 0L
            var prevBytesIn = 0L
            var prevBytesOut = 0L

            while (true) {
                val runtime = Runtime.getRuntime()
                val memoryUsedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)

                val curPacketsSent = gameServer.packetsSent.get()
                val curPacketsReceived = gameServer.packetsReceived.get()
                val curBytesIn = gameServer.bytesIn.get()
                val curBytesOut = gameServer.bytesOut.get()

                val packetsSentPerSec = curPacketsSent - prevPacketsSent
                val packetsReceivedPerSec = curPacketsReceived - prevPacketsReceived
                val bytesInPerSec = curBytesIn - prevBytesIn
                val bytesOutPerSec = curBytesOut - prevBytesOut

                prevPacketsSent = curPacketsSent
                prevPacketsReceived = curPacketsReceived
                prevBytesIn = curBytesIn
                prevBytesOut = curBytesOut

                val dbPool = DatabaseFactory.dataSource?.hikariPoolMXBean
                val redisUp = try { RedisFactory.getClient().ping() == "PONG" } catch (_: Exception) { false }
                val health = computeHealth(
                    tickLoop.getCurrentTps(), TickLoop.DEFAULT_TPS,
                    memoryUsedMb, runtime.maxMemory() / (1024 * 1024),
                    gameServer.codecErrors.get(), gameServer.unknownPacketCount.get(),
                    dbPool?.activeConnections ?: 0, dbPool?.threadsAwaitingConnection ?: 0,
                    redisUp
                )
                
                val anomalies = computeAnomalies(
                    tickLoop.getCurrentTps(), TickLoop.DEFAULT_TPS,
                    memoryUsedMb, runtime.maxMemory() / (1024 * 1024),
                    gameServer.codecErrors.get(),
                    dbPool?.threadsAwaitingConnection ?: 0,
                    redisUp,
                    ecsEngine?.taskQueueDepth ?: 0
                )

                val snapshot = mapOf(
                    "tps" to tickLoop.getCurrentTps(),
                    "uptimeMs" to tickLoop.getUptime(),
                    "connectedCount" to gameServer.connectedCount,
                    "memoryUsedMb" to memoryUsedMb,
                    "memoryMaxMb" to runtime.maxMemory() / (1024 * 1024),
                    "threads" to threadBean.threadCount,
                    "cpuCores" to runtime.availableProcessors(),
                    "cpuLoadAvg" to osBean.systemLoadAverage,
                    "processCpuPct" to processCpuPercent(),
                    "entities" to (ecsEngine?.entityCount ?: 0),
                    "taskQueueDepth" to (ecsEngine?.taskQueueDepth ?: 0),
                    "clients" to gameServer.getClients(),
                    "dbActiveConns" to (dbPool?.activeConnections ?: 0),
                    "dbIdleConns" to (dbPool?.idleConnections ?: 0),
                    "dbTotalConns" to (dbPool?.totalConnections ?: 0),
                    "dbThreadsAwaiting" to (dbPool?.threadsAwaitingConnection ?: 0),
                    "redisAlive" to redisUp,
                    "redisKeyCount" to redisKeyCount(),
                    "dockerContainers" to getDockerContainers(),
                    "avgTickMs" to tickLoop.getAvgTickMs(),
                    "worstTickMs" to tickLoop.getWorstTickMs(),
                    "tickBudgetMs" to tickLoop.getTickBudgetMs(),
                    "packetsSentPerSec" to packetsSentPerSec,
                    "packetsReceivedPerSec" to packetsReceivedPerSec,
                    "bytesInPerSec" to bytesInPerSec,
                    "bytesOutPerSec" to bytesOutPerSec,
                    "unknownPackets" to gameServer.unknownPacketCount.get(),
                    "codecErrors" to gameServer.codecErrors.get(),
                    "gcCollectors" to gcCollectors(),
                    "memoryPools" to memoryPools(),
                    "maintenanceMode" to gameServer.maintenanceMode,
                    "lastTickTime" to tickLoop.lastTickTime,
                    "health" to health,
                    "failedLoginsByIp" to gameServer.getFailedLogins(),
                    "topSenders" to gameServer.getTopSenders(10),
                    "recentLogs" to ServerLogBuffer.recent(50),
                    "anomalies" to anomalies,
                    "connectionEvents" to gameServer.getConnectionEvents(30),
                    "serverVersion" to com.runesandrocks.shared.Shared.VERSION,
                    "engineVersion" to com.runesandrocks.shared.Shared.ENGINE_VERSION,
                    "uiVersion" to com.runesandrocks.shared.Shared.SERVER_UI_VERSION
                )
                send(Frame.Text(mapper.writeValueAsString(snapshot)))
                delay(1000)
            }
        }
    }
}
