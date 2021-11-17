package com.app.blockaat.account


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.blockaat.R
import com.app.blockaat.helper.*
import kotlinx.android.synthetic.main.activity_acc_info.*
import kotlinx.android.synthetic.main.activity_acc_info.edtEmail
import kotlinx.android.synthetic.main.activity_acc_info.edtName
import kotlinx.android.synthetic.main.activity_acc_info.edtPhone
import kotlinx.android.synthetic.main.activity_acc_info.edtPhoneCode
import kotlinx.android.synthetic.main.activity_acc_info.ivTickFemale
import kotlinx.android.synthetic.main.activity_acc_info.ivTickMale
import kotlinx.android.synthetic.main.activity_acc_info.txtFemale
import kotlinx.android.synthetic.main.activity_acc_info.txtInputEmail
import kotlinx.android.synthetic.main.activity_acc_info.txtInputMobile
import kotlinx.android.synthetic.main.activity_acc_info.txtInputName
import kotlinx.android.synthetic.main.activity_acc_info.txtMale
import kotlinx.android.synthetic.main.toolbar_default.*

class AccountInfoActivity : BaseActivity(), View.OnFocusChangeListener {
    private val EDIT_PROFILE_REQUEST: Int = 100
    private val EDIT_PROFILE_RESULT: Int = 101
    private val CHANGE_PHONE_CODE: Int = 102
    private var strGender = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc_info)
        initializeToolbar()
        initializeFields()
        setFont()
        onClickListener()
        setHints()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.account_details)

    }

    private fun initializeFields() {
        setUserDetails()
        edtEmail.isEnabled = false
        edtPhoneCode.isEnabled = false
        edtName.isEnabled = false
        if(Global.getStringFromSharedPref(this@AccountInfoActivity,Constants.PREFS_USER_PHONE_CODE).isNullOrEmpty())
        {
            edtPhoneCode.setText("+965")
        }else
        {
            if(Global.getStringFromSharedPref(this@AccountInfoActivity,Constants.PREFS_USER_PHONE_CODE).contains("+"))
            {
                edtPhoneCode.setText(Global.getStringFromSharedPref(this@AccountInfoActivity,Constants.PREFS_USER_PHONE_CODE))
            }else
            {
                edtPhoneCode.setText("+"+Global.getStringFromSharedPref(this@AccountInfoActivity,Constants.PREFS_USER_PHONE_CODE))
            }
        }

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

    private fun setFont() {
        txtHead.typeface = Global.fontNavBar
        txtEdit.typeface = Global.fontBtn
        txtChangePassword.typeface = Global.fontBtn
        edtEmail.typeface = Global.fontRegular
        edtPhone.typeface = Global.fontRegular
        edtName.typeface = Global.fontRegular
        txtMale.typeface = Global.fontMedium
        txtFemale.typeface = Global.fontMedium
        edtPhoneCode.typeface = Global.fontRegular
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtChangePassword.setOnClickListener {
            val intent = Intent(this@AccountInfoActivity, ChangePasswordActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        txtEdit.setOnClickListener {
            val intent = Intent(this@AccountInfoActivity, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }

        /*
        txtUpdate.setOnClickListener {
            if (txtFirstName!!.text.isEmpty()) {
                Global.showSnackbar(this@AccountInfoActivity, resources.getString(R.string.please_enter_your_name))
            } else if (txtLastName!!.text.isEmpty()) {
                Global.showSnackbar(this@AccountInfoActivity, resources.getString(R.string.please_enter_your_name))
            }else{
                updateProfile()
            }
        }
*/
    }

    @SuppressLint("SetTextI18n")
    private fun setUserDetails() {
        edtName.setText(
            Global.getStringFromSharedPref(
                this,
                Constants.PREFS_USER_FIRST_NAME
            ) + " " + Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME)
        )
        println("USERNAME "+ Global.getStringFromSharedPref(
            this,
            Constants.PREFS_USER_FIRST_NAME
        ) + " " + Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME))
        edtEmail.setText(Global.getStringFromSharedPref(this, Constants.PREFS_USER_EMAIL))
        edtPhone.setText(Global.getStringFromSharedPref(this, Constants.PREFS_USER_PHONE))

        strGender = Global.getStringFromSharedPref(this, Constants.PREFS_USER_GENDER)
        if (strGender == "M") {
            ivTickMale.visibility = View.VISIBLE
            ivTickMale.setColorFilter(resources.getColor(R.color.edt_txt_border_color))
            ivTickFemale.visibility = View.GONE
        } else if (strGender == "F") {
            ivTickFemale.visibility = View.VISIBLE
            ivTickFemale.setColorFilter(resources.getColor(R.color.edt_txt_border_color))
            ivTickMale.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == EDIT_PROFILE_RESULT) {
            val intent = Intent()
            intent.putExtra("msg", getString(R.string.profile_update_msg))
            setResult(EDIT_PROFILE_RESULT, intent)
            finish()
        }
        if (requestCode == CHANGE_PHONE_CODE && resultCode == Activity.RESULT_OK && data!=null) {

            if(data.hasExtra("phonecode"))
            {
                val strPhoneCode = data.getStringExtra("phonecode")
                edtPhoneCode.setText(strPhoneCode)
                Global.saveStringInSharedPref(this@AccountInfoActivity,Constants.PREFS_USER_PHONE_CODE,
                    strPhoneCode!!
                )
            }

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
}