package com.app.blockaat.brands.model

data class BrandsResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: List<BrandsDataModel>
)

data class BrandsDataModel(
    val id: String,
    val name: String,
    val image_name: String,
    val banner:String
)