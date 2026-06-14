package com.spec.api

import com.spec.application.ExtractUseCase
import com.spec.application.SearchUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    searchUseCase: SearchUseCase,
    extractUseCase: ExtractUseCase
) {
    routing {
        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        get("/search") {
            val query = call.request.queryParameters["q"]

            if (query.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Parameter 'q' is required"))
                return@get
            }

            val results = searchUseCase.execute(query)
            call.respond(results)
        }

        get("/product") {
            val url = call.request.queryParameters["url"]

            if (url.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ad URL"))
                return@get
            }

            val product = extractUseCase.execute(url)
            call.respond(product)
        }
    }
}