package com.spec

import com.microsoft.playwright.Playwright
import com.spec.api.configureRouting
import com.spec.api.configureSerialization
import com.spec.application.ExtractUseCase
import com.spec.application.SearchUseCase
import com.spec.infrastructure.http.LlmHttpClient
import com.spec.infrastructure.http.MercadoLivreClient
import com.spec.infrastructure.http.Scraper
import com.spec.infrastructure.persistence.DatabaseFactory
import com.spec.infrastructure.persistence.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    DatabaseFactory.init()

    val httpClient = HttpClient()
    val apiKey = System.getenv("DEEPSEEK_API_KEY") ?: throw RuntimeException("DEEPSEEK_API_KEY not set")
    val llmClient = LlmHttpClient(httpClient, apiKey)
    val scraper = Scraper(Playwright.create())
    val mlClient = MercadoLivreClient(httpClient)
    val repository = ProductRepository()

    val searchUseCase = SearchUseCase(mlClient, scraper)
    val extracUseCase = ExtractUseCase(repository, scraper, llmClient)

    embeddedServer(Netty, port = 8080) {
        configureSerialization()
        configureRouting(searchUseCase, extracUseCase)
    }.start(wait = true)
}
