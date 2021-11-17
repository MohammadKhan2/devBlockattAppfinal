package com.app.blockaat.productdetails.model

import java.io.Serializable

data class ReviewsModel(
    val comment: String,
    val id: Int,
    val name: String,
    val rating: Int,
    val review_date: String,
    val title: String
) : Serializable