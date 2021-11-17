package com.app.blockaat.productdetails.model

import java.io.Serializable

data class ProductDetailResponseModel(
    val success: Boolean? = false,
    val status: Int? = 0,
    val message: String? = null,
    val data: ProductDetailDataModel? = null
)

data class ProductDetailDataModel(
    val id: String? = null,
    val name: String? = null,
    val short_description: String? = null,
    val description: String? = null,
    val composition_and_care: String? = null,
    val about_designer: String? = null,
    val specification: String? = null,
    val shipping_free_returns: String? = null,
    val SKU: String? = null,
    val regular_price: String? = null,
    val final_price: String? = null,
    val currency_code: String? = null,
    var remaining_quantity: Int? = 0,
    val is_featured: Int? = 0,
    val new_from_date: String? = null,
    val new_to_date: String? = null,
    val brand_id: Int? = 0,
    val brand_name: String? = null,
    val brand_banner_image: String? = null,
    val image: String? = null,
    val images: ArrayList<String>? = null,
    val video: String? = null,
    val configurable_option: ArrayList<ProductDetailConfigurableOptionModel>? = null,
    val recommended_products: ArrayList<ProductDetailRelatedModel>? = null,
    val reviews: ArrayList<ReviewsModel>? = null,
    val is_saleable: Int? = 0,
    val product_type: String? = null,
    val item_in_cart: Int? = 0,
    var item_in_wishlist: Int? = 0,
    val average_rating: String? = null,
    val size_guide: String? = null,
    var influencer_id: Int? = 0,
    val product_code: String? = null
) : Serializable

data class ProductDetailRelatedModel(
    val id: String? = null,
    val name: String? = null,
    val short_description: String? = null,
    val description: String? = null,
    val specification: String? = null,
    val shipping_free_returns: String? = null,
    val SKU: String? = null,
    val regular_price: String? = null,
    val final_price: String? = null,
    val currency_code: String? = null,
    val remaining_quantity: Int? = 0,
    val is_featured: Int? = 0,
    val new_from_date: String? = null,
    val new_to_date: String? = null,
    val brand_name: String? = null,
    val boutique_name: String? = null,
    val image: String? = null,
    val is_saleable: Int? = 0,
    val product_type: String? = null,
    var item_in_wishlist: Int? = 0,
    var influencer_id: Int? = 0,
    val product_code: String? = null,
    val video: String? = null
) : Serializable

data class ProductDetailConfigurableOptionModel(
    val type: String? = null,
    val attribute_id: String? = null,
    val attribute_code: String? = null,
    val attributes: ArrayList<ProductDetailAttributeModel>? = null
) : Serializable

data class ProductDetailAttributeModel(
    val option_id: String? = null,
    val option_product_id: String? = null,
    val value: String? = null,
    val color: String? = null,
    var filter_type: String? = null,
    var isEnabled: Boolean = false,
    var isSelected: Boolean = false
) : Serializable