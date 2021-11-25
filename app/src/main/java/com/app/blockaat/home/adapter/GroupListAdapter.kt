package com.app.blockaat.home.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.cart.model.AddCartRequestModel
import com.app.blockaat.helper.*
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.home.model.CollectionGroup
import com.app.blockaat.home.model.CollectionList
import com.app.blockaat.home.model.GroupListData
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.wishlist.modelclass.WishListResponseModel
import kotlinx.android.synthetic.main.item_home_group_list.view.*
import kotlinx.android.synthetic.main.item_home_group_list.view.imgProduct
import kotlinx.android.synthetic.main.item_home_product.view.*
import kotlinx.android.synthetic.main.item_home_product.view.ivDiscount
import kotlinx.android.synthetic.main.item_home_product.view.ivProduct
import kotlinx.android.synthetic.main.item_home_product.view.relDiscount
import kotlinx.android.synthetic.main.item_home_product.view.txtBrandName
import kotlinx.android.synthetic.main.item_home_product.view.txtDiscount
import kotlinx.android.synthetic.main.item_home_product.view.txtFinalPrice
import kotlinx.android.synthetic.main.item_home_product.view.txtName
import kotlinx.android.synthetic.main.item_home_product.view.txtRegularPrice
import kotlinx.android.synthetic.main.layout_product_detail.*
import okhttp3.internal.notify
import org.greenrobot.eventbus.EventBus

