package com.spec.application

import com.spec.domain.product.SearchResult
import com.spec.infrastructure.http.MercadoLivreClient
import com.spec.infrastructure.http.Scraper

class SearchUseCase(
    private val mlClient: MercadoLivreClient,
    private val scraper: Scraper
) {
    suspend fun execute(query: String): List<SearchResult> {
        val html = mlClient.search(query)
        return scraper.scrapeSearchResults(html)
    }
}