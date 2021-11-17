package com.app.blockaat.home.model

data class RootCategoriesModel(
    val `data`: List<RootCategoriesData?>? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
)
    data class RootCategoriesData(
        val icon: String? = null,
        val id: Int? = null,
        val image: String? = null,
        val meta_description: Any? = null,
        val meta_title: Any? = null,
        val name: String? = null,
        val show_in_website_menu: Int? = null
    )
