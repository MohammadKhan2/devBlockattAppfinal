package com.app.blockaat.productlisting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.app.blockaat.Filter.activity.SortFilterActivity
import com.google.gson.JsonObject
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.adapter.TvListAdapter
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.interfaces.*
import com.app.blockaat.productlisting.model.*
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.tv.activity.TvProductActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.google.android.material.tabs.TabLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_list.*

import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_filter.txtApply
import kotlinx.android.synthetic.main.layout_sort.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_product_list.imgClose
import kotlinx.android.synthetic.main.toolbar_product_list.imgSearch
import kotlinx.android.synthetic.main.toolbar_product_list.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_product_list.ivShare
import kotlinx.android.synthetic.main.toolbar_product_list.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_product_list.txtHead
import kotlinx.android.synthetic.main.toolbar_product_list.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_product_list.view
import kotlinx.android.synthetic.main.toolbar_product_list.viewStart


class ProductListActivity : BaseActivity(), OnHeaderClicked, OnSubcategorySelectListener,
    OnProductListListener,
    HomeItemClickInterface {

    private var totalItems: Int = 0
    private val requestCodeFilter: Int = 100
    private var strHeader: String = ""
    private var hasSubcategory: String = ""
    private lateinit var productsDBHelper: DBHelper
    private var isFromStr: String = ""
    private var strBanner: String = ""
    private var disposable: Disposable? = null
    private var adapterProductGrid: com.app.blockaat.productlisting.adapter.ProductListAdapter? =
        null
    private var strInfluencerID = ""
    private var strBrandID = ""

    private var strCategoryID = ""
    private var strFilterCategoryID = ""
    private var strFilterBrandID = ""

    private var originalCategory = ""
    private var isFromMostSelling = "" // it will be 1 for most selling else 0
    private lateinit var layoutManager: GridLayoutManager
    private var strPrice: String = ""
    private var strAttributeID: String = "" //size as well as color ids
    private var strSortBy: String = "1"
    private var isFromSuggestions = false
    private var isFromRefresh: Boolean = false
    private var totalPages = 0
    private var isLastPage = false
    lateinit var arrData: ArrayList<ProductListingFilterModel>
    private val PAGE_START = 1
    private var currentPage = PAGE_START
    private lateinit var arrListPageWiseData: ArrayList<ProductListingProductModel>
    private var isPageLoading: Boolean = false
    var strLatest = ""
    private var strSortName = ""
    var tvData: Tvs? = null
    private var arrListTv: ArrayList<Tvs>? = null
    private var adapterTv: TvListAdapter? = null
    private var arrListFilterData: ArrayList<ProductListingFilterModel?>? = null
    private var isFromBrands: Boolean? = false
    private var strIsFeatured = "0"
    private var isFromCategories = false
    private var dialog: CustomProgressBar? = null
    private var strYoutubeID: String = ""
    private var strTvID = ""
    private val TYPE_SUBCATEGORY = 0
    private val TYPE_TV = 1
    private val TYPE_BANNER = 2
    private val TYPE_HEADER = 3
    private val TYPE_ITEM = 4
    private val TYPE_LOADING = 5
    private var adapterFilter: FilterPagerAdapter? = null
    private var isFromFilter: Boolean = false
    private var isPageloaded: Boolean = false
    private var arrListSubCategory: ArrayList<Subcategory>? = null


    //filter
    private var strMinPrice: String = "0.0"
    private var strRange: String = ""
    private var strMaxPrice: String = "0.0"
    private var originalMaxPrice: String? = "0.0"
    private var isPricePresent: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_list)

        Global.setLocale(this@ProductListActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        productListByCatID()
        //window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)

    }

    private fun onClickListeners() {

        imgCloseSheet.setOnClickListener {
            simpleViewnimator.visibility = GONE
            viewTransparent.visibility = View.GONE
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {
             //   println("from type: " + isFromStr)
                if (!isFromStr.equals("brand")) {
                    strBrandID = ""
                }
                if (!isFromStr.equals("categories")) {
                    strFilterCategoryID = ""
                }
                strAttributeID = ""
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
                adapterFilter?.notifyDataSetChanged()
                if (adapterProductGrid != null) {
                    adapterProductGrid?.removeLoadingFooter()
                }
                productListByCatID()

            }
        }

        viewTransparent.setOnClickListener {
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        imgSearch.setOnClickListener {
            val intent = Intent(this@ProductListActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        linRefine.setOnClickListener {
            //hideDisplayBottomSheetFilter()
            if (arrListFilterData?.isNullOrEmpty() == false) {
                val i = Intent(this, SortFilterActivity::class.java)

                if (!isPricePresent) {
                    if (!originalMaxPrice.isNullOrEmpty()) {
                        isPricePresent = true
                        arrListFilterData?.add(
                            ProductListingFilterModel(
                                filter_name = resources.getString(R.string.price_range),
                                filter_type = "price",
                                filter_values = arrayListOf()
                            )
                        )
                    }
                }
                i.putExtra("filterData", arrListFilterData)
                i.putExtra("originalMaxPrice", originalMaxPrice)
                i.putExtra("maxPrice", strMaxPrice)
                i.putExtra("isFrom", isFromStr)
                i.putExtra("strPriceRange", strRange)
                i.putExtra("minPrice", strMinPrice)
                i.putExtra("sortBy", strSortBy)
                startActivityForResult(i, requestCodeFilter)
            }
        }


        imgClose.setOnClickListener {
            simpleViewnimator.visibility = View.GONE
            viewTransparent.visibility = View.GONE
        }

        ivBackArrow.setOnClickListener {
            finish()
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

        relMyBoutikey.setOnClickListener {
            lnrMainViewHolder.visibility = View.VISIBLE
            relTvList.visibility = View.GONE
            viewBoutikey.visibility = View.VISIBLE
            viewTv.visibility = View.GONE
        }

        relTv.setOnClickListener {
            lnrMainViewHolder.visibility = View.GONE
            relTvList.visibility = View.VISIBLE
            viewBoutikey.visibility = View.GONE
            viewTv.visibility = View.VISIBLE
        }

        relApply.setOnClickListener {

            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {
              //  println("from type: " + isFromStr)
                if (!isFromStr.equals("brand")) {
                    strBrandID = ""
                }
                if (!isFromStr.equals("categories")) {
                    strFilterCategoryID = ""
                }
                strAttributeID = ""
                val arrListAttribute = ArrayList<ProductListingFilterValueModel>()
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    val type = (fragment as FilterListFragment).getFilteredType()
                    val arrListValues = (fragment as FilterListFragment).getFilteredValues()

                    if ((type.equals(
                            "Brands",
                            true
                        ) || type.equals("Brand")) && !isFromStr.equals("brand")
                    ) {
                        if (!arrListValues.isNullOrEmpty()) {
                            strBrandID =
                                Global.getCommaSeparatedString(arrListValues as ArrayList<ProductListingFilterValueModel>)
                        }
                    } else
                        if ((type.equals(
                                "Categories",
                                true
                            ) || type.equals("Category")) && !isFromStr.equals("categories")
                        ) {
                            if (!arrListValues.isNullOrEmpty()) {
                                strFilterCategoryID =
                                    Global.getCommaSeparatedString(arrListValues as ArrayList<ProductListingFilterValueModel>)
                            }
                        } else {
                            if (!arrListValues.isNullOrEmpty()) {
                                arrListAttribute.addAll(arrListValues)
                            }
                        }

                }
                strAttributeID = Global.getCommaSeparatedString(arrListAttribute)
                imgClose.performClick()
                isFromFilter = true
                if (adapterProductGrid != null) {
                    adapterProductGrid?.removeLoadingFooter()
                }
                productListByCatID()

            }


        }
        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@ProductListActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@ProductListActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        viewStart.visibility = View.VISIBLE

    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this@ProductListActivity)
        productsDBHelper = DBHelper(this)
        Global.arrListFilter = ArrayList()
        if (intent.hasExtra("header_text") && !intent.getStringExtra("header_text")
                .isNullOrEmpty()
        ) {
            txtHead.text = intent.getStringExtra("header_text")
            strHeader = intent.getStringExtra("header_text")!!
            txtHead.text = Global.toCamelCase(strHeader)

        }

      /*  if (intent.hasExtra("subcategories")) {
            arrListSubCategory = intent.getParcelableArrayListExtra("subcategories")
        }*/

        if (intent.hasExtra("has_subcategory")) {
            hasSubcategory = intent.getStringExtra("has_subcategory").toString()
        }

        arrListSubCategory = intent.getParcelableArrayListExtra("subcategories")
       // Log.e("arrListSubCategory",""+arrListSubCategory)

        if (intent.hasExtra("tv_id") && intent.getStringExtra("tv_id") != null) {
            strTvID = intent?.getStringExtra("tv_id").toString()
        }

        if (intent.hasExtra("tvID") && intent.getStringExtra("tvID") != null) {
            strTvID = intent.getStringExtra("tvID")!!
        }

        if (intent?.getSerializableExtra("tvData") != null) {
            tvData = intent?.getSerializableExtra("tvData") as Tvs
        }

        if (intent.hasExtra("isFrom") && intent.getStringExtra("isFrom") != null) {
            isFromStr = intent.getStringExtra("isFrom")!!
        }
        if (intent.hasExtra("banner") && intent.getStringExtra("banner") != null) {
            strBanner = intent.getStringExtra("banner")!!
        }

        if (intent.hasExtra("categoryID")) {
            strCategoryID = intent.getStringExtra("categoryID")!!
            originalCategory = strCategoryID
            isFromCategories = strCategoryID != ""
        } else {
            isFromCategories = false
        }
      // println("categories :: " + intent.getStringExtra("categoryID"))
       // println("is From brand: " + isFromStr)
        if (isFromStr == "categories") {
            strCategoryID = Global.getPreferenceCategory(this@ProductListActivity)
            strFilterCategoryID = intent.getStringExtra("categoryID").toString()
            ivShare.visibility = View.GONE
        }

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
        layoutManager = GridLayoutManager(this@ProductListActivity, 2)
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
                    if (adapterProductGrid?.getItemViewType(position) == TYPE_TV) {
                        type = 2
                    }

                    return type
                }
            }
        spanSizeLookip.isSpanIndexCacheEnabled = false
        layoutManager.spanSizeLookup = spanSizeLookip
        arrListPageWiseData = ArrayList()
        adapterProductGrid =
            com.app.blockaat.productlisting.adapter.ProductListAdapter(
                this@ProductListActivity,
                arrListPageWiseData,
                this,
                isFromStr,
                hasSubcategory == "yes",
                this,
                onTvClickListener,
                strHeader,
                this,
                this
            )
        adapterProductGrid?.setHasStableIds(true)
        rcyProductListing.adapter = adapterProductGrid
        (rcyProductListing.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        rcyProductListing.layoutManager = layoutManager
        rcyProductListing.addItemDecoration(
            com.app.blockaat.helper.StickHeaderItemDecoration(
                adapterProductGrid as com.app.blockaat.helper.StickHeaderItemDecoration.StickyHeaderInterface,
                object : OnHeaderDisplay {
                    override fun isHeaderDisplay(visible: Boolean) {
                        if (visible) {
                            if (relRefine.visibility == View.GONE) {
                                relRefine.visibility = View.VISIBLE
                            }
                        } else {
                            if (relRefine.visibility == View.VISIBLE) {
                                relRefine.visibility = View.GONE
                            }
                        }
                    }

                })
        )
        rcyProductListing.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                1,
                false,
                Global.isEnglishLanguage(this)
            )
        )
        rcyProductListing.addItemDecoration(GridDividerItemDecoration(1))


        //rcyProductListing.isNestedScrollingEnabled = false
        arrListTv = ArrayList()
        adapterTv = TvListAdapter(this@ProductListActivity, arrListTv, onTvClickListener)
        val tvLayoutManager = GridLayoutManager(this@ProductListActivity, 2)
        val tvSpanSizeLookip: GridLayoutManager.SpanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    var type = 1
                    if (adapterTv?.getItemViewType(position) == 0) {
                        type = 2
                    }

                    return type
                }
            }
        tvSpanSizeLookip.isSpanIndexCacheEnabled = false
        tvLayoutManager.spanSizeLookup = tvSpanSizeLookip
        rvTvList.layoutManager = tvLayoutManager
        rvTvList.adapter = adapterTv

        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load product list data
                productListByCatID()
            }, 1000)
        }

        nestedScrollViews.viewTreeObserver
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                val view: View = nestedScrollViews.getChildAt(nestedScrollViews.childCount - 1) as View
                val diff: Int =
                    view.bottom - (nestedScrollViews.height + nestedScrollViews
                        .scrollY)
                if (!isPageLoading && !isLastPage) {
                    if (diff == 0) { // your pagination code
                        isPageLoading = true
                        currentPage += 1
                        loadMoreProducts()
                    }
                }

                swipeRefreshLayout.isEnabled = nestedScrollViews.scrollY == (nestedScrollViews.getChildAt(0).getMeasuredHeight() - nestedScrollViews.getMeasuredHeight())
            })

