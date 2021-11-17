package com.app.blockaat.checkout.model

data class CancelCheckoutResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: List<Any>
)