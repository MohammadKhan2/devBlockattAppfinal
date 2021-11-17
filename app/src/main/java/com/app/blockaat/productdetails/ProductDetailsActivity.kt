package com.app.blockaat.productdetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.app.blockaat.BuildConfig
import com.app.blockaat.R
import com.app.blockaat.cart.CartActivity
import com.app.blockaat.cart.model.AddCartRequestApi
import com.app.blockaat.cart.model.AddCartRequestModel
import com.app.blockaat.helper.*
import com.app.blockaat.helper.Global.addToCartOffline
import com.app.blockaat.helper.Global.addToCartOnline
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productdetails.activity.AddReviewActivity
import com.app.blockaat.productdetails.activity.AllReviewsActivity
import com.app.blockaat.productdetails.adapter.ConfigAdapter
import com.app.blockaat.productdetails.adapter.ProductBannerAdapter
import com.app.blockaat.productdetails.adapter.RelatedProductsAdapter
import com.app.blockaat.productdetails.interfaces.OnProductDetailsBannerClick
import com.app.blockaat.productdetails.interfaces.RelatedItemInterface
import com.app.blockaat.productdetails.model.*
import com.app.blockaat.productlisting.model.ProductListingProductModel
import com.app.blockaat.wishlist.WishlistActivity
import com.app.blockaat.wishlist.modelclass.WishListResponseModel
import com.github.demono.AutoScrollViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.util.LinkProperties
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.item_home_product.view.*
import kotlinx.android.synthetic.main.item_product_list.view.*
import kotlinx.android.synthetic.main.layout_product_detail.*
import kotlinx.android.synthetic.main.layout_product_detail.relAbout
import kotlinx.android.synthetic.main.layout_product_detail.relShipping
import kotlinx.android.synthetic.main.toolbar_default.*

class ProductDetailsActivity : BaseActivity() {

    private lateinit var adapterBannerImage: ProductBannerAdapter
    private val arrListBanners: ArrayList<String> = arrayListOf()
    private val ADD_REVIEW_REQUEST: Int = 400
    private val ADD_REVIEW_RESULT: Int = 401
    private var disposable: Disposable? = null
    private var strProductID: String = ""
    private var productDetailData: ProductDetailDataModel? = null
    private var isFromWishlist = false
    private lateinit var progressDialog: Dialog
    private var strEntityID = "" // this id we will pass in add to cart
    private var strInfluencerID = "" // this id store in shared prefernce - ProductListFragment
    private var branchIoLink: String = ""

    private lateinit var productsDBHelper: DBHelper
    val REQUEST_GALLERY_ACCESS = 1
    var strImage: String? = ""
    var strBrandName: String? = ""
    var strProductName: String? = ""
    var strDescription: String? = ""
    var strSpecification: String? = ""
    var strProductPrice: String? = ""
    var strProductRegularPrice: String? = ""
    var strSizeguide: String = ""
    var arrListSizeGuide: ArrayList<String> = ArrayList()
    var arrListRelatedItems: ArrayList<ProductDetailRelatedModel> = arrayListOf()
    var adapterRelatedProducts: RelatedProductsAdapter? = null
    var strProductImage = ""
    var imageOffline = ""
    private var dialog: CustomProgressBar? = null
    private var strBrandsID = ""
    private var productListDataModel: ProductListingProductModel? = null
    private var configAdapter1: ConfigAdapter? = null
    private var configAdapter2: ConfigAdapter? = null
    private var attributeId1 = ""
    private var attributeId2 = ""
    private var optionId1 = ""
    private var optionValue1 = ""
    private var optionValue2 = ""
    private var optionId2 = ""
    private var attributeValue1: String = ""
    private var attributeValue2: String = ""
    private var strCategoryId: String? = ""

    private var arrListConfiguration1: ArrayList<ProductDetailAttributeModel> = arrayListOf()
    private var arrListConfiguration2: ArrayList<ProductDetailAttributeModel> = arrayListOf()
    private var configurable_option: ArrayList<ProductDetailConfigurableOptionModel>? = null
    private var productType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_product_detail)

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.visibility = VISIBLE

        ivClose.visibility = GONE
        view.visibility = GONE
        ivShare.visibility = GONE
        relCartImage.visibility = VISIBLE

    }

    private fun setFonts() {

        txtHead.typeface = Global.fontNavBar
        txtBrandName.typeface = Global.fontNavBar
        txtMoreProductsNote.typeface = Global.fontMedium
        txtAttr1.typeface = Global.fontRegular
        txtAddCart.typeface = Global.fontBtn
        txtName.typeface = Global.fontRegular
        txtProductsPrice.typeface = Global.fontNavBar
        txtRegularPrice.typeface = Global.fontRegular
        txtDiscount.typeface = Global.fontRegular

        txtReview.typeface = Global.fontRegular
        txtSoldOut.typeface = Global.fontRegular
        txtNotifyMe.typeface = Global.fontBold
        txtDescTitle.typeface = Global.fontMedium
        txtComposerTitle.typeface = Global.fontMedium
        txtShippingTitle.typeface = Global.fontMedium
        txtAboutTitle.typeface = Global.fontMedium

        txtDescription.typeface = Global.fontRegular
        txtComposer.typeface = Global.fontRegular
        txtShipping.typeface = Global.fontRegular
        txtAbout.typeface = Global.fontRegular
        txtSku.typeface = Global.fontMedium
        //   txtSoldOut.setTextColor(resources.getColor(R.color.red_pink_color))

    }

    private fun initializeFields() {
        // get INFLUENCER_ID on sharedPrference
        strInfluencerID = Global.getStringFromSharedPref(
            this@ProductDetailsActivity,
            Constants.PREFS_INFLUENCER_ID
        )

        dialog = CustomProgressBar(this)
        /* if (!Global.isEnglishLanguage(this)) {
             imgGuide.rotation = 180f
             ivDiscount.rotation = 180f
         }*/

        //  txtName.setPaintFlags(txtName.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        productsDBHelper = DBHelper(this)
        if (intent.hasExtra("strProductID")) {
            strProductID = intent.getStringExtra("strProductID")!!
        }

        if (intent.hasExtra("isFromWishlist") && intent.getStringExtra("isFromWishlist") == "yes") {
            isFromWishlist = true
        }

        if (intent.hasExtra("categoryID")) {
            strCategoryId = intent.getStringExtra("categoryID")
        }


        if (intent.hasExtra("strProductName")) {
            strProductName = intent.getStringExtra("strProductName")
            txtHead.text = strProductName
        }


        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rcyRelatedProducts.layoutManager = layoutManager
        rcyRelatedProducts.isNestedScrollingEnabled = false
        rcyRelatedProducts.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                2,
                false,
                Global.isEnglishLanguage(this)
            )
        )
        rcyRelatedProducts.addItemDecoration(GridDividerItemDecoration(2))

        adapterBannerImage = ProductBannerAdapter(this, arrListBanners, bannerInterface)
        if (intent.hasExtra("itemInWishList") && intent.getSerializableExtra("itemInWishList") != null) {
            productListDataModel =
                intent.getSerializableExtra("itemInWishList") as ProductListingProductModel?
            //  println("Here i am product info is  " + productListDataModel?.item_in_wishlist)
        }
