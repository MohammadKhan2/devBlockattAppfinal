package com.app.blockaat.cart.adapter

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.cart.CartActivity
import com.app.blockaat.cart.model.DeleteCartRequest
import com.app.blockaat.cart.model.GetCartListItemModel
import com.app.blockaat.cart.model.UpdateCartRequest
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.lv_item_cart.view.*
import java.util.ArrayList

class OnlineCartDataAdapter(
    private val arrListCartList: ArrayList<GetCartListItemModel>,
    private val strOrderID: String,
    private var onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
    private val productsDBHelper: DBHelper
) : RecyclerView.Adapter<OnlineCartDataAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(arrListCartList, position, strOrderID, onCartUpdateClicked, productsDBHelper)


    override fun getItemCount(): Int = arrListCartList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_cart, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bind(
            arrListCartList: ArrayList<GetCartListItemModel>,
            position: Int,
            strOrderID: String,
            onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
            productsDBHelper: DBHelper
        ) {
            val a = arrListCartList[position]
            //set data
            itemView.txtProductName.text = a.name.toString()
            itemView.txtFinalPrice.text =
                Global.setPriceWithCurrency(activity as AppCompatActivity, a.final_price.toString())
            //itemView.txtRegularPrice.text = Global.setPriceWithCurrency(activity, a.regular_price.toString())
            Global.loadImagesUsingGlide(activity, a.image, itemView.ivProduct)

            try {
                if (!a?.configurable_option.isNullOrEmpty()) {
                    itemView.linAttribute.visibility = View.VISIBLE
                    if (a?.configurable_option?.size == 1) {
                        itemView.linAttributeOne.visibility = View.VISIBLE
                        itemView.linAttributeTwo.visibility = View.GONE
                        if (!a?.configurable_option[0]?.type.isNullOrEmpty()) {
                            itemView.txtAttr1.text =  a?.configurable_option?.get(0)?.type  + ":"
                            itemView.txtAttr1Value.text =
                                a?.configurable_option?.get(0)?.attributes?.get(0)?.value
                        }
                    } else {
                        itemView.linAttributeOne.visibility = View.VISIBLE
                        itemView.linAttributeTwo.visibility = View.VISIBLE
                        itemView.txtAttr1.text =
                            a?.configurable_option?.get(0)?.type + ":"
                        itemView.txtAttr2.text =
                            a?.configurable_option?.get(1)?.type + ":"
                        itemView.txtAttr1Value.text =
                            a?.configurable_option?.get(0)?.attributes?.get(0)?.value
                        itemView.txtAttr2Value.text =
                            a?.configurable_option?.get(1)?.attributes?.get(0)?.value
                    }
                } else {
                    itemView.linAttribute.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
            }



            itemView.txtQty.text = a.quantity.toString()
            if (a.quantity == 1) {
                itemView.ivMinus.setColorFilter(activity.resources.getColor(R.color.icon_color))
            } else {
                itemView.ivMinus.setColorFilter(activity.resources.getColor(R.color.primary_color))
            }


            //set fonts
            itemView.txtProductName.typeface = Global.fontRegular
            itemView.txtFinalPrice.typeface = Global.fontMedium
            itemView.txtAttr1.typeface = Global.fontRegular
            itemView.txtAttr2.typeface = Global.fontRegular
            itemView.txtAttr1Value.typeface = Global.fontMedium
            itemView.txtRemove.typeface = Global.fontMedium
            itemView.txtAttr2Value.typeface = Global.fontMedium
            itemView.txtMoveToWishList.typeface = Global.fontMedium
            itemView.txtQty.typeface = Global.fontMedium


            //set click listeners

            itemView.ivPlus.setOnClickListener {
                if ((a?.remaining_quantity ?: 0) > (itemView.txtQty.text.toString().toInt())) {
                    val x = itemView.txtQty.text.toString().toInt() + 1
                    itemView.txtQty.text = x.toString()
                    updateCartItem(a, strOrderID, x, onCartUpdateClicked, productsDBHelper)
                } else {
                    //no more qty available
                    //println("Remaining qty is reached")
                }

            }

            itemView.ivMinus.setOnClickListener {
                if (itemView.txtQty.text.toString().toInt() > 1) {
                    val x = itemView.txtQty.text.toString().toInt() - 1
                    itemView.txtQty.text = x.toString()
                    updateCartItem(a, strOrderID, x, onCartUpdateClicked, productsDBHelper)
                }
            }

            itemView.linRemove.setOnClickListener {
                Global.showAlert(
                    activity,
                    "",
                    activity.resources.getString(R.string.delete_item_desc),
                    activity.resources.getString(R.string.yes),
                    activity.resources.getString(R.string.no),
                    false,
                    R.drawable.ic_alert,
                    object : AlertDialogInterface {
                        override fun onYesClick() {
                            deleteCartItem(a, strOrderID, onCartUpdateClicked, productsDBHelper)
                        }

                        override fun onNoClick() {
                        }

                    })
            }
            itemView.linWishlist.setOnClickListener {
                moveToWishlist(a, strOrderID, onCartUpdateClicked, productsDBHelper)
/*
                Global.showAlert(
                    activity,
                    "",
                    activity.resources.getString(R.string.move_to_wishlist_desc),
                    activity.resources.getString(R.string.move_to_wishlist),
                    activity.resources.getString(R.string.no_thanks),
                    false,
                    R.drawable.ic_alert,
                    object : AlertDialogInterface {
                        override fun onYesClick() {
                            moveToWishlist(a, strOrderID, onCartUpdateClicked, productsDBHelper)
                        }

                        override fun onNoClick() {
                        }

                    })
*/

            }
        }

        @SuppressLint("CheckResult")
        private fun moveToWishlist(
            a: GetCartListItemModel,
            strOrderID: String,
            onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
            productsDBHelper: DBHelper
        ) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                showProgressDialog(activity as Activity)
                Global.apiService.moveToWishList(
                    Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                    strOrderID,
                    a.id.toString(),
                    com.app.blockaat.apimanager.WebServices.MoveToWishlistWs + Global.getStringFromSharedPref(
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
                            hideProgressDialog()
                          //  println("RESPONSE - Move to Wishlist Ws :   " + Gson().toJson(result))
                            if (result != null) {
                                if (result.status == 200) {
                                    if (result.data != null) {
                                        productsDBHelper.deleteProductFromCart(
                                            a.id.toString(),
                                            a.id.toString()
                                        )
                                        onCartUpdateClicked.onCartUpdateClicked(
                                            result.data,
                                            "online"
                                        )
                                        // azim
                                        if (!productsDBHelper.isProductPresentInWishlist(a.id)){
                                          productsDBHelper.addProductToWishlist(ProductsDataModel(a.id))
                                        }
                                        (activity as CartActivity).updateCounts()
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
                            hideProgressDialog()
                          //  println("ERROR - Move to Wishlist Ws :   " + error.localizedMessage)
                            Global.showSnackbar(
                                activity,
                                activity.resources.getString(R.string.error)
                            )
                        }
                    )
            }
        }

        @SuppressLint("CheckResult")
        private fun deleteCartItem(
            a: GetCartListItemModel,
            strOrderID: String,
            onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
            productsDBHelper: DBHelper
        ) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                showProgressDialog(activity as Activity)
                val deleteCart = DeleteCartRequest(
                    Global.getStringFromSharedPref(
                        activity,
                        Constants.PREFS_USER_ID
                    ), strOrderID, a.order_item_id
                )
                Global.apiService.deleteCartItem(
                    deleteCart,
                    com.app.blockaat.apimanager.WebServices.DeleteCartItemWs + Global.getStringFromSharedPref(
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
                            hideProgressDialog()
                           // println("RESPONSE - DeleteCart Ws :   " + Gson().toJson(result))
                            if (result != null) {
                                if (result.status == 200) {
                                    if (result.data != null) {
                                        productsDBHelper.deleteProductFromCart(
                                            a.id.toString(),
                                            a.id.toString()
                                        )
                                        onCartUpdateClicked.onCartUpdateClicked(
                                            result.data,
                                            "online"
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
                            hideProgressDialog()
                         //   println("ERROR - DeleteCart Ws :   " + error.localizedMessage)
                            Global.showSnackbar(
                                activity,
                                activity.resources.getString(R.string.error)
                            )
                        }
                    )
            }
        }

        @SuppressLint("CheckResult")
        private fun updateCartItem(
            a: GetCartListItemModel,
            strOrderID: String,
            updatedQty: Int,
            onCartUpdateClicked: com.app.blockaat.cart.interfaces.UpdateCartItemEvent,
            productsDBHelper: DBHelper
        ) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                showProgressDialog(activity as Activity)


                val updateCart = UpdateCartRequest(
                    a.id.toString(),
                    updatedQty.toString(),
                    a.video_id,
                    strOrderID,
                    a.celeb_id,
                    Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                    a.order_item_id
                )

                Global.apiService.updateCartItem(
                    updateCart,
                    com.app.blockaat.apimanager.WebServices.UpdateCartItemWs + Global.getStringFromSharedPref(
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
                          //  println("RESPONSE - UpdateCart Ws :   " + Gson().toJson(result))
                            if (result != null) {
                                hideProgressDialog()
                                if (result.status == 200) {
                                    if (result.data != null) {
                                        productsDBHelper.updateProductsInCart(
                                            updatedQty.toString(),
                                            a.id,
                                            a.id
                                        )
                                        onCartUpdateClicked.onCartUpdateClicked(
                                            result.data,
                                            "online"
                                        )

                                    }
                                } else {
                                    Global.showSnackbar(activity, result.message)
                                }
                            } else {
                                hideProgressDialog()
                                //if ws not working
                                Global.showSnackbar(
                                    activity,
                                    activity.resources.getString(R.string.error)
                                )
                            }
                        },
                        { error ->
                         //   println("ERROR - UpdateCart Ws :   " + error.localizedMessage)
                            hideProgressDialog()
                            Global.showSnackbar(
                                activity,
                                activity.resources.getString(R.string.error)
                            )
                        }
                    )
            }
        }

        private var progressDialog: CustomProgressBar? = null

        private fun showProgressDialog(activity: Activity) {
            progressDialog = CustomProgressBar(activity)
            progressDialog?.showDialog()
        }

        private fun hideProgressDialog() {
            if (progressDialog != null) {
                progressDialog?.hideDialog()
            }
        }
    }


}