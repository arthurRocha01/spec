package com.spec.infrastructure.http

import com.spec.domain.llm.LlmClient
import com.spec.domain.product.*
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class LlmHttpClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val endpoint: String = "https://api.deepseek.com/v1/chat/completions"
) : LlmClient {

    override suspend fun synthesize(rawText: String): Product {
        val response = httpClient.post(endpoint) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("model", "deepseek-chat")
                put("messages", buildJsonArray {
                    add(buildJsonObject {
                        put("role", "system")
                        put("content", "You are a motorcycle parts specialist. Extract product information and return ONLY valid JSON with fields: name (string), specifications (array of {key, value}), compatibility (array of {model, yearRange}). No markdown, no explanation.")
                    })
                    add(buildJsonObject {
                        put("role", "user")
                        put("content", rawText)
                    })
                })
            })
        }

        val body = response.bodyAsText()
        val json = Json.parseToJsonElement(body).jsonObject
        val content = json["choices"]?.jsonArray?.first()?.jsonObject
            ?.get("message")?.jsonObject
            ?.get("content")?.jsonPrimitive?.content
            ?: throw RuntimeException("Failed to parse LLM response")

        return parseProduct(content)
    }

    private fun parseProduct(jsonString: String): Product {
        val json = Json.parseToJsonElement(jsonString).jsonObject
        return Product(
            name = json["name"]?.jsonPrimitive?.content ?: "",
            specifications = json["specifications"]?.jsonArray?.map {
                val obj = it.jsonObject
                Specification(
                    key = obj["key"]?.jsonPrimitive?.content ?: "",
                    value = obj["value"]?.jsonPrimitive?.content ?: ""
                )
            } ?: emptyList(),
            compatibility = json["compatibility"]?.jsonArray?.map {
                val obj = it.jsonObject
                Compatibility(
                    model = obj["model"]?.jsonPrimitive?.content ?: "",
                    yearRange = obj["yearRange"]?.jsonPrimitive?.contentOrNull
                )
            } ?: emptyList(),
            images = emptyList(),
            sourceUrl = ""
        )
    }
}
