package com.app.blockaat

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.helper.*
import com.app.blockaat.home.model.RootCategoriesData
import com.app.blockaat.navigation.NavigationActivity
import com.facebook.applinks.AppLinkData
import com.google.android.gms.analytics.HitBuilders.ScreenViewBuilder
import com.google.android.gms.analytics.Tracker
import com.pushwoosh.Pushwoosh
import io.branch.referral.Branch
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class SplashActivity : BaseActivity() {

    private var arrListCategory: ArrayList<RootCategoriesData?>? = null
    private var dialog: CustomProgressBar? = null
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CustomEvents.screenViewed(this,getString(R.string.splash))

        window.setFormat(PixelFormat.TRANSLUCENT)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)
        if (supportActionBar != null) {
            supportActionBar?.hide()
        }
//        startService(Intent(this@SplashActivity, GetStoreService::class.java))

        ////////////////////////Store device density//////////////////////
        setUpDeviceWidth()
        if (Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE).isNullOrEmpty()) {
            AppController.instance.arabicLanguage()
        }

        printKeyHash()
        dialog = CustomProgressBar(this)
        setOnClickListener()
        setUpStore()
        /*   setFonts()*/
        init()
        Constants.DEVICE_TOKEN = Pushwoosh.getInstance().pushToken ?: ""
        println("T : " + Constants.DEVICE_TOKEN)
    }


    private fun setUpDeviceWidth() {
        val displaymetrics: DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val width: Double = displaymetrics.widthPixels.toDouble()
/*
        Log.d(
            "widthPixel",
            width.toString() + "  " + displaymetrics.densityDpi.toString() + "  " + displaymetrics.heightPixels.toString()
        )
*/
        val DeviceMultiplier: Double = (width / 320)
        SharedPreferencesHelper.writeString(
            this,
            Constants.PREFS_DEVIDE_MULTIPLIER,
            DeviceMultiplier.toString()
        )
        Constants.DEVICE_DENSITY = (Global.getDeviceWidthInDouble(this) / 320)
    }

    private fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
               // Log.i("HASHHH", "printHashKey() Hash Key: $hashKey")
                println("HHHH" + hashKey)

            }
        } catch (e: NoSuchAlgorithmException) {
           // Log.e("HASHHH", "printHashKey()", e)
        } catch (e: Exception) {
           // Log.e("HASHHH", "printHashKey()", e)
        }
    }

    /* private fun setFonts() {
         txtWomenEn.typeface = Global.fontMedium
         txtMenEn.typeface = Global.fontMedium
         txtWomenAr.typeface = Global.fontMedium
         txtMenAr.typeface = Global.fontMedium
     }*/

    private fun setUpStore() {
        if (Global.getStringFromSharedPref(this@SplashActivity, Constants.PREFS_STORE_CODE).isNullOrEmpty()) {
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_COUNTRY_ID, "13")
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_STORE_CODE, "KW")
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_CURRENCY_EN, "KD")
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_CURRENCY_AR, "د.ك")
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_COUNTRY_EN, "Kuwait")
            Global.saveStringInSharedPref(this@SplashActivity, Constants.PREFS_COUNTRY_AR, "الكويت")
            Global.saveStringInSharedPref(
                this@SplashActivity,
                Constants.PREFS_COUNTRY_FLAG,
                WebServices.IMAGE_DOMAIN + "uploads/16327605656151f2f5affe39.89544521.png"
            )
            Global.saveStringInSharedPref(
                this@SplashActivity,
                Constants.PREFS_CURRENCY_FLAG,
                WebServices.IMAGE_DOMAIN + "uploads/16327605656151f2f5affe39.89544521.png"
            )
            Global.saveStringInSharedPref(
                this@SplashActivity,
                Constants.PREFS_USER_PHONE_CODE,
                "+965"
            )

            Global.strCountryNameEn = "Kuwait"
            Global.strCountryNameAr = "الكويت"
            Global.strCurrencyCodeAr = "د.ك"
            Global.strCurrencyCodeEn = "KD"
            Global.strStoreCode = "KW"
        }
    }

    fun init() {
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }

    private fun setOnClickListener() {

    }

    override fun onStart() {
        super.onStart()

        //handle facebook deferred link
        AppLinkData.fetchDeferredAppLinkData(this,
            object : AppLinkData.CompletionHandler {
                override fun onDeferredAppLinkDataFetched(appLinkData: AppLinkData?) {
                    // Process app link data
                    Log.e("SPLASH", "universal Link:" + appLinkData?.targetUri)
                    if (appLinkData?.targetUri != null) {
                        val uri = Uri.parse(appLinkData?.targetUri.toString())
                        val targetId: String = uri.getQueryParameter("target_id").toString()
                        val target: String = uri.getQueryParameter("target").toString()
                        val name_en: String = uri.getQueryParameter("name_en").toString()
                        val name_ar: String = uri.getQueryParameter("name_ar").toString()
                        Global.saveStringInSharedPref(
                            this@SplashActivity,
                            Constants.PREFS_isFROM_DEFERRED,
                            "yes"
                        )
                        Global.saveStringInSharedPref(
                            this@SplashActivity,
                            Constants.PREFS_DEFERREDPLINK_TARGET,
                            target
                        )
                        Global.saveStringInSharedPref(
                            this@SplashActivity,
                            Constants.PREFS_DEFERREDLINK_ID,
                            targetId
                        )
                        Global.saveStringInSharedPref(
                            this@SplashActivity,
                            Constants.PREFS_DEFERREDLINK_NAME,
                            if (Global.isEnglishLanguage(this@SplashActivity)) name_en else name_ar
                                ?: ""
                        )

                    }
                }
            }
        )


        // Branch init
        Branch.getInstance().initSession({ referringParams, error ->
            //  println("Here i am branch data  $referringParams")
            if (error == null) {
                if (referringParams != null && referringParams.has("target_id") && !referringParams.getString(
                        "target_id"
                    ).isNullOrEmpty()
                ) {
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_isFROM_DEEPLINK,
                        "yes"
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_TARGET,
                        referringParams.optString("target", "")
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_ID,
                        referringParams.optString("target_id", "")
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_NAME,
                        referringParams.optString("title", "")
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_CREATOR_ID,
                        referringParams.optString("creator_id", "")
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEFERREDLINK_NAME,
                        if (Global.isEnglishLanguage(this@SplashActivity)) referringParams.optString(
                            "name",
                            ""
                        ) else
                            referringParams.optString("name_ar", "")
                                ?: ""
                    )

                } else {
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_isFROM_DEEPLINK,
                        ""
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_TARGET,
                        ""
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_ID,
                        ""
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_CREATOR_ID,
                        ""
                    )
                    Global.saveStringInSharedPref(
                        this@SplashActivity,
                        Constants.PREFS_DEEPLINK_NAME,
                        ""
                    )
                }
            }

        }, this.intent.data, this)
    }

    /* private fun setCategory(text: CharSequence?) {
         if (!arrListCategory.isNullOrEmpty()) {
             for (category in arrListCategory!!) {
                 if (text.toString().toLowerCase() == category?.name?.toLowerCase()) {
                     Global.saveStringInSharedPref(
                         this@SplashActivity,
                         Constants.PREFS_CATEGORY,
                         category.id.toString()
                     )
                     break
                 }
             }
         }
 //        getIntroImages()

     }*/

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
    }

