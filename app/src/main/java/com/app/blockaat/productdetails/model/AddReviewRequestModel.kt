package com.app.blockaat.productdetails.model

data class AddReviewRequestModel(
    val comment: String,
    val product_id: String,
    val rating: String,
    val title: String,
    val user_id: String
)