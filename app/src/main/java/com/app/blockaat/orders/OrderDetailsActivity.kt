package com.app.blockaat.orders

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.checkout.adapter.CheckoutItemAdapter
import com.app.blockaat.helper.*
import com.app.blockaat.orders.model.CheckoutItemItemModel
import com.app.blockaat.orders.model.OrderDetailsData
import com.app.blockaat.orders.model.OrderDetailsPaymentDetails
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.activity_order_details.nestedMain
import kotlinx.android.synthetic.main.activity_order_details.relButton
import kotlinx.android.synthetic.main.activity_order_details.relCod
import kotlinx.android.synthetic.main.activity_order_details.relCoupon
import kotlinx.android.synthetic.main.activity_order_details.relDiscount
import kotlinx.android.synthetic.main.activity_order_details.relRefId
import kotlinx.android.synthetic.main.activity_order_details.relStatus
import kotlinx.android.synthetic.main.activity_order_details.relTrackId
import kotlinx.android.synthetic.main.activity_order_details.relTransId
import kotlinx.android.synthetic.main.activity_order_details.relVat
import kotlinx.android.synthetic.main.activity_order_details.rvItems
import kotlinx.android.synthetic.main.activity_order_details.txtAddress
import kotlinx.android.synthetic.main.activity_order_details.txtCod
import kotlinx.android.synthetic.main.activity_order_details.txtCodLabel
import kotlinx.android.synthetic.main.activity_order_details.txtCoupon
import kotlinx.android.synthetic.main.activity_order_details.txtDelivery
import kotlinx.android.synthetic.main.activity_order_details.txtDeliveryLabel
import kotlinx.android.synthetic.main.activity_order_details.txtDiscount
import kotlinx.android.synthetic.main.activity_order_details.txtDiscountLabel
import kotlinx.android.synthetic.main.activity_order_details.txtDone
import kotlinx.android.synthetic.main.activity_order_details.txtItemsInfo
import kotlinx.android.synthetic.main.activity_order_details.txtMobile
import kotlinx.android.synthetic.main.activity_order_details.txtOrderNumber
import kotlinx.android.synthetic.main.activity_order_details.txtOrderNumberLabel
import kotlinx.android.synthetic.main.activity_order_details.txtOrderSummary
import kotlinx.android.synthetic.main.activity_order_details.txtPayModeLabel
import kotlinx.android.synthetic.main.activity_order_details.txtPayModeValue
import kotlinx.android.synthetic.main.activity_order_details.txtPaymentDateLabel
import kotlinx.android.synthetic.main.activity_order_details.txtPaymentDateValue
import kotlinx.android.synthetic.main.activity_order_details.txtPaymentSummary
import kotlinx.android.synthetic.main.activity_order_details.txtRefIdLabel
import kotlinx.android.synthetic.main.activity_order_details.txtRefIdValue
import kotlinx.android.synthetic.main.activity_order_details.txtShippingAddress
import kotlinx.android.synthetic.main.activity_order_details.txtStatusLabel
import kotlinx.android.synthetic.main.activity_order_details.txtStatusValue
import kotlinx.android.synthetic.main.activity_order_details.txtSubtotalLabel
import kotlinx.android.synthetic.main.activity_order_details.txtSubtotalValue
import kotlinx.android.synthetic.main.activity_order_details.txtTotalLabel
import kotlinx.android.synthetic.main.activity_order_details.txtTotalValue
import kotlinx.android.synthetic.main.activity_order_details.txtTrackIdLable
import kotlinx.android.synthetic.main.activity_order_details.txtTrackIdValue
import kotlinx.android.synthetic.main.activity_order_details.txtTransIdLabel
import kotlinx.android.synthetic.main.activity_order_details.txtTransIdValue
import kotlinx.android.synthetic.main.activity_order_details.txtVatCharges
import kotlinx.android.synthetic.main.activity_order_details.txtVatLabel
import kotlinx.android.synthetic.main.activity_order_details.viewPayMode
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.json.JSONObject

class OrderDetailsActivity : BaseActivity() {

