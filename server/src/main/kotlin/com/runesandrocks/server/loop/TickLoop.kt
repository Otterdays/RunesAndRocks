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

    @Volatile
    private var startTime = 0L

    @Volatile
    private var currentTps = 0.0

    fun getCurrentTps(): Double = currentTps
    fun getUptime(): Long = if (startTime == 0L) 0L else System.currentTimeMillis() - startTime

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
                onTick()
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
            logger.info("Tick rate: {} ticks in {:.1f}s ({:.1f} TPS, target {})",
                ticksInPeriod, elapsed, actualTps, ticksPerSecond)
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
