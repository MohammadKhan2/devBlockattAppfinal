package com.app.blockaat.productlisting.model

import android.os.Parcelable
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.home.model.Banner
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class ProductListingResponseModel(
    val success: Boolean? = null,
    val status: Int? = null,
    val message: String? = null,
    val data: ProductListingDataModel? = null
) : Serializable

data class ProductListingDataModel(
    val products: ArrayList<ProductListingProductModel>? = null,
    val total_products: String? = null,
    val total_pages: Int? = null,
    val max_product_price: String? = null,
    val filter: ArrayList<ProductListingFilterModel>? = null,
    val influencer_details: InfluencerDetails? = null,
    val topBanner: TopBanner? = null,
    val category_products: ProductListingDataModel? = null,
    val category_tvs: ArrayList<Tvs?>? = null,
    val subcategories: ArrayList<Subcategory?>? = null

) : Serializable

data class InfluencerDetails(
    val banners: ArrayList<Banner?>? = null,
    var tvs: ArrayList<Tvs?>? = null
) : Serializable

data class TopBanner(
    val banner: String? = null,
    var type: String? = null
) : Serializable

data class Tvs(
    val id: Int? = 0,
    val name: String? = null,
    val video_id: String? = null,
    val image_name: String? = null,
    val banner: String? = null
) : Serializable

@Parcelize
data class ProductListingFilterModel(
    val filter_name: String? = null,
    val filter_type: String? = null,
    var filter_value: String? = null,
    var filter_values: ArrayList<ProductListingFilterValueModel>? = null
) : Serializable, Parcelable

@Parcelize
data class ProductListingFilterValueModel(
    val id: String? = null,
    val value: String? = null,
    var isSelected: Boolean = false
) : Parcelable, Serializable

data class ProductListingProductModel(
    val id: String? = null,
    val name: String? = null,
    val short_description: String? = null,
    val description: String? = null,
    val SKU: String? = null,
    val regular_price: String? = null,
    val final_price: String? = null,
    val currency_code: String? = null,
    val remaining_quantity: Int? = null,
    val is_featured: Int? = null,
    val totalItem: Int? = 0,
    var item_in_wishlist: Int? = null,
    val is_saleable: Int? = null,
    val brand_name: String? = null,
    val image: String? = null,
    val product_type: String? = null,
    val type: String? = null,
    val product_code: String? = null,
    val category_tvs: ArrayList<Tvs>? = null,
    val influencer_details: InfluencerDetails? = null,
    val topBanner: TopBanner? = null,
    val arrListSubCategory: ArrayList<Subcategory>? = null
) : Serializable