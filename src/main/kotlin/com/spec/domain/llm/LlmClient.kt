package com.spec.domain.llm

import com.spec.domain.product.Product

interface LlmClient {
    suspend fun synthesize(rawText: String): Product
}