package com.app.blockaat.wishlist.modelclass

data class WishListResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: ArrayList<WishListDataModel> = arrayListOf()
)

data class WishListDataModel(
    val id: String?,
    val name: String?,
    val short_description: String?,
    val description: String?,
    val specification: String?,
    val shipping_free_returns: String?,
    val SKU: String?,
    val regular_price: String?,
    val final_price: String?,
    val currency_code: String?,
    val remaining_quantity: Int?,
    val is_featured: Int?,
    val new_from_date: String?,
    val new_to_date: String?,
    val brand_name: String?,
    val image: String?,
    val images: ArrayList<String>?,
    val video: String?,
    val configurable_option: ArrayList<WishListConfigurableOptionModel>?,
    val related_products: ArrayList<Any>?,
    val reviews: ArrayList<Any>?,
    val is_saleable: Int?,
    val product_type: String?,
    val item_in_cart: Int?,
    val item_in_wishlist: Int?,
    val average_rating: String?,
    val size_guide: String?
)

data class WishListConfigurableOptionModel(
    val type: String?,
    val name: String?,
    val attribute_id: String?,
    val attribute_code: String?,
    val attributes: ArrayList<WishListAttributeModel>?
)

data class WishListAttributeModel(
    val option_id: String?,
    val option_product_id: String?,
    val value: String?,
    val hex_code: String?
)