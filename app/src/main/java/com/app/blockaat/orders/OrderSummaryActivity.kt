package com.app.blockaat.orders

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.cart.CartActivity
import com.app.blockaat.checkout.activity.CheckoutActivity
import com.app.blockaat.checkout.adapter.CheckoutItemAdapter
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.orders.model.CheckoutDataModel
import com.app.blockaat.orders.model.CheckoutItemItemModel
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_order_summary.*
import kotlinx.android.synthetic.main.activity_order_summary.nestedMain
import kotlinx.android.synthetic.main.activity_order_summary.relButton
import kotlinx.android.synthetic.main.activity_order_summary.relCod
import kotlinx.android.synthetic.main.activity_order_summary.relCoupon
import kotlinx.android.synthetic.main.activity_order_summary.relDiscount
import kotlinx.android.synthetic.main.activity_order_summary.relRefId
import kotlinx.android.synthetic.main.activity_order_summary.relStatus
import kotlinx.android.synthetic.main.activity_order_summary.linAddress
import kotlinx.android.synthetic.main.activity_order_summary.linItemsInfo
import kotlinx.android.synthetic.main.activity_order_summary.linOrderSummary
import kotlinx.android.synthetic.main.activity_order_summary.linPaymentSummary
import kotlinx.android.synthetic.main.activity_order_summary.relTrackId
import kotlinx.android.synthetic.main.activity_order_summary.relTransId
import kotlinx.android.synthetic.main.activity_order_summary.relVat
import kotlinx.android.synthetic.main.activity_order_summary.rvItems
import kotlinx.android.synthetic.main.activity_order_summary.txtAddress
import kotlinx.android.synthetic.main.activity_order_summary.txtCod
import kotlinx.android.synthetic.main.activity_order_summary.txtCodLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtCoupon
import kotlinx.android.synthetic.main.activity_order_summary.txtDelivery
import kotlinx.android.synthetic.main.activity_order_summary.txtDeliveryLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtDiscount
import kotlinx.android.synthetic.main.activity_order_summary.txtDiscountLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtDone
import kotlinx.android.synthetic.main.activity_order_summary.txtTryAgain
import kotlinx.android.synthetic.main.activity_order_summary.relPaymentCancel
import kotlinx.android.synthetic.main.activity_order_summary.linPaymentDetails
import kotlinx.android.synthetic.main.activity_order_summary.txtItemsInfo
import kotlinx.android.synthetic.main.activity_order_summary.txtMobile
import kotlinx.android.synthetic.main.activity_order_summary.txtOrderNumber
import kotlinx.android.synthetic.main.activity_order_summary.txtOrderNumberLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtOrderSummary
import kotlinx.android.synthetic.main.activity_order_summary.txtPayModeLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtPayModeValue
import kotlinx.android.synthetic.main.activity_order_summary.txtPaymentDateLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtPaymentDateValue
import kotlinx.android.synthetic.main.activity_order_summary.txtPaymentSummary
import kotlinx.android.synthetic.main.activity_order_summary.txtRefIdLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtRefIdValue
import kotlinx.android.synthetic.main.activity_order_summary.txtShippingAddress
import kotlinx.android.synthetic.main.activity_order_summary.txtStatusLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtStatusValue
import kotlinx.android.synthetic.main.activity_order_summary.txtSubtotalLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtSubtotalValue
import kotlinx.android.synthetic.main.activity_order_summary.txtTotalLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtTotalValue
import kotlinx.android.synthetic.main.activity_order_summary.txtTrackIdLable
import kotlinx.android.synthetic.main.activity_order_summary.txtTrackIdValue
import kotlinx.android.synthetic.main.activity_order_summary.txtTransIdLabel
import kotlinx.android.synthetic.main.activity_order_summary.txtTransIdValue
import kotlinx.android.synthetic.main.activity_order_summary.txtVatCharges
import kotlinx.android.synthetic.main.activity_order_summary.txtVatLabel
import kotlinx.android.synthetic.main.toolbar_default.*
import org.json.JSONObject

