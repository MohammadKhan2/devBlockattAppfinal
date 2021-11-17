package com.app.blockaat.changestores

import androidx.appcompat.app.AppCompatActivity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.*
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.changestores.adapter.StoreAdapter
import com.app.blockaat.changestores.interfaces.UpdateStoreItemEvent
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_stores.*
import kotlinx.android.synthetic.main.toolbar_default.*

class ChangeStoresActivity : BaseActivity() {

    private var adapterFilterStore: StoreAdapter? = null
    private lateinit var progressDialog: Dialog
    private var disposable: Disposable? = null
    private lateinit var productsDBHelper: DBHelper
    private var arrListStoreData: ArrayList<StoreDataModel>? = arrayListOf()
    private var dialog: CustomProgressBar? = null
    private var mToolbar: Toolbar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_stores)

        Global.setLocale(this@ChangeStoresActivity)
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
        getStoreListing()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.change_store)
        txtSave.visibility = VISIBLE
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        productsDBHelper = DBHelper(this@ChangeStoresActivity)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@ChangeStoresActivity)
        rvStores.layoutManager = layoutManager
        adapterFilterStore =
            StoreAdapter(this, arrListStoreData, onStoreUpdateClicked)
        rvStores.adapter = adapterFilterStore
        Global.strCountryNameEn =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_COUNTRY_EN)
        Global.strStoreCode =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_STORE_CODE)
        Global.strFlag =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_COUNTRY_FLAG)
        Global.strCurrencyCodeEn =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_CURRENCY_EN)
        Global.strCurrencyCodeAr =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_CURRENCY_AR)
        Global.strCountryNameAr =
            Global.getStringFromSharedPref(this@ChangeStoresActivity, Constants.PREFS_COUNTRY_AR)
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtSave.typeface = Global.fontBold
    }

    private fun onClickListener() {
        txtSave.setOnClickListener {
            if (!Global.strStoreCode.isNullOrEmpty() && !Global.strCurrencyCodeEn.isNullOrEmpty() && Global.strFlag.isNullOrEmpty() && !Global.strCurrencyCodeAr.isNullOrEmpty()) {

                if (Global.getStringFromSharedPref(
                        this@ChangeStoresActivity,
                        Constants.PREFS_isUSER_LOGGED_IN
                    ) != "yes"
                ) {
                    showCurrencyDialog()
                } else {
                    var msg = ""
                    msg = if (Global.getLanguage(this) == "en"
                    ) {
                        resources.getString(
                            R.string.set_location_alert,
                            Global.strCountryNameEn + " (" + Global.strCurrencyCodeEn + ")"
                        )
                    } else {
                        resources.getString(
                            R.string.set_location_alert,
                            Global.strCountryNameAr + " (" + Global.strCurrencyCodeAr + ")"
                        )
                    }
                    showLocationDialog(msg)
                }
            }
        }
    }

    private fun showLocationDialog(msg: String) {
        try {
            Global.showAlert(this,
                "",
                msg,
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                true,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_STORE_CODE,
                            Global.strStoreCode
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_CURRENCY_EN,
                            Global.strCurrencyCodeEn
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_CURRENCY_AR,
                            Global.strCurrencyCodeAr
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_EN,
                            Global.strCountryNameEn
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_AR,
                            Global.strCountryNameAr
                        )

                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_FLAG,
                            Global.strFlag
                        )

                        val i = Intent(this@ChangeStoresActivity, NavigationActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                        finish()
                    }

                    override fun onNoClick() {
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showCurrencyDialog() {
        try {
            Global.showAlert(this,
                "",
                resources.getString(R.string.change_currency_alert_logged_out),
                resources.getString(R.string.ok),
                resources.getString(R.string.no),
                true,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_STORE_CODE,
                            Global.strStoreCode
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_CURRENCY_EN,
                            Global.strCurrencyCodeEn
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_CURRENCY_AR,
                            Global.strCurrencyCodeAr
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_EN,
                            Global.strCountryNameEn
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_AR,
                            Global.strCountryNameAr
                        )
                        Global.saveStringInSharedPref(
                            this@ChangeStoresActivity,
                            Constants.PREFS_COUNTRY_FLAG,
                            Global.strFlag
                        )

                        val i = Intent(this@ChangeStoresActivity, NavigationActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                        productsDBHelper.deleteTable("table_cart")
                        productsDBHelper.deleteTable("table_wishlist")

                        startActivity(i)
                        finish()
                    }

                    override fun onNoClick() {
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getStoreListing() {
        if (NetworkUtil.getConnectivityStatus(this@ChangeStoresActivity) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getStores(
                com.app.blockaat.apimanager.WebServices.StoreWs + Global.getStringFromSharedPref(
                    this@ChangeStoresActivity,
                    Constants.PREFS_LANGUAGE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            hideProgressDialog()
                            println("RESPONSE - Store List Ws :   " + result.data)
                            if (result.status == 200) {
                                Global.strStoreCode = Global.getStringFromSharedPref(
                                    this@ChangeStoresActivity,
                                    Constants.PREFS_STORE_CODE
                                )
                                Global.strCurrencyCodeEn = Global.getStringFromSharedPref(
                                    this@ChangeStoresActivity,
                                    Constants.PREFS_CURRENCY_EN
                                )
                                Global.strFlag = Global.getStringFromSharedPref(
                                    this@ChangeStoresActivity,
                                    Constants.PREFS_COUNTRY_FLAG
                                )
                                Global.strCurrencyCodeAr = Global.getStringFromSharedPref(
                                    this@ChangeStoresActivity,
                                    Constants.PREFS_CURRENCY_AR
                                )
                                //Global.saveStringInSharedPref(this@SelectStoreActivity, Constants.PREFS_COUNTRY_EN, Global.strCountryNameEn)
                                // Global.saveStringInSharedPref(this@SelectStoreActivity, Constants.PREFS_COUNTRY_AR, Global.strCountryNameAr)

                                if (result.data.isNullOrEmpty()) {
                                    for (o in 0 until result.data.size) {
                                        if (Global.getStringFromSharedPref(
                                                this,
                                                Constants.PREFS_STORE_CODE
                                            ) == result.data[0].iso_code
                                        ) {
                                            result.data[o]?.isSelected = true
                                        }
                                    }
                                }
                                arrListStoreData?.addAll(result.data)
                                adapterFilterStore?.notifyDataSetChanged()

                            } else {
                                rvStores.visibility = View.GONE
                                Global.showSnackbar(this@ChangeStoresActivity, result.message)
                            }
                        } else {
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@ChangeStoresActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Store List :   " + error.localizedMessage)
                        hideProgressDialog()
                        Global.showSnackbar(
                            this@ChangeStoresActivity,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }


    private val onStoreUpdateClicked = object : UpdateStoreItemEvent {
        override fun onStoreUpdateClicked(
            position: Int,
            type: String,
            get: StoreDataModel?
        ) {
            for (a in 0 until arrListStoreData!!.size) {
                //It's an if-else condition
                arrListStoreData!![a].isSelected = false
            }
            arrListStoreData!![position].isSelected = true
            adapterFilterStore!!.notifyDataSetChanged()
        }
    }

    private fun showProgressDialog() {
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
            onBackPressed()
            this.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Global.strCountryNameEn = ""
        Global.strStoreCode = ""
        Global.strFlag = ""
        Global.strCurrencyCodeEn = ""
        Global.strCurrencyCodeAr = ""
        Global.strCountryNameAr = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
