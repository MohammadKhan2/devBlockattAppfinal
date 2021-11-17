package com.app.blockaat.category.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.app.blockaat.Filter.activity.SortFilterActivity
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.DBHelper
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.Global.getDimenVallue
import com.app.blockaat.helper.Global.loadImagesUsingGlide
import com.app.blockaat.helper.NetworkUtil
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.adapter.TvListAdapter
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.interfaces.*
import com.app.blockaat.productlisting.model.*
import com.app.blockaat.tv.activity.AllTvListActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_sub_category_data.*
import kotlinx.android.synthetic.main.fragment_sub_category_data.lnrMainViewHolder
import kotlinx.android.synthetic.main.fragment_sub_category_data.rcyProductListing
import kotlinx.android.synthetic.main.fragment_sub_category_data.swipeRefreshLayout
import kotlinx.android.synthetic.main.layout_empty_page.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SubCategoryDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SubCategoryDataFragment : Fragment(), OnHeaderClicked, OnSubcategorySelectListener,
    OnProductListListener, HomeItemClickInterface, OnTvClickListener {
    private lateinit var mActivity: NavigationActivity

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

    private var strCategoryID = ""
    private var strFilterCategoryID = ""
    private var strFilterBrandID = ""

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

    private var strIsFeatured = "0"
    private var dialog: CustomProgressBar? = null
    private val TYPE_SUBCATEGORY = 0
    private val TYPE_TV = 1
    private val TYPE_BANNER = 2
    private val TYPE_HEADER = 3
    private val TYPE_ITEM = 4
    private val TYPE_LOADING = 5
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
        mActivity = activity as NavigationActivity
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        productsDBHelper = DBHelper(mActivity)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bundle = arguments

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_category_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        setFonts()
        onClickListeners()
        setEmptyPage()

    }

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

    private fun initializeFields() {
        dialog = CustomProgressBar(mActivity)

        Global.arrListFilter = ArrayList()
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
        if (!bundle?.getString("has_subcategory").isNullOrEmpty()) {
            has_subcategory = bundle?.getString("has_subcategory").toString().toLowerCase()

        }

        if (bundle?.getSerializable("tvData") != null) {
            tvData = bundle?.getSerializable("tvData") as Tvs
        }

        if (!bundle?.getString("tv_id").isNullOrEmpty()) {
            tvId = bundle?.getString("tv_id").toString()
        }



        if (!bundle?.getString("categoryID").isNullOrEmpty()) {
            strCategoryID = bundle?.getString("categoryID").toString()
            originalCategory = strCategoryID
            isFromCategories = strCategoryID != ""
        } else {
            isFromCategories = false
        }

        if (!bundle?.getString("brandID").isNullOrEmpty()) {
            strBrandID = bundle?.getString("brandID").toString()
        }

        if (!bundle?.getString("isFeatured").isNullOrEmpty()) {
            strIsFeatured = bundle?.getString("isFeatured").toString()
        }

        if (!bundle?.getString("influencerID").isNullOrEmpty()) {
            strInfluencerID = bundle?.getString("influencerID").toString()
        }

        if (!bundle?.getString("banner").isNullOrEmpty()) {
            strBanner = bundle?.getString("banner").toString()
        }

        if (!bundle?.getString("isFromMostSellingBrands")
                .isNullOrEmpty() && bundle?.getString("isFromMostSellingBrands") == "yes"
        ) {
            isFromMostSelling = "1"
        }





        layoutManager = GridLayoutManager(mActivity, 2)
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
                mActivity,
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
            com.app.blockaat.helper.StickHeaderItemDecoration(
                adapterProductGrid as com.app.blockaat.helper.StickHeaderItemDecoration.StickyHeaderInterface,
                object : OnHeaderDisplay {
                    override fun isHeaderDisplay(visible: Boolean) {
                        /* if (visible) {
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
            swipeRefreshLayout?.isRefreshing = true
            swipeRefreshLayout?.postDelayed({
                swipeRefreshLayout?.isRefreshing = false
                //Load product list data
                if (isVisible) productListByCatID()
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
        txtRefine.typeface = Global.fontRegular
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


        txtEmptyBtn.setOnClickListener {
            (mActivity).onBackPressed()
        }
    }

    fun getHeader(): String {
        return header
    }

    private fun productListByCatID() {

        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {

            if (!isFromRefresh) {
                showProgressDialog(mActivity)
            }

            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)
            disposable = Global.apiService.getSubCategories(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.CategoryWs + Global.getSelectedLanguage(
                    mActivity
                ) + "&category_id=" + strCategoryID + "&sort_by=" + strSortBy + "&page=" +
                        PAGE_START + "&per_page=10"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            println("RESPONSE - Product List Ws :   " + Gson().toJson(result.data))
                            if (result.data?.category_products != null && result.status == 200) {
                                arrListPageWiseData.clear()
                                totalItems =
                                    Global.stringToInt(result.data.category_products.total_products)

                                isLastPage = false
                                currentPage = 1
                                totalPages = result.data.category_products.total_pages as Int

                                if (!isPageloaded) {
                                    println("New filter data")
                                    arrListFilterData = ArrayList()
                                    for (i in 0 until result.data.category_products.filter?.size!!) {
                                        if (isFromStr.toString()
                                                .toLowerCase() != result.data.category_products.filter[i].filter_type.toString()
                                                .toLowerCase()
                                        )
                                            arrListFilterData?.add(result.data.category_products.filter[i])
                                    }
//                                    arrListFilterData?.addAll(result.data.filter as ArrayList<ProductListingFilterModel?>)
                                    println("filter list size" + arrListFilterData?.size)
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
                                        arrListPageWiseData.add(
                                            ProductListingProductModel(
                                                totalItem = totalItems
                                            )
                                        )
                                    }

                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            totalItem = totalItems
                                        )
                                    )

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

                                    arrListPageWiseData.add(
                                        ProductListingProductModel(
                                            totalItem = totalItems
                                        )
                                    )

                                    arrListPageWiseData.addAll(result.data.category_products.products as Collection<ProductListingProductModel>)
                                    txtItemCount.text = totalItems.toString()
                                    originalMaxPrice = result.data?.category_products.max_product_price

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
                                    mActivity,
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
                                mActivity,
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
                        Global.showSnackbar(mActivity, getString(R.string.error))
                    }
                )
        }
    }

    private fun loadMoreProducts() {
        if (NetworkUtil.getConnectivityStatus(mActivity) != 0) {
            val jsonObject = JsonObject()
            jsonObject.addProperty("attribute_id", strAttributeID)
            jsonObject.addProperty("brand_id", strFilterBrandID)

            disposable = Global.apiService.getSubCategories(
                jsonObject,
                com.app.blockaat.apimanager.WebServices.CategoryWs + Global.getSelectedLanguage(
                    mActivity
                ) + "&category_id=" + strCategoryID + "&sort_by=" + strSortBy + "&page=" +
                        currentPage + "&per_page=10"
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    hideProgressDialog()
                    if (result != null) {
                        totalPages = result.data?.category_products?.total_pages as Int
                        adapterProductGrid!!.removeLoadingFooter()

                        isPageLoading = false
                        arrListPageWiseData.addAll(result.data.category_products.products as Collection<ProductListingProductModel>)

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
        parentType?.let { (mActivity).initializeFilter(arrListFilterData, it) }

    }

    private fun showProgressDialog(activity: Activity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        isFromRefresh = false
        dialog?.hideDialog()

    }


    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null) {
            disposable?.dispose()
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SubCategoryDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onHeaderClicked(type: String) {
        if (type == "sort") {
//            linSort.performClick()
        } else {
            linRefine.performClick()
        }
    }

    override fun onSubcategorySelected(subCategory: Subcategory) {

    }

    override fun onResume() {
        super.onResume()
        updateCollection()
     //  EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
     //   EventBus.getDefault().unregister(this)
    }

    override fun onProductClicked(product: ProductListingProductModel) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra("strProductID", product.id.toString())
        intent.putExtra("strProductName", product.name.toString())
        startActivity(intent)
    }

    override fun onProductClicked(product: ProductListingProductModel, type: String) {
        updateCollection()
    }

    private fun updateCollection() {
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
            val intent = Intent(mActivity, ProductListActivity::class.java)
            intent.putExtra(
                "header_text",
                tvModel?.tvs?.name
            )
            intent.putExtra(
                "categoryID",
                Global.getPreferenceCategory(mActivity)
            )
            intent.putExtra("parent_type", "home")
            intent.putExtra("isFrom", "home")
            intent.putExtra("has_subcategory", "yes")
            intent.putExtra("tv_id", tvModel?.tvs?.id)
            intent.putExtra("tvData", tvModel?.tvs)
            startActivity(intent)
        } else if (type == "viewAll") {
            val i = Intent(mActivity, AllTvListActivity::class.java)
            i.putExtra("categoryId", strCategoryID)
            startActivity(i)
        }
    }
}