class OrderSummaryActivity : BaseActivity() {
    private lateinit var orderSummaryData: CheckoutDataModel
    private var paymentDataJson: JSONObject? = null
    private lateinit var productsDBHelper: DBHelper
    private var strPaymentType = ""
    private var cod: Double? = 0.0
    private var vat: Double? = 0.0
    private var grandTotal: Double = 0.0
    private var isCodAdded = false
    private var isCodSelected = false
    private var subtotal: Double? = 0.0
    private var delivery: Double? = 0.0
    private var discount: Double? = 0.0
    private var userId:String? = null
    private var orderId:String? = null
    private var orderDate:String? = null
    private var paymentMethod:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        Global.setLocale(this@OrderSummaryActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun setEventData(){
        orderId = orderSummaryData.order_details?.order_id.toString()
        orderDate = orderSummaryData.order_details?.order_date
        paymentMethod = orderSummaryData.payment_mode
        grandTotal = orderSummaryData.total?.toDouble()!!
        userId = Global.getUserId(this)
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = getString(R.string.order_summary)
        ivBackArrow.visibility = GONE
    }

    private fun initializeFields() {
        productsDBHelper = DBHelper(this@OrderSummaryActivity)
        productsDBHelper.deleteTable("table_cart")

        if(intent.hasExtra("orderSummaryData")){
            orderSummaryData = intent.getSerializableExtra("orderSummaryData") as CheckoutDataModel
        }
        if (intent.hasExtra("isFromVisaKNET") && intent.getStringExtra("isFromVisaKNET") == "yes") {
            paymentDataJson = JSONObject(intent.getStringExtra("paymentDetail"))
        }
        if (intent.hasExtra("paymentType")) {
            strPaymentType = intent.getStringExtra("paymentType")!!
        }
        setStatus()
        setInfo()
        setShippingAddress()
        setPaymentMethod()
        setItemsInOrder()
        nestedMain.visibility = View.VISIBLE
        relButton.visibility = View.VISIBLE
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtShippingAddress.typeface = Global.fontSemiBold
        txtAddress.typeface = Global.fontRegular
        txtMobile.typeface = Global.fontMedium
        txtName.typeface = Global.fontMedium
        txtDone.typeface = Global.fontBtn
        txtTryAgain.typeface = Global.fontBtn
        txtPaymentMsgHeader.typeface = Global.fontMedium
        txtPaymentMsg.typeface = Global.fontRegular
        txtItemsInfo.typeface = Global.fontMedium
        txtOrderSummary.typeface = Global.fontMedium
        txtPaymentSummary.typeface = Global.fontMedium
        txtOrderNumberLabel.typeface = Global.fontRegular
        txtPaymentDateLabel.typeface = Global.fontRegular

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

        txtSubtotalValue.typeface = Global.fontPriceMedium
        txtDiscount.typeface = Global.fontPriceMedium
        txtCod.typeface = Global.fontPriceMedium
        txtVatCharges.typeface = Global.fontPriceMedium
        txtDelivery.typeface = Global.fontPriceMedium
        txtTotalValue.typeface = Global.fontPriceMedium

        txtPaymentDateValue.typeface = Global.fontMedium
        txtTrackIdValue.typeface = Global.fontMedium
        txtTransIdValue.typeface = Global.fontMedium
        txtRefIdValue.typeface = Global.fontMedium
        txtPayModeValue.typeface = Global.fontMedium
        txtStatusValue.typeface = Global.fontMedium
    }

    private fun onClickListeners() {

        txtDone.setOnClickListener {
            val i = Intent(this@OrderSummaryActivity, NavigationActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setStatus() {
        if (strPaymentType == "C") {

        } else {
            if (paymentDataJson != null && paymentDataJson.toString().isNotEmpty()) {
                //it means its from k-net
                if (paymentDataJson?.has("Result") == true) {
                    val a = Global.checkNull(paymentDataJson?.getString("Result"))

                    if (!a.isNullOrEmpty() && a?.replace("+", " ") == "NOT CAPTURED") {
                        txtPaymentMsgHeader.text = resources.getString(R.string.transaction_failed)
                        txtPaymentMsg.text = resources.getString(R.string.transaction_failed_msg)
                        imgPayment.setBackgroundResource(R.drawable.ic_failed);

                    } else if (a == "CANCEL") {
                        txtPaymentMsgHeader.text = resources.getString(R.string.transaction_failed)
                        txtPaymentMsg.text = resources.getString(R.string.transaction_failed_msg)
                        imgPayment.setBackgroundResource(R.drawable.ic_failed);

                    } else if (a == "DECLINE") {
                        txtPaymentMsgHeader.text = resources.getString(R.string.transaction_failed)
                        txtPaymentMsg.text = resources.getString(R.string.transaction_failed_msg)
                        imgPayment.setBackgroundResource(R.drawable.ic_failed);

                    } else {
                        txtPaymentMsgHeader.text = resources.getString(R.string.payment_successful)
                        txtPaymentMsg.text = resources.getString(R.string.shopping_successful)
                        imgPayment.setBackgroundResource(R.drawable.ic_tick_green);
                        setEventData()
                        CustomEvents.itemPurchased(this,orderDate,orderId,paymentMethod,grandTotal.toString(),"",userId)
                    }
                } else {

                   // txtPaymentMsgHeader.text = resources.getString(R.string.transaction_failed)
                   // txtPaymentMsg.text = resources.getString(R.string.transaction_cancelled)
                    relPaymentCancel.visibility=View.VISIBLE
                    relButton.visibility=View.GONE
                    linPaymentDetails.visibility=View.GONE
                    txtDone.visibility=View.GONE
                    txtHead.text = getString(R.string.payment_error)

                    txtTryAgain.setOnClickListener {
                        val i = Intent(this@OrderSummaryActivity, CartActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        i.putExtra("isFromOrderSummary", "yes")
                        startActivity(i)
                        finish()
                    }

                }
            } else {
                txtPaymentMsgHeader.text = resources.getString(R.string.transaction_failed)
                txtPaymentMsg.text = resources.getString(R.string.transaction_cancelled)
            }
        }
    }

    private fun setInfo() {

        txtPaymentDateValue.text = Global.getFormattedDateWithNotation(
            "yyyy-MM-dd",
            orderSummaryData.order_details?.order_date.toString()
        )


        /*  txtPaymentDateValue.text = Global.getFormattedDate(
              "yyyy-MM-dd hh:mm:ss",
              "dd-MM-yyyy",
              orderSummaryData.order_details?.order_date.toString()
          )*/
        txtOrderNumber.text = "# " + orderSummaryData.order_details?.order_number

        txtSubtotalValue.text = Global.setPriceWithCurrency(
            this,
            orderSummaryData.sub_total.toString()
        )
        subtotal = orderSummaryData.sub_total?.toDouble()

        if (!orderSummaryData.delivery_charges.isNullOrEmpty() && orderSummaryData.delivery_charges?.toDouble()!! > 0) {
            txtDelivery.text = Global.setPriceWithCurrency(
                this,
                orderSummaryData.delivery_charges.toString()
            )
            delivery = orderSummaryData.delivery_charges?.toDouble()
        } else {
            txtDelivery.text = resources.getString(R.string.free).toUpperCase()
        }
        if (!orderSummaryData.vat_charges.isNullOrEmpty() && orderSummaryData.vat_charges?.toDouble()!! > 0) {
            relVat.visibility = View.VISIBLE
            txtVatCharges.text = Global.setPriceWithCurrency(
                this,
                orderSummaryData.vat_charges.toString()
            )
            vat = orderSummaryData.vat_charges?.toDouble()
        } else {
            relVat.visibility = View.GONE
            vat = 0.0
        }
        if (!orderSummaryData.cod_cost.isNullOrEmpty() && orderSummaryData.cod_cost?.toDouble()!! > 0) {
            relCod.visibility = View.VISIBLE
            txtCod.text = Global.setPriceWithCurrency(
                this,
                orderSummaryData.cod_cost.toString()
            )
            cod = orderSummaryData.cod_cost?.toDouble()
        } else {
            cod = 0.0
            relCod.visibility = View.GONE
        }

        if (!orderSummaryData.discount_price.isNullOrEmpty() && orderSummaryData.discount_price?.toDouble()!! > 0) {
            txtDiscount.text = Global.setPriceWithCurrency(
                this,
                orderSummaryData.discount_price.toString()
            )
            discount = orderSummaryData.discount_price?.toDouble()
            relDiscount.visibility = View.VISIBLE

        } else {
            discount = 0.0
            relDiscount.visibility = View.GONE
        }

        if (orderSummaryData.is_coupon_applied == 1) {
            txtCoupon.text = orderSummaryData.coupon?.code
            relCoupon.visibility = View.VISIBLE
        } else {
            relCoupon.visibility = View.GONE
        }
        txtTotalValue.text =
            Global.setPriceWithCurrency(this, orderSummaryData?.total.toString())
    }

    private fun setShippingAddress() {
        if (orderSummaryData.shipping_address != null) {
            val addressModel = orderSummaryData?.shipping_address
            txtName.text = orderSummaryData.shipping_address?.address_name

            txtAddress.text = Global.getFormattedAddressForListingWithLabel(
                activity = this,
                notes = addressModel?.notes ?: "",
                flat = addressModel?.flat_no ?: "",
                floor = addressModel?.floor_no ?: "",
                building_no = addressModel?.building ?: "",
                street = addressModel?.street ?: "",
                jaddah = addressModel?.jaddah ?: "",
                block = addressModel?.block_name ?: "",
                area = addressModel?.area_name ?: "",
                governorate = addressModel?.governorate_name ?: "",
                country = addressModel?.country_name ?: ""
            )


            if(Global.getStringFromSharedPref(this@OrderSummaryActivity,Constants.PREFS_USER_PHONE_CODE).isNullOrEmpty())
            {
                txtMobile.text ="+"+addressModel?.phonecode+" "+ addressModel?.mobile_number ?: ""
            }else
            {
                if(Global.getStringFromSharedPref(this@OrderSummaryActivity,Constants.PREFS_USER_PHONE_CODE).contains("+"))
                {
                    txtMobile.text =Global.getStringFromSharedPref(this@OrderSummaryActivity,Constants.PREFS_USER_PHONE_CODE)+" "+ addressModel?.mobile_number ?: ""
                }else
                {
                    txtMobile.text ="+"+Global.getStringFromSharedPref(this@OrderSummaryActivity,Constants.PREFS_USER_PHONE_CODE)+" "+ addressModel?.mobile_number ?: ""

                }
            }        }
    }

    private fun setPaymentMethod() {
        when (orderSummaryData?.payment_mode) {
            "C" -> {
                txtPayModeValue.text = resources.getString(R.string.cash_on_delivery)
            }
            "Q" -> {
                txtPayModeValue.text = resources.getString(R.string.q_pay)
            }
            "K" -> {
                txtPayModeValue.text = resources.getString(R.string.k_net)
            }
            "CC" -> {
                txtPayModeValue.text = resources.getString(R.string.mastercard_visa)
            }
        }
        if (intent.hasExtra("isFromVisaKNET")) {
            paymentDataJson = JSONObject(intent.getStringExtra("paymentDetail"))
            setDataKnet()
        } else {
            relTransId.visibility = View.GONE
            relRefId.visibility = View.GONE
            relTrackId.visibility = View.GONE
            relStatus.visibility = View.GONE
            relStatus.visibility = View.GONE
            //viewPayMode.visibility = View.GONE
            txtPaymentSummary.visibility = View.VISIBLE
        }

    }

    private fun setDataKnet() {
        if (paymentDataJson?.has("Result") == true && paymentDataJson?.getString("Result") != null) {
            txtStatusValue.text =
                paymentDataJson?.getString("Result").toString().replace("%20", " ")
                    .replace("+", " ")
        } else {
            relStatus.visibility = View.GONE
        }
        if (paymentDataJson?.has("TranID") == true && paymentDataJson?.getString("TranID") != null) {
            txtTransIdValue.text = paymentDataJson?.getString("TranID")
        } else {
            relTransId.visibility = View.GONE
        }

        if (paymentDataJson?.has("TrackID") == true && paymentDataJson?.getString("TrackID") != null) {
            txtTrackIdValue.text = paymentDataJson?.getString("TrackID")
        } else {
            relTrackId.visibility = View.GONE
        }


        if (paymentDataJson?.has("Ref") == true && paymentDataJson?.getString("Ref") != null) {
            txtRefIdValue.text = paymentDataJson?.getString("Ref")
        } else {
            relRefId.visibility = View.GONE
        }
    }

    private fun setItemsInOrder() {
        val adapter = CheckoutItemAdapter(
            this@OrderSummaryActivity,
            orderSummaryData.cart?.items as ArrayList<CheckoutItemItemModel>
        )
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = adapter
        adapter.notifyDataSetChanged()
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

    override fun onBackPressed() {

    }


}
