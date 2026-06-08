package com.spec.infrastructure.persistence

import org.jetbrains.exposed.dao.id.IntIdTable

object ProductTable: IntIdTable("products") {
    val url = varchar("url", 512).uniqueIndex()
    val name = varchar("name", 255)
    val query = varchar("query", 255).nullable()
    val rawText = text("raw_text").nullable()
    val llmResponse = text("llm_response").nullable()
    val createdAt = varchar("created_at", 30)
    val accessedAt = varchar("accessed_at", 30).nullable()
}