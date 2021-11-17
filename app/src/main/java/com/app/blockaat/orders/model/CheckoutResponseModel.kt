package com.app.blockaat.orders.model

import java.io.Serializable

data class CheckoutResponseModel(
    val `data`: CheckoutDataModel? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) : Serializable

data class CheckoutDataModel(
    val baseCurrencyName: String? = null,
    val cart: CheckoutItemCartModel? = null,
    val cod_cost: String? = null,
    val delivery_charges: String? = null,
    val error_url: String? = null,
    val grand_total_kwd: String? = null,
    val flat_no: String? = null,
    val floor_no: String? = null,
    val block_name: String? = null,
    val order_details: CheckoutOrderDetails? = null,
    val payment_mode: String? = null,
    val payment_url: String? = null,
    val shipping_address: CheckoutShippingAddress? = null,
    val sub_total: String? = null,
    val success_url: String? = null,
    val total: String? = null,
    val vat_charges: String? = null,
    val vat_pct: String? = null,
    val is_coupon_applied: Int? = 0,
    val discount_price: String? = null,
    val coupon: Coupon? = null
) : Serializable

data class Coupon(
    val title: String? = null,
    val discount: String? = null,
    val code: String? = null
) : Serializable

data class CheckoutConfigurableOption(
    val attribute_code: String? = null,
    val attribute_id: String? = null,
    val attributes: List<CheckoutAttribute>? = null,
    val type: String? = null
) : Serializable

data class CheckoutAttribute(
    val color: String? = null,
    val option_id: Int? = null,
    val value: String? = null
) : Serializable


data class CheckoutOrderDetails(
    val order_date: String? = null,
    val order_id: Int? = null,
    val order_number: Int? = null,
    val order_status: String? = null,
    val status_color: String? = null
) : Serializable

data class CheckoutShippingAddress(
    val address_id: Int? = null,
    val address_name: String? = null,
    val area_name: String? = null,
    val building: String? = null,
    val cod_cost: Double? = null,
    val country_name: String? = null,
    val flat_no: String? = null,
    val floor_no: String? = null,
    val block_name: String? = null,
    val governorate_name: String? = null,
    val is_cod_enable: Int? = null,
    val mobile_number: String? = null,
    val notes: String? = null,
    val phonecode: String? = null,
    val shipping_cost: Double? = null,
    val street: String? = null,
    val jaddah: String? = null,
    val zone: String? = null
) : Serializable
