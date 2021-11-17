package com.app.blockaat.address

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.app.blockaat.R
import com.app.blockaat.address.model.AddAddressRequest
import com.app.blockaat.address.model.AddressListingDataModel
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.helper.*
import com.app.blockaat.orders.model.CheckoutItemDataModel
import com.suke.widget.SwitchButton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_address.*
import kotlinx.android.synthetic.main.toolbar_default.*


class AddAddressActivity : BaseActivity() {
    private val EDIT_ADDRESS_RESULT: Int = 201
    private val ADD_ADDRESS_RESULT: Int = 101
    private var strStateID = ""
    private var strAreaID = ""
    private var strStateName = ""
    private var strBlockID = ""
    private var strJaddahID = ""
    private var strJaddahname = ""
    private var strAreaname = ""
    private var strBlockname = ""
    private var strCountryID = ""
    private var strCountryName = ""
    private var disposable: Disposable? = null
    private lateinit var editAddressDataModel: AddressListingDataModel
    private var isEditAddress = false
    private var strAddressID = ""
    private var strOrderId: String? = ""
    private var isFromCheckoutChangeAddress: Boolean = false
    private lateinit var checkoutDataModel: CheckoutItemDataModel
    private var strIsDefault = "1"
    private var strOrderID = ""
    private var strType = "0"
    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null
    private var hasArea = false
    private var hasBlock = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)


        Global.setLocale(this)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        isEditAddress()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        ivClose.visibility = View.GONE
        txtHead.text = resources.getString(R.string.add_address)

    }

    private fun initializeFields() {


        dialog = CustomProgressBar(this)
        if (intent.hasExtra("checkoutData") && intent.getSerializableExtra("checkoutData") != null) {
            checkoutDataModel = intent.getSerializableExtra("checkoutData") as CheckoutItemDataModel
        }
        isFromCheckoutChangeAddress =
            intent.hasExtra("isFromCheckoutChangeAddress") && intent.getStringExtra("isFromCheckoutChangeAddress") == "yes"

       // println("Check out ::" + isFromCheckoutChangeAddress)

        //getting order id
        if (intent.hasExtra("orderId") && !intent.getStringExtra("orderId").isNullOrEmpty()) {
            strOrderId = intent.getStringExtra("orderId")
        } else {
            strOrderId = ""
        }

        //While coming ffrom checkout
        if (intent.hasExtra("strOrderID") && intent.getStringExtra("strOrderID") != null) {
            strOrderID = intent.getStringExtra("strOrderID")!!
        }
        if (!Global.isEnglishLanguage(this)) {
            imgSelectCountryArrow.rotation = 0f
            imgAreaArrow.rotation = 0f
            imgStateArrow.rotation = 0f
            imgJadaArrow.rotation = 0f
            imgBlockArrow.rotation = 0f
        }

    }

    private fun setFonts() {
        txtHead.typeface = Global.fontBold
        edtFullName.typeface = Global.fontRegular
        edtPhoneNumber.typeface = Global.fontRegular
        txtCountry.typeface = Global.fontRegular
        txtGovernorate.typeface = Global.fontRegular
        txtArea.typeface = Global.fontRegular
        edtArea.typeface = Global.fontRegular
        txtBlock.typeface = Global.fontRegular
        edtBlock.typeface = Global.fontRegular
        edtStreet.typeface = Global.fontRegular
        txtJadaah.typeface = Global.fontRegular
        edtBuilding.typeface = Global.fontRegular
        edtFloor.typeface = Global.fontRegular
        edtFlatNo.typeface = Global.fontRegular
        btnSubmitAddress.typeface = Global.fontBold
        edtExtraDirection.typeface = Global.fontRegular
        txtDefault.typeface = Global.fontRegular
        rbHome.typeface = Global.fontMedium
        rbOffice.typeface = Global.fontMedium

    }


    private fun validation() {
        if (edtFullName.text.isEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.enter_name)
            )
        } else if (edtPhoneNumber.text.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.empty_phone_number)
            )
        } else if (edtPhoneNumber.text.toString().length < 8 || edtPhoneNumber.text.toString().length > 12) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.error_phone_length)
            )
        } else if (strCountryID.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.incorrect_country)
            )
        } else if (strStateID.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.incoorect_governorate)
            )
        } else if (hasArea && strAreaID.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.incorrect_area)
            )
        } else if (!hasArea && edtArea.text.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.plz_enter_area)
            )
        } else if (hasBlock && strBlockID.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.incorrect_block)
            )
        } else /*if (!hasBlock && edtBlock.text.isNullOrEmpty()) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.plz_enter_block)
            )
        } else*/
            if (!hasBlock && txtJadaah.text.isNullOrEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.incorrect_jadah_add)
                )
            } else if (edtStreet.text.isEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.incorrect_street_add)
                )
            } else if (edtBuilding.text.isEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.enter_building)
                )
            }
            else {
                if (isEditAddress) {
                    editAddressApi()
                } else {
                    addAddressApi()
                }
            }
          /*  else if (edtFloor.text.isEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.enter_flore)
                )
            }*/
            /*else if (edtFlatNo.text.isEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.enter_zone)
                )
            } */

    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        linSwitch.setOnClickListener {
            if (switchDefault.isChecked) {
                strIsDefault = "0"
                switchDefault.isChecked = false
            } else {
                strIsDefault = "1"
                switchDefault.isChecked = true
            }
        }
        btnSubmitAddress.setOnClickListener {
            validation()
        }

        relSelectCountry.setOnClickListener {
            val i = Intent(this, CountryListActivity::class.java)
            startActivityForResult(i, 103)
//            relState.isEnabled = true

        }

        rbHome.setOnClickListener {
            strType = "0"
            rbHome.isChecked = true
            rbOffice.isChecked = false
        }
        rbOffice.setOnClickListener {
            rbOffice.isChecked = true
            rbHome.isChecked = false
            strType = "1"
        }

        relState.setOnClickListener {
            if (!strCountryID.isNullOrEmpty()) {
                val i = Intent(this, StateListActivity::class.java)
                i.putExtra("stateID", strCountryID)
                startActivityForResult(i, 100)
            } else {
                Global.showSnackbar(this, getString(R.string.plz_select_country))
            }
//            relArea.isEnabled = true

        }

        relArea.setOnClickListener {
            if (!strStateID.isNullOrEmpty() && hasArea) {
                val i = Intent(this, AreaListActivity::class.java)
                i.putExtra("stateID", strStateID)
                startActivityForResult(i, 101)
            } else {
                Global.showSnackbar(this, getString(R.string.plz_select_state))
            }
//            relBlock.isEnabled = true
        }
        relBlock.setOnClickListener {
            if (!strAreaID.isNullOrEmpty() && hasBlock) {
                val i = Intent(this, BlockListActivity::class.java)
                i.putExtra("stateID", strAreaID)
                startActivityForResult(i, 102)
            } else {
                Global.showSnackbar(this, getString(R.string.plz_select_area))
            }
        }
        txtClose.setOnClickListener {
            onBackPressed()
        }

    }

    @SuppressLint("CheckResult")
    private fun addAddressApi() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            val addAddressRequest =
                AddAddressRequest(
                    Global.getUserId(this),
                    strOrderID,
                    edtFullName.text.toString(),
                    strCountryID,
                    strStateID,
                    strAreaID,
                    strAreaname,
                    strBlockID,
                    strBlockname,
                    edtPhoneNumber.text.toString(),
                    edtStreet.text.toString(),
                    txtJadaah.text.toString(),
                    edtBuilding.text.toString(),
                    edtFloor.text.toString(),
                    edtFlatNo.text.toString(),
                    edtExtraDirection.text.toString(),
                    strType,
                    strIsDefault
                )
            Global.apiService.addAddressUser(addAddressRequest, WebServices.AddAddressWs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {

                                Global.saveBooleanInSharedPref(this@AddAddressActivity, Constants.ADD_ADDRESS, true)

                                val intent = Intent()
                                intent.putExtra("shipToThisAddData", result.data)
                                setResult(ADD_ADDRESS_RESULT, intent)
                                finish()//finishing activity



                            } else {
                                Global.showSnackbar(this, result.message!!)
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
                       // println("ERROR - Add Address Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    private fun getCountryList() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            disposable = Global.apiService.getCountryList(
                WebServices.CountryListWs + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_LANGUAGE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                          //  println("RESPONSE - CountryList Ws :   " + result.data)
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    for (i in 0 until result.data.size) {
/*
                                        println(
                                            "Store code: " + Global.getStringFromSharedPref(
                                                this,
                                                Constants.PREFS_STORE_CODE
                                            )
                                        )
*/
                                        if (Global.getStringFromSharedPref(
                                                this,
                                                Constants.PREFS_STORE_CODE
                                            ) == result.data[i].iso
                                        ) {
                                            strCountryID = result.data[i].id.toString()
                                            txtCountry.text =
                                                Global.toCamelCase(
                                                    result.data[i].name?.trim().toString()
                                                )
                                            txtCountry.setTextColor(
                                                ContextCompat.getColor(
                                                    this,
                                                    R.color.primary_text_color
                                                )
                                            )

                                        }
                                    }
                                } else {
                                    //no data
                                }
                            } else {
                                Global.showSnackbar(this, result.message)
                            }


                        } else {
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                        hideProgressDialog()
                    },
                    { error ->
                        hideProgressDialog()
                      //  println("RESPONSE - CountryList Ws :   " + error.localizedMessage)
                    }
                )
        }
    }


    private fun editAddressApi() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            disposable = Global.apiService.editAddress(
                strAddressID,
                Global.getUserId(this),
                edtFullName.text.toString(),
                strCountryID.toInt(),
                strStateID,
                strAreaID,
                strAreaname,
                strBlockID,
                strBlockname,
                edtPhoneNumber.text.toString(),
                edtStreet.text.toString(),
                txtJadaah.text.toString(),
                edtBuilding.text.toString(),
                edtFloor.text.toString(),
                edtFlatNo.text.toString(),
                edtExtraDirection.text.toString(),
                strType,
                strIsDefault,
                WebServices.EditAddressWs + Global.getLanguage(this)
                        + "&store=" + Global.getStoreCode(this)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                //Global.showSnackbar(this, resources.getString(R.string.address_successfully_updated))
                                val intent = Intent()
                                setResult(EDIT_ADDRESS_RESULT, intent)
                                finish()
                            } else {
                                Global.showSnackbar(this, result.message)
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
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    private fun isEditAddress() {
        if (intent.hasExtra("isEditAddress") && intent.getStringExtra("isEditAddress") == "yes") {
            editAddressDataModel =
                intent.getSerializableExtra("addressData") as AddressListingDataModel
            isEditAddress = true
            strAddressID = editAddressDataModel.address_id.toString()
            setDataFromEditAddress(editAddressDataModel)
        } else {
            getCountryList()
        }
    }

    private fun setDataFromEditAddress(editAddressDataModel: com.app.blockaat.address.model.AddressListingDataModel) {
        strAddressID = editAddressDataModel.address_id.toString()
        edtFullName.setText(editAddressDataModel.address_name.toString())
        strCountryID = editAddressDataModel.country_id.toString()
        strCountryName = editAddressDataModel.country_name.toString()
        txtCountry.text = strCountryName

        strStateID = editAddressDataModel.governorate_id.toString()
        strStateName = editAddressDataModel.governorate_name.toString()
        txtGovernorate.text = strStateName

        strAreaID = editAddressDataModel.area_id.toString()
        strAreaname = editAddressDataModel.area_name.toString()

        strJaddahname = editAddressDataModel.jaddah.toString()
        txtJadaah.setText(strJaddahname)

        txtArea.text = strAreaname
        edtArea.setText(strBlockname)
        if (!strAreaID.isNullOrEmpty()) {
            hasArea = true
        }

        strBlockID = editAddressDataModel.block_id.toString()
        strBlockname = editAddressDataModel.block_name.toString()

        txtBlock.text = strBlockname
        edtBlock.setText(strBlockname)
        if (!strBlockID.isNullOrEmpty()) {
            hasBlock = true
        }

        strType = editAddressDataModel?.address_type ?: ""
        if (!editAddressDataModel.building.isNullOrEmpty()) {
            edtFlatNo.setText(editAddressDataModel.flat_no.toString())
        }
        if (!editAddressDataModel.building.isNullOrEmpty()) {
            edtFloor.setText(editAddressDataModel.floor_no.toString())
        }
        if (!editAddressDataModel.building.isNullOrEmpty()) {
            edtExtraDirection.setText(editAddressDataModel.notes.toString())
        }
        if (!editAddressDataModel.building.isNullOrEmpty()) {
            edtBuilding.setText(editAddressDataModel.building.toString())
        }
        if (!editAddressDataModel.street.isNullOrEmpty()) {
            edtStreet.setText(editAddressDataModel.street.toString())
        }
        if (!editAddressDataModel.jaddah.isNullOrEmpty()) {
            txtJadaah.setText(editAddressDataModel.jaddah.toString())
        }
        if (!editAddressDataModel.zone.isNullOrEmpty()) {
            edtFlatNo.setText(editAddressDataModel.zone.toString())
        }
        if (!editAddressDataModel.mobile_number.isNullOrEmpty()) {
            edtPhoneNumber.setText(editAddressDataModel.mobile_number.toString())
        }
        btnSubmitAddress.text = resources.getString(R.string.update)
        txtHead.text = resources.getString(R.string.edit_address)
        if (strType == "0") {
            rbHome.isChecked = true
            rbOffice.isChecked = false
        } else {
            rbHome.isChecked = false
            rbOffice.isChecked = true
        }
        if (editAddressDataModel.is_default.toString().toLowerCase() == "yes") {
            strIsDefault = "1"
            switchDefault.isChecked = true
        } else {
            strIsDefault = "0"
            switchDefault.isChecked = false
        }

    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
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

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 103 && data != null) {
            strCountryName = Global.toCamelCase(data.getStringExtra("country_name")!!)
            if (data.getStringExtra("country_id")!!.isNotEmpty()) {
                strCountryID = data.getStringExtra("country_id")!!
                txtCountry.text = strCountryName
                setCountry()
            }
            if (data.getStringExtra("gov_has") != "1") {
                relState.visibility = View.GONE
                relArea.visibility = View.GONE
            } else {
                relState.visibility = View.VISIBLE
                relArea.visibility = View.VISIBLE
            }
        } else if (resultCode == 100 && data != null) {
            strStateName = Global.toCamelCase(data.getStringExtra("state_name")!!)
            if (data.getStringExtra("state_id")!!.isNotEmpty()) {
                strStateID = data.getStringExtra("state_id")!!
                txtGovernorate.text = strStateName
                setState()
            }
            if (data.getStringExtra("state_has") != "1") {
                relArea.visibility = View.GONE
                relBlock.visibility = View.GONE
                edtArea.visibility = View.VISIBLE
                hasArea = false
            } else {
                relArea.visibility = View.VISIBLE
                hasArea = true
            }
        } else if (resultCode == 101 && data != null) {
            strAreaname = Global.toCamelCase(data.getStringExtra("area_name")!!)

            if (data.getStringExtra("area_id")!!.isNotEmpty()) {
                strAreaID = data.getStringExtra("area_id")!!
                txtArea.text = strAreaname
                setArea()
            }
            if (data.getStringExtra("area_has") != "1") {
                relBlock.visibility = View.GONE
                edtBlock.visibility = View.VISIBLE
                hasBlock = false

            } else {
                relBlock.visibility = View.VISIBLE
                edtBlock.visibility = View.GONE
                hasBlock = true
            }
            strBlockname = edtBlock.text.toString()
            strJaddahname = txtJadaah.text.toString()

        } else if (resultCode == 102 && data != null) {
            strBlockname = data.getStringExtra("block_name")!!
            if (data.getStringExtra("block_id")!!.isNotEmpty()) {
                strBlockID = data.getStringExtra("block_id")!!
                txtBlock.text = strBlockname
            }
        }
    }

    private fun setCountry() {
        strStateID = ""
        strStateName = ""
        strAreaID = ""
        strAreaname = ""
        strJaddahname = ""
        strBlockname = ""
        strBlockID = ""
        txtGovernorate.text = strStateName
        txtArea.text = strAreaname
        edtArea.setText(strAreaname)
        edtBlock.setText(strBlockname)
        txtJadaah.setText(strJaddahname)
        txtBlock.text = strBlockname
    }

    private fun setState() {
        strAreaID = ""
        strAreaname = ""
        strJaddahname = ""
        strBlockname = ""
        strBlockID = ""
        txtArea.text = strAreaname
        edtArea.setText(strAreaname)
        edtBlock.setText(strBlockname)
        txtJadaah.setText(strJaddahname)
        txtBlock.text = strBlockname
    }

    private fun setArea() {
        strBlockname = ""
        strJaddahname = ""
        strBlockID = ""
        edtBlock.setText(strBlockname)
        txtJadaah.setText(strJaddahname)
        txtBlock.text = strBlockname
    }

}