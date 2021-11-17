package com.app.blockaat.category.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.app.blockaat.Filter.activity.SortFilterActivity
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.helper.Global.hideProgressDialog
import com.app.blockaat.helper.Global.showProgressDialog
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.adapter.TvListAdapter
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.interfaces.*
import com.app.blockaat.productlisting.model.*
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.tv.activity.AllTvListActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sub_category.*
import kotlinx.android.synthetic.main.activity_sub_category.linRefine
import kotlinx.android.synthetic.main.activity_sub_category.lnrMainViewHolder
import kotlinx.android.synthetic.main.activity_sub_category.rcyProductListing
import kotlinx.android.synthetic.main.activity_sub_category.relRefine
import kotlinx.android.synthetic.main.activity_sub_category.swipeRefreshLayout
import kotlinx.android.synthetic.main.activity_sub_category.txtItemCount
import kotlinx.android.synthetic.main.activity_sub_category.txtRefine
import kotlinx.android.synthetic.main.fragment_sub_category_data.*
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_sort.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view
import kotlinx.android.synthetic.main.toolbar_default.viewStart
import kotlinx.android.synthetic.main.toolbar_default_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SubCategoryActivity : BaseActivity(), OnHeaderClicked, OnSubcategorySelectListener,
    OnProductListListener, HomeItemClickInterface, OnTvClickListener {

    private lateinit var productsDBHelper: DBHelper
    private var adapterProductGrid: com.app.blockaat.productlisting.adapter.ProductListAdapter? =
        null
    private var strInfluencerID = ""
    private var strBanner = ""
    private var strBrandID = ""
    private var strOriginalBrandID = ""

    private var strCategoryID = ""
    private var strFilterCategoryID = ""
    private var strFilterBrandID = ""
    private var disposable: Disposable? = null
    private var originalCategory = ""
    private var isFromMostSelling = "" // it will be 1 for most selling else 0
    private lateinit var layoutManager: GridLayoutManager
    private var strPrice: String = ""
    private var strAttributeID: String = "" //size as well as color ids
    private var strSortBy: String = "1"
    private var isFromRefresh: Boolean = false
    private var totalPages = 0
    private var isLastPage = false
    private val PAGE_START = 1
    private var currentPage = PAGE_START
    private var totalItems = 0
    private lateinit var arrListPageWiseData: ArrayList<ProductListingProductModel>
    private var isPageLoading: Boolean = false
    var strLatest = ""
    var tvData: Tvs? = null
    private var arrListFilterData: ArrayList<ProductListingFilterModel?>? = null
    private var arrListTv: ArrayList<Tvs>? = null
    private var adapterTv: TvListAdapter? = null
    private var isFromCategories = false
    private var strTvID = ""
    private var strIsFeatured = "0"
    private var dialog: CustomProgressBar? = null
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
    private var bundle: Bundle? = null
    private var parentType: String? = null
    private var isFromStr: String? = ""
    private var has_subcategory: String? = ""
    private var isFromBrands: Boolean? = false
    private var header = ""
    private var tvId = ""


    //filter
    private var strMinPrice: String = "0.0"
    private var strRange: String = ""
    private var strMaxPrice: String = "0.0"
    private var originalMaxPrice: String? = "0.0"
    private var isPricePresent: Boolean = false
    private val requestCodeFilter: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub_category)
        Global.setLocale(this@SubCategoryActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        setEmptyPage()

    }

    private fun setEmptyPage() {
        ivEmpty.setImageResource(R.drawable.ic_empty_products)
        ivEmpty.setColorFilter(ContextCompat.getColor(this, R.color.primary_button_color))
        txtEmptyTitle.text = getString(R.string.empty_products_heading)
        txtEmptyMessage.text = getString(R.string.empty_products_msg)
        txtEmptyBtn.text = getString(R.string.go_back)
        txtEmptyBtn.setBackgroundResource(R.drawable.blue_button_background)
        txtEmptyMessage.typeface = Global.fontSemiBold
        txtEmptyBtn.typeface = Global.fontBtn
        txtEmptyTitle.typeface = Global.fontNavBar
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        imgSearch.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        relWishlistImage.visibility = View.VISIBLE
    }

    private fun initializeFields() {
        productsDBHelper = DBHelper(this@SubCategoryActivity)

        Global.arrListFilter = ArrayList()
        if (!intent?.getStringExtra("parent_type").isNullOrEmpty()) {
            parentType = intent?.getStringExtra("parent_type")
        }
        if (!intent?.getStringExtra("header_text").isNullOrEmpty()) {
            //txtHead.text = intent.getStringExtra("header_text")
            txtHead.text = intent?.getStringExtra("header_text").toString()
            header = intent?.getStringExtra("header_text").toString()
        }
        if (!intent?.getStringExtra("has_subcategory").isNullOrEmpty()) {
            //txtHead.text = intent.getStringExtra("header_text")
            has_subcategory = intent?.getStringExtra("has_subcategory").toString().toLowerCase()
        }

        if (!intent?.getStringExtra("isFrom").isNullOrEmpty()) {
            isFromStr = intent?.getStringExtra("isFrom")

        }

        if (intent?.getStringExtra("tvData") != null) {
            tvData = intent?.getSerializableExtra("tvData") as Tvs
        }


        if (intent.hasExtra("tv_id") && intent.getStringExtra("tv_id") != null) {
            strTvID = intent?.getStringExtra("tv_id").toString()
            tvId = bundle?.getString("tv_id").toString()

        }

        if (intent.hasExtra("tvID") && intent.getStringExtra("tvID") != null) {
            strTvID = intent.getStringExtra("tvID")!!
        }


        if (intent?.hasExtra("categoryID") == true) {
            strCategoryID = intent?.getStringExtra("categoryID").toString()
            originalCategory = strCategoryID
            isFromCategories = strCategoryID != ""
        } else {
            isFromCategories = false
        }

        if (!intent?.getStringExtra("brandID").isNullOrEmpty()) {
            strBrandID = intent?.getStringExtra("brandID").toString()
        }

        if (!intent?.getStringExtra("isFeatured").isNullOrEmpty()) {
            strIsFeatured = intent?.getStringExtra("isFeatured").toString()
        }

        if (!intent?.getStringExtra("influencerID").isNullOrEmpty()) {
            strInfluencerID = intent?.getStringExtra("influencerID").toString()
        }

        if (!intent?.getStringExtra("banner").isNullOrEmpty()) {
            strBanner = intent?.getStringExtra("banner").toString()
        }

        if (!intent?.getStringExtra("isFromMostSellingBrands")
                .isNullOrEmpty() && intent?.getStringExtra("isFromMostSellingBrands") == "yes"
        ) {
            isFromMostSelling = "1"
        }


        layoutManager = GridLayoutManager(this@SubCategoryActivity, 2)
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
                this,
                arrListPageWiseData,
                this,
                isFromStr, has_subcategory == "yes",
                this,
                this,
                header,
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
                        /*if (visible) {
                            if (lnrRefine.visibility == View.GONE) {
                                lnrRefine.visibility = View.VISIBLE
                                viewRefine.visibility = View.VISIBLE
                            }
                        } else {
                            if (lnrRefine.visibility == View.VISIBLE) {
                                lnrRefine.visibility = View.GONE
                                viewRefine.visibility = View.GONE
                            }
                        }*/
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



        if (!bundle?.getString("isFromBrands")
                .isNullOrEmpty() && bundle?.getString("isFromBrands") == "yes"
        ) {
            isFromBrands = true
            strOriginalBrandID = strBrandID
        }


//        (activity as NavigationActivity).setSortListener(sortInterface)
        productListByCatID()
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtRefine.typeface = Global.fontRegular
        txtWishlistCount.typeface = Global.fontRegular
//        txtMyBoutikey.typeface = Global.fontMedium
        txtTv.typeface = Global.fontMedium
    }

    private fun onClickListeners() {
        /*  imgCloseSheet.setOnClickListener {
              simpleViewnimator.visibility = View.GONE
              viewTransparent.visibility = View.GONE
          }

          viewTransparent.setOnClickListener {
              simpleViewSort.visibility = View.GONE
              viewTransparent.visibility = View.GONE
          }*/

        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        imgSearch.setOnClickListener {
            val intent = Intent(this@SubCategoryActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        linRefine.setOnClickListener {
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

        /* linSort.setOnClickListener {
             if (simpleViewSort.visibility == View.VISIBLE) {
                 simpleViewSort.visibility = View.GONE
                 viewTransparent.visibility = View.GONE
             } else {
                 simpleViewSort.visibility = View.VISIBLE
                 viewTransparent.visibility = View.VISIBLE
             }

         }
 */
        /* imgClose.setOnClickListener {
             simpleViewnimator.visibility = View.GONE
             viewTransparent.visibility = View.GONE
         }*/

        ivBackArrow.setOnClickListener {
            finish()
        }

        txtEmptyBtn.setOnClickListener {
            finish()
        }


//        relMyBoutikey.setOnClickListener {
//            lnrMainViewHolder.visibility = View.VISIBLE
//            relTvList.visibility = View.GONE
//            viewBoutikey.visibility = View.VISIBLE
//            viewTv.visibility = View.GONE
//        }

//        relTv.setOnClickListener {
//            lnrMainViewHolder.visibility = View.GONE
//            relTvList.visibility = View.VISIBLE
//            viewBoutikey.visibility = View.GONE
//            viewTv.visibility = View.VISIBLE
//        }


    }

    private fun productListByCatID() {

        if (NetworkUtil.getConnectivityStatus(this) != 0) {

            if (!isFromRefresh) {
                showProgressDialog(this)
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)
            disposable = Global.apiService.getSubCategories(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.CategoryWs + Global.getSelectedLanguage(
                    this
                ) + "&category_id=" + strCategoryID + "&sort_by=" + strSortBy + "&page=" +
                        PAGE_START + "&per_page=10"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                          //  println("RESPONSE - Product List Ws :   " + Gson().toJson(result.data))
                            if (result.data?.category_products != null && result.status == 200) {
                                arrListPageWiseData.clear()


                                isLastPage = false
                                currentPage = 1
                                totalPages = result.data.category_products.total_pages as Int
                                totalItems =
                                    Global.stringToInt(result.data.category_products.total_products)
                                if (!isPageloaded) {
                                  //  println("New filter data")
                                    arrListFilterData = ArrayList()
                                    for (i in 0 until result.data.category_products.filter?.size!!) {
                                        if (isFromStr.toString()
                                                .toLowerCase() != result.data.category_products.filter[i].filter_type.toString()
                                                .toLowerCase()
                                        )
                                            arrListFilterData?.add(result.data.category_products.filter[i])
                                    }
//                                    arrListFilterData?.addAll(result.data.filter as ArrayList<ProductListingFilterModel?>)
                                 //   println("filter list size" + arrListFilterData?.size)
                                    setFilterData()
                                    isPageloaded = true
                                }
                                /* if(adapterProductGrid!=null)
                                 {
                                     adapterProductGrid?.removeLoadingFooter()
                                 }*/

                                if (totalPages > 0) {
                                    lnrMainViewHolder?.visibility =
                                        View.VISIBLE // if data show list
                                    relEmpty?.visibility = View.GONE
                                    // lnrRefine.visibility = View.VISIBLE
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            arrListSubCategory = result.data.subcategories as ArrayList<Subcategory>,
                                            totalItem = totalItems
                                        )
                                    )
//                                    arrListPageWiseData.add(ProductListingProductModel())

//                                    tv videos
                                    if (result.data.category_tvs != null && result.data.category_tvs.isNotEmpty()) {
                                        arrListPageWiseData.add(
                                            ProductListingProductModel(
                                                category_tvs = result.data.category_tvs as ArrayList<Tvs>,
                                                totalItem = totalItems

                                            )
                                        )
                                    } else {
                                        arrListPageWiseData.add(ProductListingProductModel(
                                            totalItem = totalItems

                                        ))
                                    }

                                    arrListPageWiseData.add(ProductListingProductModel(
                                        totalItem = totalItems

                                    ))

                                    /*          if(isFromStr.equals("celebrityHome")||isFromStr.equals("celebrity"))
                                              {
                                                  linCelebrity.visibility = View.VISIBLE
                                                  arrListPageWiseData.add(ProductListingProductModel())
                                                  if(!result.data.influencer_details?.tvs.isNullOrEmpty())
                                                  {
                                                      arrListTv?.addAll(result.data.influencer_details?.tvs as Collection<Tvs>)
                                                      adapterTv?.notifyDataSetChanged()
                                                      linCelebrity.visibility = View.VISIBLE

                                                  }else
                                                  {
                                                      linCelebrity.visibility = View.GONE
                                                  }

                                              }else{
                                                  linCelebrity.visibility = View.GONE
                                                  if (result.data.influencer_details?.banners.isNullOrEmpty() && arrListSubCategory.isNullOrEmpty() && !strBanner.isNullOrEmpty()) {
                                                      val topBanner = TopBanner(
                                                          banner = strBanner,
                                                          type = isFromStr
                                                      )
                                                      arrListPageWiseData.add(
                                                          ProductListingProductModel(
                                                              topBanner = topBanner
                                                          )
                                                      )
                                                  } else {

                                                      arrListTv?.clear()
                                                      tvData?.let { arrListTv?.add(it) }
                                                      arrListPageWiseData.add(

                                                          ProductListingProductModel(

                                                              influencer_details = InfluencerDetails(tvs = arrListTv as ArrayList<Tvs?>)
                                                          )
                                                      )
                                                  }
                                              }*/

                                    arrListPageWiseData.add(ProductListingProductModel(
                                        totalItem = totalItems

                                    ))

                                    arrListPageWiseData.addAll(result.data.category_products.products as Collection<ProductListingProductModel>)
                                    originalMaxPrice = result.data?.category_products.max_product_price
                                    txtItemCount.text = totalItems.toString()

                                    if (currentPage < totalPages) {

                                        adapterProductGrid?.addLoadingFooter()

                                    } else {
                                        isLastPage = true

                                    }
                                    adapterProductGrid?.notifyDataSetChanged()

                                } else {

                                    lnrMainViewHolder?.visibility = View.GONE // if data show list
                                    relEmpty?.visibility = View.VISIBLE
                                    relRefine?.visibility = View.GONE
                                }


                            } else if (result.status == 404) {
                                // $this->response_code = 404;
                                //$this->message = 'No product match with your search criteria.please try by another keyword';
                                lnrMainViewHolder?.visibility = View.GONE  //hide list if no data
                                if (!isFromFilter) {
                                    relRefine?.visibility = View.GONE
                                }
                                relEmpty?.visibility = View.VISIBLE

                            } else {
                                relEmpty?.visibility = View.VISIBLE
                                if (!isFromFilter) {
                                    relRefine?.visibility = View.GONE
                                }
                                lnrMainViewHolder?.visibility = View.GONE
                                Global.showSnackbar(
                                    this,
                                    result.message.toString()
                                )

                            }
                        } else {
                            relEmpty?.visibility = View.VISIBLE
                            lnrMainViewHolder?.visibility = View.GONE
                            if (!isFromFilter) {
                                relRefine?.visibility = View.GONE
                            }
                            Global.showSnackbar(
                                this,
                                getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Product List Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        lnrMainViewHolder?.visibility = View.GONE
                        relEmpty?.visibility = View.VISIBLE
                        if (!isFromFilter) {
                            relRefine?.visibility = View.GONE
                        }
                        Global.showSnackbar(this, getString(R.string.error))
                    }
                )
        }
    }

    private fun loadMoreProducts() {
        if (NetworkUtil.getConnectivityStatus(this@SubCategoryActivity) != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)
            jsonObject.addProperty("category_id", strFilterCategoryID)

            println("HERE I AM LOADING MORE PRODUCT")

            disposable = Global.apiService.getProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.SearchWs + Global.getStringFromSharedPref(
                    this@SubCategoryActivity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&category_id=" + strCategoryID + "&most_selling=" + isFromMostSelling + "&price_range=" + strPrice + "&page=" + currentPage + "&per_page=10" + "&sort_by=" + strSortBy
                        + "&is_featured=" + strIsFeatured + "&user_id=" + Global.getStringFromSharedPref(
                    this@SubCategoryActivity,
                    Constants.PREFS_USER_ID
                ) + "&influencer_id=" + strInfluencerID + "&store=" + Global.getStringFromSharedPref(
                    this@SubCategoryActivity,
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

    private fun setFilterData() {

        if (!arrListFilterData.isNullOrEmpty()) {
            adapterFilter = FilterPagerAdapter(supportFragmentManager, arrListFilterData!!)
            viewpager.adapter = adapterFilter
            tabFilter.setupWithViewPager(viewpager)
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


    override fun onHeaderClicked(type: String) {
        if (type == "sort") {
//            linSort.performClick()
        } else {
            linRefine.performClick()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayProductList(subCategory: Subcategory) {
        if (subCategory.has_subcategory == "Yes") {
            val intent = Intent(this@SubCategoryActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", subCategory.id.toString())
            intent.putExtra("header_text", subCategory.name)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "subCategory")
            intent.putExtra("has_subcategory", subCategory.has_subcategory)
          //  intent.putParcelableArrayListExtra("subcategories", subCategory.subcategories)
            startActivity(intent)
        } else {
            val intent = Intent(this@SubCategoryActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", subCategory.id.toString())
            intent.putExtra("header_text", subCategory.name)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "categories")
            intent.putExtra("banner", subCategory.image)
            intent.putExtra("has_subcategory", "no")
            startActivity(intent)
        }
    }

    override fun onSubcategorySelected(subCategory: Subcategory) {
        if (subCategory.has_subcategory == "Yes") {
            val intent = Intent(this@SubCategoryActivity, SubCategoryActivity::class.java)
            intent.putExtra("categoryID", subCategory.id.toString())
            intent.putExtra("header_text", subCategory.name)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "subCategory")
            startActivity(intent)
        } else {
            val intent = Intent(this@SubCategoryActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", subCategory.id.toString())
            intent.putExtra("header_text", subCategory.name)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "categories")
            intent.putExtra("banner", subCategory.image)
            intent.putExtra("has_subcategory", "no")
            startActivity(intent)
        }

    }

    override fun onProductClicked(product: ProductListingProductModel) {
        val intent = Intent(this@SubCategoryActivity, ProductDetailsActivity::class.java)
        intent.putExtra("strProductID", product.id.toString())
        intent.putExtra("strProductName", product.name.toString())
        startActivity(intent)
    }

    override fun onProductClicked(product: ProductListingProductModel, type: String) {
        updateCollection()

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
        EventBus.getDefault().register(this@SubCategoryActivity)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this@SubCategoryActivity)

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()

    }

    //to change wishlist item icon
    private fun updateCollection() {
        for (i in 0 until arrListPageWiseData?.size) {
            arrListPageWiseData[i].item_in_wishlist = 0
            if (productsDBHelper.isProductPresentInWishlist(
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
        if (type == "itemClick") {
            val tvModel = CelebrityTvList(
                arrListPageWiseData?.get(position).category_tvs?.get(itemPosition)
            )
            val intent = Intent(this, ProductListActivity::class.java)
            intent.putExtra(
                "header_text",
                tvModel?.tvs?.name
            )
            intent.putExtra(
                "categoryID",
                Global.getPreferenceCategory(this)
            )
            intent.putExtra("parent_type", "home")
            intent.putExtra("isFrom", "home")
            intent.putExtra("has_subcategory", "yes")
            intent.putExtra("tv_id", tvModel?.tvs?.id)
            intent.putExtra("tvData", tvModel?.tvs)
            startActivity(intent)
        } else if (type == "viewAll") {
            val i = Intent(this, AllTvListActivity::class.java)
            i.putExtra("categoryId", strCategoryID)
            startActivity(i)
        }
    }

}