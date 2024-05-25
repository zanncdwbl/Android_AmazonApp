package com.example.projectworkpcparts

import org.jsoup.Jsoup
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Random

class AmazonSearchInfo {
    private var isFirstStart: Boolean = true
    private val exchangeRate = 1.17

    suspend fun getEvents(searchQuery: String? = null): List<Product> {
        return withContext(Dispatchers.IO) {
            Log.d("AmazonSearchInfo", "Searching for query: $searchQuery")
            val result = mutableListOf<Product>()

            val urls = listOf(Constants.URLS.Amazon_DE_URL, Constants.URLS.Amazon_UK_URL)
            val productsearch = if (isFirstStart) listOf("GPU", "CPU", "RAM", "Motherboard") else listOf(searchQuery)

            val random = Random()
            val delay = random.nextInt(5000) + 1000

            try {
                for (url in urls) {
                    for (search in productsearch) {
                        val doc = Jsoup.connect("$url/s?k=$search&language=en_GB")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
                            .referrer("http://www.google.com")
                            .timeout(delay)
                            .get()

                        val elements = doc.select("div.puisg-row")

                        for (element in elements) {
                            val productURL = element.select("a.a-link-normal").attr("href")
                            val amazonURL = "$url/-/en$productURL"

                            val productImage =
                                element.select("[data-image-latency=s-product-image]").attr("src")
                            val productName = element.select("[data-cy=title-recipe]").text()
                            val combinedPrice = element.select("span.a-offscreen").text()

                            val pricestosplit = combinedPrice.split(" ")
                            var salePrice = pricestosplit.getOrNull(0)?.takeIf { it.isNotBlank() }
                            var fullPrice = pricestosplit.getOrNull(1)?.takeIf { it.isNotBlank() }

                            if (productName.isNotBlank()) {
                                if (!productName.contains("SponsoredSponsored") && !productName.contains("GesponsertGesponsert")
                                ) {
                                    if (url.contains(Constants.URLS.Amazon_DE_URL)) {
                                        val region = "DE"
                                        result.add(Product(region, productImage, productName, salePrice, fullPrice, amazonURL))
                                    }
                                    else {
                                        val region = "UK"

                                        if (salePrice != null) {
                                            salePrice = convertPoundsToEuros(salePrice)
                                        }

                                        if (fullPrice != null) {
                                            fullPrice = convertPoundsToEuros(fullPrice)
                                        }

                                        val product = Product(region, productImage, productName, salePrice, fullPrice, amazonURL)
                                        result.add(product)
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e("AmazonSearchInfo", "Error fetching data from Amazon: ${e.message}")
            } catch (e: Exception) {
                Log.e("AmazonSearchInfo", "Unknown error occurred: ${e.message}")
                e.printStackTrace()
            }

            isFirstStart = false
            return@withContext result
        }
    }

    private fun convertPoundsToEuros(price: String): String {
        val pounds = price.substring(1).toDoubleOrNull() ?: return price
        val euros = pounds * exchangeRate
        return "€${String.format("%.2f", euros)}"
    }
}