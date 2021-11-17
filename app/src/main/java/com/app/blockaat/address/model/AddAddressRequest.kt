package com.app.blockaat.address.model

data class AddAddressRequest(
    val user_id: String? = null,
    val order_id: String? = null,
    val address_name: String? = null,
    val country_id: String? = "0",
    val state_id: String? = "0",
    val area_id: String? = "0",
    val area_name: String? = "0",
    val block_id: String? = "0",
    val block_name: String? = "0",
    val mobile_number: String? = null,
    val street: String? = null,
    val jaddah: String? = null,
    val building: String? = null,
    val floor_no: String? = null,
    val flat_no: String? = null,
    val notes: String? = null,
    val address_type: String? = null,
    val is_default: String? = null
)