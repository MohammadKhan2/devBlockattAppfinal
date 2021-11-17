package com.app.blockaat.tv.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.gson.JsonObject
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.interfaces.*
import com.app.blockaat.productlisting.model.*
import com.app.blockaat.search.SearchActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tv_product.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_filter.txtApply
import kotlinx.android.synthetic.main.layout_sort.*
import kotlinx.android.synthetic.main.toolbar_default.*

class TvProductActivity : BaseActivity(), OnHeaderClicked, OnSubcategorySelectListener,
    OnProductListListener, HomeItemClickInterface, OnTvClickListener {
    private var strHeader: String = ""
    private lateinit var productsDBHelper: DBHelper
    private var disposable: Disposable? = null
    private var adapterProductGrid: com.app.blockaat.productlisting.adapter.ProductListAdapter? =
        null
    private var strInfluencerID = ""
    private var strBrandID = ""
    private var strCategoryID = ""
    private var originalCategory = ""
    private var isFromMostSelling = "" // it will be 1 for most selling else 0
    private lateinit var layoutManager: GridLayoutManager
    private var strPrice: String = ""
    private var strAttributeID: String = "" //size as well as color ids
    private var strSortBy: String = "1"
    private var isFromSuggestions = false
    private var isFromRefresh: Boolean? = false
    private var totalPages = 0
    private var isLastPage = false
    lateinit var arrData: ArrayList<ProductListingFilterModel>
    private val PAGE_START = 1
    private var currentPage = PAGE_START
    private lateinit var arrListPageWiseData: ArrayList<ProductListingProductModel>
    private var isPageLoading: Boolean = false
    var strLatest = ""
    private var strSortName = ""
    private var arrListFilterData: ArrayList<ProductListingFilterModel?>? = null
    private var isFromBrands: Boolean? = false
    private var isItemClicked = false
    private var strIsFeatured = "0"
    private var isFromCategories = false
    private var dialog: CustomProgressBar? = null
    private var strYoutubeID: String = ""
    private var strTvID = ""
    private val TYPE_SUBCATEGORY = 0
    private val TYPE_BANNER = 1
    private val TYPE_HEADER = 2
    private val TYPE_ITEM = 3
    private val TYPE_LOADING = 4
    private var adapterFilter: FilterPagerAdapter? = null
    private var isFromFilter: Boolean = false
    private var isPageloaded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_product)

        Global.setLocale(this@TvProductActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        productListByCatID()

    }

    private fun onClickListeners() {

        imgSearch.setOnClickListener {
            val intent = Intent(this@TvProductActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        linFilter.setOnClickListener {
            simpleViewnimator.visibility = View.VISIBLE
            viewTransparent.visibility = View.VISIBLE

        }
        simpleViewSort.setOnClickListener {
            simpleViewSort.visibility = View.GONE
        }

        linSort.setOnClickListener {
            if (simpleViewSort.visibility == View.VISIBLE) {
                simpleViewSort.visibility = View.GONE
            } else {
                simpleViewSort.visibility = View.VISIBLE
            }

        }

        imgCloseSheet.setOnClickListener {
            simpleViewnimator.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        txtProductsEmptyDetail.setOnClickListener {
            finish()
        }

        relClearAll.setOnClickListener {
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {

                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
            }

        }

        relApply.setOnClickListener {

            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {
                strBrandID = ""
                strCategoryID = originalCategory
                strAttributeID = ""
                val arrListAttribute = ArrayList<ProductListingFilterValueModel>()
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    val type = (fragment as FilterListFragment).getFilteredType()
                    val arrListValues = (fragment as FilterListFragment).getFilteredValues()

                    if (type.equals("Brands", true) || type.equals("Brand")) {
                        if (!arrListValues.isNullOrEmpty()) {
                            strBrandID =
                                getCommaSeparatedString(arrListValues as ArrayList<ProductListingFilterValueModel>)
                        }
                    } else
                        if (type.equals("Categories", true) || type.equals("Category")) {
                            if (!arrListValues.isNullOrEmpty()) {
                                strCategoryID =
                                    getCommaSeparatedString(arrListValues as ArrayList<ProductListingFilterValueModel>)
                            }
                        } else {
                            if (!arrListValues.isNullOrEmpty()) {
                                arrListAttribute.addAll(arrListValues)
                            }
                        }

                }
                strAttributeID = getCommaSeparatedString(arrListAttribute)
                imgCloseSheet.performClick()
                isFromFilter = true
                if (adapterProductGrid != null) {
                    adapterProductGrid?.removeLoadingFooter()
                }
                productListByCatID()

            }


        }

    }

    private fun getCommaSeparatedString(arrListFilterValues: ArrayList<ProductListingFilterValueModel>): String {
        val sbString = StringBuilder("")
        arrListFilterValues

        for (i in 0 until arrListFilterValues.size) {
            if (arrListFilterValues[i].isSelected)
                sbString.append(arrListFilterValues[i].id).append(",");

        }
        //convert StringBuffer to String
        var strList = sbString?.toString()

        //remove last comma from String if you want
        if (strList.length > 0) {
            strList = strList.substring(0, strList.length - 1)
        } else {
            strList = ""
        }

        return strList
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this@TvProductActivity)
        productsDBHelper = DBHelper(this)

        Global.arrListFilter = ArrayList()
        if (intent.hasExtra("header_text") && !intent.getStringExtra("header_text")
                .isNullOrEmpty()
        ) {
            txtHead.text = intent.getStringExtra("header_text")
            strHeader = intent.getStringExtra("header_text")!!
        }

        if (intent.hasExtra("tvID") && intent.getStringExtra("tvID") != null) {
            strTvID = intent.getStringExtra("tvID")!!
        }

        strCategoryID = Global.getSelectedCategory(this@TvProductActivity)

        if (intent.hasExtra("brandID")) {
            strBrandID = intent.getStringExtra("brandID")!!
        }

        if (intent.hasExtra("influencerID")) {
            strInfluencerID = intent.getStringExtra("influencerID")!!
        }

        if (intent.hasExtra("isFromMostSellingBrands") && intent.getStringExtra("isFromMostSellingBrands") == "yes") {
            isFromMostSelling = "1"
        }

        if (intent.hasExtra("latest") && !intent.getStringExtra("latest").isNullOrEmpty()) {
            strLatest = intent.getStringExtra("latest")!!
        }

        if (intent.hasExtra("isFeatured") && !intent.getStringExtra("isFeatured").isNullOrEmpty()) {
            strIsFeatured = intent.getStringExtra("isFeatured")!!
        }

        isFromSuggestions =
            intent.hasExtra("isFromSuggestion") && intent.getStringExtra("isFromSuggestion") == "yes"
        layoutManager = GridLayoutManager(this@TvProductActivity, 2)
        val spanSizeLookip: GridLayoutManager.SpanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    var type = 2
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_SUBCATEGORY) {
                        type = 2
                    }
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_BANNER) {
                        type = 2
                    }
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_HEADER) {
                        type = 2
                    }
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_LOADING) {
                        type = 2
                    }
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_ITEM) {
                        type = 1
                    }

                    return type
                }
            }
        spanSizeLookip.isSpanIndexCacheEnabled = false
        layoutManager.spanSizeLookup = spanSizeLookip
        arrListPageWiseData = ArrayList()
        adapterProductGrid =
            com.app.blockaat.productlisting.adapter.ProductListAdapter(
                this@TvProductActivity,
                arrListPageWiseData,
                this,
                "",
                false,
                this,
                this,
                strHeader,
                this,
                this
            )
        adapterProductGrid?.setHasStableIds(true)
        rcyProductListing.adapter = adapterProductGrid
        (rcyProductListing.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rcyProductListing.layoutManager = layoutManager
        rcyProductListing.addItemDecoration(
            StickHeaderItemDecoration(adapterProductGrid as com.app.blockaat.helper.StickHeaderItemDecoration.StickyHeaderInterface,
                object : OnHeaderDisplay {
                    override fun isHeaderDisplay(visible: Boolean) {
                        if (visible) {
                            if (lnrRefine.visibility == View.GONE) {
                                lnrRefine.visibility = View.VISIBLE
                                viewRefine.visibility = View.VISIBLE
                            }
                        } else {
                            if (lnrRefine.visibility == View.VISIBLE) {
                                lnrRefine.visibility = View.GONE
                                viewRefine.visibility = View.GONE
                            }
                        }
                    }

                })
        )

        rcyProductListing.isNestedScrollingEnabled = false

        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load product list data
                productListByCatID()
            }, 1000)
        }

        rcyProductListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
                swipeRefreshLayout.isEnabled = topRowVerticalPosition >= 0
                /*


                  val visibleItemCount = layoutManager.childCount
                  val totalItemCount = layoutManager.itemCount
                  val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                  if (!isPageLoading && !isLastPage) {
                      if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                          isPageLoading = true
                          currentPage += 1
                          loadMoreProducts()
                      }
                  }*/
            }
        })


        if (intent.hasExtra("isFromBrands") && intent.getStringExtra("isFromBrands") == "yes") {
            isFromBrands = true
        }


        ivSortRecommendedTick.isChecked = true
        relSortHighToLow.setOnClickListener {
            strSortBy = "3"
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = true
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            productListByCatID()
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        relSortLowToHigh.setOnClickListener {
            strSortBy = "4"
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = true
            ivSortRecommendedTick.isChecked = false
            productListByCatID()
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }


        relSortLatest.setOnClickListener {
            strSortBy = "1"
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = true
            productListByCatID()
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        relSortNewIn.setOnClickListener {
            strSortBy = "2"
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = true
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            productListByCatID()
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        //lnrFilter.layoutParams.height = Global.getDeviceHeight(this@ProductListActivity)-resources.getDimensionPixelSize(R.dimen.forty_dp)

    }


    private fun generateColorStateList(
        enabledColor: Int = Color.parseColor("#bea376"), // Capri
        disabledColor: Int = Color.parseColor("#9A9A9A"), // grey
        checkedColor: Int = Color.parseColor("#bea376"), // Bud green
        uncheckedColor: Int = Color.parseColor("#9A9A9A") // grey

    ): ColorStateList {
        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked)
        )
        val colors = intArrayOf(
            enabledColor, // enabled
            disabledColor, // disabled
            checkedColor, // checked
            uncheckedColor // unchecked
        )
        return ColorStateList(states, colors)
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtNoProducts.typeface = Global.fontBold
        txtProductsEmptyDetail.typeface = Global.fontSemiBold
        txtFilter.typeface = Global.fontMedium
        txtSort.typeface = Global.fontMedium
        txtSortOptionTitle.typeface = Global.fontMedium
        txtSortHighToLow.typeface = Global.fontMedium
        txtSortLowToHigh.typeface = Global.fontMedium
        txtRecommended.typeface = Global.fontMedium
        txtSortNewIn.typeface = Global.fontMedium
        txtFilterByLabel.typeface = Global.fontMedium
        txtApply.typeface = Global.fontMedium
        txtClearAll.typeface = Global.fontMedium
    }


    private fun productListByCatID() {

        if (NetworkUtil.getConnectivityStatus(this@TvProductActivity) != 0) {

            if (!isFromRefresh!!) {
                showProgressDialog(this@TvProductActivity)
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strBrandID)
            jsonObject.addProperty("category_id", strCategoryID)

            disposable = Global.apiService.getTvProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.ProductsTv + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&user_id=" + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_USER_ID
                ) + "&tv_id=" + strTvID + "&store=" + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_STORE_CODE
                ) + "&sort_by=" + strSortBy
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }

                        if (result != null) {
                            if (result.data != null && result.status == 200) {
                                arrListPageWiseData.clear()
                                isLastPage = false
                                currentPage = 1
                                //totalPages = result.data.total_pages as Int

                                if (!isPageloaded) {
                                    arrListFilterData = ArrayList()
                                    arrListFilterData =
                                        result.data.filters as ArrayList<ProductListingFilterModel?>?
                                    setFilterData()
                                    isPageloaded = true
                                }


                                // if (totalPages > 0) {
                                lnrMainViewHolder.visibility = View.VISIBLE // if data show list
                                linNoItems.visibility = View.GONE
                                val arrListTvs = ArrayList<Tvs>()
                                arrListTvs.add(
                                    Tvs(
                                        video_id = result.video_id,
                                        name = result.name,
                                        image_name = result.image
                                    )
                                )
                                val influencerDetails = InfluencerDetails()
                                influencerDetails.tvs = arrListTvs as ArrayList<Tvs?>

                                arrListPageWiseData.add(
                                    ProductListingProductModel(
                                        influencer_details = influencerDetails
                                    )
                                )
                                arrListPageWiseData.add(ProductListingProductModel(
                                    totalItem = Global.stringToInt(result.data.total_products)
                                ))
                                result.data.products?.let { arrListPageWiseData.addAll(it) }


                                if (currentPage < totalPages) {

                                    adapterProductGrid!!.addLoadingFooter()

                                } else {
                                    isLastPage = true

                                }
                                adapterProductGrid?.notifyDataSetChanged()

                                /*    } else {

                                        lnrMainViewHolder.visibility = View.GONE // if data show list
                                        linNoItems.visibility = View.VISIBLE
                                        lnrRefine.visibility = View.GONE
                                    }*/


                            } else if (result.status == 404) {
                                // $this->response_code = 404;
                                //$this->message = 'No product match with your search criteria.please try by another keyword';
                                lnrMainViewHolder.visibility = View.GONE  //hide list if no data
                                if (!isFromFilter) {
                                    lnrRefine.visibility = View.GONE
                                }
                                linNoItems.visibility = View.VISIBLE

                            } else {
                                linNoItems.visibility = View.VISIBLE
                                if (!isFromFilter) {
                                    lnrRefine.visibility = View.GONE
                                }
                                lnrMainViewHolder.visibility = View.GONE
                                Global.showSnackbar(
                                    this@TvProductActivity as Activity,
                                    result.message.toString()
                                )

                            }
                        } else {
                            linNoItems.visibility = View.VISIBLE
                            lnrMainViewHolder.visibility = View.GONE
                            if (!isFromFilter) {
                                lnrRefine.visibility = View.GONE
                            }
                            Global.showSnackbar(
                                this@TvProductActivity as Activity,
                                getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Product List Ws :   " + error.printStackTrace())
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        lnrMainViewHolder.visibility = View.GONE
                        linNoItems.visibility = View.VISIBLE
                        if (!isFromFilter) {
                            lnrRefine.visibility = View.GONE
                        }
                        Global.showSnackbar(this@TvProductActivity, getString(R.string.error))
                    }
                )
        }
    }

    private fun setFilterData() {
        if (!arrListFilterData.isNullOrEmpty()) {
            adapterFilter = FilterPagerAdapter(supportFragmentManager, arrListFilterData!!)
            viewpager.adapter = adapterFilter
            tabFilter.setupWithViewPager(viewpager)
        }

    }

    private fun loadMoreProducts() {
        if (NetworkUtil.getConnectivityStatus(this@TvProductActivity) != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strBrandID)
            jsonObject.addProperty("category_id", strCategoryID)

            disposable = Global.apiService.getTvProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.ProductsTv + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&user_id=" + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_USER_ID
                ) + "&tv_id=" + strTvID + "&store=" + Global.getStringFromSharedPref(
                    this@TvProductActivity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    if (!isFromRefresh!!) {
                        hideProgressDialog()
                    }

                    if (result != null) {
                        //totalPages = result.data?.total_pages!!
                        adapterProductGrid!!.removeLoadingFooter()

                        isPageLoading = false
                        result.data?.products?.let { arrListPageWiseData.addAll(it) }

                        if (currentPage != totalPages) {
                            adapterProductGrid!!.addLoadingFooter()
                        } else {
                            isLastPage = true
                            adapterProductGrid?.notifyDataSetChanged()
                        }
                    }
                }
        }
    }

    private fun setVideo(tvs: ArrayList<Tvs?>?) {

    }


    private fun showProgressDialog(activity: Activity) {
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

    override fun onResume() {
        super.onResume()
        isItemClicked = false

        if (adapterProductGrid != null) {
            adapterProductGrid!!.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()

    }

    class FilterPagerAdapter(
        manager: FragmentManager,
        private val arrListFilters: ArrayList<ProductListingFilterModel?>
    ) : FragmentPagerAdapter(manager) {
        private var arrListTitle: java.util.ArrayList<String>? = null
        private var arrListFagment: ArrayList<FilterListFragment>? = null

        init {
            arrListTitle = java.util.ArrayList<String>()
            arrListFagment = java.util.ArrayList()
            for (filter in arrListFilters) {
                val strName =
                    filter?.filter_name?.trim()?.get(0)?.toUpperCase()
                        .toString() + filter?.filter_name?.trim()?.substring(
                        1
                    )?.toLowerCase().toString()
                arrListTitle?.add(strName)
                arrListFagment?.add(
                    FilterListFragment.newInstance(
                        filter?.filter_values as ArrayList<ProductListingFilterValueModel>,
                        filter?.filter_type.toString(),
                        strName
                    )
                )
            }
        }

        override fun getItem(position: Int): Fragment {
            return arrListFagment?.get(position) as Fragment
        }

        override fun getCount(): Int {
            return arrListTitle?.size as Int
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return arrListTitle?.get(position)
        }


    }

    override fun onHeaderClicked(type: String) {
        if (type == "sort") {
            linSort.performClick()
        } else {
            linFilter.performClick()
        }
    }

    override fun onSubcategorySelected(subCategory: Subcategory) {

    }

    override fun onProductClicked(product: ProductListingProductModel) {
        val intent = Intent(this@TvProductActivity, ProductDetailsActivity::class.java)
        intent.putExtra("strProductID", product.id.toString())
        intent.putExtra("strProductName", product.name.toString())
        startActivity(intent)
    }

    override fun onProductClicked(product: ProductListingProductModel, type: String) {
        updateCollection()
    }

    //to change wishlist item icon
    fun updateCollection() {
        for (i in 0 until arrListPageWiseData?.size) {
            arrListPageWiseData[i].item_in_wishlist = 0
            if (productsDBHelper?.isProductPresentInWishlist(
                    arrListPageWiseData[i].id.toString()
                )
            ) {
                arrListPageWiseData[i].item_in_wishlist = 1
            }
        }
        adapterProductGrid?.notifyDataSetChanged()
    }

    override fun onClickItem(
        position: Int,
        type: String,
        link_type: String,
        link_id: String,
        header: String
    ) {

    }

    override fun onTvClickListener(type: String, position: Int, itemPosition: Int) {

    }
}
