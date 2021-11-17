package com.app.blockaat.search.model

data class SearchResponseModel(
    val data: List<SearchDataModel>? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val status: Int? = null
)
