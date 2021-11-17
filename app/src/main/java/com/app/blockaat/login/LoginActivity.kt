package com.app.blockaat.login

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.app.blockaat.R
import com.app.blockaat.cart.model.AddCartRequestApi
import com.app.blockaat.helper.*
import com.app.blockaat.login.model.LoginDataModel
import com.app.blockaat.register.RegisterActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_guestlogin.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnFbLogin
import kotlinx.android.synthetic.main.activity_login.edtEmail
import kotlinx.android.synthetic.main.activity_login.edtPassword
import kotlinx.android.synthetic.main.activity_login.ivClose
import kotlinx.android.synthetic.main.activity_login.ivLogo
import kotlinx.android.synthetic.main.activity_login.txtLogin
import kotlinx.android.synthetic.main.activity_login.txtLoginWith
import kotlinx.android.synthetic.main.activity_login.txtRegister
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONException
import java.util.*

class LoginActivity : BaseActivity() {

    private var mToolbar: Toolbar? = null
    private var strEmail = ""
    private var strPass = ""
    private lateinit var progressDialog: Dialog
    private var isFromMyAccount: Boolean = false
    private var isFromCart: Boolean = false
    private var isFromProducts: Boolean = false
    private lateinit var productsDBHelper: DBHelper
    private var disposable: Disposable? = null
    private var callbackManager: CallbackManager? = null
    private var strSocialName: String? = ""
    private var strFbgender: String? = ""
    private var strSocialEmail: String? = ""
    private var strSocialLname: String = ""
    private var strFacebook_id: String? = ""
    private var strGender: String? = null
    private var strDOB: String? = null
    private var strfrom: String? = null
    private var type: Boolean = false
    private var strSocialType: String? = null
    private val PRODUCT_REQUEST = 1000

