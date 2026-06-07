package com.spec.domain.product

data class Product(
    val name: String,
    val specifications: List<Specification>,
    val compatibility: List<Compatibility>,
    val images: List<ProductImage>,
    val sourceUrl: String
) {
    init {
        require(name.isNotBlank()) { "Name must not be empty" }
        require(sourceUrl.isNotBlank()) { "SourceUrl must not be empty" }
    }
}