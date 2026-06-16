package com.spec.infrastructure.http

import com.spec.domain.product.Product
import com.spec.domain.product.SearchResult
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MercadoLivreClient(
    private val httpClient: HttpClient,
    private val clientId: String,
    private val clientSecret: String,
) {
    private val parser = MercadoLivreParser()
    private val json = Json { ignoreUnknownKeys = true }
    private val scope = CoroutineScope(Dispatchers.Default)

    private var accessToken: String? = null

    init {
        scope.launch {
            refreshToken()
            startAutoRefresh()
        }
    }

    private suspend fun refreshToken() {
        val response = httpClient.post("https://api.mercadolibre.com/oauth/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody("grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret")
        }
        val body = response.bodyAsText()
        val tokenResponse = json.decodeFromString<MlTokenResponse>(body)
        accessToken = tokenResponse.accessToken
    }

    private suspend fun startAutoRefresh() {
        while (true) {
            delay(5 * 60 * 60 * 1000L) // 5 horas
            refreshToken()
        }
    }

    suspend fun search(query: String): List<SearchResult> {
        val token = accessToken ?: throw IllegalStateException("Token not initialized")
        val response = httpClient.get("https://api.mercadolibre.com/products/search") {
            header("Authorization", "Bearer $token")
            url {
                parameters.append("status", "active")
                parameters.append("site_id", "MLB")
                parameters.append("q", query)
                parameters.append("limit", "10")
            }
        }
        val body = response.bodyAsText()
        return parser.parseSearchResults(body)
    }

    suspend fun getProduct(productId: String): Product {
        val token = accessToken ?: throw IllegalStateException("Token not initialized")
        val response = httpClient.get("https://api.mercadolibre.com/products/$productId") {
            header("Authorization", "Bearer $token")
        }
        val body = response.bodyAsText()
        return parser.parseProduct(body)
    }
}