/*
        rcyProductListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView.childCount == 0) 0 else recyclerView.getChildAt(0).top
                swipeRefreshLayout.isEnabled = topRowVerticalPosition >= 0


                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isPageLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                        isPageLoading = true
                        currentPage += 1
                        loadMoreProducts()
                    }
                }
            }
        })
*/


        if (intent.hasExtra("isFromBrands") && intent.getStringExtra("isFromBrands") == "yes") {
            isFromBrands = true
        }


        ivSortRecommendedTick.isChecked = true
        txtRecommended.typeface = Global.fontMedium

        relSortHighToLow.setOnClickListener {
            strSortBy = "3"
            txtSortHighToLow.typeface = Global.fontSemiBold
            txtSortLowToHigh.typeface = Global.fontRegular
            txtRecommended.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = true
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
            if (adapterProductGrid != null) {
                adapterProductGrid?.removeLoadingFooter()
            }
            productListByCatID()
        }

        relSortLowToHigh.setOnClickListener {
            strSortBy = "4"
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontSemiBold
            txtRecommended.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = true
            ivSortRecommendedTick.isChecked = false
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
            if (adapterProductGrid != null) {
                adapterProductGrid?.removeLoadingFooter()
            }
            productListByCatID()
        }


        relSortLatest.setOnClickListener {
            strSortBy = "1"
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontRegular
            txtRecommended.typeface = Global.fontSemiBold
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = true
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
            if (adapterProductGrid != null) {
                adapterProductGrid?.removeLoadingFooter()
            }
            productListByCatID()
        }

        relSortNewIn.setOnClickListener {
            strSortBy = "2"
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontSemiBold
            txtRecommended.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = true
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            simpleViewSort.visibility = View.GONE
            viewTransparent.visibility = View.GONE
            if (adapterProductGrid != null) {
                adapterProductGrid?.removeLoadingFooter()
            }
            productListByCatID()
        }

        //lnrFilter.layoutParams.height = Global.getDeviceHeight(this@ProductListActivity)-resources.getDimensionPixelSize(R.dimen.forty_dp)
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.VISIBLE
        relWishlistImage.visibility = View.GONE
        ivShare.setOnClickListener(View.OnClickListener {
            val shareIntent = ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setChooserTitle("")
                .setText(getString(R.string.share_app_msg) + "https://play.google.com/store/apps/details?id=" + packageName)
                .intent

            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(shareIntent)
            }
        })
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
        txtItemCount.typeface = Global.fontMedium
        txtRefine.typeface = Global.fontRegular
        txtSortHighToLow.typeface = Global.fontMedium
        txtSortLowToHigh.typeface = Global.fontMedium
        txtRecommended.typeface = Global.fontMedium
        txtSortNewIn.typeface = Global.fontMedium
        txtSortOptionTitle.typeface = Global.fontBold
        txtFilterByLabel.typeface = Global.fontBold
        txtApply.typeface = Global.fontBtn
        txtClearAll.typeface = Global.fontBtn
        txtWishlistCount.typeface = Global.fontRegular
        txtMyBoutikey.typeface = Global.fontMedium
        txtTv.typeface = Global.fontMedium
    }


    private fun productListByCatID() {
      //  println("HERE I AM  prodcut list 1")
        if (NetworkUtil.getConnectivityStatus(this@ProductListActivity) != 0) {
            if (!isFromRefresh!!) {
                showProgressDialog(this@ProductListActivity)
            }
            if (!strFilterBrandID.isNullOrEmpty()){
                strBrandID = strFilterBrandID
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strBrandID)  //perviously pass strFilterBrandID
            jsonObject.addProperty("category_id", strFilterCategoryID)
            disposable = Global.apiService.getProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.SearchWs + Global.getLanguage(this)
                        + "&category_id=" + strCategoryID + "&price_range=" + strRange + "&page=" + PAGE_START + "&per_page=10" +
                        "&sort_by=" + strSortBy + "&latest=" + strLatest
                        + "&is_featured=" + strIsFeatured + "&user_id=" + Global.getUserId(this) + "&influencer_id=" + strInfluencerID +
                        "&store=" + Global.getStoreCode(this) + "&tv_id=" + strTvID
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.data?.products != null && result.status == 200) {
                                arrListPageWiseData.clear()


                                isLastPage = false
                                currentPage = 1
                                totalPages = result.data.total_pages as Int
                                totalItems = Global.stringToInt(result.data.total_products)
                                if (!isPageloaded) {
                                    arrListFilterData = ArrayList()
                                    arrListFilterData =
                                        result.data.filter as ArrayList<ProductListingFilterModel?>?
                                    setFilterData()
                                    isPageloaded = true
                                }
                                /* if(adapterProductGrid!=null)
                                 {
                                     adapterProductGrid?.removeLoadingFooter()
                                 }*/

                                if (totalPages > 0) {
                                    lnrMainViewHolder.visibility = View.VISIBLE // if data show list
                                    linNoItems.visibility = View.GONE
                                    // lnrRefine.visibility = View.VISIBLE
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            arrListSubCategory = arrListSubCategory,
                                            totalItem = totalItems
                                        )
                                    )
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            category_tvs = null,
                                            totalItem = totalItems
                                        )
                                    )

                                    if (isFromStr.equals("celebrityHome") || isFromStr.equals("celebrity")) {
                                        linCelebrity?.visibility = View.VISIBLE
                                        if (!result.data.influencer_details?.tvs.isNullOrEmpty()) {
                                            arrListTv?.clear()
                                            if (!strBanner.isNullOrEmpty()) {
                                                arrListTv?.add(Tvs(banner = strBanner))
                                            } else {
                                                arrListTv?.add(Tvs(banner = ""))

                                            }
                                            arrListTv?.addAll(result.data.influencer_details?.tvs as Collection<Tvs>)
                                            adapterTv?.notifyDataSetChanged()
                                            linCelebrity?.visibility = View.VISIBLE

                                        } else {
                                            linCelebrity?.visibility = View.GONE
                                        }

                                        if (!strInfluencerID.isNullOrEmpty()) {
                                            var topBanner: TopBanner? = null
                                            if (!strBanner.isNullOrEmpty()) {
                                                topBanner = TopBanner(
                                                    banner = strBanner,
                                                    type = isFromStr
                                                )
                                            }
                                            val influencerDetails =
                                                InfluencerDetails(banners = result.data.influencer_details?.banners)
                                            arrListPageWiseData.add(
                                                ProductListingProductModel(
                                                    topBanner = topBanner,
                                                    influencer_details = influencerDetails,
                                                    totalItem = totalItems
                                                )
                                            )
                                        } else {
                                            arrListPageWiseData.add(
                                                ProductListingProductModel(
                                                    totalItem = totalItems
                                                )
                                            )

                                        }

                                    } else {
                                        linCelebrity.visibility = View.GONE
                                        if (result.data.influencer_details?.banners.isNullOrEmpty() && arrListSubCategory.isNullOrEmpty() && !strBanner.isNullOrEmpty()) {
                                            val topBanner = TopBanner(
                                                banner = strBanner,
                                                type = isFromStr
                                            )
                                            arrListPageWiseData.add(
                                                ProductListingProductModel(
                                                    topBanner = topBanner,
                                                    totalItem = totalItems
                                                )
                                            )
                                        } else {

                                            arrListTv?.clear()
                                            tvData?.let { arrListTv?.add(it) }
                                            arrListPageWiseData.add(

                                                ProductListingProductModel(

                                                    influencer_details = InfluencerDetails(tvs = arrListTv as ArrayList<Tvs?>),
                                                    totalItem = totalItems
                                                )
                                            )
                                        }
                                    }
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            totalItem = totalItems
                                        )
                                    )
                                    result.data.products.let { arrListPageWiseData.addAll(it) }
                                    txtItemCount.text =
                                        totalItems
                                            .toString() + " " + resources
                                            .getString(R.string.items)
                                    originalMaxPrice = result.data?.max_product_price

                                    if (currentPage < totalPages) {

                                        adapterProductGrid?.addLoadingFooter()

                                    } else {
                                        isLastPage = true

                                    }
                                    adapterProductGrid?.notifyDataSetChanged()

                                } else {

                                    lnrMainViewHolder.visibility = View.GONE // if data show list
                                    linNoItems.visibility = View.VISIBLE
                                    relRefine.visibility = View.GONE
                                }


                            } else if (result.status == 404) {
                                // $this->response_code = 404;
                                //$this->message = 'No product match with your search criteria.please try by another keyword';
                                lnrMainViewHolder.visibility = View.GONE  //hide list if no data
                                if (!isFromFilter) {
                                    relRefine.visibility = View.GONE
                                }
                                linNoItems.visibility = View.VISIBLE

                            } else {
                                linNoItems.visibility = View.VISIBLE
                                if (!isFromFilter) {
                                    relRefine.visibility = View.GONE
                                }
                                lnrMainViewHolder.visibility = View.GONE
                                Global.showSnackbar(
                                    this@ProductListActivity as AppCompatActivity,
                                    result.message.toString()
                                )

                            }
                        } else {
                            linNoItems.visibility = View.VISIBLE
                            lnrMainViewHolder.visibility = View.GONE
                            if (!isFromFilter) {
                                relRefine.visibility = View.GONE
                            }
                            Global.showSnackbar(
                                this@ProductListActivity as AppCompatActivity,
                                getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                     //   println("ERROR - Product List Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        lnrMainViewHolder.visibility = View.GONE
                        linNoItems.visibility = View.VISIBLE
                        if (!isFromFilter) {
                            relRefine.visibility = View.GONE
                        }
                        Global.showSnackbar(this@ProductListActivity, getString(R.string.error))
                    }
                )
        }
    }

    private fun setFilterData() {

        if (!arrListFilterData.isNullOrEmpty()) {
            for (filter in arrListFilterData!!) {

                if (filter?.filter_values.isNullOrEmpty()) {
                    arrListFilterData?.remove(filter)
                    break
                }

            }
            for (filter in arrListFilterData!!) {
                if ((filter?.filter_type.equals(
                        "Brands",
                        true
                    ) || filter?.filter_type.equals("Brand")) && isFromStr.equals("brand")
                ) {
                    arrListFilterData?.remove(filter)
                    break
                }
            }
            for (filter in arrListFilterData!!) {
                if ((filter?.filter_type.equals(
                        "Categories",
                        true
                    ) || filter?.filter_type.equals("Category")) && isFromStr.equals("categories")
                ) {
                    arrListFilterData?.remove(filter)
                    break
                }
            }
            adapterFilter = FilterPagerAdapter(supportFragmentManager, arrListFilterData!!)
            viewpager.adapter = adapterFilter
            if (arrListFilterData?.size ?: 0 > 3) {
                tabFilter.tabMode = TabLayout.MODE_SCROLLABLE
            }

            tabFilter.setupWithViewPager(viewpager)
        }

    }

    private fun loadMoreProducts() {
        if (NetworkUtil.getConnectivityStatus(this@ProductListActivity) != 0) {

            if (!strFilterBrandID.isNullOrEmpty()){
                strBrandID = strFilterBrandID
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strBrandID)  //perviously pass strFilterBrandID
            jsonObject.addProperty("category_id", strFilterCategoryID)

           // println("HERE I AM LOADING MORE PRODUCT 2")

            disposable = Global.apiService.getProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.SearchWs + Global.getStringFromSharedPref(
                    this@ProductListActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&category_id=" + strCategoryID + "&most_selling=" + isFromMostSelling + "&price_range=" + strRange + "&page=" + currentPage + "&per_page=10" + "&sort_by=" + strSortBy
                        + "&is_featured=" + strIsFeatured + "&user_id=" + Global.getStringFromSharedPref(
                    this@ProductListActivity,
                    Constants.PREFS_USER_ID
                ) + "&influencer_id=" + strInfluencerID + "&store=" + Global.getStringFromSharedPref(
                    this@ProductListActivity,
                    Constants.PREFS_STORE_CODE
                ) + "&tv_id=" + strTvID
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    hideProgressDialog()

                    if (result != null) {
                        totalPages = result.data?.total_pages as Int
                        adapterProductGrid!!.removeLoadingFooter()

                        isPageLoading = false
                        result.data.products?.let { arrListPageWiseData.addAll(it) }
                        // adapterProductGrid!!.addAll(arrListPageWiseData)

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

    override fun onSubcategorySelected(subCategory: Subcategory) {
        val intent = Intent(this@ProductListActivity, ProductListActivity::class.java)
        intent.putExtra("categoryID", subCategory.id.toString())
        intent.putExtra("header_text", subCategory.name)
        if (subCategory.has_subcategory == "Yes") {
            intent.putExtra("hasSubcategory", subCategory.has_subcategory)
            intent.putParcelableArrayListExtra("subcategoryList", subCategory.subcategories)
        }
        startActivity(intent)
    }

    private fun showProgressDialog(activity: Activity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        isFromRefresh = false
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

        if (adapterProductGrid != null) {
            adapterProductGrid!!.notifyDataSetChanged()
        }
        if (Global.isUserLoggedIn(this)) {

            txtWishlistCount.visibility = View.GONE
            if (Global.getTotalWishListProductCount(this) > 0) {
                txtWishlistCount.visibility = View.VISIBLE
                txtWishlistCount.text =
                    Global.getTotalWishListProductCount(this).toString()
            } else {
                txtWishlistCount.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()

    }

    override fun onHeaderClicked(type: String) {
        if (type == "sort") {
            linRefine.performClick()
        } else {
            linRefine.performClick()
        }
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

    private val onTvClickListener = object : OnTvClickListener {
        override fun onTvClickListener(type: String, position: Int, itemPosition: Int) {
            if (!arrListTv.isNullOrEmpty()) {
                val i = Intent(this@ProductListActivity, ProductListActivity::class.java)
                i.putExtra("header_text", arrListTv?.get(position)?.name)
                i.putExtra("parent_type", "product")
                i.putExtra("isFrom", "product")
                i.putExtra("tv_id", arrListTv?.get(position)?.id.toString())
                i.putExtra("tvData", arrListTv?.get(position))
                i.putExtra("categoryID", Global.getPreferenceCategory(this@ProductListActivity))
                startActivity(i)
            }
        }

    }

    override fun onProductClicked(product: ProductListingProductModel) {
      //  println("Category to details: " + strCategoryID)
        val intent = Intent(this@ProductListActivity, ProductDetailsActivity::class.java)
        intent.putExtra("strProductID", product.id.toString())
        intent.putExtra("strProductName", product.name.toString())
        intent.putExtra("categoryID", strCategoryID)
        startActivity(intent)
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

    override fun onProductClicked(product: ProductListingProductModel, type: String) {
        updateCollection()
    }

    override fun onClickItem(
        position: Int,
        type: String,
        link_type: String,
        link_id: String,
        header: String
    ) {
        if (link_type.toLowerCase() == "c") {
            val i = Intent(this@ProductListActivity, ProductListActivity::class.java)
            i.putExtra("header_text", header)
            i.putExtra("parent_type", "product")
            i.putExtra("isFrom", "categories")
            i.putExtra("categoryID", link_id)
            startActivity(i)
        } else if (link_type.toLowerCase().toString().toLowerCase() == "p") {
            val intent = Intent(this@ProductListActivity, ProductDetailsActivity::class.java)
            intent.putExtra("strProductID", link_id.toString())
            intent.putExtra("strProductName", header.toString())
            startActivity(intent)
        } else if (link_type.toString().toLowerCase() == "t" || link_type.toString()
                .toLowerCase() == "v"
        ) {
            val intent = Intent(this@ProductListActivity, TvProductActivity::class.java)
            intent.putExtra("tvID", link_id.toString())
            intent.putExtra("header_text", header)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeFilter && data != null && data.hasExtra("applyFilter") && data.extras?.getString(
                "applyFilter"
            ) == "yes" && data.hasExtra("applyFilter")
        ) {
            strMinPrice = data.extras?.getString("minPrice").toString()
            strMaxPrice = data.extras?.getString("maxPrice").toString()
            originalMaxPrice = data.extras?.getString("originalMaxPrice").toString()
            strRange = data.extras?.getString("priceRange").toString()
            strSortBy = data.extras?.getString("sortBy").toString()
            arrListFilterData =
                data.getSerializableExtra("filterData") as? java.util.ArrayList<ProductListingFilterModel?>
            if (data.extras?.getInt("noFilterApplied") ?: 0 > 0) {
                isFromFilter = true
                //filter by

                //noOfFilteredApplied = data.getIntExtra("noFilterApplied", 0)
                val json = JsonObject() // json form for attribute
                strAttributeID = ""
                strFilterCategoryID = ""
                strFilterBrandID = ""

                //category - filter
                for (u in 0 until (arrListFilterData?.size ?: 0)) {
                    if (arrListFilterData?.get(u)?.filter_type.toString()
                            .toLowerCase() == "categories"
                    ) {
                        for (v in 0 until (arrListFilterData?.get(u)?.filter_values?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.filter_values?.get(v)?.isSelected == true) {
                                strFilterCategoryID = strFilterCategoryID + "," +
                                        arrListFilterData?.get(u)?.filter_values?.get(v)?.id.toString()

                            }
                        }
                    } else if (arrListFilterData?.get(u)?.filter_type.toString()
                            .toLowerCase() == "brand"
                    ) {
                        for (v in 0 until (arrListFilterData?.get(u)?.filter_values?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.filter_values?.get(v)?.isSelected == true) {
                                strFilterBrandID =
                                    strFilterBrandID + "," + arrListFilterData?.get(u)?.filter_values?.get(
                                        v
                                    )?.id.toString()

                            }
                        }
                    } else {
                        for (v in 0 until (arrListFilterData?.get(u)?.filter_values?.size ?: 0)) {
                            if (arrListFilterData?.get(u)?.filter_values?.get(v)?.isSelected == true) {
                                strAttributeID =
                                    strAttributeID + "," + arrListFilterData?.get(u)?.filter_values?.get(
                                        v
                                    )?.id.toString()

                            }
                        }

                    }
                }
                if (strFilterCategoryID.isNotEmpty()) {
                    strFilterCategoryID = strFilterCategoryID.substring(1)
                }
                if (strFilterBrandID.isNotEmpty()) {
                    strFilterBrandID = strFilterBrandID.substring(1)
                }
                if (strAttributeID.isNotEmpty()) {
                    strAttributeID = strAttributeID.substring(1)
                }
                productListByCatID()
            } else {
                isFromFilter = false
                strAttributeID = ""
                strFilterCategoryID = ""
                strFilterBrandID = ""
                productListByCatID()
            }
        }

    }


}
