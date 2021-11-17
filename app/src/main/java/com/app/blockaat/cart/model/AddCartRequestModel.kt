package com.app.blockaat.cart.model

import java.io.Serializable

data class AddCartRequestModel(
    var id: String?= null,
    var entity_id: String?= null,
    var name: String?= null,
    var SKU: String?= null,
    var regular_price: String?= null,
    var final_price: String?= null,
    var is_saleable:Int?=null,
    var brand: String?= null,
    var image: String?= null,
    var product_type: String?= null,
    var brand_id: Int?= 0,
    var influencer_id: Int?= 0,
    var video_id: Int?= 0,
    var brand_name: String?= null

    ): Serializable

data class AddCartRequestApi(
    val user_id: String?= "",
    val products: String?= "",
    val quantity: String?= "",
    val celebs: String?= "",
    val videos: String?= ""
): Serializable