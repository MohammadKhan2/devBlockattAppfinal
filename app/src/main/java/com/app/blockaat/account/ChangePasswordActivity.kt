package com.app.blockaat.account

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.blockaat.R
import com.app.blockaat.apimanager.WebServices
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.toolbar_default.*

class ChangePasswordActivity : BaseActivity(), View.OnFocusChangeListener {

    private val EDIT_PROFILE_RESULT: Int = 101
    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        initializeToolbar()
        initializeFields()
        setFont()
        onClickListener()
        setHints()
    }

    private fun setHints() {
        edtCurrentPass.onFocusChangeListener=this
        edtPassword.onFocusChangeListener=this
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtSavePassword.setOnClickListener {
            if (edtCurrentPass.text.isEmpty()) {
                Global.showSnackbar(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.invalid_current_pass)
                )
            } else if (edtCurrentPass.text.length < 6) {
                Global.showSnackbar(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.passwword_must_have_six_letters)
                )
            } else if (edtPassword.text.isEmpty()) {
                Global.showSnackbar(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.empty_new_password)
                )
            } else if (edtPassword.text.length < 6) {
                Global.showSnackbar(
                    this@ChangePasswordActivity,
                    resources.getString(R.string.passwword_must_have_six_letters)
                )
            } else {
                Global.hideKeyboard(this)
                updateProfile()
            }
        }

    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.change_password)
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this@ChangePasswordActivity)

    }

    private fun setFont() {
        txtHead.typeface = Global.fontNavBar
        txtSavePassword.typeface = Global.fontBtn
        edtCurrentPass.typeface = Global.fontRegular
        edtPassword.typeface = Global.fontRegular
    }


    @SuppressLint("CheckResult")
    private fun updateProfile() {
        if (NetworkUtil.getConnectivityStatus(this@ChangePasswordActivity) != 0) {
            //loading
            showProgressDialog(this@ChangePasswordActivity)
            Global.apiService.changePassowrd(
                user_id = Global.getStringFromSharedPref(this, Constants.PREFS_USER_ID),
                first_name = Global.getStringFromSharedPref(this, Constants.PREFS_USER_FIRST_NAME),
                last_name = Global.getStringFromSharedPref(this, Constants.PREFS_USER_LAST_NAME),
                gender = Global.getStringFromSharedPref(this, Constants.PREFS_USER_GENDER),
                dob = "",
                old_password = edtCurrentPass.text.toString(),
                password = edtPassword.text.toString(),
                phone_code = "",
                phone = Global.getStringFromSharedPref(this, Constants.PREFS_USER_PHONE),
                image = "",
                newsletter_subscribed = "1",
                url = WebServices.AccDetailsWs
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result != null) {
                            if (result.status == 200) {
                                val intent = Intent()
                                intent.putExtra("msg", getString(R.string.profile_update_msg))
                                setResult(EDIT_PROFILE_RESULT, intent)
                                finish()

                            } else if (result.status == 201) {
                                Global.showSnackbar(
                                    this@ChangePasswordActivity,
                                    result.message.toString()
                                )
                            } else {
                                //some error in response like some field missing or any other status code
                                Global.showSnackbar(this@ChangePasswordActivity, result.message!!)
                            }
                        }
                    },
                    { error ->
                        hideProgressDialog()
                      // println("error change passowrd" + error.localizedMessage)
                        Global.showSnackbar(
                            this@ChangePasswordActivity,
                            resources.getString(R.string.error)
                        ) //error
                    }
                )
        }
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.edtCurrentPass -> {
                if (hasFocus) {
                    txtInputCurrentPassword.hint =
                        resources.getString(R.string.current_password).toUpperCase()
                } else {
                    if (edtCurrentPass.text.toString().isNotEmpty()) {
                        txtInputCurrentPassword.hint =
                            resources.getString(R.string.current_password).toUpperCase()
                    } else {
                        txtInputCurrentPassword.hint =
                            resources.getString(R.string.current_password)
                    }
                }
            }
            R.id.edtPassword -> {
                if (hasFocus) {
                    txtInputPassword.hint = resources.getString(R.string.password).toUpperCase()
                } else {
                    if (edtPassword.text.toString().isNotEmpty()) {
                        txtInputPassword.hint =
                            resources.getString(R.string.password).toUpperCase()
                    } else {
                        txtInputPassword.hint = resources.getString(R.string.password)
                    }
                }
            }
        }
    }


}