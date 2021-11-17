package com.app.blockaat.tv.model

import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingProductModel
import java.io.Serializable


data class TvProductResponseModel (
    val success: Boolean? = null,
    val status: Int? = null,
    val message: String? = null,
    val data: TvProductDataModel? = null,
    val video_id: String? = null,
    val name: String? = null,
    val image: String? = null
    )
data class TvProductDataModel(
    val products: ArrayList<ProductListingProductModel>? = null,
    val total_products: String? = null,
    val total_pages: Int? = null,
    val max_product_price: String? = null,
    val filters: ArrayList<ProductListingFilterModel>? = null

) : Serializable

