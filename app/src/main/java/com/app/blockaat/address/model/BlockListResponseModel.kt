package com.app.blockaat.address.model

data class BlockListResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<com.app.blockaat.address.model.BlockListDataModel>
)

data class BlockListDataModel(
    val id: Int,
    val name: String
)