    private var orderDetailsData: OrderDetailsData? = null
    private var dialog: CustomProgressBar? = null
    private var mToolbar: Toolbar? = null
    private var cod: Double? = 0.0
    private var vat: Double? = 0.0
    private var grandTotal: Double = 0.0
    private var isCodAdded = false
    private var isCodSelected = false
    private var subtotal: Double? = 0.0
    private var delivery: Double? = 0.0
    private var discount: Double? = 0.0
    private var paymentDataJson: JSONObject? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        Global.setLocale(this@OrderDetailsActivity)
        initializeToolbar()
        initializeFields()
        setOnClickListener()
        setFonts()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)

        txtHead.text = resources.getString(R.string.order_details)
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtSubtotalLabel.typeface = Global.fontMedium
        txtShippingAddress.typeface = Global.fontSemiBold
        txtAddress.typeface = Global.fontRegular
        txtDone.typeface = Global.fontBtn
        txtItemsInfo.typeface = Global.fontMedium
        txtOrderSummary.typeface = Global.fontMedium
        txtPaymentSummary.typeface = Global.fontMedium
        txtOrderNumberLabel.typeface = Global.fontRegular
        txtPaymentDateLabel.typeface = Global.fontRegular
        txtMobile.typeface = Global.fontNavBar
        txtSubtotalLabel.typeface = Global.fontRegular
        txtDiscountLabel.typeface = Global.fontRegular
        txtCodLabel.typeface = Global.fontRegular
        txtVatLabel.typeface = Global.fontRegular
        txtDeliveryLabel.typeface = Global.fontRegular
        txtTotalLabel.typeface = Global.fontMedium

        txtRefIdLabel.typeface = Global.fontRegular
        txtTransIdLabel.typeface = Global.fontRegular
        txtStatusLabel.typeface = Global.fontRegular
        txtTrackIdLable.typeface = Global.fontRegular
        txtPayModeLabel.typeface = Global.fontRegular

        txtOrderNumber.typeface = Global.fontMedium
        txtPaymentDateValue.typeface = Global.fontMedium

        txtSubtotalValue.typeface = Global.fontPriceMedium
        txtDiscount.typeface = Global.fontPriceMedium
        txtCod.typeface = Global.fontPriceMedium
        txtVatCharges.typeface = Global.fontPriceMedium
        txtDelivery.typeface = Global.fontPriceMedium
        txtTotalValue.typeface = Global.fontMedium

        txtTrackIdValue.typeface = Global.fontMedium
        txtTransIdValue.typeface = Global.fontMedium
        txtRefIdValue.typeface = Global.fontMedium
        txtPayModeValue.typeface = Global.fontMedium
        txtStatusValue.typeface = Global.fontMedium
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this@OrderDetailsActivity)
        getOrderDetail()
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        relButton.visibility= GONE
        txtDone.visibility= GONE
        /*txtDone.setOnClickListener {
            onBackPressed()
        }*/
    }

    private fun getOrderDetail() {
        if (NetworkUtil.getConnectivityStatus(this@OrderDetailsActivity) != 0) {
            showProgressDialog()
            val disposable = Global.apiService.getMyOrderDetail(
                com.app.blockaat.apimanager.WebServices.MyOrdersDetailWs + Global.getLanguage(this)
                        + "&user_id=" + Global.getUserId(this) + "&id=" + intent.getStringExtra("orderID")
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {

                            println("RESPONSE - MyOrder Detail Ws :   " + result.data)
                            if (result.status == 200) {
                                setData(result.data)
                            } else {
                                nestedMain.visibility = GONE
                                relButton.visibility = GONE
                            }
                            hideProgressDialog()
                        } else {
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@OrderDetailsActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - MyOrder Detail :   " + error.localizedMessage)
                        hideProgressDialog()
                        Global.showSnackbar(
                            this@OrderDetailsActivity,
                            resources.getString(R.string.error)
                        )
                        nestedMain.visibility = GONE
                        relButton.visibility = GONE
                    }
                )
        } else {
            Global.showSnackbar(
                this@OrderDetailsActivity,
                resources.getString(R.string.connection_error)
            )
        }
    }

    private fun setData(data: OrderDetailsData?) {
        orderDetailsData = data
        if (orderDetailsData != null) {
            setInfo()
            setShippingAddress()
            setPaymentMethod()
            setItemsInOrder()
            nestedMain.visibility = View.VISIBLE
            //if required Done button then uncomment recylerview
            relButton.visibility = View.GONE
        } else {
            nestedMain.visibility = GONE
            relButton.visibility = GONE
        }
    }


    /// payment details
    private fun setDataKnet(paymentDetails: OrderDetailsPaymentDetails?) {
        if (paymentDetails?.result.isNullOrEmpty()) {
            relStatus.visibility = GONE
        } else {
            relStatus.visibility = View.VISIBLE
            txtStatusValue.text =
                paymentDetails?.result.toString().replace("%20", " ")
                    .replace("+", " ")
         //   txtStatusValue.text = paymentDetails?.result
        }

        if (paymentDetails?.transaction_id.isNullOrEmpty()) {
            relTransId.visibility = GONE
        } else {
            relTransId.visibility = View.VISIBLE
            txtTransIdValue.text = paymentDetails?.transaction_id
        }


        if (paymentDetails?.track_id.isNullOrEmpty()) {
            relTrackId.visibility = GONE
        } else {
            relTrackId.visibility = View.VISIBLE
            txtTrackIdValue.text = paymentDetails?.track_id
        }



        if (paymentDetails?.ref.isNullOrEmpty()) {
            relRefId.visibility = GONE
        } else {
            relRefId.visibility = View.VISIBLE
            txtRefIdValue.text = paymentDetails?.ref
        }
    }


    private fun setInfo() {

        txtPaymentDateValue.text = Global.getFormattedDateWithNotation(
            "yyyy-MM-dd",
            orderDetailsData?.created_date.toString()
        )


       /* txtPaymentDateValue.text = Global.getFormattedDate(
            "yyyy-MM-dd hh:mm:ss",
            "dd-MM-yyyy",
            orderDetailsData?.created_date.toString()
        )*/
        txtOrderNumber.text = "# " + orderDetailsData?.order_number

        txtSubtotalValue.text = Global.setPriceWithCurrency(
            this,
            orderDetailsData?.sub_total.toString()
        )
        subtotal = orderDetailsData?.sub_total?.toDouble()

        if (!orderDetailsData?.delivery_charges.isNullOrEmpty() && orderDetailsData?.delivery_charges?.toDouble()!! > 0) {
            txtDelivery.text = Global.setPriceWithCurrency(
                this,
                orderDetailsData?.delivery_charges.toString()
            )
            delivery = orderDetailsData?.delivery_charges?.toDouble()
        } else {
            txtDelivery.text = resources.getString(R.string.free).toUpperCase()
        }
        if (!orderDetailsData?.vat_charges.isNullOrEmpty() && orderDetailsData?.vat_charges?.toDouble()!! > 0) {
            relVat.visibility = View.VISIBLE
            txtVatCharges.text = Global.setPriceWithCurrency(
                this,
                orderDetailsData?.vat_charges.toString()
            )
            vat = orderDetailsData?.vat_charges?.toDouble()
        } else {
            relVat.visibility = GONE
            vat = 0.0
        }
        if (!orderDetailsData?.cod_cost.isNullOrEmpty() && orderDetailsData?.cod_cost?.toDouble()!! > 0) {
            relCod.visibility = View.VISIBLE
            txtCod.text = Global.setPriceWithCurrency(
                this,
                orderDetailsData?.cod_cost.toString()
            )
            cod = orderDetailsData?.cod_cost?.toDouble()
        } else {
            cod = 0.0
            relCod.visibility = GONE
        }

        if (!orderDetailsData?.discount_price.isNullOrEmpty() && orderDetailsData?.discount_price?.toDouble()!! > 0) {
            txtDiscount.text = Global.setPriceWithCurrency(
                this,
                orderDetailsData?.discount_price.toString()
            )
            discount = orderDetailsData?.discount_price?.toDouble()
            relDiscount.visibility = View.VISIBLE

        } else {
            discount = 0.0
            relDiscount.visibility = GONE
        }

        if (orderDetailsData?.is_coupon_applied == 1) {
            txtCoupon.text = orderDetailsData?.coupon?.code
            relCoupon.visibility = View.VISIBLE
        } else {
            relCoupon.visibility = GONE
        }
        txtTotalValue.text =
            Global.setPriceWithCurrency(this, orderDetailsData?.total.toString())

        txtMobile.text ="+"+orderDetailsData?.shipping_address?.phonecode +" "+ orderDetailsData?.shipping_address?.mobile_number
    }

    private fun setShippingAddress() {
        if (orderDetailsData?.shipping_address != null) {
            txtAddress.text = Global.getFormattedAddressForListingWithLabel(
                activity = this,
                notes = orderDetailsData?.shipping_address?.notes ?: "",
                flat = orderDetailsData?.shipping_address?.flat_no ?: "",
                floor = orderDetailsData?.shipping_address?.floor_no ?: "",
                building_no = orderDetailsData?.shipping_address?.building ?: "",
                street = orderDetailsData?.shipping_address?.street ?: "",
                jaddah = orderDetailsData?.shipping_address?.jaddah ?: "",
                block = orderDetailsData?.shipping_address?.block_name ?: "",
                area = orderDetailsData?.shipping_address?.area_name ?: "",
                governorate = orderDetailsData?.shipping_address?.governorate_name ?: "",
                country = orderDetailsData?.shipping_address?.country_name ?: ""
            )
        }
    }

    private fun setPaymentMethod() {
        when (orderDetailsData?.payment_mode) {
            "C" -> {
                txtPayModeValue.text = resources.getString(R.string.cash_on_delivery)
            }
            "Q" -> {
                txtPayModeValue.text = resources.getString(R.string.q_pay)
            }
            "CC" -> {
                txtPayModeValue.text = resources.getString(R.string.mastercard_visa)
            }
            "K" -> {
            txtPayModeValue.text = resources.getString(R.string.k_net)
        }
        }
        // Removed by vikki condition - && !orderDetailsData?.payment_details?.paymentID.isNullOrEmpty()
        if (orderDetailsData?.payment_details != null) {
            setDataKnet(orderDetailsData?.payment_details)
            viewPayMode.visibility = VISIBLE
        } else {
            relTransId.visibility = GONE
            relRefId.visibility = GONE
            relTrackId.visibility = GONE
            relStatus.visibility = GONE
            viewPayMode.visibility = GONE
            txtPaymentSummary.visibility = View.VISIBLE
        }

    }

    private fun setItemsInOrder() {
        val arrListItems =  ArrayList<CheckoutItemItemModel>(0)

        arrListItems.addAll(orderDetailsData?.items as ArrayList<CheckoutItemItemModel>)

        val adapter = CheckoutItemAdapter(this, arrListItems)
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    fun showProgressDialog() {
        dialog?.showDialog()
    }

    fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    //below is inbuilt function of android to manage activity toolbar arrow
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
