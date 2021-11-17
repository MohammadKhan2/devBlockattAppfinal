package com.app.blockaat.home.model

import java.io.Serializable

data class HomeLinkModel(
    val link_id: String,
    val link_type: String,
    val type_name: String,
    val url: String,
    val categoryId: String
) : Serializable