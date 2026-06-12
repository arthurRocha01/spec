package com.spec.infrastructure.http

object ScrapingConfigForPage {
    // Página do anúncio
    val IMAGE_CONTAINER_SELECTOR = "div.ui-pdp-gallery__column"
    val DESCRIPTION_CONTAINER_SELECTOR = "#description"
    val SEE_MORE_SELECTGOR = "Ver descrição completa"
}

object ScrapingConfigForSearchPage {
    val PRODUCTS = "ui-search-layout__item"
    val PRODUCT_IMAGE = "poly-component__picture"
    val PRODUCT_URL = "poly-card__content a"
    val PRODUCT_TITLE = "poly-component__title-wrapper"
}