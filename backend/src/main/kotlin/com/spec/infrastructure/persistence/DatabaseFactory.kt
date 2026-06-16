package com.spec.infrastructure.persistence

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DatabaseFactory {
    fun init() {
        val dataDir = File("data")
        if (!dataDir.exists()) dataDir.mkdir()

        Database.connect(
            url = "jdbc:sqlite:data/spec.db",
            driver = "org.sqlite.JDBC"
        )

        transaction {
            SchemaUtils.create(
                ProductTable,
                ProductSpecTable,
                ProductCompatibilityTable,
                ProductImageTable
            )
        }
    }
}