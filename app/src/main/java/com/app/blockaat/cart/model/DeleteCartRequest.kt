package com.app.blockaat.cart.model

data class DeleteCartRequest (
   val user_id: String? = null,
   val order_id: String? = null,
   val order_items: String? = null
)