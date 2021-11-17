package com.app.blockaat.checkout.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.app.blockaat.R
import com.app.blockaat.address.AddressListingActivity
import com.app.blockaat.address.model.AddressListingDataModel
import com.app.blockaat.cart.model.CheckItemStockRequest
import com.app.blockaat.helper.*
import com.app.blockaat.orders.model.CheckoutItemDataModel
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_address_checkout.*
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.toolbar_default.*

class AddressCheckoutActivity : BaseActivity() {
    private var strAddressID: String = ""
    private var strOrderId: String = ""
    private lateinit var checkoutDataModel: CheckoutItemDataModel
    private val EDIT_ADDRESS_REQUEST: Int = 200
    private val PROMOCODE_UPDATE_REQUEST: Int = 300
    private val PROMOCODE_UPDATE_RESULT: Int = 301
    private val ADD_ADDRESS_REQUEST: Int = 100
    private val ADD_ADDRESS_RESULT: Int = 101
    private val EDIT_ADDRESS_RESULT: Int = 201
    private var dialog: CustomProgressBar? = null
    private lateinit var productsDBHelper: DBHelper
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_checkout)


        Global.setLocale(this)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.checkout_2_of_3)
    }

    private fun initializeFields() {

        productsDBHelper = DBHelper(this)
        dialog = CustomProgressBar(this)
        ivEmpty.setImageResource(R.drawable.ic_empty_address)
        txtEmptyMessage.text = getString(R.string.empty_address_msg)
        txtEmptyTitle.text = getString(R.string.empty_address_title)
        txtEmptyBtn.text = getString(R.string.add_new_addres)


        if (intent.hasExtra("checkoutData")) {
            checkoutDataModel = intent.getSerializableExtra("checkoutData") as CheckoutItemDataModel
            strOrderId = checkoutDataModel.cart!!.id ?: ""
            if (!checkoutDataModel?.default_address?.address_id.isNullOrEmpty()) {
                linBottom.visibility = VISIBLE
                linData.visibility = VISIBLE
                setAddress(checkoutDataModel?.default_address)
                setPrices()
            } else {
                // showNoAddressAlert()
                showAddAddress()
                linBottom.visibility = GONE
                linData.visibility = GONE
            }
        }

    }


    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtEdit.typeface = Global.fontBold
        txtAddAddress.typeface = Global.fontBold
        txtName.typeface = Global.fontMedium
        txtAddress.typeface = Global.fontRegular
        txtMobile.typeface = Global.fontMedium
        txtSelectedAddress.typeface = Global.fontMedium
        txtSubTotalLabel.typeface = Global.fontRegular
        txtSubtotal.typeface = Global.fontMedium
        txtVatLabel.typeface = Global.fontRegular
        txtVatCharges.typeface = Global.fontMedium
        txtTotalLabel.typeface = Global.fontRegular
        txtTotal.typeface = Global.fontNavBar
        txtProceedToCheckout.typeface = Global.fontBtn
        txtEmptyMessage.typeface = Global.fontRegular
        txtEmptyTitle.typeface = Global.fontMedium
        txtEmptyBtn.typeface = Global.fontBtn

    }

    private fun onClickListeners() {
        txtEdit.setOnClickListener {
            val intent = Intent(
                this,
                AddressListingActivity::class.java
            )
            intent.putExtra("isFromCheckoutChangeAddress", "yes")
            intent.putExtra("orderId", checkoutDataModel.cart?.id)
            intent.putExtra("selectedAddress", strAddressID ?: "")
            startActivityForResult(intent, EDIT_ADDRESS_REQUEST)
        }
        txtAddAddress.setOnClickListener {
            val intent = Intent(
                this,
                com.app.blockaat.address.AddAddressActivity::class.java
            )
            intent.putExtra("isFromCheckoutChangeAddress", "yes")
            intent.putExtra("orderId", checkoutDataModel.cart?.id)
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }

        txtEmptyBtn.setOnClickListener {
            val intent = Intent(
                this,
                com.app.blockaat.address.AddAddressActivity::class.java
            )
            intent.putExtra("isFromCheckoutChangeAddress", "yes")
            intent.putExtra("orderId", checkoutDataModel.cart?.id)
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }
        txtProceedToCheckout.setOnClickListener {
            if (!strAddressID.isNullOrEmpty()) {
                val i = Intent(this, CheckoutActivity::class.java)
                i.putExtra("checkoutData", checkoutDataModel)
                startActivityForResult(i,PROMOCODE_UPDATE_REQUEST)
            } else {
                Global.showSnackbar(this, getString(R.string.plz_select_aaddress))
            }
        }

        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showAddAddress() {
        val intent = Intent(
            this,
            com.app.blockaat.address.AddAddressActivity::class.java
        )
        intent.putExtra("isFromCheckoutChangeAddress", "yes")
        intent.putExtra("orderId", checkoutDataModel.cart?.id)
        startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        finish()
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
                    this,
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


    private fun checkItemStock() {
        val strMultipleCartEntityIDs = productsDBHelper.getAllCartEntityIDs().toString()
            .substring(1, productsDBHelper.getAllCartEntityIDs().toString().length - 1)
        val strMultipleCartProductQty = productsDBHelper.getAllCartProductQty().toString()
            .substring(1, productsDBHelper.getAllCartProductQty().toString().length - 1)
        val strMultipleCartOrderItemId = productsDBHelper.getAllOrderId().toString()
            .substring(1, productsDBHelper.getAllOrderId().toString().length - 1)

        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            val checkItemStock = CheckItemStockRequest(
                Global.getStringFromSharedPref(this, Constants.PREFS_USER_ID),
                strMultipleCartEntityIDs,
                strMultipleCartProductQty,
                checkoutDataModel.cart?.id,
                strMultipleCartOrderItemId,
                strAddressID
            )
            disposable = Global.apiService.checkItemStock(
                checkItemStock,
                com.app.blockaat.apimanager.WebServices.CheckItemStockWs + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_LANGUAGE
                ) + "&store=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                    //    println("RESPONSE - check-item-stock Ws :   " + Gson().toJson(result))
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                checkoutDataModel = result.data as CheckoutItemDataModel
                                setPrices()
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                        hideProgressDialog()
                      //  println("ERROR - check-item-stock Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    private fun setPrices() {
        //subtotal
        if (Global.stringToDouble(checkoutDataModel?.sub_total) > 0) {
            relSubTotal.visibility = VISIBLE
            txtSubtotal.text = Global.setPriceWithCurrency(
                this,
                checkoutDataModel.sub_total.toString()
            )
        } else {
            relSubTotal.visibility = GONE
        }

        //DeliveryCharges
        if (Global.stringToDouble(checkoutDataModel?.delivery_charges) > 0) {
            relDeliveryCharge.visibility = VISIBLE
            txtDeliveryCharge.text = Global.setPriceWithCurrency(
                this,
                checkoutDataModel.delivery_charges.toString()
            )
        } else {
            relDeliveryCharge.visibility = GONE
        }

        //Discount
        if (Global.stringToDouble(checkoutDataModel?.discount_price) > 0) {
            relDiscount.visibility = VISIBLE
            txtDiscount.text = Global.setPriceWithCurrency(
                this,
                checkoutDataModel.discount_price.toString()
            )
        } else {
            relDiscount.visibility = GONE
        }

        //total
        if (Global.stringToDouble(checkoutDataModel?.total) > 0) {
            relTotal.visibility = VISIBLE
            txtTotal.text = Global.setPriceWithCurrency(
                this,
                checkoutDataModel.total.toString()
            )
        } else {
            relTotal.visibility = GONE
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

    private fun setAddress(addressModel: AddressListingDataModel?) {
        if (addressModel != null) {
            strAddressID = addressModel?.address_id.toString()
            linAddress.visibility = View.VISIBLE
            linData.visibility = View.VISIBLE
            linBottom.visibility = View.VISIBLE
            relEmpty.visibility = View.GONE
            txtName.text = addressModel?.address_name ?: ""
            txtMobile.text = addressModel?.mobile_number ?: ""

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

        } else {
            linAddress.visibility = View.GONE
            linData.visibility = View.GONE
            linBottom.visibility = View.GONE
            relEmpty.visibility = View.VISIBLE

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == PROMOCODE_UPDATE_REQUEST && resultCode == PROMOCODE_UPDATE_RESULT && data != null) {
            val p =
                data.getSerializableExtra("shipToThisAddData") as CheckoutItemDataModel
            checkoutDataModel.delivery_charges = p.shipping_note
            checkoutDataModel.cod_cost = p.cod_cost
            checkoutDataModel.vat_charges = p.vat_charges
            checkoutDataModel.vat_pct = p.vat_pct
            //update existing address
            linAddress.visibility = View.VISIBLE
            checkItemStock()

        } else if (requestCode == EDIT_ADDRESS_REQUEST && resultCode == EDIT_ADDRESS_RESULT && data != null) {
            val p =
                data.getSerializableExtra("shipToThisAddData") as com.app.blockaat.address.model.AddressListingDataModel
            checkoutDataModel.default_address = p
            checkoutDataModel.delivery_charges = p.shipping_cost
            checkoutDataModel.cod_cost = p.cod_cost
            checkoutDataModel.vat_charges = p.vat_charges
            checkoutDataModel.vat_pct = p.vat_pct
            //update existing address
            linAddress.visibility = View.VISIBLE
            strAddressID = p.address_id.toString()
            setAddress(p)
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
            linAddress.visibility = View.VISIBLE
            strAddressID = p.address_id.toString()
            setAddress(p)
            checkItemStock()

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
        disposable?.dispose()
    }

}