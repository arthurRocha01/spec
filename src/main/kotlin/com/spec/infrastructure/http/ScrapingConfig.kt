package com.spec.infrastructure.http

object ScrapingConfig {
    // Página do anúncio
    val IMAGE_CONTAINER_SELECTOR = "div.ui-pdp-gallery__column"
    val DESCRIPTION_CONTAINER_SELECTOR = "#description"
    val VER_MAIS_SELECTOR = "Ver descrição completa"

    // Página de busca
    val SEARCH_BASE_URL = "https://lista.mercadolivre.com.br"
    val SEARCH_TITLE_SELECTOR = "h2.ui-search-item__title"
    val SEARCH_URL_SELECTOR = "a.ui-search-link"
    val SEARCH_THUMBNAIL_SELECTOR = "img.ui-search-result-image__element"
    val SEARCH_PRICE_SELECTOR = "span.andes-money-amount__fraction"
}