//    private fun getIntroImages() {
//        if (NetworkUtil.getConnectivityStatus(this) != 0) {
//            showProgressDialog(this)
//            disposable = Global.apiService.getLandingImages(
//                WebServices.LandingPageWs + Global.getLanguage(this) + "&category_id=" + Global.getPreferenceCategory(
//                    this@SplashActivity
//                )
//            )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                    { result ->
//                        hideProgressDialog()
//                        if (result != null) {
//                            if (result.status == 200 && result.data != null) {
//                                val intent =
//                                    Intent(this@SplashActivity, IntroSliderActivity::class.java)
//                                intent.putExtra("introModel", result.data)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//                    },
//                    { error ->
//                        //println("ERROR - landing Ws :   " + error.localizedMessage)
//                        hideProgressDialog()
//
//                    })
//
//        }
//    }
/*

    private fun showProgressDialog(mActivity: AppCompatActivity) {
        dialog?.showDialog()

    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()

    }
*/

    /* @SuppressLint("CheckResult")
     private fun getRootParameters() {
         if (NetworkUtil.getConnectivityStatus(this@SplashActivity) != 0) {
             //loading
             Global.apiService.getRootCategories(
                 WebServices.RootCategoriesWs + Global.getLanguage(this@SplashActivity) + "&store=" + Global.getStringFromSharedPref(
                     this@SplashActivity,
                     Constants.PREFS_STORE_CODE
                 )
                         + "&user_id=" + Global.getStringFromSharedPref(
                     this@SplashActivity,
                     Constants.PREFS_USER_ID
                 )
             )
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(this::handleResponse, this::handleError)
         }
     }*/

    ///handling success response for home
    /* private fun handleResponse(rootCategories: RootCategoriesModel?) {
         if (rootCategories?.status == 200) {
             setData(rootCategories.data)
         } else {
             rootCategories?.message?.let { Global.showSnackbar(this@SplashActivity, it) }
         }

     }*/

    /*  private fun handleError(error: Throwable) {
      }

      private fun setData(data: List<RootCategoriesData?>?) {

          *//*  rvCategory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)*//*

        data?.let { arrListCategory?.addAll(it) }
        Global.setCategories(this, arrListCategory)
        val rootAdapter = RootCategoryAdapter(
            this@SplashActivity,
            arrListCategory, rootCategoryInterface
        )
        *//* rvCategory.adapter = rootAdapter*//*
        rootAdapter.notifyDataSetChanged()
        *//*rvCategory.visibility = View.VISIBLE*//*

    }
*/

    /*   private val rootCategoryInterface = object :
           OnRootCategoryClickListener {
           override fun onRootCategoryClicked(position: Int) {

               Global.saveStringInSharedPref(
                   this@SplashActivity,
                   Constants.PREFS_CATEGORY,
                   arrListCategory?.get(position)?.id.toString()
               )

   //            getIntroImages()
           }

       }
   */

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null) {
            disposable?.dispose()
        }
    }
}