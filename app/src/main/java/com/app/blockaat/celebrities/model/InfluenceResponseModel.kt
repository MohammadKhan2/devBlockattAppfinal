package com.app.blockaat.celebrities.model

data class InfluenceResponseModel(
    val `data`: ArrayList<InfluencerList?>,
    val message: String,
    val status: Int,
    val success: Boolean
)

data class InfluencerList(
    val image: String,
    val id: Int,
    val title: String,
    val banner: String

)