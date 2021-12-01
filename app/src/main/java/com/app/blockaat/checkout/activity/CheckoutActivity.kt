package com.app.blockaat.checkout.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.cart.model.CheckItemStockRequest
import com.app.blockaat.checkout.adapter.CheckoutItemAdapter
import com.app.blockaat.helper.*
import com.app.blockaat.orders.OrderSummaryActivity
import com.app.blockaat.orders.model.CheckoutItemDataModel
import com.app.blockaat.orders.model.CheckoutItemItemModel
import com.app.blockaat.orders.model.CheckoutItemPaymentTypeModel
import com.app.blockaat.payment.PaymentWebActivity
import com.app.blockaat.payment.adapter.PaymentTypeAdapter
import com.app.blockaat.payment.interfaces.PaymentClickListener
import com.app.blockaat.payment.model.RedeemCouponRequest
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_acc_info.*
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.toolbar_default.*


class CheckoutActivity : BaseActivity(), PaymentClickListener {
    private lateinit var itemsAdapter: CheckoutItemAdapter
    private lateinit var paymentAdapter: PaymentTypeAdapter
    private val arrListPayments: ArrayList<CheckoutItemPaymentTypeModel> = arrayListOf()
    private val arrListItems: ArrayList<CheckoutItemItemModel> = arrayListOf()
    private val EDIT_ADDRESS_REQUEST: Int = 200
    private val ADD_ADDRESS_REQUEST: Int = 100
    private val ADD_ADDRESS_RESULT: Int = 101
    private val EDIT_ADDRESS_RESULT: Int = 201
    private val PROMOCODE_UPDATE_REQUEST: Int = 202
    private val PROMOCODE_UPDATE_RESULT: Int = 301

    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null
    private lateinit var checkoutDataModel: CheckoutItemDataModel
    private var strPaymentType: String = ""
    private var arrListPayment: ArrayList<CheckoutItemPaymentTypeModel>? = null
    private var strAddressID: String? = null
    private var strPaymentCode: String? = null
    private var coupon_code: String? = null
    private var subtotal: Double? = 0.0
    private var delivery: Double? = 0.0
    private var discount: Double? = 0.0

    private var cod: Double? = 0.0
    private var vat: Double? = 0.0
    private var grandTotal: Double = 0.0
    private var isCodAdded = false
    private var isCodSelected = false
    private var disposable: Disposable? = null
    private var isPromoCodeApplied: Boolean = false
    private var lastClickTime: Long = 0

