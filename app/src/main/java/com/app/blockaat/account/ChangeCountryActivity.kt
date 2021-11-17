package com.app.blockaat.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder
import com.app.blockaat.R
import com.app.blockaat.account.adapter.ChangeCountryAdapter
import com.app.blockaat.account.interfaces.ChangeCountryInterface
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_country.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*

class ChangeCountryActivity : BaseActivity() {

    private var disposable: Disposable? = null
    private var adapterAllCountry: ChangeCountryAdapter? = null
    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null
    private var arrListCountry = ArrayList<StoreDataModel?>(0)
    private var selectedCountryPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_country)

        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
        getStoreList()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.change_country)
        relWishlistImage.visibility = View.GONE
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)

        val layoutManager = LinearLayoutManager(this@ChangeCountryActivity, LinearLayoutManager.VERTICAL, false)
        rvCountry.layoutManager = layoutManager
        adapterAllCountry = ChangeCountryAdapter(arrListCountry, changeCountryInterface)
        rvCountry.adapter = adapterAllCountry
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtSelectCountry.typeface = Global.fontNavBar
        txtSaveCountry.typeface = Global.fontBtn
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        txtSaveCountry.setOnClickListener {
            showChangeCountryDialog()
        }
    }


    private fun getStoreList() {
        if (NetworkUtil.getConnectivityStatus(this@ChangeCountryActivity) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getStores(
                WebServices.StoreWs + Global.getLanguage(this)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (!result.data.isNullOrEmpty()) {
                                    arrListCountry.clear()
                                    for (i in 0 until result.data.size) {
                                        if (Global.getStringFromSharedPref(
                                                this@ChangeCountryActivity, Constants.PREFS_COUNTRY_ID
                                            ) == Global.intToString(result.data[i]?.id)) {

                                            result.data[i].isSelected = true
                                        }
                                    }

                                    arrListCountry.addAll(result.data)

                                    adapterAllCountry?.notifyDataSetChanged()
                                    linMain.visibility = View.VISIBLE
                                } else {
                                    //no data
                                    linMain.visibility = View.GONE
                                }
                            } else {
                                Global.showSnackbar(this@ChangeCountryActivity, result.message)
                            }
                        } else {
                            Global.showSnackbar(
                                this@ChangeCountryActivity,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                        hideProgressDialog()
                    }
                )
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun showChangeCountryDialog() {
        Global.showAlert(this,
            "",
            resources.getString(R.string.change_country_alert, arrListCountry[selectedCountryPosition]?.name_en),
            resources.getString(R.string.yes),
            resources.getString(R.string.no),
            false,
            R.drawable.ic_alert,
            object : AlertDialogInterface {
                override fun onYesClick() {

                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_COUNTRY_ID,
                        Global.intToString(arrListCountry[selectedCountryPosition]?.id)
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_STORE_CODE,
                        arrListCountry[selectedCountryPosition]?.iso_code ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_COUNTRY_EN,
                        arrListCountry[selectedCountryPosition]?.name_en ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_COUNTRY_AR,
                        arrListCountry[selectedCountryPosition]?.name_ar ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_CURRENCY_EN,
                        arrListCountry[selectedCountryPosition]?.currency_en ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_CURRENCY_AR,
                        arrListCountry[selectedCountryPosition]?.currency_ar ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_COUNTRY_FLAG,
                        arrListCountry[selectedCountryPosition]?.flag ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_CURRENCY_FLAG,
                        arrListCountry[selectedCountryPosition]?.flag ?: ""
                    )
                    Global.saveStringInSharedPref(
                        this@ChangeCountryActivity,
                        Constants.PREFS_USER_PHONE_CODE,
                        "+" + arrListCountry[selectedCountryPosition]?.phonecode ?: ""
                    )

                    val i = Intent(this@ChangeCountryActivity, NavigationActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    finish()
                }
                override fun onNoClick() {
                }

            })
    }

    private val changeCountryInterface = object : ChangeCountryInterface {
        override fun onCountryClick(type: String, position: Int) {
            if (type == "countryClick") {
                for (i in 0 until arrListCountry.size) {
                    if (i == position) {
                        arrListCountry[i]?.isSelected = true
                        selectedCountryPosition = i
                    } else {
                        arrListCountry[i]?.isSelected = false
                    }
                }
                adapterAllCountry?.notifyDataSetChanged()
            }
        }
    }

    private fun showProgressDialog() {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

}