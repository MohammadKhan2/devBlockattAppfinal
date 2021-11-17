package com.app.blockaat.addressListingCart

import androidx.appcompat.app.AppCompatActivity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.address.AddAddressActivity
import com.app.blockaat.address.interfaceaddress.UpdateAddressItemEvent
import com.app.blockaat.address.model.AddressListingDataModel
import com.app.blockaat.addressListingCart.adapter.AddressFromCartAdapter
import com.app.blockaat.addressListingCart.interfaceaddresscart.UpdateAddressFromCartItemEvent
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.checkout.activity.CheckoutActivity
import com.app.blockaat.helper.*
import com.app.blockaat.orders.model.CheckoutItemDataModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_address_listing_cart.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.ArrayList

class AddressListingCartActivity : BaseActivity() {

    private val ADD_ADDRESS_REQUEST: Int = 100
    private val ADD_ADDRESS_RESULT: Int = 101
    private lateinit var checkoutDataModel: CheckoutItemDataModel
    private lateinit var progressDialog: Dialog
    private lateinit var adapterAddressList: AddressFromCartAdapter
    private var disposable: Disposable? = null
    private lateinit var arrListAddressData: ArrayList<AddressListingDataModel>
    private var isFromCheckoutChangeAddress: Boolean = false
    private var strOrderId: String? = ""
    private var isFromRefresh: Boolean? = false
    private var arrListAddressListData: ArrayList<AddressListingDataModel>? = ArrayList()
    private lateinit var addressListingDataModel: AddressListingDataModel
    private var mToolbar: Toolbar? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_listing_cart)

        Global.setLocale(this@AddressListingCartActivity)
        initializeToolbar()
        setFonts()
        initializeFields()
        onCLickListeners()
    }

    private fun onCLickListeners() {
        txtProceedToPurchase.setOnClickListener {
            val i = Intent(this@AddressListingCartActivity, CheckoutActivity::class.java)
            i.putExtra("checkoutData", checkoutDataModel)
            i.putExtra("addressData", addressListingDataModel)
            startActivity(i)
        }
        imgFilter.setOnClickListener {
            val intent = Intent(this@AddressListingCartActivity, AddAddressActivity::class.java)
            intent.putExtra("strOrderID", checkoutDataModel.cart!!.id)
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }



    }

    private fun initializeFields() {
        checkoutDataModel = intent.getSerializableExtra("checkoutData") as CheckoutItemDataModel
        strOrderId = checkoutDataModel.cart!!.id
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@AddressListingCartActivity)
        (rvCartAddressListing as RecyclerView).layoutManager = layoutManager
        (rvCartAddressListing as RecyclerView).isNestedScrollingEnabled = false
//        txtSave.visibility = View.GONE
        getAddressListing()

    }

    private fun setFonts() {

        txtHead.typeface = Global.fontNavBar
        txtProceedToPurchase.typeface = Global.fontBtn
        txtAddressStep.typeface = Global.fontSemiBold
        txtAddressLabel.typeface = Global.fontSemiBold
        txtChangeAddress.typeface = Global.fontRegular
    }


    private fun initializeToolbar() {

        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.checkout_2_of_3)
    }

    private fun getAddressListing() {
        if (NetworkUtil.getConnectivityStatus(this@AddressListingCartActivity) != 0) {
            if (!isFromRefresh!!) {
                showProgressDialog(this@AddressListingCartActivity)
            }
            disposable = Global.apiService.getAddressList(
                WebServices.AddressListingWs + Global.getStringFromSharedPref(
                    this@AddressListingCartActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&user_id=" + Global.getStringFromSharedPref(
                    this@AddressListingCartActivity,
                    Constants.PREFS_USER_ID
                ) + "&store=" + Global.getStringFromSharedPref(
                    this@AddressListingCartActivity,
                    Constants.PREFS_STORE_CODE
                ) + "&order_id=" + strOrderId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                          //  println("RESPONSE - Address List Ws :   " + result.data)
                            if (result.status == 200) {
                                if (result.data != null && result.data.size != 0) {
                                    relAddressBook.visibility = View.VISIBLE
                                    rvCartAddressListing.visibility = View.VISIBLE
                                    linNoItems!!.visibility = View.GONE
                                    arrListAddressData = result.data
                                    adapterAddressList =
                                        AddressFromCartAdapter(
                                            this@AddressListingCartActivity,
                                            arrListAddressData,
                                            onAddressUpdateClicked,
                                            onAddressDeleteClicked
                                        )
                                    (rvCartAddressListing as RecyclerView).adapter =
                                        adapterAddressList

                                    addressListingDataModel = arrListAddressData[0]
                                } else {
                                    //empty address list
                                    relAddressBook.visibility = View.GONE
                                    rvCartAddressListing.visibility = View.GONE
                                    linNoItems!!.visibility = View.VISIBLE
                                }
                            } else {
                                relAddressBook.visibility = View.GONE
                                rvCartAddressListing.visibility = View.GONE
                                linNoItems!!.visibility = View.VISIBLE
                            }
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(
                                this@AddressListingCartActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                       // println("ERROR - Address List :   " + error.localizedMessage)
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        Global.showSnackbar(
                            this@AddressListingCartActivity,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_ADDRESS_REQUEST && resultCode == ADD_ADDRESS_RESULT) {
            getAddressListing()
        }
    }

    private val onAddressUpdateClicked = object :
        UpdateAddressFromCartItemEvent {
        override fun onAddressUpdateClicked(position: Int, type: String) {
          //  println("HERE I AM ITEM CLICKED 2")
            if (adapterAddressList != null && arrListAddressData != null) {
                for (a in 0 until arrListAddressData.size) {
                    //It's an if-else condition
                    arrListAddressData[position].isSelected = a == position
                    if (a == position) {
                        addressListingDataModel = arrListAddressData[position]
                    }
                }
                adapterAddressList.notifyDataSetChanged()
            }
        }
    }

    private val onAddressDeleteClicked = object :
        UpdateAddressItemEvent {
        override fun onAddressUpdateClicked(
            data: ArrayList<AddressListingDataModel>,
            type: String
        ) {
            if (adapterAddressList != null) {
                arrListAddressData.clear()
                arrListAddressData.addAll(data)
                adapterAddressList.notifyDataSetChanged()
            }
        }
    }


    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog = ProgressDialog(activity)
        dialog!!.setMessage(resources.getString(R.string.loading))
        dialog!!.show()
    }

    private fun hideProgressDialog() {
        if (dialog!!.isShowing) {
            dialog!!.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
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