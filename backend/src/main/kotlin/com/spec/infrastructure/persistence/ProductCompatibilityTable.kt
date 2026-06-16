package com.spec.infrastructure.persistence

import org.jetbrains.exposed.dao.id.IntIdTable

object ProductCompatibilityTable: IntIdTable("product_compatibility") {
    val productId = reference("product_id", ProductTable.id)
    val model = varchar("model", 255)
    val yearRange = varchar("year_range", 50).nullable()
}