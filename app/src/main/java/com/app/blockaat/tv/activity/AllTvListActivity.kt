package com.app.blockaat.tv.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.app.blockaat.Filter.activity.FilterActivity
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.productlisting.model.Tvs
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.tv.adapter.AllTvAdapter
import com.app.blockaat.tv.model.TvRequestModel
import com.app.blockaat.wishlist.WishlistActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_all_tv.*
import kotlinx.android.synthetic.main.activity_all_tv.linFilter
import kotlinx.android.synthetic.main.activity_all_tv.linSort
import kotlinx.android.synthetic.main.activity_all_tv.lnrRefine
import kotlinx.android.synthetic.main.activity_all_tv.rcyTv
import kotlinx.android.synthetic.main.activity_all_tv.swipe_Tv
import kotlinx.android.synthetic.main.activity_all_tv.txtFilter
import kotlinx.android.synthetic.main.activity_all_tv.txtSort
import kotlinx.android.synthetic.main.toolbar_product_list.*

class AllTvListActivity : BaseActivity() {

    private var disposable: Disposable? = null
    private lateinit var mActivity: AppCompatActivity
    private var isFromRefresh: Boolean? = false
    private var dialog: CustomProgressBar? = null
    private var arrListFilter: ArrayList<ProductListingFilterModel>? = null
    private var strCategory = ""
    private var strBrand = ""
    private var strInfluencer = ""
    private var isFirstLoaded = false
    private var adapterTv: AllTvAdapter? = null
    private var isGridThree = false
    private var arrListTv: ArrayList<Tvs> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_tv)
        mActivity = this
        Global.setLocale(this)
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
        updateCounts()
    }

    fun updateCounts() {
        if (Global.isUserLoggedIn(this)) {

            // txtWishlistCount.visibility = View.GONE
            if (Global.getTotalWishListProductCount(this) > 0) {
                txtWishlistCount.visibility = View.VISIBLE
                txtWishlistCount.text =
                    Global.getTotalWishListProductCount(this).toString()
            } else {
                txtWishlistCount.visibility = View.GONE
            }
        } else {
            txtWishlistCount.visibility = View.GONE
        }

    }

    //initializing toolbar
    private fun initializeToolbar() {
        arrListFilter = ArrayList()
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.boutikey_ty)
        relWishlistImage.visibility = View.VISIBLE
        imgSearch.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        if (intent.hasExtra("categoryId")) {
            strCategory = intent.extras?.getString("categoryId").toString()
        }

        adapterTv = AllTvAdapter(arrListTv, mActivity)
        ivSort.setImageResource(R.drawable.grid)
        val layoutManager = GridLayoutManager(mActivity, 2)

        val spanSizeLookip: GridLayoutManager.SpanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    val value = 2
                    try {
                        (return when (adapterTv?.getItemViewType(position)) {
                            adapterTv?.VIEW_TYPE_ITEM -> 1
                            adapterTv?.VIEW_TYPE_HEADER -> 2
                            else -> 1

                        })
                    } catch (e: Exception) {
                        e.printStackTrace()

                    }
                    return value
                }
            }
        spanSizeLookip.isSpanIndexCacheEnabled = false
        layoutManager.spanSizeLookup = spanSizeLookip
        rcyTv.itemAnimator = DefaultItemAnimator()
        rcyTv.layoutManager = layoutManager
        val spacing = resources.getDimensionPixelOffset(R.dimen.five_dp) / 2
        rcyTv.setPadding(spacing, 0, spacing, 0)
        rcyTv.clipToPadding = false
        rcyTv.clipChildren = false
        rcyTv.addItemDecoration(
            com.app.blockaat.helper.SpacesItemDecoration(
                spacing
            )
        )
        rcyTv.adapter = adapterTv
        getAllTv()

    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtSort.typeface = Global.fontMedium
        txtFilter.typeface = Global.fontMedium
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            finish()
        }
        imgSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        linFilter.setOnClickListener {
            val i = Intent(mActivity, FilterActivity::class.java)
            i.putParcelableArrayListExtra("filterData", arrListFilter)
            startActivityForResult(i, 100)
        }
        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }
        linSort.setOnClickListener {
            println("Sort list")
            if (!isGridThree) {
                ivSort.setImageResource(R.drawable.list)
                val layoutManager = GridLayoutManager(mActivity, 3)
                val spanSizeLookip: GridLayoutManager.SpanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {

                            val value = 3
                            try {
                                (return when (adapterTv?.getItemViewType(position)) {
                                    adapterTv?.VIEW_TYPE_ITEM -> 1
                                    adapterTv?.VIEW_TYPE_HEADER -> 3
                                    else -> 1

                                })
                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                            return value
                        }
                    }
                spanSizeLookip.isSpanIndexCacheEnabled = false
                layoutManager.spanSizeLookup = spanSizeLookip
                rcyTv.itemAnimator = DefaultItemAnimator()
                adapterTv?.gridCount(3)
                rcyTv.layoutManager = layoutManager
                adapterTv?.notifyDataSetChanged()
                isGridThree = true
            } else {
                ivSort.setImageResource(R.drawable.grid)
                val layoutManager = GridLayoutManager(mActivity, 2)
                val spanSizeLookip: GridLayoutManager.SpanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {

                            val value = 2
                            try {
                                (return when (adapterTv?.getItemViewType(position)) {
                                    adapterTv?.VIEW_TYPE_ITEM -> 1
                                    adapterTv?.VIEW_TYPE_HEADER -> 2
                                    else -> 1

                                })
                            } catch (e: Exception) {
                                e.printStackTrace()

                            }
                            return value
                        }
                    }
                spanSizeLookip.isSpanIndexCacheEnabled = false
                layoutManager.spanSizeLookup = spanSizeLookip
                rcyTv.itemAnimator = DefaultItemAnimator()
                adapterTv?.gridCount(2)
                rcyTv.layoutManager = layoutManager
                adapterTv?.notifyDataSetChanged()
                isGridThree = false
            }

        }
        swipe_Tv.setOnRefreshListener {
            isFromRefresh = true
            swipe_Tv.isRefreshing = true
            swipe_Tv.postDelayed({
                swipe_Tv.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    getAllTv()

                }
            }, 1000)
        }
    }

    private fun getAllTv() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {

            if (!isFromRefresh!!) {
                showProgressDialog(mActivity)
            }
            val tvRequest = TvRequestModel(strCategory, strBrand, strInfluencer)
            disposable = Global.apiService.getAlltTv(
                tvRequest,
                com.app.blockaat.apimanager.WebServices.AllTV + Global.getStringFromSharedPref(
                    mActivity,
                    Constants.PREFS_LANGUAGE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        if (result != null) {
                            if (result.status == 200 && result.data != null) {
                                lnrRefine.visibility = View.VISIBLE
                                rcyTv.visibility = View.VISIBLE
                                if (!isFirstLoaded) {
                                    setFilterData(result.data?.filters)
                                    isFirstLoaded = true
                                }
                                result.data?.tv?.let { arrListTv.addAll(it) }
                                adapterTv?.notifyDataSetChanged()
                            } else {
                                rcyTv.visibility = View.GONE
                                lnrRefine.visibility = View.GONE
                            }
                        } else {
                            rcyTv.visibility = View.GONE
                            lnrRefine.visibility = View.GONE
                        }
                    },
                    { error ->
                        println("ERROR - Brands Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                    })

        }
    }

    private fun setFilterData(arrListFilter: ArrayList<ProductListingFilterModel>?) {
        arrListFilter?.let { this.arrListFilter?.addAll(it) }
    }


    private fun showProgressDialog(mActivity: AppCompatActivity) {
        dialog = CustomProgressBar(mActivity)
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            arrListFilter =
                data.getParcelableArrayListExtra<ProductListingFilterModel>("filterData") as ArrayList<ProductListingFilterModel>
            strBrand = ""
            strCategory = ""
            strInfluencer = ""
            for (i in 0 until arrListFilter?.size as Int) {
                if (arrListFilter?.get(i)?.filter_type?.toLowerCase().equals("influencers")) {
                    strInfluencer =
                        Global.getCommaSeparatedString(arrListFilter?.get(i)?.filter_values as java.util.ArrayList<ProductListingFilterValueModel>)
                }
                if (arrListFilter?.get(i)?.filter_type?.toLowerCase().equals("brands")) {
                    strBrand =
                        Global.getCommaSeparatedString(arrListFilter?.get(i)?.filter_values as java.util.ArrayList<ProductListingFilterValueModel>)
                }
                if (arrListFilter?.get(i)?.filter_type?.toLowerCase().equals("categories")) {
                    strCategory =
                        Global.getCommaSeparatedString(arrListFilter?.get(i)?.filter_values as java.util.ArrayList<ProductListingFilterValueModel>)
                }
            }
            getAllTv()
        }
        if (resultCode == 1) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.user_success_msg)
            )
            updateCounts()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }


}