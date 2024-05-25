package com.example.projectworkpcparts
import android.provider.BaseColumns

object Constants {
    object URLS {
        const val Amazon_UK_URL = "https://www.amazon.co.uk"
        const val Amazon_DE_URL = "https://www.amazon.de"
    }
}

data class Product(
    val region: String,
    val productImage: String,
    val productName: String,
    val salePrice: String?,
    val fullPrice: String?,
    val URL: String,
    var isLogged: Boolean = false
)

data class ProductMatched(
    val region: String,
    val productImage: String,
    val productName: String,
    val productNameUK: String?,
    val salePriceUK: String?,
    val fullPriceUK: String?,
    val salePriceDE: String?,
    val fullPriceDE: String?,
    val URL_UK: String,
    val URL_DE: String
)

object ProductDB {
    const val DATABASE_NAME = "PC_Parts_DB"
    const val DATABASE_VERSION = 44
    const val TABLE_NAME_UK = "PC_Part_UK"
    const val TABLE_NAME_DE = "PC_Part_DE"
    const val TABLE_NAME_MATCHED = "PC_Part_Matched"
    const val COUNTER = "counter" // Primary Key

//    UK or DE table
    object ProductEntry : BaseColumns {
        const val PRODUCTIMAGE_FIELD = "productImage"
        const val PRODUCTNAME_FIELD = "productName"
        const val SALEPRICE_FIELD = "salePrice"
        const val FULLPRICE_FIELD = "fullPrice"
        const val URL = "url"
    }

//    Matched
    object ProductEntryMatched : BaseColumns {
        const val PRODUCTIMAGE_FIELD = "productImage"
        const val PRODUCTNAME_FIELD = "productName"
        const val PRODUCTNAME_UK_FIELD = "productNameUK"
        const val SALEPRICE_FIELD_UK = "salePriceUK"
        const val FULLPRICE_FIELD_UK = "fullPriceUK"
        const val SALEPRICE_FIELD_DE = "salePriceDE"
        const val FULLPRICE_FIELD_DE = "fullPriceDE"
        const val URL_UK = "url_uk"
        const val URL_DE = "url_de"
    }
}