    //  private var dialog: ProgressDialog? = null
    private var LOGINREQUEST = 2000
    private var isSocial = false
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN: Int = 3000
    private var dialog: CustomProgressBar? = null
    private var isFromNav = false
    private var isFromProductDetails = false
    private var emails: String = ""
    private var guestEmail: String? = null
    private lateinit var guestEmailID: String
    private lateinit var mTracker: Tracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application: AppController = application as AppController
        mTracker = application.getDefaultTracker()!!
        mTracker.setScreenName("Login Screen")
        mTracker.send(HitBuilders.ScreenViewBuilder().build())
        setContentView(R.layout.activity_login)
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        initializeToolbar()
        init()
        setFont()
        onClickListener()
        setHints()
    }

    private fun setHints() {
    }


    private fun onClickListener() {
        txtLogin.setOnClickListener {
            //Global.hideKeyboard(this)
            validation()
        }


        txtFrgtPass.setOnClickListener {

            showForgotPassword()

        }

        txtRegisterGuestUser.setOnClickListener {

            showGuestLogin()

        }

        txtRegister.setOnClickListener {
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            i.putExtra("isFromLogin", true)
            i.putExtra("isFromNav", if (isFromNav) "yes" else "no")
            i.putExtra("isFromProductDetails", if (isFromProductDetails) "yes" else "no")
            startActivityForResult(i, LOGINREQUEST)
            finish()
        }

        flFaceBook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email")
            )
            btnFbLogin.setReadPermissions(listOf("email"))
        }

        flGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        ivClose.setOnClickListener {
            onBackPressed()
        }

    }

    private fun showForgotPassword() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.activity_frgtpass)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window?.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        val edtEmails = dialog.findViewById<EditText>(R.id.edtEmails)
        val txtHead = dialog.findViewById<TextView>(R.id.txtHead)
        val txtNote = dialog.findViewById<TextView>(R.id.txtNote)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val txtResetPass = dialog.findViewById<TextView>(R.id.txtResetPass)

        txtHead.typeface = Global.fontBold
        txtNote.typeface = Global.fontRegular
        edtEmails.typeface = Global.fontRegular
        txtResetPass.typeface = Global.fontBtn

        txtHead.text = resources.getString(R.string.forgot_pass)

        txtResetPass.setOnClickListener {
            //Global.hideKeyboard(this)
            if (edtEmails!!.text.isEmpty()) {
                Global.showSnackbar(this@LoginActivity, getString(R.string.empty_email))
            } else if (!Global.isValidEmail(edtEmails.text)) {
                Global.showSnackbar(
                    this@LoginActivity,
                    getString(R.string.please_enter_valid_email_id)
                )
            } else {
                emails = edtEmails!!.text.toString()
                postForgotPassword()
            }
        }
        ivClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showGuestLogin() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.activity_guestlogin)
        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        dialog.window?.attributes = lp
        dialog.setCanceledOnTouchOutside(false)
        val edtGuestEmails = dialog.findViewById<EditText>(R.id.edtGuestEmails)
        val txtHead = dialog.findViewById<TextView>(R.id.txtHead)
        val txtNote = dialog.findViewById<TextView>(R.id.txtNote)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val txtSubmitGuest = dialog.findViewById<TextView>(R.id.txtSubmitGuest)

        txtHead.typeface = Global.fontBold
        txtNote.typeface = Global.fontRegular
        edtGuestEmails.typeface = Global.fontRegular
        txtSubmitGuest.typeface = Global.fontBtn

        txtHead.text = resources.getString(R.string.guest_login)

        txtSubmitGuest.setOnClickListener {
            Global.hideKeyboard(this)
            if (edtGuestEmails!!.text.isEmpty()) {
                Global.showSnackbar(this@LoginActivity, getString(R.string.empty_email))
            } else if (!Global.isValidEmail(edtGuestEmails.text)) {
                Global.showSnackbar(
                    this@LoginActivity,
                    getString(R.string.please_enter_valid_email_id)
                )
            } else {
                // Call API Here
                guestEmailID = edtGuestEmails!!.text.toString()
                guestLoginApi()
            }
        }
        ivClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun initializeToolbar() {

    }

    private fun init() {
        dialog = CustomProgressBar(this)

        if (!Global.isEnglishLanguage(this@LoginActivity)) {
            ivLogo.setImageResource(R.drawable.app_logo_ar)
        }else{
            ivLogo.setImageResource(R.drawable.app_logo)

        }

//        ivPassword.setColorFilter(resources.getColor(R.color.icon_color))
//        ivEmail.setColorFilter(resources.getColor(R.color.icon_color))

        if (intent.hasExtra("isFromNav")) {
            isFromNav = true
        }
        if (intent.hasExtra("isFromProductDetails")) {
            isFromProductDetails = true
        }

        productsDBHelper = DBHelper(this@LoginActivity)

        if (intent.hasExtra("isFromCartPageToLogin") && intent.getStringExtra("isFromCartPageToLogin") == "yes") {
            isFromCart = true
        }

        if (intent.hasExtra("isFromMyAccount") && intent.getStringExtra("isFromMyAccount") == "yes") {
            isFromMyAccount = true
        }

        if (intent.hasExtra("isFromCartPageToLogin") && intent.getStringExtra("isFromCartPageToLogin") == "yes") {
            isFromProducts = true
            txtRegisterGuestUser.visibility = View.VISIBLE
        } else {
            txtRegisterGuestUser.visibility = View.GONE

        }


//        txtNewUser.paintFlags = txtRegister.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txtFrgtPass.paintFlags = txtFrgtPass.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        initializeGoogle()
        ///Facebook
        // btnFbLogin!!.setReadPermissions(Arrays.asList("email, user_birthday"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {

                    val accessToken = AccessToken.getCurrentAccessToken()
                    val request = GraphRequest.newMeRequest(accessToken) { `object`, response ->
                        // TODO Auto-generated method stub
                        // Log.v("LoginActivity", response.toString());
                        /* println("===================JSON++" + `object`)*/
                        try {
                            strSocialName = `object`.getString("name")
                            if (`object`.has("last_name")) {
                                strSocialLname = `object`.getString("last_name")
                            }
                            strFacebook_id = `object`.getString("id")
                            if (`object`.has("email")) {
                                strSocialEmail = `object`.getString("email")
                            }


                            if (strFbgender == "male") {
                                strGender = "M"
                            } else if (strFbgender == "female") {
                                strGender = "F"
                            }
                            if (`object`.has("birthday")) {
                                strDOB = `object`.getString("birthday")
                            }
                            if (android.os.Build.VERSION.SDK_INT > 9) {
                                try {
                                    val policy =
                                        StrictMode.ThreadPolicy.Builder().permitAll().build()
                                    StrictMode.setThreadPolicy(policy)
                                } catch (e: Exception) {

                                }
                            }

                            // Log.d("bittt", bitmap.toString());
                            strfrom = "facebook"
                            type = false
                            strSocialType = "F"
                            if (`object`.has("email")) {

                                socialRegisterApi()
                            } else {
                                LoginManager.getInstance().logOut()
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.email_not_found)
                                )
                            }


                        } catch (e: JSONException) {
                            // TODO Auto-generated catch block
                            e.printStackTrace()

                            val preferences = getSharedPreferences("login", 0)
                            val editor = preferences.edit()
                            editor.clear()
                            editor.commit()
                            Global.showSnackbar(
                                this@LoginActivity,
                                resources.getString(R.string.email_not_found)
                            )
                        }
                    }

                    val parameters = Bundle()
