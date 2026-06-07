package com.spec.infrastructure.persistence

import org.jetbrains.exposed.sql.Table

object ProductSpecs: Table("product_specs") {
    val id = varchar("id", 30).uniqueIndex()
    val productId = varchar("product_id", 64) references ProductTable.id
    val key = varchar("key", 255)
    val value = varchar("value", 255)

    override val primaryKey = PrimaryKey(id)
}