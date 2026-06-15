package com.spec.domain.product

import kotlinx.serialization.Serializable

@Serializable
data class ProductImage(
    val url: String,
    val order: Int
) {
    init {
        require(url.startsWith("http")) { "Image url must start with http: $url" }
        require(order >= 0) { "Image order must be zero or greater" }
    }
}
