package com.spec.infrastructure.http

import kotlinx.serialization.json.Json
import com.spec.domain.product.Product
import com.spec.domain.product.ProductImage
import com.spec.domain.product.SearchResult
import com.spec.domain.product.Specification

class MercadoLivreParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun parseSearchResults(jsonStr: String): List<SearchResult> {
        val response = json.decodeFromString<MlSearchResponse>(jsonStr)
        return response.results.map { ml ->
            SearchResult(
                title = ml.name,
                url = ml.id,
                thumbnail = ml.pictures?.firstOrNull()?.url
            )
        }
    }

    fun parseProduct(jsonStr: String): Product {
        val detail = json.decodeFromString<MlProductDetail>(jsonStr)
        return Product(
            name = detail.name,
            description = detail.shortDescription?.content?.takeIf { it.isNotBlank() },
            specifications = detail.attributes.map { attr ->
                Specification(
                    key = attr.name,
                    value = attr.valueName ?: ""
                )
            },
            compatibility = emptyList(),
            images = detail.pictures.mapIndexed { i, pic ->
                ProductImage(url = pic.url, order = i)
            },
            sourceUrl = detail.id
        )
    }
}