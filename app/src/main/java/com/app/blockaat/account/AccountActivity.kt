package com.app.blockaat.account

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat

import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import com.app.blockaat.BuildConfig
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.app.blockaat.R
import com.app.blockaat.address.AddressListingActivity
import com.app.blockaat.changestores.ChangeStoresActivity
import com.app.blockaat.cms.CmsActivity
import com.app.blockaat.contactus.ContactUsActivity
import com.app.blockaat.faq.FaqActivity
import com.app.blockaat.faq.fragment.FaqFragment
import com.app.blockaat.helper.*
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.orders.OrderListingActivity
import com.app.blockaat.search.SearchActivity
import com.app.blockaat.wishlist.WishlistActivity

import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.txtSignIn
import kotlinx.android.synthetic.main.activity_account.txtUserName
import kotlinx.android.synthetic.main.activity_account.txtVersion
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.imgSearch
import kotlinx.android.synthetic.main.toolbar_default.ivBackArrow
import kotlinx.android.synthetic.main.toolbar_default.relWishlistImage
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_default.txtWishlistCount
import kotlinx.android.synthetic.main.toolbar_default.view
import kotlinx.android.synthetic.main.toolbar_default.viewStart
import kotlinx.android.synthetic.main.toolbar_default_main.*
import java.util.*

class AccountActivity : BaseActivity() {

