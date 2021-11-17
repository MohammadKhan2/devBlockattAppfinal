package com.app.blockaat.productdetails.model

data class ConfigurationOptionRequestModel(
    val parent_product_id: String,

    val attribute_id: String,
    val option_id: String
)