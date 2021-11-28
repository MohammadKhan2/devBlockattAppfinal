package com.app.blockaat.celebrities.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.celebrities.adapter.InfluencerListAdapter
import com.app.blockaat.celebrities.interfaces.OnInfluencerClickListener
import com.app.blockaat.celebrities.model.InfluenceResponseModel
import com.app.blockaat.celebrities.model.InfluencerList
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_celebrity_data.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view

class CelebrityActivity : BaseActivity() {

    private var dialog: CustomProgressBar? = null
    private var isFromRefresh = false
    private var arrListCelebrities: ArrayList<InfluencerList?>? = null
    private var strCategoryID: String = ""
    private lateinit var mTracker: Tracker
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_celebrity)

        if (Global.isUserLoggedIn(this)){
            val userId = Global.getUserId(this)
            CustomEvents.screenViewed(this,userId,getString(R.string.celebrity_screen))
        }

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListener()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        /*txtHead.text = resources.getString(R.string.menu_celebrities)*/
        txtHead.text = resources.getString(R.string.bottom_menu_stars)
        imgSearch.visibility = View.VISIBLE
        relWishlistImage.visibility = View.VISIBLE
        view.visibility = View.GONE
        viewStart.visibility = View.VISIBLE
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        rcyCelebrity.visibility = View.GONE
        isFromRefresh = false
        arrListCelebrities = ArrayList()
        if (intent.hasExtra("categoryID")) {
            strCategoryID = intent.getStringExtra("categoryID")!!
        }
        swipe_refresh_layout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipe_refresh_layout.isRefreshing = true
            swipe_refresh_layout.postDelayed(Runnable {
                swipe_refresh_layout.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(this@CelebrityActivity) != 0) {
                    getCelebrity()
                }
            }, 1000)
        })

        if (rcyCelebrity.itemDecorationCount == 0) {
            rcyCelebrity.addItemDecoration(
                com.app.blockaat.celebrities.model.CelebrityItemDecorator(
                    resources.getDimensionPixelSize(R.dimen._1sdp),
                    3
                )
            )
        }

        getCelebrity()
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtWishlistCount.typeface = Global.fontRegular

    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@CelebrityActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@CelebrityActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        imgSearch.setOnClickListener {
            val intent = Intent(this@CelebrityActivity, SearchActivity::class.java)
            startActivity(intent)
        }
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

    private fun getCelebrity() {
        if (NetworkUtil.getConnectivityStatus(this@CelebrityActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(this@CelebrityActivity)

            Global.apiService.getInfluencer(
                com.app.blockaat.apimanager.WebServices.AllInfluencersWs + Global.getLanguage(
                    this@CelebrityActivity
                ) + "&category_id=" + Global.getPreferenceCategory(this@CelebrityActivity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    ///handling success response
    private fun handleResponse(model: InfluenceResponseModel?) {

        if (!isFromRefresh)
            dismissProgressDialog()

        println("Success")

        if (model != null) {

            if (model.status == 200) {
                rcyCelebrity?.visibility = View.VISIBLE
                val layoutManager = GridLayoutManager(this@CelebrityActivity, 3)
                arrListCelebrities?.clear()
                arrListCelebrities?.addAll(model.data)
                arrListCelebrities?.sortWith(Comparator { c1, c2 ->
                    c1?.title?.compareTo(c2?.title.toString(), true) ?: 0
                })
                val adapter =
                    arrListCelebrities?.let {
                        InfluencerListAdapter(
                            it,
                            this@CelebrityActivity,
                            onInfluencerClickListener
                        )
                    }
                rcyCelebrity?.layoutManager = layoutManager
                rcyCelebrity?.adapter = adapter

            } else {
                rcyCelebrity?.visibility = View.GONE
            }

        }
    }
    ////

    private val onInfluencerClickListener = object : OnInfluencerClickListener {

        override fun onInfluencerClicked(position: Int) {
            val i = Intent(this@CelebrityActivity, ProductListActivity::class.java)
            i.putExtra("influencerID", arrListCelebrities?.get(position)?.id.toString())
            i.putExtra("header_text", arrListCelebrities?.get(position)?.title)
            i.putExtra("parent_type", "celebrity")
            i.putExtra("banner", arrListCelebrities?.get(position)?.banner)
            i.putExtra("isFrom", "celebrity")
            i.putExtra("categoryID", strCategoryID)
            i.putExtra("has_subcategory", "yes")
            startActivity(i)
        }

    }

    ///handling error
    private fun handleError(error: Throwable) {

        if (!isFromRefresh)
            dismissProgressDialog()
    }
    ////

    private fun showProgressDialog(activity: Activity) {
        dialog = CustomProgressBar(activity)
        dialog?.showDialog()
    }

    private fun dismissProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }


}