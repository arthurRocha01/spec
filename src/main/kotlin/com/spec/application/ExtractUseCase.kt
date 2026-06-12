package com.spec.application

import com.spec.domain.llm.LlmClient
import com.spec.domain.product.Product
import com.spec.domain.product.ProductImage
import com.spec.infrastructure.http.Scraper
import com.spec.infrastructure.persistence.ProductRepository

class ExtractUseCase(
    private val repository: ProductRepository,
    private val scraper: Scraper,
    private val llmClient: LlmClient
) {
    suspend fun execute(url: String): Product {
        val cached = repository.findByUrl(url)
        if (cached != null)  return cached

        val pageData = scraper.scrapeProductPage(url)
        val product = llmClient.synthesize(pageData.description)

        val enrichedProduct = product.copy(
            images = pageData.images.mapIndexed { i, url ->
                ProductImage(url = url , order = i)
            },
            sourceUrl = url
        )

        repository.save(enrichedProduct)
        return enrichedProduct
    }
}