package com.spec.domain.product

data class SearchResult(
    val title: String,
    val url: String,
    val thumbnail: String? = null,
    val price: Double? = null
) {
    init {
        require(title.isNotBlank()) { "Title must not be blank" }
        require(url.isNotBlank()) { "Url must be not be blank" }
    }
}
