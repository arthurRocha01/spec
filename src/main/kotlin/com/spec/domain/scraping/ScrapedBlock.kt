package com.spec.domain.scraping

enum class BlockType {
    TITLE, SPECS, DESCRIPTION, COMPATIBILITY, IMAGE_URL
}

data class ScrapedBlock(
    val content: String,
    val type: BlockType
) {
    init {
        require(content.isNotBlank()) { "Content must not be empty" }
    }
}
