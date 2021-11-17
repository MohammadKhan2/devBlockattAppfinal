package com.app.blockaat.payment.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.orders.OrderSummaryActivity
import com.app.blockaat.orders.model.CheckoutItemDataModel
import com.app.blockaat.orders.model.CheckoutItemPaymentTypeModel
import com.app.blockaat.payment.PaymentWebActivity
import com.app.blockaat.payment.adapter.PaymentTypeAdapter
import com.app.blockaat.payment.interfaces.PaymentClickListener
import com.app.blockaat.payment.model.RedeemCouponRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.toolbar_default.*

class PaymentActivity : BaseActivity() {

    private var mToolbar: Toolbar? = null
    private lateinit var checkoutDataModel: CheckoutItemDataModel
    private var paymentAdapter: PaymentTypeAdapter? = null
    private var arrListPayment: ArrayList<CheckoutItemPaymentTypeModel>? = null
    private var strAddressID: String? = null
    private var strPaymentCode: String? = null
    private var dialog: CustomProgressBar? = null
    private var subtotal: Double? = 0.0
    private var delivery: Double? = 0.0
    private var discount: Double? = 0.0

    private var cod: Double? = 0.0
    private var vat: Double? = 0.0
    private var grandTotal: Double = 0.0
    private var isCodAdded = false
    private var isCodSelected = false
    private var disposable: Disposable? = null
    private var isPromoCodeApplied : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        Global.setLocale(this@PaymentActivity)
        initializeToolbar()
        initializeFields()
        setOnClickListener()
        setFonts()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)

        /* mToolbar = toolbar_actionbar as Toolbar?
         setSupportActionBar(mToolbar)
         supportActionBar!!.setDisplayHomeAsUpEnabled(true)
         supportActionBar!!.setDisplayShowTitleEnabled(false)
         var upArrow = ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_back_arrow)
         if (!Global.isEnglishLanguage(this@PaymentActivity)) {
             upArrow = ContextCompat.getDrawable(this@PaymentActivity, R.drawable.ic_back_arrow_ar)
         }
         upArrow?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
         upArrow?.setVisible(true, true)
         supportActionBar!!.setHomeAsUpIndicator(upArrow)
         AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)*/
        txtHead.text = resources.getString(R.string.checkout)
    }

    private fun initializeFields() {
        checkoutDataModel = intent.getSerializableExtra("checkoutData") as CheckoutItemDataModel
        if (checkoutDataModel?.shipping_note.isNullOrEmpty()) {
            expandedNotes.collapse()
            ivNotes.rotationX = 180f
        } else {
            expandedNotes.expand()
            txtNotes.text = checkoutDataModel?.shipping_note.toString()
        }
        setPayment()
        setSummary()
        setAddress()
        setRedeemCode()
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtStep.typeface = Global.fontMedium
        txtPayment.typeface = Global.fontMedium
        txtPromoCode.typeface = Global.fontMedium
        txtPaymentOptions.typeface = Global.fontMedium
        txtOrderSummary.typeface = Global.fontMedium
        txtSubtotalLabel.typeface = Global.fontRegular
        txtSubtotal.typeface = Global.fontPriceRegular
        txtDeliveryLabel.typeface = Global.fontRegular
        txtDelivery.typeface = Global.fontPriceMedium
        txtGrandTotalLabel.typeface = Global.fontBold
        txtGrandTotal.typeface = Global.fontBold
        txtPayNow.typeface = Global.fontBtn
        txtLabelDeliveryAddress.typeface = Global.fontMedium
        txtFullName.typeface = Global.fontMedium
        txtAddress.typeface = Global.fontRegular
        txtPhoneNumber.typeface = Global.fontRegular
        txtCodLabel.typeface = Global.fontRegular
        txtCod.typeface = Global.fontPriceRegular
        txtVatLabel.typeface = Global.fontRegular
        txtVat.typeface = Global.fontPriceRegular
        txtNotesLabel.typeface = Global.fontMedium
        txtNotes.typeface = Global.fontRegular
        edtPromoCode.typeface = Global.fontRegular
        txtApply.typeface = Global.fontBtn
        txtDiscountLabel.typeface = Global.fontRegular
        txtDiscount.typeface = Global.fontPriceRegular
        txtCouponLabel.typeface = Global.fontRegular
        txtCoupon.typeface = Global.fontPriceRegular
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtPayNow.setOnClickListener {
            if (strPaymentCode.isNullOrEmpty()) {
                Global.showSnackbar(
                    this@PaymentActivity,
                    resources.getString(R.string.please_select_payment)
                )
            } else {
                placeOrder()
            }
        }

        txtApply.setOnClickListener {
            if(!edtPromoCode.text.toString().isNullOrEmpty())
            {
                redeemCoupon()
            }else
            {
                Global.showSnackbar(this@PaymentActivity,resources.getString(R.string.enter_promo_code))
            }
        }

        relNotes.setOnClickListener {
            if (expandedNotes.isExpanded) {
                expandedNotes.collapse()
                ivNotes.rotationX = 180f
            } else {
                expandedNotes.expand()
                ivNotes.rotationX = 0f
            }
        }
    }

    private fun setPayment() {
        if (checkoutDataModel != null) {
            arrListPayment = ArrayList()
            checkoutDataModel.payment_types?.let { arrListPayment?.addAll(it) }
            paymentAdapter = PaymentTypeAdapter(
                this@PaymentActivity,
                arrListPayment as ArrayList<CheckoutItemPaymentTypeModel>,
                paymentInterface
            )
            rvPayment.layoutManager = LinearLayoutManager(this)
            rvPayment.adapter = paymentAdapter
            paymentAdapter?.notifyDataSetChanged()
        }

    }

    private fun setSummary() {
        if (checkoutDataModel != null) {
      //   println("Set cod price")

            txtSubtotal.text = Global.setPriceWithCurrency(
                this@PaymentActivity,
                checkoutDataModel.sub_total.toString()
            )
            subtotal = checkoutDataModel.sub_total?.toDouble()

            if (!checkoutDataModel.delivery_charges.isNullOrEmpty() && checkoutDataModel.delivery_charges?.toDouble()!! > 0) {
                txtDelivery.text = Global.setPriceWithCurrency(
                    this@PaymentActivity,
                    checkoutDataModel.delivery_charges.toString()
                )
                delivery = checkoutDataModel.delivery_charges?.toDouble()
            } else {
                txtDelivery.text = resources.getString(R.string.free).toUpperCase()
            }
            if (!checkoutDataModel.vat_charges.isNullOrEmpty() && checkoutDataModel.vat_charges?.toDouble()!! > 0) {
                relVat.visibility = View.VISIBLE
                txtVat.text = Global.setPriceWithCurrency(
                    this@PaymentActivity,
                    checkoutDataModel.vat_charges.toString()
                )
                vat = checkoutDataModel.vat_charges?.toDouble()
            } else {
                relVat.visibility = View.GONE
                vat = 0.0
            }
         //  println("cod added :"+isCodAdded)
            if(isCodSelected)
            {
                if (!checkoutDataModel.cod_cost.isNullOrEmpty() && checkoutDataModel.cod_cost?.toDouble()!! > 0) {
                    relCod.visibility = View.VISIBLE
                    txtCod.text = Global.setPriceWithCurrency(
                        this@PaymentActivity,
                        checkoutDataModel.cod_cost.toString()
                    )
                    cod = checkoutDataModel.cod_cost?.toDouble()
                } else {
                    cod = 0.0
                    relCod.visibility = View.GONE
                }
            }else
            {
                cod = 0.0
                relCod.visibility = View.GONE
            }

            if (!checkoutDataModel.discount_price.isNullOrEmpty() && checkoutDataModel.discount_price?.toDouble()!! > 0) {
                txtDiscount.text = Global.setPriceWithCurrency(
                    this@PaymentActivity,
                    checkoutDataModel.discount_price.toString()
                )
                discount = checkoutDataModel.discount_price?.toDouble()
                relDiscount.visibility = View.VISIBLE

            } else {
                discount = 0.0
                relDiscount.visibility = View.GONE
            }

            if (checkoutDataModel.is_coupon_applied == 1) {
                txtCoupon.text = checkoutDataModel.coupon?.code
                relCoupon.visibility = View.VISIBLE
            } else {
                relCoupon.visibility = View.GONE
            }

            if(isCodSelected)
            {
                grandTotal = subtotal as Double + delivery as Double + vat as Double + cod as Double - discount as Double

            }else{
                grandTotal = subtotal as Double + delivery as Double + vat as Double - discount as Double

            }
            // println("grand total: "+grandTotal)
            txtGrandTotal.text =
                Global.setPriceWithCurrency(this@PaymentActivity, grandTotal.toString())
        }
    }

    private fun setAddress() {
        txtFullName.text = checkoutDataModel.default_address?.address_name
        strAddressID = checkoutDataModel.default_address?.address_id.toString()
        txtAddress.text = Global.getFormattedAddress(
            this@PaymentActivity,
            checkoutDataModel.default_address?.zone.toString(),
            "",
            checkoutDataModel.default_address?.street.toString(),
            checkoutDataModel.default_address?.jaddah.toString(),
            checkoutDataModel.default_address?.area_name.toString(),
            checkoutDataModel.default_address?.governorate_name.toString(),
            checkoutDataModel.default_address?.country_name.toString(),
            checkoutDataModel.default_address?.building.toString()
        )
        if(checkoutDataModel.default_address?.phonecode?.contains("+")==true)
        {
            txtPhoneNumber.text =
                "${checkoutDataModel.default_address?.phonecode}" + checkoutDataModel.default_address?.mobile_number
        }else
        {
            txtPhoneNumber.text =
                "+${checkoutDataModel.default_address?.phonecode}" + checkoutDataModel.default_address?.mobile_number
        }
        txtPhoneNumber.layoutDirection = View.LAYOUT_DIRECTION_LTR

    }

    private fun setRedeemCode()
    {
        if(checkoutDataModel.is_coupon_applied == 1)
        {
            edtPromoCode.setText(checkoutDataModel.coupon?.code.toString())
            txtApply.text = resources.getString(R.string.remove)
            isPromoCodeApplied = true
            edtPromoCode.isEnabled = false
        }
    }

    @SuppressLint("CheckResult")
    private fun placeOrder() {

        if (NetworkUtil.getConnectivityStatus(this@PaymentActivity) != 0) {
            showProgressDialog()
            Global.apiService.checkout(
                Global.getStringFromSharedPref(this@PaymentActivity, Constants.PREFS_USER_ID),
                checkoutDataModel.cart?.id.toString(),
                strPaymentCode.toString(),
                strAddressID.toString(),
                Constants.DEVICE_TYPE,
                Constants.DEVICE_MODEL,
                Constants.APP_VERSION,
                Constants.OS_VERSION,
                Constants.DEVICE_TOKEN,
                "1",
                com.app.blockaat.apimanager.WebServices.CheckoutWs + Global.getStringFromSharedPref(
                    this@PaymentActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this@PaymentActivity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                           // println("RESPONSE - CheckOut Ws :   " + Gson().toJson(result.data))
                            if (result.status == 200) {
                                Global.strDeliveryAddressId = ""
                                if (result.data != null && result.data.payment_url.toString().isNullOrEmpty()) {
                                    //COD - take to order summary page

                                    val i = Intent(
                                        this@PaymentActivity,
                                        OrderSummaryActivity::class.java
                                    )
                                    i.putExtra("orderSummaryData", result.data)
                                    i.putExtra("paymentType", strPaymentCode)
                                    startActivity(i)
                                    finish()

                                } else if (result.data != null) {
                                    // visa/k-net take to payment page
                                    val i =
                                        Intent(this@PaymentActivity, PaymentWebActivity::class.java)
                                    i.putExtra("orderSummaryData", result.data)
                                    i.putExtra("paymentType", strPaymentCode)
                                    startActivity(i)
                                }
                            } else {
                                //some error in response
                                Global.showSnackbar(this@PaymentActivity, result.message.toString())
                            }
                        } else {
                            //ws not working
                            Global.showSnackbar(
                                this@PaymentActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        hideProgressDialog()
                      //  println("ERROR - CheckOut Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this@PaymentActivity,
                            resources.getString(R.string.error)
                        )

                    }
                )
        }
    }

    private fun redeemCoupon() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {

            showProgressDialog()
            val requestModel = RedeemCouponRequest(
              user_id = Global.getUserId(this@PaymentActivity),
              shipping_address_id = strAddressID,
              order_id =   checkoutDataModel.cart?.id,
              coupon_code = if(isPromoCodeApplied) "" else edtPromoCode.text.toString()
            )
            disposable = Global.apiService.redeemCoupon(
                requestModel,
                com.app.blockaat.apimanager.WebServices.RedeemCoupon + Global.getStoreCode(
                    this
                )+ "&lang=" + Global.getStringFromSharedPref(
                    this@PaymentActivity,
                    Constants.PREFS_LANGUAGE
                )
            ) .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result.status == 200 && result.data != null) {
                              if(!isPromoCodeApplied)
                              {
                                  txtApply.text = resources.getString(R.string.remove)
                                  isPromoCodeApplied = true
                                  edtPromoCode.isEnabled = false
                              }else
                              {
                                  edtPromoCode.isEnabled = true
                                  edtPromoCode.setText("")
                                  txtApply.text = resources.getString(R.string.apply)
                                  isPromoCodeApplied = true
                              }
                            checkoutDataModel = result.data
                            setSummary()
                        } else {
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.enter_promo_code_not_exist)
                            )
                           /* Global.showSnackbar(
                                this,
                                result.message.toString()
                            )*/
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    })
        }
    }

    private fun showProgressDialog() {
        dialog = CustomProgressBar(this@PaymentActivity)
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }


    private val paymentInterface = object : PaymentClickListener {
        override fun onPaymentClicked(position: Int) {
            for (i in 0 until arrListPayment?.size as Int) {
                arrListPayment?.get(i)?.isSelected = false

                if (position == i) {

                    if (arrListPayment?.get(i)?.code == "C") {
                        if (isCodAdded) {
                            grandTotal = grandTotal + cod as Double
                            isCodAdded = true

                        }
                        isCodSelected = true
                    } else {
                        if (isCodAdded) {
                            grandTotal = grandTotal - cod as Double
                            isCodAdded = false

                        }
                        isCodSelected = false
                    }
                    setSummary()
                   /* txtGrandTotal.text =
                        Global.setPriceWithCurrency(this@PaymentActivity, grandTotal.toString())*/
                    strPaymentCode = arrListPayment?.get(i)?.code
                    arrListPayment?.get(i)?.isSelected = true
                }
            }
            if (paymentAdapter != null)
                paymentAdapter?.notifyDataSetChanged()
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
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        if(disposable!=null)
        {
            disposable?.dispose()
        }
    }

}
