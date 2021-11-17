package com.app.blockaat.address

import android.app.Dialog
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
import kotlinx.android.synthetic.main.activity_state_list.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


//State is governorate
class StateListActivity : BaseActivity() {
    private var disposable: Disposable? = null
    private lateinit var progressDialog: Dialog
    private var adapterAllState: com.app.blockaat.address.adapter.PinSectionStateAdapter? = null
    private var mToolbar: Toolbar? = null
    private var dialog: CustomProgressBar? = null
    private var strSelected: String = ""
    private var strCountryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state_list)   //State is governorate

        if ("ar".equals(Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE).toLowerCase())) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        Global.setLocale(this@StateListActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
        getGovList()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.select_governorate)
    }

    private fun initializeFields() {

        if (intent.hasExtra("stateID")) {
            strSelected = intent.getStringExtra("stateID")!!
        }
        if (intent.hasExtra("stateID")) {
            strCountryId = intent.getStringExtra("stateID")!!
        }
        dialog = CustomProgressBar(this)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@StateListActivity)
        rvState.layoutManager = layoutManager
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
        if (NetworkUtil.getConnectivityStatus(this@StateListActivity) != 0) {
            showProgressDialog()
            disposable = Global.apiService.getStateList(
                com.app.blockaat.apimanager.WebServices.StateListWs + Global.getLanguage(this) + "&country_id=" + strCountryId/*intent.getStringExtra("countryID")*/
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    adapterAllState =
                                        com.app.blockaat.address.adapter.PinSectionStateAdapter(
                                            this
                                        )
                                    adapterAllState!!.setData(result.data)

                                    val topStickyHeadersItemDecoration = StickyHeadersBuilder()
                                        .setAdapter(adapterAllState!!)
                                        .setRecyclerView(rvState)
                                        .build()
                                    rvState!!.addItemDecoration(topStickyHeadersItemDecoration)

                                    linStateIndex.removeAllViews()
                                    for (element in adapterAllState!!.headersLetters as TreeSet<Char>) {
                                        val view =
                                            View.inflate(this, R.layout.lv_item_state_index, null)
                                        val txtStateIndex =
                                            view.findViewById<TextView>(R.id.txtStateIndex)
                                        txtStateIndex.text = element.toString()
                                        txtStateIndex.typeface = Global.fontSemiBold
                                        txtStateIndex.setOnClickListener {
                                            for (i in result.data.indices) {
                                                if (result.data[i].name!![0] == txtStateIndex.text.toString()[0]) {
                                                    rvState!!.layoutManager!!.scrollToPosition(i)
                                                    break
                                                }
                                            }
                                        }
                                        linStateIndex.addView(view)
                                    }
                                    rvState.visibility = View.VISIBLE

                                } else {
                                    //no data
                                    rvState.visibility = View.GONE
                                }
                            } else {
                                Global.showSnackbar(this@StateListActivity, result.message)
                            }


                        } else {
                            Global.showSnackbar(
                                this@StateListActivity,
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