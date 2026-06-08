package com.spec.infrastructure.http

import com.spec.infrastructure.proxy.ProxyConfig
import com.spec.infrastructure.proxy.ProxyManager
import com.spec.infrastructure.proxy.ProxyType
import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Url

class MercadoLivreClient(
    private val httpClient: HttpClient,
    private val proxyManager: ProxyManager? = null
) {
    suspend fun search(query: String): String {
        val url = "https://lista.mercadolivre.com.br/${query.replace(" ", "+")}"
        return fetch(url)
    }

    suspend fun fetchPage(url: String): String {
        return fetch(url)
    }

    private suspend fun fetch(url: String): String {
        while (true) {
            val proxyConfig = proxyManager?.getProxy()
            val client = if (proxyConfig != null) {
                HttpClient {
                    engine {
                        val ktorProxy = when (proxyConfig.type) {
                            ProxyType.HTTP -> ProxyBuilder.http(Url("http://${proxyConfig.host}:${proxyConfig.port}"))
                            ProxyType.SOCKS5 -> ProxyBuilder.socks(proxyConfig.host, proxyConfig.port)
                        }

                        proxy = ktorProxy
                    }

                }
            } else {
                httpClient
            }

            val response = client.get(url)

            if (response.status.value == 429 || response.status.value == 503) {
                proxyManager?.reportBlock()
                continue
            }

            proxyManager?.reportSuccess()
            return response.bodyAsText()
        }
    }
}