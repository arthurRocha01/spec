package com.spec.infrastructure.persistence

import org.jetbrains.exposed.dao.id.IntIdTable

object ProductImageTable: IntIdTable("product_images") {
    val productId = reference("product_id", ProductTable.id)
    val url = varchar("url", 512)
    val imageOrder = integer("image_order").default(0)
}