package com.spec.api

import com.spec.application.ExtractUseCase
import com.spec.application.SearchUseCase
import com.spec.infrastructure.http.LlmHttpClient
import com.spec.infrastructure.http.MercadoLivreClient
import com.spec.infrastructure.persistence.DatabaseFactory
import com.spec.infrastructure.persistence.ProductRepository
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*

fun Application.module() {
    DatabaseFactory.init()

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }
    val dotenv = dotenv()
    val clientId = dotenv["ML_CLIENT_ID"] ?: throw RuntimeException("ML_CLIENT_ID not set")
    val clientSecret = dotenv["ML_CLIENT_SECRET"] ?: throw RuntimeException("ML_CLIENT_SECRET not set")
    val apiKey = dotenv["DEEPSEEK_API_KEY"] ?: throw RuntimeException("DEEPSEEK_API_KEY not set")
    val llmClient = LlmHttpClient(httpClient, apiKey)
    val mlClient = MercadoLivreClient(httpClient, clientId, clientSecret)
    val repository = ProductRepository()

    val searchUseCase = SearchUseCase(mlClient)
    val extracUseCase = ExtractUseCase(repository, mlClient, llmClient)

    configureSerialization()
    configureRouting(searchUseCase, extracUseCase)
}