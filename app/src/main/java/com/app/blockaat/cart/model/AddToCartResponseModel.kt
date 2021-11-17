package com.app.blockaat.cart.model


data class AddToCartResponseModel(
        val success: Boolean,
        val status: Int,
        val message: String?,
        val data: AddToCartDataModel?
)

data class AddToCartDataModel(
        val id: String?,
        val items: ArrayList<AddToCartItemModel>?
)

data class AddToCartItemModel(
        val id: String?,
        val name: String?,
        val short_description: String?,
        val description: String?,
        val SKU: String?,
        val parent_id: String?,
        val regular_price: String?,
        val final_price: String?,
        val base_currency_id: Int?,
        val currency_code: String?,
        val remaining_quantity: Int?,
        val quantity: Int?,
        val is_featured: Int?,
        val image: String?,
        val configurable_option: ArrayList<AddToCartConfigurableOptionModel>?,
        val product_type: String?,
        val is_saleable: Int?
)

data class AddToCartConfigurableOptionModel(
        val type: String?,
        val attribute_id: String?,
        val attribute_code: String?,
        val attributes: ArrayList<AddToCartAttributeModel>?
)

data class AddToCartAttributeModel(
        val option_id: Int?,
        val value: String?
)
