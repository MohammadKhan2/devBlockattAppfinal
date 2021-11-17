package com.app.blockaat.category.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class CategoryResponseDataModel(
    val `data`: ArrayList<Subcategory>? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
): Serializable

data class CategoryResponseData(
    val category_products: CategoryResponseProducts? = null,
    val category_tvs: ArrayList<Any>? = null,
    val subcategories: ArrayList<Subcategory>? = null
): Serializable

data class CategoryResponseProducts(
    val filter: ArrayList<Filter>? = null,
    val max_product_price: String? = null,
    val products: ArrayList<Product>? = null,
    val total_pages: Int? = null,
    val total_products: String? = null
): Serializable

data class Filter(
    val filter_name: String? = null,
    val filter_type: String? = null,
    val filter_values: ArrayList<FilterValue>? = null
): Serializable

data class FilterValue(
    val id: String? = null,
    val value: String? = null
): Serializable


data class Product(
    val SKU: String? = null,
    val brand_id: Int? = null,
    val brand_name: String? = null,
    val currency_code: String? = null,
    val description: String? = null,
    val final_price: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val influencer_id: Int? = null,
    val is_featured: Int? = null,
    val is_saleable: Int? = null,
    val item_in_wishlist: Int? = null,
    val name: String? = null,
    val product_type: String? = null,
    val regular_price: String? = null,
    val remaining_quantity: Int? = null,
    val short_description: String? = null,
    val video_id: Int? = null
): Serializable

@Parcelize
data class Subcategory(
    val has_subcategory: String? = null,
    val icon: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val meta_description: String? = null,
    val name: String? = null,
    val subcategories: ArrayList<Subcategory>? = null
): Serializable, Parcelable



