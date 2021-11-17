package com.app.blockaat.home.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

data class HomeResponseModel(
    val `data`: Data? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
)

data class Data(
    val banners: List<Banner>? = null,
    val influencers: ArrayList<Influencer?>? = null,
    val influencers_of_the_week: ArrayList<Influencer?>? = null,
    val notify_text: String? = null,
    val our_picks: ArrayList<OurPick?>? = null,
    val new_arrivals: ArrayList<Arrivals?>? = null,
    val best_seller: ArrayList<BestSellers?>? = null,
    val brands: ArrayList<Brands?>? = null,
    val videos: ArrayList<Videos>? = null,
    val collectionGroups: ArrayList<CollectionGroup?>? = null,
    val support_email: String? = null,
    val support_phone: String? = null
)
@Parcelize
data class CollectionGroup(
    val id: Int? = null,
    val title: String? = null,
    val image_width: String? = null,
    val image_height: String? = null,
    val image_margin: Int? = null,
    val margin_top: Int? = null,
    val margin_bottom: Int? = null,
    val image: String? = null,
    val hide_title: Int? = null,
    val hide_collection_title: Int? = null,
    val hide_collection_sub_title: Int? = null,
    val group_type: String? = null,
    val group_type_id: String? = null,
    var category_id: String? = null,
    val collection_list: ArrayList<CollectionList>? = null
): Serializable, Parcelable
@Parcelize
data class CollectionList(
    var id: Int? = null,
    var title: String? = null,
    val sub_title: String? = null,
    val image: String? = null,
    val type: String? = null,
    var type_id: String? = null,
    var link: String? = null,
    val short_description: String? = null,
    val name: String? = null,
    val image_name: String? = null,
    val featured_image: String? = null,
    val banner: String? = null,
    val website_brand_image: String? = null,
    val description: String? = null,
    val specification: String? = null,
    val shipping_free_returns: String? = null,
    val SKU: String? = null,
    val regular_price: String? = null,
    val final_price: String? = null,
    val currency_code: String? = null,
    val remaining_quantity: Int? = null,
    val is_featured: Int? = null,
    val new_from_date: String? = null,
    val new_to_date: String? = null,
    val brand_id: Int? = null,
    val brand_name: String? = null,
    val brand_banner_image: String? = null,
    val images: ArrayList<String>? = null,
    val video: String? = null,
    val product_type: String? = null,
    val item_in_cart: Int? = null,
    var item_in_wishlist: Int? = null,
    val influencer_id: Int? = null,
    val product_code: String? = null,
    val is_saleable: Int? = 0,
    val video_id: Int? = null,
    var parentCAtType: String? = null
): Serializable, Parcelable

data class BestSellers(
    val currency_code: String? = null,
    val final_price: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val is_saleable: Int? = null,
    val item_in_cart: Int? = null,
    var item_in_wishlist: Int? = null,
    val name: String? = null,
    val regular_price: String? = null,
    val SKU: String? = null,
    val type: String? = null,
    val brand_name: String? = null,
    val brand_id: Int? = null,
    val influencer_id: Int? = null,
    val video_id: Int? = null
)

data class Videos(
    val id: Int? = null,
    val image: String? = null,
    val name: String? = null,
    val youtube_video_id: String? = null
)

data class Arrivals(
    val brand: String? = null,
    val currency_code: String? = null,
    val final_price: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val is_saleable: Int? = null,
    val item_in_cart: Int? = null,
    var item_in_wishlist: Int? = null,
    val name: String? = null,
    val regular_price: String? = null,
    val SKU: String? = null,
    val type: String? = null,
    val brand_name: String? = null
)

data class Influencer(
    val id: Int? = null,
    val image: String? = null,
    val title: String? = null,
    val banner: String? = null,
    var categoryId: String? = null


    )

data class OurPick(
    val brand: String? = null,
    val currency_code: String? = null,
    val final_price: String? = null,
    val id: Int? = null,
    val image: String? = null,
    val is_saleable: Int? = null,
    val item_in_cart: Int? = null,
    var item_in_wishlist: Int? = null,
    val name: String? = null,
    val regular_price: String? = null,
    val SKU: String? = null,
    val type: String? = null,
    val brand_name: String? = null
)

data class Brands(
    val banner: String? = null,
    val id: String? = null,
    val image_name: String? = null,
    val name: String? = null,
    val website_brand_image: String? = null
)

data class Banner(
    val id: Int? = null,
    val name: String? = null,
    val sub_title: String? = null,
    val link_type: String? = null,
    val link_id: String? = null,
    val url: String? = null,
    val position: String? = null,
    val image: String? = null,
    val categoryID: String? = null
)