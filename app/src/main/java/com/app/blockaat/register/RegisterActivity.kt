package com.app.blockaat.register

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.register.model.RegisterData

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import android.graphics.Paint
import android.os.Build
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.app.blockaat.cart.model.AddCartRequestApi
import com.app.blockaat.cms.CmsActivity
import com.app.blockaat.login.LoginActivity
import com.app.blockaat.login.model.LoginDataModel
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_register.edtEmail
import kotlinx.android.synthetic.main.activity_register.edtName
import kotlinx.android.synthetic.main.activity_register.edtPhone
import kotlinx.android.synthetic.main.activity_register.edtRetypePass
import kotlinx.android.synthetic.main.activity_register.ivLogo
import kotlinx.android.synthetic.main.activity_register.ivTickFemale
import kotlinx.android.synthetic.main.activity_register.ivTickMale
import kotlinx.android.synthetic.main.activity_register.relFemale
import kotlinx.android.synthetic.main.activity_register.relMale
import kotlinx.android.synthetic.main.activity_register.txtFemale
import kotlinx.android.synthetic.main.activity_register.txtMale
import kotlinx.android.synthetic.main.activity_register.txtShowRetypePassword
import kotlinx.android.synthetic.main.toolbar_default_main.*
import org.json.JSONException
import java.util.*


class RegisterActivity : BaseActivity(), TextWatcher/*, View.OnFocusChangeListener*/ {
    private var dialog: CustomProgressBar? = null
    private var isFromCart: Boolean = false
    private var isFromMyAccount: Boolean = false
    private var isFromProducts: Boolean = false
    private var isFromNav: Boolean = false
    private var isFromProductDetails: Boolean = false
    private var disposable: Disposable? = null
    private lateinit var productsDBHelper: DBHelper
    private val CHANGE_PHONE_CODE: Int = 102
    private val PRODUCT_REQUEST = 1000
    private var mToolbar: Toolbar? = null
    private var boolShow = true
    private var boolShowRetypePass = true
    private var callbackManager: CallbackManager? = null
    private var strFbName: String? = ""
    private var strFbgender: String? = ""
    private var strFbEmail: String? = ""
    private var strFbLname: String = ""
    private var strFacebook_id: String? = ""
    private var strGender: String? = null
    private var strDOB: String? = null
    private var strfrom: String? = null
    private var type: Boolean = false
    private var strSocialType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        setContentView(R.layout.activity_register)

        AppController.instance.trackScreenView(getString(R.string.register_screen))