/*        rvAttribute1.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        configAdapter1 = ConfigAdapter(arrAttr1, this, config1, 1)
        rvAttribute1.adapter = configAdapter1

        arrAttr2 = ArrayList()
        rvAttribute2.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        configAdapter2 = ConfigAdapter(arrAttr2, this, config2)
        rvAttribute2.adapter = configAdapter2*/

        adapterRelatedProducts = RelatedProductsAdapter(
            arrListRelatedItems,
            this,
            productsDBHelper,
            relatedItemInterface
        )
        rcyRelatedProducts.adapter = adapterRelatedProducts
        getProductDetail()

        // First time default expand Description & set Rotation for arrow filed
        elDesc.expand()
        imgDesc.rotation = 180f


    }

    //creating deep link for sharing product with branch io
    private fun setBranchIo() {
        //Branch io
        val desktopUrl = "https://blockaat.com/product-detail/$strProductID/$strProductName"
        val branchUniversalObject =
            BranchUniversalObject().setCanonicalIdentifier(strProductID.toString() ?: "")
                .setTitle(strProductName ?: "")
                .setContentDescription(strDescription)
                .setContentImageUrl(strProductImage)
                .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
                .addContentMetadata("target", "P")
                .addContentMetadata("target_id", strProductID.toString() ?: "")
                .addContentMetadata("title", strProductName ?: "")


        val linkProperties = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .addControlParameter(
                "\$desktop_url",
                desktopUrl
            )

        branchUniversalObject.generateShortUrl(
            this,
            linkProperties,
            Branch.BranchLinkCreateListener { url, error ->
                if (error == null) {
                    branchIoLink = url
                }
            })
        //  println("desktopUrl - "+branchIoLink)

    }


    private fun getProductDetail() {

        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            disposable = Global.apiService.getProductDetail(
                com.app.blockaat.apimanager.WebServices.ProductDetailWs + strProductID + "&lang=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_STORE_CODE
                )
                        + "&user_id=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_USER_ID
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        //  println("RESPONSE - ProductDetail Ws :   " + result.data)
                        if (result != null) {
                            if (result.data != null && result.status == 200) {
                                productDetailData = result.data
                                nestedScrollView.visibility = VISIBLE
                                if (productDetailData?.is_saleable == 0) {
                                    txtAddCart.visibility = GONE
                                    txtSoldOut.visibility = VISIBLE
                                    txtNotifyMe.visibility = VISIBLE
                                } else {
                                    txtAddCart.visibility = VISIBLE
                                    txtSoldOut.visibility = GONE
                                    txtNotifyMe.visibility = GONE
                                }
                                setData()
                            } else {
                                nestedScrollView.visibility = View.GONE
                                txtAddCart.visibility = View.GONE
                            }
                        } else {
                            nestedScrollView.visibility = View.GONE
                            txtAddCart.visibility = View.GONE
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }

                    },
                    { error ->
                        nestedScrollView.visibility = View.GONE
                        txtAddCart.visibility = View.GONE
                        //  println("ERROR - ProductDetail Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        //set top banner if data
        if (!productDetailData?.images.isNullOrEmpty()) {
            val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Global.getDimenVallue(this, 320.0).toInt()
            )
            autoPager?.layoutParams = params
            productDetailData?.images?.let { setProductBannerImages(it) }
        }
        strProductImage = productDetailData?.image!!
        strProductName = productDetailData?.name.toString()
        strDescription = productDetailData?.description.toString()
        strSpecification = productDetailData?.specification.toString()
        strSizeguide = productDetailData?.size_guide.toString()
        productType = productDetailData?.product_type ?: ""
        txtName.text = productDetailData?.name.toString()

        if (!strSizeguide.isNullOrEmpty()) {
            linAttributes.visibility = View.VISIBLE
        } else {
            linAttributes.visibility = View.VISIBLE
        }

        if (productDetailData?.item_in_wishlist == 1) {
            ivWishList.setImageResource(R.drawable.ic_wishlist_selected)

        } else {
            ivWishList.setImageResource(R.drawable.ic_wishlist_unselected)

        }

        imageOffline = productDetailData?.image.toString()


        txtHead.text = strProductName



        if (!productDetailData?.brand_name.isNullOrEmpty()) {
            strBrandName = productDetailData?.brand_name.toString()
            strBrandsID = productDetailData?.brand_id.toString()
            txtBrandName.text = Global.toCamelCase(strBrandName?.trim().toString())
        }



        simpleRatingBar.rating = productDetailData?.reviews?.size?.toFloat() as Float
        simpleRatingBar.visibility = View.GONE
        txtReview.visibility = View.GONE
        txtSku.text = getString(R.string.SKU, productDetailData?.SKU)
        if (productDetailData?.reviews.isNullOrEmpty()) {
            txtReview.text =
                resources.getString(R.string.first_review)
        } else {
            txtReview.text =
                resources.getString(R.string.reviews, productDetailData?.reviews?.size.toString())
        }

        if (!productDetailData?.description.isNullOrEmpty()) {
            //txtDescription.text = Global.getWebViewData(this, productDetailData?.description.toString())
            txtDescription.text = productDetailData?.description.toString()
            linDesc.visibility = View.VISIBLE
        } else {
            linDesc.visibility = View.GONE
        }

        if (!productDetailData?.about_designer.isNullOrEmpty()) {
            //txtAbout.text = Global.getWebViewData(this, productDetailData?.about_designer.toString())
            txtAbout.text = productDetailData?.about_designer.toString()
            linAbout.visibility = View.VISIBLE
        } else {
            linAbout.visibility = View.GONE
        }
        if (!productDetailData?.shipping_free_returns.isNullOrEmpty()) {
            //txtShipping.text = Global.getWebViewData(this, productDetailData?.shipping_free_returns.toString())
            txtShipping.text = productDetailData?.shipping_free_returns.toString()
            linShipping.visibility = View.VISIBLE
        } else {
            linShipping.visibility = View.GONE
        }
        if (!productDetailData?.composition_and_care.isNullOrEmpty()) {
            //txtComposer.text = Global.getWebViewData(this, productDetailData?.composition_and_care.toString())
            txtComposer.text = productDetailData?.composition_and_care.toString()
            linComposer.visibility = View.VISIBLE
        } else {
            linComposer.visibility = View.GONE
        }


        txtRegularPrice.text =
            Global.setPriceWithCurrency(
                this,
                productDetailData?.regular_price.toString()
            )
        txtRegularPrice.paintFlags = txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        txtProductsPrice.text =
            Global.setPriceWithCurrency(
                this,
                productDetailData?.final_price.toString()
            )
        strProductPrice = productDetailData?.final_price.toString()
        strProductRegularPrice = productDetailData?.regular_price.toString()

        val disData = Global.getDiscountedPrice(
            productDetailData?.final_price.toString(),
            productDetailData?.regular_price.toString()
        )
        relDiscount.visibility = VISIBLE

        if (disData != 0) {
            txtDiscount.text = disData.toString() + "%"
        } else {
            relDiscount.visibility = GONE
            txtRegularPrice.visibility = GONE
        }

        /*  txtDiscount.text = Global.getDiscountedPrice(
              productDetailData?.final_price.toString(),
              productDetailData?.regular_price.toString()
           ).toString() + "%"*/

        setConfig(productDetailData?.configurable_option)

        if (productDetailData?.recommended_products != null && productDetailData?.recommended_products!!.size > 0) {
            linRelatedProducts.visibility = View.VISIBLE
            arrListRelatedItems.clear()
            productDetailData?.recommended_products?.let { arrListRelatedItems.addAll(it) }
            adapterRelatedProducts?.notifyDataSetChanged()

        } else {
            linRelatedProducts.visibility = View.GONE
        }

        setBranchIo()

    }

    private fun setProductBannerImages(images: List<String>) {
        arrListBanners.clear()
        var indicatorPosition = 0
        images.let { arrListBanners.addAll(it) }
        if (!Global.isEnglishLanguage(this@ProductDetailsActivity)) {
            arrListBanners.reverse()
            indicatorPosition = arrListBanners.size - 1
        }
        autoPager.adapter = adapterBannerImage
        autoPager.offscreenPageLimit = arrListBanners.size

        //set banner indicator
        //if more than one image show indicator

        if (arrListBanners.size > 1) {
            relPager1.visibility = VISIBLE
            /*  linViewPagerIndicator1?.noOfPages = arrListBanners?.size as Int
              linViewPagerIndicator1?.visibleDotCounts = 7
              linViewPagerIndicator1?.onPageChange(0)*/
/*
            autoPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {

                    if (position > viewPagerIndicator?.noOfPages ?: 1 - 1) {
                        viewPagerIndicator?.onPageChange(viewPagerIndicator?.noOfPages ?: 1 - 1)
                    } else {
                        viewPagerIndicator?.onPageChange(position)
                    }
                }
            })
*/
            if (!Global.isEnglishLanguage(this@ProductDetailsActivity)) {
                arrListBanners.reverse()
                indicatorPosition = arrListBanners.size?.minus(1) ?: 0
            }

            autoPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    for (m in 0 until linViewPagerIndicator1.childCount) {
                        linViewPagerIndicator1?.getChildAt(m)
                            ?.findViewById<ImageView>(R.id.imgBtnIndicator)?.isSelected =
                            position == m
                    }
                }
            })

            //set banner indicator
            linViewPagerIndicator1?.removeAllViews()
            if (arrListBanners.isNotEmpty() && arrListBanners.size ?: 0 > 1) {
                //if more than one image show indicator
                for (m in 0 until (arrListBanners.size ?: 0)) {
                    val v = LayoutInflater.from(this@ProductDetailsActivity)
                        .inflate(R.layout.viewpager_indicator_home, null)
                    val imgBtnIndicator = v.findViewById(R.id.imgBtnIndicator) as ImageButton

                    if (m == 0) {
                        //if more than one image then showing first one as selected
                        imgBtnIndicator.isSelected = true
                    }
                    linViewPagerIndicator1?.addView(v)
                }
            }

            if (!Global.isEnglishLanguage(this@ProductDetailsActivity)) {
                arrListBanners.reverse()
                adapterBannerImage?.notifyDataSetChanged()
                autoPager?.currentItem = adapterBannerImage.count - 1
                autoPager?.direction = AutoScrollViewPager.DIRECTION_LEFT
                //              linViewPagerIndicator1.onPageChange(arrListTopBanner?.size - 1)
                linViewPagerIndicator1?.rotation = 180f

            }

            autoPager.currentItem = indicatorPosition
        } else {
            relPager1.visibility = GONE

        }
    }

    private val relatedItemInterface = object : RelatedItemInterface {
        override fun onItemClick(position: Int, type: String) {
            val a = arrListRelatedItems[position]
            when (type) {
                "addToCart" -> {
                    addToCartRelated(position)
                }
                "addOrRemoveWishlist" -> {
                    if (Global.isUserLoggedIn(this@ProductDetailsActivity)) {
                        if (a?.item_in_wishlist == 1) {
                            addOrRemoveWishlist(a?.id ?: "", "related", false)
                        } else {
                            addOrRemoveWishlist(a?.id ?: "", "related", true)
                        }
                    } else {
                        val i = Intent(this@ProductDetailsActivity, LoginActivity::class.java)
                        i.putExtra("isFromProducts", "yes")
                        startActivityForResult(i, 1)
                    }
                }
                "openProduct" -> {
                    val intent =
                        Intent(this@ProductDetailsActivity, ProductDetailsActivity::class.java)
                    intent.putExtra("strProductID", a?.id.toString())
                    intent.putExtra("strProductName", a?.name.toString())
                    startActivity(intent)
                }
                /*  "notifyMe" -> {
                      Global.notifyMe(this@ProductDetailsActivity, a?.id ?: "")


                  }*/
            }
        }

    }

    private val bannerInterface = object : OnProductDetailsBannerClick {
        override fun onBannerClicked(
            position: Int,
            arrListProductDetailsBanner: ArrayList<String>
        ) {

            val i = Intent(this@ProductDetailsActivity, ImageViewer::class.java)
            i.putExtra("arrayOfImages", arrListProductDetailsBanner)
            i.putExtra("currentItem", position)
            startActivity(i)
        }

    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        relAttribute1.setOnClickListener {
            showAttribute1SelectionDialog()
        }
        relAttribute2.setOnClickListener {
            showAttribute2SelectionDialog()
        }

        relDesc.setOnClickListener {


            if (elDesc.isExpanded) {
                elDesc.collapse()
                imgDesc.rotation = 0f
            } else {
                elDesc.expand()
                imgDesc.rotation = 180f
            }
        }

        relComposer.setOnClickListener {
            if (elComposer.isExpanded) {
                elComposer.collapse()
                imgComposer.rotation = 0f
            } else {
                elComposer.expand()
                imgComposer.rotation = 180f
            }
        }
        relShipping.setOnClickListener {
            if (elShipping.isExpanded) {
                elShipping.collapse()
                imgShipping.rotation = 0f
            } else {
                elComposer.expand()
                imgComposer.rotation = 180f
            }
        }

        relAbout.setOnClickListener {
            if (elAbout.isExpanded) {
                elAbout.collapse()
                imgAbout.rotation = 0f
            } else {
                elAbout.expand()
                imgAbout.rotation = 180f
            }
        }

        txtReview.setOnClickListener {
            if (Global.isUserLoggedIn(this@ProductDetailsActivity)) {
                if (!productDetailData?.reviews.isNullOrEmpty()) {
                    val i = Intent(this, AllReviewsActivity::class.java)
                    i.putExtra("reviews", productDetailData?.reviews)
                    i.putExtra("productId", strProductID)
                    startActivityForResult(i, ADD_REVIEW_REQUEST)
                } else {
                    val i = Intent(this, AddReviewActivity::class.java)
                    i.putExtra("productId", strProductID)
                    startActivityForResult(i, ADD_REVIEW_REQUEST)
                }
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.putExtra("isFromProductDetails", "yes")
                startActivityForResult(i, ADD_REVIEW_REQUEST)
            }

        }

        txtNotifyMe.setOnClickListener {
            Global.notifyMe(this@ProductDetailsActivity, strProductID)
        }


        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.putExtra("isFromProducts", "yes")
                startActivityForResult(i, 1)
            }
        }



        ivWishList.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                if (productDetailData?.item_in_wishlist == 1) {
                    addOrRemoveWishlist(strProductID, "product", false)
                } else {
                    addOrRemoveWishlist(strProductID, "product", true)
                }
            } else {
                val i = Intent(this, LoginActivity::class.java)
                i.putExtra("isFromProducts", "yes")
                startActivityForResult(i, 1)
            }
        }


        /*relCart.setOnClickListener {
            val i = Intent(this, CartActivity::class.java)
            //i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            //i.putExtra("fromProductDetailToCart", "yes")
            startActivity(i)
        }*/


        /*linBrands.setOnClickListener {
            println("category product details: " + strCategoryId)

            val i = Intent(this, ProductListActivity::class.java)
            i.putExtra("brandID", productDetailData?.brand_id.toString())
            i.putExtra("header_text", productDetailData?.brand_name)
            i.putExtra("isFrom", "brand")
            i.putExtra("banner", productDetailData?.brand_banner_image)
            i.putExtra("categoryID", strCategoryId)
            i.putExtra("has_subcategory", "yes")
            startActivity(i)
        }*/
