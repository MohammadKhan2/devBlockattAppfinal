package com.app.blockaat.productdetails.model

data class AddReviewResponseModel(
    val `data`: AddReviewResponseModelItem,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class AddReviewResponseModelItem(
    val comment: String,
    val id: Int,
    val is_approved: String,
    val name: String,
    val rating: String,
    val title: String,
    val user_id: String
)