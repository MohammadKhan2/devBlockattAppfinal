package com.app.blockaat.address.model

import java.io.Serializable


data class AddAddressModel(
    val success: Boolean?,
    val status: Int?,
    val message: String?,
    val data: com.app.blockaat.address.model.AddressListingDataModel?
):Serializable