//                    parameters.putString("fields", "id,name,link,email,picture,gender, birthday")
                    parameters.putString(
                        "fields",
                        "id, name, email, gender, birthday, first_name, last_name"
                    )
                    request.parameters = parameters
                    request.executeAsync()
                }


                override fun onCancel() {
                    Global.showSnackbar(
                        this@LoginActivity,
                        resources.getString(R.string.login_cancelled)
                    )
                }

                override fun onError(e: FacebookException) {
                 //   Log.e("FB_ERROR", "facebook error:" + e.localizedMessage)
                    Global.showSnackbar(
                        this@LoginActivity,
                        resources.getString(R.string.login_unsuccessfull)
                    )
                }
            })

    }

    private fun setFont() {
        edtEmail.typeface = Global.fontRegular
        edtPassword.typeface = Global.fontRegular
        txtPasswordShow.typeface = Global.fontRegular
        txtLogin.typeface = Global.fontBtn
        txtRegister.typeface = Global.fontBtn
        txtFrgtPass.typeface = Global.fontRegular
        txtRegisterGuestUser.typeface = Global.fontBtn
        txtLoginWith.typeface = Global.fontMedium
        txtOrDown.typeface = Global.fontMedium
        txtNewUser.typeface = Global.fontRegular

    }

    private fun validation() {
        if (edtEmail!!.text.isEmpty()) {

            Global.showSnackbar(
                this@LoginActivity,
                resources.getString(R.string.empty_email)
            )
        } else if (!Global.isValidEmail(edtEmail.text.toString())) {
            Global.showSnackbar(
                this@LoginActivity,
                resources.getString(R.string.please_enter_a_valid_email_id)
            )
        } else if (edtPassword!!.text.isEmpty()) {
            Global.showSnackbar(
                this@LoginActivity,
                resources.getString(R.string.empty_password)
            )
        } else if (edtPassword.text.length < 6) {
            Global.showSnackbar(
                this@LoginActivity,
                resources.getString(R.string.passwword_must_have_six_letters)
            )
        } else {
            loginApi()
        }
    }

    private fun initializeGoogle() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
        callbackManager = CallbackManager.Factory.create()
        setGooleSignIn(mGoogleSignInClient)
        var account = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
    }

    private fun setGooleSignIn(mGoogleSignInClient: GoogleSignInClient?) {
        this.mGoogleSignInClient = mGoogleSignInClient
    }

    @SuppressLint("CheckResult")
    private fun loginApi() {
        if (NetworkUtil.getConnectivityStatus(this@LoginActivity) != 0) {
            //loading
//            showProgressDialog(this@LoginActivity)
            dialog?.showDialog()
            Global.apiService.loginUser(
                edtEmail!!.text.toString(),
                edtPassword!!.text.toString(),
                Constants.DEVICE_TOKEN,
                Constants.DEVICE_TYPE,
                Constants.DEVICE_MODEL,
                Constants.APP_VERSION,
                Constants.OS_VERSION,
                com.app.blockaat.apimanager.WebServices.LoginWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            println("RESPONSE - Login Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                //successfully logged in
                                handleLoginResponse(result.data)
                            } else if (result.status == 201) {
                                //invalid password
                                dialog?.hideDialog()

                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.invalid_password)
                                )
                            } else if (result.status == 404) {
                                //user does not exist
                                dialog?.hideDialog()
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.user_doesn_t_exists)
                                )
                            } else {
                                //some error in response like some field missing or any other status code
                                dialog?.hideDialog()
                                Global.showSnackbar(this@LoginActivity, result.message!!)
                            }
                        } else {
                            //if ws not working
                            dialog?.hideDialog()
                            Global.showSnackbar(
                                this@LoginActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Login Ws :   " + error.localizedMessage)
                        dialog?.hideDialog()                                                             //error
                        Global.showSnackbar(this@LoginActivity, resources.getString(R.string.error))
                    }
                )
        }
    }

    private fun handleLoginResponse(loginDataModel: LoginDataModel?) {
        if (loginDataModel != null) {

            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_ID,
                loginDataModel.id.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_FIRST_NAME,
                loginDataModel.first_name.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_LAST_NAME,
                loginDataModel.last_name.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_EMAIL,
                loginDataModel.email.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_PHONE_CODE,
                loginDataModel.phone_code ?: "+965"
            )
            println("Phone code : " + loginDataModel.phone_code)
            println(
                "Phone code pref : " + Global.getStringFromSharedPref(
                    this@LoginActivity,
                    Constants.PREFS_USER_PHONE_CODE
                )
            )


            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_PHONE,
                loginDataModel.phone.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_GENDER,
                loginDataModel.gender.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_DOB,
                loginDataModel.dob.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_IMAGE,
                loginDataModel.image.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_CODE,
                loginDataModel.code.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_isPHONE_VERIFIED,
                loginDataModel.is_phone_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_isEMAIL_VERIFIED,
                loginDataModel.is_email_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_isSOCIAL_REGISTER,
                loginDataModel.is_social_register.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_SOCIAL_REGISTER_TYPE,
                loginDataModel.social_register_type.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_isUSER_LOGGED_IN,
                "yes"
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.PREFS_USER_PUSH_ENABLED,
                loginDataModel.push_enabled.toString()
            )
            Global.saveStringInSharedPref(
                this@LoginActivity,
                Constants.NEWSLETTER_SUBSCRIBED,
                loginDataModel.newsletter_subscribed.toString()
            )


            onLoginAddDBItemToWishlist()
            onLoginAddDBItemToCart(loginDataModel.id.toString())


        } else {
            //if ws not working
            dialog?.hideDialog()
            Global.showSnackbar(this@LoginActivity, resources.getString(R.string.error))
        }
    }

    private fun onLoginAddDBItemToWishlist() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
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
                            if (result.status == 200) {
                                if (result.data != null && result.data.isNotEmpty()) {
                                    for (x in 0 until result.data.size) {
                                        productsDBHelper.addProductToWishlist(
                                            ProductsDataModel(
                                                result.data[x].id.toString()
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    },
                    { error ->
                        productsDBHelper.deleteTable("table_wishlist")
                    }
                )
        }
    }

    private fun onLoginAddDBItemToCart(user_id: String) {
        val strMultipleCartEntityIDs =
            productsDBHelper.getAllCartEntityIDs().toString()
                .substring(
                    1,
                    productsDBHelper.getAllCartEntityIDs().toString().length - 1
                )
        val strMultipleCartProductQty =
            productsDBHelper.getAllCartProductQty().toString()
                .substring(
                    1,
                    productsDBHelper.getAllCartProductQty().toString().length - 1
                )

        println("HERE IS :::    $strMultipleCartEntityIDs   :   $strMultipleCartProductQty")

        if (strMultipleCartEntityIDs.isNotEmpty()) {
            if (NetworkUtil.getConnectivityStatus(this@LoginActivity) != 0) {

                val addCartObject = AddCartRequestApi(
                    Global.getStringFromSharedPref(
                        this@LoginActivity,
                        Constants.PREFS_USER_ID
                    ), strMultipleCartEntityIDs, strMultipleCartProductQty, "", ""
                )
                val disposable = Global.apiService.addToCart(
                    com.app.blockaat.apimanager.WebServices.AddToCartWs + Global.getStringFromSharedPref(
                        this@LoginActivity,
                        Constants.PREFS_LANGUAGE
                    )
                            + "&store=" + Global.getStringFromSharedPref(
                        this@LoginActivity,
                        Constants.PREFS_STORE_CODE
                    ), addCartObject
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            if (result != null) {
                                println(
                                    "RESPONSE - Add to cart Ws :   " + Gson().toJson(
                                        result.data
                                    )
                                )
                                dialog?.hideDialog()

                                val intent = Intent()
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                if (isFromCart) {
                                    intent.putExtra(
                                        "isFromCartPageToLogin",
                                        "yes"
                                    ) //if is coming from cart page -> login
                                }

                                if (isFromMyAccount) {
                                    intent.putExtra(
                                        "isFromMyAccount",
                                        "yes"
                                    ) //if is coming from my account -> login
                                }
                                setResult(1, intent)
                                finish()
                            }
                        },
                        { error ->
                            println("RESPONSE - Add to cart Ws :   " + error.localizedMessage)
                            dialog?.hideDialog()
                            if (intent.hasExtra("isFromProducts")) {
                                val intent = Intent()
                                setResult(AppCompatActivity.RESULT_OK, intent)
                                setResult(1, intent)
                                finish()

                            } else {
                                if (isFromCart) {
                                    intent.putExtra(
                                        "isFromCartPageToLogin",
                                        "yes"
                                    ) //if is coming from cart page -> login
                                }
                                if (isFromMyAccount) {
                                    intent.putExtra(
                                        "isFromMyAccount",
                                        "yes"
                                    ) //if is coming from my account -> login
                                }
                                setResult(1, intent)
                                finish()
                            }
                        }
                    )
            }
        } else {
            dialog?.hideDialog()
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            if (isFromCart) {
                intent.putExtra(
                    "isFromCartPageToLogin",
                    "yes"
                ) //if is coming from cart page -> login
            }
            if (isFromMyAccount) {
                intent.putExtra(
                    "isFromMyAccount",
                    "yes"
                ) //if is coming from my account -> login
            }
            setResult(1, intent)
            finish()
        }
    }

    @SuppressLint("CheckResult")
    private fun socialRegisterApi() {

        if (NetworkUtil.getConnectivityStatus(this@LoginActivity) != 0) {
            //loading
//            showProgressDialog(this@LoginActivity)
            dialog?.showDialog()
            Global.apiService.Socialregisteration(
                this!!.strSocialEmail!!,
                strSocialName!!,
                "",
                "",
                "",
                "",
                strSocialType!!,
                Constants.DEVICE_TOKEN,
                Constants.DEVICE_TYPE,
                Build.MODEL,
                Constants.APP_VERSION,
                Build.VERSION.RELEASE,
                strFacebook_id!!,
                "1",
                com.app.blockaat.apimanager.WebServices.SocialLoginWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            println(
                                "RESPONSE - SocialLogin Ws :   " + Gson().toJson(
                                    result
                                )
                            )
                            if (result.status == 200) {
                                //successfully logged in
                                handleLoginResponse(result.data)
                            } else if (result.status == 201) {
                                //invalid password
                                dialog?.hideDialog()
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.invalid_password)
                                )
                            } else if (result.status == 404) {
                                //user does not exist
                                dialog?.hideDialog()
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.user_doesn_t_exists)
                                )
                            } else {
                                //some error in response like some field missing or any other status code
                                dialog?.hideDialog()
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    result.message!!
                                )
                            }
                        } else {
                            //if ws not working
                            dialog?.hideDialog()
                            Global.showSnackbar(
                                this@LoginActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Login Ws :   " + error.localizedMessage)
                        dialog?.hideDialog()                                                             //error
                        Global.showSnackbar(
                            this@LoginActivity,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }

    }

    @SuppressLint("CheckResult")
    private fun guestLoginApi() {

        if (NetworkUtil.getConnectivityStatus(this@LoginActivity) != 0) {
            //loading
//            showProgressDialog(this@LoginActivity)

            dialog?.showDialog()
            Global.apiService.guestLoginUser(
                guestEmailID,
                Constants.DEVICE_TOKEN,
                Constants.DEVICE_TYPE,
                Constants.DEVICE_MODEL,
                Constants.APP_VERSION,
                Constants.OS_VERSION,
                com.app.blockaat.apimanager.WebServices.GuestLoginWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        if (result != null) {
                            println("RESPONSE - Login Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                //successfully logged in
                                handleLoginResponse(result.data)
                            } else
                                if (result.status == 404) {
                                    //user does not exist
                                    dialog?.hideDialog()
                                    Global.showSnackbar(
                                        this@LoginActivity,
                                        resources.getString(R.string.user_doesn_t_exists)
                                    )
                                } else {
                                    //some error in response like some field missing or any other status code
                                    dialog?.hideDialog()
                                    Global.showSnackbar(this@LoginActivity, result.message!!)
                                }
                        } else {
                            //if ws not working
                            dialog?.hideDialog()
                            Global.showSnackbar(
                                this@LoginActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                        println("ERROR - Login Ws :   " + error.localizedMessage)
                        dialog?.hideDialog()                                                             //error
                        Global.showSnackbar(this@LoginActivity, resources.getString(R.string.error))
                    }
                )
        }
    }


    /*   private fun showProgressDialog(activity: AppCompatActivity) {
           dialog = ProgressDialog(activity)
           dialog!!.setMessage(resources.getString(R.string.loading))
           dialog!!.show()
       }

       private fun hideProgressDialog() {
           if (dialog!!.isShowing) {
               dialog!!.dismiss()
           }
       }
   */
    //Facebook login result
    override fun onActivityResult(
        requestCode: Int, responseCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, responseCode, data)


        if (requestCode == PRODUCT_REQUEST) {
            val intent = Intent()
            setResult(AppCompatActivity.RESULT_OK, intent)
            setResult(2, intent)
            finish()
        } else if (requestCode == 1 && data!!.getStringExtra("passwordReset") == "yes") {
            Global.showSnackbar(
                this@LoginActivity,
                resources.getString(R.string.Password_successfully_reset)
            )
        } else if (requestCode == LOGINREQUEST) {


            if (data != null && data.hasExtra("backpressed") && data.getBooleanExtra(
                    "backpressed",
                    false
                )
            ) {
                setResult(2, intent)
                finish()
            }
        } else
            if (requestCode == RC_SIGN_IN) {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                if (task.isSuccessful) {
                    val acct =
                        GoogleSignIn.getLastSignedInAccount(this@LoginActivity)
                    if (acct != null) {
                        strSocialName = acct.displayName
                        strSocialLname = acct.familyName.toString()
                        strSocialEmail = acct.email
                        strSocialType = "G"
                        strFacebook_id = ""
                        // val splitName = personName!!.split(" ")
                        var device_token = Global.getStringFromSharedPref(
                            this@LoginActivity,
                            Constants.DEVICE_TOKEN
                        )
                        mGoogleSignInClient?.signOut()
                        socialRegisterApi()

                    } else {

                    }

                } else {

                }
            } else {
                callbackManager!!.onActivityResult(requestCode, responseCode, data)

            }

    }

    @SuppressLint("CheckResult")
    private fun postForgotPassword() {

        if (NetworkUtil.getConnectivityStatus(this@LoginActivity) != 0) {
            showProgressDialog(this@LoginActivity)
            //loading k

            println("forgotPass - $emails")
            Global.apiService.forgotpasswordUser(
                emails,
                com.app.blockaat.apimanager.WebServices.ForgotPasswordWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {

                            println("RESPONSE - ForgotPassword Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                showAlert()


                                //Successsfully changed
                            } else if (result.status == 404) {
                                Global.showSnackbar(
                                    this@LoginActivity,
                                    resources.getString(R.string.user_doesn_t_exists)
                                )
                                //user does not exist                                                            //
                            }
                        } else {
                            //api not working
                        }
                    },

                    { error ->
                        hideProgressDialog()
                        println("ERROR - ForgotPassword Ws :   " + error.localizedMessage)
                        //error
                    }
                )
        }

    }

    /////show language alert
    private fun showAlert() {
        try {
            Global.showAlert(this,
                "",
                resources.getString(R.string.forgot_pass_alert_msg),
                resources.getString(R.string.ok),
                resources.getString(R.string.ok),
                true,
                R.drawable.ic_alert,
                object : AlertDialogInterface {
                    override fun onYesClick() {

                    }

                    override fun onNoClick() {
                        val intent = Intent()
                        setResult(1, intent)
                        intent.putExtra("passwordReset", "yes")
                        finish()
                    }

                })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }


    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            val locale =
                Locale(SharedPreferencesHelper.getString(this, "lang_new", "en"))
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

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}


