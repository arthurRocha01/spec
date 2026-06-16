package com.spec.infrastructure.http

import com.spec.domain.llm.LlmClient
import com.spec.domain.product.Compatibility
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

class LlmHttpClient(
    private val httpClient: HttpClient,
    private val apiKey: String,
    private val endpoint: String = "https://api.deepseek.com/v1/chat/completions",
    private val promptPath: String = "/prompts/extract-compatibility.txt"
) : LlmClient {

    private val systemPrompt: String by lazy {
        LlmHttpClient::class.java.getResourceAsStream(promptPath)
            ?.bufferedReader()
            ?.readText()
            ?: throw RuntimeException("Prompt file not found: $promptPath")
    }

    override suspend fun synthesize(rawText: String): List<Compatibility> {
        val response = httpClient.post(endpoint) {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                put("model", "deepseek-chat")
                put("messages", buildJsonArray {
                    add(buildJsonObject {
                        put("role", "system")
                        put("content", systemPrompt)
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

        return parseCompatibilityList(content)
    }

    private fun parseCompatibilityList(jsonString: String): List<Compatibility> {
        val json = Json.parseToJsonElement(jsonString).jsonArray
        return json.map {
            val obj = it.jsonObject
            Compatibility(
                model = obj["model"]?.jsonPrimitive?.content ?: "",
                yearRange = obj["yearRange"]?.jsonPrimitive?.contentOrNull
            )
        }
    }
}