        Global.setLocale(this)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        setHints()
    }

    private fun setHints() {
        /* edtName.onFocusChangeListener = this
         edtEmail.onFocusChangeListener = this
         edtPassword.onFocusChangeListener = this
         edtPhone.onFocusChangeListener = this*/
    }

    private fun initializeToolbar() {
        /* Global.setBackArrow(this, ivBackArrow, txtHead, view)
         //        txtClose.visibility = View.VISIBLE
         txtHead.text = resources.getString(R.string.register)*/

    }

    private fun initializeFields() {
        if (!Global.isEnglishLanguage(this@RegisterActivity)) {
            ivLogo.setImageResource(R.drawable.app_logo_ar)
        }else{
            ivLogo.setImageResource(R.drawable.app_logo)

        }

        /*edtPhoneCode.isEnabled = false*/
        dialog = CustomProgressBar(this)
        /*    ivTickMale.setColorFilter(resources.getColor(R.color.golden_color))
            ivTickFemale.setColorFilter(resources.getColor(R.color.golden_color))
            if (!Global.isEnglishLanguage(this)) {
                edtPhone.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
                imgDownAr.visibility = VISIBLE
                txtInputCodeAr.visibility = VISIBLE
                viewCodeAr.visibility = VISIBLE
                imgDown.visibility = GONE
                txtInputCode.visibility = GONE
                viewCode.visibility = GONE
            } else {
                edtPhone.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
                imgDownAr.visibility = GONE
                txtInputCodeAr.visibility = GONE
                viewCodeAr.visibility = GONE
                imgDown.visibility = VISIBLE
                txtInputCode.visibility = VISIBLE
                viewCode.visibility = VISIBLE
            }*/
        productsDBHelper = DBHelper(this@RegisterActivity)
        if (intent.hasExtra("isFromCartPageToLogin") && intent.getStringExtra("isFromCartPageToLogin") == "yes") {
            isFromCart = true
        }

        if (intent.hasExtra("isFromMyAccount") && intent.getStringExtra("isFromMyAccount") == "yes") {
            isFromMyAccount = true
        }

        if (intent.hasExtra("isFromProducts") && intent.getStringExtra("isFromProducts") == "yes") {
            isFromProducts = true
        }
        if (intent.hasExtra("isFromNav") && intent.getStringExtra("isFromNav") == "yes") {
            isFromProducts = true
        }
        if (intent.hasExtra("isFromProductDetails") && intent.getStringExtra("isFromProductDetails") == "yes") {
            isFromProductDetails = true
        }
        /* txtLoginNote.paintFlags = txtLoginNote.paintFlags or Paint.UNDERLINE_TEXT_FLAG*/
        txtTnc.paintFlags = txtTnc.paintFlags or Paint.UNDERLINE_TEXT_FLAG


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
                            strFbName = `object`.getString("name")
                            if (`object`.has("last_name")) {
                                strFbLname = `object`.getString("last_name")
                            }
                            strFacebook_id = `object`.getString("id")
                            if (`object`.has("email")) {
                                strFbEmail = `object`.getString("email")
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
                                    this@RegisterActivity,
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
                                this@RegisterActivity,
                                resources.getString(R.string.email_not_found)
                            )
                        }
                    }

                    val parameters = Bundle()
                    parameters.putString("fields", "id,name,link,email,picture,gender, birthday")
                    request.parameters = parameters
                    request.executeAsync()
                }


                override fun onCancel() {
                    Global.showSnackbar(
                        this@RegisterActivity,
                        resources.getString(R.string.login_cancelled)
                    )
                }

                override fun onError(e: FacebookException) {
                    Global.showSnackbar(
                        this@RegisterActivity,
                        resources.getString(R.string.login_unsuccessfull)
                    )
                }
            })


    }


    private fun setFonts() {
        /*    txtHead.typeface = Global.fontNavBar*/
        edtName!!.typeface = Global.fontRegular
        edtEmail!!.typeface = Global.fontRegular
        edtPassword!!.typeface = Global.fontRegular
        txtShowRetypePassword.typeface = Global.fontSemiBold
        txtFb.typeface = Global.fontMedium
        txtNote.typeface = Global.fontRegular
        txtOrSignUp.typeface = Global.fontRegular
        txtRegister.typeface = Global.fontBtn
        txtLogin.typeface = Global.fontBtn
        txtLoginNote.typeface = Global.fontRegular
        txtMale.typeface = Global.fontRegular
        txtFemale.typeface = Global.fontRegular
        txtLoginWith.typeface = Global.fontMedium
        txtTnc.typeface = Global.fontMedium
        txtAgree.typeface = Global.fontRegular
        txtStar.typeface = Global.fontRegular
        edtPhone.typeface = Global.fontRegular
    }

    private fun onClickListeners() {
        ivClose.setOnClickListener {
              onBackPressed()
          }

        txtTnc.setOnClickListener {
            val intent = Intent(this, CmsActivity::class.java)
            intent.putExtra("id", "2")
            intent.putExtra("header", resources.getString(R.string.tnc))
            startActivity(intent)
        }
        relFbLogin.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                this,
                listOf("email")
            )
            btnFbLogin.setReadPermissions(listOf("email"))
        }

        /* txtClose.setOnClickListener {
             onBackPressed()
         }*/
        txtRegister!!.setOnClickListener {
            //Global.hideKeyboard(this)
            validation()
        }


        txtLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        /* txtShow.setOnClickListener {

             if (boolShow) {

                 boolShow = false
                 txtShow.text = resources.getString(R.string.hide)
                 edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
             } else {

                 edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                 boolShow = true
                 txtShow.text = resources.getString(R.string.show)
             }
         }*/

        txtShowRetypePassword.setOnClickListener {

            if (boolShowRetypePass) {

                boolShowRetypePass = false
                txtShowRetypePassword.text = resources.getString(R.string.hide)
                edtRetypePass.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {

                edtRetypePass.transformationMethod = PasswordTransformationMethod.getInstance()
                boolShowRetypePass = true
                txtShowRetypePassword.text = resources.getString(R.string.show)
            }
        }

        relMale.setOnClickListener {
            txtMale.setTextColor(resources.getColor(R.color.primary_button_color))
            txtFemale.setTextColor(resources.getColor(R.color.primary_text))
            ivTickMale.visibility = View.VISIBLE
            ivTickFemale.visibility = View.GONE
            strGender = "M"
        }

        relFemale.setOnClickListener {
            txtFemale.setTextColor(resources.getColor(R.color.primary_button_color))
            txtMale.setTextColor(resources.getColor(R.color.primary_text))
                ivTickFemale.visibility = View.VISIBLE
                ivTickMale.visibility = View.GONE
            strGender = "F"
        }

        /* relPhoneCode.setOnClickListener {
             val i = Intent(this@RegisterActivity, com.app.blockaat.account.ChangeCountryActivity::class.java)
             startActivityForResult(i,CHANGE_PHONE_CODE)
         }

         edtPhoneCode.setOnClickListener {
             relPhoneCode.performClick()
         }*/


        /*    relLater.setOnClickListener {
                txtLater.setTextColor(resources.getColor(R.color.golden_color))
                txtFemale.setTextColor(resources.getColor(R.color.primary_text))
                txtMale.setTextColor(resources.getColor(R.color.primary_text))
                ivTickLater.visibility = View.VISIBLE
                ivTickFemale.visibility = View.GONE
                ivTickMale.visibility = View.GONE
                strGender = "L"
            }*/

    }

    private fun validation() {

        if (edtName!!.text.isEmpty()) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.please_enter_first_name))
        } else if (edtEmail!!.text.isEmpty()) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.empty_email))
        } else if (!Global.isValidEmail(edtEmail.text)) {
            Global.showSnackbar(this@RegisterActivity, getString(R.string.please_enter_valid_email_id))
        } else if (edtPassword!!.text.isEmpty()) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.empty_password))
        } else if (edtPassword.text.length < 6) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.passwword_must_have_six_letters))
        } else if (edtPhone.text.isEmpty()) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.empty_phone_number))
        } else if (edtPhone.text.toString().length < 8 || edtPhone.text.toString().length > 12) {
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.error_phone_length))
        } else if (strGender.isNullOrEmpty()) {
            Global.showSnackbar(this@RegisterActivity, getString(R.string.select_gender))
        } else if (!checkTnc.isChecked) {
            Global.showSnackbar(this@RegisterActivity, getString(R.string.tnc_error))
        } else {
            registerApi()
        }
    }

    @SuppressLint("CheckResult")
    private fun registerApi() {
        if (NetworkUtil.getConnectivityStatus(this@RegisterActivity) != 0) {
            var firstName = ""
            var lastName = ""
            if (edtName.text.trim().toString().contains(" ")) {
                val fullName = edtName.text.trim().toString()
                firstName = fullName.toString()
             //   lastName = fullName.toString()
            } else {
                firstName = edtName.text.trim().toString()
            }
            showProgressDialog(this@RegisterActivity)

            Global.apiService.registerUser(
                firstName,
                "",
                edtEmail!!.text.toString(),
                edtPassword!!.text.toString(),
                edtPhone.text.toString(),
                strGender.toString(),
                Constants.DEVICE_TOKEN,
                Constants.DEVICE_TYPE,
                Constants.APP_VERSION,
                Constants.OS_VERSION,
                Constants.NEWSLETTER_SUBSCRIBED,
                Constants.DEVICE_MODEL,
                com.app.blockaat.apimanager.WebServices.RegisterWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        if (result != null) {
                         //   println("RESPONSE - Register Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                handleRegisterResponse(result.data)
                                CustomEvents.registrationComplete(this,result.data?.id?.toInt(),firstName,
                                        edtEmail!!.text.toString(),"")

                            } else if (result.status == 406) {
                                hideProgressDialog()
                                Global.showSnackbar(
                                    this@RegisterActivity,
                                    resources.getString(R.string.user_already_registered)
                                ) //User with same email already exists.
                            }
                        } else {
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@RegisterActivity,
                                resources.getString(R.string.error)
                            ) //api not working
                        }
                    },
                    { error ->
                      //  println("ERROR - Register Ws :   " + error.localizedMessage)
                        hideProgressDialog()
                        Global.showSnackbar(
                            this@RegisterActivity,
                            resources.getString(R.string.error)
                        ) //error
                    }
                )
        }
    }

    private fun handleRegisterResponse(registerDataModel: RegisterData?) {
        if (registerDataModel != null) {
            //storing users info after successful login
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_ID,
                registerDataModel.id.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_FIRST_NAME,
                registerDataModel.first_name.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_LAST_NAME,
                registerDataModel.last_name.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_EMAIL,
                registerDataModel.email.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PHONE_CODE,
                registerDataModel.phone_code.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PHONE,
                registerDataModel.phone.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_GENDER,
                registerDataModel.gender.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_DOB,
                registerDataModel.dob.toString()
            )

            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_IMAGE,
                registerDataModel.image.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_CODE,
                registerDataModel.code.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isPHONE_VERIFIED,
                registerDataModel.is_phone_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isEMAIL_VERIFIED,
                registerDataModel.is_email_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isSOCIAL_REGISTER,
                registerDataModel.is_social_register.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_SOCIAL_REGISTER_TYPE,
                registerDataModel.social_register_type.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isUSER_LOGGED_IN,
                "yes"
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PUSH_ENABLED,
                registerDataModel.push_enabled.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.NEWSLETTER_SUBSCRIBED,
                registerDataModel.newsletter_subscribed.toString()
            )

            onRegisterAddDBItemToCart(registerDataModel.id.toString())


        } else {
            hideProgressDialog()
            //if ws not working no data for login
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.error))
        }
    }

    private fun onRegisterAddDBItemToCart(user_id: String) {
        val strMultipleCartEntityIDs = productsDBHelper.getAllCartEntityIDs().toString()
            .substring(1, productsDBHelper.getAllCartEntityIDs().toString().length - 1)
        val strMultipleCartProductQty = productsDBHelper.getAllCartProductQty().toString()
            .substring(1, productsDBHelper.getAllCartProductQty().toString().length - 1)

       // println("HERE IS :::    $strMultipleCartEntityIDs   :   $strMultipleCartProductQty")

        if (strMultipleCartEntityIDs.isNotEmpty()) {
            if (NetworkUtil.getConnectivityStatus(this@RegisterActivity) != 0) {
                val addCartObject = AddCartRequestApi(
                    Global.getStringFromSharedPref(
                        this@RegisterActivity,
                        Constants.PREFS_USER_ID
                    ), strMultipleCartEntityIDs, strMultipleCartProductQty, "", ""
                )
                val disposable = Global.apiService.addToCart(
                    com.app.blockaat.apimanager.WebServices.AddToCartWs + Global.getStringFromSharedPref(
                        this@RegisterActivity,
                        Constants.PREFS_LANGUAGE
                    )
                            + "&store=" + Global.getStringFromSharedPref(
                        this@RegisterActivity,
                        Constants.PREFS_STORE_CODE
                    ), addCartObject
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            if (result != null) {
                       //         println("RESPONSE - Add to cart Ws :   " + Gson().toJson(result.data))
                                hideProgressDialog()

                                if (intent.hasExtra("isFromNav")) {
                                //    println("HERE I AM IN isFromNav")
                                    val intent = Intent()
                                    intent.putExtra("backpressed", true)
                                    setResult(AppCompatActivity.RESULT_OK, intent)
                                    finish()
                                } else if (intent.hasExtra("isFromProductDetails")) {
                                  //  println("HERE I AM IN isFromProductDetails")
                                    val intent = Intent()
                                    intent.putExtra("backpressed", true)
                                    setResult(AppCompatActivity.RESULT_OK, intent)
                                    finish()
                                } else if (intent.hasExtra("isFromProducts")) {
                                   // println("HERE I AM IN isFromProductDetails")
                                    val intent = Intent()
                                    intent.putExtra("backpressed", true)
                                    setResult(AppCompatActivity.RESULT_OK, intent)
                                    finish()
                                } else if (intent.hasExtra("isFromLogin")) {
                                  //  println("HERE I AM IN isFromLogin")

                                    val intent = Intent()
                                    intent.putExtra("backpressed", true)
                                    setResult(AppCompatActivity.RESULT_OK, intent)
                                    finish()
                                } else {
                                 //   println("HERE I AM IN REGISTER 2")
                                    val intent = Intent(
                                        this@RegisterActivity,
                                        NavigationActivity::class.java
                                    )
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
                                    if (isFromNav) {
                                        intent.putExtra(
                                            "isFromNav",
                                            "yes"
                                        ) //if is coming from my account -> login
                                    }
                                    if (isFromProductDetails) {
                                        intent.putExtra(
                                            "isFromProductDetails",
                                            "yes"
                                        )
                                    }
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }
                            }
                        },
                        { error ->

                           // println("RESPONSE - Add to cart Ws :   " + error.localizedMessage)
                            hideProgressDialog()
                            if (intent.hasExtra("isFromProducts")) {
                             //   println("HERE I AM IN REGISTER 5")
                                val intent = Intent()
                                setResult(AppCompatActivity.RESULT_OK, intent)
                                finish()
                            } else if (intent.hasExtra("isFromLogin")) {

                                val intent = Intent()
                                intent.putExtra("backpressed", true)
                                setResult(RESULT_OK, intent)
                                finish()
                            } else {
                               // println("HERE I AM IN REGISTER 6")
                                val intent =
                                    Intent(this@RegisterActivity, NavigationActivity::class.java)
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
                                startActivity(intent)
                                finish()
                            }
                        }
                    )
            }
        } else {
            hideProgressDialog()
            if (intent.hasExtra("isFromProducts")) {
             //   println("HERE I AM IN REGISTER 3")
                val intent = Intent()
                setResult(AppCompatActivity.RESULT_OK, intent)
                finish()
            } else if (intent.hasExtra("isFromLogin")) {

                val intent = Intent()
                intent.putExtra("backpressed", true)
                setResult(AppCompatActivity.RESULT_OK, intent)
                finish()
            } else {
              //  println("HERE I AM IN REGISTER 4")
                val intent = Intent(this@RegisterActivity, NavigationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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
                startActivity(intent)
                finish()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun socialRegisterApi() {

        if (NetworkUtil.getConnectivityStatus(this@RegisterActivity) != 0) {
            //loading
            showProgressDialog(this@RegisterActivity)

            Global.apiService.Socialregisteration(
                this!!.strFbEmail!!,
                strFbName!!,
                strFbLname!!,
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
                        //    println("RESPONSE - SocialLogin Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                //successfully logged in
                                handleLoginResponse(result.data)
                            } else if (result.status == 201) {
                                //invalid password
                                hideProgressDialog()
                                Global.showSnackbar(
                                    this@RegisterActivity,
                                    resources.getString(R.string.invalid_password)
                                )
                            } else if (result.status == 404) {
                                //user does not exist
                                hideProgressDialog()
                                Global.showSnackbar(
                                    this@RegisterActivity,
                                    resources.getString(R.string.user_doesn_t_exists)
                                )
                            } else {
                                //some error in response like some field missing or any other status code
                                hideProgressDialog()
                                Global.showSnackbar(this@RegisterActivity, result.message!!)
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@RegisterActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                      //  println("ERROR - Login Ws :   " + error.localizedMessage)
                        hideProgressDialog()                                                             //error
                        Global.showSnackbar(
                            this@RegisterActivity,
                            resources.getString(R.string.error)
                        )
                    }
                )
        }

    }

    private fun handleLoginResponse(loginDataModel: LoginDataModel?) {
        if (loginDataModel != null) {

            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_ID,
                loginDataModel.id.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_FIRST_NAME,
                loginDataModel.first_name.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_LAST_NAME,
                loginDataModel.last_name.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_EMAIL,
                loginDataModel.email.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PHONE_CODE,
                loginDataModel.phone_code.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PHONE,
                loginDataModel.phone.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_DOB,
                loginDataModel.dob.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_CODE,
                loginDataModel.code.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isPHONE_VERIFIED,
                loginDataModel.is_phone_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isEMAIL_VERIFIED,
                loginDataModel.is_email_verified.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isSOCIAL_REGISTER,
                loginDataModel.is_social_register.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_SOCIAL_REGISTER_TYPE,
                loginDataModel.social_register_type.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_isUSER_LOGGED_IN,
                "yes"
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.PREFS_USER_PUSH_ENABLED,
                loginDataModel.push_enabled.toString()
            )
            Global.saveStringInSharedPref(
                this@RegisterActivity,
                Constants.NEWSLETTER_SUBSCRIBED,
                loginDataModel.newsletter_subscribed.toString()
            )


            onRegisterAddDBItemToCart(loginDataModel.id.toString())


        } else {
            //if ws not working
            hideProgressDialog()
            Global.showSnackbar(this@RegisterActivity, resources.getString(R.string.error))
        }
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }


    //Facebook login result
    override fun onActivityResult(
        requestCode: Int, responseCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, responseCode, data)
        if (requestCode == PRODUCT_REQUEST) {
            val intent = Intent()
            setResult(AppCompatActivity.RESULT_OK, intent)
            finish()
        } else if (requestCode == 1 && data!!.getStringExtra("passwordReset") == "yes") {
            Global.showSnackbar(
                this@RegisterActivity,
                resources.getString(R.string.Password_successfully_reset)
            )
        } else if (requestCode == CHANGE_PHONE_CODE && data != null) {

            if (data.hasExtra("phonecode")) {
                val strPhoneCode = data.getStringExtra("phonecode")
                //edtPhoneCode.setText(strPhoneCode)
                Global.saveStringInSharedPref(
                    this@RegisterActivity,
                    Constants.PREFS_USER_PHONE_CODE,
                    strPhoneCode!!
                )
            }

        } else {
            callbackManager!!.onActivityResult(requestCode, responseCode, data)
        }
    }

    private fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (intent.hasExtra("isFromLogin")) {
                    val intent = Intent()
                    intent.putExtra("backpressed", false)
                    setResult(AppCompatActivity.RESULT_OK, intent)
                    finish()
                } else {
                    finish()
                }
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (intent.hasExtra("isFromLogin")) {
            val intent = Intent()
            intent.putExtra("backpressed", false)
            setResult(AppCompatActivity.RESULT_OK, intent)
            finish()
        } else {
            finish()
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

/*    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.edtName -> {
                if (hasFocus) {
                    txtInputName.hint = resources.getString(R.string.name_hint).toUpperCase()
                } else {
                    if (edtName.text.toString().isNotEmpty()) {
                        txtInputName.hint = resources.getString(R.string.name_hint).toUpperCase()
                    } else {
                        txtInputName.hint = resources.getString(R.string.name_hint)
                    }
                }
            }
            R.id.edtEmail -> {
                if (hasFocus) {
                    txtInputEmail.hint = resources.getString(R.string.email_address).toUpperCase()
                } else {
                    if (edtEmail.text.toString().isNotEmpty()) {
                        txtInputEmail.hint = resources.getString(R.string.email_address).toUpperCase()
                    } else {
                        txtInputEmail.hint = resources.getString(R.string.email_address)
                    }
                }
            }
            R.id.edtPassword -> {
                if (hasFocus) {
                    txtInputPassword.hint = resources.getString(R.string.password).toUpperCase()
                } else {
                    if (edtPassword.text.toString().isNotEmpty()) {
                        txtInputPassword.hint = resources.getString(R.string.password).toUpperCase()
                    } else {
                        txtInputPassword.hint = resources.getString(R.string.password)
                    }
                }
            }
            R.id.edtPhone -> {
                if (hasFocus) {
                    txtInputMobile.hint = resources.getString(R.string.phone_number).toUpperCase()
                } else {
                    if (edtPhone.text.toString().isNotEmpty()) {
                        txtInputMobile.hint = resources.getString(R.string.phone_number).toUpperCase()
                    } else {
                        txtInputMobile.hint = resources.getString(R.string.phone_number)
                    }
                }
            }
        }
    }*/

}
