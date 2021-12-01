package com.app.blockaat.helper

import RestClient
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.Patterns
import android.util.TypedValue
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.app.blockaat.R
import com.app.blockaat.apimanager.WebClient
import com.app.blockaat.cart.CartActivity
import com.app.blockaat.cart.model.AddCartRequestApi
import com.app.blockaat.cart.model.AddCartRequestModel
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.home.model.RootCategoriesData
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.productdetails.model.NotifyMeRequestModel
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.wishlist.modelclass.WishListDataModel
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.custom_alert.*
import kotlinx.android.synthetic.main.custom_notify.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Global {

    val INSTABUG_KEY_DEBUG: String = "d51b40cd51a0fce92da59e0014c70f29" /*old: 81d324566c0da15c883289f6e937744f*/ /*old 03/08: b908b15a18c2065d1bc09fb032bb8c71*/ /*old 12/7: 7a8b1696621bce116141e083f64011bf*/
    val INSTAGRAM_LINK = "https://instagram.com/blockatapp"
    val FACEBOOK_LINK = ""
    val YOUTUBE_LINK = "https://www.youtube.com/channel/UCMGW2OSb6gdDhKnMoynr6lg"
    val TWITTER_LINK = "https://twitter.com/blockatapp"
    val SOUND_CLOUD = "https://soundcloud.com/blockatapp"
    val LINKEDIN_LINK = "https://business.linkedin.com/marketing-solutions/linkedin-pages"
    val GOOGLE_PLUS_LINK = ""
    val SNAPCHAT_LINK = ""

    internal var fontThin: Typeface? = null
    internal var fontLight: Typeface? = null
    internal var fontItalics: Typeface? = null
    internal var fontBoldItalics: Typeface? = null
    internal var fontRegular: Typeface? = null
    internal var fontSemiBold: Typeface? = null
    internal var fontMedium: Typeface? = null
    internal var fontArMedium: Typeface? = null

    internal var fontNavBar: Typeface? = null
    internal var fontTabBar: Typeface? = null
    internal var fontExtraBold: Typeface? = null
    internal var fontBtn: Typeface? = null
    internal var fontHeader: Typeface? = null
    internal var fontHeaderBold: Typeface? = null
    internal var fontBold: Typeface? = null
    internal var fontPriceBold: Typeface? = null
    internal var fontPriceRegular: Typeface? = null
    internal var fontPriceMedium: Typeface? = null
    internal var fontTitle: Typeface? = null
    internal var fontTitleBold: Typeface? = null
    internal var fontTitleDarkBold: Typeface? = null
    internal var fontNameReverse: Typeface? = null
    internal var fontEngReg: Typeface? = null

    private var dialog: CustomProgressBar? = null
    var progressDialog: Dialog? = null
    internal var imgProduct: Bitmap? = null

    var strSupportMail = ""
    var strSupportPhone = ""
    var strFlag = ""
    var strStoreCode = ""
    var strCurrencyCodeEn = ""
    var strCurrencyCodeAr = ""
    var strCountryNameEn = ""
    var strCountryNameAr = ""
    var strSupportEmail: String = ""
    var strDeliveryAddressId = ""
    var arrListStore: ArrayList<StoreDataModel> = ArrayList()
    private val REQUEST_CALL_PHONE = 3
    private var firebaseAnalytics:FirebaseAnalytics? = null

    @JvmField
    var arrListFilter: ArrayList<ProductListingFilterModel>? = null

    //below function is required to initialize retrofit interface
    val apiService by lazy {
        RestClient.create()
    }

    val apiService2 by lazy {
        RestClient.create3()
    }

    val apiService1 by lazy {
        RestClient.create1()
    }

    //CALLED IN hOMEfRAGMENT
    // call this method when language is applied , so it will set the fonts

    fun setFont(lang: String, appController: AppController) = if (lang == "en") {
      //  println("Lang: " + lang)
        fontBold = Typeface.createFromAsset(appController.assets, "FuturaPTBold.otf")
        fontPriceBold = Typeface.createFromAsset(appController.assets, "FuturaPTBold.otf")
        fontSemiBold = Typeface.createFromAsset(appController.assets, "FuturaPTMedium.otf")
        fontMedium = Typeface.createFromAsset(appController.assets, "FuturaPTMedium.otf")
        fontPriceMedium = Typeface.createFromAsset(appController.assets, "FuturaPTMedium.otf")
        fontRegular = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        fontPriceRegular = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        fontThin = Typeface.createFromAsset(appController.assets, "FuturaPTLight.otf")
        fontLight = Typeface.createFromAsset(appController.assets, "FuturaPTLight.otf")
        fontBtn = Typeface.createFromAsset(appController.assets, "FuturaPTBold.otf")
        fontTabBar = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        fontNavBar = Typeface.createFromAsset(appController.assets, "FuturaPTDemi.otf")
        fontEngReg = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        fontExtraBold = Typeface.createFromAsset(appController.assets, "FuturaPTExtraBold.otf")
        fontArMedium =
            Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        fontNameReverse =
            Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        TypefaceUtil.overrideFont(appController, "MONOSPACE", "FuturaPTBook.otf")

    } else {
        fontBold = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-65Bold.ttf")
        fontPriceBold = Typeface.createFromAsset(appController.assets, "FuturaPTBold.otf")
        fontSemiBold = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-65Bold.ttf")
        fontMedium = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        fontPriceMedium = Typeface.createFromAsset(appController.assets, "FuturaPTMedium.otf")
        fontRegular = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        fontPriceRegular = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        fontThin = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-45Light.ttf")
        fontLight = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-45Light.ttf")
        fontBtn = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-65Bold.ttf")
        fontTabBar = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        fontNavBar = Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-65Bold.ttf")
        fontArMedium =
            Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-55Roman.ttf")
        fontExtraBold =
            Typeface.createFromAsset(appController.assets, "FrutigerLTArabic-65Bold.ttf")
        fontEngReg = Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")

        fontNameReverse =
            Typeface.createFromAsset(appController.assets, "FuturaPTBook.otf")
        TypefaceUtil.overrideFont(appController, "MONOSPACE", "FrutigerLTArabic-55Roman.ttf")

    }


    //EMPTY AND NULL
    fun checkEmptyAndNUll(strValue: String): String {
        return if (strValue.isNullOrEmpty()) {
            ""
        } else {
            strValue
        }

    }

    //check if user is logged in
    fun isUserLoggedIn(context: Context): Boolean {
        return getStringFromSharedPref(context, Constants.PREFS_isUSER_LOGGED_IN) == "yes"
    }

    //get APP language
    fun getLanguage(context: Context): String {
        return getStringFromSharedPref(context, Constants.PREFS_LANGUAGE) ?: ""
    }

    //get user id
    fun getUserId(context: Context): String {
        return getStringFromSharedPref(context, Constants.PREFS_USER_ID) ?: ""
    }

    //get store code
    fun getStoreCode(context: Context): String {
        return getStringFromSharedPref(context, Constants.PREFS_STORE_CODE) ?: ""
    }

    fun showToastShort(context: Context, strMsg: String) {
        Toast.makeText(context, strMsg, Toast.LENGTH_SHORT).show()
    }

    fun getStringFromSharedPref(context: Context, key: String): String {
        return SharedPreferencesHelper.getString(context, key, "")!!
    }

    fun getPreferenceCategory(activity: Activity): String {
        return getStringFromSharedPref(activity, Constants.PREFS_CATEGORY)
    }

    fun saveStringInSharedPref(context: Context, key: String, strInput: String) {
        SharedPreferencesHelper.writeString(context, key, strInput)
    }

    fun getSelectedCategory(context: Context): String {
        return Global.getStringFromSharedPref(context, Constants.PREFS_CATEGORY)
    }

    fun isValidEmail(target: CharSequence): Boolean =
        !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()

    fun isEnglishLanguage(activity: Activity): Boolean {
        return getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en"
    }

    fun getSelectedLanguage(activity: Activity): String {
        return getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE)

    }

    fun getWebViewData(activity: AppCompatActivity, strData: String): String {
        val strHead: String
        val strHtmlData: String
        if (getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en") {
            strHead =
                "<head><style>@font-face {font-family: 'FuturaPTBook';src: url('file:///android_asset/FuturaPTBook.otf');}body {font-family: 'FuturaPTBook';text-align:left;font-size:15px}</style></head>"
            strHtmlData =
                "<html>$strHead<body style=\"font-family: FuturaPTBook\">${
                    strData.replace(
                        "font-family",
                        ""
                    )
                }</body></html>"
        } else {
            strHead =
                "<head><style>@font-face {font-family: 'FrutigerLTArabic-55Roman';src: url('file:///android_asset/FrutigerLTArabic-55Roman.otf');}body {font-family: 'FrutigerLTArabic-55Roman';text-align:right;font-size:15px}</style></head>"
            strHtmlData =
                "<html>$strHead<body style=\"font-family: FrutigerLTArabic-55Roman\">${
                    strData.replace(
                        "font-family",
                        ""
                    )
                }</body></html>"
        }
        return strHtmlData
    }

    fun getCommaSeparatedString(arrListFilterValues: ArrayList<ProductListingFilterValueModel>): String {
        val sbString = StringBuilder("")

        for (i in 0 until arrListFilterValues.size) {
            if (arrListFilterValues[i].isSelected)
                sbString.append(arrListFilterValues[i].id).append(",");

        }
        //convert StringBuffer to String
        var strList = sbString?.toString()

        //remove last comma from String if you want
        if (strList.length > 0) {
            strList = strList.substring(0, strList.length - 1)
        } else {
            strList = ""
        }

        return strList
    }

    fun showSnackbar(activity: Activity, strMsg: String) {

        /*Protein.builder()
            .setActivity(activity)
            .text(strMsg)
            .backgroundColor(activity.resources.getColor(R.color.snack_bar_bg_color))
            .textColor(activity.resources.getColor(R.color.white))
            .build()
            .setDuration(1000)
            .show()*/
        /*val snack: Snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
            strMsg,
            Snackbar.LENGTH_SHORT
        )
        val textView = snack.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

        textView.setTextColor(ContextCompat.getColor(activity,R.color.white))
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,(activity.resources?.getDimension(R.dimen._12ssp)!!))
        textView.typeface = Global.fontMedium
        textView.gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        } else {
            textView.gravity = Gravity.CENTER_HORIZONTAL
        }
        SnackbarHelper.configSnackbar(activity, snack)
        val view = snack.view
        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        params.topMargin = activity.resources.getDimension(R.dimen._40sdp).toInt()
        params.marginStart = activity.resources.getDimension(R.dimen._5sdp).toInt()
        params.marginEnd = activity.resources.getDimension(R.dimen._5sdp).toInt()
        view.layoutParams = params
        snack.show()*/
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            strMsg,
            TSnackbar.LENGTH_LONG
        )
        val snackbarView: View = snackbar.getView()

        snackbarView.setBackgroundColor(activity.resources.getColor(R.color.black))
        val textView =
            snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text) as TextView
        textView.setTextColor(ContextCompat.getColor(activity, R.color.white))
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            (activity.resources?.getDimension(R.dimen._13ssp)!!)
        )
        textView.gravity = Gravity.CENTER
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
        } else {
            textView.gravity = Gravity.CENTER_HORIZONTAL
        }
        textView.typeface = Global.fontMedium
        com.app.blockaat.helper.SnackbarHelper.configSnackbar(activity, snackbar)
        val view = snackbar.view
        val params: FrameLayout.LayoutParams = view.layoutParams as FrameLayout.LayoutParams
        params.topMargin = activity.resources.getDimension(R.dimen._40sdp).toInt()
        params.marginStart = activity.resources.getDimension(R.dimen._5sdp).toInt()
        params.marginEnd = activity.resources.getDimension(R.dimen._5sdp).toInt()
        view.layoutParams = params
        snackbar.show()

    }


        fun setPriceWithCurrency(activity: Context, strPrice: String): String =
            if (strPrice.isNullOrEmpty()) {
                ""
            } else {
                if (Global.getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en") {

                    String.format(
                        "%.2f",
                        java.lang.Double.parseDouble(strPrice.replace(",", ""))
                    ) + " " + Global.getStringFromSharedPref(activity, Constants.PREFS_CURRENCY_EN)
                } else {
                    Global.arabicToEnglish(
                        String.format(
                            "%.2f",
                            java.lang.Double.parseDouble(strPrice.replace(",", ""))
                        )
                    ) + " " + Global.getStringFromSharedPref(activity, Constants.PREFS_CURRENCY_EN)
                }
            }
