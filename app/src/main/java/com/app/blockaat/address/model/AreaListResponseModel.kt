package com.app.blockaat.address.model

data class AreaListResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<com.app.blockaat.address.model.AreaListDataModel>?
)

data class AreaListDataModel(
    val id: Int?,
    val name: String?,
    val has_blocks: String?
)