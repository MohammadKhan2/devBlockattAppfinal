package com.app.blockaat.cart

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.addressListingCart.AddressListingCartActivity
import com.app.blockaat.cart.adapter.OfflineCartDataAdapter
import com.app.blockaat.cart.adapter.OnlineCartDataAdapter
import com.app.blockaat.cart.model.CheckItemStockRequest
import com.app.blockaat.cart.model.GetCartListDataModel
import com.app.blockaat.cart.model.GetCartListItemModel
import com.app.blockaat.checkout.activity.AddressCheckoutActivity
import com.app.blockaat.checkout.activity.CheckoutActivity
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.layout_empty_page.*

class CartFragment : Fragment() {
    private var disposable: Disposable? = null
    private var progressDialog: CustomProgressBar? = null
    private lateinit var adapterCartListOnline: OnlineCartDataAdapter
    private lateinit var adapterCartListOffline: OfflineCartDataAdapter
    private var arrListCartItemOnline = ArrayList<GetCartListItemModel>()
    private var arrListCartItemOffline = ArrayList<ProductsDataModel>()
    private var strOrderID: String = ""
    private lateinit var productsDBHelper: DBHelper
    private var isFromRefresh: Boolean? = false
    private var isAddAddress: Boolean? = false
    private lateinit var mActivity: NavigationActivity
    private var swipeRefreshLayout: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity

