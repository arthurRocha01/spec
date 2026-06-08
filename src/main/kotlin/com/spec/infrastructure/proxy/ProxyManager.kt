package com.spec.infrastructure.proxy

class ProxyManager(private val proxies: List<ProxyConfig>) {
    private var currentIndex = -1
    private var blockCount = 0
    private val maxBlocks = 3

    fun getProxy(): ProxyConfig? {
        if (currentIndex == -1) return null
        return proxies.getOrNull(currentIndex)

    }

    fun reportBlock() {
        blockCount++

        if (blockCount >= maxBlocks) {
            currentIndex++
            blockCount = 0
        }
    }

    fun reportSuccess() {
        blockCount--

        if (blockCount <= 0) {
            blockCount = 0
            currentIndex = -1
        }
    }
}

data class ProxyConfig(
    val host: String,
    val port: Int,
    val type: ProxyType = ProxyType.HTTP
)

enum class ProxyType { HTTP, SOCKS5 }