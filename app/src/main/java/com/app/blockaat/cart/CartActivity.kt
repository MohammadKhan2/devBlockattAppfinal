package com.app.blockaat.cart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adjust.sdk.Adjust
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.account.AccountActivity
import com.app.blockaat.cart.adapter.OfflineCartDataAdapter
import com.app.blockaat.cart.adapter.OnlineCartDataAdapter
import com.app.blockaat.cart.model.CheckItemStockRequest
import com.app.blockaat.cart.model.GetCartListDataModel
import com.app.blockaat.cart.model.GetCartListItemModel
import com.app.blockaat.checkout.activity.AddressCheckoutActivity
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.app.blockaat.wishlist.adapter.WishlistAdapter
import com.app.blockaat.wishlist.modelclass.WishListDataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_cart.linBottom
import kotlinx.android.synthetic.main.activity_cart.relSubTotal
import kotlinx.android.synthetic.main.activity_cart.relDeliveryCharges
import kotlinx.android.synthetic.main.activity_cart.relTotal
import kotlinx.android.synthetic.main.activity_cart.rvCartList
import kotlinx.android.synthetic.main.activity_cart.swipeRefreshLayout
import kotlinx.android.synthetic.main.activity_cart.txtProceedToCheckout
import kotlinx.android.synthetic.main.activity_cart.txtSubTotalLabel
import kotlinx.android.synthetic.main.activity_cart.txtSubtotal
import kotlinx.android.synthetic.main.activity_cart.txtDeliveryCharges
import kotlinx.android.synthetic.main.activity_cart.txtTotal
import kotlinx.android.synthetic.main.activity_cart.txtTotalLabel
import kotlinx.android.synthetic.main.activity_cart.txtVatCharges
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.txtTotalItem
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view
import kotlinx.android.synthetic.main.toolbar_default_main.*

class CartActivity : BaseActivity() {

    private var disposable: Disposable? = null
    private var progressDialog: CustomProgressBar? = null
    private lateinit var adapterCartListOnline: OnlineCartDataAdapter
    private lateinit var adapterCartListOffline: OfflineCartDataAdapter
    private var arrListCartItemOnline = ArrayList<GetCartListItemModel>()
    private var arrListCartItemOffline = ArrayList<ProductsDataModel>()
    private var strOrderID: String = ""
    private lateinit var productsDBHelper: DBHelper
    private var isAddAddress: Boolean? = false

