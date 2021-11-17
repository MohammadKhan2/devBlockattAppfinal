package com.app.blockaat.productdetails.model

data class ConfigurationOptionResponseModel(
    val success: Boolean,
    val status: Int,
    val message: String,
    val `data`: ArrayList<ConfigurationOptionDataModel?>? = null
)

data class ConfigurationOptionDataModel(
    val type: String?,
    val attribute_id: String?,
    val entity_id: String?,
    val attributes: ArrayList<ConfigurationOptionAttributeModel>?,
    val regular_price: String?,
    val sku: String?,

    val image_url: String? = null,
    var remaining_quantity: Int?,
    val final_price: String?,
    val images: ArrayList<String>?,
    var isSizeAvailable: Boolean = false



)

data class ConfigurationOptionAttributeModel(
    val option_id: String?,
    val value: String?,
    val image_url: String?,
    var isEnabled: Boolean = false
)
