package com.app.blockaat.cms.model

data class CmsResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: CmsDataModel
)

data class CmsDataModel(
    val id: Int,
    val title: String,
    val content: String
)