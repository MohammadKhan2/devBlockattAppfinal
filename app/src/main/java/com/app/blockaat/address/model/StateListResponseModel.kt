package com.app.blockaat.address.model

data class StateListResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<com.app.blockaat.address.model.StateListDataModel>?
)

data class StateListDataModel(
    val id: Int?,
    val name: String?,
    val has_areas: String?
)