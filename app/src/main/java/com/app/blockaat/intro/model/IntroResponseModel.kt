package com.app.blockaat.intro.model

import java.io.Serializable

data class IntroResponseModel(
    val `data`: IntroResponseModelItem,
    val message: String,
    val status: Int,
    val success: Boolean
) :Serializable

data class IntroResponseModelItem(
    val id: Int,
    val image: String,
    val link_id: String,
    val link_type: String,
    val type_name: String,
    val url: String
) : Serializable