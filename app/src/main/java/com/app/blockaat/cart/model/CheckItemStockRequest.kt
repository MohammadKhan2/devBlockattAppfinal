package com.app.blockaat.cart.model

data class CheckItemStockRequest (
    val user_id:String? = null,
    val products:String? = null,
    val quantity:String? = null,
    val order_id:String? = null,
    val order_items:String? = null,
    val address_id:String? = null

)