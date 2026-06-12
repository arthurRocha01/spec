package com.spec.application

import com.spec.domain.llm.LlmClient
import com.spec.domain.product.Product

class ProductService(
    private val llmClient: LlmClient
) {
    suspend fun execute(description: String): Product {
        return llmClient.synthesize(rawText = description)
    }
}