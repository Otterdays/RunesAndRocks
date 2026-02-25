package com.runesandrocks.server.admin

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Logback appender that feeds log lines into ServerLogBuffer for the admin dashboard.
 * Captures WARN+ by default (configured in logback.xml).
 * [TRACE: SERVER_UI_UPGRADE.md]
 */
class RingBufferAppender : AppenderBase<ILoggingEvent>() {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    override fun append(event: ILoggingEvent) {
        val time = formatter.format(Instant.ofEpochMilli(event.timeStamp))
        val line = "[$time] ${event.level} ${event.loggerName.substringAfterLast('.')}: ${event.formattedMessage}"
        ServerLogBuffer.append(line)
    }
}
