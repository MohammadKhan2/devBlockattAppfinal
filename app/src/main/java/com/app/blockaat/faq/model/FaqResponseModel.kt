package com.app.blockaat.faq.model

import java.io.Serializable

data class FaqResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<FaqDataModel>
) : Serializable

data class FaqDataModel(
    val id: Int,
    val question: String,
    val answer: String
) : Serializable