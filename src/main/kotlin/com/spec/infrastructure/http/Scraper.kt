package com.spec.infrastructure.http

import com.microsoft.playwright.Playwright
import com.spec.domain.product.SearchResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Scraper(private val playwright: Playwright) {

    fun scrapeProductPage(url: String): ProductPageData {
        val browser = playwright.chromium().launch(
            com.microsoft.playwright.BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500.0)
        )
        val page = browser.newPage()
        page.navigate(url)

        val imageCarousel = page.locator(ScrapingConfigForPage.IMAGE_CONTAINER_SELECTOR)
        val imageElements = imageCarousel.locator("img").all()
        val images = imageElements.map { it.getAttribute("src") ?: "" }
            .filter { it.startsWith("http") }

        page.getByText(ScrapingConfigForPage.SEE_MORE_SELECTGOR).click()
        val descriptionElement = page.locator(ScrapingConfigForPage.DESCRIPTION_CONTAINER_SELECTOR)
        val description = descriptionElement.locator("p").innerText()


        browser.close()

        return ProductPageData(description, images)
    }

    fun scrapeSearchResults(html: String): List<SearchResult> {
        val doc: Document = Jsoup.parse(html)

        val productsSearched = doc.select(ScrapingConfigForSearchPage.PRODUCTS)

        val listResults = mutableListOf<SearchResult>()

        for (productSearched in productsSearched) {
            val title = productSearched.select(ScrapingConfigForSearchPage.PRODUCT_TITLE).text()
            val url = productSearched.select((ScrapingConfigForSearchPage.PRODUCT_URL)).attr("href")
            val thumbnail = productSearched.select(ScrapingConfigForSearchPage.PRODUCT_IMAGE).attr("src")

            listResults.add(SearchResult(
                title = title,
                url = url,
                thumbnail = thumbnail
            ))
        }

        return listResults
    }

}

data class ProductPageData(
    val description: String,
    val images: List<String>
)