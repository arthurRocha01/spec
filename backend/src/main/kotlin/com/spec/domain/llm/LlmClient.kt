package com.spec.domain.llm

import com.spec.domain.product.Compatibility

interface LlmClient {
    suspend fun synthesize(rawText: String): List<Compatibility>
}