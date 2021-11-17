package com.app.blockaat.address.model

import java.io.Serializable

data class EditAddressResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val data: com.app.blockaat.address.model.Data
)

data class Data(
    val address_id: Int,
    val first_name: String,
    val last_name: String,
    val area_id: String,
    val area_name: String,
    val governorate_id: String,
    val governorate_name: String,
    val country_id: String,
    val country_name: String,
    val vat_pct: String,
    val vat_charges: String,
    val block_id: String,
    val block_name: String,
    val street: String,
    val addressline_1: String,
    val mobile_number: String,
    val alt_phone_number: String,
    val location_type: String,
    val notes: String,
    val is_default: String,
    val shipping_cost: String,
    val cod_cost: String,
    val is_cod_enable: Int,
    val phonecode: String,
    val delivery_options: ArrayList<com.app.blockaat.address.model.DeliveryOptionEdtiAddress>
) : Serializable

data class DeliveryOptionEdtiAddress(
    val id: Int,
    val name: String,
    val price: String,
    val days: String
) : Serializable