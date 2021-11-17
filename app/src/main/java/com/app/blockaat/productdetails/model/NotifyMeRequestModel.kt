package com.app.blockaat.productdetails.model

data class NotifyMeRequestModel(
    val email: String,
    val name: String,
    val phone: String,
    val product_id: String,
    val user_id: String,
    val phone_code: String
)