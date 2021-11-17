package com.app.blockaat.orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.orders.adapter.OrdersAdapter
import com.app.blockaat.helper.*
import com.app.blockaat.orders.interfaces.OnOrderClickListener
import com.app.blockaat.orders.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_listing.*
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.toolbar_default.*

class OrderListingActivity : BaseActivity() {
    private var disposable: Disposable? = null
    var adapterOrderListing: OrdersAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private var isFromRefresh: Boolean = false
    private lateinit var progressDialog: CustomProgressBar
    private var mToolbar: Toolbar? = null
    private var strUserID: String = ""
    private var arrListMyOrderList: ArrayList<MyOrderDataModel>? = null

    private var arrListMyItemList: ArrayList<MyOrderItemModel>? = null
    private var arrListMyOrderConfigurableList: ArrayList<MyOrderConfigurableOptionModel>? = null
    private var arrListMyOrderAttributeModelList: ArrayList<MyOrderAttributeModel>? = null
    private var model: MyOrderShippingAddressModel? = null


    private var dialog: CustomProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_listing)

        Global.setLocale(this@OrderListingActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()

    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.my_orders)
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        ivEmpty.setImageResource(R.drawable.ic_no_order)
     //   ivEmpty.setColorFilter(ContextCompat.getColor(this, R.color.edt_box_border_color))
        txtEmptyTitle.text = getString(R.string.empty_order_heading)
        txtEmptyMessage.text = getString(R.string.empty_order_msg)
        txtEmptyBtn.text = getString(R.string.start_shopping)
        arrListMyOrderList = ArrayList()
        strUserID = SharedPreferencesHelper.getString(
            this@OrderListingActivity,
            Constants.PREFS_USER_ID,
            ""
        )!!

        swipeRefreshLayout?.setOnRefreshListener {
            isFromRefresh = true
            getOrdersData()
        }

        txtEmptyBtn.setOnClickListener {
            finish()
        }

        rvOrderListing!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView == null || recyclerView.childCount == 0) 0 else recyclerView.getChildAt(
                        0
                    ).top
                swipeRefreshLayout!!.isEnabled = topRowVerticalPosition >= 0
            }
        })

        adapterOrderListing = OrdersAdapter(
            arrListMyOrderList as ArrayList<MyOrderDataModel>,
            this@OrderListingActivity,
            orderInterface
        )
        val layoutManager = LinearLayoutManager(this@OrderListingActivity)
        rvOrderListing.layoutManager = layoutManager
        rvOrderListing.hasFixedSize()
        adapterOrderListing?.hasStableIds()
        rvOrderListing?.adapter = adapterOrderListing
        getOrdersData()

    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtEmptyMessage.typeface = Global.fontSemiBold
        txtEmptyBtn.typeface = Global.fontBtn
        txtEmptyTitle.typeface = Global.fontNavBar

    }


    @SuppressLint("CheckResult")
    fun getOrdersData() {

        if (NetworkUtil.getConnectivityStatus(this@OrderListingActivity) != 0) {

            showProgressDialog()

            Global.apiService.getOrdersData(
                com.app.blockaat.apimanager.WebServices.OrderListingWs + Global.getLanguage(this)
                        + "&user_id=" + Global.getUserId(this) + "&store=" + Global.getStringFromSharedPref(

                    this,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                         //   println("RESPONSE - Order Listing Ws :   " + result)
                            if (result.status == 200) {

                                if (result.data != null) {
                                    arrListMyOrderList?.clear()
                                    rvOrderListing.visibility = View.VISIBLE
                                    relEmpty.visibility = View.GONE
                                    result.data.let { arrListMyOrderList?.addAll(it) }
                                    adapterOrderListing?.notifyDataSetChanged()
                                } else {
                                    relEmpty.visibility = View.VISIBLE
                                }
                            } else {
                                rvOrderListing.visibility = View.GONE
                                relEmpty.visibility = View.VISIBLE
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(
                                this@OrderListingActivity,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                      //  println("ERROR - Order Listing Ws :   " + error.localizedMessage)

                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        Global.showSnackbar(
                            this@OrderListingActivity,
                            resources.getString(R.string.error)
                        )
                    })
        }
    }

    private val orderInterface = object : OnOrderClickListener {
        override fun onOrderClicked(position: Int) {
            val i = Intent(this@OrderListingActivity, OrderDetailsActivity::class.java)
            i.putExtra("orderID", arrListMyOrderList?.get(position)?.id.toString())
            startActivity(i)
        }

    }


    private fun showProgressDialog() {
        if (!isFromRefresh) {
            dialog?.showDialog()
        }
    }

    private fun hideProgressDialog() {
        if (!isFromRefresh) {
            isFromRefresh = false
            dialog?.hideDialog()
        } else {
            swipeRefreshLayout.isRefreshing = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            this.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}
