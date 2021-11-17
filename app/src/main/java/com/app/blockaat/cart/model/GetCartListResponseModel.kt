package com.app.blockaat.cart.model


data class GetCartListResponseModel(
        val success: Boolean,
        val status: Int,
        val message: String,
        val data: GetCartListDataModel?
)

data class GetCartListDataModel(
        val id: String?,
        val items: ArrayList<GetCartListItemModel>?,
        val vat_pct: Int?,
        val vat_charges: String?,
        val total: String?,
        val delivery_charge: String?,
        val sub_total: String?,
        val shipping_cost: String?
)

data class GetCartListItemModel(
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
        val configurable_option: ArrayList<GetCartListConfigurableOptionModel>?,
        val product_type: String?,
        val is_saleable: Int?,
        val brand_name: String?,
        val order_id: String?,
        val video_id: String?,
        val celeb_id: String?,
        val order_item_id: String?
)

data class GetCartListConfigurableOptionModel(
        val type: String?,
        val attribute_id: String?,
        val attribute_code: String?,
        val name: String?,
        val attributes: ArrayList<GetCartListAttributeModel>?
)

data class GetCartListAttributeModel(
        val option_id: Int?,
        val value: String?
)