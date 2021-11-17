package com.app.blockaat.navigation

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.app.blockaat.R
import com.app.blockaat.account.AccountFragment
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.brands.fragment.BrandDataFragment
import com.app.blockaat.cart.CartFragment
import com.app.blockaat.category.CategoryFragment
import com.app.blockaat.category.fragment.CategoryDataFragment
import com.app.blockaat.category.fragment.SubCategoryDataFragment
import com.app.blockaat.celebrities.CelebrityFragment
import com.app.blockaat.celebrities.fragment.CelebrityDataFragment
import com.app.blockaat.changestores.adapter.StoreAdapter
import com.app.blockaat.changestores.interfaces.UpdateStoreItemEvent
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.contactus.ContactUsFragment
import com.app.blockaat.faq.fragment.FaqDetailsFragment
import com.app.blockaat.faq.fragment.FaqFragment
import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.helper.*
import com.app.blockaat.home.HomeFragment
import com.app.blockaat.home.fragment.HomeDataFragment
import com.app.blockaat.home.model.HomeLinkModel
import com.app.blockaat.home.model.RootCategoriesData
import com.app.blockaat.home.model.RootCategoriesModel
import com.app.blockaat.intro.model.IntroResponseModelItem
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.navigation.model.Navigationmodel
import com.app.blockaat.orders.OrderListingActivity
import com.app.blockaat.productdetails.ProductDetailsActivity
import com.app.blockaat.productfilter.FilterDialogFragment
import com.app.blockaat.productlisting.fragment.FilterListFragment
import com.app.blockaat.productlisting.fragment.ProductListFragment
import com.app.blockaat.productlisting.interfaces.OnFilterClickListener
import com.app.blockaat.productlisting.interfaces.OnSortClickListener
import com.app.blockaat.productlisting.model.FilterObject
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.productlisting.model.SortObject
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.tv.TvFragment
import com.app.blockaat.tv.activity.TvProductActivity
import com.app.blockaat.webview.WebViewActivity
import com.app.blockaat.wishlist.WishlistActivity
import com.app.blockaat.wishlist.fragment.WishListFragment
import com.facebook.login.LoginManager
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.common.api.internal.LifecycleCallback.getFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.pushwoosh.Pushwoosh
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_rate_us.*
import kotlinx.android.synthetic.main.item_drawer.view.*
import kotlinx.android.synthetic.main.item_store_list.view.*
import kotlinx.android.synthetic.main.layout_filter.*
import kotlinx.android.synthetic.main.layout_sort.*
import kotlinx.android.synthetic.main.toolbar_default_main.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class NavigationActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var navigationView: NavigationView
    private var isViewAll: Boolean = false
    private var introModel: IntroResponseModelItem? = null
    private var backPressed: Long = 0
    private var selectedTabPosition: Int = -1
    private var isHomeLoaded: Boolean = false
    private var isBrandLoaded: Boolean = false
    private var isCatLoaded: Boolean = false
    private var isMusicianLoaded: Boolean = false
    private var isCartLoaded: Boolean = false
    private var isTvLoaded: Boolean = false
    private var isFaqLoaded: Boolean = false
    private var isFaqDetailsLoaded: Boolean = false
    private var isProductListLoaded: Boolean = false
    private var isContactUsLoaded: Boolean = false
    private var isAccountLoaded: Boolean = false
    private var adapterFilter: FilterPagerAdapter? = null
    private var arrListFilterData: ArrayList<ProductListingFilterModel?>? = null
    private val homeFragment: Fragment = HomeFragment()
    private val fm = supportFragmentManager
    private var activeFragment = homeFragment
    private val categoriesFragment: Fragment = CategoryFragment()
    private val cartFragment: Fragment = CartFragment()
    private var brandsFragment: Fragment = com.app.blockaat.brands.BrandFragment()
    private var celebrityFragment: Fragment = CelebrityFragment()
    private val wishListFragment: Fragment = WishListFragment()
    private val tvFragment: Fragment = TvFragment()
    private val faqFragment: Fragment = FaqFragment()
    private val faqDetailsFragment: Fragment = FaqDetailsFragment()
    private val contactUsFragment: Fragment = ContactUsFragment()
    private val accountFragment: Fragment = AccountFragment()
    private var strHomeProductListHeader = ""
    private var strBrandProductListHeader = ""
    private var strCategoryProductListHeader = ""
    private var parentType = ""
    private var arrListCategoryTitle: ArrayList<String>? = null
    var toggle: ActionBarDrawerToggle? = null
    var toolbar: Toolbar? = null
    var boolToggle = false
    private lateinit var productsDBHelper: DBHelper
    private var isProductListLoadedInHome = false
    private var isProductListLoadedInBrands = false
    private var isProductListLoadedInCategory = false
    private var filterInterface: OnFilterClickListener? = null
    private var isProductListLoadedInCelebrity = false
    private var strCelebrityProductListHeader = ""
    private var productListFragment: Fragment = ProductListFragment()
    private lateinit var mTracker: Tracker

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        return false
    }

    private lateinit var navAdapter: NavigationAapter

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                //home page
                R.id.navigation_home -> {
                    displayView(0)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_avenue -> {
                    displayView(3)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_stars -> {
                    displayView(2)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_tv -> {
                    displayView(5)
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_cart -> {
                    displayView(4)
                    return@OnNavigationItemSelectedListener true
                }

            }
            false
        }

    override fun onStart() {
        super.onStart()
        // startService(Intent(this@NavigationActivity, GetStoreService::class.java))
        arrListCategoryTitle = ArrayList()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the shared Tracker instance.
        val application: AppController = application as AppController
        mTracker = application.getDefaultTracker()!!
        mTracker.setScreenName("Home Screen")

        mTracker.send(HitBuilders.ScreenViewBuilder().build())
        productsDBHelper = DBHelper(this@NavigationActivity)
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        txtHead.visibility = VISIBLE
        viewStart.visibility = GONE
        view.visibility = VISIBLE
        if (!Global.isEnglishLanguage(this)) {
            ivBackArrow.rotationY = 180f
            navigationView.setBackgroundResource(R.drawable.ic_nav_background_arabic)
        } else {
            navigationView.setBackgroundResource(R.drawable.ic_nav_background_english)

        }
        ivBackArrow.visibility = GONE

        boolToggle = true


        /*toggle = object : ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            */
        /** Called when a drawer has settled in a completely closed state.  *//*
            override fun onDrawerClosed(view: View) {
                super.onDrawerClosed(view)
                // Do whatever you want here
                println("Drawer closed")

            }

            */
        /** Called when a drawer has settled in a completely open state.  *//*
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // Do whatever you want here
                hideKeyboard()
                println("Drawer open")
            }
        }*/

        // Set the drawer toggle as the DrawerListener
        //drawer.addDrawerListener(toggle!!)
        //toggle?.syncState()

        //navigationView.setNavigationItemSelectedListener(this)

        //bottom_navigation.itemIconTintList = null
        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottom_navigation.setItemIconTintList(null);

        // disableShiftMode(bottom_navigation)

        txtSignIn.setOnClickListener {
            val i = Intent(this@NavigationActivity, LoginActivity::class.java)
            startActivityForResult(i, 1)
            drawer.closeDrawers()
        }
        txtUserName.setOnClickListener {
            loadAccountFragment()
            drawer.closeDrawers()
        }
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        txtCopyright.text =
            resources.getString(R.string.copyright) + year.toString()+" "+ resources.getString(R.string.app_name)

        //Facebook follow us
        cvFaceBook.setOnClickListener {

            val uri = Uri.parse("https://www.facebook.com/")
            val insta = Intent(Intent.ACTION_VIEW, uri)
            insta.setPackage("com.facebook.android")

            if (isIntentAvailable(this@NavigationActivity, insta)) {
                startActivity(insta)
            } else {
                val intent = Intent(this@NavigationActivity, WebViewActivity::class.java)
                intent.putExtra("text_header", resources.getString(R.string.facebook))
                intent.putExtra("strUrl", "https://www.facebook.com/")
                startActivity(intent)
            }

            drawer.closeDrawers()
        }
        //Instagram follow us
        cvInstagram.setOnClickListener {

            val uri = Uri.parse("http://instagram.com/")
            val insta = Intent(Intent.ACTION_VIEW, uri)
            insta.setPackage("com.instagram.android")

            if (isIntentAvailable(this@NavigationActivity, insta)) {
                startActivity(insta)
            } else {
                val intent = Intent(this@NavigationActivity, WebViewActivity::class.java)
                intent.putExtra("text_header", resources.getString(R.string.instagram))
                intent.putExtra("strUrl", "https://www.instagram.com")
                startActivity(intent)
            }
            drawer.closeDrawers()
        }
        // Snapchat follow us
        cvSnapchat.setOnClickListener {

            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            intent.setPackage("com.snapchat.android")
            startActivity(Intent.createChooser(intent, "Open Snapchat"))
            drawer.closeDrawers()
        }
        cvGooglePlus.setOnClickListener {

            drawer.closeDrawers()
        }

        tvLogout.setOnClickListener {
            showLogoutAlert()
            //   (accountFragment as AccountFragment).showLogoutAlert()

            drawer.closeDrawers()
        }

        imgSearch.setOnClickListener {
            val intent = Intent(this@NavigationActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        ivDrawer.setOnClickListener {
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START)
                Global.hideKeyboard(this@NavigationActivity)
            }
        }

        ///Will handle back click when fragment show from view all
        toolbar!!.setNavigationOnClickListener() {
            if (!boolToggle) {
                //resetDrawerIcon()
                onBackPressed()
//                displayView(0)
            } else {
                val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.openDrawer(GravityCompat.START)
                } else {
                    drawer.openDrawer(GravityCompat.START)
                }
            }
        }
        ////
        arrListFilterData = ArrayList()
        setTabFonts()
        setFonts()
        setOnClickListerner()
        displayView(0)
        setLeftDrawer()
        imgSearch.visibility = View.VISIBLE
        updateCounts()
        Handler().postDelayed({
            //doSomethingHere()
            setNavigation()

        }, 1000)
        handleDeepLink()

    }

    private fun isIntentAvailable(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    fun showLogoutAlert() {

        try {
            Global.showAlert(
                this@NavigationActivity,
                "",
                resources.getString(R.string.logout_alert),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        Global.showSnackbar(
                            this@NavigationActivity,
                            resources.getString(R.string.logout_success_msg)
                        )
                        val strLanguage =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_LANGUAGE
                            )
                        val strCurrencyEN = Global.getStringFromSharedPref(
                            this@NavigationActivity,
                            Constants.PREFS_CURRENCY_EN
                        )
                        val strCurrencyAR = Global.getStringFromSharedPref(
                            this@NavigationActivity,
                            Constants.PREFS_CURRENCY_AR
                        )
                        val strCountryEn =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_COUNTRY_EN
                            )
                        val strCountryAr =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_COUNTRY_AR
                            )
                        val strStoreCode =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_STORE_CODE
                            )
                        val deviceMultiplier =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_DEVIDE_MULTIPLIER
                            )
                        val strSelectedCategory =
                            Global.getStringFromSharedPref(
                                this@NavigationActivity,
                                Constants.PREFS_CATEGORY
                            )
                        SharedPreferencesHelper.clearSharedPreferences()
                        Global.saveStringInSharedPref(
                            this@NavigationActivity,
                            Constants.PREFS_LANGUAGE,
                            strLanguage
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_STORE_CODE,
                            strStoreCode
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_EN,
                            strCurrencyEN
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_AR,
                            strCurrencyAR
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_CATEGORY,
                            strSelectedCategory
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_EN,
                            strCountryEn
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_AR,
                            strCountryAr
                        )
                        Global.saveStringInSharedPref(
                            this@NavigationActivity as AppCompatActivity,
                            Constants.PREFS_DEVIDE_MULTIPLIER,
                            deviceMultiplier
                        )

                        productsDBHelper.deleteTable("table_cart")
                        productsDBHelper.deleteTable("table_wishlist")

                        LoginManager.getInstance().logOut()
                        setUserInfo()
                        //return
                    }

                    override fun onNoClick() {
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUserInfo() {
        txtSignIn.visibility = View.VISIBLE
        tvLogout.visibility = View.GONE
        txtUser.visibility = View.GONE
        txtUserName.visibility = View.GONE
    }


    private fun handleDeepLink() {
        if (Global.getStringFromSharedPref(
                this,
                Constants.PREFS_isFROM_DEEPLINK
            ) == "yes"
        ) {
            deepLinking()
        } else if (Global.getStringFromSharedPref(
                this,
                Constants.PREFS_isFROM_DEFERRED
            ) == "yes"
        ) {
            openUniversalLink()
        } else if (intent.hasExtra(Pushwoosh.PUSH_RECEIVE_EVENT)) {
            //when coming from push notification
            pushNotifications()
        }
    }


    private fun openUniversalLink() {
        val target = Global.getStringFromSharedPref(
            this,
            Constants.PREFS_DEFERREDPLINK_TARGET
        )
        val linkId =
            Global.getStringFromSharedPref(
                this,
                Constants.PREFS_DEFERREDLINK_ID
            )
        val name = Global.getStringFromSharedPref(
            this,
            Constants.PREFS_DEFERREDLINK_NAME
        )
        // Log.e("HOME", "target:" + target + "id:" + linkId + "name:" + name)
//      TODO:navigation to be handled here


        //clear deeplink data
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_isFROM_DEFERRED,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEFERREDPLINK_TARGET,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEFERREDLINK_ID,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEFERREDLINK_NAME,
            ""
        )
    }

    //branch io navigation
    private fun deepLinking() {
        val target = Global.getStringFromSharedPref(
            this,
            Constants.PREFS_DEEPLINK_TARGET
        )
        val linkId =
            Global.getStringFromSharedPref(
                this,
                Constants.PREFS_DEEPLINK_ID
            )
        val name = Global.getStringFromSharedPref(
            this,
            Constants.PREFS_DEFERREDLINK_NAME
        )


        displayProductList(target,linkId,name)
//        TODO:handle target and id here

        //clear deeplink data
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_isFROM_DEEPLINK,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEEPLINK_TARGET,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEEPLINK_ID,
            ""
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_DEEPLINK_NAME,
            ""
        )
    }

    //pushwoosh notification navigation handled
    private fun pushNotifications() {
        var target_id = ""
        var title = ""

        val jsonObject =
            JSONObject(intent.getStringExtra(Pushwoosh.PUSH_RECEIVE_EVENT).toString())
        println("HERE IS PUSHWOOSH DATA ::: " + jsonObject)

        if (jsonObject.has("target_id") && !jsonObject.getString("target_id")
                .isNullOrEmpty()
        ) {
            target_id = jsonObject.getString("target_id")

            if (Global.isEnglishLanguage(this)) {
                if (jsonObject.has("name_en")) {
                    title = jsonObject.getString("name_en") ?: ""
                }
            } else {
                if (jsonObject.has("name_ar")) {
                    title = jsonObject.getString("name_ar") ?: ""
                }
            }
        }

        //TODO:navigation is to be handled here
        if (target_id.isNotEmpty()) {

        }
    }


    private fun setTabFonts() {

    }

    private fun setFonts() {
        txtSignIn.typeface = Global.fontBtn
        txtUserName.typeface = Global.fontMedium
        txtUser.typeface = Global.fontMedium
        tvLogout.typeface = Global.fontMedium
        txtFollowUs.typeface = Global.fontMedium
//      txtSortOptionTitle.typeface = Global.fontBold
        txtFilterByLabel.typeface = Global.fontBold
        txtApply.typeface = Global.fontBtn
        txtClearAll.typeface = Global.fontBtn
//       txtAppName.typeface = Global.fontExtraBold
    }

    private fun setOnClickListerner() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        ivShare.setOnClickListener {
            val shareIntent = ShareCompat.IntentBuilder
                .from(this)
                .setType("text/plain")
                .setChooserTitle("")
                .setText(getString(R.string.share_app_msg) + "https://play.google.com/store/apps/details?id=" + packageName)
                .intent

            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(shareIntent)
            }
        }

        /*imgCross.setOnClickListener {
            val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
        }*/

        imgUser.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                loadAccountFragment()
            } else {
                val i = Intent(this@NavigationActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@NavigationActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@NavigationActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relClearAll.setOnClickListener {
            println("Clear data")
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {

                //filterInterface?.onFilterClicked(arrListFilterData,adapterFilter,"clear")
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
            }
            adapterFilter?.notifyDataSetChanged()
        }

        imgCloseSheet.setOnClickListener {
            println("Clear data")
            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {

                //filterInterface?.onFilterClicked(arrListFilterData,adapterFilter,"clear")
                for (i in 0 until arrListFilterData?.size as Int) {
                    val fragment = adapterFilter?.getItem(i)
                    (fragment as FilterListFragment).clearAll()
                }
            }
            adapterFilter?.notifyDataSetChanged()

        }


        relApply.setOnClickListener {

            if (!arrListFilterData.isNullOrEmpty() && adapterFilter != null) {
                //filterInterface?.onFilterClicked(arrListFilterData,adapterFilter,"apply")
                EventBus.getDefault()
                    .post(FilterObject(arrListFilterData, adapterFilter, "apply", parentType))
                simpleViewnimator.visibility = View.GONE
                Global.hideKeyboard(this)
                setLeftDrawer()
            }

        }

    }

    private fun setLeftDrawer() {

        val arryList = ArrayList<Navigationmodel>()

        val leftMenu1 =
            Navigationmodel(
                1, resources.getString(R.string.menu_home),
                R.drawable.finalhome_unsel, isArrowVisible = true
            )
        arryList.add(leftMenu1)

        val leftMenu2 = Navigationmodel(
            1,
            resources.getString(R.string.bottom_menu_avenue),
            R.drawable.finalcat_unsel,
            isArrowVisible = false
        )
        arryList.add(leftMenu2)

        val leftMenu3 =
            Navigationmodel(
                0,
                resources.getString(R.string.brands),
                R.drawable.ic_brand_new,
                isArrowVisible = false
            )
        arryList.add(leftMenu3)

        /* val leftMenu4 = Navigati`onmodel(
             1,
             resources.getString(R.string.bottom_menu_stars),
             isArrowVisible = true
         )
         arryList.add(leftMenu4)*/

        val leftMenu5 =
            Navigationmodel(
                0,
                resources.getString(R.string.my_account),
                R.drawable.ic_user_grey,
                isArrowVisible = false
            )
        arryList.add(leftMenu5)
/*
        if (!Global.isEnglishLanguage(this@NavigationActivity)) {
            val leftMenu6 =
                Navigationmodel(
                    6,
                    resources.getString(R.string.change_language),
                    R.drawable.ic_english,
                    isArrowVisible = false)
            arryList.add(leftMenu6)
        }else{*/
        val leftMenu6 =
            Navigationmodel(
                0,
                resources.getString(R.string.language),
                R.drawable.ic_language,
                isArrowVisible = false
            )
        arryList.add(leftMenu6)

        /* val leftMenu6 =
             Navigationmodel(0, resources.getString(R.string.my_orders), isArrowVisible = true)
         arryList.add(leftMenu6)

         val leftMenu7 =
             Navigationmodel(0, resources.getString(R.string.acc_my_wishlist), isArrowVisible = true)
         arryList.add(leftMenu7)

         val leftMenu8 =
             Navigationmodel(0, resources.getString(R.string.current_lang), isArrowVisible = false)
         arryList.add(leftMenu8)

         val leftMenu9 =
             Navigationmodel(0, resources.getString(R.string.acc_rate_us), isArrowVisible = true)
         arryList.add(leftMenu9)*/

        navAdapter = NavigationAapter(arryList, this@NavigationActivity, onStoreUpdateClicked)
        rcyDrawer.layoutManager = LinearLayoutManager(this@NavigationActivity)
        rcyDrawer.adapter = navAdapter

        scrollNav.visibility = View.VISIBLE

        val pInfo: PackageInfo? = this.packageManager.getPackageInfo(packageName, 0)
        val verName: String = pInfo!!.versionName
        txtVersion.text = resources.getString(R.string.version_name, verName)
        //txtVersion.typeface = typeNormal
        //navigationView.setNavigationItemSelectedListener(this)

    }


    fun updateCartBadge() {
        setBadgeCount(
            4,
            Global.getTotalCartProductQtyCount(this@NavigationActivity)
        )  // set wishlist count
        println("Here i am cart count is        " + Global.getTotalCartProductQtyCount(this@NavigationActivity))
    }


    private fun setBadgeCount(position: Int, count: Int) {
        val bottomNavigationMenuView = bottom_navigation.getChildAt(0) as BottomNavigationMenuView
        val itemViewCart = bottomNavigationMenuView.getChildAt(position) as BottomNavigationItemView
        val badgeCart = LayoutInflater.from(this)
            .inflate(R.layout.layout_bottom_tab_badge, bottomNavigationMenuView, false)
        val txtBadgeCount: TextView = badgeCart.findViewById(R.id.txtBadgeCount) as TextView
        if (!Global.isEnglishLanguage(this@NavigationActivity)) {
            (txtBadgeCount.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
        }
        if (!Global.isEnglishLanguage(this@NavigationActivity)) {
            ivLogo.setImageResource(R.drawable.app_logo_ar)
            imgAppLogo.setImageResource(R.drawable.app_logo_ar)
        } else {
            ivLogo.setImageResource(R.drawable.app_logo)
            imgAppLogo.setImageResource(R.drawable.app_logo)

        }

        txtBadgeCount.text = count.toString()
        //as count will always be in english number so apply english font
        txtBadgeCount.typeface = Global.fontMedium

        if (itemViewCart.childCount != 2)
            itemViewCart.removeViewAt(2)

        if (count > 0) {
            itemViewCart.addView(badgeCart)
        }
    }


    fun displayView(position: Int, categoryId: Int = 0) {
        val data = Bundle()
        var fragment: Fragment? = null
        val fragmentManager = supportFragmentManager
        val ft = fragmentManager.beginTransaction()
        var i: Intent? = null

        txtHead.typeface = Global.fontNavBar

        when (position) {
            0 -> {
                if (selectedTabPosition != 0) {

                    if (!isHomeLoaded) {
                        fm.beginTransaction().add(R.id.content_frame, homeFragment, "0")
                            .show(homeFragment).commit()
                        isHomeLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(homeFragment).commit()
                    }

                    activeFragment = HomeFragment()
                    selectedTabPosition = 0
                    fm.beginTransaction().hide(categoriesFragment).remove(cartFragment)
                        .hide(brandsFragment)
                        .hide(tvFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(celebrityFragment).hide(accountFragment).commit()

                    activeFragment = homeFragment
                    isCartLoaded = false
                    bottom_navigation.selectedItemId = R.id.navigation_home

                    println(" Is home fragment added :" + homeFragment.isAdded)
                    if (homeFragment.isAdded) {
                        displayHomeHeader()
                    } else {
                        imgSearch.visibility = View.VISIBLE

                        ivShare.visibility = View.GONE
                        txtHead.visibility = View.GONE
                        txtHead.text = resources.getString(R.string.menu_home)
                        view.visibility = View.GONE
                        viewStart.visibility = View.GONE
                        ivLogo.visibility = View.VISIBLE
                        ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
                        ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
                        relWishlistImage.visibility = View.VISIBLE
                    }

                }

            }
            1 -> {
                if (selectedTabPosition != 1) {
                    val bundle = Bundle()
                    bundle.putString("categoryID", categoryId.toString())

                    if (!isBrandLoaded) {

                        brandsFragment.arguments = bundle
                        fm.beginTransaction().add(R.id.content_frame, brandsFragment, "1")
                            .show(brandsFragment).commit()
                        isBrandLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(brandsFragment).commit()

                    }
                    activeFragment = com.app.blockaat.brands.BrandFragment()
                    selectedTabPosition = 1
                    fm.beginTransaction().hide(homeFragment).remove(cartFragment)
                        .hide(categoriesFragment)
                        .hide(tvFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(celebrityFragment).hide(accountFragment).commit()
                    activeFragment = categoriesFragment
                    isCartLoaded = false
                    if (brandsFragment.isAdded) {
                        displayBrandHeader()
                    } else {
                        imgSearch.visibility = View.VISIBLE
                        txtHead.text = resources.getString(R.string.menu_brand)
                        txtHead.visibility = View.VISIBLE
                        ivLogo.visibility = View.GONE
                        ivShare.visibility = View.GONE
                        ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
                        ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
                        relWishlistImage.visibility = View.VISIBLE
                        view.visibility = View.GONE
                        viewStart.visibility = View.GONE
                        if (!boolToggle) {
                            resetDrawerIcon()
                        }
                    }
                }
            }

            2 -> {
                if (selectedTabPosition != 2) {

                    val bundle = Bundle()
                    bundle.putString("categoryID", categoryId.toString())
                    if (!isMusicianLoaded) {
                        celebrityFragment.arguments = bundle
                        fm.beginTransaction().add(R.id.content_frame, celebrityFragment, "3")
                            .show(celebrityFragment)
                            .commit()
                        isMusicianLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(celebrityFragment).commit()


                    }
                    activeFragment = CelebrityFragment()
                    selectedTabPosition = 2
                    fm.beginTransaction().hide(homeFragment).remove(cartFragment)
                        .hide(brandsFragment)
                        .hide(tvFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(categoriesFragment).hide(accountFragment).commit()

                    activeFragment = celebrityFragment
                    isCartLoaded = false
                    if (celebrityFragment.isAdded) {
                        displayCelebrityHeader()
                    } else {
                        imgSearch.visibility = View.VISIBLE
                        txtHead.text = resources.getString(R.string.bottom_menu_stars)
                        txtHead.visibility = View.VISIBLE
                        ivLogo.visibility = View.GONE
                        ivShare.visibility = View.GONE
                        ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
                        ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
                        relWishlistImage.visibility = View.VISIBLE
                        view.visibility = View.GONE
                        viewStart.visibility = View.GONE
                        if (!boolToggle) {
                            resetDrawerIcon()
                        }
                    }
                }

            }
            3 -> {
                if (selectedTabPosition != 3) {


                    if (!isCatLoaded) {
                        fm.beginTransaction().add(R.id.content_frame, categoriesFragment, "2")
                            .show(categoriesFragment)
                            .commit()
                        isCatLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(categoriesFragment).commit()
                    }
                    activeFragment = CategoryFragment()
                    selectedTabPosition = 3
                    fm.beginTransaction().hide(homeFragment).remove(cartFragment)
                        .hide(brandsFragment)
                        .hide(tvFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(celebrityFragment).hide(accountFragment).commit()
                    activeFragment = categoriesFragment
                    isCartLoaded = false
                    if (categoriesFragment.isAdded) {
                        displayCategoryHeader()
                    } else {
                        imgSearch.visibility = View.VISIBLE
                        /* txtHead.text = resources.getString(R.string.menu_categories)*/
                        txtHead.text = resources.getString(R.string.bottom_menu_avenue)
                        txtHead.visibility = View.VISIBLE
                        ivLogo.visibility = View.GONE
                        ivShare.visibility = View.GONE
                        ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
                        ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
                        relWishlistImage.visibility = View.VISIBLE
                        view.visibility = View.GONE
                        viewStart.visibility = View.GONE
                        if (!boolToggle) {
                            resetDrawerIcon()
                        }
                    }
                }

            }

            4 -> {
                if (selectedTabPosition != 4) {
                    if (!boolToggle) {
                        resetDrawerIcon()
                    }
                    imgSearch.visibility = View.VISIBLE

                    ivShare.visibility = View.GONE
                    txtHead.text = resources.getString(R.string.menu_cart)
                    txtHead.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                    ivLogo.visibility = View.GONE
                    viewStart.visibility = View.GONE
                    ivBackArrow.visibility = View.GONE
                    ivDrawer.visibility = View.VISIBLE
                    relWishlistImage.visibility = View.VISIBLE

                    if (!isCartLoaded) {
                        fm.beginTransaction().add(R.id.content_frame, cartFragment, "4")
                            .show(cartFragment).commit()
                        isCartLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(cartFragment).commit()
                    }
                    activeFragment = CartFragment()
                    selectedTabPosition = 4
                    fm.beginTransaction().hide(homeFragment).hide(categoriesFragment)
                        .hide(brandsFragment)
                        .hide(tvFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(celebrityFragment).remove(wishListFragment).hide(accountFragment)
                        .commit()
                    activeFragment = cartFragment
                }

            }

            5 -> {
                if (selectedTabPosition != 5) {
                    imgSearch.visibility = View.VISIBLE
                    txtHead.text = resources.getString(R.string.tv)
                    txtHead.visibility = View.VISIBLE
                    ivLogo.visibility = View.GONE
                    ivShare.visibility = View.GONE
                    ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
                    ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
                    relWishlistImage.visibility = View.VISIBLE
                    view.visibility = View.GONE
                    viewStart.visibility = View.GONE
                    if (!boolToggle) {
                        resetDrawerIcon()
                    }

                    if (!isTvLoaded) {
                        fm.beginTransaction().add(R.id.content_frame, tvFragment, "5")
                            .show(tvFragment)
                            .commit()
                        isTvLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(tvFragment).commit()
                    }
                    activeFragment = com.app.blockaat.brands.BrandFragment()
                    selectedTabPosition = 5
                    fm.beginTransaction().hide(homeFragment).remove(cartFragment)
                        .hide(brandsFragment)
                        .hide(celebrityFragment).hide(faqFragment).hide(contactUsFragment)
                        .hide(categoriesFragment).remove(wishListFragment).hide(accountFragment)
                        .commit()
                    /*fm.beginTransaction().hide(homeFragment).hide(categoriesFragment).hide(cartFragment)
                        .hide(brandsFragment).commit()*/
                    activeFragment = TvFragment()
                    isCartLoaded = false
                }

            }

            6 -> {
                if (selectedTabPosition != 6) {
                    loadFaqFragment()
                }
            }

            7 -> {
                if (selectedTabPosition != 7) {
                    loadFaqFragment()
                }
            }

            10 -> {
                if (selectedTabPosition != 10) {
                    toggle?.isDrawerIndicatorEnabled = false
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    boolToggle = false
                    imgSearch.visibility = View.GONE
                    ivBackArrow.visibility = View.VISIBLE
                    ivDrawer.visibility = View.GONE
                    view.visibility = View.VISIBLE
                    relWishlistImage.visibility = GONE
                    viewStart.visibility = GONE
                    txtHead.text = resources.getString(R.string.contact_us)
                    txtHead.visibility = View.VISIBLE
                    ivLogo.visibility = View.GONE
                    ivShare.visibility = View.GONE

                    if (!isContactUsLoaded) {
                        fm.beginTransaction().add(R.id.content_frame, contactUsFragment, "7")
                            .show(contactUsFragment)
                            .commit()
                        isContactUsLoaded = true
                    } else {
                        fm.beginTransaction().hide(activeFragment).show(contactUsFragment).commit()
                    }
                    activeFragment = ContactUsFragment()
                    selectedTabPosition = 7
                    fm.beginTransaction().hide(homeFragment).remove(cartFragment)
                        .hide(brandsFragment)
                        .hide(celebrityFragment).hide(tvFragment).hide(faqFragment)
                        .hide(categoriesFragment).remove(wishListFragment).hide(accountFragment)
                        .commit()
                    /*fm.beginTransaction().hide(homeFragment).hide(categoriesFragment).hide(cartFragment)
                        .hide(brandsFragment).commit()*/
                    activeFragment = ContactUsFragment()
                    isCartLoaded = false
                }
            }

        }

        /* when (position) {
             0 -> {
                 fragment = HomeFragment()
                 txtHead.visibility = View.VISIBLE
                 txtHead.text=resources.getString(R.string.menu_home)
                 imgLogo.visibility = View.VISIBLE
                 ft.replace(R.id.content_frame, fragment)
                 ft.commit()

             }

             1 -> {
                 fragment = BrandFragment()
                 txtHead.text=resources.getString(R.string.menu_brand)
                 txtHead.visibility = View.VISIBLE
                 imgLogo.visibility = View.GONE
                 *//* data.putString("id", strId)
                 fragment.arguments = data*//*
                ft.replace(R.id.content_frame, fragment)
                ft.commit()

            }

            2 -> {
                fragment = CategoryFragment()
                txtHead.text=resources.getString(R.string.menu_categories)
                txtHead.visibility = View.VISIBLE
                imgLogo.visibility = View.GONE
               *//* data.putString("id", strId)
                fragment.arguments = data*//*
                ft.replace(R.id.content_frame, fragment)
                ft.commit()

            }

            3 -> {
                fragment = celebrityFragment()
                txtHead.text=resources.getString(R.string.menu_music)
                txtHead.visibility = View.VISIBLE
                imgLogo.visibility = View.GONE
                *//* data.putString("id", strId)
                 fragment.arguments = data*//*
                ft.replace(R.id.content_frame, fragment)
                ft.commit()

            }

            4 -> {
                fragment = CartFragment()
                txtHead.text=resources.getString(R.string.menu_cart)
                txtHead.visibility = View.VISIBLE
                imgLogo.visibility = View.GONE
                *//* data.putString("id", strId)
                 fragment.arguments = data*//*
                ft.replace(R.id.content_frame, fragment)
                ft.commit()

            }*/
        // }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
    }

    private fun loadAccountFragment() {
        if (selectedTabPosition != 11) {
            imgSearch.visibility = View.VISIBLE
            txtHead.text = resources.getString(R.string.account)
            txtHead.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
            ivShare.visibility = View.GONE
            ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
            ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
            relWishlistImage.visibility = View.VISIBLE
            view.visibility = View.GONE
            viewStart.visibility = View.GONE
            if (!boolToggle) {
                resetDrawerIcon()
            }

            if (!isAccountLoaded) {
                fm.beginTransaction().add(R.id.content_frame, accountFragment, "11")
                    .show(accountFragment)
                    .commit()
                isAccountLoaded = true
            } else {
                fm.beginTransaction().hide(activeFragment).show(accountFragment).commit()
            }

            activeFragment = AccountFragment()
            selectedTabPosition = 11
            fm.beginTransaction()
                .hide(homeFragment).remove(cartFragment)
                .hide(brandsFragment).hide(faqDetailsFragment)
                .hide(celebrityFragment).hide(tvFragment).hide(contactUsFragment)
                .hide(categoriesFragment).remove(wishListFragment)
                .hide(faqFragment).commit()
            activeFragment = accountFragment
        }

    }

    private fun loadFaqFragment() {
        toggle?.isDrawerIndicatorEnabled = false
        boolToggle = false
        imgSearch.visibility = View.GONE
        ivShare.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        view.visibility = View.VISIBLE
        relWishlistImage.visibility = GONE
        txtHead.text = resources.getString(R.string.faqs_title)
        txtHead.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        viewStart.visibility = View.GONE
//        txtAppName.visibility = View.GONE
        if (!isFaqLoaded) {
            fm.beginTransaction().add(R.id.content_frame, faqFragment, "7")
                .show(faqFragment)
                .commit()
            isFaqLoaded = true
        } else {
            fm.beginTransaction().hide(activeFragment).show(faqFragment).commit()
        }
        activeFragment = FaqFragment()
        selectedTabPosition = 7
        fm.beginTransaction().hide(homeFragment).remove(cartFragment)
            .hide(brandsFragment).hide(faqDetailsFragment)
            .hide(celebrityFragment).hide(tvFragment).hide(contactUsFragment)
            .hide(categoriesFragment).remove(wishListFragment).hide(accountFragment).commit()
        activeFragment = faqFragment
        isCartLoaded = false
    }

    fun loadFaqDetailsFragment(faqDataModel: FaqDataModel) {
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
        imgSearch.visibility = View.GONE
        ivShare.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        view.visibility = View.VISIBLE
        txtHead.text = resources.getString(R.string.faqs_title)
        txtHead.visibility = View.VISIBLE
        viewStart.visibility = View.GONE
        ivLogo.visibility = View.GONE
        val bundle: Bundle = Bundle()
        bundle.putSerializable("faqModel", faqDataModel)
        faqDetailsFragment.arguments = bundle
        if (!isFaqDetailsLoaded) {
            fm.beginTransaction().add(R.id.content_frame, faqDetailsFragment, "7")
                .show(faqDetailsFragment)
                .commit()
            isFaqDetailsLoaded = true
        } else {
            fm.beginTransaction().hide(activeFragment).show(faqDetailsFragment).commit()
        }
        activeFragment = FaqDetailsFragment()
        selectedTabPosition = 7
        fm.beginTransaction().hide(homeFragment).remove(cartFragment)
            .hide(brandsFragment).hide(faqFragment)
            .hide(celebrityFragment).hide(tvFragment).hide(contactUsFragment)
            .hide(categoriesFragment).remove(wishListFragment).hide(accountFragment).commit()

        activeFragment = faqDetailsFragment
        isCartLoaded = false
    }

    /////show language alert
    private fun showLanguageAlert() {
        try {
            Global.showLanguageAlert(
                this,
                "",
                resources.getString(R.string.alert_language),
                resources.getString(R.string.yes_lang),
                resources.getString(R.string.no_lang),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        AppController.instance.changeLanguage()
                        val intent = Intent(this@NavigationActivity, NavigationActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }

                    override fun onNoClick() {
                    }

                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //////////
    @SuppressLint("CheckResult")
    private fun getRootParameters() {
        if (NetworkUtil.getConnectivityStatus(this@NavigationActivity) != 0) {
            //loading
            Global.apiService.getRootCategories(
                WebServices.RootCategoriesWs + Global.getLanguage(this@NavigationActivity) + "&store=" + Global.getStringFromSharedPref(
                    this@NavigationActivity,
                    Constants.PREFS_STORE_CODE
                )
                        + "&user_id=" + Global.getStringFromSharedPref(
                    this@NavigationActivity,
                    Constants.PREFS_USER_ID
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        }
    }

    ///handling success response for home
    private fun handleResponse(rootCategories: RootCategoriesModel?) {
        if (rootCategories?.status == 200) {
            setData(rootCategories.data)
        } else {
            rootCategories?.message?.let { Global.showSnackbar(this@NavigationActivity, it) }
        }

    }

    private fun handleError(error: Throwable) {
    }

    private fun setData(data: List<RootCategoriesData?>?) {

        Global.setCategories(this, data as ArrayList<RootCategoriesData?>)

    }

    fun updateCounts() {
        if (Global.isUserLoggedIn(this)) {

            // txtWishlistCount.visibility = View.GONE
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

    //Will show fragment on click of view all from home page
    fun showFragment(position: Int, categoryId: Int) {
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bottom_navigation.visibility = View.GONE
        boolToggle = false
        displayView(position, categoryId)
    }
////

    fun showViewAll(position: Int, categoryId: Int) {
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        bottom_navigation.visibility = View.GONE
        ivBackArrow.visibility = VISIBLE
        ivDrawer.visibility = View.GONE
        boolToggle = true
        isViewAll = true
        displayView(position, categoryId)
    }

    ///Resetting Drawer Menu button
    private fun resetDrawerIcon() {

        toggle?.isDrawerIndicatorEnabled = true
        bottom_navigation.visibility = View.VISIBLE
        ivBackArrow.visibility = GONE
        //ivDrawer.visibility = View.VISIBLE
        boolToggle = true
        isViewAll = false
    }

    ////////////
    fun clickHomeTab() {
        resetDrawerIcon()
        val view = bottom_navigation.findViewById<BottomNavigationItemView>(R.id.navigation_home)
        view.performClick()
    }

    fun clickCartTab() {
        resetDrawerIcon()
        val view = bottom_navigation.findViewById<BottomNavigationItemView>(R.id.navigation_cart)
        view.performClick()
    }

    fun clickCelebrityTab() {
        resetDrawerIcon()
        val view =
            bottom_navigation.findViewById<BottomNavigationItemView>(R.id.navigation_stars)
        view.performClick()
    }

    fun clickCategoryTab() {
        resetDrawerIcon()
        val view =
            bottom_navigation.findViewById<BottomNavigationItemView>(R.id.navigation_avenue)
        view.performClick()
    }

    fun clickBrandTab() {
        resetDrawerIcon()
        val view = bottom_navigation.findViewById<BottomNavigationItemView>(R.id.navigation_tv)
        view.performClick()
    }

    override fun onBackPressed() {

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            if (selectedTabPosition == 0 && activeFragment == homeFragment) {
                val poppedBack = (homeFragment as HomeFragment).popBack()
                if (poppedBack) {
                    displayHomeHeader()
                } else {
                    if (backPressed + 2000 > System.currentTimeMillis()) {
                        super.onBackPressed()
                        finish()

                    } else {
                        if (backPressed + 2000 > System.currentTimeMillis()) {
                            super.onBackPressed()
                            finish()

                        } else {
                            Global.showSnackbar(
                                this@NavigationActivity,
                                resources.getString(R.string.press_back_once_again_to_close_app)
                            )
                            backPressed = System.currentTimeMillis()
                        }
                    }
                }


            } else if (selectedTabPosition == 1) {

                val poppedBack = (brandsFragment as com.app.blockaat.brands.BrandFragment).popBack()
                if (poppedBack) {
                    displayBrandHeader()
                } else {
                    clickHomeTab()
                }

            } else if (selectedTabPosition == 2) {
                val poppedBack = (celebrityFragment as CelebrityFragment).popBack()
                if (poppedBack) {
                    displayCelebrityHeader()
                } else {
                    clickHomeTab()
                }

            } else if (selectedTabPosition == 3) {


                //category
                val poppedBack = (categoriesFragment as CategoryFragment).popBack()
                if (poppedBack) {
                    displayCategoryHeader()
                } else {
                    clickHomeTab()
                }


            } else if (isFaqDetailsLoaded && activeFragment == faqDetailsFragment) {
                loadFaqFragment()
            } else if (selectedTabPosition == 11 && isAccountLoaded) {
                clickHomeTab()
            } else if (selectedTabPosition == 5 && isTvLoaded) {
                clickHomeTab()
            } else {
                clickHomeTab()
            }

        }
    }

    inner class NavigationAapter(
        val menuList: ArrayList<Navigationmodel>,
        private val context: Context?,
        private val onStoreUpdateClicked: UpdateStoreItemEvent
    ) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private var LIST = 1
        private var STORE = 2

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            return when (viewType) {
                LIST -> {
                    val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_drawer, parent, false)
                    MyViewHolder(v)
                }
                else -> {
                    val v = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_store_list, parent, false)
                    StoreListViewHolder(v)
                }
            }

        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            when (getItemViewType(position)) {
                LIST -> {
                    val userViewHolder = holder as MyViewHolder
                    /*if (position == 0) {
                        holder.itemView.linDividerTop.visibility = View.VISIBLE
                    } else {
                        holder.itemView.linDividerTop.visibility = View.GONE
                    }*/
                    holder.itemView.txtDrawerTitle.text = menuList[position].name
                    menuList[position].thumbnail?.let { holder.itemView.imgItem.setImageResource(it) }
                    holder.itemView.imgItem.setColorFilter(resources.getColor(R.color.white))

                    if (!Global.isEnglishLanguage(this@NavigationActivity)) {
                        holder.itemView.ivDetail.rotation = 180f
                    }

                    if (position == 4) {
                        /*   holder.itemView.txtDrawerTitle.typeface = Global.fontNameReverse*/
                        holder.itemView.txtDrawerTitle.typeface = Global.fontNameReverse
                        holder.itemView.txtDrawerTitle.typeface = Global.fontMedium
                    } else {
                        /*   holder.itemView.txtDrawerTitle.typeface = Global.fontRegular*/
                        holder.itemView.txtDrawerTitle.typeface = Global.fontMedium
                    }

                    /* if (position == menuList.size - 1) {
                         holder.itemView.linDividerMenu.visibility = View.GONE
                     } else {
                         holder.itemView.linDividerMenu.visibility = View.VISIBLE
                     }

                     if (menuList[position].isArrowVisible) {
                         holder.itemView.ivDetail.visibility = View.VISIBLE
                     } else {
                         holder.itemView.ivDetail.visibility = View.GONE
                     }*/

                    holder.itemView.setOnClickListener() {
                        println("navigation item" + position)

                        when (position) {
                            //home
                            0 -> {
                                displayView(0)
                                bottom_navigation.selectedItemId = R.id.navigation_home
                                //
                            }
                            //avenues
                            1 -> {
                                displayView(3)
                                bottom_navigation.selectedItemId = R.id.navigation_avenue
                            }

                            //Brands
                            2 -> {
                                displayView(1)
                            }
                            /*//stars
                            3 -> {
                                displayView(2)
                                bottom_navigation.selectedItemId = R.id.navigation_stars
                            }

                            //my account
                            4 -> {
                                loadAccountFragment()
                            }*/


                            //my account
                            3 -> {
                                loadAccountFragment()
                            }

                            4 -> {
                                showLanguageAlert()
                            }

                            //My Orders
                            5 -> {
                                if (Global.isUserLoggedIn(this@NavigationActivity)) {
                                    val i = Intent(
                                        this@NavigationActivity,
                                        OrderListingActivity::class.java
                                    )
                                    startActivity(i)
                                } else {
                                    val i =
                                        Intent(this@NavigationActivity, LoginActivity::class.java)
                                    startActivityForResult(i, 1)
                                }
                            }
                            //My Wishlist
                            6 -> {

                                if (Global.isUserLoggedIn(this@NavigationActivity)) {
                                    val i = Intent(
                                        this@NavigationActivity,
                                        WishlistActivity::class.java
                                    )
                                    startActivity(i)
                                } else {
                                    val i =
                                        Intent(this@NavigationActivity, LoginActivity::class.java)
                                    startActivityForResult(i, 1)
                                }
                            }
                            //change language
                            7 -> {
                                showLanguageAlert()
                            }

                            //Rate US
                            8 -> {
                                showRateUs()
                            }
/*
                            //brands
                            3-> {
                                displayView(1)
                                bottom_navigation.selectedItemId = R.id.navigation_tv
                            }

                            //tv products
                            5 -> {

                                  }
                            //FAQs
                            7 -> {
                                loadFaqFragment()
                            }

                            //terms and conditions
                            8 -> {
                                val intent = Intent(this@NavigationActivity, CmsActivity::class.java)
                                intent.putExtra("id", "2")
                                intent.putExtra("header", resources.getString(R.string.tnc_caps))
                                startActivity(intent)
                            }

                            //chat
                            9 -> {
                                val i =
                                    Intent(this@NavigationActivity, LiveChatActivity::class.java)
                                startActivity(i)
                            }

                            //change country
                            10 -> {
                                startActivity(
                                    Intent(
                                        this@NavigationActivity,
                                        ChangeStoresActivity::class.java
                                    )
                                )

                            }*/
                        }
                        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
                        drawer.closeDrawer(GravityCompat.START)

                    }
                }
                else -> {
                    val adapterFilterStore = StoreAdapter(
                        context as Activity,
                        menuList[position].arrayList,
                        onStoreUpdateClicked
                    )
                    holder.itemView.rvStore.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    holder.itemView.rvStore.adapter = adapterFilterStore
                    adapterFilterStore.notifyDataSetChanged()
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            if (!menuList[position].arrayList.isNullOrEmpty()) {
                return STORE
            }
            return LIST
        }

        override fun getItemCount(): Int {
            return menuList.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
        inner class StoreListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    }

    private fun showRateUs() {
        val dialog = Dialog(this@NavigationActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.alert_rate_us)
        dialog.show()

        dialog.txtRateUsTitle.typeface = Global.fontBold
        dialog.txtRateUsMsg.typeface = Global.fontRegular
        dialog.txtRateItNow.typeface = Global.fontBtn
        dialog.txtRemindMe.typeface = Global.fontBtn
        dialog.txtCancel.typeface = Global.fontBtn

        dialog.txtRateItNow.setOnClickListener {
            dialog.dismiss()
            val uri: Uri = Uri.parse("market://details?id=$packageName")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(
                Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            )
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }

        }

        dialog.txtRemindMe.setOnClickListener {
            dialog.dismiss()
        }

        dialog.txtCancel.setOnClickListener {
            dialog.dismiss()
        }
    }


    private val onStoreUpdateClicked = object : UpdateStoreItemEvent {
        @SuppressLint("StringFormatMatches")
        override fun onStoreUpdateClicked(
            position: Int,
            type: String,
            storeModel: StoreDataModel?
        ) {
            Global.strStoreCode = storeModel?.iso_code.toString()
            Global.strCurrencyCodeEn = storeModel?.currency_en.toString()
            Global.strCurrencyCodeAr = storeModel?.currency_ar.toString()
            Global.strCountryNameEn = storeModel?.name_en.toString()
            Global.strCountryNameAr = storeModel?.name_ar.toString()
            if (!Global.strStoreCode.isNullOrEmpty() && !Global.strCurrencyCodeEn.isNullOrEmpty() && !Global.strCurrencyCodeAr.isNullOrEmpty()) {

//                if (Global.isUserLoggedIn(this@NavigationActivity)) {
//                    showCurrencyDialog()
//                } else {
                val msg =
                    if (Global.isEnglishLanguage(this@NavigationActivity)) resources.getString(
                        R.string.set_location_alert,
                        storeModel?.name_en + " (" + storeModel?.currency_en + ")"
                    )
                    else resources.getString(
                        R.string.set_location_alert,
                        Global.strCountryNameAr + " (" + Global.strCurrencyCodeAr + ")"
                    )
                showLocationDialog(msg)
//                }
            }

        }

    }


    private fun showCurrencyDialog() {
        Global.showAlert(
            this,
            "",
            resources.getString(R.string.change_currency_alert_logged_out),
            resources.getString(R.string.yes),
            resources.getString(R.string.no),
            false,
            R.drawable.ic_alert,
            object : AlertDialogInterface {
                override fun onYesClick() {

                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_STORE_CODE,
                        Global.strStoreCode
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_CURRENCY_EN,
                        Global.strCurrencyCodeEn
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_CURRENCY_AR,
                        Global.strCurrencyCodeAr
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_COUNTRY_EN,
                        Global.strCountryNameEn
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_COUNTRY_AR,
                        Global.strCountryNameAr
                    )

                    val i = Intent(this@NavigationActivity, NavigationActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

                    productsDBHelper.deleteTable("table_cart")
                    //productsDBHelper.deleteTable("table_wishlist")

                    startActivity(i)
                    finish()
                }

                override fun onNoClick() {
                }

            }
        )
    }

    private fun showLocationDialog(msg: String) {
        Global.showAlert(
            this,
            "",
            msg,
            resources.getString(R.string.yes),
            resources.getString(R.string.no),
            false,
            R.drawable.ic_alert,
            object : AlertDialogInterface {
                override fun onYesClick() {
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_STORE_CODE,
                        Global.strStoreCode
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_CURRENCY_EN,
                        Global.strCurrencyCodeEn
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_CURRENCY_AR,
                        Global.strCurrencyCodeAr
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_COUNTRY_EN,
                        Global.strCountryNameEn
                    )
                    Global.saveStringInSharedPref(
                        this@NavigationActivity,
                        Constants.PREFS_COUNTRY_AR,
                        Global.strCountryNameAr
                    )
                    productsDBHelper.deleteTable("table_cart")

                    val i = Intent(this@NavigationActivity, NavigationActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    finish()
                }

                override fun onNoClick() {
                }

            }
        )
    }

    override fun onResume() {
        super.onResume()
        updateCartBadge()
        updateCounts()
        if (Global.getStringFromSharedPref(this, Constants.PREFS_isUSER_LOGGED_IN)
                .equals("yes", true)
        ) {
            txtSignIn.visibility = View.GONE
            txtUser.visibility = View.VISIBLE
            txtUserName.visibility = View.VISIBLE
            txtUserName.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_USER_FIRST_NAME) + " "
            Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME)
            tvLogout.visibility = View.VISIBLE
        } else {
            txtSignIn.visibility = View.VISIBLE
            txtUserName.visibility = View.GONE
            txtUser.visibility = View.GONE
            tvLogout.visibility = View.GONE
        }
        // EventBus.getDefault().register(this)

    }

    override fun onPostResume() {
        super.onPostResume()

    }

    fun userLoginUpdate(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            txtSignIn.visibility = View.GONE
            txtUser.visibility = View.VISIBLE
            txtUserName.visibility = View.VISIBLE
            txtUserName.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_USER_FIRST_NAME) + " "
            Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME)
            tvLogout.visibility = View.VISIBLE
        } else {
            txtSignIn.visibility = View.VISIBLE
            txtUser.visibility = View.GONE
            txtUserName.visibility = View.GONE
            tvLogout.visibility = View.GONE
        }
    }

    ////////HIDEKEYBOARD//
    fun hideKeyboard() {
        val view: View = currentFocus ?: View(this@NavigationActivity)
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration


            val locale = Locale(SharedPreferencesHelper.getString(this, "lang_new", "en"))
            Locale.setDefault(locale)
            config.setLocale(locale)
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            Global.showSnackbar(
                this@NavigationActivity,
                resources.getString(R.string.user_success_msg)
            )
            updateCounts()
        }
    }

    fun displayProductListInHome(header: String) {
        println("Header :: " + header)
        isProductListLoadedInHome = true
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        txtHead.text = Global.toCamelCase(header)
        strHomeProductListHeader = header
        txtHead.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        view.visibility = View.GONE
        viewStart.visibility = View.VISIBLE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
    }

    fun displayProductListInBrands(header: String) {
        isProductListLoadedInBrands = true
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        txtHead.text = Global.toCamelCase(header)
        strBrandProductListHeader = header
        txtHead.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        view.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
    }

    fun displayProductListInCategory(header: String) {
        isProductListLoadedInCategory = true
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        txtHead.text = Global.toCamelCase(header)
        strCategoryProductListHeader = header
        arrListCategoryTitle?.add(header)
        txtHead.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        view.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
    }

    fun displaySubCategoryInCategory(header: String) {
        isProductListLoadedInCategory = true
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        txtHead.text = Global.toCamelCase(header)
        strCategoryProductListHeader = header
        arrListCategoryTitle?.add(header)
        txtHead.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        view.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
    }

    fun displayProductListInCelebrity(header: String) {
        isProductListLoadedInCelebrity = true
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        viewStart.visibility = View.VISIBLE
        txtHead.text = Global.toCamelCase(header)
        strCelebrityProductListHeader = header
        txtHead.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        view.visibility = View.GONE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
    }


    fun displaySort(strSortBy: String, parentType: String) {
        this.parentType = parentType
        val parentView = layoutInflater.inflate(R.layout.layout_sort_bottomsheet, null)
        val bottomSheerDialog = BottomSheetDialog(this, R.style.sheetDialog)
        bottomSheerDialog.setContentView(parentView)
        bottomSheerDialog.setCanceledOnTouchOutside(true)
        bottomSheerDialog.setCancelable(true)
        val txtSortOptionTitle = parentView.findViewById(R.id.txtSortOptionTitle) as TextView
        val txtSortHighToLow = parentView.findViewById(R.id.txtSortHighToLow) as TextView
        val txtSortLowToHigh = parentView.findViewById(R.id.txtSortLowToHigh) as TextView
        val txtRecommended = parentView.findViewById(R.id.txtRecommended) as TextView
        val txtSortNewIn = parentView.findViewById(R.id.txtSortNewIn) as TextView
        val ivSortNewInTick = parentView.findViewById(R.id.ivSortNewInTick) as RadioButton
        val ivSortHighToLowTick = parentView.findViewById(R.id.ivSortHighToLowTick) as RadioButton
        val ivSortLowToHighTick = parentView.findViewById(R.id.ivSortLowToHighTick) as RadioButton
        val ivSortRecommendedTick =
            parentView.findViewById(R.id.ivSortRecommendedTick) as RadioButton
        val relSortHighToLow = parentView.findViewById(R.id.relSortHighToLow) as RelativeLayout
        val relSortLowToHigh = parentView.findViewById(R.id.relSortLowToHigh) as RelativeLayout
        val relSortLatest = parentView.findViewById(R.id.relSortLatest) as RelativeLayout
        val relSortNewIn = parentView.findViewById(R.id.relSortNewIn) as RelativeLayout
        txtSortOptionTitle.typeface = Global.fontBold

        when (strSortBy) {
            "1" -> {
                txtSortHighToLow.typeface = Global.fontRegular
                txtSortLowToHigh.typeface = Global.fontRegular
                txtRecommended.typeface = Global.fontSemiBold
                txtSortNewIn.typeface = Global.fontRegular
                ivSortNewInTick.isChecked = false
                ivSortHighToLowTick.isChecked = false
                ivSortLowToHighTick.isChecked = false
                ivSortRecommendedTick.isChecked = true
            }

            "2" -> {
                txtSortHighToLow.typeface = Global.fontRegular
                txtSortLowToHigh.typeface = Global.fontRegular
                txtSortNewIn.typeface = Global.fontSemiBold
                txtRecommended.typeface = Global.fontRegular
                ivSortNewInTick.isChecked = true
                ivSortHighToLowTick.isChecked = false
                ivSortLowToHighTick.isChecked = false
                ivSortRecommendedTick.isChecked = false
            }

            "3" -> {
                txtSortHighToLow.typeface = Global.fontSemiBold
                txtSortLowToHigh.typeface = Global.fontRegular
                txtRecommended.typeface = Global.fontRegular
                txtSortNewIn.typeface = Global.fontRegular
                ivSortNewInTick.isChecked = false
                ivSortHighToLowTick.isChecked = true
                ivSortLowToHighTick.isChecked = false
                ivSortRecommendedTick.isChecked = false

            }

            "4" -> {
                txtSortHighToLow.typeface = Global.fontRegular
                txtSortLowToHigh.typeface = Global.fontSemiBold
                txtRecommended.typeface = Global.fontRegular
                txtSortNewIn.typeface = Global.fontRegular
                ivSortNewInTick.isChecked = false
                ivSortHighToLowTick.isChecked = false
                ivSortLowToHighTick.isChecked = true
                ivSortRecommendedTick.isChecked = false

            }

        }
        relSortHighToLow.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontRegular
            txtRecommended.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = true
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("3", parentType))
            bottomSheerDialog.dismiss()
//            simpleViewSort.visibility = View.GONE
        }

        relSortLowToHigh.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = true
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("4", parentType))
            bottomSheerDialog.dismiss()
//            simpleViewSort.visibility = View.GONE
        }

        relSortLatest.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontRegular
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = true
            EventBus.getDefault().post(SortObject("1", parentType))
            bottomSheerDialog.dismiss()
//            simpleViewSort.visibility = View.GONE
        }

        relSortNewIn.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontRegular
            txtSortLowToHigh.typeface = Global.fontRegular
            txtSortNewIn.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontRegular
            ivSortNewInTick.isChecked = true
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("2", parentType))
            bottomSheerDialog.dismiss()

