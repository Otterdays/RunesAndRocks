package com.runesandrocks.server.loop

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Verifies fixed-timestep loop runs at expected TPS over N seconds.
 */
class TickLoopTest {

    @Test
    fun `tick count matches expected over 2 seconds at 20 TPS`() {
        val tps = 20
        val runSeconds = 2.0
        val expectedTicks = (tps * runSeconds).toInt()
        val tolerance = 5

        val loop = TickLoop(ticksPerSecond = tps) { }
        val thread = Thread { loop.start() }
        thread.start()

        Thread.sleep((runSeconds * 1000).toLong())
        loop.stop()
        thread.join(2000)

        val actual = loop.getTickCount()
        assertTrue(
            actual in (expectedTicks - tolerance)..(expectedTicks + tolerance),
            "Expected ~$expectedTicks ticks in ${runSeconds}s, got $actual"
        )
    }
}
