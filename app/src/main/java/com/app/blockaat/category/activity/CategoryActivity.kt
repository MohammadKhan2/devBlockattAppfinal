package com.app.blockaat.category.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.account.AccountActivity
import com.app.blockaat.category.adapter.CategoryAdapter
import com.app.blockaat.category.interfaces.OnCategorySelectListener
import com.app.blockaat.category.model.CategoryResponseDataModel
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.helper.Global.hideProgressDialog
import com.app.blockaat.helper.Global.showProgressDialog
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category_data.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view
import kotlinx.android.synthetic.main.toolbar_default.viewStart
import kotlinx.android.synthetic.main.toolbar_default_main.*

class CategoryActivity : BaseActivity(), OnCategorySelectListener {

    private var dialog: CustomProgressBar? = null
    private var isFromRefresh = false
    private var arrListCategory: ArrayList<Subcategory>? = null
    private var adapterCategory: CategoryAdapter? = null
    private var strCategoryId = ""
    private var is_featuredID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        CustomEvents.screenViewed(this,getString(R.string.category_screen))

        Global.setLocale(this@CategoryActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()

    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        imgSearch.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        relWishlistImage.visibility = View.VISIBLE
        txtHead.text = resources.getString(R.string.featured_categories)

    }

    private fun initializeFields() {

        isFromRefresh = false
        arrListCategory = ArrayList()
        if (intent.hasExtra("categoryId")) {
            strCategoryId = intent.getStringExtra("categoryId")!!
        }

        if (intent.hasExtra("is_featured")) {
            is_featuredID = intent.getStringExtra("is_featured")!!
        }
        adapterCategory =
            CategoryAdapter(
                arrListCategory as ArrayList<Subcategory>,
                this@CategoryActivity,
                this@CategoryActivity
            )
        rcyCategory.layoutManager = LinearLayoutManager(this@CategoryActivity)
        rcyCategory.adapter = adapterCategory

        swipe_category.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_category.isRefreshing = true
            swipe_category.postDelayed(Runnable {
                swipe_category.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(this@CategoryActivity) != 0) {
                    getCategories()
                }
            }, 1000)
        })

        getCategories()

    }


    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            finish()
        }


        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@CategoryActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@CategoryActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        imgSearch.setOnClickListener {
            val intent = Intent(this@CategoryActivity, SearchActivity::class.java)
            startActivity(intent)
        }


    }

    @SuppressLint("CheckResult")
    private fun getCategories() {
        if (NetworkUtil.getConnectivityStatus(this@CategoryActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(this@CategoryActivity)

            Global.apiService.getCategories(
                com.app.blockaat.apimanager.WebServices.CategoryWs + Global.getSelectedLanguage(
                    this@CategoryActivity
                ) + "&category_id=" + strCategoryId + "&is_featured=" + is_featuredID
                // category_id always pass 1 & is_featured if we go from home its 1 else 0

            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    ///handling success response
    private fun handleResponse(model: CategoryResponseDataModel?) {

        if (!isFromRefresh)
            hideProgressDialog()

     //   println("Success")

        if (model != null) {

            if (model.status == 200) {
                rcyCategory?.visibility = View.VISIBLE
                arrListCategory?.clear()
                model.data?.let { arrListCategory?.addAll(it) }
                adapterCategory?.notifyDataSetChanged()
            } else {
                rcyCategory?.visibility = View.GONE
            }

        }
    }
    ////

    ///handling error
    private fun handleError(error: Throwable) {

        if (!isFromRefresh)
            hideProgressDialog()
    }

    override fun onResume() {
        super.onResume()

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

    override fun onCategorySelected(position: Int) {
        if (arrListCategory?.get(position)?.has_subcategory == "Yes") {
           // println("selected catedgory: " + arrListCategory?.get(position))
            val intent = Intent(this@CategoryActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", arrListCategory?.get(position)?.id.toString())
            intent.putExtra("header_text", arrListCategory?.get(position)?.name)
            intent.putExtra("has_subcategory", arrListCategory?.get(position)?.has_subcategory)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "subCategory")
           // intent.putParcelableArrayListExtra("subcategories", arrListCategory.subcategories)
            startActivity(intent)
        } else {
            val intent = Intent(this@CategoryActivity, ProductListActivity::class.java)
            intent.putExtra("categoryID", arrListCategory?.get(position)?.toString())
            intent.putExtra("header_text", arrListCategory?.get(position)?.name)
            intent.putExtra("parent_type", "SubCategory")
            intent.putExtra("isFrom", "categories")
            intent.putExtra("has_subcategory", "no")
            intent.putExtra("banner", arrListCategory?.get(position)?.image)
            startActivity(intent)
        }
    }
}
