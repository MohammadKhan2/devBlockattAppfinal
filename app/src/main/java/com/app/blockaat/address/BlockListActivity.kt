package com.app.blockaat.address

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder
import com.app.blockaat.R
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_block_list.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.*


class BlockListActivity : BaseActivity() {
    private var disposable: Disposable? = null
    private lateinit var progressDialog: Dialog
    private var adapterAllBlock: com.app.blockaat.address.adapter.PinSectionBlockAdapter? = null
    private var mToolbar: Toolbar? = null
    private var strSelected: String = ""
    private var strCountryId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_list)

        Global.setLocale(this@BlockListActivity)
        initializeToolbar()
        initializeFields()
        getGovList()
        setFonts()
    }

    //initializing toolbar
    private fun initializeToolbar() {

      /*  mToolbar = toolbar_actionbar as Toolbar?
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)*/
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.select_block)
    }

    private fun initializeFields() {

        if (intent.hasExtra("stateID")) {
            strSelected = intent.getStringExtra("stateID")!!
        }
        if (intent.hasExtra("stateID")) {
            strCountryId = intent.getStringExtra("stateID")!!
        }

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@BlockListActivity)
        rvBlock.layoutManager = layoutManager
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun getGovList() {
        if (NetworkUtil.getConnectivityStatus(this@BlockListActivity) != 0) {

            disposable = Global.apiService.getBlockList(com.app.blockaat.apimanager.WebServices.BlockListWs + Global.getStringFromSharedPref(this@BlockListActivity, Constants.PREFS_LANGUAGE) + "&area_id=" + strCountryId/*intent.getStringExtra("areaID")*/)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                          //  println("RESPONSE - BlockList Ws :   " + result.data)
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    adapterAllBlock =
                                        com.app.blockaat.address.adapter.PinSectionBlockAdapter(
                                            this
                                        )
                                    adapterAllBlock!!.setData(result.data)

                                    val topStickyHeadersItemDecoration = StickyHeadersBuilder()
                                        .setAdapter(adapterAllBlock!!)
                                        .setRecyclerView(rvBlock)
                                        .build()
                                    rvBlock!!.addItemDecoration(topStickyHeadersItemDecoration)

                                    linBlockIndex.removeAllViews()
                                    for (element in adapterAllBlock!!.headersLetters as TreeSet<Char>) {
                                        val view = View.inflate(this, R.layout.lv_item_state_index, null)
                                        val txtStateIndex = view.findViewById<TextView>(R.id.txtStateIndex)
                                        txtStateIndex.text = element.toString()
                                        txtStateIndex.typeface = Global.fontSemiBold
                                        txtStateIndex.setOnClickListener {
                                            for (i in result.data.indices) {
                                                if (result.data[i].name[0] == txtStateIndex.text.toString()[0]) {
                                                    rvBlock!!.layoutManager!!.scrollToPosition(i)
                                                    break
                                                }
                                            }
                                        }
                                        linBlockIndex.addView(view)
                                    }
                                    rvBlock.visibility = View.VISIBLE

                                } else {
                                    //no data
                                    rvBlock.visibility = View.GONE
                                }
                            } else {
                                Global.showSnackbar(this@BlockListActivity, result.message)
                            }

                        } else {
                            Global.showSnackbar(this@BlockListActivity, resources.getString(R.string.error))
                        }

                    },
                    { error ->
                      //  println("RESPONSE - BlockList Ws :   " + error.localizedMessage)
                    }
                )
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

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}