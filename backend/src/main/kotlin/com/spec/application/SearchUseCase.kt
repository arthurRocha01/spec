package com.spec.application

import com.spec.domain.product.SearchResult
import com.spec.infrastructure.http.MercadoLivreClient

class SearchUseCase(
    private val mlClient: MercadoLivreClient
) {
    suspend fun execute(query: String): List<SearchResult> {
        return mlClient.search(query)
    }
}