package com.app.blockaat.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.app.blockaat.search.model.SearchDataModel

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_WISHLIST_ENTRIES)
        db.execSQL(SQL_CREATE_S_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_WISHLIST_ENTRIES)
        db.execSQL(SQL_DELETE_SEARCH_ENTRIES)
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    fun addProductToCart(product: ProductsDataModel): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, if (product.product_id == null) "" else product.product_id)
        values.put(COLUMN_PRODUCT_ENTITY_ID, if (product.entity_id == null) "" else product.entity_id)
        values.put(COLUMN_PRODUCT_NAME, if (product.name == null) "" else product.name)
        values.put(COLUMN_PRODUCT_BRAND, if (product.brand == null) "" else product.brand)
        values.put(COLUMN_PRODUCT_REGULAR_PRICE, if (product.regular_price == null) "" else product.regular_price)
        values.put(COLUMN_PRODUCT_IMAGE, if (product.image == null) "" else product.image)
        values.put(COLUMN_PRODUCT_DESC, if (product.description == null) "" else product.description)
        values.put(COLUMN_PRODUCT_QTY, if (product.quantity == null) "" else product.quantity)
        values.put(COLUMN_PRODUCT_FINAL_PRICE, if (product.final_price == null) "" else product.final_price)
        values.put(COLUMN_PRODUCT_SKU, if (product.sku == null) "" else product.sku)
        values.put(COLUMN_PRODUCT_REMAINING_QTY, if (product.remaining_quantity == null) "" else product.remaining_quantity)
        values.put(COLUMN_PRODUCT_IS_FEATURED, if (product.is_featured == null) "" else product.is_featured)
        values.put(COLUMN_PRODUCT_IS_SALEABLE, if (product.is_saleable == null) "" else product.is_saleable)
        values.put(COLUMN_PRODUCT_PRODUCT_TYPE, if (product.type == null) "" else product.type)
        values.put(COLUMN_PRODUCT_COLOR_VALUE, if (product.colorValue == null) "" else product.colorValue)
        values.put(COLUMN_PRODUCT_SIZE_VALUE, if (product.sizeValue == null) "" else product.sizeValue)
        values.put(COLUMN_PRODUCT_ORDER_ITEM_ID, if (product.order_item_id == null) "" else product.order_item_id)

        db.insert(TABLE_CART, null, values)
        db.close()
        close()
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun updateProductsInCart(strQty: String?, productID: String?, productEntityID: String?) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_QTY, strQty)
        db.update(TABLE_CART, values, "$COLUMN_PRODUCT_ENTITY_ID = $productEntityID", null)
        db.close()
        close()
    }

    @Throws(SQLiteConstraintException::class)
    fun updateProductsInCartOffline(strQty: String?, productID: String?, productEntityID: String?, strColor: String?, strSize: String?) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_QTY, strQty)
        values.put(COLUMN_PRODUCT_COLOR_VALUE, strColor)
        values.put(COLUMN_PRODUCT_SIZE_VALUE, strSize)
        db.update(TABLE_CART, values, "$COLUMN_PRODUCT_ENTITY_ID = $productEntityID", null)
        db.close()
        close()
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteProductFromCart(productID: String, productEntityID: String): Boolean {
        val db = writableDatabase
        db.delete(TABLE_CART, "$COLUMN_PRODUCT_ENTITY_ID = $productEntityID", null)
        db.close()
        close()
        return true

    }

    fun getQtyInCart(productID: String?, productEntityID: String?): String {
        val db = writableDatabase
        var qty = ""
        val c = db.rawQuery("select DISTINCT $COLUMN_PRODUCT_QTY from $TABLE_CART where $COLUMN_PRODUCT_ENTITY_ID=$productEntityID", null)
        if (c.moveToFirst())
            qty = c.getString(c.getColumnIndex(COLUMN_PRODUCT_QTY))
        c.close()
        close()
        return qty
    }

    fun getTotalCartProductCount(): Int {
        val countQuery = "SELECT  * FROM $TABLE_CART"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val cnt = cursor.count
        cursor.close()
        close()
        return cnt
    }

    @Throws(SQLiteConstraintException::class)
    fun isProductPresentInCart(productID: String?, productEntityID: String?): Boolean {
        val db = writableDatabase
        val selectString = "SELECT $COLUMN_PRODUCT_ENTITY_ID FROM $TABLE_CART where $COLUMN_PRODUCT_ENTITY_ID=$productEntityID"
        val cursor = db.rawQuery(selectString, null)
        var hasObject = false
        if (cursor.moveToFirst()) {
            hasObject = true
        }
        cursor.close()
        close()
        return hasObject
    }

    @Throws(SQLiteConstraintException::class)
    fun getAllCartProducts(): ArrayList<ProductsDataModel> {
        val arrListAllCartProducts = ArrayList<ProductsDataModel>()
        val db = writableDatabase
        var cursor: Cursor? = null
        cursor = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ID))
                val name = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)))
                val brand_name = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_BRAND)))
                val description = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_DESC)))
                val quantity = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QTY)))
                val final_price = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_FINAL_PRICE)))
                val regular_price = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_REGULAR_PRICE)))
                val image = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IMAGE)))
                val sku = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_SKU)))
                val remaining_quantity = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_REMAINING_QTY)))
                val is_featured = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IS_FEATURED)))
                val is_saleable = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_IS_SALEABLE)))
                val product_type = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_PRODUCT_TYPE)))
                val entity_id = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ENTITY_ID)))
                val size_value = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_SIZE_VALUE)))
                val color_value = Global.checkNull(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_COLOR_VALUE)))

                arrListAllCartProducts.add(ProductsDataModel(id, entity_id, name, brand_name, image, description,
                        quantity, final_price, regular_price, sku, remaining_quantity.toString(), is_featured.toString(), is_saleable.toString()
                        , product_type, size_value, color_value))

                cursor.moveToNext()
            }
        }
        cursor.close()
        close()
        return arrListAllCartProducts
    }

    @Throws(SQLiteConstraintException::class)
    fun getTotalQtyCount(): Int {
        var total = 0
        val db = writableDatabase
        val cursor: Cursor? = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return 0
        }
        try {
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    total += ((cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QTY)).toInt()))
                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
            total = 0
        }
        cursor?.close()
        close()
        return total
    }

    @Throws(SQLiteConstraintException::class)
    fun getTotalCartAmount(): Double {
        var total = 0.00
        val db = writableDatabase
        val cursor: Cursor? = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return 0.00
        }
        try {
            if (cursor!!.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    if (cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_REGULAR_PRICE)) != null && cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_FINAL_PRICE)) != null
                            && cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_REGULAR_PRICE)).toDouble() > cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_FINAL_PRICE)).toDouble()) {
                        total += ((cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_FINAL_PRICE)).toDouble() * cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QTY)).toDouble()))
                    } else {
                        total += ((cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_REGULAR_PRICE)).toDouble() * cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QTY)).toDouble()))
                    }

                    cursor.moveToNext()
                }
            }
        } catch (e: Exception) {
        }
        cursor?.close()
        close()
        return total
    }

    @Throws(SQLiteConstraintException::class)
    fun getAllCartEntityIDs(): java.util.ArrayList<String> {
        val allCartProductIDs = java.util.ArrayList<String>()
        val db = writableDatabase
        val cursor: Cursor? = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return java.util.ArrayList()
        }
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                allCartProductIDs.add(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ENTITY_ID)))
                cursor.moveToNext()
            }
        }
        cursor.close()
        close()
        return allCartProductIDs
    }

    @Throws(SQLiteConstraintException::class)
    fun getAllCartProductQty(): ArrayList<String> {
        val allCartProductQty = ArrayList<String>()
        val db = writableDatabase
        val cursor: Cursor? = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                allCartProductQty.add(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_QTY)))
                cursor.moveToNext()
            }
        }
        cursor.close()
        close()
        return allCartProductQty
    }

    //wishlist
    @Throws(SQLiteConstraintException::class)
    fun isProductPresentInWishlist(productID: String?): Boolean {
        val db = writableDatabase
        val selectString = "SELECT $COLUMN_PRODUCT_ID FROM $TABLE_WISHLIST where $COLUMN_PRODUCT_ID=$productID"
        val cursor = db.rawQuery(selectString, null)
        var hasObject = false
        if (cursor.moveToFirst()) {
            hasObject = true
        }
        cursor.close()
        close()
        return hasObject
    }

    @Throws(SQLiteConstraintException::class)
    fun addProductToWishlist(product: ProductsDataModel): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_PRODUCT_ID, if (product.product_id == null) "" else product.product_id)
        db.insert(TABLE_WISHLIST, null, values)
        db.close()
        close()
        return true
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteProductFromWishlist(productID: String): Boolean {
        val db = writableDatabase
        db.delete(TABLE_WISHLIST, "$COLUMN_PRODUCT_ID = $productID", null)
        db.close()
        close()
        return true

    }

    fun getTotalWishlistProductCount(): Int {
        val countQuery = "SELECT  * FROM $TABLE_WISHLIST"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val cnt = cursor.count
        cursor.close()
        close()
        return cnt
    }

    fun deleteTable(tableName: String) {
        val db = writableDatabase
        db.execSQL("delete from $tableName")
        close()
    }



    //RECENT SEARCH

    @Throws(SQLiteConstraintException::class)
    fun addToRecentSearch(product: SearchDataModel): Boolean {
        val db = writableDatabase
        val values = ContentValues()

        values.put(COLUMN_S_ID, product.id)
        values.put(COLUMN_S_NAME, product.name)
        values.put(COLUMN_S_TYPE, product.type)
        values.put(COLUMN_S_COUNT, product.product_total)
        db.insert(TABLE_SEARCH, null, values)


        db.close()
        close()
        return true
    }
    //to get recent search

    @Throws(SQLiteConstraintException::class)
    fun getAllRecentSearch(): java.util.ArrayList<SearchDataModel> {
        val arrListAllRecentSearch = java.util.ArrayList<SearchDataModel>()
        val db = writableDatabase
        var cursor: Cursor?
        cursor = try {
            db.rawQuery("select * from " + TABLE_SEARCH, null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_S_ENTRIES)
            return java.util.ArrayList()
        }

        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                val id = cursor.getString(cursor.getColumnIndex(COLUMN_S_ID))
                val name = Global.checkEmptyAndNUll(cursor.getString(cursor.getColumnIndex(COLUMN_S_NAME)))
                val type = Global.checkEmptyAndNUll(cursor.getString(cursor.getColumnIndex(COLUMN_S_TYPE)))
                val product_count = Global.checkEmptyAndNUll(cursor.getString(cursor.getColumnIndex(COLUMN_S_COUNT)))
                arrListAllRecentSearch.add(SearchDataModel(id, type, name, product_count))
                cursor.moveToNext()
            }
        }
        cursor.close()
        close()
        return arrListAllRecentSearch
    }


    @Throws(SQLiteConstraintException::class)
    fun isPresentInRecentSearch(productID: String): Boolean {
        val db = writableDatabase
        val selectString = "SELECT $COLUMN_S_ID FROM $TABLE_SEARCH where $COLUMN_S_ID='$productID'"
        val cursor = db.rawQuery(selectString, null)
        var hasObject = false
        if (cursor.moveToFirst()) {
            hasObject = true
        }
        cursor.close()
        close()
        return hasObject
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteFromRecent(productID: String): Boolean {
        val db = writableDatabase
        val selection = COLUMN_S_ID + " LIKE ?"
        val selectionArgs = arrayOf(productID)
        db.delete(TABLE_SEARCH, selection, selectionArgs)
        db.close()
        close()
        return true

    }

    fun deleteFirstRow() {
        val db = writableDatabase
        val cursor = db.query(TABLE_SEARCH, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val rowId = cursor.getString(cursor.getColumnIndex(COLUMN_S_ID))
            db.delete(TABLE_SEARCH, COLUMN_S_ID + "=?", arrayOf<String>(rowId))
        }
        db.close()
    }


    @Throws(SQLiteConstraintException::class)
    fun deleteAll(tableName: String) {
        val db = writableDatabase
        db.execSQL("delete from " + tableName)

    }

    @Throws(SQLiteConstraintException::class)
    fun getAllOrderId(): ArrayList<String> {
        val allCartProductQty = ArrayList<String>()
        val db = writableDatabase
        val cursor: Cursor?
        cursor = try {
            db.rawQuery("select * from $TABLE_CART", null)
        } catch (e: SQLiteException) {
            db.execSQL(SQL_CREATE_ENTRIES)
            return ArrayList()
        }
        if (cursor!!.moveToFirst()) {
            while (!cursor.isAfterLast) {
                allCartProductQty.add(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_ORDER_ITEM_ID)))
                cursor.moveToNext()
            }
        }
        cursor.close()
        close()
        return allCartProductQty
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "boutikey.db"
        const val TABLE_CART = "table_cart"
        private const val TABLE_WISHLIST = "table_wishlist"
        const val TABLE_SEARCH = "table_search"

        private const val COLUMN_ID = "_id"
        private const val COLUMN_PRODUCT_ID = "product_id"
        private const val COLUMN_PRODUCT_ENTITY_ID = "product_entity_id"
        private const val COLUMN_PRODUCT_NAME = "product_name"
        private const val COLUMN_PRODUCT_BRAND = "product_brand"
        private const val COLUMN_PRODUCT_REGULAR_PRICE = "product_regular_price"
        private const val COLUMN_PRODUCT_IMAGE = "product_image"
        private const val COLUMN_PRODUCT_DESC = "product_desc"
        private const val COLUMN_PRODUCT_QTY = "product_quantity"
        private const val COLUMN_PRODUCT_FINAL_PRICE = "product_final_price"
        private const val COLUMN_PRODUCT_SKU = "product_sku"
        private const val COLUMN_PRODUCT_REMAINING_QTY = "product_remaining_qty"
        private const val COLUMN_PRODUCT_IS_FEATURED = "product_is_featured"
        private const val COLUMN_PRODUCT_IS_SALEABLE = "product_is_saleable"
        private const val COLUMN_PRODUCT_PRODUCT_TYPE = "product_type"
        private const val COLUMN_PRODUCT_SIZE_VALUE = "size_value"
        private const val COLUMN_PRODUCT_COLOR_VALUE = "color_value"
        private const val COLUMN_PRODUCT_ORDER_ITEM_ID = "order_item_id"
        //Recent Search
        private const val COLUMN_S_ID = "recent_s_id"
        private const val COLUMN_S_NAME = "recent_s_name"
        private const val COLUMN_S_TYPE = "recent_s_type"
        private const val COLUMN_S_COUNT = "recent_s_count"


        private const val SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_CART + " (" +
                        COLUMN_ID + " TEXT PRIMARY KEY," +
                        COLUMN_PRODUCT_ID + " TEXT," +
                        COLUMN_PRODUCT_ENTITY_ID + " TEXT," +
                        COLUMN_PRODUCT_NAME + " TEXT," +
                        COLUMN_PRODUCT_BRAND + " TEXT," +
                        COLUMN_PRODUCT_REGULAR_PRICE + " TEXT," +
                        COLUMN_PRODUCT_IMAGE + " TEXT," +
                        COLUMN_PRODUCT_DESC + " TEXT," +
                        COLUMN_PRODUCT_QTY + " TEXT," +
                        COLUMN_PRODUCT_FINAL_PRICE + " TEXT," +
                        COLUMN_PRODUCT_SKU + " TEXT," +
                        COLUMN_PRODUCT_REMAINING_QTY + " TEXT," +
                        COLUMN_PRODUCT_IS_FEATURED + " TEXT," +
                        COLUMN_PRODUCT_IS_SALEABLE + " TEXT," +
                        COLUMN_PRODUCT_PRODUCT_TYPE + " TEXT," +
                        COLUMN_PRODUCT_SIZE_VALUE + " TEXT," +
                        COLUMN_PRODUCT_ORDER_ITEM_ID + " TEXT," +
                        COLUMN_PRODUCT_COLOR_VALUE + " TEXT)"


        private const val SQL_CREATE_WISHLIST_ENTRIES =
                "CREATE TABLE " + TABLE_WISHLIST + " (" +
                        COLUMN_ID + " TEXT PRIMARY KEY," +
                        COLUMN_PRODUCT_ID + " TEXT)"
        //Recent  search
        private const val SQL_CREATE_S_ENTRIES =
                "CREATE TABLE " + TABLE_SEARCH + " (" +
                        COLUMN_S_ID + " TEXT PRIMARY KEY," +
                        COLUMN_S_NAME + " TEXT," +
                        COLUMN_S_TYPE + " TEXT," +
                        COLUMN_S_COUNT + " TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_CART"
        private const val SQL_DELETE_WISHLIST_ENTRIES = "DROP TABLE IF EXISTS $TABLE_WISHLIST"

        private const val SQL_DELETE_SEARCH_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_SEARCH
    }
}