class GroupListAdapter(
    private val arrCollectionList: ArrayList<CollectionList>,
    private val collectionGroup: CollectionGroup,
    private val activity: Activity,
    private val homeItemClickInterface: HomeItemClickInterface,
    private val strCategoryType: String
) : RecyclerView.Adapter<GroupListAdapter.GroupListHolder>() {

    private var PRODUCT = 1
    private var ADD_CART = 2

    private var image_width: String? = null
    private var image_height: String? = null
    private var image_margin = 0
    private var margin_top = 0
    private var margin_bottom = 0
    private var hide_title = 0
    private var hide_collection_title = 0
    private var hide_collection_sub_title = 0
    private var deviceMultiplier = 0.0
    private var screenWidthForCategories = 0.0
    private var screenWidthForFC = 0.0
    private var screenHeightForFC = 0.0

    private var imageWidthForCategories = 0.0
    private var imageHeightForCategories = 0.0
    private var productsDBHelper: DBHelper

    init {
        productsDBHelper = DBHelper(activity)

        image_width = collectionGroup.image_width
        image_height = collectionGroup.image_height
        image_margin = collectionGroup.image_margin as Int
        margin_top = collectionGroup.margin_top as Int
        margin_bottom = collectionGroup.margin_bottom as Int
        hide_title = collectionGroup.hide_title as Int
        hide_collection_title = collectionGroup.hide_collection_title as Int
        hide_collection_sub_title = collectionGroup.hide_collection_sub_title as Int
        deviceMultiplier = Global.getDeviceWidth(activity) / 320.toDouble()
        screenWidthForCategories = (Global.getDeviceWidth(activity) - (20 * deviceMultiplier)) / 2
        imageWidthForCategories = screenWidthForCategories
        imageHeightForCategories = screenWidthForCategories
        screenWidthForFC = (Global.getDeviceWidth(activity) - (20 * deviceMultiplier)) / 2.5
        screenHeightForFC = screenWidthForFC
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): GroupListHolder {

        return when (getItemViewType(position)) {
            ADD_CART -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_home_product, parent, false)

                GroupListHolder(view)
            }

            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_home_group_list, parent, false)

                GroupListHolder(view)
            }
        }

    }

    override fun getItemCount(): Int {

        return arrCollectionList.size
    }

    override fun onBindViewHolder(holder: GroupListHolder, @SuppressLint("RecyclerView") position: Int) {
        val groupList = arrCollectionList[position]

        if (collectionGroup.group_type == "C") {
            holder.itemView.linMainHolder.layoutParams.width = screenWidthForCategories.toInt()
            holder.itemView.ivProduct.layoutParams.width = imageWidthForCategories.toInt()
            holder.itemView.ivProduct.layoutParams.height = imageHeightForCategories.toInt()
            Global.loadImagesUsingGlide(activity, groupList.image, holder.itemView.ivProduct)
            if (position == 0) {
                (holder.itemView.linMainHolder.layoutParams as RecyclerView.LayoutParams).marginStart =
                    ((deviceMultiplier * 5).toInt())
                (holder.itemView.linMainHolder.layoutParams as RecyclerView.LayoutParams).marginEnd =
                    ((deviceMultiplier * 5).toInt())
            } else {
                (holder.itemView.linMainHolder.layoutParams as RecyclerView.LayoutParams).marginEnd =
                    ((deviceMultiplier * 5).toInt())
            }
            if (!Global.isEnglishLanguage(activity)) {
                holder.itemView.ivDiscount.rotation = 180f
            }
            holder.itemView.txtName.typeface = Global.fontRegular
            holder.itemView.txtBrandName.typeface = Global.fontBold
            holder.itemView.txtFinalPrice.typeface = Global.fontPriceMedium
            holder.itemView.txtRegularPrice.typeface = Global.fontRegular
            holder.itemView.txtDiscount.typeface = Global.fontRegular
            holder.itemView.txtAddCart.typeface = Global.fontBtn
            holder.itemView.txtName.text = groupList.name
            holder.itemView.txtBrandName.text = groupList.brand_name
            holder.itemView.txtFinalPrice.text =
                Global.setPriceWithCurrency(activity, groupList.final_price.toString())
            if (groupList?.product_type.toString().toLowerCase() == "simple") {
                if (groupList?.is_saleable == 1) {
                    holder.itemView.txtAddCart.text =
                        activity.resources.getString(R.string.add_to_cart)
                } else {
                    holder.itemView.txtAddCart.text =
                        activity.resources.getString(R.string.notify_me)
                    //holder.itemView.txtSoldOut.visibility = View.VISIBLE
                }
            } else {
                holder.itemView.txtAddCart.text = activity.resources.getString(R.string.view_more)
            }
            holder.adapterPosition
            /*if (groupList.is_saleable == 1) {
                if (groupList.type == "Simple") {
                    holder.itemView.txtAddCart.text =
                        activity.resources.getString(R.string.add_to_cart)
                } else {
                    holder.itemView.txtAddCart.text =
                        activity.resources.getString(R.string.view_more)
                }
            } else {
                holder.itemView.txtAddCart.text = activity.resources.getString(R.string.sold_out)
            }*/
            if (groupList?.item_in_wishlist == 1) {
                holder.itemView.ivWishList.setImageResource(R.drawable.ic_wishlist_selected)
             //   holder.itemView.ivWishlist.setColorFilter(R.color.tab_select)

            } else {
                holder.itemView.ivWishList.setImageResource(R.drawable.ic_wishlist_unselected)
             //   holder.itemView.ivWishlist.setColorFilter(R.color.tab_select)

            }
//            holder.itemView.ivWishList.isChecked = groupList.item_in_wishlist == 1
//            holder.itemView.togWishlist.isChecked =
//                productsDBHelper.isProductPresentInWishlist(groupList.id.toString())

            if (groupList.final_price != groupList.regular_price) {
                holder.itemView.txtRegularPrice.visibility = View.VISIBLE
                holder.itemView.relDiscount.visibility = View.VISIBLE
                holder.itemView.txtDiscount.text = Global.getDiscountedPrice(
                    groupList.final_price.toString(),
                    groupList.regular_price.toString()
                ).toString() + "%"

                holder.itemView.txtRegularPrice.text =
                    Global.setOnlyPriceWithDecimal(activity, groupList.regular_price.toString())
                holder.itemView.txtRegularPrice.paintFlags =
                    holder.itemView.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.itemView.relDiscount.visibility = View.GONE
                holder.itemView.txtRegularPrice.visibility = View.GONE
            }
            holder.itemView.ivWishList.setOnClickListener {

                if (Global.getStringFromSharedPref(
                        activity,
                        Constants.PREFS_isUSER_LOGGED_IN
                    ) == "yes"
                ) {
                   val flag = groupList.item_in_wishlist==0

                    Global.addOrRemoveWishList(activity,groupList.id.toString(),productsDBHelper,flag,object:AddWishListInterface{
                        override fun onRemove(result: WishListResponseModel) {
                            homeItemClickInterface.onClickItem(position, "homeWishlist", "", "", "")
                        }

                        override fun onAdd(result: WishListResponseModel) {
                            homeItemClickInterface.onClickItem(position, "homeWishlist", "", "", "")
                        }

                    })
                } else {
                    val i = Intent(activity, LoginActivity::class.java)
                    i.putExtra("isFromProducts", "yes")
                    activity.startActivityForResult(i, 1)
                }
            }

            holder.itemView.txtAddCart.setOnClickListener {
                if (groupList?.product_type.toString().toLowerCase() == "simple") {
                    if (groupList.is_saleable == 1) {
                        val model = AddCartRequestModel(
                            groupList.id.toString(),
                            groupList.id.toString(),
                            groupList.name,
                            groupList.SKU,
                            groupList.regular_price,
                            groupList.final_price,
                            groupList.is_saleable,
                            groupList.brand_name,
                            groupList.image,
                            groupList.type,
                            groupList.brand_id,
                            groupList.influencer_id,
                            groupList.video_id
                        )

                        if (Global.getStringFromSharedPref(
                                activity,
                                Constants.PREFS_isUSER_LOGGED_IN
                            ) == "yes"
                        ) {
                            Global.addToCartOnline(activity, model)
                        } else {
                            Global.addToCartOffline(activity, model)
                        }

                    } else {
                        Global.notifyMe(activity, groupList?.id.toString())
                    }
                } else {
                    holder.itemView.linMainHolder.performClick()
                }
            }

            holder.itemView.linMainHolder.setOnClickListener {
                println("Category type details: "+strCategoryType)
                val intent = Intent(activity, ProductDetailsActivity::class.java)
                intent.putExtra("strProductID", groupList.id.toString())
                intent.putExtra("strProductName", groupList.name.toString())
                intent.putExtra("categoryID", strCategoryType)
                activity.startActivity(intent)
            }
        }


        when (collectionGroup.group_type) {
            "G" -> {
                if (image_width != null) {
                    holder.itemView.imgProduct.layoutParams.width =
                        ((deviceMultiplier * (image_width?.toInt() as Int)).toInt())
                    holder.itemView.mcMain.layoutParams.width =
                        ((deviceMultiplier * (image_width?.toInt() as Int)).toInt())
                }
                if (image_height != null) {
                    holder.itemView.imgProduct.layoutParams.height =
                        ((deviceMultiplier * (image_height?.toInt() as Int)).toInt())
                }

                if (position == 0) {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                        (deviceMultiplier * image_margin).toInt()
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        (deviceMultiplier * image_margin).toInt()

                } else {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        (deviceMultiplier * image_margin).toInt()
                }
                holder.itemView.txtTitle.typeface = Global.fontMedium
                holder.itemView.txtSubtitle.typeface = Global.fontRegular
                Global.loadImagesUsingGlide(activity, groupList.image, holder.itemView.imgProduct)


             //   println("Here i am HideTitle 111 "+groupList.title+" - "+hide_title)
              //  println("Here i am HideTitle 222 "+groupList.title+" - "+hide_collection_title)
              //  println("Here i am HideTitle 333 "+groupList.title+" - "+hide_collection_sub_title)

                holder.itemView.txtTitle.text = groupList.title

                holder.itemView.txtSubtitle.text = groupList.sub_title
                if (hide_title == 0) {
                    holder.itemView.txtTitle.visibility = View.VISIBLE
                } else {
                    holder.itemView.txtTitle.visibility = View.GONE
                }
                if (hide_collection_title == 0) {
                    holder.itemView.txtTitle.visibility = View.VISIBLE
                } else {
                    holder.itemView.txtTitle.visibility = View.GONE
                }
                if (hide_collection_sub_title == 0) {
                    holder.itemView.txtSubtitle.visibility = View.VISIBLE
                } else {
                    holder.itemView.txtTitle.visibility = View.GONE
                }
                holder.itemView.mcMain.setOnClickListener {
                    groupList.parentCAtType = strCategoryType
                    EventBus.getDefault().post(GroupListData(groupList, collectionGroup.group_type))

                    // Analytics event
                    if (Global.isUserLoggedIn(activity)){
                        CustomEvents.contentViewed(activity,Global.getUserId(activity),groupList.id,groupList.title)
                    }
                }


            }

            "EB" -> {
                holder.itemView.mcMain.layoutParams.width =
                    Global.getDimenVallue(activity, 100.0).toInt()
                holder.itemView.mcMain.layoutParams.height =
                    Global.getDimenVallue(activity, 100.0).toInt()


                if (position == 0) {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                        ((5 * deviceMultiplier).toInt())
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                } else {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                }

                holder.itemView.txtTitle.typeface = Global.fontRegular
                holder.itemView.txtSubtitle.typeface = Global.fontRegular

                Global.loadImagesUsingGlide(
                    activity,
                    groupList.image_name,
                    holder.itemView.imgProduct
                )
                holder.itemView.mcMain.setOnClickListener {
                    groupList.parentCAtType = strCategoryType
                    EventBus.getDefault().post(GroupListData(groupList, collectionGroup.group_type))

                    // Analytics event
                    if (Global.isUserLoggedIn(activity)){
                        CustomEvents.contentViewed(activity,Global.getUserId(activity),groupList.id,groupList.title)
                    }
                    /*val intent = Intent(activity, ProductListActivity::class.java)
                    intent.putExtra("brandID", groupList.id.toString())
                    intent.putExtra("header_text", groupList.name)
                    activity.startActivity(intent)*/
                }
            }

            "FB" -> {
//                holder.itemView.mcMain.layoutParams.width = screenWidthForFC.toInt()
//                holder.itemView.mcMain.layoutParams.height = screenHeightForFC.toInt()
                holder.itemView.mcMain.layoutParams.width =
                    Global.getDimenVallue(activity, 100.0).toInt()
                holder.itemView.mcMain.layoutParams.height =
                    Global.getDimenVallue(activity, 100.0).toInt()

//                holder.itemView.mcMain.strokeColor =
//                    ContextCompat.getColor(activity, R.color.divider_line_color)
//                holder.itemView.mcMain.strokeWidth =
//                    activity.resources.getDimension(R.dimen.divider_line_height).toInt()

                if (position == 0) {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                        ((5 * deviceMultiplier).toInt())
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                } else {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                }

                holder.itemView.txtTitle.typeface = Global.fontRegular
                holder.itemView.txtSubtitle.typeface = Global.fontRegular
                Global.loadImagesUsingGlide(
                    activity,
                    groupList.image_name,
                    holder.itemView.imgProduct
                )
                holder.itemView.mcMain.setOnClickListener {
                    groupList.parentCAtType = strCategoryType
                    EventBus.getDefault().post(GroupListData(groupList, collectionGroup.group_type))

                    // Analytics event
                    if (Global.isUserLoggedIn(activity)){
                        CustomEvents.contentViewed(activity,Global.getUserId(activity),groupList.id,groupList.title)
                    }

                }
            }

            "FC" -> {
                holder.itemView.mcMain.layoutParams.width =
                    Global.getDimenVallue(activity, 140.0).toInt()
                holder.itemView.mcMain.layoutParams.height =
                    Global.getDimenVallue(activity, 100.0).toInt()

                if (position == 0) {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                        ((5 * deviceMultiplier).toInt())
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                } else {
                    (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                        ((5 * deviceMultiplier).toInt())
                }

                holder.itemView.txtTitle.visibility = View.VISIBLE
                holder.itemView.txtSubtitle.visibility = View.GONE
                Global.loadImagesUsingGlide(
                    activity,
                    groupList.featured_image,
                    holder.itemView.imgProduct
                )
                holder.itemView.mcMain.setOnClickListener {
                    groupList.parentCAtType = strCategoryType
                     EventBus.getDefault().post(GroupListData(groupList, collectionGroup.group_type))

                    // Analytics event
                    if (Global.isUserLoggedIn(activity)){
                        CustomEvents.contentViewed(activity,Global.getUserId(activity),groupList.id,groupList.title)
                    }

                }
            }


        }


    }


    override fun getItemViewType(position: Int): Int {
        return if (collectionGroup.group_type == "C") {
            ADD_CART
        } else {
            PRODUCT
        }

    }

    class GroupListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}