        AppController.instance.trackScreenView(getString(R.string.my_bag_screen))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields(view)
        setFonts()
        Global.setLocale(mActivity)
    }

    private fun setFonts() {
        txtTotal!!.typeface = Global.fontPriceBold
        txtSubtotal!!.typeface = Global.fontMedium
        txtVatCharges!!.typeface = Global.fontRegular
        txtSubTotalLabel!!.typeface = Global.fontRegular
        txtProceedToCheckout!!.typeface = Global.fontBtn
        txtEmptyMessage?.typeface = Global.fontRegular
        txtEmptyBtn?.typeface = Global.fontBtn
        txtEmptySecond?.typeface = Global.fontBtn
        txtEmptyTitle?.typeface = Global.fontMedium
        txtTotalLabel!!.typeface = Global.fontBold
        txtTotalItem.typeface = Global.fontRegular
    }

    private fun initializeFields(rootView: View) {

        productsDBHelper = DBHelper(mActivity!!)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(mActivity)
        ivEmpty?.setImageResource(R.drawable.empty_cart)
        // ivEmpty.setColorFilter(ContextCompat.getColor(mActivity, R.color.red_pink_color))

        txtEmptyMessage?.text = getString(R.string.empty_bag)
        txtEmptyMessage?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F)
        txtEmptyTitle?.visibility = GONE
        //  txtEmptyTitle?.text = getString(R.string.empty_bag_title)
        txtEmptyBtn?.text = getString(R.string.continue_shopping)
        txtEmptySecond?.visibility = View.GONE
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout)

        rvCartList!!.layoutManager = layoutManager
        rvCartList!!.isNestedScrollingEnabled = false

        txtEmptyBtn.setOnClickListener {
            (mActivity).displayView(0)
        }

        txtProceedToCheckout!!.setOnClickListener {
            if (Global.isUserLoggedIn(mActivity)) {
                checkItemStock()
            } else {
                val intent = Intent(mActivity, LoginActivity::class.java)
                intent.putExtra("isFromCartPageToLogin", "yes")
                startActivityForResult(intent, 1)
            }
        }

        swipeRefreshLayout!!.setOnRefreshListener {
            try {
                isFromRefresh = true
                swipeRefreshLayout!!.isRefreshing = true
                swipeRefreshLayout!!.postDelayed({
                    swipeRefreshLayout!!.isRefreshing = false
                    //Load cart page data
                    loadData()
                }, 1000)

            } catch (e: Exception) {
            }
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
        txtTotalItem!!.visibility = View.VISIBLE
        rvCartList?.visibility = View.VISIBLE
        linBottom?.visibility = View.VISIBLE
        relEmpty?.visibility = View.GONE
    }

    private fun emptyData() {
        txtTotalItem?.visibility = View.GONE
        rvCartList?.visibility = View.GONE
        linBottom?.visibility = View.GONE
        relEmpty?.visibility = View.VISIBLE
    }

    @SuppressLint("StringFormatInvalid")
    private fun getCartList() {
        if (NetworkUtil.getConnectivityStatus(mActivity!!) != 0) {
            if (isFromRefresh == false) {
                showProgressDialog(mActivity!!)
            }
            disposable = Global.apiService.getCartList(
                com.app.blockaat.apimanager.WebServices.GetCartListWs + Global.getLanguage(
                    mActivity!!
                ) + "&user_id=" + Global.getUserId(mActivity!!)
                        + "&store=" + Global.getStoreCode(mActivity!!)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - CART LIST Ws :   " + result.data)
                        productsDBHelper.deleteTable("table_cart")
                        if (result != null) {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            if (result.status == 200) {


                                if (result.data?.items != null && result.data.items.isNotEmpty()) {
                                    showListData()
                                    arrListCartItemOnline = result.data.items

                                    if (arrListCartItemOnline.size > 0) {
                                        txtTotalItem.visibility = VISIBLE
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
                                    rvCartList?.visibility = View.VISIBLE
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
                                Global.showSnackbar(mActivity!!, result.message)
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                        /*    Global.showSnackbar(
                                mActivity!!,
                                mActivity!!.resources.getString(R.string.error)
                            )*/
                        }

                        (mActivity).updateCartBadge()
                    },
                    { error ->
                       // println("ERROR - CART LIST Ws :   " + error.localizedMessage + " " + error.cause + " " + error.message)
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
/*
                        Global.showSnackbar(
                            mActivity!!,
                            mActivity!!.resources.getString(R.string.error)
                        )
*/
                        productsDBHelper.deleteTable("table_cart")
                        (mActivity).updateCartBadge()
                    }
                )
            isFromRefresh = false
        }
    }

    private val onCartUpdateClicked =
        com.app.blockaat.cart.interfaces.UpdateCartItemEvent { data: GetCartListDataModel?, type: String ->
            if (type == "online") {
                if (adapterCartListOnline != null) {
                    arrListCartItemOnline.clear()
                    arrListCartItemOnline.addAll(data!!.items!!)
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

            (mActivity).updateCartBadge()
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
            txtSubtotal!!.text = Global.setPriceWithCurrency(mActivity!!, sub_total)

        } else {
            relSubTotal?.visibility = View.GONE
        }

        if (Global.stringToDouble(shipping_cost) > 0) {
            relDeliveryCharges?.visibility = View.VISIBLE
            txtDeliveryCharges!!.text = Global.setPriceWithCurrency(mActivity!!, shipping_cost)

        } else {
            relDeliveryCharges?.visibility = View.GONE
        }

        if (Global.stringToDouble(total) > 0) {
            relTotal?.visibility = View.VISIBLE
            txtTotal!!.text = Global.setPriceWithCurrency(mActivity!!, total)

        } else {
            relTotal?.visibility = View.GONE
        }
    }

    private fun checkItemStock() {
        val strMultipleCartEntityIDs = productsDBHelper.getAllCartEntityIDs().toString()
            .substring(1, productsDBHelper.getAllCartEntityIDs().toString().length - 1)
        val strMultipleCartProductQty = productsDBHelper.getAllCartProductQty().toString()
            .substring(1, productsDBHelper.getAllCartProductQty().toString().length - 1)
        val strMultipleCartOrderItemId = productsDBHelper.getAllOrderId().toString()
            .substring(1, productsDBHelper.getAllOrderId().toString().length - 1)

        if (NetworkUtil.getConnectivityStatus(mActivity!!) != 0) {
            showProgressDialog(mActivity!!)
            val checkItemStock = CheckItemStockRequest(
                Global.getStringFromSharedPref(mActivity!!, Constants.PREFS_USER_ID),
                strMultipleCartEntityIDs,
                strMultipleCartProductQty,
                strOrderID,
                strMultipleCartOrderItemId
            )
            disposable = Global.apiService.checkItemStock(
                checkItemStock,
                com.app.blockaat.apimanager.WebServices.CheckItemStockWs + Global.getStringFromSharedPref(
                    mActivity!!,
                    Constants.PREFS_LANGUAGE
                ) + "&store=" + Global.getStringFromSharedPref(
                    mActivity!!,
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

                                val i = Intent(mActivity, AddressCheckoutActivity::class.java)
                                i.putExtra("checkoutData", result.data)
                                i.putExtra("strOrderID", result.data?.cart?.id)
                                startActivity(i)
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(mActivity!!, resources.getString(R.string.error))
                        }

                    },
                    { error ->
                        hideProgressDialog()
                      //  println("ERROR - check-item-stock Ws :   " + error.localizedMessage)
                        Global.showSnackbar(mActivity!!, resources.getString(R.string.error))
                    }
                )
        }
    }

    private fun loadData() {
        if (Global.isUserLoggedIn(mActivity)) {
            getCartList()
        } else {
            //not logged in
            arrListCartItemOffline = productsDBHelper.getAllCartProducts()
            if (arrListCartItemOffline != null && arrListCartItemOffline.isNotEmpty()) {
                rvCartList!!.visibility = View.VISIBLE
                adapterCartListOffline =
                    OfflineCartDataAdapter(
                        arrListCartItemOffline,
                        onCartUpdateClicked,
                        productsDBHelper
                    )
                rvCartList!!.adapter = adapterCartListOffline
                offlineTotalPrice()
                if (arrListCartItemOffline.size > 0) {
                    txtTotalItem.visibility = VISIBLE
                    txtTotalItem.text = getString(
                        R.string.total_items_in_cart,
                        arrListCartItemOffline.size.toString()
                    )

                } else {
                    txtTotalItem.visibility = GONE
                }
                showListData()
            } else {
                //no item in db cart
                emptyData()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //   hideProgressDialog()
    }

    private fun showProgressDialog(activity: Context) {
        if (progressDialog == null) {
            progressDialog = CustomProgressBar(mActivity)
            progressDialog?.showDialog()
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.hideDialog()
            progressDialog = null

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onResume() {
        super.onResume()
        loadData()
        isAddAddress = Global.getBooleanFromSharedPref(mActivity, Constants.ADD_ADDRESS)
        if (isAddAddress == true) {
            checkItemStock()
            Global.saveBooleanInSharedPref(mActivity, Constants.ADD_ADDRESS, false)

        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (progressDialog != null) {
            progressDialog?.hideDialog()
        }
    }

}