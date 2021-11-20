package com.app.blockaat.wishlist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.cart.model.AddCartRequestModel
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.wishlist.adapter.WishlistAdapter
import com.app.blockaat.wishlist.interfaces.WishlistInterface
import com.app.blockaat.wishlist.modelclass.WishListDataModel
import com.app.blockaat.wishlist.modelclass.WishListResponseModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_wishlist.*
import kotlinx.android.synthetic.main.layout_empty_page.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.ArrayList

class WishlistActivity : BaseActivity() {

    private val OPEN_PRODUCT_DETAILS: Int = 100
    private lateinit var progressDialog: CustomProgressBar
    private lateinit var layoutManager: GridLayoutManager
    private var disposable: Disposable? = null
    private lateinit var adapterWishList: WishlistAdapter
    private var arrListWishlistData: ArrayList<WishListDataModel> = arrayListOf()
    private lateinit var productsDBHelper: DBHelper
    private var isFromRefresh: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        CustomEvents.screenViewed(this,getString(R.string.wish_list_screen))

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
    }


    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.acc_my_wishlist)
    }

    @SuppressLint("StringFormatInvalid")
    private fun initializeFields() {
        ivEmpty.setImageResource(R.drawable.ic_empty_wishlist)
//        ivEmpty.setColorFilter(ContextCompat.getColor(this, R.color.red_pink_color));

        txtEmptyTitle.text = getString(R.string.empty_wishlist)
        txtEmptyMessage.text = getString(R.string.empty_wishlist_heading)
        txtEmptyBtn.text = getString(R.string.shop_now)
        productsDBHelper = DBHelper(this@WishlistActivity)

        layoutManager =  GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        rvWishList.layoutManager = layoutManager
        rvWishList.isNestedScrollingEnabled = false
        rvWishList.addItemDecoration(
            GridSpacingItemDecoration(
                2,
                2,
                false,
                Global.isEnglishLanguage(this)
            )
        )
        rvWishList.addItemDecoration(GridDividerItemDecoration(1))


       /* layoutManager = GridLayoutManager(this@WishlistActivity, 2, GridLayoutManager.VERTICAL, false)*/

        progressDialog = CustomProgressBar(this@WishlistActivity)
        rvWishList.layoutManager = layoutManager
        adapterWishList = WishlistAdapter(
            this,
            arrListWishlistData,
            wishlistInterface,
            productsDBHelper,
            false
        )
        rvWishList!!.adapter = adapterWishList

        rvWishList!!.isNestedScrollingEnabled = false

        if (Global.getTotalWishListProductCount(this@WishlistActivity) != -1) {

            linDivItems!!.visibility = View.VISIBLE
            //relItems!!.visibility = View.VISIBLE
        } else {
            linDivItems!!.visibility = View.GONE
            relItems!!.visibility = View.GONE
//            txtItems.text = resources.getString(
//                R.string.no_of_item_s_in_your_saves,
//                "0"
//            )
        }
        getWishList()
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtEmptyMessage.typeface = Global.fontSemiBold
        txtEmptyBtn.typeface = Global.fontBtn
        txtEmptyTitle.typeface = Global.fontNavBar
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        swipeRefreshLayout!!.setOnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout!!.isRefreshing = true
            swipeRefreshLayout!!.postDelayed({
                swipeRefreshLayout!!.isRefreshing = false
                //Load wishList data
                if (Global.isUserLoggedIn(this)) {
                    getWishList()
                } else {
                    rvWishList!!.visibility = View.GONE
                    relEmpty.visibility = View.VISIBLE
                    linDivItems!!.visibility = View.GONE
                }
            }, 1000)
        }

        rvWishList!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val topRowVerticalPosition =
                    if (recyclerView == null || recyclerView.childCount == 0) 0 else recyclerView.getChildAt(
                        0
                    ).top
                swipeRefreshLayout!!.isEnabled = topRowVerticalPosition >= 0

            }

        })
        txtEmptyBtn.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun getWishList() {
        if (NetworkUtil.getConnectivityStatus(this@WishlistActivity) != 0) {
            if (!isFromRefresh!!) {
                showProgressDialog(this@WishlistActivity)
            }
            disposable = Global.apiService.getWishList(
                com.app.blockaat.apimanager.WebServices.WishListWs + Global.getLanguage(this)
                        + "&user_id=" + Global.getUserId(this)
                        + "&store=" + Global.getStoreCode(this)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        productsDBHelper.deleteTable("table_wishlist")
                        if (result != null) {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            if (result.status == 200) {

                                if (result.data != null && result.data.isNotEmpty()) {
                                    rvWishList!!.visibility = View.VISIBLE
                                    relEmpty.visibility = View.GONE
                                    arrListWishlistData.clear()
                                    result.data.let { arrListWishlistData.addAll(it) }
                                    for (x in 0 until arrListWishlistData.size) {
                                        productsDBHelper.addProductToWishlist(
                                            ProductsDataModel(
                                                arrListWishlistData[x].id.toString()
                                            )
                                        )
                                    }
                                    adapterWishList.notifyDataSetChanged()

                                    if (Global.getTotalWishListProductCount(this@WishlistActivity) != -1) {

                                        linDivItems!!.visibility = View.VISIBLE
                                        //  relItems!!.visibility = View.VISIBLE
                                    } else {
//                                        txtItems.text = resources.getString(
//                                            R.string.no_of_item_in_your_saves,
//                                            "0"
//                                        )
                                        linDivItems!!.visibility = View.GONE
                                        relItems!!.visibility = View.GONE
                                    }

                                    println(
                                        "Here wishlist count is " + Global.getTotalWishListProductCount(
                                            this@WishlistActivity
                                        ).toString()
                                    )
                                } else {
                                    // wish list is empty
                                    rvWishList!!.visibility = View.GONE
                                    relEmpty.visibility = View.VISIBLE
                                }

                            } else if (result.status == 404) {
                                //wish list is empty
                                //No products in the wishlist.
                                rvWishList!!.visibility = View.GONE
                                relEmpty.visibility = View.VISIBLE

                            } else {
                                rvWishList!!.visibility = View.GONE
                                relEmpty.visibility = View.VISIBLE
                                Global.showSnackbar(this@WishlistActivity, result.message)
                            }
                        } else {
                            if (!isFromRefresh!!) {
                                hideProgressDialog()
                            }
                            Global.showSnackbar(
                                this@WishlistActivity,
                                resources.getString(R.string.error)
                            )
                        }

//                                (activity as HomeActivity).updateWishlistBadge()
                    },
                    { error ->
                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }

                        productsDBHelper.deleteTable("table_wishlist")
                    }
                )
        }
        isFromRefresh = false
    }


    private val wishlistInterface = object : WishlistInterface {
        override fun onItemClick(position: Int, type: String) {
            val a = arrListWishlistData[position]
            when (type) {
                "addToCart" -> {
                    val model = AddCartRequestModel(
                        a.id.toString(), a.id.toString(),
                        a.name, "", a.regular_price, a.final_price,
                        a.is_saleable, a.brand_name, a.image,
                        a.product_type
                    )

                    if (Global.isUserLoggedIn(this@WishlistActivity)) {
                        Global.addToCartOnline(this@WishlistActivity, model)
                    } else {
                        Global.addToCartOffline(this@WishlistActivity, model)
                    }

                }
                "deleteItem" -> {
                    Global.showAlert(
                        this@WishlistActivity,
                        "",
                        resources.getString(R.string.delete_item_wishlist_alert),
                        resources.getString(R.string.yes),
                        resources.getString(R.string.no),
                        false,
                        R.drawable.ic_alert,
                        object : AlertDialogInterface {
                            override fun onYesClick() {
                                addOrRemoveWishlist(a?.id ?: "", "wishlist", false)
                            }

                            override fun onNoClick() {
                            }
                        })
                }
                "openProduct" -> {
                    val intent =
                        Intent(this@WishlistActivity, ProductDetailsActivity::class.java)
                    intent.putExtra("strProductID", a?.id.toString())
                    intent.putExtra("strProductName", a?.name.toString())
                    startActivityForResult(intent, OPEN_PRODUCT_DETAILS)
                }
                "notifyMe" -> {
                    if (Global.isUserLoggedIn(this@WishlistActivity)) {
                        Global.notifyMe(this@WishlistActivity, a?.id ?: "")
                    } else {
                        val i = Intent(this@WishlistActivity, LoginActivity::class.java)
                        i.putExtra("isFromProducts", "yes")
                        startActivityForResult(i, 1)
                    }
                }
            }
        }

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
                    if (productsDBHelper.isProductPresentInWishlist(productId.toString())) {
                        productsDBHelper.deleteProductFromWishlist(productId.toString())
                    }
                    if (adapterWishList != null) {
                        arrListWishlistData.clear()
                        result.data.let { arrListWishlistData.addAll(it) }
                        adapterWishList.notifyDataSetChanged()
                        if (Global.getTotalWishListProductCount(this@WishlistActivity!!) != -1) {
                            linDivItems!!.visibility = View.VISIBLE
                            //  relItems!!.visibility = View.VISIBLE
                        } else {
                            linDivItems!!.visibility = View.GONE
                            relItems!!.visibility = View.GONE
                        }


                        if (arrListWishlistData != null && arrListWishlistData.size > 0) {
                            rvWishList!!.visibility = View.VISIBLE
                            relEmpty.visibility = View.GONE
                        } else {
                            // wish list is empty
                            rvWishList!!.visibility = View.GONE
                            relEmpty.visibility = View.VISIBLE
                        }
                    } else {
                        // wish list is empty
                        rvWishList!!.visibility = View.GONE
                        relEmpty.visibility = View.VISIBLE
                    }
                }

                override fun onAdd(result: WishListResponseModel) {
                }

            })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_PRODUCT_DETAILS) {
            getWishList()
        }
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        progressDialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog?.hideDialog()
        }
    }

}