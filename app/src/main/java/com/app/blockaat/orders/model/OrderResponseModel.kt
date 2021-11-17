package com.app.blockaat.orders.model

import java.io.Serializable

data class OrderResponseModel(
        val success: Boolean?,
        val status: Int?,
        val message: String?,
        val data: ArrayList<MyOrderDataModel>?
) : Serializable

data class MyOrderDataModel(
        val id: Int?,
        val order_number: Int?,
        val recipient_name: String?,
        val recipient_phone: String?,
        val created_date: String?,
        val payment_mode: String?,
        val sub_total: String?,
        val total: String?,
        val cod_cost: String?,
        val delivery_charges: String?,
        val vat_pct: String?,
        val vat_charges: String?,
        val baseCurrencyName: String?,
        val items: ArrayList<MyOrderItemModel>?,
        val status_id: Int?,
        val status: String?,
        val status_color: String?,
        val is_return_active: Int?,
        val is_cancel_active: Int?,
        val shipping_address: MyOrderShippingAddressModel?
) : Serializable

data class MyOrderItemModel(
        val id: Int?,
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
        val configurable_option: ArrayList<MyOrderConfigurableOptionModel>?,
        val product_type: String?,
        val is_saleable: Int?,
        val brand_name: String?
) : Serializable

data class MyOrderConfigurableOptionModel(
        val type: String?,
        val attribute_id: String?,
        val attribute_code: String?,
        val attributes: ArrayList<MyOrderAttributeModel>?
) : Serializable

data class MyOrderAttributeModel(
        val option_id: Int?,
        val value: String?
) : Serializable

data class MyOrderShippingAddressModel(
        val first_name: String?,
        val last_name: String?,
        val area_name: String?,
        val governorate_name: String?,
        val country_name: String?,
        val phonecode: String?,
        val block_name: String?,
        val street: String?,
        val addressline_1: String?,
        val mobile_number: String?,
        val alt_phone_number: String?,
        val location_type: String?,
        val notes: String?
) : Serializable