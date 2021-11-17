package com.app.blockaat.changestores.model

data class StoreResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<StoreDataModel>
)

data class StoreDataModel(
    val id: Int,
    val name_en: String,
    val name_ar: String,
    val currency_en: String,
    val currency_ar: String,
    val flag: String,
    val phonecode: String,
    val iso_code: String,
    val iso3_code: String,
    var isSelected: Boolean? = false
)