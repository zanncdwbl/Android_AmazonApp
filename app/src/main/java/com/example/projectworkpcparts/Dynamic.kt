package com.example.projectworkpcparts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class EventViewModel : ViewModel() {

    private val amazon = AmazonSearchInfo()

    private val _productList = MutableLiveData<List<ProductMatched>>()
    val productList: LiveData<List<ProductMatched>> = _productList

    fun fetchData(context: Context) {
        viewModelScope.launch(IO) {
            try {
                val productList = amazon.getEvents()

                if (productList.isNotEmpty()) {

                    val matchedProducts = matchProducts(context, productList)
                    _productList.postValue(matchedProducts)

                    productList.forEach { product ->
                        // Hopefully removing duplicates
                        if (!product.isLogged)
                            product.isLogged = true

                        // Logging for the sake of easier readability and knowing any issues.-
                        if (product.fullPrice == product.salePrice) {
                            Log.d(
                                "fetchData", "Product Image: ${product.productImage}\n" +
                                        "Product Name: ${product.productName}" + "\nFull Price: ${product.salePrice}"
                            )
                            return@forEach
                        }

                        Log.d(
                            "fetchData", "Product Image: ${product.productImage}\n" +
                                    "Product Name: ${product.productName}" + "\nOn Sale Price: ${product.salePrice}" +
                                    "\nFull Price: ${product.fullPrice}"
                        )

                        val sqLiteManager = SQLiteManager(context)
                        sqLiteManager.insertProduct(
                            product.region,
                            product.productImage,
                            product.productName,
                            product.salePrice,
                            product.fullPrice,
                            product.URL
                        )
                    }
                } else {
                    Log.d("fetchData", "No products fetched")
                }

            } catch (e: Exception) {
                Log.e("fetchData", "Error fetching data: ${e.message}")
            }
        }
    }

    suspend fun searchProductsByUrl(context: Context, searchQuery: String) {
        val productList = amazon.getEvents(searchQuery)
        val matchedProducts = matchProducts(context, productList)
        _productList.postValue(matchedProducts)
    }

    //    Moved from SQL due to so many problems.
    private fun matchProducts(context: Context, productList: List<Product>): List<ProductMatched> {
        val matchedProducts = mutableListOf<ProductMatched>()
        val ukProducts = mutableListOf<Product>()
        val deProducts = mutableListOf<Product>()

        // Separate UK and DE products
        productList.forEach { product ->
            if (product.region.equals("UK", ignoreCase = true)) {
                ukProducts.add(product)
            } else {
                deProducts.add(product)
            }
        }

        // Match UK and DE products
        for (ukProduct in ukProducts) {
            for (deProduct in deProducts) {
                if (!ukProduct.salePrice.isNullOrBlank() || !ukProduct.fullPrice.isNullOrBlank()
                    && !deProduct.salePrice.isNullOrBlank() || !deProduct.fullPrice.isNullOrBlank()
                ) {
                    if (calculateJaccardSimilarity(ukProduct.productName, deProduct.productName)) {
                        val matchedProduct = ProductMatched(
                            region = "Matched",
                            productImage = deProduct.productImage,
                            productName = deProduct.productName,
                            productNameUK = ukProduct.productName,
                            salePriceUK = ukProduct.salePrice,
                            fullPriceUK = ukProduct.fullPrice,
                            salePriceDE = deProduct.salePrice,
                            fullPriceDE = deProduct.fullPrice,
                            URL_UK = ukProduct.URL,
                            URL_DE = deProduct.URL
                        )
                        matchedProducts.add(matchedProduct)
                    }
                }
            }
        }

        val sqLiteManager = SQLiteManager(context)
        matchedProducts.forEach { product ->
            sqLiteManager.insertMatchedProduct(
                product.productImage,
                product.productName,
                product.productNameUK,
                product.salePriceUK,
                product.fullPriceUK,
                product.salePriceDE,
                product.fullPriceDE,
                product.URL_UK,
                product.URL_DE
            )
        }

        return matchedProducts
    }

    private fun getFirstHalfOfString(str: String): String {
        val splitIndex = str.length / 2
        return str.substring(0, splitIndex)
    }

    fun calculateJaccardSimilarity(productUK: String, productDE: String): Boolean {

        // Split the product names into halves
        val halfUK = getFirstHalfOfString(productUK)
        val halfDE = getFirstHalfOfString(productDE)

        val tokens2 = halfUK.split("\\s+".toRegex()).toSet()
        val tokens1 = halfDE.split("\\s+".toRegex()).toSet()

        val intersectionSize = tokens1.intersect(tokens2)
        val unionSize = tokens1.union(tokens2)

        val similarity = intersectionSize.size.toDouble() / unionSize.size.toDouble()
        val threshold = 0.5

        return similarity >= threshold
    }

    fun fetchAllProducts(context: Context) {
        viewModelScope.launch(IO) {
            try {
                val productList = mutableListOf<ProductMatched>()
                val sqLiteManager = SQLiteManager.getInstance(context)

                productList.addAll(sqLiteManager.getAllMatchedProducts())

                _productList.postValue(productList)
            } catch (e: Exception) {
                Log.e("fetchAllProducts", "Error fetching all products: ${e.message}")
            }
        }
    }
}

