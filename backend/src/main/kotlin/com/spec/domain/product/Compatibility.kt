package com.spec.domain.product

import kotlinx.serialization.Serializable

@Serializable
data class Compatibility(
    val model: String,
    val yearRange: String? = null
) {
    init {
        require(model.isNotBlank()) { "Model must not be blank" }
    }
}
