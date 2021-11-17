package com.app.blockaat.wishlist.adapter

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.wishlist.interfaces.WishlistInterface
import com.app.blockaat.wishlist.modelclass.WishListDataModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.fragment_contact_us.*
import kotlinx.android.synthetic.main.lv_item_wishlist.view.*
import kotlinx.android.synthetic.main.lv_item_wishlist.view.ivProduct
import kotlinx.android.synthetic.main.lv_item_wishlist.view.txtBrandName
import kotlinx.android.synthetic.main.lv_item_wishlist.view.txtFinalPrice
import kotlinx.android.synthetic.main.lv_item_wishlist.view.txtName

class WishlistAdapter(
    private val activity: AppCompatActivity,
    private val arrListWishListData: ArrayList<WishListDataModel>,
    private var wishlistInterface: WishlistInterface,
    private val productsDBHelper: DBHelper,
    private val isFromFragment: Boolean

) : RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(
            activity,
            arrListWishListData,
            position,
            wishlistInterface,
            productsDBHelper
        )

    override fun getItemCount(): Int = arrListWishListData.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_wishlist, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(
            activity: AppCompatActivity,
            arrListWishListData: ArrayList<WishListDataModel>,
            position: Int,
            wishlistInterface: WishlistInterface,
            productsDBHelper: DBHelper
        ) {

            //set data

        /*    val lpMain = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )*/

           /*
           sefali
           if (!Global.isEnglishLanguage(activity)) {
                itemView.ivDiscount.rotation = 180f
            }*/
        /*    if (position % 2 == 0) {
                lpMain.marginEnd = Global.getDimenVallue(activity, 8.0).toInt()
            }
            lpMain.topMargin = Global.getDimenVallue(activity, 8.0).toInt()
            itemView.linMainHolder.layoutParams = lpMain

            val lpImage = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Global.getDimenVallue(activity as Activity, 153.00).toInt()
            )
            itemView.ivProduct.layoutParams = lpImage*/
            var width = 0.0
            var height = 0.0
            var screenWidth = 0.0
            var deviceMultiplier = 0.0
            if(position> -1) {
                val a = arrListWishListData[position]
                deviceMultiplier =
                    Global.getDeviceWidth(activity) / java.lang.Double.valueOf(320.0)

                screenWidth =
                    Global.getDeviceWidth(activity) - 30 * deviceMultiplier
                width = screenWidth / 2
                height = screenWidth / 2
                if (activity != null) {
                    Glide.with(activity)
                        .load(a?.image)
                        .override(width.toInt(), height.toInt())
                        .listener(object : RequestListener<Drawable?> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any,
                                target: Target<Drawable?>,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any,
                                target: Target<Drawable?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .transition(DrawableTransitionOptions.withCrossFade(800))
                        .into(itemView.ivProduct)
                }



                itemView.txtName.text = a?.name
                itemView.txtBrandName.text = a?.brand_name
//        (itemView.linMainHolder.layoutParams as RelativeLayout.LayoutParams).marginEnd =
//            activity.resources.getDimension(R.dimen.ten_dp).toInt()
             /*   if (activity != null) {
                    Global.loadImagesUsingGlide(activity, a?.image, itemView.ivProduct)
                }
*/
                if (!Global.isEnglishLanguage(activity)) {
                    itemView.txtFinalPrice.gravity = View.TEXT_ALIGNMENT_TEXT_START
                }
                //discount and prices
                itemView.txtFinalPrice.text = Global.setPriceWithCurrency(
                    activity as Activity,
                    a?.final_price ?: ""
                )

                /*
            sefali
            itemView.txtRegularPrice.paintFlags =
                itemView.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            if (a?.final_price != a?.regular_price) {
                itemView.txtRegularPrice.text = Global.setOnlyPrice(
                    activity,
                    a?.regular_price ?: ""
                )
                itemView.txtRegularPrice.visibility = View.VISIBLE
                itemView.relDiscount.visibility = View.VISIBLE
                itemView.txtDiscount.text = Global.getDiscountedPrice(
                    a?.final_price ?: "",
                    a?.regular_price ?: ""
                ).toString() + "%"
            } else {
                itemView.txtRegularPrice.visibility = View.GONE
                itemView.relDiscount.visibility = View.INVISIBLE
            }

            //set button text
            if (a?.product_type.toString().toLowerCase() == "simple") {
                if (a?.is_saleable == 1) {
                    itemView.txtAddCart.text = activity.resources.getString(R.string.add_to_cart)
                } else {
                    itemView.txtAddCart.text = activity.resources.getString(R.string.notify_me)
                }
            } else {
                itemView.txtAddCart.text = activity.resources.getString(R.string.view_more)
            }*/

                //set fonts
                itemView.txtBrandName.typeface = Global.fontNavBar
                itemView.txtName.typeface = Global.fontRegular
                /*  itemView.txtAddCart.typeface = Global.fontSemiBold*/
                itemView.txtFinalPrice.typeface = Global.fontNavBar
                /* itemView.txtRegularPrice.typeface = Global.fontPriceRegular*/

                //set click listener
                itemView.linMainHolder.setOnClickListener {
                    wishlistInterface.onItemClick(position, "openProduct")
                }
                if(a?.is_saleable==0){
                    itemView.txtSoldOut.visibility =View.VISIBLE
                }else{
                    itemView.txtSoldOut.visibility = View.GONE
                }



                itemView.txtAddCart.setOnClickListener {
                    if (a?.product_type.toString().toLowerCase() == "simple") {
                        if (a?.is_saleable == 1) {
                            wishlistInterface.onItemClick(position, "addToCart")
                        } else {
                            wishlistInterface.onItemClick(position, "notifyMe")
                        }
                    } else {
                        wishlistInterface.onItemClick(position, "openProduct")
                    }
                }
                itemView.ivDelete.setOnClickListener {
                    wishlistInterface.onItemClick(position, "deleteItem")
                    /*  Global.showAlert(
                      activity,
                      "",
                      activity.resources.getString(R.string.delete_item_wishlist_alert),
                      activity.resources.getString(R.string.yes),
                      activity.resources.getString(R.string.no),
                      false,
                      R.drawable.ic_alert,
                      object : AlertDialogInterface {
                          override fun onYesClick() {
                              if (productsDBHelper.isProductPresentInWishlist(e.id.toString())) {
                                  productsDBHelper.deleteProductFromWishlist(e.id.toString())
                              }

                          }

                          override fun onNoClick() {
                          }

                      })*/
                }

/*
            itemView.ivBag.setOnClickListener {
                addToCartOnline(activity, productsDBHelper, arrListWishListData[position].id!!, arrListWishListData[position].name!!, arrListWishListData[position].brand_name!!, arrListWishListData[position].image!!,
                    arrListWishListData[position].description!!, arrListWishListData[position].final_price!!, arrListWishListData[position].regular_price!!, arrListWishListData[position].SKU!!, arrListWishListData[position].remaining_quantity.toString(),
                    arrListWishListData[position].is_featured.toString(), arrListWishListData[position].is_saleable.toString(), arrListWishListData[position].product_type!!, isFromFragment)
            }
*/

            }
        }
/*

        @SuppressLint("CheckResult")
        private fun deleteFromWishlist(
            e: WishListDataModel,
            onWishlistUpdateClicked: UpdateWishlistItemEvent
        ) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                showProgressDialog(activity)
                Global.apiService.deleteWishlist(
                    Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                    e.id.toString(),
                    WebServices.DeleteWishlistItemWs + Global.getStringFromSharedPref(
                        activity as AppCompatActivity,
                        Constants.PREFS_LANGUAGE
                    )
                            + "&store=" + Global.getStringFromSharedPref(
                        activity,
                        Constants.PREFS_STORE_CODE
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            println("RESPONSE - DELETE wishlist Ws :   " + Gson().toJson(result))
                            if (result != null) {
                                hideProgressDialog()
                                if (result.status == 200) {
                                    if (result.data != null) {
                                        onWishlistUpdateClicked.onWishlistUpdateClicked(
                                            result.data,
                                            "deleteWishlistItem"
                                        )
                                    }
                                } else {
                                    Global.showSnackbar(activity, result.message)
                                }
                            } else {
                                //if ws not working
                                Global.showSnackbar(
                                    activity,
                                    activity.resources.getString(R.string.error)
                                )
                            }
                        },
                        { error ->
                            println("ERROR - DELETE wishlist Ws :   " + error.localizedMessage)
                            hideProgressDialog()
                            Global.showSnackbar(
                                activity,
                                activity.resources.getString(R.string.error)
                            )
                        }
                    )
            }
        }

*/

/*
        private fun addToCartOnline(activity: Activity, productsDBHelper: DBHelper, strProductID: String, strProductName: String, strBrandName: String, strImage: String, strDescription: String, strFinalPrice: String, strRegularPrice: String,
                                    strSku: String, strRemainingQty: String, strIsFeatured: String, strIsSaleable: String, strProductType: String, isFromFragment: Boolean) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                showProgressDialog(activity)
                val strEntityID = "5"
                Global.apiService.addToCart(Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID), strProductID, "1", WebServices.AddToCartWs + Global.getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE)
                        + "&store=" + Global.getStringFromSharedPref(activity, Constants.PREFS_STORE_CODE))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            println("RESPONSE - AddToCart Ws :   " + Gson().toJson(result))
                            hideProgressDialog()
                            if (result != null) {
                                if (result.status == 200) {
                                    if (!isFromFragment) {
                                        val dialogView = LayoutInflater.from(activity).inflate(R.layout.alert_dialog_custom, null)
                                        dialogView.txtAlertTitle.text = activity.resources.getString(R.string.great_choice)
                                        dialogView.txtAlertMessage.text = activity.resources.getString(R.string.the_item_is_now_in_your_bag)
                                        dialogView.txtPositive.text = activity.resources.getString(R.string.continue_shopping)
                                        dialogView.txtNegative.text = activity.resources.getString(R.string.view_cart)
                                        dialogView.txtAlertTitle.typeface = Global.fontSemiBold
                                        dialogView.txtAlertMessage.typeface = Global.fontRegular
                                        dialogView.txtPositive.typeface = Global.fontRegular
                                        dialogView.txtNegative.typeface = Global.fontSemiBold

                                        val builder = android.support.v7.app.AlertDialog.Builder(activity)
                                            .setView(dialogView)
                                        val dialog = builder.show()
                                        dialog.setCancelable(false)
                                        dialogView.txtPositive.setOnClickListener {
                                            dialog.dismiss()
                                            activity.finish()
                                        }

                                        dialogView.txtNegative.setOnClickListener {
                                            dialog.dismiss()
                                            val i = Intent(activity, MainActivity::class.java)
                                            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            i.putExtra("fromProductDetailToCart", "yes")
                                            activity.startActivity(i)
                                        }


                                        if (!productsDBHelper.isProductPresentInCart(strProductID, strEntityID)) {
                                            //once added in ws update same in DB
                                            productsDBHelper.addProductToCart(ProductsDataModel(strProductID, strEntityID, strBrandName, strBrandName, strImage, strDescription,
                                                "1", strFinalPrice, strRegularPrice, strSku, strRemainingQty, strIsFeatured, strIsSaleable
                                                , strProductType, "", ""))
                                            println("HERE I M  :::  ADD - ONLINE :::    " + " : " + strProductID + "   :   " + strEntityID)
                                        } else {
                                            var qty: Int = productsDBHelper.getQtyInCart(strProductID, strEntityID).toInt()
                                            qty += 1
                                            productsDBHelper.updateProductsInCart(qty.toString(), strProductID, strEntityID)
                                            println("HERE I M  :::  UPDATE - ONLINE :::    " + " : " + strProductID + "   :   " + strEntityID)
                                        }
                                    }
                                    //after adding data in cart updating cart badge
                                    //updateCartBadge()
                                } else {
                                    Global.showSnackbar(activity, result.message.toString())
                                }
                            } else {
                                //if ws not working
                                hideProgressDialog()
                                Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                            }
                        },
                        { error ->
                            hideProgressDialog()
                            println("ERROR - AddToCart Ws :   " + error.localizedMessage)
                            Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                        }
                    )
            }
        }
*/

    }

}
