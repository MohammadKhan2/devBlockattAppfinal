package com.app.blockaat.productlisting.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.Filter.activity.SortFilterActivity
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.helper.Global.loadImagesUsingGlide
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.adapter.TvListAdapter
import com.app.blockaat.productlisting.interfaces.*
import com.app.blockaat.productlisting.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_list.*
import kotlinx.android.synthetic.main.fragment_product_list.*
import kotlinx.android.synthetic.main.fragment_product_list.linCelebrity
import kotlinx.android.synthetic.main.fragment_product_list.linRefine
import kotlinx.android.synthetic.main.fragment_product_list.lnrMainViewHolder
import kotlinx.android.synthetic.main.fragment_product_list.rcyProductListing
import kotlinx.android.synthetic.main.fragment_product_list.relMyBoutikey
import kotlinx.android.synthetic.main.fragment_product_list.relRefine
import kotlinx.android.synthetic.main.fragment_product_list.relTv
import kotlinx.android.synthetic.main.fragment_product_list.relTvList
import kotlinx.android.synthetic.main.fragment_product_list.rvTvList
import kotlinx.android.synthetic.main.fragment_product_list.swipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_product_list.txtItemCount
import kotlinx.android.synthetic.main.fragment_product_list.txtMyBoutikey
import kotlinx.android.synthetic.main.fragment_product_list.txtRefine
import kotlinx.android.synthetic.main.fragment_product_list.txtTv
import kotlinx.android.synthetic.main.fragment_product_list.viewBoutikey
import kotlinx.android.synthetic.main.fragment_product_list.viewTv
import kotlinx.android.synthetic.main.layout_empty_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductListFragment : Fragment(), OnHeaderClicked, OnSubcategorySelectListener,
    OnProductListListener, HomeItemClickInterface, OnTvClickListener {

    private var jsonAttributes = JsonObject()
    private val requestCodeFilter: Int = 100
    private var myTvLoaded: Boolean = false
    private lateinit var mActivity: Activity
    private lateinit var productsDBHelper: DBHelper
    private var param1: String? = null
    private var param2: String? = null
    private var disposable: Disposable? = null
    private var adapterProductGrid: com.app.blockaat.productlisting.adapter.ProductListAdapter? =
        null
    private var strInfluencerID = ""
    private var strBanner = ""
    private var strBrandID = ""
    private var strOriginalBrandID = ""

    private var strCategoryID = "1"
    private var strFilterCategoryID = ""
    private var strFilterBrandID = ""

    private var originalCategory = ""
    private var isFromMostSelling = "" // it will be 1 for most selling else 0
    private lateinit var layoutManager: GridLayoutManager
    private var strAttributeID: String = "" //size as well as color ids
    private var strSortBy: String = "1"
    private var isFromRefresh: Boolean = false
    private var totalPages = 0
    private var isLastPage = false
    private val PAGE_START = 1
    private var currentPage = PAGE_START
    private lateinit var arrListPageWiseData: ArrayList<ProductListingProductModel>
    private var isPageLoading: Boolean = false
    var strLatest = ""
    var tvData: Tvs? = null
    private var arrListFilterData: ArrayList<ProductListingFilterModel?>? = null
    private var arrListTv: ArrayList<Tvs>? = null
    private var adapterTv: TvListAdapter? = null
    private var isFromCategories = false

    private var strIsFeatured = "0"
    private var dialog: CustomProgressBar? = null
    private val TYPE_SUBCATEGORY = 0
    private val TYPE_TV = 1
    private val TYPE_BANNER = 2
    private val TYPE_HEADER = 3
    private val TYPE_ITEM = 4
    private val TYPE_LOADING = 5
    private var adapterFilter: NavigationActivity.FilterPagerAdapter? = null
    private var isFromFilter: Boolean = false
    private var isPageloaded: Boolean = false
    private var arrListSubCategory: ArrayList<Subcategory>? = null
    private var bundle: Bundle? = null
    private var parentType: String? = null
    private var isFromStr: String? = ""
    private var has_subcategory: String? = ""
    private var totalItem: Int = 0
    private var isFromBrands: Boolean? = false
    private var header = ""
    private var tvId = ""
    private var isFrom = ""
    private var nestedScrollView: NestedScrollView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    //filter
    private var strMinPrice: String = "0.0"
    private var strRange: String = ""
    private var strCategoryEmpt: String = ""
    private var strMaxPrice: String = "0.0"
    private var originalMaxPrice: String? = "0.0"
    private var isPricePresent: Boolean = false

    private var image: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mActivity = context as Activity
        productsDBHelper = DBHelper(context as Activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bundle = arguments
        //  Log.e("TopBanner", "ProductListFragment")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        setFonts()
        onClickListeners()
    }


    private fun onClickListeners() {

        linRefine.setOnClickListener {
            if (arrListFilterData?.isNullOrEmpty() == false) {
                val i = Intent(mActivity, SortFilterActivity::class.java)

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

//        linRefine.setOnClickListener {
//            (activity as NavigationActivity).displaySort(strSortBy, parentType.toString())
//        }

        txtEmptyBtn.setOnClickListener {
            (activity as NavigationActivity).onBackPressed()
        }

        relMyBoutikey.setOnClickListener {
            lnrMainViewHolder.visibility = View.VISIBLE
            relTvList.visibility = View.GONE
            viewBoutikey.visibility = View.VISIBLE
            viewTv.visibility = View.GONE
            myTvLoaded = false
        }

        relTv.setOnClickListener {
            lnrMainViewHolder.visibility = View.GONE
            relTvList.visibility = View.VISIBLE
            viewBoutikey.visibility = View.GONE
            viewTv.visibility = View.VISIBLE
            myTvLoaded = true
        }
    }


    private fun initializeFields() {
        dialog = CustomProgressBar(activity)
        setEmptyPage()
        totalItem = 0
        Global.arrListFilter = ArrayList()

        nestedScrollView = view?.findViewById<NestedScrollView>(R.id.nestedScrollView)
        swipeRefreshLayout = view?.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        if (!bundle?.getString("parent_type").isNullOrEmpty()) {
            parentType = bundle?.getString("parent_type")
        }
        if (!bundle?.getString("header_text").isNullOrEmpty()) {
            //txtHead.text = intent.getStringExtra("header_text")
            header = bundle?.getString("header_text").toString()
        }

        if (!bundle?.getString("isFrom").isNullOrEmpty()) {
            isFromStr = bundle?.getString("isFrom")
        }

        if (bundle?.getSerializable("tvData") != null) {
            tvData = bundle?.getSerializable("tvData") as Tvs
        }

        if (!bundle?.getString("tv_id").isNullOrEmpty()) {
            tvId = bundle?.getString("tv_id").toString()
        }
        if (!bundle?.getString("has_subcategory").isNullOrEmpty()) {
            has_subcategory = bundle?.getString("has_subcategory").toString().toLowerCase()
        }

        /* if (!bundle!!.getParcelableArrayList("subcategories").isNullOrEmpty()) {
             arrListSubCategory = bundle?.getParcelableArrayList("subcategories")
             strFilterCategoryID = ""
         }*/

        if (!bundle?.getString("image").isNullOrEmpty()) {
            image = bundle?.getString("image").toString().toLowerCase()
            showImageBanner()
        }


        arrListSubCategory = bundle?.getParcelableArrayList("subcategories")

        //   Log.e("arrListSubCategory",""+arrListSubCategory)

        if (!bundle?.getString("categoryID").isNullOrEmpty()) {
            strCategoryID = bundle?.getString("categoryID").toString()
            originalCategory = strCategoryID
            isFromCategories = strCategoryID != ""
        } else {
            isFromCategories = false
        }

        if (isFromStr.equals("categories")) {
            strCategoryID = Global.getPreferenceCategory(activity as NavigationActivity)
            strFilterCategoryID = bundle?.getString("categoryID").toString()
        }

        if (isFromStr.equals("brand")) {
            strBrandID = Global.getPreferenceCategory(activity as NavigationActivity)
            strFilterBrandID = bundle?.getString("brandID").toString()
        }


        if (!bundle?.getString("isFeatured").isNullOrEmpty()) {
            strIsFeatured = bundle?.getString("isFeatured").toString()
        }

        if (!bundle?.getString("influencerID").isNullOrEmpty()) {
            strInfluencerID = bundle?.getString("influencerID").toString()

            Global.saveStringInSharedPref(
                mActivity,
                Constants.PREFS_INFLUENCER_ID,
                strInfluencerID
            )

        } else {
            Global.saveStringInSharedPref(
                mActivity,
                Constants.PREFS_INFLUENCER_ID,
                ""
            )
        }

        if (!bundle?.getString("banner").isNullOrEmpty()) {
            strBanner = bundle?.getString("banner").toString()
        }

        if (!bundle?.getString("isFromMostSellingBrands")
                .isNullOrEmpty() && bundle?.getString("isFromMostSellingBrands") == "yes"
        ) {
            isFromMostSelling = "1"
        }




        layoutManager = GridLayoutManager(activity, 2)
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
                activity,
                arrListPageWiseData,
                this,
                isFromStr,
                has_subcategory == "yes",
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
            StickHeaderItemDecoration(
                adapterProductGrid as com.app.blockaat.helper.StickHeaderItemDecoration.StickyHeaderInterface,
                object : OnHeaderDisplay {
                    override fun isHeaderDisplay(visible: Boolean) {
                        /* if (visible) {
                             if (relRefine.visibility == View.GONE) {
                                 relRefine.visibility = View.VISIBLE
                                 viewFilter.visibility = View.VISIBLE
                             }
                         } else {
                             if (relRefine.visibility == View.VISIBLE) {
                                 relRefine.visibility = View.GONE
                                 viewFilter.visibility = View.GONE
                             }
                         }*/
                    }

                })

        )

        rcyProductListing.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                1,
                false,
                Global.isEnglishLanguage(mActivity)
            )
        )
        rcyProductListing.addItemDecoration(GridDividerItemDecoration(3))
        rcyProductListing.isNestedScrollingEnabled = false

//        rcyProductListing.isNestedScrollingEnabled = false
        arrListTv = ArrayList()
        adapterTv = TvListAdapter(activity as NavigationActivity, arrListTv, onTvClickListener)
        val tvLayoutManager = GridLayoutManager(activity, 2)
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
        rvTvList?.layoutManager = tvLayoutManager
        rvTvList?.adapter = adapterTv

      /*  swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            //Load product list data
            if (isVisible) productListByCatID()
        }
*/

        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load product list data
                productListByCatID()
            }, 1000)
        }
        rcyProductListing.isNestedScrollingEnabled = false


        try {

            nestedScrollView!!.viewTreeObserver
                .addOnScrollChangedListener {
                    val view: View =
                        nestedScrollView!!.getChildAt(nestedScrollView!!.childCount - 1) as View
                    val diff: Int =
                        view.bottom - (nestedScrollView!!.height + nestedScrollView!!.scrollY)
                    if (!isPageLoading && !isLastPage) {
                        if (diff == 0) { // your pagination code
                            isPageLoading = true
                            currentPage += 1
                            loadMoreProducts()
                        }
                    }

                    swipeRefreshLayout!!.isEnabled =
                        nestedScrollView!!.scrollY == (nestedScrollView!!.getChildAt(0)
                            .measuredHeight - nestedScrollView!!.measuredHeight)
                }
        } catch (e: Exception) {
        }

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
        if (!bundle?.getString("isFromBrands")
                .isNullOrEmpty() && bundle?.getString("isFromBrands") == "yes"
        ) {
            isFromBrands = true
            strOriginalBrandID = strBrandID
        }

