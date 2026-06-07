package com.spec.domain.scraping

data class ScrapedPage(
    val rawHtml: String,
    val url: String,
    val statusCode: Int,
    val timestamp: Long
) {
    init {
        require(rawHtml.isNotBlank()) { "Raw HTML must not be blank" }
        require(url.isNotBlank()) { "URL must not be blank" }
        require(statusCode in 100..599) { "Status code must be a valid HTTP status code" }
        require(timestamp > 0) { "Timestamp must be positive" }
    }
}
