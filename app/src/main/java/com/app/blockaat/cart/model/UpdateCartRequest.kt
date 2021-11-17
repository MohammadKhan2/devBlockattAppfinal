package com.app.blockaat.cart.model

data class UpdateCartRequest (
    val products: String? = null,
    val quantity: String? = null,
    val videos: String? = null,
    val order_id: String? = null,
    val celebs: String? = null,
    val user_id: String? = null,
    val order_items: String? = null
    )