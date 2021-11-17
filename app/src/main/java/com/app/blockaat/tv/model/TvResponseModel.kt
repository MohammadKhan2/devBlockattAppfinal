package com.app.blockaat.tv.model

import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.Tvs

data class TvResponseModel(
    val `data`: AllTvData? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
)

data class AllTvData
    (
    val tv: ArrayList<Tvs>? = null,
    val filters: ArrayList<ProductListingFilterModel>? = null

)

