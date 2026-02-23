package com.runesandrocks.server.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun init() {
        logger.info("[DB] Initializing PostgreSQL connection via HikariCP...")
        
        val config = HikariConfig().apply {
            jdbcUrl = System.getenv("JDBC_URL") ?: "jdbc:postgresql://localhost:5432/runesandrocks"
            driverClassName = "org.postgresql.Driver"
            username = System.getenv("DB_USER") ?: "runes_admin"
            password = System.getenv("DB_PASS") ?: "runes_password"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)
        
        transaction {
            SchemaUtils.create(Players)
            logger.info("[DB] Schema validated / created successfully.")
        }
    }
}