/*
        txtAddCart.setOnClickListener {
            println("Entity id: " + strEntityID)
            if (productDetailData?.remaining_quantity != 0) {
                if (strEntityID.isNullOrEmpty()) {
                    Global.showSnackbar(
                        this,
                        resources.getString(R.string.please_select_attribute)
                    )
                } else {
                    if (Global.isUserLoggedIn(this)) {
                        addToCartOnline() // this method will add product in cart by calling ws
                    } else {
                        addToCartOffline()
                    }
                }
            } else {
                //out of stock
                //notify me
                if (strEntityID.isNullOrEmpty()) {
                    Global.showSnackbar(
                        this,
                        resources.getString(R.string.please_select_attribute)
                    )
                } else {
                    Global.notifyMe(
                        this@ProductDetailsActivity,
                        strProductID
                    )

                }
            }
        }*/

        txtAddCart.setOnClickListener {
            validations()
        }



        ivShare.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_GALLERY_ACCESS
                    )
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_GALLERY_ACCESS
                    )
                }
            } else {
                //  println("Here i am share clicked    1   ")
                DownloadImage().execute()
            }
        }

        /*  clSizeGuide.setOnClickListener {
              if (strSizeguide?.isNotEmpty()) {
                  val i = Intent(this, WebViewActivity::class.java)
                  i.putExtra("text_header", resources.getString(R.string.size_guide))
                  i.putExtra("strUrl", txtLink.text.toString())
                  startActivity(i)
              } else {
                  Global.showSnackbar(
                      this,
                      resources.getString(R.string.size_guide_error)
                  )
              }
              *//* if (expandableSize.isExpanded) {
                 expandableSize.collapse()
                 imgGuide.rotation = 0f
             } else {
                 expandableSize.expand()
                 imgGuide.rotation = 90f
             }*//*
        }*/

        relCartImage.setOnClickListener {
            val i = Intent(this, CartActivity::class.java)
            startActivity(i)
        }


    }

    @SuppressLint("StringFormatInvalid")
    private fun validations() {
        if (!configurable_option.isNullOrEmpty()) {
            if ((configurable_option?.size as Int) > 1) {
                if (optionId1?.isNullOrEmpty()) {
                    Global.showSnackbar(
                        this,
                        resources.getString(
                            R.string.please_select_attribute,
                            configurable_option?.get(0)?.type
                        )
                    )
                    return
                }
                if (optionId2?.isNullOrEmpty()) {
                    Global.showSnackbar(
                        this,
                        resources.getString(
                            R.string.please_select_attribute,
                            configurable_option?.get(1)?.type
                        )
                    )
                    return
                }

            } else {
                if (optionId1?.isNullOrEmpty()) {

                    Global.showSnackbar(
                        this,
                        resources.getString(
                            R.string.please_select_attribute,
                            configurable_option?.get(0)?.type
                        )
                    )
                    return
                }
            }
        }

        if (Global.isUserLoggedIn(this)) {
            addToCartOnline()
        } else {
            addToCartOffline()
        }
    }


    //setting configuration and attributes on product details API response
    private fun setConfig(configurableOption: List<ProductDetailConfigurableOptionModel>?) {
        configurable_option = ArrayList()
        if (!configurableOption.isNullOrEmpty()) {
            configurable_option?.clear()
            configurable_option?.addAll(configurableOption)
            if (productType != "grouped") {
                if (configurableOption.size == 1) {
                    txtAttr1.text = configurableOption[0].attributes!![0].value
                    optionId1 = configurableOption[0].attributes!![0].option_id.toString()

/*                  txtAttr1.text =
                        getString(
                            R.string.select_attribute,
                            configurableOption[0].type.toString().toLowerCase()
                        )
*/
                    attributeValue1 = configurableOption[0].type ?: ""
                    attributeId1 = configurableOption[0].attribute_id.toString()
                    arrListConfiguration1 = ArrayList()
                    configurableOption[0].attributes?.let { arrListConfiguration1.addAll(it) }
                    for (value in arrListConfiguration1) {
                        value.isSelected = true
                        optionValue1 = value.value ?: ""
                    }
                    linAttributes1.visibility = View.GONE
                    relAttribute1.isClickable = false
                    relAttribute1.visibility = View.VISIBLE
                    relAttribute2.visibility = View.GONE
                } else {
                    /* txtAttr1.text =
                         getString(
                             R.string.select_attribute,
                             configurableOption[0].type.toString().toLowerCase()
                         )
                     txtAttr2.text =
                         getString(
                             R.string.select_attribute,
                             configurableOption[1].type.toString().toLowerCase()
                         )*/
                    txtAttr1.text = configurableOption[0].attributes!![0].value

                    txtAttr2.text = configurableOption[1].attributes!![0].value

                    attributeValue1 = configurableOption[0].type.toString()
                    attributeValue2 = configurableOption[1].type.toString()

                    optionId1 = configurableOption[0].attributes!![0].option_id.toString()
                    optionId2 = configurableOption[1].attributes!![0].option_id.toString()


                    if (configurableOption[0].attributes!!.size == 1) {
                        linAttributes1.visibility = View.GONE
                        relAttribute1.isClickable = false
                    } else {
                        linAttributes1.visibility = View.VISIBLE

                    }
                    if (configurableOption[1].attributes!!.size == 1) {
                        linAttributes2.visibility = View.GONE
                        relAttribute2.isClickable = false
                    } else {
                        linAttributes2.visibility = View.VISIBLE
                    }
                    relAttribute2.visibility = View.VISIBLE
                    relAttribute1.visibility = View.VISIBLE


                    arrListConfiguration1 = ArrayList()
                    configurableOption[0].attributes?.let { arrListConfiguration1.addAll(it) }
                    for (value in arrListConfiguration1) {
                        value.isSelected = true
                        optionValue1 = value.value ?: ""
                    }

                    arrListConfiguration2 = ArrayList()
                    configurableOption[1].attributes?.let { it ->
                        arrListConfiguration2.addAll(
                            it
                        )
                    }
                    for (value in arrListConfiguration2) {
                        value.isEnabled = true
                        value.isSelected = true
                        optionValue2 = value.value.toString()
                    }

                    attributeId1 = configurableOption[0].attribute_id.toString()
                    attributeId2 = configurableOption[1].attribute_id.toString()


                    relAttribute1.visibility = View.VISIBLE
                    relAttribute2.visibility = View.VISIBLE
                }
            } else {
                if (configurableOption.size == 1) {
                    //attributes
                    txtAttr1.text =
                        getString(
                            R.string.select_attribute,
                            configurableOption[0].type.toString().toLowerCase()
                        )

                    attributeValue1 = configurableOption[0].type.toString()
                    attributeId1 = configurableOption[0].attribute_id.toString()
                    arrListConfiguration1 = ArrayList()
                    configurableOption[0].attributes?.let { arrListConfiguration1.addAll(it) }
                    arrListConfiguration1[0].isSelected = true
                    optionValue1 = arrListConfiguration1[0].value.toString()
                    optionId1 = arrListConfiguration1[0].option_id.toString()


                    relAttribute1.visibility = View.VISIBLE
                    relAttribute2.visibility = View.GONE

                } else {

                    txtAttr1.text =
                        getString(
                            R.string.select_attribute,
                            configurableOption[0].type.toString().toLowerCase()
                        )
//                    txtAttr1Value.text = configurableOption[0].attributes?.get(0)?.value

                    attributeValue1 = configurableOption[0].type.toString()
                    attributeId1 = configurableOption[0].attribute_id.toString()

                    arrListConfiguration1 = ArrayList()
                    configurableOption[0].attributes?.let { arrListConfiguration1.addAll(it) }
                    arrListConfiguration1[0].isSelected = true
                    optionValue1 = arrListConfiguration1[0].value.toString()
                    optionId1 = arrListConfiguration1[0].option_id.toString()

                    txtAttr2.text =
                        getString(
                            R.string.select_attribute,
                            configurableOption[1].type.toString().toLowerCase()
                        )

//                  txtAttr2Value.text = configurableOption[1].attributes?.get(0)?.value
                    attributeValue2 = configurableOption[1].type.toString()
                    arrListConfiguration2 = ArrayList()
                    configurableOption[1].attributes?.let { arrListConfiguration2.addAll(it) }

                    for (value in arrListConfiguration2) {
                        value.isEnabled = true
                    }

                    relAttribute2.visibility = View.VISIBLE
                    relAttribute1.visibility = View.VISIBLE
                    changeConfig(1)
                }
            }

        } else {
            relAttribute1.visibility = View.GONE
            relAttribute2.visibility = View.GONE
        }
    }

    //call this method to change configuration on any attribute change
    private fun changeConfig(configType: Int) {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            val configChange =
                ConfigurationOptionRequestModel(
                    strProductID,
                    attributeId1 + attributeId2,
                    optionId1 + optionId2
                )
            disposable = Global.apiService.checkConfigOptions(
                strProductID,
                attributeId1 + attributeId2,
                optionId1 + optionId2,
                com.app.blockaat.apimanager.WebServices.ConfigurationOptionWs + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    println("RESPONSE - Get config Ws :   " + Gson().toJson(result.data))
                    if (result != null) {
                        hideProgressDialog()
                        if (result.status == 200) {
                            if (!result.data.isNullOrEmpty()) {
                                strEntityID = result.data[0]?.entity_id.toString()
                                setAttribute(result.data, configType)
                                if (!Global.isUserLoggedIn(this)) {
                                    imageOffline = result.data?.get(0)?.image_url.toString()
                                }

                                // Set Banner Images After select Color
                                if (!result.data.get(0)?.images.isNullOrEmpty()) {
                                    val params = RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        Global.getDimenVallue(this, 320.0).toInt()
                                    )
                                    autoPager?.layoutParams = params
                                    result.data.get(0)?.images?.let { setProductBannerImages(it) }
                                }
                            }

                        } else {

                        }
                    } else {
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }

                }, { error ->
                    hideProgressDialog()
                    nestedScrollView.visibility = GONE
                    txtAddCart.visibility = GONE
                    //  println("Error in change config:" + error.localizedMessage)
                    error.printStackTrace()
                    Global.showSnackbar(this, resources.getString(R.string.error))
                })
        }

    }


    private fun setAttribute(
        data: ArrayList<ConfigurationOptionDataModel?>?,
        configType: Int
    ) {
        attributeId2 = "," + data?.get(0)?.attribute_id.toString()
        //  strEntityID = data[0]?.e.toString()


        if (!data?.get(0)?.attributes.isNullOrEmpty() && configType == 1) {
            val attrAvailable = data?.get(0)?.attributes

            for (attrValue in arrListConfiguration2) {
                attrValue.isEnabled = false

                if (attrAvailable != null) {

                    for (available in attrAvailable) {
                        if (attrValue.option_id == available.option_id) {
                            attrValue.isEnabled = true
                            //If single second attribute present call api

                            if (attrAvailable.size == 1) {
                                attrValue.isSelected = true
                                optionValue2 = attrValue.value ?: ""
//                                txtAttr2Value.text = attrValue.value ?: ""
                                optionId2 = "," + attrValue.option_id.toString()
                                changeConfig(2)
                                break
                            }
                        }
                    }


                }

            }


        } else {
            if (!arrListConfiguration1.isNullOrEmpty()) {
                for (config in arrListConfiguration1) {
                    //  println("Config option: 1" + config.value + " " + config.isSelected)
                    if (config.isSelected) {
                        config.isEnabled = false
                    }
                }
//                adapterConfiguration1.notifyDataSetChanged()
            }
            if (configType == 1) {
                if (!arrListConfiguration2.isNullOrEmpty()) {
                    for (config in arrListConfiguration2) {
                        //  println("Config option: 2" + config.value + " " + config.isSelected)
                        config.isEnabled = false
//                        config.isPresent = false
                    }
                }
            }


        }


    }


    private fun addToCartOnline() {
        if (strEntityID.isNullOrEmpty()) {
            strEntityID = strProductID
        }
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog(this)
            val addCartObject = AddCartRequestApi(
                Global.getStringFromSharedPref(
                    this, Constants.PREFS_USER_ID
                ), strEntityID, "1", strInfluencerID, ""
            )
            val disposable = Global.apiService.addToCart(
                com.app.blockaat.apimanager.WebServices.AddToCartWs + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    this,
                    Constants.PREFS_STORE_CODE
                ), addCartObject
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        //  println("RESPONSE - AddToCart Ws :   " + Gson().toJson(result))
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {

//                                if (isFromWishlist) {
//                                    deleteFromWishlist()
//                                }

                                if (!productsDBHelper.isProductPresentInCart(
                                        strProductID,
                                        strEntityID
                                    )
                                ) {
                                    //once added in ws update same in DB
                                    productsDBHelper.addProductToCart(
                                        ProductsDataModel(
                                            strProductID,
                                            strEntityID,
                                            productDetailData?.name,
                                            productDetailData?.brand_name,
                                            productDetailData?.image,
                                            productDetailData?.description,
                                            "1",
                                            strProductPrice,
                                            strProductRegularPrice,
                                            productDetailData?.SKU,
                                            productDetailData?.remaining_quantity.toString(),
                                            productDetailData?.is_featured.toString(),
                                            productDetailData?.is_saleable.toString(),
                                            productDetailData?.product_type,
                                            attributeValue1,
                                            attributeValue2
                                        )
                                    )
                                    //     println("HERE I M  :::  ADD - ONLINE :::    " + " : " + strProductID + "   :   " + strEntityID)
                                } else {
                                    var qty: Int =
                                        productsDBHelper.getQtyInCart(strProductID, strEntityID)
                                            .toInt()
                                    qty += 1
                                    productsDBHelper.updateProductsInCart(
                                        qty.toString(),
                                        strProductID,
                                        strEntityID
                                    )
                                    //    println("HERE I M  :::  UPDATE - ONLINE :::    " + " : " + strProductID + "   :   " + strEntityID)
                                }
                                updateCounts()
                                println(
                                    "Here i am cart total is 1   " + Global.getTotalCartProductQtyCount(
                                        this
                                    )
                                )


                                showShoppingAlert()

                            } else {
                                Global.showSnackbar(
                                    this,
                                    result.message.toString()
                                )
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        // println("ERROR - AddToCart Ws :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }
    }

    private fun showShoppingAlert() {
        updateCounts()

        showAlert(
            this,
            "",
            resources.getString(R.string.cart_success_msg),
            resources.getString(R.string.checkout),
            resources.getString(R.string.yes),
            false,
            R.drawable.ic_alert_bag,
            object : AlertDialogInterface {
                override fun onYesClick() {
                    val intent = Intent(this@ProductDetailsActivity, CartActivity::class.java)
                    startActivity(intent)
                }

                override fun onNoClick() {
                    onBackPressed()


                }

            })
    }

    fun showAlert(
        activity: Context,
        title: String,
        msg: String,
        yesBtn: String,
        noBtn: String,
        singleBtn: Boolean,
        image: Int,
        alertDialogInterface: AlertDialogInterface
    ) {
        try {
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_alert)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window?.attributes = lp
            dialog.setCanceledOnTouchOutside(false)
            //dialog.txtAlertTitle.text = title
            dialog.txtAlertTitle.text = activity.getString(R.string.awesome_choice)
            dialog.txtAlertMessage.text = msg
            dialog.btnAlertNegative.text = noBtn
            dialog.btnAlertPositive.text = yesBtn
            dialog.ivAlertImage.setImageResource(image)

            dialog.txtAlertMessage.typeface = Global.fontRegular
            dialog.txtAlertTitle.typeface = Global.fontSemiBold
            dialog.btnAlertNegative.typeface = Global.fontBtn
            dialog.btnAlertPositive.typeface = Global.fontBtn
            dialog.btnAlertPositive.visibility = if (singleBtn) GONE else VISIBLE
            dialog.btnAlertNegative.setOnClickListener() {
                dialog.dismiss()
                alertDialogInterface.onNoClick()
            }
            dialog.btnAlertPositive.setOnClickListener() {
                dialog.dismiss()
                alertDialogInterface.onYesClick()
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addToCartOffline() {
        if (strEntityID.isNullOrEmpty()) {
            strEntityID = strProductID
        }
        if (!productsDBHelper.isProductPresentInCart(strProductID, strEntityID)) {

            if (!productsDBHelper.isProductPresentInCart(strProductID, strEntityID)) {
                //once added in ws update same in DB
                productsDBHelper.addProductToCart(
                    ProductsDataModel(
                        strProductID,
                        strEntityID,
                        productDetailData?.name,
                        productDetailData?.brand_name,
                        imageOffline,
                        productDetailData?.description,
                        "1",
                        strProductPrice,
                        strProductRegularPrice,
                        productDetailData?.SKU,
                        productDetailData?.remaining_quantity.toString(),
                        productDetailData?.is_featured.toString(),
                        productDetailData?.is_saleable.toString(),
                        productDetailData?.product_type,
                        optionValue1,
                        optionValue2
                    )
                )
            } else {
                var qty: Int = productsDBHelper.getQtyInCart(strProductID, strEntityID).toInt()
                qty += 1
                productsDBHelper.updateProductsInCart(qty.toString(), strProductID, strEntityID)
            }

        } else {
            var qty: Int = productsDBHelper.getQtyInCart(strProductID, strEntityID).toInt()
            qty += 1
            productsDBHelper.updateProductsInCartOffline(
                qty.toString(),
                strProductID,
                strEntityID,
                attributeValue1,
                attributeValue2
            )
            //println("HERE I M  :::  UPDATE     " + strEntityID + "   :   " + strAttributeTwoValue + "    :   " + strAttributeOneValue)

        }

        showShoppingAlert()
        //after adding data in cart updating cart badge
        //updateCartBadge()
    }


    private fun addToCartRelated(position: Int) {
        val model = AddCartRequestModel()
        model.id = arrListRelatedItems[position].id
        model.entity_id = arrListRelatedItems[position].id
        model.name = arrListRelatedItems[position].name
        model.SKU = arrListRelatedItems[position].SKU
        model.regular_price = arrListRelatedItems[position].regular_price
        model.final_price = arrListRelatedItems[position].final_price
        model.is_saleable = arrListRelatedItems[position].is_saleable
        model.brand_name = arrListRelatedItems[position].brand_name
        model.image = arrListRelatedItems[position].image
        model.product_type = arrListRelatedItems[position].product_type

        if (Global.isUserLoggedIn(this)) {
            addToCartOnline(this, model)
        } else {
            addToCartOffline(this, model)
        }
        //after adding data in cart updating cart badge
        //updateCartBadge()
    }


    @SuppressLint("CheckResult")
    private fun addOrRemoveWishlist(productId: String, type: String, flag: Boolean) {
        Global.addOrRemoveWishList(
            this,
            productId,
            productsDBHelper,
            flag,
            object : AddWishListInterface {
                override fun onRemove(result: WishListResponseModel) {
                    updateCounts()
                    if (type == "product") {
                        productDetailData?.item_in_wishlist = 0
                        ivWishList.setImageResource(R.drawable.ic_wishlist_unselected)
                        if (productsDBHelper.isProductPresentInWishlist(strProductID)) {
                            productsDBHelper.deleteProductFromWishlist(strProductID)
                            productListDataModel?.item_in_wishlist = 0
                        }
                    } else if (type == "related") {
                        updateRelatedItems()
                    }
                }

                override fun onAdd(result: WishListResponseModel) {
                    updateCounts()
                    if (type == "product") {
                        productDetailData?.item_in_wishlist = 1
                        ivWishList.setImageResource(R.drawable.ic_wishlist_selected)
                        if (!productsDBHelper.isProductPresentInWishlist(strProductID)) {
                            productsDBHelper.addProductToWishlist(
                                ProductsDataModel(
                                    strProductID
                                )
                            )
                            productListDataModel?.item_in_wishlist = 1
                        }
                    } else if (type == "related") {
                        updateRelatedItems()
                    }
                }

            })

    }


    private inner class DownloadImage : AsyncTask<String?, Void?, Bitmap?>() {

        override fun onPreExecute() {
            super.onPreExecute()
            Global.showProgressDialog(this@ProductDetailsActivity)
        }

        override fun doInBackground(vararg URL: String?): Bitmap? {
            var bitmap: Bitmap? = null
            try {
                // Download Image from URL
                val input = java.net.URL(strProductImage).openStream()
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input)


            } catch (e: Exception) {
                e.printStackTrace()
            }

            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            Global.hideProgressDialog()
            val path = MediaStore.Images.Media.insertImage(
                contentResolver,
                result,
                "Image Description",
                null
            )

            if (path != null) {
                val uri = Uri.parse(path)
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_SUBJECT, strProductName)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    strProductName + "\n" + branchIoLink
                )
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.type = "image/*"
                startActivity(Intent.createChooser(intent, "Share Data"))
            } else {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, strProductName)
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    strProductName + "\n" + branchIoLink
                )
                startActivity(Intent.createChooser(intent, "Share Data"))
            }

        }
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    private fun showAttribute1SelectionDialog() {
        if (!arrListConfiguration1.isNullOrEmpty()) {
            val parentView =
                layoutInflater.inflate(R.layout.layout_bottom_dialog_attribute, null)
            val bottomSheerDialog = BottomSheetDialog(this)
            bottomSheerDialog.setContentView(parentView)
            bottomSheerDialog.setCanceledOnTouchOutside(false)
            bottomSheerDialog.setCancelable(false)
            val wheelView = parentView.findViewById(R.id.wheelView) as WheelView
            val txtDone = parentView.findViewById(R.id.txtDone) as TextView
            val txtCancel = parentView.findViewById(R.id.txtCancel) as TextView
            txtCancel.typeface = Global.fontMedium
            txtDone.typeface = Global.fontMedium

            val arrayListData: ArrayList<String> = ArrayList()
            for (d in 0 until arrListConfiguration1!!.size) {
                arrayListData.add(this!!.arrListConfiguration1?.get(d)?.value ?: "")
            }

            if (arrayListData.isNotEmpty()) {
                wheelView.setItems(arrayListData, Global.getDimenVallue(this, 5.0).toInt())
                txtCancel.setOnClickListener {
                    bottomSheerDialog.dismiss()
                }

                txtDone.setOnClickListener {
                    bottomSheerDialog.dismiss()
                    val attr: ProductDetailAttributeModel =
                        arrListConfiguration1!![wheelView.seletedIndex]

                    txtAttr1.text = arrListConfiguration1!![wheelView.seletedIndex]?.value ?: ""
                    attributeValue1 = arrListConfiguration1[wheelView.seletedIndex]?.value ?: ""
                    optionId1 = arrListConfiguration1!![wheelView.seletedIndex]?.option_id ?: ""
                    onAttributeChange(
                        attributeId1, attr, 1,
                        configurable_option?.get(0)?.type.toString(), wheelView.seletedIndex
                    )
                }
                bottomSheerDialog.show()
            }
        }
    }

    private fun showAttribute2SelectionDialog() {
        if (!arrListConfiguration2?.isNullOrEmpty()) {
            val parentView =
                layoutInflater.inflate(R.layout.layout_bottom_dialog_attribute, null)
            val bottomSheerDialog = BottomSheetDialog(this)
            bottomSheerDialog.setContentView(parentView)
            bottomSheerDialog.setCanceledOnTouchOutside(false)
            bottomSheerDialog.setCancelable(false)
            val wheelView = parentView.findViewById(R.id.wheelView) as WheelView
            val txtDone = parentView.findViewById(R.id.txtDone) as TextView
            val txtCancel = parentView.findViewById(R.id.txtCancel) as TextView
            txtCancel.typeface = Global.fontMedium
            txtDone.typeface = Global.fontMedium

            val arrayListData: ArrayList<String> = ArrayList()
            for (d in 0 until arrListConfiguration2.size) {
                arrayListData.add(this.arrListConfiguration2?.get(d)?.value ?: "")
            }

            if (arrayListData.isNotEmpty()) {
                wheelView.setItems(arrayListData, Global.getDimenVallue(this, 5.0).toInt())
                txtCancel.setOnClickListener {
                    bottomSheerDialog.dismiss()
                }

                txtDone.setOnClickListener {
                    bottomSheerDialog.dismiss()
                    val attr: ProductDetailAttributeModel =
                        arrListConfiguration2!![wheelView.seletedIndex]
                    txtAttr2.text = arrListConfiguration2!![wheelView.seletedIndex]?.value ?: ""
                    attributeValue2 = arrListConfiguration2[wheelView.seletedIndex]?.value ?: ""
                    optionId2 = arrListConfiguration2!![wheelView.seletedIndex]?.option_id ?: ""
                    onAttributeChange(
                        attributeId2, attr, 2,
                        configurable_option?.get(1)?.type.toString(), wheelView.seletedIndex
                    )
                }
                bottomSheerDialog.show()
            }
        }
    }

    //call this method on attribute change
    fun onAttributeChange(
        attributeId: String,
        attr: ProductDetailAttributeModel,
        configType: Int,
        type: String,
        position: Int
    ) {
        if (configType == 1) {
            if (attr.isSelected) {
                attributeId1 = attributeId
                attributeId2 = ""
                optionId1 = attr.option_id?.toString() ?: ""
                strEntityID = strProductID
                optionId2 = ""

                //for offline cart
                optionValue1 = attr.value ?: ""
                optionValue2 = ""
//                txtAttr1Value.text = optionValue1

                arrListConfiguration1[position].isSelected = true
//                adapterConfiguration1.notifyDataSetChanged()
                for (arrConfig in arrListConfiguration1) {
                    // arrConfig.isSelected = false
                    if (arrConfig.option_id == attr.option_id) {
                        arrConfig.isSelected = true
                    }
                }
//                adapterConfiguration1.notifyDataSetChanged()
                if (!arrListConfiguration2.isNullOrEmpty()) {
                    for (arrConfig in arrListConfiguration2) {
                        arrConfig.isSelected = false
                    }
//                    adapterConfiguration2.notifyDataSetChanged()

                }

                changeConfig(configType)

            }
        } else {
            if (!attr.isSelected && attr.isEnabled) {
                attributeId2 = ",$attributeId"
                //for offline cart
                optionId2 = "," + attr.option_id.toString()
                optionValue2 = attr.value ?: ""
//                txtAttr2Value.text = optionValue2
                strEntityID = attr.option_product_id.toString()
                for (arrConfig in arrListConfiguration2) {
                    arrConfig.isSelected = false
                    if (arrConfig.option_id == attr.option_id) {
                        arrConfig.isSelected = true
                    }
                }
//                adapterConfiguration2.notifyDataSetChanged()
                changeConfig(configType)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    override fun onBackPressed() {
        if (isFromWishlist) {
            //if coming from wishlist then we will let that method know so he can refresh wishlist as user can add and remove from wishlist in detail page
            val i = Intent()
            setResult(2, i)
        }
        this.finish()

    }

    override fun onPostResume() {
        super.onPostResume()
        updateCounts()
        if (productDetailData != null) {
            if (productsDBHelper.isProductPresentInWishlist(strProductID)) {
                productDetailData?.item_in_wishlist = 1
                /* sefali ivWishList.setImageResource(R.drawable.ic_wishlist_selected)*/
            } else {
                productDetailData?.item_in_wishlist = 0
                /* sefali  ivWishList.setImageResource(R.drawable.ic_wishlist_unselected)*/
            }
        }
        updateRelatedItems()
    }

    private fun updateCounts() {
        if (Global.getTotalCartProductQtyCount(this) > 0) {
            txtCartCount.visibility = View.VISIBLE
            txtCartCount.text =
                Global.getTotalCartProductQtyCount(this).toString()
        } else {
            txtCartCount.visibility = View.GONE
        }

        if (Global.getTotalWishListProductCount(this) > 0) {
            txtWishlistCount.visibility = View.VISIBLE
            txtWishlistCount.text =
                Global.getTotalWishListProductCount(this).toString()
        } else {
            txtWishlistCount.visibility = View.GONE
        }
    }

    private fun updateRelatedItems() {
        if (arrListRelatedItems.size > 0) {
            for (i in 0 until arrListRelatedItems.size) {
                arrListRelatedItems[i].item_in_wishlist =
                    if (productsDBHelper.isProductPresentInWishlist(arrListRelatedItems[i].id)) 1 else 0
            }
            adapterRelatedProducts?.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.user_success_msg)
            )

        } else if (requestCode == ADD_REVIEW_REQUEST && resultCode == ADD_REVIEW_RESULT) {
            Global.showSnackbar(
                this,
                resources.getString(R.string.add_review_success_msg)
            )
            getProductDetail()
        }
    }

    private fun sendEmail() {
        val strCustomerSupportEmail =
            Global.strSupportEmail
        if (strCustomerSupportEmail.isNullOrEmpty()) {
            return
        }
        val emailText =
            "\n\n\n\n\nApp version: " + BuildConfig.VERSION_NAME + "(${BuildConfig.VERSION_CODE})" +
                    "\nAndroid version: " +
                    "" + Build.VERSION.RELEASE +
                    "\nDevice: " + Build.MODEL +
                    "\n\n\n\nSent from Android"


        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", strCustomerSupportEmail, null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject))
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }

    private fun showAlert() {
        val strCustomerSupportNum = Global.getStringFromSharedPref(
            this@ProductDetailsActivity,
            Global.strSupportPhone
        )
        if (strCustomerSupportNum.isNullOrEmpty()) {
            return
        }

        try {
            Global.showAlert(this,
                resources.getString(R.string.call),
                resources.getString(R.string.call_alert_msg, strCustomerSupportNum),
                resources.getString(R.string.yes_btn),
                resources.getString(R.string.no_btn),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.parse("tel:" + strCustomerSupportNum)
                        )

                        if (ContextCompat.checkSelfPermission(
                                this@ProductDetailsActivity,
                                android.Manifest.permission.CALL_PHONE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            Global.requestCallPermission(this@ProductDetailsActivity)
                        } else {
                            startActivity(intent)
                        }

                    }

                    override fun onNoClick() {

                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
