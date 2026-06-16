package com.spec.infrastructure.persistence

import org.jetbrains.exposed.dao.id.IntIdTable

object ProductSpecTable: IntIdTable("product_specs") {
    val productId = reference("product_id", ProductTable.id)
    val key = varchar("key", 255)
    val value = varchar("value", 255)
}