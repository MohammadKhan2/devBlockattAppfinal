package com.app.blockaat.orders.model

import com.app.blockaat.address.model.AddressListingDataModel
import java.io.Serializable

data class CheckoutItemResponseModel(
    val `data`: CheckoutItemDataModel? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) : Serializable

data class CheckoutItemDataModel(
    val baseCurrencyName: String? = null,
    val cart: CheckoutItemCartModel? = null,
    var cod_cost: String? = null,
    var default_address: AddressListingDataModel? = null,
    var delivery_charges: String? = null,
    val delivery_options: List<Any>? = null,
    val payment_types: List<CheckoutItemPaymentTypeModel>? = null,
    val shipping_note: String? = null,
    val sub_total: String? = null,
    val total: String? = null,
    val total_addresses: String? = null,
    var vat_charges: String? = null,
    var vat_pct: String? = null,
    val is_coupon_applied: Int? = 0,
    val discount_price: String? = null,
    val coupon: Coupon? = null
) : Serializable

data class CheckoutItemCartModel(
    val id: String? = null,
    val items: ArrayList<CheckoutItemItemModel>? = null
) : Serializable

data class CheckoutItemItemModel(
    val SKU: String? = null,
    val base_currency_id: Int? = null,
    val brand_name: String? = null,
    val celeb_id: String? = null,
    val configurable_option: List<CheckoutItemConfigurableOptionModel>? = null,
    val currency_code: String? = null,
    val description: String? = null,
    val final_price: String? = null,
    val final_price_kw: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val is_featured: Int? = null,
    val is_saleable: Int? = null,
    val name: String? = null,
    val order_item_id: String? = null,
    val parent_id: String? = null,
    val product_type: String? = null,
    val quantity: Int? = null,
    val regular_price: String? = null,
    val regular_price_kw: String? = null,
    val remaining_quantity: Int? = null,
    val short_description: String? = null,
    val video_id: String? = null
) : Serializable

data class CheckoutItemConfigurableOptionModel(
    val attribute_code: String? = null,
    val attribute_id: String? = null,
    val attributes: ArrayList<CheckoutItemAttributeModel>? = null,
    val type: String? = null
) : Serializable

data class CheckoutItemAttributeModel(
    val color: String? = null,
    val option_id: Int? = null,
    val value: String? = null
) : Serializable


data class CheckoutItemPaymentTypeModel(
    val code: String? = null,
    val fail_url: String? = null,
    val icon: String? = null,
    val is_enable: Int? = null,
    val success_url: String? = null,
    val type: String? = null,
    var isSelected: Boolean? = false
) : Serializable

