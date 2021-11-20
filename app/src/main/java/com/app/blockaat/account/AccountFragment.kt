package com.app.blockaat.account

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.app.blockaat.BuildConfig
import com.app.blockaat.R
import com.app.blockaat.address.AddressListingActivity
import com.app.blockaat.cms.CmsActivity
import com.app.blockaat.faq.FaqActivity
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.orders.OrderListingActivity
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.lnrOrders
import kotlinx.android.synthetic.main.fragment_account.relAbout
import kotlinx.android.synthetic.main.fragment_account.relCall
import kotlinx.android.synthetic.main.fragment_account.relChangeCountry
import kotlinx.android.synthetic.main.fragment_account.relChangelanguage
import kotlinx.android.synthetic.main.fragment_account.relDetails
import kotlinx.android.synthetic.main.fragment_account.relEmail
import kotlinx.android.synthetic.main.fragment_account.relFaq
import kotlinx.android.synthetic.main.fragment_account.relPrivacy
import kotlinx.android.synthetic.main.fragment_account.relShipping
import kotlinx.android.synthetic.main.fragment_account.relTnc
import kotlinx.android.synthetic.main.fragment_account.switchNotification
import kotlinx.android.synthetic.main.fragment_account.txtAboutNote
import kotlinx.android.synthetic.main.fragment_account.txtAddressNote
import kotlinx.android.synthetic.main.fragment_account.txtCallNote
import kotlinx.android.synthetic.main.fragment_account.txtChangeCountry
import kotlinx.android.synthetic.main.fragment_account.txtChangelanguage
import kotlinx.android.synthetic.main.fragment_account.txtEmail
import kotlinx.android.synthetic.main.fragment_account.txtFaqs
import kotlinx.android.synthetic.main.fragment_account.txtLanguage
import kotlinx.android.synthetic.main.fragment_account.txtLogoutNote
import kotlinx.android.synthetic.main.fragment_account.txtNote
import kotlinx.android.synthetic.main.fragment_account.txtOrdersNote
import kotlinx.android.synthetic.main.fragment_account.txtPrivacy
import kotlinx.android.synthetic.main.fragment_account.txtPushNotification
import kotlinx.android.synthetic.main.fragment_account.txtSignIn
import kotlinx.android.synthetic.main.fragment_account.txtTncNote
import kotlinx.android.synthetic.main.fragment_account.txtUserInfo
import kotlinx.android.synthetic.main.fragment_account.txtUserName
import kotlinx.android.synthetic.main.fragment_account.txtVersion
import kotlinx.android.synthetic.main.fragment_account.txtdetail
import kotlinx.android.synthetic.main.fragment_account.txtorderHistory
import kotlinx.android.synthetic.main.fragment_account.viewNotificationSwitch

class AccountFragment : Fragment() {

    private lateinit var mActivity: NavigationActivity
    private val EDIT_PROFILE_REQUEST: Int = 100
    private val EDIT_PROFILE_RESULT: Int = 101
    private lateinit var productsDBHelper: DBHelper
    private var strPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity

