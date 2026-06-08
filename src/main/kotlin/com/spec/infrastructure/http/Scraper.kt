package com.spec.infrastructure.http

import com.microsoft.playwright.Playwright

class Scraper(private val playwright: Playwright) {

    fun scrapeProductPage(url: String): ProductPageData {
        val browser = playwright.chromium().launch(
            com.microsoft.playwright.BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500.0)
        )
        val page = browser.newPage()
        page.navigate(url)

        val imageCarousel = page.locator(ScrapingConfig.IMAGE_CONTAINER_SELECTOR)
        val imageElements = imageCarousel.locator("img").all()
        val images = imageElements.map { it.getAttribute("src") ?: "" }
            .filter { it.startsWith("http") }

        page.getByText(ScrapingConfig.VER_MAIS_SELECTOR).click()
        val descriptionElement = page.locator(ScrapingConfig.DESCRIPTION_CONTAINER_SELECTOR)
        val description = descriptionElement.locator("p").innerText()


        browser.close()

        return ProductPageData(description, images)
    }
}

data class ProductPageData(
    val description: String,
    val images: List<String>
)