package com.app.blockaat.brands.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.account.AccountActivity
import com.app.blockaat.brands.adapter.BrandsAdapter
import com.app.blockaat.brands.interfaces.OnBrandClickListener
import com.app.blockaat.brands.model.BrandsDataModel
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.*
import com.app.blockaat.helper.Global.hideProgressDialog
import com.app.blockaat.helper.Global.showProgressDialog
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.bumptech.glide.Glide
import com.havrylyuk.alphabetrecyclerview.StickyHeadersBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_brands.*
import kotlinx.android.synthetic.main.activity_brands.linBrandIndex
import kotlinx.android.synthetic.main.activity_brands.rvAllBrands
import kotlinx.android.synthetic.main.activity_brands.swipe_refresh_layout
import kotlinx.android.synthetic.main.fragment_brand_data.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view
import kotlinx.android.synthetic.main.toolbar_default_main.*
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class BrandsActivity : BaseActivity() {

    private var disposable: Disposable? = null
    private var arrListBrands: ArrayList<BrandsDataModel> = arrayListOf()
    private var adapterBrands: com.app.blockaat.brands.adapter.BrandsAdapter? = null
    private var isFromRefresh: Boolean? = false
    private var strCategoryId: String? = ""
    private var arrListSubCategory: ArrayList<Subcategory>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_brands)
        Global.setLocale(this@BrandsActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()
        getAllBrands()

    }


    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.menu_brand)
        imgSearch.visibility = View.VISIBLE
        relWishlistImage.visibility = View.VISIBLE
        view.visibility = View.GONE

    }

    private fun initializeFields() {
        arrListSubCategory = intent.getParcelableArrayListExtra("subcategories")
       // Log.e("arrListSubCategory", "" + arrListSubCategory)
        rvAllBrands.visibility = View.VISIBLE
        val layoutManager =
            LinearLayoutManager(this@BrandsActivity, LinearLayoutManager.VERTICAL, false)
        rvAllBrands.layoutManager = layoutManager
        adapterBrands = BrandsAdapter(arrListBrands, this@BrandsActivity, onBrandClickListener)

        val topStickyHeadersItemDecoration = StickyHeadersBuilder()
            .setAdapter(adapterBrands)
            .setRecyclerView(rvAllBrands)
            .setSticky(false)
            .build()
        rvAllBrands.addItemDecoration(topStickyHeadersItemDecoration)

        if (intent.hasExtra("categoryId")) {
            strCategoryId = intent.getStringExtra("categoryId")
        }
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_refresh_layout.setRefreshing(true)
            swipe_refresh_layout.postDelayed(Runnable {
                swipe_refresh_layout.setRefreshing(false)

                if (NetworkUtil.getConnectivityStatus(this@BrandsActivity) != 0) {
                    //set adapater based on user login and logout
                    getAllBrands()

                }
            }, 1000)
        })
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            finish()
        }


        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@BrandsActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@BrandsActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        imgSearch.setOnClickListener {
            val intent = Intent(this@BrandsActivity, SearchActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllBrands() {
        if (NetworkUtil.getConnectivityStatus(this@BrandsActivity) != 0) {

            if (!isFromRefresh!!) {
                showProgressDialog(this@BrandsActivity)
            }

            disposable = Global.apiService.getBrandData(
                com.app.blockaat.apimanager.WebServices.BrandsWs + Global.getStringFromSharedPref(
                    this@BrandsActivity,
                    Constants.PREFS_LANGUAGE) + "&category_id=" + Global.getPreferenceCategory(this@BrandsActivity)+"&is_featured=" + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        if (result != null) {
                            if (result.status == 200) {
                                arrListBrands.clear()
                                adapterBrands?.notifyDataSetChanged()
                              // println("Brand data: ")
                                if (result.data != null && result.data.isNotEmpty()) {
                                    result.data.let { arrListBrands.addAll(it) }
                                    setData()
                                    //  setIndex(arrListBrands)
                                }
                            }

                        } else {
                            //no data
                        }


                    },
                    { error ->
                        //println("ERROR - Brands Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                    })

        }
    }

    private fun setData() {
        //brand list
        arrListBrands.sortWith(Comparator { p0, p1 ->
            (p0?.name ?: "").compareTo(
                p1?.name ?: "",
                ignoreCase = true
            )
        })

        adapterBrands?.setData(arrListBrands as List<BrandsDataModel?>?)

        linBrandIndex.removeAllViews()
        for (element in adapterBrands!!.headersLetters as TreeSet<Char>) {
            val view = View.inflate(this@BrandsActivity, R.layout.item_brand_index, null)
            val txtCountryIndex = view.findViewById<TextView>(R.id.txtBrandsIndex)
            txtCountryIndex.text = element.toString()
            txtCountryIndex.typeface = Global.fontRegular
            txtCountryIndex.setOnClickListener {
                for (i in arrListBrands.indices) {
                    if (arrListBrands[i]?.name?.get(0) ?: "" == txtCountryIndex.text.toString()[0]) {
                        rvAllBrands!!.layoutManager?.scrollToPosition(i)
                        break
                    }
                }
            }
            linBrandIndex.addView(view)
        }
        rvAllBrands.visibility = View.VISIBLE
        scrollIndex1.visibility = View.VISIBLE
        swipe_refresh_layout.visibility = View.VISIBLE

    }

/*
// Previous code
    private fun getAllBrands() {
        Log.e("getAllBrands","getAllBrands")
        if (NetworkUtil.getConnectivityStatus(this@BrandsActivity) != 0) {

            if (!isFromRefresh!!) {
                showProgressDialog(this@BrandsActivity)
            }

            disposable = Global.apiService.getBrandData(
                com.app.blockaat.apimanager.WebServices.BrandsWs + Global.getStringFromSharedPref(
                    this@BrandsActivity,
                    Constants.PREFS_LANGUAGE
                ) + "&category_id=" + strCategoryId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        if (!isFromRefresh!!) {
                            hideProgressDialog()
                        }
                        if (result != null) {
                            if (result.status == 200) {
                                rvAllBrands.layoutManager =
                                    GridLayoutManager(this@BrandsActivity, 4)
                                arrListBrands = ArrayList()
                                arrListBrands.addAll(result.data as ArrayList<BrandsDataModel>)

                                if (!arrListBrands.isNullOrEmpty()) {
                                    arrListBrands.sortWith(Comparator { c1, c2 ->
                                        c1.name.compareTo(c2.name, true)
                                    })
                                    Log.e("getAllBrands_List",""+arrListBrands)

                                    adapterBrands =
                                        BrandsAdapter(
                                            arrListBrands,
                                            this,
                                            onBrandClickListener
                                        )
                                    rvAllBrands.adapter = adapterBrands
                                    adapterBrands?.notifyDataSetChanged()

                                    setIndex(arrListBrands)

                                }

                            }

                        } else {
                            //no data
                        }


                    },
                    { error ->
                        //println("ERROR - Brands Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                    })

        }
    }
*/

    private fun setIndex(arrList: ArrayList<BrandsDataModel>) {
        linBrandIndex.removeAllViews()
        val arrListAlphabet = ArrayList<String>()
        for (brand in arrListBrands) {
            val view = View.inflate(this@BrandsActivity, R.layout.item_brand_index, null)
            val txtBrandsIndex = view.findViewById<TextView>(R.id.txtBrandsIndex)
          //  println("Brand name: " + brand.name)
           // println("Brand name alphabet : " + brand.name.toCharArray()[0])
            if (!arrListAlphabet.contains(brand.name.toCharArray()[0].toString())) {
                arrListAlphabet.add(brand.name.toCharArray()[0].toString())
                txtBrandsIndex.text = brand.name.toCharArray()[0].toString()
                txtBrandsIndex.typeface = Global.fontRegular
                txtBrandsIndex.setOnClickListener {
                    for (i in arrList.indices) {
                        if (arrList[i].name[0].toUpperCase() == txtBrandsIndex.text.toString()[0].toUpperCase()) {
                            rvAllBrands.layoutManager!!.scrollToPosition(i)
                            break
                        }
                    }
                }
                linBrandIndex.addView(view)
            }


        }
    }

    override fun onResume() {
        super.onResume()
        updateCounts()
    }

    private val onBrandClickListener = object : OnBrandClickListener {
        override fun onBrandClick(type: String, position: Int, dateModel: Any?) {
            val intent = Intent(this@BrandsActivity, ProductListActivity::class.java)
            intent.putExtra("brandID", arrListBrands[position].id.toString())
            intent.putExtra("header_text", arrListBrands[position].name)
            intent.putExtra("categoryID", Global.getPreferenceCategory(this@BrandsActivity))
            intent.putExtra("banner", arrListBrands[position].banner)
            intent.putExtra("isFromBrands", "yes")
            intent.putExtra("isFrom", "brands")
            intent.putExtra("has_subcategory", "yes")
            startActivity(intent)
        }

    }

    fun updateCounts() {
        if (Global.isUserLoggedIn(this)) {

            txtWishlistCount.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null) {
            disposable?.dispose()
        }
    }
}