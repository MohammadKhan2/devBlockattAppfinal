package com.app.blockaat.address

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder
import com.app.blockaat.R
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_country_list.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


class CountryListActivity : BaseActivity() {
    private var disposable: Disposable? = null
    private var adapterAllCountry: com.app.blockaat.address.adapter.PinSectionCountryAdapter? = null
    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_list)   //State is governorate

        Global.setLocale(this@CountryListActivity)
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
        getGovList()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.select_country)
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this@CountryListActivity)
        rvCountry.layoutManager = layoutManager
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getGovList() {
        if (NetworkUtil.getConnectivityStatus(this@CountryListActivity) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getCountryList(
                com.app.blockaat.apimanager.WebServices.CountryListWs + Global.getLanguage(this)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    adapterAllCountry =
                                        com.app.blockaat.address.adapter.PinSectionCountryAdapter(
                                            this
                                        )
                                    adapterAllCountry!!.setData(result.data)

                                    val topStickyHeadersItemDecoration = StickyHeadersBuilder()
                                        .setAdapter(adapterAllCountry!!)
                                        .setRecyclerView(rvCountry)
                                        .build()
                                    rvCountry!!.addItemDecoration(topStickyHeadersItemDecoration)

                                    linCountryIndex.removeAllViews()
                                    for (element in adapterAllCountry!!.headersLetters as TreeSet<Char>) {
                                        val view =
                                            View.inflate(this, R.layout.lv_item_state_index, null)
                                        val txtStateIndex =
                                            view.findViewById<TextView>(R.id.txtStateIndex)
                                        txtStateIndex.text = element.toString()
                                        txtStateIndex.typeface = Global.fontSemiBold
                                        txtStateIndex.setOnClickListener {
                                            for (i in result.data.indices) {
                                                if (result.data[i].name!![0] == txtStateIndex.text.toString()[0]) {
                                                    rvCountry!!.layoutManager!!.scrollToPosition(i)
                                                    break
                                                }
                                            }
                                        }
                                        linCountryIndex.addView(view)
                                    }
                                    rvCountry.visibility = View.VISIBLE

                                } else {
                                    //no data
                                    rvCountry.visibility = View.GONE
                                }
                            } else {
                                Global.showSnackbar(this@CountryListActivity, result.message)
                            }


                        } else {
                            Global.showSnackbar(
                                this@CountryListActivity,
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