package com.app.blockaat.address

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import kotlinx.android.synthetic.main.activity_area_list.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


class AreaListActivity : BaseActivity() {
    private var disposable: Disposable? = null
    private lateinit var progressDialog: Dialog
    private var mToolbar: Toolbar? = null
    private var adapterAllArea: com.app.blockaat.address.adapter.PinSectionAreaAdapter? = null
    private var dialog: CustomProgressBar? = null
    private var strSelected: String = ""
    private var strCountryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_area_list)

        Global.setLocale(this@AreaListActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
        getGovList()

    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.select_area)
    }

    private fun initializeFields() {


        if (intent.hasExtra("stateID")) {
            strSelected = intent.getStringExtra("stateID")!!
        }
        if (intent.hasExtra("stateID")) {
            strCountryId = intent.getStringExtra("stateID")!!
        }

        dialog = CustomProgressBar(this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@AreaListActivity)
        rvArea.layoutManager = layoutManager
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
        if (NetworkUtil.getConnectivityStatus(this@AreaListActivity) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getAreaList(
                com.app.blockaat.apimanager.WebServices.AreaListWs + Global.getStringFromSharedPref(
                    this@AreaListActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&state_id=" + /*intent.getStringExtra("stateID")*/strCountryId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            println("RESPONSE - AreaList Ws :   " + result.data)
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    adapterAllArea =
                                        com.app.blockaat.address.adapter.PinSectionAreaAdapter(
                                            this
                                        )
                                    adapterAllArea!!.setData(result.data)

                                    val topStickyHeadersItemDecoration = StickyHeadersBuilder()
                                        .setAdapter(adapterAllArea!!)
                                        .setRecyclerView(rvArea)
                                        .build()
                                    rvArea!!.addItemDecoration(topStickyHeadersItemDecoration)

                                    linAreaIndex.removeAllViews()
                                    for (element in adapterAllArea!!.headersLetters as TreeSet<Char>) {
                                        val view =
                                            View.inflate(this, R.layout.lv_item_state_index, null)
                                        val txtStateIndex =
                                            view.findViewById<TextView>(R.id.txtStateIndex)
                                        txtStateIndex.text = element.toString()
                                        txtStateIndex.typeface = Global.fontSemiBold
                                        txtStateIndex.setOnClickListener {
                                            for (i in result.data.indices) {
                                                if (result.data[i].name!![0] == txtStateIndex.text.toString()[0]) {
                                                    rvArea!!.layoutManager!!.scrollToPosition(i)
                                                    break
                                                }
                                            }
                                        }
                                        linAreaIndex.addView(view)
                                    }
                                    rvArea.visibility = View.VISIBLE

                                } else {
                                    //no data
                                    rvArea.visibility = View.GONE
                                }
                            } else {
                                Global.showSnackbar(this@AreaListActivity, result.message)
                            }


                        } else {
                            Global.showSnackbar(
                                this@AreaListActivity,
                                resources.getString(R.string.error)
                            )
                        }


                    },
                    { error ->
                        hideProgressDialog()
                        println("RESPONSE - AreaList Ws :   " + error.localizedMessage)

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
}