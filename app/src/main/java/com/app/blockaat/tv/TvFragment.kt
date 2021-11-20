package com.app.blockaat.tv

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.Filter.activity.FilterActivity
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.productlisting.model.Tvs
import com.app.blockaat.tv.adapter.AllTvAdapter
import com.app.blockaat.tv.model.TvRequestModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.fragment_tv.*
import kotlinx.android.synthetic.main.fragment_tv.linRefine
import kotlinx.android.synthetic.main.fragment_tv.relRefine
import kotlinx.android.synthetic.main.fragment_tv.txtItemCount
import kotlinx.android.synthetic.main.fragment_tv.txtRefine
import kotlinx.android.synthetic.main.fragment_tv.view.*
import kotlin.collections.ArrayList


class TvFragment : Fragment() {
    private var disposable: Disposable? = null
    private lateinit var mActivity: NavigationActivity
    private lateinit var rcyTv: RecyclerView
    private var isFromRefresh: Boolean? = false
    private var dialog: CustomProgressBar? = null
    private var arrListFilter: ArrayList<ProductListingFilterModel>? = null
    private var strCategory = ""
    private var strBrand = ""
    private var strInfluencer = ""
    private var isFirstLoaded = false
    private var adapterTv: AllTvAdapter? = null
    private var isGridThree = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mActivity = activity as NavigationActivity

        CustomEvents.screenViewed(activity as NavigationActivity, getString(R.string.tv_screen))
        Global.setLocale(mActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_tv, container, false)


        rcyTv = rootView!!.rcyTv

        getAllTv()

        rootView.swipe_Tv.setOnRefreshListener {
            isFromRefresh = true
            rootView.swipe_Tv.isRefreshing = true
            rootView.swipe_Tv.postDelayed({
                rootView.swipe_Tv.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
                    getAllTv()

                }
            }, 1000)
        }


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        setOnClickListener()
        setFont()
    }

    private fun initializeFields() {
        arrListFilter = ArrayList()

    }

    private fun setFont() {
        //txtSort.typeface = Global.fontMedium
        txtRefine.typeface = Global.fontRegular
        txtItemCount.typeface = Global.fontMedium
    }

    private fun setOnClickListener() {
        linRefine.setOnClickListener {
            val i = Intent(mActivity, FilterActivity::class.java)
            i.putParcelableArrayListExtra("filterData", arrListFilter)
            startActivityForResult(i, 100)
        }
       /* linSort.setOnClickListener {
            println("Sort list")
            if (!isGridThree) {
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

        }*/
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
                                relRefine.visibility = VISIBLE
                                rcyTv.visibility = VISIBLE
                                if (!isFirstLoaded) {
                                    setFilterData(result.data?.filters)
                                    isFirstLoaded = true
                                }
                                adapterTv =
                                    AllTvAdapter(result.data?.tv as ArrayList<Tvs>, mActivity)
                                val layoutManager = GridLayoutManager(mActivity, 2)

                                if (result.data.tv.size == 1) {
                                    txtItemCount.text = activity?.resources?.getString(R.string.video, result.data.tv.size.toString())
                                } else {
                                    txtItemCount.text = activity?.resources?.getString(R.string.videos, result.data.tv.size.toString())
                                }

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
                            } else {
                                rcyTv.visibility = GONE
                                relRefine.visibility = GONE
                            }
                        } else {
                            rcyTv.visibility = GONE
                            relRefine.visibility = GONE
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
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

}