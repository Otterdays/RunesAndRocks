package com.runesandrocks.server.loop

import org.slf4j.LoggerFactory

/**
 * Fixed-timestep game loop. Accumulates real elapsed time and runs update()
 * at a fixed rate (e.g. 20 TPS). Prevents logic from running faster on better hardware.
 * [TRACE: ARCHITECTURE.md]
 */
class TickLoop(
    private val ticksPerSecond: Int = DEFAULT_TPS,
    private val onTick: () -> Unit
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val delta: Double = 1.0 / ticksPerSecond
    private val maxAccumulator = delta * MAX_TICKS_PER_FRAME

    private var accumulator = 0.0
    private var tickCount = 0L
    private var lastLogTime = System.nanoTime()
    private var lastLogTickCount = 0L
    private var running = false

    // Tick duration tracking
    private var tickDurationSumNs = 0L
    private var tickDurationCount = 0L
    private var worstTickNs = 0L

    @Volatile
    private var startTime = 0L

    @Volatile
    private var currentTps = 0.0

    @Volatile
    private var avgTickMs = 0.0

    @Volatile
    private var worstTickMs = 0.0

    @Volatile
    private var lastTickMs = 0.0

    @Volatile
    var lastTickTime = 0L
        private set

    fun getCurrentTps(): Double = currentTps
    fun getUptime(): Long = if (startTime == 0L) 0L else System.currentTimeMillis() - startTime
    fun getAvgTickMs(): Double = avgTickMs
    fun getWorstTickMs(): Double = worstTickMs
    fun getLastTickMs(): Double = lastTickMs
    fun getTickBudgetMs(): Double = delta * 1000

    fun start() {
        running = true
        startTime = System.currentTimeMillis()
        var lastTime = System.nanoTime()
        logger.info("Tick loop started at {} TPS", ticksPerSecond)

        while (running) {
            val now = System.nanoTime()
            val elapsed = (now - lastTime) / 1e9
            lastTime = now

            accumulator += elapsed
            if (accumulator > maxAccumulator) accumulator = maxAccumulator

            while (accumulator >= delta) {
                val tickStart = System.nanoTime()
                onTick()
                val tickElapsed = System.nanoTime() - tickStart
                lastTickTime = System.currentTimeMillis()

                tickDurationSumNs += tickElapsed
                tickDurationCount++
                if (tickElapsed > worstTickNs) worstTickNs = tickElapsed
                lastTickMs = tickElapsed / 1_000_000.0

                tickCount++
                accumulator -= delta
            }

            logTickRateIfDue(now)
            sleepRemainder(delta, accumulator)
        }
    }

    fun stop() {
        running = false
    }

    fun getTickCount(): Long = tickCount

    private fun logTickRateIfDue(now: Long) {
        val elapsed = (now - lastLogTime) / 1e9
        if (elapsed >= TICK_RATE_LOG_INTERVAL_SEC) {
            val ticksInPeriod = tickCount - lastLogTickCount
            val actualTps = ticksInPeriod / elapsed
            currentTps = actualTps

            // Snapshot and reset tick duration rolling window
            if (tickDurationCount > 0) {
                avgTickMs = (tickDurationSumNs.toDouble() / tickDurationCount) / 1_000_000.0
                worstTickMs = worstTickNs / 1_000_000.0
            }
            tickDurationSumNs = 0L
            tickDurationCount = 0L
            worstTickNs = 0L

            logger.info("Tick rate: {} ticks in {}s ({} TPS, target {}) | avg {}ms worst {}ms",
                ticksInPeriod, "%.1f".format(elapsed), "%.1f".format(actualTps), ticksPerSecond, "%.2f".format(avgTickMs), "%.2f".format(worstTickMs))
            lastLogTime = now
            lastLogTickCount = tickCount
        }
    }

    private fun sleepRemainder(delta: Double, acc: Double) {
        val sleepTime = (delta - acc) * 1000
        if (sleepTime > 1) {
            Thread.sleep(sleepTime.toLong())
        }
    }

    companion object {
        const val DEFAULT_TPS = 20
        private const val MAX_TICKS_PER_FRAME = 5
        private const val TICK_RATE_LOG_INTERVAL_SEC = 5.0
    }
}