//            simpleViewSort.visibility = View.GONE
        }
        bottomSheerDialog.show()
/*
        if (simpleViewSort.visibility == View.VISIBLE) {

            simpleViewSort.visibility = View.GONE
            //viewTransparent.visibility = View.GONE
        } else {
            simpleViewSort.visibility = View.VISIBLE

            println("Sort visibility visible")
            // viewTransparent.visibility = View.VISIBLE
        }*/
    }

    fun setSortListener(onSortListener: OnSortClickListener) {

        ivSortRecommendedTick.isChecked = true
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ivSortRecommendedTick.backgroundTintList = generateColorStateList()
//        }

        relSortHighToLow.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = true
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("3", parentType))
            simpleViewSort.visibility = View.GONE
        }

        relSortLowToHigh.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = true
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("4", parentType))
            simpleViewSort.visibility = View.GONE
        }

        relSortLatest.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = false
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = true
            EventBus.getDefault().post(SortObject("1", parentType))
            simpleViewSort.visibility = View.GONE
        }

        relSortNewIn.setOnClickListener {
            txtSortHighToLow.typeface = Global.fontMedium
            txtSortLowToHigh.typeface = Global.fontMedium
            txtSortNewIn.typeface = Global.fontMedium
            txtRecommended.typeface = Global.fontMedium
            ivSortNewInTick.isChecked = true
            ivSortHighToLowTick.isChecked = false
            ivSortLowToHighTick.isChecked = false
            ivSortRecommendedTick.isChecked = false
            EventBus.getDefault().post(SortObject("2", parentType))

            simpleViewSort.visibility = View.GONE
        }
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

    fun displayFilter(
        arrListFilterData: ArrayList<ProductListingFilterModel?>?,
        parentType: String,
        fromStr: String
    ) {
        if (!arrListFilterData.isNullOrEmpty()) {
            this.parentType = parentType
            this.arrListFilterData?.clear()
            this.arrListFilterData?.addAll(arrListFilterData)

            val bundle: Bundle = Bundle()
            bundle.putSerializable("list", this.arrListFilterData)
            bundle.putSerializable("parentType", parentType)
            bundle.putString("isFrom", fromStr)
            val f = FilterDialogFragment()
            f.arguments = bundle
            f.show(supportFragmentManager, "dialog")
        }
    }


    fun initializeFilter(
        arrListFilterData: ArrayList<ProductListingFilterModel?>?,
        parentType: String
    ) {
        if (!arrListFilterData.isNullOrEmpty()) {
            this.parentType = parentType
            this.arrListFilterData?.clear()
            this.arrListFilterData?.addAll(arrListFilterData)
            println("Current data nav: " + this.arrListFilterData)

        }
    }


    class FilterPagerAdapter(
        manager: FragmentManager,
        private val arrListFilters: ArrayList<ProductListingFilterModel?>
    ) : FragmentStatePagerAdapter(manager) {
        private var arrListTitle: java.util.ArrayList<String>? = null
        private var arrListFagment: ArrayList<FilterListFragment>? = null

        init {
            arrListTitle = java.util.ArrayList<String>()
            arrListFagment = java.util.ArrayList()
            for (filter in arrListFilters) {

                val strName = Global.toCamelCase(filter?.filter_name.toString())


                arrListTitle?.add(strName)
                arrListFagment?.add(
                    FilterListFragment.newInstance(
                        filter?.filter_values as ArrayList<ProductListingFilterValueModel>,
                        filter.filter_type.toString(),
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

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    private fun setNavigation() {
        if (intent.hasExtra("introModel")) {
            introModel = intent.extras?.get("introModel") as IntroResponseModelItem
            if (introModel?.link_type.toString().isNotEmpty() && introModel?.link_id.toString()
                    .isNotEmpty()
            ) {
                if (introModel?.link_type.toString().toLowerCase() == "p") {
                    val intent = Intent(this, ProductDetailsActivity::class.java)
                    intent.putExtra("strProductID", introModel?.link_id.toString())
                    intent.putExtra("strProductName", introModel?.type_name.toString())
                    startActivity(intent)
                } else if (introModel?.link_type.toString()
                        .toLowerCase() == "t" || introModel?.link_type.toString()
                        .toLowerCase() == "v"
                ) {
                    val intent = Intent(this, TvProductActivity::class.java)
                    intent.putExtra("tvID", introModel?.link_id.toString())
                    intent.putExtra("header_text", introModel?.type_name)
                    startActivity(intent)
                } else {
                    val homeLinkModel = HomeLinkModel(
                        link_id = introModel?.link_id ?: "",
                        link_type = introModel?.link_type ?: "",
                        type_name = introModel?.type_name ?: "",
                        url = introModel?.url ?: "",
                        categoryId = Global.getPreferenceCategory(this@NavigationActivity)
                    )
                    println("Home link navigation: " + homeLinkModel)

                    EventBus.getDefault().post(homeLinkModel)

                }
            }
        } else if (intent.hasExtra(Constants.PUSH_RECEIVE_EVENTS)) {
            //when coming from push notification
            pushNotification()
        }
    }

    private fun pushNotification() {
        try {
            var id = ""
            var title = ""

            val jsonObject =
                JSONObject(intent.getStringExtra(Constants.PUSH_RECEIVE_EVENTS).toString())
            println("HERE IS PUSHWOOSH DATA ::: " + jsonObject)

            if (jsonObject.has("target_id") && !jsonObject.getString("target_id").isNullOrEmpty()) {
                id = jsonObject.getString("target_id")

                if (jsonObject.has("title")) {
                    title = jsonObject.getString("title") ?: ""
                }
            }

            if (id.isNotEmpty()) {
                var target = ""
                if (jsonObject.has("target"))
                    target = jsonObject.getString("target").toString().toLowerCase()
                if (target.isNotEmpty()) {
                    if (target == "p") {
                        val intent = Intent(this, ProductDetailsActivity::class.java)
                        intent.putExtra("strProductID", id.toString())
                        intent.putExtra("strProductName", title.toString())
                        startActivity(intent)
                    } else if (target == "t") {
                        val intent = Intent(this, TvProductActivity::class.java)
                        intent.putExtra("tvID", id.toString())
                        intent.putExtra("header_text", title)
                        startActivity(intent)
                    } else if (target == "ca") {
                        clickCartTab()
                    } else {
                        val homeLinkModel = HomeLinkModel(
                            link_id = id ?: "",
                            link_type = target ?: "",
                            type_name = title ?: "",
                            url = "",
                            categoryId = Global.getPreferenceCategory(this@NavigationActivity)
                        )
                        EventBus.getDefault().post(homeLinkModel)

                    }
                }
            }

        } catch (e: Exception) {
            // Log.e("PUSH", "Error:" + e.localizedMessage.toString())
        }
    }

    private fun displayHomeHeader() {
        println("Display home ")
        val currentFragment = (activeFragment as HomeFragment).getFragment()
        println("Fragment in home ::" + currentFragment)

        if (currentFragment is ProductListFragment) {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            boolToggle = false
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            // imgUser.visibility = View.GONE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            viewStart.visibility = View.VISIBLE
            view.visibility = View.GONE
            ivLogo.visibility = View.GONE
            ivBackArrow.visibility = View.VISIBLE
            ivDrawer.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
        }

        if (currentFragment is HomeDataFragment) {
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            txtHead.visibility = View.GONE
            imgUser.visibility = View.VISIBLE
            txtHead.text = resources.getString(R.string.menu_home)
            view.visibility = View.GONE
            viewStart.visibility = View.GONE
            ivLogo.visibility = View.VISIBLE
            ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
            ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
            relWishlistImage.visibility = View.VISIBLE
            if (!boolToggle) {
                resetDrawerIcon()
            }
        }
    }

    private fun displayBrandHeader() {
        val currentFragment =
            (brandsFragment as com.app.blockaat.brands.BrandFragment).getFragment()

        if (currentFragment is ProductListFragment) {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            boolToggle = false
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
            imgUser.visibility = View.VISIBLE
            ivBackArrow.visibility = View.VISIBLE
            ivDrawer.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
            viewStart.visibility = View.VISIBLE
            view.visibility = View.GONE

        }

        if (currentFragment is BrandDataFragment) {
            imgSearch.visibility = View.VISIBLE
            txtHead.text = resources.getString(R.string.menu_brand)
            txtHead.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
            imgUser.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
            ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
            relWishlistImage.visibility = View.VISIBLE
            view.visibility = View.GONE
            viewStart.visibility = View.GONE
            if (!boolToggle) {
                resetDrawerIcon()
            }
        }
    }


    private fun displayCelebrityHeader() {
        val currentFragment = (celebrityFragment as CelebrityFragment).getFragment()

        if (currentFragment is ProductListFragment) {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            boolToggle = false
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
            //  imgUser.visibility = View.GONE
            ivBackArrow.visibility = View.VISIBLE
            ivDrawer.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
            viewStart.visibility = View.VISIBLE
            view.visibility = View.GONE

        }

        if (currentFragment is CelebrityDataFragment) {

            imgSearch.visibility = View.VISIBLE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            ivLogo.visibility = View.GONE
            //  imgUser.visibility = View.GONE
            ivShare.visibility = View.GONE
            ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
            ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
            relWishlistImage.visibility = View.VISIBLE
            view.visibility = View.GONE
            viewStart.visibility = View.GONE
            if (!boolToggle) {
                resetDrawerIcon()
            }
        }
    }

    private fun displayCategoryHeader() {
        val currentFragment = (categoriesFragment as CategoryFragment).getFragment()

        if (currentFragment is ProductListFragment) {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            boolToggle = false
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            //  imgUser.visibility = View.GONE
            ivLogo.visibility = View.GONE
            ivBackArrow.visibility = View.VISIBLE
            ivDrawer.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
            viewStart.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        if (currentFragment is SubCategoryDataFragment) {
            toggle?.isDrawerIndicatorEnabled = false
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            boolToggle = false
            imgSearch.visibility = View.VISIBLE
            ivShare.visibility = View.GONE
            txtHead.text = Global.toCamelCase(currentFragment.getHeader())
            txtHead.visibility = View.VISIBLE
            // imgUser.visibility = View.GONE
            ivLogo.visibility = View.GONE
            ivBackArrow.visibility = View.VISIBLE
            ivDrawer.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
            viewStart.visibility = View.VISIBLE
            view.visibility = View.GONE
        }

        if (currentFragment is CategoryDataFragment) {

            imgSearch.visibility = View.VISIBLE
            txtHead.text = resources.getString(R.string.bottom_menu_avenue)
            txtHead.visibility = View.VISIBLE
            //  imgUser.visibility = View.GONE
            ivLogo.visibility = View.GONE
            ivBackArrow.visibility = if (isViewAll) VISIBLE else GONE
            ivDrawer.visibility = if (isViewAll) GONE else VISIBLE
            ivShare.visibility = View.GONE
            view.visibility = View.GONE
            viewStart.visibility = View.GONE
            relWishlistImage.visibility = View.VISIBLE
            if (!boolToggle) {
                resetDrawerIcon()
            }
        }
    }

    //Handle Deeplink responce
    fun displayProductList(target: String, linkId: String, name: String) {
        toggle?.isDrawerIndicatorEnabled = false
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boolToggle = false
        imgSearch.visibility = View.VISIBLE
        ivShare.visibility = View.GONE
        txtHead.visibility = View.VISIBLE
        ivLogo.visibility = View.GONE
        imgUser.visibility = View.VISIBLE
        ivBackArrow.visibility = View.VISIBLE
        ivDrawer.visibility = View.GONE
        relWishlistImage.visibility = View.VISIBLE
        viewStart.visibility = View.VISIBLE
        view.visibility = View.GONE

        txtHead.text = name
        println("here i am - Name $name")
        println("here i am - Target $target")
        println("here i am - LinkId $linkId")

        val bundle = Bundle()
        when (target.toLowerCase()) {
            "cl" -> {
                bundle.putString("influencerID", linkId.toString())
                bundle.putString("categoryID", "")
            }
            "br" -> {
                bundle.putString("brandID", linkId.toString())
                bundle.putString("categoryID", "")
                bundle.putString("isFromBrands", "yes")
            }
            "n" -> {
                bundle.putString("categoryID", linkId.toString())
                bundle.putString("categoryID", "")
                bundle.putString("isFeatured", "1")
            }
            "c" -> {
                bundle.putString("categoryID", linkId.toString())
            }
        }
        bundle.putString("header_text", name)
        bundle.putString("parent_type", "home")
        productListFragment = ProductListFragment()
        productListFragment.arguments = bundle


        if (!isProductListLoaded) {
            fm.beginTransaction().add(R.id.content_frame, productListFragment, "12")
                .show(productListFragment)
                .commit()
            isProductListLoaded = true
        } else {
            fm.beginTransaction().hide(activeFragment).show(productListFragment).commit()
        }
        activeFragment = ProductListFragment()
        selectedTabPosition = 12
        fm.beginTransaction().hide(homeFragment).remove(cartFragment)
            .hide(brandsFragment).hide(faqDetailsFragment).hide(faqFragment)
            .hide(celebrityFragment).hide(tvFragment).hide(contactUsFragment)
            .hide(categoriesFragment).remove(wishListFragment).commit()
        activeFragment = productListFragment

    }




    override fun onPause() {
        super.onPause()
        // EventBus.getDefault().unregister(this)
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
}
