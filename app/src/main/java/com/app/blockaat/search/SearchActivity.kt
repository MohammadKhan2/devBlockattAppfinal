package com.app.blockaat.search

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.search.adapter.PopularSearchAdapter
import com.app.blockaat.search.adapter.SearchAdapter
import com.app.blockaat.search.model.PopularSearchModel
import com.app.blockaat.search.model.SearchDataModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable? = null
    private var PoListAdapter: PopularSearchAdapter? = null
    private lateinit var arrPopularSearch: ArrayList<PopularSearchModel>
    private var adapterSearch: SearchAdapter? = null
    private lateinit var searchDBHelper: DBHelper
    private var isDataVisible: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        Global.setLocale(this@SearchActivity )
        init()
        Setfont()
        onClick()
    }

    private fun onClick() {
        txtCancel.setOnClickListener {
            //on cancel click clear everything related to search
            hideSoftKeyboard()
            edtSearch.setText("")
            clearSearch()
            finish()
        }

        ivClear.setOnClickListener {
            edtSearch.setText("")
            ivClear.visibility = View.GONE
        }

/*
         edtSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView, actionId: Int, event: KeyEvent): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val strSearchQuery: String? = edtSearch.text.toString()
                    suggestionApiCall(strSearchQuery!!)
                    return true
                }
                return false
            }
        })
*/
    }

    private fun Setfont() {
        txtNoData.typeface = Global.fontSemiBold
        txtCancel.typeface = Global.fontRegular
        edtSearch.typeface = Global.fontRegular
    }

    private fun init() {
        mCompositeDisposable = CompositeDisposable()
        txtPopularSearch.setTypeface(Global.fontMedium)
        searchDBHelper = DBHelper(this)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        rvSearchList!!.layoutManager = layoutManager
        arrPopularSearch = ArrayList<PopularSearchModel>()

        showRecentSearch()
        txtCancel.visibility = View.VISIBLE
        relSearchBar.visibility = View.VISIBLE
        rvSearchList.isNestedScrollingEnabled = false

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {

                if (editable.toString().isNotEmpty() && editable!!.length > 2) {
                    rltSearchHolder!!.visibility = View.VISIBLE
                    ivClear.visibility = View.VISIBLE
                    suggestionApiCall(editable.toString())
                } else {
                    rltSearchHolder!!.visibility = View.GONE
                    ivClear.visibility = View.GONE
                    rvSearchList.visibility = (View.GONE)
                }


            }

            override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
        })

        edtSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (edtSearch.text.toString().isNotEmpty() && edtSearch.text.length > 2) {
                    suggestionApiCall(edtSearch.text.toString())
                    hideSoftKeyboard()
                }
                Global.showSnackbar(
                    this@SearchActivity,
                    resources.getString(R.string.please_enter_characters)
                )
                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun clearSearch() {
        //this will hide search related stuff, and categories tab will become visible
        rltSearchHolder!!.visibility = View.GONE
        isDataVisible = true
    }

    fun clearRecentSearches() {
        //from adapter this method is accessed , onclick of clear we will clear all the recent search
        deleteRecentSearch()
    }


    fun showRecentSearch() {

        rltSearchHolder!!.visibility = View.VISIBLE
        if (searchDBHelper.getAllRecentSearch().isNotEmpty()) {
            adapterSearch = null
            rvSearchList!!.visibility = View.VISIBLE
            lnrNoitems!!.visibility = View.GONE
            adapterSearch = SearchAdapter(searchDBHelper.getAllRecentSearch().reversed(), searchDBHelper, true, Global.fontRegular!!, Global.fontLight!!, Global.fontMedium!!,
                Global.fontSemiBold!!)
            rvSearchList!!.adapter = adapterSearch
            rvSearchList!!.visibility = View.VISIBLE
            isDataVisible = false
        } else {
            //this means no data in ws and no data in recent search
            rvSearchList!!.visibility = View.GONE
            if (arrPopularSearch.size > 0) {
                lnrNoitems!!.visibility = View.GONE

            } else {
                lnrNoitems!!.visibility = View.GONE
            }

        }
    }

    fun deleteRecentSearch() {
        //this will delete all recent search data
        searchDBHelper.deleteAll("table_search")
        rvSearchList!!.visibility = View.GONE
        lnrNoitems!!.visibility = View.GONE
    }

    @SuppressLint("CheckResult")
    private fun suggestionApiCall(searchQuery: String) {
        if (NetworkUtil.getConnectivityStatus(this@SearchActivity) != 0) {
            Global.apiService.getSearchListData(
                com.app.blockaat.apimanager.WebServices.SuggestionWs + searchQuery +
                    "&store=" + Global.getStringFromSharedPref(this@SearchActivity, Constants.PREFS_STORE_CODE) + "&lang=" + Global.getStringFromSharedPref(this@SearchActivity, Constants.PREFS_LANGUAGE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            //println("RESPONSE - Suggestions Ws :   " + result.data)
                            if (result.status == 200) {
                                println("Here i am arraylist size is        " + result.data!!.size)
                                if (result.data != null && result.data.size > 0) {
                                    rvSearchList!!.visibility = View.VISIBLE
                                    val adapterSearch = SearchAdapter(result.data, searchDBHelper, false, Global.fontRegular!!,
                                        Global.fontLight!!, Global.fontMedium!!, Global.fontSemiBold!!)
                                    rvSearchList!!.adapter = adapterSearch
                                    rltSearchHolder!!.visibility = View.VISIBLE
                                } else {
                                    //no suggestions found
                                    println("Here i am no data")
                                    rvSearchList!!.visibility = View.GONE
                                    clearData(result.data)
                                }
                            } else {
                                Global.showSnackbar(this@SearchActivity, result.message!!)
                            }
                        } else {
                            //if ws not working
                            Global.showSnackbar(this@SearchActivity, resources.getString(R.string.error))
                        }
                    },
                    { error ->
                        //println("ERROR - Suggestions Ws :   " + error.localizedMessage)
                        Global.showSnackbar(this@SearchActivity, resources.getString(R.string.error))
                    }
                )
        }

    }

    private fun clearData(data: List<SearchDataModel>?) {
        if (data!!.isNotEmpty()) {
            rltSearchHolder!!.visibility = View.VISIBLE
        } else {
            rltSearchHolder!!.visibility = View.GONE
        }
    }


    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration


            val locale = Locale(SharedPreferencesHelper.getString(this, "lang_new", "en"))
            Locale.setDefault(locale)
            config.setLocale(locale)
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }

    fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

}
