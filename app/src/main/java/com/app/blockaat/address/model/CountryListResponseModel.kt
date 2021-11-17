package com.app.blockaat.address.model

data class CountryListResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<com.app.blockaat.address.model.CountryListDataModel>?
)

data class CountryListDataModel(
    val id: Int?,
    val name: String?,
    val phonecode: String?,
    val iso: String?,
    val has_states: String?
)