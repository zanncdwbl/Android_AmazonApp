package com.example.projectworkpcparts

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class SQLiteManager(context: Context?) : SQLiteOpenHelper(context, ProductDB.DATABASE_NAME, null, ProductDB.DATABASE_VERSION) {

    companion object {
        @Volatile
        private var instance: SQLiteManager? = null

        fun getInstance(context: Context): SQLiteManager {
            return instance ?: synchronized(this) {
                instance ?: SQLiteManager(context.applicationContext).also { instance = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE ${ProductDB.TABLE_NAME_UK} (" +
                    "${ProductDB.COUNTER} INTEGER PRIMARY KEY," +
                    "${ProductDB.ProductEntry.PRODUCTIMAGE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.PRODUCTNAME_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.SALEPRICE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.FULLPRICE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.URL} TEXT)"
        )

        db?.execSQL(
            "CREATE TABLE ${ProductDB.TABLE_NAME_DE} (" +
                    "${ProductDB.COUNTER} INTEGER PRIMARY KEY," +
                    "${ProductDB.ProductEntry.PRODUCTIMAGE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.PRODUCTNAME_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.SALEPRICE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.FULLPRICE_FIELD} TEXT," +
                    "${ProductDB.ProductEntry.URL} TEXT)"
        )

        db?.execSQL(
            "CREATE TABLE ${ProductDB.TABLE_NAME_MATCHED} (" +
                    "${ProductDB.COUNTER} INTEGER PRIMARY KEY," +
                    "${ProductDB.ProductEntryMatched.PRODUCTIMAGE_FIELD} TEXT," +
                    "${ProductDB.ProductEntryMatched.PRODUCTNAME_FIELD} TEXT," +
                    "${ProductDB.ProductEntryMatched.PRODUCTNAME_UK_FIELD} TEXT," +
                    "${ProductDB.ProductEntryMatched.SALEPRICE_FIELD_UK} TEXT," +
                    "${ProductDB.ProductEntryMatched.FULLPRICE_FIELD_UK} TEXT," +
                    "${ProductDB.ProductEntryMatched.SALEPRICE_FIELD_DE} TEXT," +
                    "${ProductDB.ProductEntryMatched.FULLPRICE_FIELD_DE} TEXT," +
                    "${ProductDB.ProductEntryMatched.URL_UK} TEXT," +
                    "${ProductDB.ProductEntryMatched.URL_DE} TEXT)"
        )
    }

    fun getAllMatchedProducts(): List<ProductMatched> {
        val productList = mutableListOf<ProductMatched>()
        val cursor = readableDatabase.rawQuery("SELECT * FROM ${ProductDB.TABLE_NAME_MATCHED}", null)

        while (cursor.moveToNext()) {
            val productImage = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.PRODUCTIMAGE_FIELD))
            val productName = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.PRODUCTNAME_FIELD))
            val productNameUK = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.PRODUCTNAME_UK_FIELD))
            val salePriceUK = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.SALEPRICE_FIELD_UK))
            val fullPriceUK = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.FULLPRICE_FIELD_UK))
            val salePriceDE = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.SALEPRICE_FIELD_DE))
            val fullPriceDE = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.FULLPRICE_FIELD_DE))
            val URL_UK = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.URL_UK))
            val URL_DE = cursor.getString(cursor.getColumnIndexOrThrow(ProductDB.ProductEntryMatched.URL_DE))

            val product = ProductMatched("", productImage, productName, productNameUK, salePriceUK, fullPriceUK, salePriceDE, fullPriceDE, URL_UK, URL_DE)
            productList.add(product)
        }
        cursor.close()
        return productList
    }


    fun insertProduct(region: String, productImage: String, productName: String, salePrice: String?, fullPrice: String?, URL: String?) {
        val tableName = if (region.equals("UK", ignoreCase = true))
            ProductDB.TABLE_NAME_UK else ProductDB.TABLE_NAME_DE
        val values = ContentValues().apply {
            put(ProductDB.ProductEntry.PRODUCTIMAGE_FIELD, productImage)
            put(ProductDB.ProductEntry.PRODUCTNAME_FIELD, productName)
            put(ProductDB.ProductEntry.SALEPRICE_FIELD, salePrice)
            put(ProductDB.ProductEntry.FULLPRICE_FIELD, fullPrice)
            put(ProductDB.ProductEntry.URL, URL)
        }
        writableDatabase?.insert(tableName, null, values)
    }

    fun insertMatchedProduct(productImage: String, productName: String?, productNameUK: String?, salePriceUK: String?,
                             fullPriceUK: String?, salePriceDE: String?, fullPriceDE: String?, URL_UK: String?, URL_DE: String?) {
        val values = ContentValues().apply {
            put(ProductDB.ProductEntryMatched.PRODUCTIMAGE_FIELD, productImage)
            put(ProductDB.ProductEntryMatched.PRODUCTNAME_FIELD, productName)
            put(ProductDB.ProductEntryMatched.PRODUCTNAME_UK_FIELD, productNameUK)
            put(ProductDB.ProductEntryMatched.SALEPRICE_FIELD_UK, salePriceUK)
            put(ProductDB.ProductEntryMatched.FULLPRICE_FIELD_UK, fullPriceUK)
            put(ProductDB.ProductEntryMatched.SALEPRICE_FIELD_DE, salePriceDE)
            put(ProductDB.ProductEntryMatched.FULLPRICE_FIELD_DE, fullPriceDE)
            put(ProductDB.ProductEntryMatched.URL_UK, URL_UK)
            put(ProductDB.ProductEntryMatched.URL_DE, URL_DE)
        }
        writableDatabase?.insert(ProductDB.TABLE_NAME_MATCHED, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS ${ProductDB.TABLE_NAME_UK}")
        db?.execSQL("DROP TABLE IF EXISTS ${ProductDB.TABLE_NAME_DE}")
        db?.execSQL("DROP TABLE IF EXISTS ${ProductDB.TABLE_NAME_MATCHED}")
        onCreate(db)
    }

    fun deleteDatabase(context: Context) {
        writableDatabase.close()

        context.getDatabasePath(ProductDB.DATABASE_NAME)?.let { databaseFile ->
            if (databaseFile.exists()) {
                databaseFile.delete()
            }
        }
    }
}
