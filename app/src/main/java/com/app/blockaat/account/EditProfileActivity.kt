package com.app.blockaat.account

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.app.blockaat.R
import com.app.blockaat.account.model.EditProfileData
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.edtEmail
import kotlinx.android.synthetic.main.activity_edit_profile.edtName
import kotlinx.android.synthetic.main.activity_edit_profile.edtPhone
import kotlinx.android.synthetic.main.activity_edit_profile.edtPhoneCode
import kotlinx.android.synthetic.main.activity_edit_profile.ivTickFemale
import kotlinx.android.synthetic.main.activity_edit_profile.ivTickMale
import kotlinx.android.synthetic.main.activity_edit_profile.relFemale
import kotlinx.android.synthetic.main.activity_edit_profile.relMale
import kotlinx.android.synthetic.main.activity_edit_profile.txtFemale
import kotlinx.android.synthetic.main.activity_edit_profile.txtInputEmail
import kotlinx.android.synthetic.main.activity_edit_profile.txtInputMobile
import kotlinx.android.synthetic.main.activity_edit_profile.txtInputName
import kotlinx.android.synthetic.main.activity_edit_profile.txtMale
import kotlinx.android.synthetic.main.toolbar_default.*

class EditProfileActivity : BaseActivity(), View.OnFocusChangeListener {
    private var mToolbar: Toolbar? = null
    private var boolEdit = false
    private var dialog: CustomProgressBar? = null
    private var strGender = ""
    private val EDIT_PROFILE_RESULT: Int = 101
    private val CHANGE_PHONE_CODE: Int = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        initializeToolbar()
        initializeFields()
        setFont()
        onClickListener()
        setHints()
    }

    private fun setHints() {
        if (edtName.text.toString().isNotEmpty()) {
            txtInputName.hint = resources.getString(R.string.full_name).toUpperCase()
        } else {
            txtInputName.hint = resources.getString(R.string.full_name)
        }
        if (edtPhone.text.toString().isNotEmpty()) {
            txtInputMobile.hint = resources.getString(R.string.phone_number).toUpperCase()
        } else {
            txtInputMobile.hint = resources.getString(R.string.phone_number)
        }
        if (edtEmail.text.toString().isNotEmpty()) {
            txtInputEmail.hint = resources.getString(R.string.email_address).toUpperCase()
        } else {
            txtInputEmail.hint = resources.getString(R.string.email_address)
        }
        edtName.onFocusChangeListener = this
        edtEmail.onFocusChangeListener = this
        edtPhone.onFocusChangeListener = this
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtEditProfile.setOnClickListener {
            if (edtName.text.isEmpty()) {
                Global.showSnackbar(
                    this@EditProfileActivity,
                    resources.getString(R.string.please_enter_your_name)
                )

            } else {
                Global.hideKeyboard(this)
                updateProfile()
            }
        }
        relMale.setOnClickListener {
            ivTickMale.visibility = View.VISIBLE
            ivTickFemale.visibility = View.GONE
            strGender = "M"
        }

        relFemale.setOnClickListener {
            ivTickFemale.visibility = View.VISIBLE
            ivTickMale.visibility = View.GONE
            strGender = "F"
        }

        edtPhoneCode.setOnClickListener {
            val i = Intent(this@EditProfileActivity, ChangeCountryActivity::class.java)
            startActivityForResult(i, CHANGE_PHONE_CODE)
        }
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.edit_profile)

    }

    private fun initializeFields() {
        edtEmail.isEnabled = false

        dialog = CustomProgressBar(this@EditProfileActivity)
        edtName.setText(
            Global.getStringFromSharedPref(
                this,
                Constants.PREFS_USER_FIRST_NAME
            ) + " " + Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME)
        )
        edtEmail.setText(Global.getStringFromSharedPref(this, Constants.PREFS_USER_EMAIL))
        edtPhone.setText(Global.getStringFromSharedPref(this, Constants.PREFS_USER_PHONE))
        if (Global.getStringFromSharedPref(
                this@EditProfileActivity,
                Constants.PREFS_USER_PHONE_CODE
            ).isNullOrEmpty()
        ) {
            edtPhoneCode.setText("+965")
        } else {
            if (Global.getStringFromSharedPref(
                    this@EditProfileActivity,
                    Constants.PREFS_USER_PHONE_CODE
                ).contains("+")
            ) {
                edtPhoneCode.setText(
                    Global.getStringFromSharedPref(
                        this@EditProfileActivity,
                        Constants.PREFS_USER_PHONE_CODE
                    )
                )
            } else {
                edtPhoneCode.setText(
                    "+" + Global.getStringFromSharedPref(
                        this@EditProfileActivity,
                        Constants.PREFS_USER_PHONE_CODE
                    )
                )
            }
        }


        strGender = Global.getStringFromSharedPref(this, Constants.PREFS_USER_GENDER)
        if (strGender == "M") {
//            txtGender.text = resources.getString(R.string.male)
            ivTickMale.visibility = View.VISIBLE
            ivTickFemale.visibility = View.GONE
        } else if (strGender == "F") {
//            txtGender.text = resources.getString(R.string.female)
            ivTickFemale.visibility = View.VISIBLE
            ivTickMale.visibility = View.GONE
        }
