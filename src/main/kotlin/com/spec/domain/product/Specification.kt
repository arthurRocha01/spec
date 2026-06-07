package com.spec.domain.product

data class Specification(
    val key: String,
    val value: String
) {
    init {
        require(key.isNotBlank()) { "Key must not be blank" }
        require(value.isNotBlank()) { "Value must not be blank" }
    }
}