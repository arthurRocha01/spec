package com.spec.application

import com.spec.domain.llm.LlmClient
import com.spec.domain.product.Product
import com.spec.infrastructure.http.MercadoLivreClient
import com.spec.infrastructure.persistence.ProductRepository

class ExtractUseCase(
    private val repository: ProductRepository,
    private val mlClient: MercadoLivreClient,
    private val llmClient: LlmClient
) {
    suspend fun execute(productId: String): Product {
        val cached = repository.findBySourceUrl(productId)
        if (cached != null) return cached

        val product = mlClient.getProduct(productId)

        val compatibility = product.description?.let { desc ->
            llmClient.synthesize(desc)
        } ?: emptyList()

        val enrichedProduct = product.copy(
            compatibility = compatibility
        )

        repository.save(enrichedProduct)
        return enrichedProduct
    }
}