    private var isFromRefresh = false
    private var isFromRefreshFavorites = false
    private var isCartLoaded = false
    private var isFavoritesLoaded = false
    private lateinit var adapterWishList: WishlistAdapter
    private lateinit var arrListWishlistData: ArrayList<WishListDataModel>
    private val isFromFragment = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.menu_cart)
    }

    private fun initializeFields() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)

        ivEmpty?.setImageResource(R.drawable.empty_cart)
       // ivEmpty.setColorFilter(ContextCompat.getColor(this@CartActivity, R.color.red_pink_color))

        txtEmptyMessage?.text = getString(R.string.empty_bag)
        txtEmptyMessage?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
        txtEmptyTitle?.visibility=GONE
        //  txtEmptyTitle?.text = getString(R.string.empty_bag_title)
        txtEmptyBtn?.text = getString(R.string.continue_shopping)
        txtEmptySecond?.visibility = View.GONE
        productsDBHelper = DBHelper(this@CartActivity)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@CartActivity)
        rvCartList.layoutManager = layoutManager
        rvCartList.isNestedScrollingEnabled = false

        relWishlistImage.visibility = VISIBLE
        imgSearch.visibility = GONE

        if (Global.getTotalWishListProductCount(this) > 0) {
            txtWishlistCount.visibility = View.VISIBLE
            txtWishlistCount.text =
                Global.getTotalWishListProductCount(this).toString()
        } else {
            txtWishlistCount.visibility = View.GONE
        }
    }


    private fun setFonts() {
        txtTotal!!.typeface = Global.fontPriceBold
        txtSubtotal!!.typeface = Global.fontRegular
        txtVatCharges!!.typeface = Global.fontRegular
        txtSubTotalLabel!!.typeface = Global.fontRegular
        txtProceedToCheckout!!.typeface = Global.fontBtn
        txtEmptyMessage!!.typeface = Global.fontSemiBold
        txtEmptyBtn!!.typeface = Global.fontBtn
        txtEmptySecond!!.typeface = Global.fontBtn
        txtTotalLabel!!.typeface = Global.fontBold
        txtHead.typeface = Global.fontNavBar
    }


    private fun onClickListener() {



        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                if (progressDialog!=null){
                    hideProgressDialog()
                }
                val i = Intent(this, WishlistActivity::class.java)
                startActivity(i)
            } else {
                if (progressDialog!=null){
                    hideProgressDialog()
                }
                val i = Intent(this, LoginActivity::class.java)
                i.putExtra("isFromProducts", "yes")
                startActivityForResult(i, 1)
            }
        }

        ivBackArrow.setOnClickListener {

            if (progressDialog!=null){
                hideProgressDialog()
            }
            if (intent.getStringExtra("isFromOrderSummary") == "yes") {
                val i = Intent(this@CartActivity, NavigationActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
                finish()
            }else{
                onBackPressed()

            }



        }

        txtEmptyBtn.setOnClickListener {
            if (progressDialog!=null){
                hideProgressDialog()
            }
            val i = Intent(this@CartActivity, NavigationActivity::class.java)
            startActivity(i)
            finishAffinity()
        }
        txtProceedToCheckout.setOnClickListener {
            if (progressDialog!=null){
                hideProgressDialog()
            }
            if (Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_isUSER_LOGGED_IN
                ) == "yes"
            ) {
                checkItemStock()
            } else {
                val intent = Intent(this@CartActivity, LoginActivity::class.java)
                intent.putExtra("isFromCartPageToLogin", "yes")
                startActivityForResult(intent, 1)
            }
        }
        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load cart page data
                loadData()
            }, 1000)
        }

        rvCartList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView == null || recyclerView.childCount == 0) 0 else recyclerView.getChildAt(
                        0
                    ).top
                swipeRefreshLayout!!.isEnabled = topRowVerticalPosition >= 0

            }

        })


    }

    private fun showListData() {
        rvCartList!!.visibility = View.VISIBLE
        txtTotalItem!!.visibility = View.VISIBLE
        linBottom!!.visibility = View.VISIBLE
        relEmpty!!.visibility = View.GONE
    }

    private fun emptyData() {

        rvCartList!!.visibility = View.GONE
        linBottom!!.visibility = View.GONE
        txtTotalItem!!.visibility = View.GONE
        relEmpty!!.visibility = View.VISIBLE
    }

    @SuppressLint("StringFormatInvalid")
    private fun getCartList() {
        if (NetworkUtil.getConnectivityStatus(this@CartActivity) != 0) {
            if (!isFromRefresh) {
                showProgressDialog(this@CartActivity)
                hideProgressDialog()

            }
            disposable = Global.apiService.getCartList(
                com.app.blockaat.apimanager.WebServices.GetCartListWs + Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&user_id=" + Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_USER_ID
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - CART LIST Ws :   " + result.data)
                        productsDBHelper.deleteTable("table_cart")
                        if (result != null) {
                            if (!isFromRefresh) {
                                hideProgressDialog()
                            }
                            if (result.status == 200) {

                                if (result.data?.items != null && result.data.items.isNotEmpty()) {
                                    showListData()
                                    arrListCartItemOnline = result.data.items
                                    if (arrListCartItemOnline.size > 0) {
                                        txtTotalItem.visibility = View.VISIBLE
                                        txtTotalItem.text = getString(
                                            R.string.total_items_in_cart,
                                            arrListCartItemOnline.size.toString()
                                        )

                                    } else {
                                        txtTotalItem.visibility = GONE
                                    }

                                    for (x in 0 until arrListCartItemOnline.size) {
                                        productsDBHelper.addProductToCart(
                                            ProductsDataModel(
                                                arrListCartItemOnline[x].id,
                                                arrListCartItemOnline[x].id,
                                                arrListCartItemOnline[x].name,
                                                "",
                                                arrListCartItemOnline[x].image,
                                                arrListCartItemOnline[x].description,
                                                arrListCartItemOnline[x].quantity.toString(),
                                                arrListCartItemOnline[x].final_price,
                                                arrListCartItemOnline[x].regular_price,
                                                arrListCartItemOnline[x].SKU,
                                                arrListCartItemOnline[x].remaining_quantity.toString(),
                                                arrListCartItemOnline[x].is_featured.toString(),
                                                arrListCartItemOnline[x].is_saleable.toString(),
                                                arrListCartItemOnline[x].product_type,
                                                "",
                                                "",
                                                arrListCartItemOnline[x].order_item_id
                                            )
                                        ) //not passing color and size and when logout then db will have to clear
                                    }

                                    strOrderID = result.data.id.toString()
                                    // cart not empty
                                    rvCartList!!.visibility = View.VISIBLE
                                    adapterCartListOnline = OnlineCartDataAdapter(
                                        arrListCartItemOnline,
                                        strOrderID,
                                        onCartUpdateClicked,
                                        productsDBHelper
                                    )
                                    rvCartList!!.adapter = adapterCartListOnline


                                    // after local db updated now start setting the price
                                    //managePriceData(result.data.total.toString(), "", "", "")
                                    managePriceData(
                                        productsDBHelper.getTotalCartAmount().toString(),
                                        productsDBHelper.getTotalCartAmount().toString(),
                                        "",
                                        result.data.vat_charges,
                                        result.data.shipping_cost!!
                                    )
                                } else {
                                    // cart is empty
                                    emptyData()
                                }


                            } else {
                                Global.showSnackbar(this@CartActivity, result.message)
                            }
                        } else {
                            if (!isFromRefresh) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(
                                this@CartActivity,
                                this@CartActivity.resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                       // println("ERROR - CART LIST Ws :   " + error.localizedMessage + " " + error.cause + " " + error.message)
                        if (!isFromRefresh) {
                            hideProgressDialog()
                        }
                        Global.showSnackbar(
                            this@CartActivity,
                            this@CartActivity.resources.getString(R.string.error)
                        )
                        productsDBHelper.deleteTable("table_cart")
                    }
                )
            isFromRefresh = false
        }
    }


    @SuppressLint("StringFormatInvalid")
    private val onCartUpdateClicked =
        com.app.blockaat.cart.interfaces.UpdateCartItemEvent { data: GetCartListDataModel?, type: String ->
            if (type == "online") {
                if (adapterCartListOnline != null) {
                    arrListCartItemOnline.clear()
                    arrListCartItemOnline.addAll(data!!.items!!)
                    txtTotalItem.text = getString(R.string.total_items_in_cart, data?.items?.size.toString())
                    adapterCartListOnline.notifyDataSetChanged()




                    if (data.items != null && data.items.size != 0) {
                        showListData()

                        // after local db updated now start setting the price
                        //println("HERE IS :::    " + data.total + "  :   " + data.sub_total + "  :   " + data.delivery_charge)
                        // managePriceData(data.total.toString(), "", "", "")
                        managePriceData(
                            productsDBHelper.getTotalCartAmount().toString(),
                            productsDBHelper.getTotalCartAmount().toString(),
                            "",
                            data.vat_charges,
                            data.shipping_cost!!
                        )
                    } else {
                        emptyData()
                    }

                } else {
                    //hide cart option as adapter null means no item
                    emptyData()
                }
            } else {
                if (adapterCartListOffline != null) {
                    arrListCartItemOffline.clear()
                    arrListCartItemOffline.addAll(productsDBHelper.getAllCartProducts())
                    txtTotalItem.text = getString(R.string.total_items_in_cart, data?.items?.size.toString())
                    adapterCartListOffline.notifyDataSetChanged()

                    if (productsDBHelper.getAllCartProducts() != null && productsDBHelper.getAllCartProducts().size != 0) {
                        showListData()
                        offlineTotalPrice()

                    } else {
                        emptyData()
                    }

                } else {
                    //hide cart option as adapter null means no item
                    emptyData()
                }

            }
        }

    private fun offlineTotalPrice() {
        //offline manage cart
        managePriceData(
            productsDBHelper.getTotalCartAmount().toString(),
            productsDBHelper.getTotalCartAmount().toString(),
            "",
            "",
            ""
        )
    }

    private fun managePriceData(
        grand_total: String?,
        sub_total: String?,
        strDeliveryCharge: String?,
        vat_charges: String?,
        shipping_cost: String
    ) {
        //showing total product count // -1 means count is zero/ cart is empty

        var total = ""
        if (vat_charges != null && vat_charges != "") {
            total =
                (sub_total!!.toDouble()!! + vat_charges!!.toDouble()!! + shipping_cost!!.toDouble()!!).toString()
        } else {
            total = sub_total!!.toDouble().toString()
        }
        /*  if (total != null && total.isNotEmpty()) {
              txtTotal!!.text = Global.setPriceWithCurrency(mActivity!!, total)
              txtBagTotal!!.text =
                  resources.getString(R.string.bag_total) + "  " + Global.setPriceWithCurrency(
                      mActivity!!,
                      total
                  )
          }*/

        if (vat_charges != null && !vat_charges.isNullOrEmpty() && vat_charges != "0" && vat_charges != "0.00") {
//            lnrVatCharges!!.visibility = View.GONE
//            txtVatTotal!!.text = Global.setPriceWithCurrency(mActivity!!, vat_charges!!)

        } else {
//            lnrVatCharges!!.visibility = View.GONE
        }

        if (shipping_cost != null && !shipping_cost.isNullOrEmpty() && shipping_cost != "0" && shipping_cost != "0.00") {
//            txtShippingTotal!!.text = Global.setPriceWithCurrency(mActivity!!, shipping_cost)
//            lnrShippingCost!!.visibility = View.GONE
        } else {
//            lnrShippingCost!!.visibility = View.GONE
        }

        if (Global.stringToDouble(sub_total) > 0) {
            relSubTotal?.visibility = View.VISIBLE
            txtSubtotal!!.text = Global.setPriceWithCurrency(this, sub_total)

        } else {
            relSubTotal?.visibility = View.GONE
        }

        if (Global.stringToDouble(shipping_cost) > 0) {
            relDeliveryCharges?.visibility = View.VISIBLE
            txtDeliveryCharges!!.text = Global.setPriceWithCurrency(this, shipping_cost)

        } else {
            relDeliveryCharges?.visibility = View.GONE
        }

        if (Global.stringToDouble(total) > 0) {
            relTotal?.visibility = View.VISIBLE
            txtTotal!!.text = Global.setPriceWithCurrency(this, total)

        } else {
            relTotal?.visibility = View.GONE
        }
    }


    private fun checkItemStock() {
        val strMultipleCartEntityIDs = productsDBHelper.getAllCartEntityIDs().toString()
            .substring(1, productsDBHelper.getAllCartEntityIDs().toString().length - 1)
        val strMultipleCartProductQty = productsDBHelper.getAllCartProductQty().toString()
            .substring(1, productsDBHelper.getAllCartProductQty().toString().length - 1)
        //println("HERE IS :::    $strMultipleCartEntityIDs   :   $strMultipleCartProductQty    :   $strOrderID")
        val strMultipleCartOrderItemId = productsDBHelper.getAllOrderId().toString()
            .substring(1, productsDBHelper.getAllOrderId().toString().length - 1)
        if (NetworkUtil.getConnectivityStatus(this@CartActivity) != 0) {
            showProgressDialog(this@CartActivity)
            val checkItemStock = CheckItemStockRequest(
                Global.getStringFromSharedPref(this@CartActivity, Constants.PREFS_USER_ID),
                strMultipleCartEntityIDs,
                strMultipleCartProductQty,
                strOrderID,
                strMultipleCartOrderItemId
            )
            disposable = Global.apiService.checkItemStock(
                checkItemStock,
                com.app.blockaat.apimanager.WebServices.CheckItemStockWs + Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this@CartActivity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - check-item-stock Ws :   " + Gson().toJson(result))
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (Global.isUserLoggedIn(this)){
                                    val userId = Global.getUserId(this)
                                    val total:String? = result.data?.total
                                    val orderId:String? = result.data?.cart?.id
                                    CustomEvents.initiatedCheckout(this,orderId,total,userId)
                                }
                                val i =
                                    Intent(this@CartActivity, AddressCheckoutActivity::class.java)
                                i.putExtra("checkoutData", result.data)
                                i.putExtra("strOrderID", result.data?.cart?.id)
                                startActivity(i)
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@CartActivity,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                        hideProgressDialog()
                      //  println("ERROR - check-item-stock Ws :   " + error.localizedMessage)
                        Global.showSnackbar(this@CartActivity, resources.getString(R.string.error))
                    }
                )
        }
    }

    private fun loadData() {
        if (Global.getStringFromSharedPref(
                this@CartActivity,
                Constants.PREFS_isUSER_LOGGED_IN
            ) == "yes"
        ) {
            getCartList()

        } else {
            //not logged in
            arrListCartItemOffline = productsDBHelper.getAllCartProducts()

            if (arrListCartItemOffline != null && !arrListCartItemOffline.isEmpty()) {
                rvCartList!!.visibility = View.VISIBLE
                adapterCartListOffline = OfflineCartDataAdapter(
                    arrListCartItemOffline,
                    onCartUpdateClicked,
                    productsDBHelper
                )
                rvCartList!!.adapter = adapterCartListOffline
                if (arrListCartItemOffline.size > 0) {
                    txtTotalItem.visibility = View.VISIBLE
                    txtTotalItem.text = getString(
                        R.string.total_items_in_cart,
                        arrListCartItemOffline.size.toString()
                    )

                } else {
                    txtTotalItem.visibility = GONE
                }
                offlineTotalPrice()
                showListData()
            } else {
                //no item in db cart
                emptyData()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        Adjust.onPause()
        hideProgressDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
        if (progressDialog!=null){
            hideProgressDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
        loadData()

        isAddAddress = Global.getBooleanFromSharedPref(this@CartActivity, Constants.ADD_ADDRESS)
        if (isAddAddress == true) {
            checkItemStock()
            Global.saveBooleanInSharedPref(this@CartActivity, Constants.ADD_ADDRESS, false)

        }
    }

    private fun showProgressDialog(activity: Context) {
        progressDialog = CustomProgressBar(this@CartActivity)
        progressDialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.hideDialog()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            Global.showSnackbar(this@CartActivity, resources.getString(R.string.user_success_msg))
        }


    }

    fun updateCounts() {
        if (Global.isUserLoggedIn(this)) {

            // txtWishlistCount.visibility = View.GONE
            if (Global.getTotalWishListProductCount(this) > 0) {
                txtWishlistCount.visibility = View.VISIBLE
                txtWishlistCount.text =
                    Global.getTotalWishListProductCount(this).toString()
            } else {
                txtWishlistCount.visibility = View.GONE
            }
        } else {
            txtWishlistCount.visibility = View.GONE
        }

    }

/*
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            loadData()
        }
    }
*/


}