        CustomEvents.screenViewed(activity as NavigationActivity,getString(R.string.account_screen))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        setOnClickListener()
        setFont()
    }

    private fun init() {
        productsDBHelper = DBHelper(mActivity)
        strPhone = Global.strSupportPhone
        val pInfo: PackageInfo? = mActivity.packageManager.getPackageInfo(mActivity.packageName, 0)
        val verName: String = pInfo!!.versionName
        txtVersion.text = resources.getString(R.string.version_name, verName)


        txtLanguage.text = resources.getString(R.string.current_lang)
        if (!Global.isEnglishLanguage(mActivity)) {
            ivArrowDetail.rotation = 180f
            ivArrowAddress.rotation = 180f
            ivArrowNotification.rotation = 180f
            ivArrowChangeCountry.rotation = 180f
            ivArrowChangeLang.rotation = 180f
            ivArrowAbout.rotation = 180f
            ivArrowTandC.rotation = 180f
            ivArrowFaqs.rotation = 180f
            ivArrowPrivacy.rotation = 180f
            ivArrowCall.rotation = 180f
            ivArrowEmail.rotation = 180f
            imgTruck.setImageDrawable(resources.getDrawable(R.drawable.ic_truck));

        }
        //updateCounts()
    }

    private fun setOnClickListener() {
        txtSignIn.setOnClickListener {
            val i = Intent(mActivity, LoginActivity::class.java)
            startActivity(i)
        }

        txtLogoutNote.setOnClickListener {
            showLogoutAlert()
        }

        /*relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(mActivity)) {
                val i = Intent(mActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(mActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }*/

        /*imgSearch.setOnClickListener {
            val intent = Intent(mActivity, SearchActivity::class.java)
            startActivity(intent)
        }*/

        relTnc.setOnClickListener {
            val intent = Intent(mActivity, CmsActivity::class.java)
            intent.putExtra("id", "2")
            intent.putExtra("header", resources.getString(R.string.tnc_caps))
            startActivity(intent)
        }

        lnrOrders.setOnClickListener {
            if (Global.isUserLoggedIn(mActivity)) {
                val i = Intent(mActivity, OrderListingActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(mActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relDetails.setOnClickListener {
            if (Global.isUserLoggedIn(mActivity)) {
                val i = Intent(mActivity, AccountInfoActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(mActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relChangeCountry.setOnClickListener {
            val i = Intent(mActivity, ChangeCountryActivity::class.java)
            startActivity(i)
        }

        txtUserName.setOnClickListener {
            val intent = Intent(mActivity, AccountInfoActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        txtSignIn.setOnClickListener {
            val i = Intent(mActivity, LoginActivity::class.java)
            startActivityForResult(i, 1)
        }

        relShipping.setOnClickListener {
            if (Global.isUserLoggedIn(mActivity)) {
                val i = Intent(mActivity, AddressListingActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(mActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relAbout.setOnClickListener {
            val intent = Intent(mActivity, CmsActivity::class.java)
            intent.putExtra("id", "1")
            intent.putExtra("header", resources.getString(R.string.about_us))
            startActivity(intent)
        }

        relChangelanguage.setOnClickListener {
            showLanguageAlert()
        }

        relFaq.setOnClickListener {
            val i = Intent(mActivity, FaqActivity::class.java)
            startActivity(i)
        }

        relEmail.setOnClickListener {
            sendEmail()
        }

        relPrivacy.setOnClickListener {
            val intent = Intent(mActivity, CmsActivity::class.java)
            intent.putExtra("id", "6")
            intent.putExtra("header", resources.getString(R.string.privacy_policy))
            startActivity(intent)
        }

        relCall.setOnClickListener {
            Global.showAlert(mActivity,
                "",
                resources.getString(R.string.call) + " " + strPhone,
                resources.getString(R.string.yes),
                resources.getString(R.string.cancel),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        onCallBtnClick()
                    }

                    override fun onNoClick() {
                    }

                })
        }

        viewNotificationSwitch.setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, mActivity.packageName)
                startActivity(intent)
            }
        }
    }

    private fun setFont() {
        //btnLogout.typeface = Global.fontBtn
        //txtHead.typeface = Global.fontNavBar
        txtUserName.typeface = Global.fontBold
        txtOrdersNote.typeface = Global.fontMedium
        txtorderHistory.typeface = Global.fontRegular
        txtUserInfo.typeface = Global.fontNavBar
        txtdetail.typeface = Global.fontMedium
        txtAddressNote.typeface = Global.fontMedium
        txtPushNotification.typeface = Global.fontMedium
        txtChangeCountry.typeface = Global.fontMedium
        txtChangelanguage.typeface = Global.fontMedium
        txtLanguage.typeface = Global.fontRegular
        txtAboutNote.typeface = Global.fontMedium
        txtTncNote.typeface = Global.fontMedium
        txtPrivacy.typeface = Global.fontMedium
        txtFaqs.typeface = Global.fontMedium
        txtCallNote.typeface = Global.fontMedium
        txtEmail.typeface = Global.fontMedium
        txtLogout1.typeface = Global.fontMedium
        txtVersion.typeface = Global.fontTabBar
        txtNote.typeface = Global.fontNavBar
        txtSignIn.typeface = Global.fontBtn
    }

    private fun isIntentAvailable(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
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

    private fun onCallBtnClick() {
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall()
        } else {

            if (ActivityCompat.checkSelfPermission(
                    mActivity,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                phoneCall()
            } else {
                val permissionsStorage = arrayOf<String>(Manifest.permission.CALL_PHONE)
                //Asking request Permissions
                ActivityCompat.requestPermissions(mActivity, permissionsStorage, 1200)
            }
        }
    }

    private fun phoneCall() {
        if (ActivityCompat.checkSelfPermission(
                mActivity,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + strPhone)
            startActivity(callIntent)
        } else {
            Toast.makeText(
                mActivity,
                getString(R.string.call_permission_note),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /////show alert
    fun showLogoutAlert() {

        try {
            Global.showAlert(
                mActivity,
                "",
                resources.getString(R.string.logout_alert),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        Global.showSnackbar(
                            this@AccountFragment.mActivity,
                            resources.getString(R.string.logout_success_msg)
                        )
                        val strLanguage =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_LANGUAGE
                            )
                        val strCurrencyEN = Global.getStringFromSharedPref(
                            this@AccountFragment.mActivity,
                            Constants.PREFS_CURRENCY_EN
                        )
                        val strCurrencyAR = Global.getStringFromSharedPref(
                            this@AccountFragment.mActivity,
                            Constants.PREFS_CURRENCY_AR
                        )
                        val strCountryEn =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_COUNTRY_EN
                            )
                        val strCountryAr =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_COUNTRY_AR
                            )
                        val strStoreCode =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_STORE_CODE
                            )
                        val deviceMultiplier =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_DEVIDE_MULTIPLIER
                            )
                        val strSelectedCategory =
                            Global.getStringFromSharedPref(
                                this@AccountFragment.mActivity,
                                Constants.PREFS_CATEGORY
                            )
                        SharedPreferencesHelper.clearSharedPreferences()
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity,
                            Constants.PREFS_LANGUAGE,
                            strLanguage
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_STORE_CODE,
                            strStoreCode
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_EN,
                            strCurrencyEN
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_AR,
                            strCurrencyAR
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_CATEGORY,
                            strSelectedCategory
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_EN,
                            strCountryEn
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_AR,
                            strCountryAr
                        )
                        Global.saveStringInSharedPref(
                            this@AccountFragment.mActivity as AppCompatActivity,
                            Constants.PREFS_DEVIDE_MULTIPLIER,
                            deviceMultiplier
                        )

                        productsDBHelper.deleteTable("table_cart")
                        productsDBHelper.deleteTable("table_wishlist")

                        LoginManager.getInstance().logOut()
                        setUserInfo()
                    }

                    override fun onNoClick() {
                    }

                })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /////show language alert
    private fun showLanguageAlert() {
        try {
            Global.showLanguageAlert(
                mActivity,
                "",
                resources.getString(R.string.alert_language),
                resources.getString(R.string.yes_lang),
                resources.getString(R.string.no_lang),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        AppController.instance.changeLanguage()
                        val intent = Intent(mActivity, NavigationActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        mActivity.finish()
                    }

                    override fun onNoClick() {
                    }

                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*fun updateCounts() {
        if (Global.isUserLoggedIn(mActivity)) {
            // txtWishlistCount.visibility = View.GONE
            if (Global.getTotalWishListProductCount(mActivity) > 0) {
                txtWishlistCount.visibility = View.VISIBLE
                txtWishlistCount.text =
                    Global.getTotalWishListProductCount(mActivity).toString()
            } else {
                txtWishlistCount.visibility = View.GONE
            }
        } else {
            txtWishlistCount.visibility = View.GONE
        }

    }*/

    private fun setUserInfo() {

        switchNotification.isChecked =
            NotificationManagerCompat.from(mActivity).areNotificationsEnabled()
        println("Country_URL "+ Global.getStringFromSharedPref(mActivity, Constants.PREFS_COUNTRY_FLAG))

        Global.loadImagesUsingGlide(
            mActivity,
            Global.getStringFromSharedPref(mActivity, Constants.PREFS_COUNTRY_FLAG),
            imgCountryFlag
        )
        if (Global.getStringFromSharedPref(mActivity, Constants.PREFS_isUSER_LOGGED_IN)
                .equals("yes", true)
        ) {
            txtUserName.text =
                Global.getStringFromSharedPref(mActivity, Constants.PREFS_USER_FIRST_NAME)
            Global.getStringFromSharedPref(mActivity, Constants.PREFS_USER_LAST_NAME)
            txtUserName.visibility = View.VISIBLE
            lnrOrders.visibility = View.VISIBLE
            txtSignIn.visibility = View.GONE
            txtLogoutNote.visibility = View.VISIBLE
            relDetails.visibility = View.VISIBLE
            relShipping.visibility = View.VISIBLE
            mActivity.userLoginUpdate(true)
        } else {
            txtUserName.visibility = View.GONE
            lnrOrders.visibility = View.GONE
            txtSignIn.visibility = View.VISIBLE
            txtLogoutNote.visibility = View.GONE
            relDetails.visibility = View.GONE
            relShipping.visibility = View.GONE
            mActivity.userLoginUpdate(false)
        }
    }

    override fun onResume() {
        super.onResume()
        setUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            Global.showSnackbar(
                mActivity,
                resources.getString(R.string.user_success_msg)
            )
            setUserInfo()
        } else if (requestCode == EDIT_PROFILE_REQUEST && resultCode == EDIT_PROFILE_RESULT) {
            if (data?.hasExtra("msg")!!) {
                Global.showSnackbar(
                    mActivity,
                    data?.extras?.getString("msg").toString()
                )
                setUserInfo()
            }
        }
    }

}