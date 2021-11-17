package com.app.blockaat.address

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.address.adapter.AddressAdapter
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.activity_address_book.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

class AddressListingActivity : BaseActivity() {

    private val ADD_ADDRESS_RESULT: Int = 101
    private val EDIT_ADDRESS_RESULT: Int = 201
    private val ADD_ADDRESS_REQUEST: Int = 100
    private val EDIT_ADDRESS_REQUEST: Int = 200
    private lateinit var adapterAddressList: AddressAdapter
    private var disposable: Disposable? = null
    private var arrListAddressData: ArrayList<com.app.blockaat.address.model.AddressListingDataModel> =
        arrayListOf()
    private var isFromCheckoutChangeAddress: Boolean = false
    private var strOrderId: String? = ""
    private var strSelectedAddressId: String? = ""
    private var isFromRefresh: Boolean = false
    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_book)

        Global.setLocale(this)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
    }


    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        ivClose.visibility = GONE
        txtHead.text = resources.getString(R.string.shipping_addresses)
        txtSave.visibility = View.VISIBLE
    }

    private fun initializeFields() {
        ivEmpty.setImageResource(R.drawable.ic_empty_address)
        txtEmptyMessage.visibility = View.GONE
     /*   txtEmptyMessage.text = getString(R.string.empty_address_msg)*/
        txtEmptyTitle.text = getString(R.string.empty_address_title)
        txtEmptyBtn.text = getString(R.string.add_new_addres)
        rvAddressListing!!.isNestedScrollingEnabled = false
        isFromCheckoutChangeAddress =
            intent.hasExtra("isFromCheckoutChangeAddress") && intent.getStringExtra("isFromCheckoutChangeAddress") == "yes"
        if (isFromCheckoutChangeAddress) {
            txtAddAddress.visibility = GONE
        } else {
            txtAddAddress.visibility = VISIBLE
        }
        //getting order id
        if (intent.hasExtra("orderId") && !intent.getStringExtra("orderId").isNullOrEmpty()) {
            strOrderId = intent.getStringExtra("orderId")
        } else {
            strOrderId = ""
        }

        if (intent.hasExtra("selectedAddress") && !intent.getStringExtra("selectedAddress")
                .isNullOrEmpty()
        ) {
            strSelectedAddressId = intent.getStringExtra("selectedAddress")
        } else {
            strSelectedAddressId = ""
        }

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        rvAddressListing.layoutManager = layoutManager
        adapterAddressList = com.app.blockaat.address.adapter.AddressAdapter(
            this,
            arrListAddressData,
            isFromCheckoutChangeAddress,
            strSelectedAddressId ?: "",
            interfaceAddress
        )
        rvAddressListing.adapter = adapterAddressList
        getAddressListing()
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontBold
        txtAddAddress.typeface = Global.fontBold
        txtEmptyMessage.typeface = Global.fontRegular
        txtEmptyTitle.typeface = Global.fontMedium
        txtEmptyBtn.typeface = Global.fontBtn
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtAddAddress.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra("isEditAddress", "no")
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }

        txtSave.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra("isEditAddress", "no")
            startActivity(intent)
        }

        swipeRefreshLayout.setOnRefreshListener {
            isFromRefresh = true
            getAddressListing()
        }


        rvAddressListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(
                        0
                    ).top
                swipeRefreshLayout.isEnabled = topRowVerticalPosition >= 0
            }

        })

        txtEmptyBtn.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra("isEditAddress", "no")
            startActivityForResult(intent, ADD_ADDRESS_REQUEST)
        }
    }

    //fetch address list
    private fun getAddressListing() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getAddressList(
                com.app.blockaat.apimanager.WebServices.AddressListingWs
                        + Global.getLanguage(this)
                        + "&user_id=" + Global.getUserId(this)
                        + "&store=" + Global.getStoreCode(this)
                        + "&order_id=" + strOrderId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null && result.data.size != 0) {
                                    rvAddressListing.visibility = View.VISIBLE
                                    txtAddAddress.visibility =
                                        if (isFromCheckoutChangeAddress) GONE else VISIBLE
                                    relEmpty.visibility = View.GONE
                                    txtSave.visibility = View.GONE
                                    txtAddAddress.visibility = View.VISIBLE
                                    arrListAddressData.clear()
                                    result.data.let { arrListAddressData.addAll(it) }
                                    adapterAddressList.notifyDataSetChanged()
                                } else {
                                    //empty address list
                                    txtAddAddress.visibility = GONE
                                    txtSave.visibility = View.VISIBLE
                                    rvAddressListing.visibility = View.GONE
                                    relEmpty.visibility = View.VISIBLE
                                }
                            } else {
                                txtAddAddress.visibility = GONE
                                txtSave.visibility = View.VISIBLE
                                rvAddressListing.visibility = View.GONE
                                relEmpty.visibility = View.VISIBLE
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                        reladdresslist.visibility = View.VISIBLE
                    },
                    { error ->
                       // println("ERROR - Address List :   " + error.localizedMessage)
                        relEmpty.visibility = View.VISIBLE
                        hideProgressDialog()
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    private val interfaceAddress = object :
        com.app.blockaat.address.interfaceaddress.AddressInterface {
        override fun onItemClick(position: Int, type: String) {
            when (type) {
                "editAddress" -> {
                    val i = Intent(
                        this@AddressListingActivity,
                        com.app.blockaat.address.AddAddressActivity::class.java
                    )
                    i.putExtra("isEditAddress", "yes")
                    i.putExtra("addressData", arrListAddressData[position])
                    startActivityForResult(i, EDIT_ADDRESS_REQUEST)
                }
                "deleteAddress" -> {
                    Global.showAlert(
                        this@AddressListingActivity,
                        "",
                        resources.getString(R.string.delete_address_msg),
                        resources.getString(R.string.yes),
                        resources.getString(R.string.no),
                        false,
                        R.drawable.ic_alert,
                        object : AlertDialogInterface {
                            override fun onYesClick() {
                                deleteAddress(position)
                            }

                            override fun onNoClick() {
                            }
                        })
                }
                "defaultAddress" -> {
                    setAsDefaultAddress(position)
                }
                "selectAddress" -> {
                    if (isFromCheckoutChangeAddress) {
                        val intent = Intent()
                        intent.putExtra("shipToThisAddData", arrListAddressData[position])
                        setResult(EDIT_ADDRESS_RESULT, intent)
                        finish()//finishing activity
                    }
                }
            }
        }

    }

    @SuppressLint("CheckResult")
    private fun deleteAddress(
        position: Int
    ) {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog()
            Global.apiService.deleteAddress(
                Global.getUserId(this),
                arrListAddressData[position].address_id.toString(),
                com.app.blockaat.apimanager.WebServices.DeleteAddressWs + Global.getLanguage(this)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - DeleteAddress Ws :   " + Gson().toJson(result))
                        if (result != null) {
                            hideProgressDialog()
                            if (result.status == 200) {
                                if (!result.data.isNullOrEmpty()) {
                                    arrListAddressData.clear()
                                    result.data.let { arrListAddressData.addAll(it) }
                                    adapterAddressList?.notifyDataSetChanged()
                                }
                            } else {
                                Global.showSnackbar(this, result.message!!)
                            }
                        } else {
                            hideProgressDialog()
                            //if ws not working
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        hideProgressDialog()
                       // println("ERROR - DeleteAddress Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    /* private val onAddressUpdateClicked = object : UpdateAddressItemEvent {
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
     }*/

    /* private val onAddressSelected = object : OnAddressSelectedListener {
         override fun onAddressSelected(position: Int) {
             val i = Intent()
             i.putExtra("shipToThisAddData", arrListAddressData[position])
             setResult(Activity.RESULT_OK, i)
             finish()
         }

     }*/

    @SuppressLint("CheckResult")
    private fun setAsDefaultAddress(
        position: Int
    ) {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            //GlobalshowProgressDialog(activity as Activity)
            showProgressDialog()
            Global.apiService.setAsDefaultAddress(
                Global.getUserId(this),
                arrListAddressData[position].address_id.toString(),
                com.app.blockaat.apimanager.WebServices.setAsDefaultAddressWs + Global.getLanguage(
                    this
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - setAsDefaultAddress Ws :   " + Gson().toJson(result))
                        if (result != null) {
                            hideProgressDialog()
                            if (result.status == 200) {
                                if (!result.data.isNullOrEmpty()) {
                                    arrListAddressData.clear()
                                    result.data.let { arrListAddressData.addAll(it) }
                                    adapterAddressList?.notifyDataSetChanged()
                                }
                            } else {
                                Global.showSnackbar(this, result.message!!)
                            }
                        } else {
                            hideProgressDialog()
                            //if ws not working
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                       // println("ERROR - setAsDefaultAddress Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    fun showProgressDialog() {
        if (!isFromRefresh) {
            dialog = CustomProgressBar(this)
            dialog?.showDialog()
        }
    }

    fun hideProgressDialog() {
        if (!isFromRefresh) {
            isFromRefresh = false
            dialog?.hideDialog()
        } else {
            swipeRefreshLayout?.isRefreshing = false
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

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_ADDRESS_REQUEST && resultCode == EDIT_ADDRESS_RESULT) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.address_successfully_updated)
            )
            getAddressListing()
        } else if (requestCode == ADD_ADDRESS_REQUEST && resultCode == ADD_ADDRESS_RESULT) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.add_successfully_added)
            )
            getAddressListing()
        }
    }

}