    // private lateinit var addAddressDataModel: AddAddressDataModel
    private lateinit var addressListingDataModel: com.app.blockaat.address.model.AddressListingDataModel
    private lateinit var productsDBHelper: DBHelper
    private var isMoveToPayment = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_checkout)

        Global.setLocale(this@CheckoutActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtAddShippingAddress.setOnClickListener {
            val intent = Intent(
                this@CheckoutActivity,
                com.app.blockaat.address.AddAddressActivity::class.java
            )
            intent.putExtra("isFromCheckoutChangeAddress", "yes")
            intent.putExtra("orderId", checkoutDataModel.cart?.id)
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }

        /*  relAddress.setOnClickListener {
              val intent = Intent(
                  this@CheckoutActivity,
                  AddressListingActivity::class.java
              )
              intent.putExtra("isFromCheckoutChangeAddress", "yes")
              intent.putExtra("orderId", checkoutDataModel.cart?.id)
              intent.putExtra("selectedAddress", strAddressID ?: "")
              startActivityForResult(intent, EDIT_ADDRESS_REQUEST)
          }*/

        txtPlaceOrder.setOnClickListener {
            if (strAddressID.isNullOrEmpty()) {
                Global.showSnackbar(this, getString(R.string.please_add_address))
                return@setOnClickListener
            } else if (strPaymentType.isNullOrEmpty()) {
                Global.showSnackbar(this, getString(R.string.please_select_payment))
                return@setOnClickListener
            } else {

                // preventing double, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){

                    return@setOnClickListener;

                }else{
                    placeOrder()
                }

                lastClickTime = SystemClock.elapsedRealtime();


            }
        }

        if (!isPromoCodeApplied) {
            if (!checkoutDataModel.coupon?.code.isNullOrEmpty()) {
                edtPromoCode.setText(checkoutDataModel.coupon?.code.toString())
                txtApply.text = resources.getString(R.string.remove)
            }
        }

        txtApply.setOnClickListener {
            if (!edtPromoCode.text.toString().isNullOrEmpty()) {
                if (!checkoutDataModel.coupon?.code.isNullOrEmpty()) {
                    isPromoCodeApplied = true
                    redeemCoupon()
                } else {
                    redeemCoupon()
                }
            } else {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.enter_promo_code)
                )
            }
        }
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)

        txtHead.text = resources.getString(R.string.checkout_3_of_3)
    }

    private fun initializeFields() {
        if (!Global.isEnglishLanguage(this@CheckoutActivity)) {
            ivArrowShipping.rotation = 180f
        }

        checkoutDataModel = intent.getSerializableExtra("checkoutData") as CheckoutItemDataModel
        productsDBHelper = DBHelper(this@CheckoutActivity)
        dialog = CustomProgressBar(this@CheckoutActivity)


        //payment
        paymentAdapter = PaymentTypeAdapter(
            this,
            arrListPayments,
            this
        )
        rvPayment.layoutManager = LinearLayoutManager(this)
        rvPayment.isNestedScrollingEnabled = false
        rvPayment.adapter = paymentAdapter


        //items
        itemsAdapter = CheckoutItemAdapter(
            this@CheckoutActivity,
            arrListItems
        )
        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.isNestedScrollingEnabled = false
        rvItems.adapter = itemsAdapter

        if (checkoutDataModel != null) {
            nestedScroll.visibility = VISIBLE
            setData()
        } else {
            nestedScroll.visibility = GONE
        }
        
    }

    private fun redeemCoupon() {
        /*if (isPromoCodeApplied) {
            isPromoCodeApplied = false
            coupon_code = ""
        } else {
            coupon_code = edtPromoCode.text.toString()
        }*/
        if (NetworkUtil.getConnectivityStatus(this) != 0) {

            showProgressDialog(this)
            val requestModel = RedeemCouponRequest(
                user_id = Global.getUserId(this),
                shipping_address_id = strAddressID,
                order_id = checkoutDataModel.cart?.id,
                coupon_code = if (isPromoCodeApplied) "" else edtPromoCode.text.toString()

            )
            disposable = Global.apiService.redeemCoupon(
                requestModel,
                com.app.blockaat.apimanager.WebServices.RedeemCoupon + Global.getStoreCode(
                    this
                )+ "&lang=" + Global.getStringFromSharedPref(
                    this@CheckoutActivity,
                    Constants.PREFS_LANGUAGE
                )
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result.status == 200 && result.data != null) {
                            if (!isPromoCodeApplied) {
                                txtApply.text = resources.getString(R.string.remove)
                                isPromoCodeApplied = true
                                edtPromoCode.isEnabled = false
                                Global.showSnackbar(
                                    this,
                                    resources.getString(R.string.enter_promo_code_success)
                                )
                            } else {
                                edtPromoCode.isEnabled = true
                                edtPromoCode.setText("")
                                txtApply.text = resources.getString(R.string.apply)
                                isPromoCodeApplied = false
                                Global.showSnackbar(
                                    this,
                                    resources.getString(R.string.enter_promo_code_remove)

                                )
                              /*  if (isPromoCodeApplied){

                                }*/
                            }
                            checkoutDataModel = result.data
                            setSummary()
                        } else {
                            /*Global.showSnackbar(
                                this,
                                result.message.toString()
                            )*/

                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.enter_promo_code_not_exist)
                            )
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

    private fun setData() {
        setSummary()
        setItems()
        setPaymentMethods()
        if (checkoutDataModel.default_address != null && !checkoutDataModel.default_address?.address_id.isNullOrEmpty()) {
            setAddressAndTime(checkoutDataModel.default_address!!)
            relAddress.visibility = View.VISIBLE
            txtAddShippingAddress.visibility = View.GONE
        } else {
            relAddress.visibility = View.GONE
            txtAddShippingAddress.visibility = View.VISIBLE
        }
    }

    private fun setSummary() {
        if (checkoutDataModel != null) {
           // println("Set cod price")

            txtSubtotalValue.text = Global.setPriceWithCurrency(
                this,
                checkoutDataModel.sub_total.toString()
            )
            subtotal = checkoutDataModel.sub_total?.toDouble()

            if (!checkoutDataModel.delivery_charges.isNullOrEmpty() && checkoutDataModel.delivery_charges?.toDouble()!! > 0) {
                txtDelivery.text = Global.setPriceWithCurrency(
                    this,
                    checkoutDataModel.delivery_charges.toString()
                )
                delivery = checkoutDataModel.delivery_charges?.toDouble()
            } else {
                delivery = checkoutDataModel.delivery_charges?.toDouble()

                txtDelivery.text = resources.getString(R.string.free).toUpperCase()
            }
            if (!checkoutDataModel.vat_charges.isNullOrEmpty() && checkoutDataModel.vat_charges?.toDouble()!! > 0) {
                relVat.visibility = View.VISIBLE
                txtVatCharges.text = Global.setPriceWithCurrency(
                    this,
                    checkoutDataModel.vat_charges.toString()
                )
                vat = checkoutDataModel.vat_charges?.toDouble()
            } else {
                relVat.visibility = View.GONE
                vat = 0.0
            }
          //  println("cod added :" + isCodAdded)
            if (isCodSelected) {
                if (!checkoutDataModel.cod_cost.isNullOrEmpty() && checkoutDataModel.cod_cost?.toDouble()!! > 0) {
                    relCod.visibility = View.VISIBLE
                    txtCod.text = Global.setPriceWithCurrency(
                        this,
                        checkoutDataModel.cod_cost.toString()
                    )
                    cod = checkoutDataModel.cod_cost?.toDouble()
                } else {
                    cod = 0.0
                    relCod.visibility = View.GONE
                }
            } else {
                cod = 0.0
                relCod.visibility = View.GONE
            }

            if (!checkoutDataModel.discount_price.isNullOrEmpty() && checkoutDataModel.discount_price?.toDouble()!! > 0) {
                txtDiscount.text = Global.setPriceWithCurrency(
                    this,
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

            if (isCodSelected) {
                grandTotal =
                    subtotal as Double + delivery as Double + vat as Double + cod as Double - discount as Double

            } else {
                grandTotal =
                    subtotal as Double + delivery as Double + vat as Double - discount as Double

            }
          //  println("grand total: " + grandTotal)
            txtTotalValue.text =
                Global.setPriceWithCurrency(this, grandTotal.toString())
        }


    }

    private fun showNoAddressAlert() {

        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_alert_no_address)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window?.attributes = lp
            dialog.setCanceledOnTouchOutside(false)
            dialog.txtAlertTitle.text = title
            dialog.txtAlertMessage.text = resources.getString(R.string.no_address_linked)
            dialog.btnAlertNegative.text = resources.getString(R.string.ok)
            dialog.btnAlertPositive.text = ""
            dialog.ivAlertImage.setImageResource(R.drawable.ic_info)

            dialog.txtAlertMessage.typeface = Global.fontRegular
            dialog.txtAlertTitle.typeface = Global.fontSemiBold
            dialog.btnAlertNegative.typeface = Global.fontBtn
            dialog.btnAlertPositive.typeface = Global.fontBtn
            dialog.btnAlertPositive.visibility = GONE
            dialog.btnAlertNegative.setOnClickListener() {
                dialog.dismiss()
                val intent = Intent(
                    this@CheckoutActivity,
                    com.app.blockaat.address.AddAddressActivity::class.java
                )
                intent.putExtra("isFromCheckoutChangeAddress", "yes")
                intent.putExtra("orderId", checkoutDataModel.cart?.id)
                startActivityForResult(intent, ADD_ADDRESS_REQUEST)
            }
            dialog.btnAlertPositive.setOnClickListener() {
                dialog.dismiss()

            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setItems() {
        val list = checkoutDataModel?.cart?.items as ArrayList<CheckoutItemItemModel>
        list.let { it -> arrListItems.addAll(it) }
        itemsAdapter.notifyDataSetChanged()
    }

    private fun setFonts() {

        txtHead.typeface = Global.fontNavBar
        txtShippingAddress.typeface = Global.fontMedium
        txtItemsInfo.typeface = Global.fontMedium
        txtPaymentInfo.typeface = Global.fontMedium
        txtPriceInfo.typeface = Global.fontMedium
        txtAddress.typeface = Global.fontRegular
        txtMobile.typeface = Global.fontMedium
        txtPlaceOrder.typeface = Global.fontBtn
        txtSubtotalLabel.typeface = Global.fontRegular
        txtDiscountLabel.typeface = Global.fontRegular
        txtCodLabel.typeface = Global.fontRegular
        txtVatLabel.typeface = Global.fontRegular
        txtDeliveryLabel.typeface = Global.fontRegular
        txtTotalLabel.typeface = Global.fontNavBar
        txtSubtotalValue.typeface = Global.fontPriceMedium
        txtDiscount.typeface = Global.fontPriceMedium
        txtCod.typeface = Global.fontPriceMedium
        txtVatCharges.typeface = Global.fontPriceMedium
        txtDelivery.typeface = Global.fontPriceMedium
        txtTotalValue.typeface = Global.fontNavBar
        txtAddShippingAddress.typeface = Global.fontBtn
        edtPromoCode.typeface = Global.fontRegular
        txtPromoCode.typeface = Global.fontMedium
        txtApply.typeface = Global.fontBtn
    }

    private fun setAddressAndTime(addressModel: com.app.blockaat.address.model.AddressListingDataModel) {
        if (addressModel != null) {
            strAddressID = addressModel?.address_id.toString()
            txtAddShippingAddress.visibility = GONE
            relAddress.visibility = VISIBLE
            txtAddress.text = Global.getFormattedAddressForListingWithLabel(
                activity = this,
                notes = addressModel?.notes ?: "",
                flat = addressModel?.flat_no ?: "",
                floor = addressModel.floor_no ?: "",
                building_no = addressModel?.building ?: "",
                street = addressModel?.street ?: "",
                jaddah = addressModel?.jaddah ?: "",
                block = addressModel?.block_name ?: "",
                area = addressModel?.area_name ?: "",
                governorate = addressModel?.governorate_name ?: "",
                country = addressModel?.country_name ?: ""
            )
            if(Global.getStringFromSharedPref(this@CheckoutActivity,Constants.PREFS_USER_PHONE_CODE).isNullOrEmpty())
            {
                txtMobile.text ="+"+addressModel?.phonecode+" "+ addressModel?.mobile_number ?: ""
            }else
            {
                if(Global.getStringFromSharedPref(this@CheckoutActivity,Constants.PREFS_USER_PHONE_CODE).contains("+"))
                {
                    txtMobile.text =Global.getStringFromSharedPref(this@CheckoutActivity,Constants.PREFS_USER_PHONE_CODE)+" "+ addressModel?.mobile_number ?: ""
                }else
                {
                    txtMobile.text ="+"+Global.getStringFromSharedPref(this@CheckoutActivity,Constants.PREFS_USER_PHONE_CODE)+" "+ addressModel?.mobile_number ?: ""

                }
            }

        } else {
            relAddress.visibility = View.GONE
            txtAddShippingAddress.visibility = View.GONE
        }
    }

    private fun setPaymentMethods() {
        arrListPayments.clear()
        val arrayList = checkoutDataModel.payment_types as ArrayList<CheckoutItemPaymentTypeModel>
        if (!strPaymentCode.isNullOrEmpty()) {
            for (i in 0 until arrayList.size) {
                if (arrayList[i].code.toString()
                        .toLowerCase() == strPaymentCode?.toLowerCase()
                ) {
                    arrayList[i].isSelected = true
                    strPaymentType = arrayList[i].type ?: ""
                }
            }
        }
        arrayList.let { it -> arrListPayments.addAll(it) }
        paymentAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ADDRESS_REQUEST && resultCode == EDIT_ADDRESS_RESULT && data != null) {
            val p =
                data.getSerializableExtra("shipToThisAddData") as com.app.blockaat.address.model.AddressListingDataModel

            checkoutDataModel.default_address = p
            checkoutDataModel.delivery_charges = p.shipping_cost
            checkoutDataModel.cod_cost = p.cod_cost
            checkoutDataModel.vat_charges = p.vat_charges
            checkoutDataModel.vat_pct = p.vat_pct
            //update existing address
            relAddress.visibility = View.VISIBLE
            strAddressID = p.address_id.toString()
            setAddressAndTime(p)
            isMoveToPayment = false
            checkItemStock()

        } else if (requestCode == ADD_ADDRESS_REQUEST && resultCode == ADD_ADDRESS_RESULT && data != null) {
            //coming from add address - as new user no address so add address

            val p =
                data.getSerializableExtra("shipToThisAddData") as com.app.blockaat.address.model.AddressListingDataModel

            checkoutDataModel.default_address = p
            checkoutDataModel.delivery_charges = p.shipping_cost
            checkoutDataModel.cod_cost = p.cod_cost
            checkoutDataModel.vat_charges = p.vat_charges
            checkoutDataModel.vat_pct = p.vat_pct

            //update existing address
            relAddress.visibility = View.VISIBLE
            strAddressID = p.address_id.toString()
            setAddressAndTime(p)
            isMoveToPayment = false

            checkItemStock()

        }

    }


    @SuppressLint("CheckResult")
    private fun placeOrder() {

        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            Global.apiService.checkout(
                Global.getStringFromSharedPref(this, Constants.PREFS_USER_ID),
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
                    this,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
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
                         //   println("RESPONSE - CheckOut Ws :   " + Gson().toJson(result.data))
                            if (result.status == 200) {
                                Global.strDeliveryAddressId = ""
                                if (result.data != null && result.data.payment_url.toString()
                                        .isNullOrEmpty()
                                ) {
                                    //COD - take to order summary page

                                    val i = Intent(
                                        this,
                                        OrderSummaryActivity::class.java
                                    )
                                    i.putExtra("orderSummaryData", result.data)
                                    i.putExtra("paymentType", strPaymentCode)
                                    startActivity(i)
                                    finish()

                                } else if (result.data != null) {
                                    // visa/k-net take to payment page
                                    val i =
                                        Intent(this, PaymentWebActivity::class.java)
                                    i.putExtra("orderSummaryData", result.data)
                                    i.putExtra("paymentType", strPaymentCode)
                                    startActivity(i)

                                }
                            } else {
                                //some error in response
                                Global.showSnackbar(this, result.message.toString())
                            }
                        } else {
                            //ws not working
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        hideProgressDialog()
                       // println("ERROR - CheckOut Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )

                    }
                )
        }
    }

    private fun checkItemStock() {
        val strMultipleCartEntityIDs = productsDBHelper.getAllCartEntityIDs().toString()
            .substring(1, productsDBHelper.getAllCartEntityIDs().toString().length - 1)
        val strMultipleCartProductQty = productsDBHelper.getAllCartProductQty().toString()
            .substring(1, productsDBHelper.getAllCartProductQty().toString().length - 1)
        val strMultipleCartOrderItemId = productsDBHelper.getAllOrderId().toString()
            .substring(1, productsDBHelper.getAllOrderId().toString().length - 1)

        if (NetworkUtil.getConnectivityStatus(this@CheckoutActivity) != 0) {
            showProgressDialog(this@CheckoutActivity)
            val checkItemStock = CheckItemStockRequest(
                Global.getStringFromSharedPref(this@CheckoutActivity, Constants.PREFS_USER_ID),
                strMultipleCartEntityIDs,
                strMultipleCartProductQty,
                checkoutDataModel.cart?.id,
                strMultipleCartOrderItemId,
                strAddressID
            )
            disposable = Global.apiService.checkItemStock(
                checkItemStock,
                com.app.blockaat.apimanager.WebServices.CheckItemStockWs + Global.getStringFromSharedPref(
                    this@CheckoutActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&store=" + Global.getStringFromSharedPref(
                    this@CheckoutActivity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                      //  println("RESPONSE - check-item-stock Ws :   " + Gson().toJson(result))
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                checkoutDataModel = result.data as CheckoutItemDataModel
                                setData()
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@CheckoutActivity,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                        hideProgressDialog()
                       // println("ERROR - check-item-stock Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this@CheckoutActivity,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }


    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    //below is inbuilt function of android to manage activity toolbar arrow
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            if (dialog!=null){
                hideProgressDialog()
            }
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
        disposable?.dispose()
        if (dialog!=null){
            hideProgressDialog()
        }
    }

    override fun onBackPressed() {
        // cancelCheckoutApi()
        if (dialog!=null){
            hideProgressDialog()
        }
        intent.putExtra("shipToThisAddData", checkoutDataModel)
        setResult(PROMOCODE_UPDATE_RESULT, intent)
        finish()//finishing activity
    }


    private fun cancelCheckoutApi() {
        if (NetworkUtil.getConnectivityStatus(this@CheckoutActivity) != 0) {
            val a = checkoutDataModel.cart!!.id
            showProgressDialog(this@CheckoutActivity)
            disposable = Global.apiService.cancelCheckout(
                com.app.blockaat.apimanager.WebServices.CancelCheckoutWs + a + "&user_id=" + Global.getStringFromSharedPref(
                    this@CheckoutActivity,
                    Constants.PREFS_USER_ID
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            hideProgressDialog()
                         //   println("RESPONSE - Cancel Checkout Ws :   " + result.data)
                        }
                        finish()
                    },
                    { error ->
                       // println("ERROR - Cancel Checkout Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        finish()
                    }
                )
        }
    }

    override fun onPaymentClicked(position: Int) {
        for (i in 0 until arrListPayments.size) {
            arrListPayments[i].isSelected = false
        }
        strPaymentType = arrListPayments[position].type ?: ""
        strPaymentCode = arrListPayments[position].code ?: ""
        arrListPayments[position].isSelected = true
        if (strPaymentCode == "C") {
            isCodSelected = true
            setSummary()

        }
        paymentAdapter?.notifyDataSetChanged()
    }
}