//        edtPhoneCode.isEnabled = false
    }

    private fun setHint() {
        edtName.hint = getString(R.string.full_name).toUpperCase()
        edtEmail.hint = getString(R.string.email_address).toUpperCase()
        edtPhoneCode.hint = getString(R.string.code_hint).toUpperCase()
        edtPhone.hint = getString(R.string.phone_number).toUpperCase()
    }


    private fun setFont() {
        txtHead.typeface = Global.fontNavBar
        edtName.typeface = Global.fontRegular
        edtEmail.typeface = Global.fontRegular
        txtEditProfile.typeface = Global.fontBtn
        edtPhoneCode.typeface = Global.fontRegular
        edtPhone.typeface = Global.fontRegular
        txtMale.typeface = Global.fontMedium
        txtFemale.typeface = Global.fontMedium
    }


    @SuppressLint("CheckResult")
    private fun updateProfile() {
        if (NetworkUtil.getConnectivityStatus(this@EditProfileActivity) != 0) {
            var firstName = ""
            var lastName = ""
            if (edtName.text.toString().contains(" ")) {
                val name = edtName.text.split(" ")
                firstName = name[0]
                lastName = name[1]
            } else {
                firstName = edtName.text.toString()
            }
            //loading
            showProgressDialog()
            Global.apiService.accDetailsUser(
                Global.getStringFromSharedPref(this@EditProfileActivity, Constants.PREFS_USER_ID),
                firstName,
                lastName,
                strGender,
                "",
                "",
                "",
                edtPhoneCode.text.toString(),
                edtPhone.text.toString(),
                "",
                "1",
                WebServices.AccDetailsWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                handleEditProfileResponse(result.data)
                            } else if (result.status == 201) {
                                val intent = Intent()
                                intent.putExtra("msg", result.message.toString())
                                setResult(EDIT_PROFILE_RESULT, intent)
                                finish()
                            } else if (result.status == 500) {
                                Global.showSnackbar(
                                    this@EditProfileActivity,
                                    resources.getString(R.string.error)
                                ) //error
                            } else {
                                //some error in response like some field missing or any other status code
                                Global.showSnackbar(this@EditProfileActivity, result.message!!)
                            }
                        }
                    },
                    { error ->
                        hideProgressDialog()
                      //  println("ERROR - Edit Profile :   " + error.localizedMessage)
                        Global.showSnackbar(
                            this@EditProfileActivity,
                            resources.getString(R.string.error)
                        ) //error
                    }
                )
        }
    }

    private fun handleEditProfileResponse(editProfileDataModel: EditProfileData?) {

        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_ID,
            editProfileDataModel!!.id!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_FIRST_NAME,
            editProfileDataModel.first_name!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_LAST_NAME,
            editProfileDataModel.last_name!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_EMAIL,
            editProfileDataModel.email!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_PHONE_CODE,
            editProfileDataModel.phone_code!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_PHONE,
            editProfileDataModel.phone!!
        )
        Global.saveStringInSharedPref(
            this,
            Constants.PREFS_USER_IMAGE,
            editProfileDataModel.image.toString()
        )

        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_GENDER,
            editProfileDataModel.gender.toString()
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_DOB,
            editProfileDataModel.dob.toString()
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_CODE,
            editProfileDataModel.code!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_isPHONE_VERIFIED,
            editProfileDataModel.is_phone_verified.toString()
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_isEMAIL_VERIFIED,
            editProfileDataModel.is_email_verified.toString()
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_isSOCIAL_REGISTER,
            editProfileDataModel.is_social_register.toString()
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_SOCIAL_REGISTER_TYPE,
            editProfileDataModel.social_register_type!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.PREFS_USER_PUSH_ENABLED,
            editProfileDataModel.push_enabled!!
        )
        Global.saveStringInSharedPref(
            this@EditProfileActivity,
            Constants.NEWSLETTER_SUBSCRIBED,
            editProfileDataModel.newsletter_subscribed.toString()
        )
        val intent = Intent()
        intent.putExtra("msg", getString(R.string.profile_update_msg))
        setResult(EDIT_PROFILE_RESULT, intent)
        finish()
    }

    private fun showProgressDialog() {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.edtName -> {
                if (hasFocus) {
                    txtInputName.hint = resources.getString(R.string.full_name).toUpperCase()
                } else {
                    if (edtName.text.toString().isNotEmpty()) {
                        txtInputName.hint = resources.getString(R.string.full_name).toUpperCase()
                    } else {
                        txtInputName.hint = resources.getString(R.string.full_name)
                    }
                }
            }
            R.id.edtEmail -> {
                if (hasFocus) {
                    txtInputEmail.hint = resources.getString(R.string.email_address).toUpperCase()
                } else {
                    if (edtEmail.text.toString().isNotEmpty()) {
                        txtInputEmail.hint =
                            resources.getString(R.string.email_address).toUpperCase()
                    } else {
                        txtInputEmail.hint = resources.getString(R.string.email_address)
                    }
                }
            }
            R.id.edtPhone -> {
                if (hasFocus) {
                    txtInputMobile.hint = resources.getString(R.string.phone_number).toUpperCase()
                } else {
                    if (edtPhone.text.toString().isNotEmpty()) {
                        txtInputMobile.hint =
                            resources.getString(R.string.phone_number).toUpperCase()
                    } else {
                        txtInputMobile.hint = resources.getString(R.string.phone_number)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHANGE_PHONE_CODE && data != null) {
            if (data.hasExtra("phoneCode")) {
                var strPhoneCode = data.getStringExtra("phoneCode")
                if (!data.getStringExtra("phoneCode")!!.contains("+")) {
                    strPhoneCode = "+" + data.getStringExtra("phoneCode")
                }
                edtPhoneCode.setText(strPhoneCode)
                Global.saveStringInSharedPref(
                    this@EditProfileActivity,
                    Constants.PREFS_USER_PHONE_CODE,
                    strPhoneCode!!
                )
            }

        }
    }

}