/*
    fun setPriceWithCurrency(activity: Context, strPrice: String): String =
        if (strPrice.isNullOrEmpty()) {
            ""
        } else {
            if (Global.getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en") {

                String.format(
                    Global.getStringFromSharedPref(activity, Constants.PREFS_CURRENCY_EN) + " " +
                            "%.2f",
                    java.lang.Double.parseDouble(strPrice.replace(",", ""))
                )
            } else {
                Global.arabicToEnglish(
                    String.format(
                        Global.getStringFromSharedPref(
                            activity,
                            Constants.PREFS_CURRENCY_EN
                        ) + " " +
                                "%.2f",
                        java.lang.Double.parseDouble(strPrice.replace(",", ""))
                    )
                )
            }
        }
*/

    fun setOnlyPrice(activity: Activity, strPrice: String): String = if (strPrice.isNullOrEmpty()) {
        ""
    } else {
        if (Global.getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en") {
            String.format("%.2f", java.lang.Double.parseDouble(strPrice.replace(",", "")))
        } else {
            Global.arabicToEnglish(
                String.format(
                    "%.2f",
                    java.lang.Double.parseDouble(strPrice.replace(",", ""))
                )
            )
        }
    }


    fun setOnlyPriceWithDecimal(activity: Activity, strPrice: String): String =
        if (strPrice.isNullOrEmpty()) {
            ""
        } else {
            if (Global.getStringFromSharedPref(activity, Constants.PREFS_LANGUAGE) == "en") {
                String.format("%.1f", java.lang.Double.parseDouble(strPrice.replace(",", "")))
            } else {
                Global.arabicToEnglish(
                    String.format(
                        "%.1f",
                        java.lang.Double.parseDouble(strPrice.replace(",", ""))
                    )
                )
            }
        }

    fun saveBooleanInSharedPref(context: Context, key: String, value: Boolean) {
        SharedPreferencesHelper.writeBoolean(context, key, value)
    }

    fun getBooleanFromSharedPref(context: Context, key: String): Boolean {
        return SharedPreferencesHelper.getBoolean(context, key, false)
    }

    fun checkNull(strValue: String?): String? {
        return strValue ?: ""
    }

    fun checkNullReturnDouble(strValue: Double?): Double? {
        return strValue ?: 0.0
    }

    fun setStore(arrListStore: ArrayList<StoreDataModel>) {
        this.arrListStore = arrListStore
    }

    fun setTabFont(tabLayout: TabLayout?, position: Int) {
        //your other customizations related to tab strip...blahblah
        // Set first tab selected
        val mTabsLinearLayout = tabLayout!!.getChildAt(0) as ViewGroup
        val tabsCount = mTabsLinearLayout.childCount
        for (j in 0 until tabsCount) {
            val vgTab = mTabsLinearLayout.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (k in 0 until tabChildsCount) {

                val tabViewChild = vgTab.getChildAt(k)
                if (j == position) {
                    // strPos="1000"
                    (tabViewChild as? TextView)?.typeface = Global.fontMedium
                } else {
                    (tabViewChild as? TextView)?.typeface = Global.fontLight
                }

            }
        }

    }

    public fun toCamelCase(init: String): String {
        if (init == null)
            return ""

        val ret = StringBuilder(init.length)

        for (word in init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(Character.toUpperCase(word[0]))
                ret.append(word.substring(1).toLowerCase())
            }
            if (ret.length != init.length)
                ret.append(" ");
        }
        return ret.toString();
    }

    fun getFontRegular(): Typeface? {
        return fontRegular
    }

    fun getFontNavbar(): Typeface? {
        return fontNavBar
    }

    fun getFontBold(): Typeface? {
        return fontBold
    }

    fun getFontPriceBold(): Typeface? {
        return fontPriceBold
    }

    fun getFontPriceMedium(): Typeface? {
        return fontPriceMedium
    }

    fun getFontPriceRegular(): Typeface? {
        return fontPriceRegular
    }

    fun getFontButton(): Typeface? {
        return fontBtn
    }

    fun getFontMedium(): Typeface? {
        return fontMedium
    }

    fun arabicToEnglish(strNumber: String?): String {
        var strNumber = strNumber
        if (strNumber != null && !strNumber.isEmpty()) {
            if (strNumber.contains("١"))
                strNumber = strNumber.replace("١".toRegex(), "1")
            if (strNumber.contains("٢"))
                strNumber = strNumber.replace("٢".toRegex(), "2")
            if (strNumber.contains("٣"))
                strNumber = strNumber.replace("٣".toRegex(), "3")
            if (strNumber.contains("٤"))
                strNumber = strNumber.replace("٤".toRegex(), "4")
            if (strNumber.contains("٥"))
                strNumber = strNumber.replace("٥".toRegex(), "5")
            if (strNumber.contains("٦"))
                strNumber = strNumber.replace("٦".toRegex(), "6")
            if (strNumber.contains("٧"))
                strNumber = strNumber.replace("٧".toRegex(), "7")
            if (strNumber.contains("٨"))
                strNumber = strNumber.replace("٨".toRegex(), "8")
            if (strNumber.contains("٩"))
                strNumber = strNumber.replace("٩".toRegex(), "9")
            if (strNumber.contains("٠"))
                strNumber = strNumber.replace("٠".toRegex(), "0")
            if (strNumber.contains("٫"))
                strNumber = strNumber.replace("٫".toRegex(), ".")
        } else {
            strNumber = ""
        }

        return strNumber
    }

    fun getDiscountedPrice(finalPrice: String, regularPrice: String): Int {
        //if final & regular price non empty then calculating discount else 0
        return if (!finalPrice.isNullOrEmpty() && !regularPrice.isNullOrEmpty()) {
            (100 - Math.ceil(finalPrice.toDouble() / regularPrice.toDouble() * 100)).toInt()
        } else {
            0
        }
    }

    fun loadImagesUsingGlide(context: Context, strUrl: String?, imageView: ImageView) {
        Glide.with(context).load(strUrl?.replace("https","http")).fitCenter().into(imageView)
    }

    fun getDeviceWidth(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

    fun getDeviceHeight(activity: Activity): Int {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels

    }

    fun getFormattedDateWithNotation(inputFormat: String, value: String): String {
        var newDateStr = ""
        if (!value.isEmpty()) {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val dateObj: Date?
            try {
                dateObj = inputFormatter.parse(value)

                val cal = Calendar.getInstance()
                cal.time = dateObj
                val day = cal.get(Calendar.DATE)
                if (!(day > 10 && day < 19))
                    newDateStr = when (day % 10) {
                        1 -> SimpleDateFormat("d'st' MMM, yyyy").format(dateObj)
                        2 -> SimpleDateFormat("d'nd' MMM, yyyy").format(dateObj)
                        3 -> SimpleDateFormat("d'rd' MMM, yyyy").format(dateObj)
                        else -> SimpleDateFormat("d'th' MMM, yyyy").format(dateObj)
                    } else {
                    newDateStr = SimpleDateFormat("d'th' MMM, yyyy").format(dateObj)
                }
            } catch (e: ParseException) {
                newDateStr = value
            }
        }

        return newDateStr
    }


    fun getFormattedDate(inputFormat: String, outputFormat: String, value: String): String {
        // TODO Auto-generated method stub
        var newDateStr = ""
        if (!value.isNullOrEmpty()) {
            val inputFormatter = SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val dateObj: Date?
            try {
                dateObj = inputFormatter.parse(value)
                val outputFormatter = SimpleDateFormat(outputFormat)
                newDateStr = outputFormatter.format(dateObj)
            } catch (e: ParseException) {
                //e.printStackTrace()
                newDateStr = value
            }
        }
        return Global.arabicToEnglish(newDateStr)
    }

    fun getFormattedLocalDateTime(
        inputFormat: String,
        outputFormat: String,
        value: String
    ): String {
        val curFormater = SimpleDateFormat(inputFormat, Locale.ENGLISH)
        curFormater.timeZone = TimeZone.getTimeZone("UTC")
        var dateObj: Date? = null
        try {
            dateObj = curFormater.parse(value)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val postFormater = SimpleDateFormat(outputFormat)
        postFormater.timeZone = TimeZone.getDefault()
        val newDateStr = postFormater.format(dateObj)
        //   System.out.println("outputValue: " + newDateStr);
        return arabicToEnglish(newDateStr)
    }

    //this will return total number of product present in cart
    fun getTotalCartProductCount(context: Context): Int {
        val productsDBHelper = DBHelper(context)
        return if (productsDBHelper.getTotalCartProductCount() > 0) {
            productsDBHelper.getTotalCartProductCount()
        } else {
            -1
        }
    }

    //this will return total number of qty present in cart
    fun getTotalCartProductQtyCount(context: Context): Int {
        val productsDBHelper = DBHelper(context)
        return if (productsDBHelper.getTotalQtyCount() > 0) {
            productsDBHelper.getTotalQtyCount()
        } else {
            -1
        }
    }

    fun getTotalWishListProductCount(context: Context): Int {
        val productsDBHelper = DBHelper(context)
        return if (productsDBHelper.getTotalWishlistProductCount() > 0) {
            productsDBHelper.getTotalWishlistProductCount()
        } else {
            -1
        }
    }

/*
    fun getFormattedAddressForListing(activity: Activity, strNotes: String, strFloor: String, strApartment: String, strBuildingNo: String, strBlockName: String, strStreet: String, strAreaName: String, strGovernorate: String, strCountryName: String): String {
        var strAddress = ""

//        Block,Street,Building No , Floor , Flat/apartment
//        Area,Governorate

        if (strBlockName != null && strBlockName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strBlockName"
            else
                strAddress = strBlockName
        }

        if (strStreet != null && strStreet != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strStreet"
            else
                strAddress = strStreet
        }

        if (strBuildingNo != null && strBuildingNo != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, " + activity.getString(R.string.bld_no) + strBuildingNo
            else
                strAddress = activity.getString(R.string.bld_no) + strBuildingNo
        }

        if (strFloor != null && strFloor != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, " + activity.getString(R.string.floor_no) + strFloor
            else
                strAddress = activity.getString(R.string.floor_no) + strFloor
        }


        if (strApartment != null && strApartment != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strApartment"
            else
                strAddress = strApartment
        }


        if (strAreaName != null && strAreaName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress,\n$strAreaName"
            else
                strAddress = strAreaName
        }

        if (strGovernorate != null && strGovernorate != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strGovernorate"
            else
                strAddress = strGovernorate
        }

        if (strCountryName != null && strCountryName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strCountryName"
            else
                strAddress = strCountryName
        }

        return strAddress
    }
*/


/*
    fun getFormattedAddress(activity: Activity, strNotes: String, strFloor: String, strApartment: String, strBuildingNo: String, strBlockName: String, strStreet: String, strAreaName: String, strGovernorate: String, strCountryName: String): String {
        var strAddress = ""

        //        Block,Street,Building No , Floor , Flat/apartment
//        Area,Governorate


        if (strBlockName != null && strBlockName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strBlockName"
            else
                strAddress = strBlockName
        }

        if (strStreet != null && strStreet != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strStreet"
            else
                strAddress = strStreet
        }

        if (strBuildingNo != null && strBuildingNo != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, " + activity.getString(R.string.bld_no) + strBuildingNo
            else
                strAddress = activity.getString(R.string.bld_no) + strBuildingNo
        }

        if (strFloor != null && strFloor != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, " + activity.getString(R.string.floor_no) + strFloor
            else
                strAddress = strFloor
        }

        if (strApartment != null && strApartment != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strApartment"
            else
                strAddress = strApartment
        }

        if (strAreaName != null && strAreaName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress,\n$strAreaName"
            else
                strAddress = strAreaName
        }

        if (strGovernorate != null && strGovernorate != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strGovernorate"
            else
                strAddress = strGovernorate
        }

        if (strCountryName != null && strCountryName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strCountryName"
            else
                strAddress = strCountryName
        }

        return strAddress
    }
*/

    fun getFormattedAddressForListing(
        activity: Context,
        notes: String?,
        flat: String?,
        floor: String?,
        building_no: String?,
        street: String?,
        block: String?,
        area: String?,
        governorate: String?,
        country: String?
    ): String? {
        var strAddress = ""



        if (flat != null && flat != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $flat"
            else strAddress = flat
        }

        if (floor != null && floor != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $floor"
            else strAddress = floor
        }

        if (building_no != null && building_no != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $building_no"
            else strAddress = building_no
        }



        if (street != null && street != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $street"
            else strAddress = street
        }

        if (block != null && block != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $block"
            else strAddress = block
        }

        if (area != null && area != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $area"
            else strAddress = area
        }

        if (governorate != null && governorate != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $governorate"
            else strAddress = governorate
        }

        //country
        if (country != null && country != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $country"
            else strAddress = country
        }

        if (notes != null && notes != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $notes"
            else strAddress = notes
        }
        return strAddress
    }

    fun getFormattedAddressForListingWithLabel(
        activity: Context,
        notes: String?,
        flat: String?,
        floor: String?,
        building_no: String?,
        street: String?,
        jaddah: String?,
        block: String?,
        area: String?,
        governorate: String?,
        country: String?
    ): String? {
        var strAddress = ""

        if (flat != null && flat != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.flat_label) + flat
            else strAddress = activity.resources.getString(R.string.flat_label) + flat
        }

        if (floor != null && floor != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.floor_label) + floor
            else strAddress = activity.resources.getString(R.string.floor_label) + floor
        }

        if (building_no != null && building_no != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.building_label) + building_no
            else strAddress = activity.resources.getString(R.string.building_label) + building_no
        }


        if (street != null && street != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, " + activity.resources.getString(R.string.street_label) + street
            else strAddress = activity.resources.getString(R.string.street_label) + street
        }

        if (jaddah != null && jaddah != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.jaddah_label) + jaddah
            else strAddress = activity.resources.getString(R.string.jaddah_label) + jaddah
        }


        if (block != null && block != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.block_label) + block
            else strAddress = activity.resources.getString(R.string.block_label) + block
        }


        if (area != null && area != "") {
            if (strAddress.isNotEmpty()) strAddress =
                "$strAddress, " + activity.resources.getString(R.string.area_label) + area
            else strAddress = activity.resources.getString(R.string.area_label) + area
        }

        if (governorate != null && governorate != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $governorate"
            else strAddress = governorate
        }

        //country
        if (country != null && country != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress, $country"
            else strAddress = country
        }

        if (notes != null && notes != "") {
            if (strAddress.isNotEmpty()) strAddress = "$strAddress \n$notes"
            else strAddress = notes
        }
        return strAddress
    }


    fun getFormattedAddressCart(
        activity: AppCompatActivity,
        strNotes: String,
        strBlockName: String,
        strStreet: String,
        strAreaName: String,
        strGovernorate: String,
        strCountryName: String
    ): String {
        var strAddress = ""

        //        Block,Street,Building No , Floor , Flat/apartment
//        Area,Governorate


        if (strBlockName != null && strBlockName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strBlockName"
            else
                strAddress = strBlockName
        }

        if (strStreet != null && strStreet != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress, $strStreet"
            else
                strAddress = strStreet
        }

        if (strAreaName != null && strAreaName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress\n$strAreaName"
            else
                strAddress = strAreaName
        }

        if (strGovernorate != null && strGovernorate != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress\n$strGovernorate"
            else
                strAddress = strGovernorate
        }

        if (strCountryName != null && strCountryName != "") {
            if (strAddress.isNotEmpty())
                strAddress = "$strAddress \n$strCountryName"
            else
                strAddress = strCountryName
        }

        return strAddress
    }

    fun getFormattedAddress(
        activity: Activity,
        strZone: String,
        strBlockName: String,
        strStreet: String,
        strJaddah: String,
        strAreaName: String,
        strGovernorate: String,
        strCountryName: String,
        strBuilding: String

    ): String {
        var strAddress = ""

        if (strBuilding != null && strBuilding != "") {
            strAddress = if (strBuilding.isNotEmpty())
                activity.resources.getString(R.string.bld_no) + " " + strBuilding
            else
                strBuilding
        }

        if (strGovernorate != null && strGovernorate != "") {
            strAddress = if (strAddress.isNotEmpty())
                "$strAddress $strGovernorate"
            else
                strGovernorate
        }

        if (strStreet != null && strStreet != "") {
            strAddress = if (strAddress.isNotEmpty())
                strAddress + "," + activity.resources.getString(R.string.street) + " " + strStreet
            else
                strStreet
        }
        if (strJaddah != null && strJaddah != "") {
            strAddress = if (strAddress.isNotEmpty())
                strAddress + "," + activity.resources.getString(R.string.jaddah) + " " + strJaddah
            else
                strJaddah
        }
        if (strAreaName != null && strAreaName != "") {
            strAddress = if (strAddress.isNotEmpty())
                "$strAddress, $strAreaName"
            else
                strAreaName
        }

        if (strZone != null && strZone != "") {
            strAddress = if (strAddress.isNotEmpty())
                strAddress + "," + activity.resources.getString(R.string.zone) + " " + strZone
            else
                strZone
        }




        if (strCountryName != null && strCountryName != "") {
            strAddress = if (strAddress.isNotEmpty())
                "$strAddress, $strCountryName"
            else
                strCountryName
        }

        return strAddress
    }

    fun intToString(value: Int?): String {
        return try {
            if (value == null) {
                ""
            } else {
                value.toString()
            }
        } catch (e: Exception) {
            ""
        }
    }

    fun stringToInt(value: String?): Int {
        return try {
            if (value.isNullOrEmpty()) {
                0
            } else {
                value.toInt()
            }
        } catch (e: Exception) {
            0
        }
    }

    fun stringToDouble(value: String?): Double {
        return try {
            if (value.isNullOrEmpty()) {
                0.0
            } else {
                value.toDouble()
            }
        } catch (e: Exception) {
            0.0
        }
    }

    fun setTabFont(tabLayout: TabLayout?, context: Context) {
        val vg = tabLayout!!.getChildAt(0) as ViewGroup
        val tabsCount = vg.childCount
        for (j in 0 until tabsCount) {
            val vgTab = vg.getChildAt(j) as ViewGroup
            val tabChildsCount = vgTab.childCount
            for (k in 0 until tabChildsCount) {
                val tabViewChild = vgTab.getChildAt(k)
                (tabViewChild as? TextView)?.typeface = fontMedium
            }
        }

    }


    fun showProgressDialog(activity: Context) {
        dialog = CustomProgressBar(activity as Activity)
        dialog?.showDialog()
    }

    fun hideProgressDialog() {
        dialog?.hideDialog()
    }


    fun setContext(context: Context) {
        val locale = Locale(SharedPreferencesHelper.getString(context, "lang_new", "en"))
        Locale.setDefault(locale)
        val config = context.getResources().getConfiguration()
        config.setLocale(locale)
        context.createConfigurationContext(config)
        context.getResources()
            .updateConfiguration(config, context.getResources().getDisplayMetrics())
    }

    fun getDimenVallue(context: Context, value: Double): Double {
        val DEVIDE_MULTIPLIER: Double =
            SharedPreferencesHelper.getString(
                context,
                Constants.PREFS_DEVIDE_MULTIPLIER,
                ""
            )!!.toDouble()
        return DEVIDE_MULTIPLIER * value
    }

    fun getDimension(activity: Activity, size: Int): Int {
        return if (Constants.DEVICE_DENSITY > 0) {
            //density saved in constant calculated on first time in splash if in case its 0 then calculate again
            (Constants.DEVICE_DENSITY * size.toDouble()).toInt()
        } else {
            ((Global.getDeviceWidthInDouble(activity) / 320) * size.toDouble()).toInt()

        }
    }


    fun getDeviceWidthInDouble(activity: Activity): Double {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.toDouble()
    }


    fun dp2px(context: Context, value: Float): Int { //convert dp to px
        return (value / context.resources.displayMetrics.density).toInt()
    }

    fun getStatusBarHeight(activity: AppCompatActivity): Int {
        val rectangle = Rect()
        // Window window = getWindow();
        val window: Window = activity.window
        window.decorView.getWindowVisibleDisplayFrame(rectangle);
        val statusBarHeight = rectangle.top
       // println("Here i am rectangle top is     " + rectangle)
        return statusBarHeight
        /* var result = 0
         val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
         if (resourceId > 0) {
             result = activity.resources.getDimensionPixelSize(resourceId)
         }
         return result*/
    }

    fun setLocale(activity: Activity) {
        val res = activity.resources
        val dm = res.displayMetrics
        val conf = res.configuration
        conf.setLocale(
            Locale(
                Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_LANGUAGE
                ).toLowerCase()
            )
        )
        res.updateConfiguration(conf, dm)
    }


    fun addToWishlist(activity: Context, strProductID: String, productsDBHelper: DBHelper) {
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            showProgressDialog(activity)
            Global.apiService.addToWishlist(
                Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                strProductID,
                com.app.blockaat.apimanager.WebServices.AddToWishlistWs + Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                       // println("RESPONSE - Add to wishlist Ws :   " + Gson().toJson(result.data))
                        hideProgressDialog()

                        if (result != null) {

                            if (result.data != null && result.status == 200) {

                                if (!productsDBHelper.isProductPresentInWishlist(strProductID)) {
                                    productsDBHelper.addProductToWishlist(
                                        ProductsDataModel(
                                            strProductID
                                        )
                                    )
                                }
                                if (activity is NavigationActivity)
                                    (activity as NavigationActivity).updateCounts()

                            }
                        }
                    },
                    { error ->
                       // println("RESPONSE - Add to wishlist Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                    }
                )
        }
    }


    fun deleteFromWishlist(activity: Context, strProductID: String, productsDBHelper: DBHelper) {
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            showProgressDialog(activity)

            Global.apiService.deleteWishlist(
                Global.getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                strProductID,
                com.app.blockaat.apimanager.WebServices.DeleteWishlistItemWs + Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_STORE_CODE
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                      //  println("RESPONSE - DELETE wishlist Ws :   " + Gson().toJson(result))
                        hideProgressDialog()

                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null) {

                                    if (productsDBHelper.isProductPresentInWishlist(strProductID)) {
                                        productsDBHelper.deleteProductFromWishlist(strProductID)
                                    }
                                    if (activity is NavigationActivity)
                                        (activity as NavigationActivity).updateCounts()

                                }
                            } else {
                                //Global.showSnackbar(activity, result.message)
                            }
                        } else {
                            //if ws not working
                            //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                        }
                    },
                    { error ->
                        hideProgressDialog()

                       // println("ERROR - DELETE wishlist Ws :   " + error.localizedMessage)
                        //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                    }
                )
        }
    }

    fun addToCartOnline(activity: Activity, model: AddCartRequestModel) {

        val productsDBHelper = DBHelper(activity)
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            showProgressDialog(activity)
            val addCartObject = AddCartRequestApi(
                getStringFromSharedPref(activity, Constants.PREFS_USER_ID),
                model.entity_id ?: "",
                "1",
                model.influencer_id.toString(),
                model.video_id.toString()
            )
            val disposable = apiService.addToCart(
                com.app.blockaat.apimanager.WebServices.AddToCartWs + Global.getStringFromSharedPref(
                    activity,
                    Constants.PREFS_LANGUAGE
                )
                        + "&store=" + Global.getStringFromSharedPref(
                    activity,
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

                                if (!productsDBHelper.isProductPresentInCart(
                                        model.id,
                                        model.entity_id ?: "0"
                                    )
                                ) {
                                    //once added in ws update same in DB
                                    productsDBHelper.addProductToCart(
                                        ProductsDataModel(
                                            model.id,
                                            model.entity_id ?: "0",
                                            model.name,
                                            model.brand,
                                            model.image,
                                            "",
                                            "1",
                                            model.final_price,
                                            model.regular_price,
                                            model.SKU,
                                            "10",
                                            "0",
                                            model.is_saleable.toString(),
                                            model.product_type,
                                            "",
                                            ""
                                        )
                                    )
                                    println(
                                        "HERE I M  :::  ADD - ONLINE :::    " + " : " + model.id
                                        + "   :   " + model.entity_id ?: "0"
                                    )
                                } else {
                                    var qty: Int = productsDBHelper.getQtyInCart(
                                        model.id,
                                        model.entity_id ?: "0"
                                    ).toInt()
                                    qty += 1
                                    productsDBHelper.updateProductsInCart(
                                        qty.toString(),
                                        model.id,
                                        model.entity_id ?: "0"
                                    )
                                }
                                println(
                                    "Here i am cart total is 1   " + Global.getTotalCartProductQtyCount(
                                        activity
                                    )
                                )


                                showAlert(
                                    activity,
                                    "",
                                    activity.resources.getString(R.string.cart_success_msg),
                                    activity.resources.getString(R.string.checkout),
                                    activity.resources.getString(R.string.yes),
                                    false,
                                    R.drawable.ic_alert_bag,
                                    object : AlertDialogInterface {
                                        override fun onYesClick() {
                                            val intent = Intent(activity, CartActivity::class.java)
                                            activity.startActivity(intent)
                                        }

                                        override fun onNoClick() {

                                        }

                                    })

                            } else {

                                showSnackbar(activity, result.message ?: "")
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                        }
                    },
                    { error ->
                        hideProgressDialog()
                     //   println("ERROR - AddToCart Ws :   " + error.localizedMessage)
                    }
                )
        }
    }

    fun addToCartOffline(activity: Context, model: AddCartRequestModel) {

        val productsDBHelper = DBHelper(activity)
        if (!productsDBHelper.isProductPresentInCart(model.id, model.entity_id ?: "0")) {

            if (!productsDBHelper.isProductPresentInCart(model.id, model.entity_id ?: "0")) {
                //once added in ws update same in DB
                productsDBHelper.addProductToCart(
                    ProductsDataModel(
                        model.id,
                        model.entity_id ?: "0",
                        model.name,
                        model.brand,
                        model.image,
                        "",
                        "1",
                        model.final_price,
                        model.regular_price,
                        model.SKU,
                        "10",
                        "0",
                        model.is_saleable.toString(),
                        model.product_type,
                        "",
                        ""
                    )
                )

            } else {
                var qty: Int =
                    productsDBHelper.getQtyInCart(model.id, model.entity_id ?: "0").toInt()
                qty += 1
                productsDBHelper.updateProductsInCart(
                    qty.toString(),
                    model.id,
                    model.entity_id ?: "0"
                )
            }

            if (Global.getTotalCartProductQtyCount(activity) > 0) {
                //txtCartBadgeCount.visibility = View.VISIBLE
                //txtCartBadgeCount.text = Global.getTotalCartProductQtyCount(this@ProductDetailsActivity).toString()
            } else {
                //txtCartBadgeCount.visibility = View.GONE
            }

        } else {
            //Global.showSnackbar(this@ProductDetailsActivity, resources.getString(R.string.update_to_cart_successfully))
            var qty: Int = productsDBHelper.getQtyInCart(model.id, model.entity_id ?: "0").toInt()
            qty += 1
            productsDBHelper.updateProductsInCartOffline(
                qty.toString(),
                model.id,
                model.entity_id ?: "0",
                "",
                ""
            )
            //println("HERE I M  :::  UPDATE     " + strEntityID + "   :   " + strAttributeTwoValue + "    :   " + strAttributeOneValue)
        }

        showAlert(
            activity,
            "",
            activity.resources.getString(R.string.cart_success_msg),
            activity.resources.getString(R.string.checkout),
            activity.resources.getString(R.string.yes),
            false,
            R.drawable.ic_alert_bag,
            object : AlertDialogInterface {
                override fun onYesClick() {
                    val intent = Intent(activity, CartActivity::class.java)
                    activity.startActivity(intent)
                }

                override fun onNoClick() {

                }

            })
    }


    private fun showAlertNotify(
        activity: Context,
        requestModel: NotifyMeRequestModel,
        productId: String
    ) {
        try {
            val phone = getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_PHONE
            )
            val dialog = Dialog(activity)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setContentView(R.layout.custom_notify)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(dialog.window?.attributes)
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            dialog.window?.attributes = lp
//            dialog?.edtMobileNumber.setText(phone)
            dialog.setCanceledOnTouchOutside(false)

            if (requestModel.name.isNotEmpty())
                dialog.edtFullName.setText(requestModel.name)
          //  println("Global phone code: " + requestModel.phone_code)
            if (requestModel.phone_code.isNotEmpty()) {
                dialog.txtPhoneCode.text = requestModel.phone_code
            } else {
                dialog.txtPhoneCode.text = activity.resources.getString(R.string.phone_code_hint)

            }
            if (requestModel.phone.isNotEmpty())
                dialog.edtMobileNumber.setText(requestModel.phone)
            if (requestModel.email.isNotEmpty())
                dialog.edtEmail.setText(requestModel.email)

            dialog.txtNotifyMe.typeface = fontBold
            dialog.edtFullName.typeface = fontRegular
            dialog.txtPhoneCode.typeface = fontRegular
            dialog.edtMobileNumber.typeface = fontRegular
            dialog.edtEmail.typeface = fontRegular
            dialog.btnNotifyMe.typeface = fontBtn

            dialog.imgClose.setOnClickListener {
                dialog.dismiss()

            }

            dialog.btnNotifyMe.setOnClickListener {
                if (dialog.currentFocus != null) {
                    val imm =
                        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(dialog?.currentFocus?.windowToken, 0)
                }

                if (dialog.edtFullName.text.isNullOrEmpty()) {
                    showSnackbar(
                        activity as Activity,
                        activity.resources.getString(R.string.enter_name)
                    )
                } else if (dialog.edtMobileNumber.text.isNullOrEmpty()) {
                    showSnackbar(
                        activity as Activity,
                        activity.resources.getString(R.string.error_phone)
                    )

                } else if (dialog.edtMobileNumber.text.length < 8) {
                    showSnackbar(
                        activity as Activity,
                        activity.resources.getString(R.string.error_phone_length)
                    )
                } else if (dialog.edtEmail.text.isNullOrEmpty()) {
                    showSnackbar(
                        activity as Activity,
                        activity.resources.getString(R.string.empty_email)
                    )
                } else if (!isValidEmail(dialog.edtEmail.text.toString())) {
                    showSnackbar(
                        activity as Activity,
                        activity.resources.getString(R.string.please_enter_valid_email_id)
                    )
                } else {
                    if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                        val requestModel = NotifyMeRequestModel(
                            user_id = getUserId(activity),
                            email = dialog.edtEmail.text.toString(),
                            phone = dialog.edtMobileNumber.text.toString(),
                            product_id = productId,
                            name = dialog.edtFullName.text.toString(),
                            phone_code = dialog.txtPhoneCode.text.toString()
                        )
                        //loading
                        showProgressDialog(activity)
                        Global.apiService.notifyMe(
                            requestModel,
                            com.app.blockaat.apimanager.WebServices.NotifyMe + getLanguage(activity)
                        )
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                { result ->
                                    hideProgressDialog()
                                    if (result != null) {

                                    }
                                },
                                { error ->
                                    hideProgressDialog()
                                    Global.showSnackbar(
                                        activity as Activity,
                                        activity.resources.getString(R.string.error)
                                    )
                                }
                            )
                    }
                    dialog.dismiss()

                }

            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

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
            dialog.txtAlertTitle.text = activity.getString(R.string.alert)
            dialog.txtAlertMessage.text = msg
            dialog.btnAlertNegative.text = noBtn
            dialog.btnAlertPositive.text = yesBtn
            dialog.ivAlertImage.setImageResource(image)

            dialog.txtAlertMessage.typeface = fontRegular
            dialog.txtAlertTitle.typeface = fontSemiBold
            dialog.btnAlertNegative.typeface = fontBtn
            dialog.btnAlertPositive.typeface = fontBtn
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


    fun showLanguageAlert(
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
            dialog.txtAlertTitle.text = activity.getString(R.string.are_you_sure)
            dialog.txtAlertMessage.text = msg
            dialog.btnAlertNegative.text = noBtn
            dialog.btnAlertPositive.text = yesBtn
            dialog.ivAlertImage.setImageResource(image)

            dialog.txtAlertMessage.typeface = fontNameReverse
            dialog.txtAlertTitle.typeface = fontSemiBold
            dialog.btnAlertNegative.typeface = fontNameReverse
            dialog.btnAlertPositive.typeface = fontNameReverse
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


    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun convertPixelsToDp(dimension: Float, navigationActivity: NavigationActivity): Float {
        return dimension / (navigationActivity.resources
            .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

    }

    fun setBackArrow(activity: Activity, ivBackArrow: ImageView, txtHead: TextView, view: View) {
        ivBackArrow.visibility = VISIBLE
        txtHead.visibility = VISIBLE
        view.visibility = VISIBLE
        if (!isEnglishLanguage(activity)) {
            ivBackArrow.rotationY = 180f
        }
    }

    fun setBackArrows(activity: Activity, ivBackArrow: ImageView, txtHead: TextView, view: View) {
        ivBackArrow.visibility = GONE
        txtHead.visibility = VISIBLE
        view.visibility = GONE
        if (!isEnglishLanguage(activity)) {
            ivBackArrow.rotationY = 180f
        }
    }

    @SuppressLint("CheckResult")
    fun notifyMe(activity: Context, productId: String) {
        val requestModel = NotifyMeRequestModel(
            user_id = getUserId(activity),
            email = getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_EMAIL
            ),
            phone = getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_PHONE
            ),
            product_id = productId,
            name = getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_FIRST_NAME
            ) + getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_LAST_NAME
            ),
            phone_code = getStringFromSharedPref(
                activity,
                Constants.PREFS_USER_PHONE_CODE
            )
        )
        showAlertNotify(activity, requestModel, productId)

    }

    // updated by azim
    @SuppressLint("CheckResult")
    fun addOrRemoveWishList(
        activity: Context,
        position: Int,
        strProductID: String,
        productsDBHelper: DBHelper,
        flagWishlist: Boolean,
        addWishListInterface: AddWishListInterface
    ) {
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            showProgressDialog(activity)
            val url =
                if (flagWishlist) com.app.blockaat.apimanager.WebServices.AddToWishlistWs else com.app.blockaat.apimanager.WebServices.DeleteWishlistItemWs
            Global.apiService.deleteWishlist(
                getUserId(activity),
                strProductID,
                url + getLanguage(activity)
                        + "&store=" + getStoreCode(activity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null) {
                                    // start azim
//                                    val wishListData: WishListDataModel = result.data[position]
//                                    val productId: String? = wishListData.id
//                                    val productName:String? = wishListData.name
//                                    val brandName:String? = wishListData.brand_name
//                                    val price:String? = wishListData.final_price
//                                    firebaseAnalytics = AppController.instance.fAnalytics
//                                    CustomEvents.addToWishList(firebaseAnalytics!!,productId,productName,brandName,price)
                                    if (!flagWishlist) {
                                        if (productsDBHelper.isProductPresentInWishlist(strProductID)) {
                                            productsDBHelper.deleteProductFromWishlist(strProductID)
                                        }
                                        if (activity is NavigationActivity)
                                            (activity as NavigationActivity).updateCounts()
                                        addWishListInterface.onRemove(result)
                                    } else {
                                        if (!productsDBHelper.isProductPresentInWishlist(
                                                strProductID
                                            )
                                        ) {
                                            productsDBHelper.addProductToWishlist(
                                                ProductsDataModel(
                                                    strProductID
                                                )
                                            )
                                        }
                                        if (activity is NavigationActivity)
                                            (activity as NavigationActivity).updateCounts()
                                        addWishListInterface.onAdd(result)
                                    }
                                }
                            } else {
                                //Global.showSnackbar(activity, result.message)
                            }
                        } else {
                            //if ws not working
                            //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                    }
                )
        }
    }

    @SuppressLint("CheckResult")
    fun addOrRemoveWishList(
        activity: Context,
        strProductID: String,
        productsDBHelper: DBHelper,
        flagWishlist: Boolean,
        addWishListInterface: AddWishListInterface
    ) {
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            showProgressDialog(activity)
            val url =
                if (flagWishlist) com.app.blockaat.apimanager.WebServices.AddToWishlistWs else com.app.blockaat.apimanager.WebServices.DeleteWishlistItemWs
            Global.apiService.deleteWishlist(
                getUserId(activity),
                strProductID,
                url + getLanguage(activity)
                        + "&store=" + getStoreCode(activity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                if (result.data != null) {
                                    if (!flagWishlist) {
                                        if (productsDBHelper.isProductPresentInWishlist(strProductID)) {
                                            productsDBHelper.deleteProductFromWishlist(strProductID)
                                        }
                                        if (activity is NavigationActivity)
                                            (activity as NavigationActivity).updateCounts()
                                        addWishListInterface.onRemove(result)
                                    } else {
                                        if (!productsDBHelper.isProductPresentInWishlist(
                                                strProductID
                                            )
                                        ) {
                                            productsDBHelper.addProductToWishlist(
                                                ProductsDataModel(
                                                    strProductID
                                                )
                                            )
                                        }
                                        if (activity is NavigationActivity)
                                            (activity as NavigationActivity).updateCounts()
                                        addWishListInterface.onAdd(result)
                                    }
                                }
                            } else {
                                //Global.showSnackbar(activity, result.message)
                            }
                        } else {
                            //if ws not working
                            //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        //Global.showSnackbar(activity, activity.resources.getString(R.string.error))
                    }
                )
        }
    }

    fun setCategories(activity: Activity, categories: ArrayList<RootCategoriesData?>?) {
        val sharedPreferences =
            activity.getSharedPreferences(Constants.CATEGORY_LIST, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(categories)
        editor.putString(Constants.CATEGORY_LIST, json)
        editor.apply()
    }

    fun getRootCategories(activity: Activity): List<RootCategoriesData>? {
        val sharedPreferences = activity?.getSharedPreferences(
            Constants.CATEGORY_LIST,
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json = sharedPreferences?.getString(Constants.CATEGORY_LIST, null)
        val type = object : TypeToken<ArrayList<RootCategoriesData?>?>() {}.type
        return gson.fromJson<List<RootCategoriesData>>(json, type)
    }

    @SuppressLint("CheckResult")
    fun getRootParameters(activity: Activity) {
        if (NetworkUtil.getConnectivityStatus(activity) != 0) {
            //loading
            showProgressDialog(activity)
            Global.apiService.getRootCategories(
                com.app.blockaat.apimanager.WebServices.RootCategoriesWs + getLanguage(activity) + "&store=" + getStoreCode(
                    activity
                )
                        + "&user_id=" + getUserId(activity)
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200 && result.data != null) {
                                setCategories(
                                    activity,
                                    result.data as ArrayList<RootCategoriesData?>?
                                )
                                val i = Intent(activity, NavigationActivity::class.java)
                                i.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                activity.startActivity(i)
                            }
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        println("ERROR - root category Ws :   " + error.localizedMessage)

                    })
        }
    }

    fun requestCallPermission(mActivity: Activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                mActivity,
                Manifest.permission.CALL_PHONE
            )
        ) {

            ActivityCompat.requestPermissions(
                mActivity,
                arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PHONE
            )

        } else {
            ActivityCompat.requestPermissions(
                mActivity, arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL_PHONE
            )
        }
    }

    fun getUserID(activity: Activity): String {
        if (isUserLoggedIn(activity)) {
            return getStringFromSharedPref(activity, Constants.PREFS_USER_ID)
        } else {
            return ""
        }
    }

    fun getCurrencyCode(context: Context): String {
        return getStringFromSharedPref(context, Constants.PREFS_STORE_CODE)
    }

    fun getIso3(context: Context, lang: String): String {
        var iso2 = ""
        if (lang.isNullOrEmpty()) {
            iso2 = getCurrencyCode(context)
        } else {
            iso2 = lang
        }
        var currency_code = ""

        when (iso2) {
            "KW" -> {
                currency_code = "KWD"
            }
            "QA" -> {
                currency_code = "QAR"
            }
            "BH" -> {
                currency_code = "BHD"
            }
            "SA" -> {
                currency_code = "SAR"
            }
            "OM" -> {
                currency_code = "OMR"
            }
            "AE" -> {
                currency_code = "AED"
            }
            "US" -> {
                currency_code = "USD"
            }
        }
      //  Log.e("ISO", "iso3:" + currency_code)

        return currency_code

    }

    fun firstLetterCaps(activity: Activity, value: String?): String? {
        return if (!value.isNullOrEmpty()) {
            try {
                value.split(' ').joinToString(" ") { it.capitalize() }
            } catch (e: Exception) {
                value
            }
        } else {
            ""
        }
    }

}