//     (activity as NavigationActivity).setSortListener(sortInterface)
        productListByCatID()



    }

    private fun showImageBanner() {

        if (image.isNotEmpty()) {
            loadImagesUsingGlide(
                mActivity,
                image,
                ivBanner
            )
        } else {
            ivBanner.visibility = View.GONE
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setEmptyPage() {
        ivEmpty.setImageResource(R.drawable.ic_empty_products)
        ivEmpty.setColorFilter(ContextCompat.getColor(mActivity, R.color.primary_button_color))
        txtEmptyTitle.text = getString(R.string.empty_products_heading)
        txtEmptyMessage.text = getString(R.string.empty_products_msg)
        txtEmptyBtn.text = getString(R.string.go_back)
        txtEmptyBtn.setBackgroundResource(R.drawable.blue_button_background)
        txtEmptyMessage.typeface = Global.fontSemiBold
        txtEmptyBtn.typeface = Global.fontBtn
        txtEmptyTitle.typeface = Global.fontNavBar
    }

    //to change wishlist item icon
    fun updateCollection() {
        for (i in 0 until arrListPageWiseData.size) {
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

    private fun setFonts() {
        txtItemCount.typeface = Global.fontMedium
        txtRefine.typeface = Global.fontRegular
        txtMyBoutikey.typeface = Global.fontMedium
        txtTv.typeface = Global.fontMedium
    }

    private fun productListByCatID() {
         println("HERE I AM LOADING MORE PRODUCT 3")

        if (NetworkUtil.getConnectivityStatus(activity as Activity) != 0) {

            if (!isFromRefresh) {
                showProgressDialog(activity as Activity)
            }


            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)
            jsonObject.addProperty("category_id", strFilterCategoryID)
            disposable = Global.apiService.getProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.SearchWs + Global.getLanguage(activity as Activity)
                        + "&category_id=" + strCategoryID + "&price_range=" + strRange + "&page=" +
                        PAGE_START + "&per_page=10" + "&sort_by=" + strSortBy + "&latest=" + strLatest
                        + "&is_featured=" + strIsFeatured + "&user_id=" + Global.getUserId(activity as Activity) + "&influencer_id=" +
                        strInfluencerID + "&store=" + Global.getStoreCode(activity as Activity) + "&tv_id=" + tvId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            //  println("RESPONSE - Product List Ws :   " + Gson().toJson(result.data))
                            if (result.data?.products != null && result.status == 200) {
                                rcyProductListing?.visibility = View.VISIBLE
                                viewFilter?.visibility = View.GONE
                                txtItemCount?.visibility = View.VISIBLE

                                arrListPageWiseData.clear()

                                isLastPage = false
                                currentPage = 1
                                totalPages =
                                    Global.stringToInt(result.data.total_pages?.toString())

                                //     totalPages = result.data.total_pages as Int
                                totalItem = Global.stringToInt(result.data.total_products)

                                if (!isPageloaded) {
                                    //  println("New filter data")
                                    arrListFilterData = ArrayList()
                                    for (i in 0 until result.data.filter?.size!!) {
                                        if (isFromStr.toString()
                                                .toLowerCase() != result.data.filter[i].filter_type.toString()
                                                .toLowerCase()
                                        )
                                            arrListFilterData?.add(result.data.filter[i])
                                    }
//                                    arrListFilterData?.addAll(result.data.filter as ArrayList<ProductListingFilterModel?>)
                                    //  println("filter list size" + arrListFilterData?.size)
                                    setFilterData()


                                    isPageloaded = true
                                }
                                /* if(adapterProductGrid!=null)
                                 {
                                     adapterProductGrid?.removeLoadingFooter()
                                 }*/

                                if (totalPages > 0) {
                                    if (myTvLoaded) {
                                        lnrMainViewHolder?.visibility =
                                            View.GONE // if data show list
                                        relTvList?.visibility = View.VISIBLE // if data show list
                                    } else {
                                        lnrMainViewHolder?.visibility =
                                            View.VISIBLE // if data show list
                                        relTvList?.visibility = View.GONE // if data show list
                                    }
                                    relEmpty?.visibility = View.GONE
                                    // lnrRefine.visibility = View.VISIBLE
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            arrListSubCategory = arrListSubCategory,
                                            totalItem = totalItem
                                        )
                                    )
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            category_tvs = null,
                                            totalItem = totalItem
                                        )
                                    )
                                    println("here i am $isFromStr")

                                    if (isFromStr.equals("celebrityHome") || isFromStr.equals("celebrity")) {
                                        linCelebrity?.visibility = View.VISIBLE
                                        showImageBanner()

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
                                                    totalItem = totalItem
                                                )
                                            )
                                        } else {
                                            arrListPageWiseData.add(
                                                ProductListingProductModel(
                                                    totalItem = totalItem
                                                )
                                            )

                                        }

                                    } else {
                                        if (!result.data.influencer_details?.banners.isNullOrEmpty()){
                                            image =
                                                result.data.influencer_details?.banners?.get(0)?.image
                                                    ?: ""
                                            println("here i am - Image $image")
                                            showImageBanner()
                                        }


                                        linCelebrity?.visibility = View.GONE
                                        if (result.data.influencer_details?.banners.isNullOrEmpty() && arrListSubCategory.isNullOrEmpty() && !strBanner.isNullOrEmpty()) {
                                            val topBanner = TopBanner(
                                                banner = strBanner,
                                                type = isFromStr
                                            )
                                            arrListPageWiseData.add(
                                                ProductListingProductModel(
                                                    topBanner = topBanner,
                                                    totalItem = totalItem
                                                )
                                            )
                                        } else {

                                            arrListTv?.clear()
                                            tvData?.let { arrListTv?.add(it) }
                                            arrListPageWiseData.add(

                                                ProductListingProductModel(

                                                    influencer_details = InfluencerDetails(tvs = arrListTv as ArrayList<Tvs?>),
                                                    totalItem = totalItem
                                                )
                                            )
                                        }
                                    }

                                    //to add filter view
                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            totalItem = totalItem
                                        )
                                    )
                                    result.data.products.let { arrListPageWiseData.addAll(it) }
                                    txtItemCount?.text =
                                        "$totalItem " + resources
                                            .getString(R.string.items)
                                    originalMaxPrice = result.data.max_product_price

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

                                rcyProductListing?.visibility = View.GONE  //hide list if no data
                                viewFilter?.visibility = View.GONE
                                txtItemCount?.visibility = View.GONE

                                if (!isFromFilter) {
                                    relRefine?.visibility = View.GONE
                                }
                                relEmpty?.visibility = View.VISIBLE

                            } else {
                                relEmpty.visibility = View.VISIBLE
                                if (!isFromFilter) {
                                    relRefine?.visibility = View.GONE
                                }
                                lnrMainViewHolder.visibility = View.GONE
                                Global.showSnackbar(
                                    activity as Activity,
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
                                activity as Activity,
                                getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        //  println("ERROR - Product List Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        lnrMainViewHolder?.visibility = View.GONE
                        relEmpty?.visibility = View.VISIBLE
                        if (!isFromFilter) {
                            relRefine?.visibility = View.GONE
                        }
                        Global.showSnackbar(activity as Activity, getString(R.string.error))
                    }
                )
        }
    }

    fun getHeader(): String {
        return header
    }

    private fun loadMoreProducts() {
        //  println("HERE I AM LOADING MORE PRODUCT4")


        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)
            jsonObject.addProperty("category_id", strFilterCategoryID)
            hideProgressDialog()

            disposable = Global.apiService.getProducts(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.SearchWs + Global.getLanguage(mActivity)
                        + "&category_id=" + strCategoryID + "&most_selling=" + isFromMostSelling + "&price_range=" + strRange + "&page="
                        + currentPage + "&per_page=10" + "&sort_by=" + strSortBy
                        + "&is_featured=" + strIsFeatured + "&user_id=" + Global.getUserId(mActivity) + "&influencer_id=" + strInfluencerID +
                        "&store=" + Global.getStoreCode(mActivity) + "&tv_id=" + tvId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    hideProgressDialog()
                    if (result != null) {
                        totalPages = Global.stringToInt(result.data?.total_pages?.toString())

                        // totalPages = result.data?.total_pages as Int
                        adapterProductGrid!!.removeLoadingFooter()

                        isPageLoading = false
                        result.data?.products?.let { arrListPageWiseData.addAll(it) }
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

    private val sortInterface = object : OnSortClickListener {
        override fun onSortClicked(strSort: String) {

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun sortData(sortObject: SortObject) {
        // println("parent type sort: " + sortObject.parentType + " parent type main: " + parentType)
        if (sortObject.parentType == parentType) {
            strSortBy = sortObject.sortBy.toString()
            productListByCatID()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun filterData(filterObject: FilterObject) {
        val type = filterObject.type
        //  println("parent type sort: " + filterObject.parentType + " parent type main: " + parentType)

        if (filterObject.parentType == parentType) {
            arrListFilterData?.clear()
            arrListFilterData?.addAll(filterObject.arrListFilterData as Collection<ProductListingFilterModel?>)
            adapterFilter = filterObject.adapterFilter
            if (type == "clear") {
                /*for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }*/
            } else {
                if (!isFromStr.equals("brand")) {
                    strBrandID = ""
                }
                strAttributeID = ""
                val arrListAttribute = ArrayList<ProductListingFilterValueModel>()
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    val type = (fragment as FilterListFragment).getFilteredType()
                    val arrListValues = fragment.getFilteredValues()
                    if ((type.equals(
                            "Brands",
                            true
                        ) || type.equals("Brand")) && !isFromStr.equals("brand")
                    ) {
                        if (!arrListValues.isNullOrEmpty()) {
                            strBrandID =
                                Global.getCommaSeparatedString(arrListValues)
                        }
                    } else

                        if ((type.equals(
                                "Categories",
                                true
                            ) || type.equals("Category")) && !isFromStr.equals("categories")
                        ) {
                            if (!arrListValues.isNullOrEmpty()) {
                                strFilterCategoryID =
                                    Global.getCommaSeparatedString(arrListValues)
                            }
                        } else {
                            if (!arrListValues.isNullOrEmpty()) {
                                arrListAttribute.addAll(arrListValues)
                            }
                        }

                }
                if (strBrandID.isNullOrEmpty() && isFromBrands == true) {
                    strBrandID = strOriginalBrandID
                }
                strAttributeID = Global.getCommaSeparatedString(arrListAttribute)
                isFromFilter = true
                if (adapterProductGrid != null) {
                    adapterProductGrid?.removeLoadingFooter()
                }
                productListByCatID()

            }
        }

    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun setFilterData() {
        parentType?.let { (activity as NavigationActivity).initializeFilter(arrListFilterData, it) }

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
                initializeFields()

                // productListByCatID()
            } else {
                isFromFilter = false
                strAttributeID = ""
                strFilterCategoryID = ""
                strFilterBrandID = ""
                initializeFields()
                // productListByCatID()
            }
        }

    }


    private val onTvClickListener = object : OnTvClickListener {
        override fun onTvClickListener(type: String, position: Int, itemPosition: Int) {
            if (!arrListTv.isNullOrEmpty()) {
                //   println("On click id from: " + isFromStr)
//                if (isFromStr.equals("celebrityHome")) {
//                    EventBus.getDefault().post(HomeTvList(arrListTv?.get(position)))
//                } else {
                val tvModel = CelebrityTvList(
                    arrListTv?.get(position)
                )
                val intent = Intent(mActivity, ProductListActivity::class.java)
                intent.putExtra(
                    "header_text",
                    tvModel.tvs?.name
                )
                intent.putExtra(
                    "categoryID",
                    Global.getPreferenceCategory(mActivity)
                )
                intent.putExtra("parent_type", "celebrity")
                intent.putExtra("isFrom", "celebrityTab")
                intent.putExtra("has_subcategory", "yes")
                intent.putExtra("tv_id", tvModel.tvs?.id)
                intent.putExtra("tvData", tvModel.tvs)
                startActivity(intent)
            }

//            }
        }

    }

    override fun onResume() {
        super.onResume()
        updateCollection()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        EventBus.getDefault().unregister(this)
    }


    private fun showProgressDialog(activity: Activity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (!isFromRefresh) {
            isFromRefresh = false
            dialog?.hideDialog()
        } else {
            swipeRefreshLayout?.isRefreshing = false
        }

    }

    override fun onHeaderClicked(type: String) {
        if (type == "sort") {
            linRefine.performClick()
        } else {
            linRefine.performClick()
        }
    }

    override fun onSubcategorySelected(subCategory: Subcategory) {

    }

    override fun onProductClicked(product: ProductListingProductModel) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("strProductID", product.id.toString())
        intent.putExtra("strProductName", product.name.toString())
        intent.putExtra("categoryID", strCategoryID.toString())
        startActivity(intent)
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

        if (isFromStr == "celebrityHome") {

            EventBus.getDefault()
                .post(ProductListBannerHome(id = link_id, header = header, type = link_type))
        }
        if (isFromStr == "celebrity") {
            EventBus.getDefault()
                .post(
                    ProductListBannerCelebrity(
                        id = link_id,
                        header = header,
                        type = link_type
                    )
                )
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            initializeFields()
        }
    }

    override fun onTvClickListener(type: String, position: Int, itemPosition: Int) {

    }

}
