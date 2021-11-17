package com.app.blockaat.address.model

import java.io.Serializable

data class AddressListingModel(
    val success: Boolean?,
    val status: Int?,
    val message: String?,
    val data: ArrayList<com.app.blockaat.address.model.AddressListingDataModel>?
) : Serializable

data class AddressListingDataModel(
    val address_id: String?,
    val address_name: String?,
    val area_id: String?,
    val area_name: String?,
    val governorate_id: String?,
    val governorate_name: String?,
    val country_id: String?,
    val country_name: String?,
    val vat_pct: String?,
    val vat_charges: String?,
    val block_id: String?,
    val block_name: String?,
    val shipping_cost: String?,
    val express_shipping_cost: String?,
    val cod_cost: String?,
    val is_cod_enable: Int?,
    val phonecode: String?,
    val street: String?,
    val jaddah: String?,
    val addressline_1: String?,
    val mobile_number: String?,
    val alt_phone_number: String?,
    val location_type: String?,
    val address_type: String?,
    val notes: String?,
    val flat_no: String?,
    val floor_no: String?,
    val is_default: String?,
    val zone: String?,
    val delivery_options: List<com.app.blockaat.address.model.DataDeliveryOption>?,
    var isSelected: Boolean,
    val building: String?

) : Serializable

data class DataDeliveryOption(
    val id: Int?,
    val name: String?,
    val price: String?,
    val days: String?
) : Serializable