    private val EDIT_PROFILE_REQUEST: Int = 100
    private val EDIT_PROFILE_RESULT: Int = 101
    private lateinit var productsDBHelper: DBHelper
    private var mToolbar: Toolbar? = null
    private var strPhone = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_account)
        initializeToolbar()
        init()
        setOnClickListener()
        setFont()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        viewStart.visibility = VISIBLE
        txtHead.text = resources.getString(R.string.my_account)

        if (Global.isUserLoggedIn(this)) {
            relWishlistImage.visibility = View.VISIBLE
            imgSearch.visibility = View.VISIBLE
        } else {
            relWishlistImage.visibility = View.GONE
            imgSearch.visibility = View.GONE
        }

        updateCounts()
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


    private fun init() {
        if (!Global.isEnglishLanguage(this@AccountActivity)) {
//            imgEdit.rotationY = 180f
        }
        productsDBHelper = DBHelper(this@AccountActivity)
        strPhone = Global.strSupportPhone

        val pInfo: PackageInfo? = this.packageManager.getPackageInfo(packageName, 0)
        val verName: String = pInfo!!.versionName
        txtVersion.text = "Version" + " " + verName


    }

    private fun setUserInfo() {

        switchNotification.isChecked = NotificationManagerCompat.from(this@AccountActivity).areNotificationsEnabled()

        if (Global.getStringFromSharedPref(this, Constants.PREFS_isUSER_LOGGED_IN).equals("yes", true)) {
            txtUserName.text = Global.getStringFromSharedPref(this, Constants.PREFS_USER_FIRST_NAME)
            Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME)
            txtUserName.visibility = View.VISIBLE
            lnrOrders.visibility = View.VISIBLE
            txtSignIn.visibility = View.GONE
            txtLogoutNote.visibility = View.VISIBLE
            relDetails.visibility = View.VISIBLE
            relShipping.visibility = View.VISIBLE
        } else {
            txtUserName.visibility = View.GONE
            lnrOrders.visibility = View.GONE
            txtSignIn.visibility = View.VISIBLE
            txtLogoutNote.visibility = View.GONE
            relDetails.visibility = View.GONE
            relShipping.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        setUserInfo()
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        txtSignIn.setOnClickListener {
            val i = Intent(this@AccountActivity, LoginActivity::class.java)
            startActivity(i)
        }

        txtLogoutNote.setOnClickListener {
            showLogoutAlert()
        }

        relWishlistImage.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@AccountActivity, WishlistActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@AccountActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        imgSearch.setOnClickListener {
            val intent = Intent(this@AccountActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        relTnc.setOnClickListener {
            val intent = Intent(this@AccountActivity, CmsActivity::class.java)
            intent.putExtra("id", "2")
            intent.putExtra("header", resources.getString(R.string.tnc_caps))
            startActivity(intent)
        }

        relPrivacy.setOnClickListener {
            val intent = Intent(this@AccountActivity, CmsActivity::class.java)
            intent.putExtra("id", "6")
            intent.putExtra("header", resources.getString(R.string.privacy_policy))
            startActivity(intent)
        }

        lnrOrders.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@AccountActivity, OrderListingActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@AccountActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relDetails.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@AccountActivity, AccountInfoActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@AccountActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relChangeCountry.setOnClickListener {
            val i = Intent(this@AccountActivity, ChangeCountryActivity::class.java)
            startActivity(i)
        }

        txtUserName.setOnClickListener {
            val intent = Intent(this@AccountActivity, AccountInfoActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        txtSignIn.setOnClickListener {
            val i = Intent(this@AccountActivity, LoginActivity::class.java)
            startActivityForResult(i, 1)
        }

        relShipping.setOnClickListener {
            if (Global.isUserLoggedIn(this)) {
                val i = Intent(this@AccountActivity, AddressListingActivity::class.java)
                startActivity(i)
            } else {
                val i = Intent(this@AccountActivity, LoginActivity::class.java)
                startActivityForResult(i, 1)
            }
        }

        relAbout.setOnClickListener {
            val intent = Intent(this@AccountActivity, CmsActivity::class.java)
            intent.putExtra("id", "1")
            intent.putExtra("header", resources.getString(R.string.about_us))
            startActivity(intent)
        }

        relChangelanguage.setOnClickListener {
            showLanguageAlert()
        }

        relFaq.setOnClickListener {
            val i = Intent(this@AccountActivity, FaqActivity::class.java)
            startActivity(i)
        }

        relEmail.setOnClickListener {
            sendEmail()
        }

        relCall.setOnClickListener {
            Global.showAlert(this,
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
                    .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
            }
        }

        /*
          lnrWishlist.setOnClickListener {
             if (Global.isUserLoggedIn(this)) {
                 val i = Intent(this@AccountActivity, WishlistActivity::class.java)
                 startActivity(i)
             } else {
                 val i = Intent(this@AccountActivity, LoginActivity::class.java)
                 startActivityForResult(i, 1)
             }
         }
         lnrContact.setOnClickListener {
             val i = Intent(this@AccountActivity, ContactUsActivity::class.java)
             startActivity(i)
         }
          lnrAboutOrder.setOnClickListener {
           val intent = Intent(this@AccountActivity, CmsActivity::class.java)
           intent.putExtra("id", "5")
           intent.putExtra("header", resources.getString(R.string.about_orders))
           startActivity(intent)
       }
       lnrReturn.setOnClickListener {
              val intent = Intent(this@AccountActivity, CmsActivity::class.java)
              intent.putExtra("id", "4")
              intent.putExtra("header", resources.getString(R.string.refund_note))
              startActivity(intent)
          }
          lnrDelivery.setOnClickListener {
              val intent = Intent(this@AccountActivity, CmsActivity::class.java)
              intent.putExtra("id", "6")
              intent.putExtra("header", resources.getString(R.string.delivery_policy))
              startActivity(intent)
          }


         lnrLanguage.setOnClickListener {
             showLanguageAlert()
         }*/

/*
        lnrFollowOnInstagram.setOnClickListener {
            val uri = Uri.parse("http://instagram.com/_u/xxx")
            val insta = Intent(Intent.ACTION_VIEW, uri)
            insta.setPackage("com.instagram.android")

            if (isIntentAvailable(this@AccountActivity, insta)) {
                startActivity(insta)
            } else {
                val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
                intent.putExtra("text_header", resources.getString(R.string.instagram))
                intent.putExtra("strUrl", "https://www.instagram.com")
                startActivity(intent)
            }
        }
*/

        /* lnrShare.setOnClickListener {
             val shareIntent = ShareCompat.IntentBuilder
                 .from(this@AccountActivity)
                 .setType("text/plain")
                 .setChooserTitle("")
                 .setText(getString(R.string.share_app_msg) + "https://play.google.com/store/apps/details?id=" + packageName)
                 .intent

             if (shareIntent.resolveActivity(this@AccountActivity.packageManager) != null) {
                 this@AccountActivity.startActivity(shareIntent)
             }
         }

         lnrRate.setOnClickListener {
             val dialog = Dialog(this@AccountActivity)
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
         }*/

        /*     lnrChat.setOnClickListener {
                 if (Global.getStringFromSharedPref(this, Constants.PREFS_isUSER_LOGGED_IN).equals("yes", true)) {
                     val visitorData = VisitorInfo.Builder()
                         .name(Global.getStringFromSharedPref(this@AccountActivity, Constants.PREFS_USER_FIRST_NAME) + " " + Global.getStringFromSharedPref(this@AccountActivity, Constants.PREFS_USER_LAST_NAME))
                         .email(Global.getStringFromSharedPref(this@AccountActivity, Constants.PREFS_USER_EMAIL))
                         .phoneNumber(Global.getStringFromSharedPref(this@AccountActivity, Constants.PREFS_USER_PHONE))
                         .build()
                     ZopimChat.setVisitorInfo(visitorData)

                     startActivity(Intent(applicationContext, LiveChatActivity::class.java))
                 } else {
                     val i = Intent(this@AccountActivity, LoginActivity::class.java)
                     startActivityForResult(i, 1)
                 }
             }*/
        /*ivInstagram.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.instagram))
            intent.putExtra("strUrl", Global.INSTAGRAM_LINK)
            startActivity(intent)
        }

        ivFaceBook.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.facebook))
            intent.putExtra("strUrl", Global.FACEBOOK_LINK)
            startActivity(intent)
        }

        ivTwitter.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.twitter))
            intent.putExtra("strUrl", Global.TWITTER_LINK)
            startActivity(intent)
        }

        ivYoutube.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.youtube))
            intent.putExtra("strUrl", Global.YOUTUBE_LINK)
            startActivity(intent)
        }

        ivGooglePlus.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.google_plus))
            intent.putExtra("strUrl", Global.GOOGLE_PLUS_LINK)
            startActivity(intent)
        }

        ivSnapchat.setOnClickListener {
            val intent = Intent(this@AccountActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.snapchat))
            intent.putExtra("strUrl", Global.SNAPCHAT_LINK)
            startActivity(intent)
        }*/
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

    private fun setFont() {
        //btnLogout.typeface = Global.fontBtn
        txtHead.typeface = Global.fontNavBar
        txtUserName.typeface = Global.fontBold
        txtOrdersNote.typeface = Global.fontMedium
        txtorderHistory.typeface = Global.fontRegular
        txtUserInfo.typeface = Global.fontNavBar
        txtdetail.typeface = Global.fontMedium
        txtAddressNote.typeface = Global.fontMedium
        txtPushNotification.typeface = Global.fontMedium
        txtChangeCountry.typeface = Global.fontMedium
        txtChangelanguage.typeface = Global.fontMedium
        txtAboutNote.typeface = Global.fontMedium
        txtTncNote.typeface = Global.fontMedium
        txtPrivacy.typeface = Global.fontMedium
        txtFaqs.typeface = Global.fontMedium
        txtCallNote.typeface = Global.fontMedium
        txtEmail.typeface = Global.fontMedium
        txtLogout.typeface = Global.fontMedium
        txtVersion.typeface = Global.fontTabBar
        txtNote.typeface = Global.fontNavBar
        txtSignIn.typeface = Global.fontBtn

        //txtChatNote.typeface = Global.fontRegular
        //txtLanguageNote.typeface = Global.fontRegular
        /* txtWishNote.typeface = Global.fontSemiBold
         txtContactNote.typeface = Global.fontSemiBold
         txtRateNote.typeface = Global.fontSemiBold
         txtReturnNote.typeface = Global.fontSemiBold
         txtDeliveryNote.typeface = Global.fontSemiBold
         txtAboutOrderNote.typeface = Global.fontSemiBold
         txtShareNote.typeface = Global.fontSemiBold
         txtFollowUs.typeface = Global.fontMedium */
    }


    private fun onCallBtnClick() {
        if (Build.VERSION.SDK_INT < 23) {
            phoneCall()
        } else {

            if (ActivityCompat.checkSelfPermission(
                    this@AccountActivity,
                    Manifest.permission.CALL_PHONE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                phoneCall()
            } else {
                val permissionsStorage = arrayOf<String>(Manifest.permission.CALL_PHONE)
                //Asking request Permissions
                ActivityCompat.requestPermissions(this@AccountActivity, permissionsStorage, 1200)
            }
        }
    }

    private fun phoneCall() {
        if (ActivityCompat.checkSelfPermission(
                this@AccountActivity,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:" + strPhone)
            startActivity(callIntent)
        } else {
            Toast.makeText(
                this@AccountActivity,
                getString(R.string.call_permission_note),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /////show alert
    private fun showLogoutAlert() {
        try {
            Global.showAlert(this,
                "",
                resources.getString(R.string.logout_alert),
                resources.getString(R.string.yes),
                resources.getString(R.string.no),
                false,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {
                        Global.showSnackbar(
                            this@AccountActivity,
                            resources.getString(R.string.logout_success_msg)
                        )
                        val strLanguage =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_LANGUAGE
                            )
                        val strCurrencyEN = Global.getStringFromSharedPref(
                            this@AccountActivity,
                            Constants.PREFS_CURRENCY_EN
                        )
                        val strCurrencyAR = Global.getStringFromSharedPref(
                            this@AccountActivity,
                            Constants.PREFS_CURRENCY_AR
                        )
                        val strCountryEn =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_COUNTRY_EN
                            )
                        val strCountryAr =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_COUNTRY_AR
                            )
                        val strStoreCode =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_STORE_CODE
                            )
                        val deviceMultiplier =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_DEVIDE_MULTIPLIER
                            )
                        val strSelectedCategory =
                            Global.getStringFromSharedPref(
                                this@AccountActivity,
                                Constants.PREFS_CATEGORY
                            )
                        SharedPreferencesHelper.clearSharedPreferences()
                        Global.saveStringInSharedPref(
                            this@AccountActivity,
                            Constants.PREFS_LANGUAGE,
                            strLanguage
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_STORE_CODE,
                            strStoreCode
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_EN,
                            strCurrencyEN
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_CURRENCY_AR,
                            strCurrencyAR
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_CATEGORY,
                            strSelectedCategory
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_EN,
                            strCountryEn
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
                            Constants.PREFS_COUNTRY_AR,
                            strCountryAr
                        )
                        Global.saveStringInSharedPref(
                            this@AccountActivity as AppCompatActivity,
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
                    }

                    override fun onNoClick() {
                    }

                }
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /////////
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 1) {
            Global.showSnackbar(
                this@AccountActivity,
                resources.getString(R.string.user_success_msg)
            )
            setUserInfo()
        } else if (requestCode == EDIT_PROFILE_REQUEST && resultCode == EDIT_PROFILE_RESULT) {
            if (data?.hasExtra("msg")!!) {
                Global.showSnackbar(
                    this@AccountActivity,
                    data?.extras?.getString("msg").toString()
                )
                setUserInfo()
            }
        }
    }

}