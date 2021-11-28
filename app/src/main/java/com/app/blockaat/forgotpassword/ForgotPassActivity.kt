package com.app.blockaat.forgotpassword

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_frgtpass.*
import kotlinx.android.synthetic.main.toolbar_default.*

class ForgotPassActivity : BaseActivity()/*, View.OnFocusChangeListener*/ {

    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frgtpass)

        if (Global.isUserLoggedIn(this)){
            val userId = Global.getUserId(this)
            CustomEvents.screenViewed(this,userId,getString(R.string.forgot_password_screen))
        }

        /* initializeToolbar()*/
        initializeFields()
        setFonts()
        onClickListener()
        /*setHints()*/
    }

    /*private fun setHints() {
        edtEmail.onFocusChangeListener = this
    }*/

    /*private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.forgot_pass)
    }*/

    private fun initializeFields() {
        dialog = CustomProgressBar(this)

    }

    private fun setFonts() {
        /*    txtHead.typeface = Global.fontNavBar*/
        /*txtNote.typeface = Global.fontRegular
        edtEmails.typeface = Global.fontRegular
        txtClose.typeface = Global.fontMedium
        txtResetPass.typeface = Global.fontBtn*/
    }

    private fun onClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtResetPass.setOnClickListener {
            Global.hideKeyboard(this)
            validation()
        }
        txtClose.setOnClickListener { onBackPressed() }
    }

    private fun validation() {

        if (edtEmails!!.text.isEmpty()) {
            Global.showSnackbar(this@ForgotPassActivity, getString(R.string.empty_email))
        } else if (!Global.isValidEmail(edtEmails.text)) {
            Global.showSnackbar(
                this@ForgotPassActivity,
                getString(R.string.please_enter_valid_email_id)
            )
        } else {
            postForgotPassword()
        }
    }

    @SuppressLint("CheckResult")
    private fun postForgotPassword() {

        if (NetworkUtil.getConnectivityStatus(this@ForgotPassActivity) != 0) {
            showProgressDialog(this@ForgotPassActivity)
            //loading k
            Global.apiService.forgotpasswordUser(
                edtEmails!!.text.toString(),
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
                                    this@ForgotPassActivity,
                                    resources.getString(R.string.user_doesn_t_exists)
                                )
                                                           //
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

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    /*override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.edtEmail -> {
                if (hasFocus) {
                    txtInputEmail.hint = resources.getString(R.string.email_address).toUpperCase()
                } else {
                    if (edtEmail.text.toString().isNotEmpty()) {
                        txtInputEmail.hint =
                            resources.getString(R.string.email_address).toUpperCase()
                    } else {
                        txtInputEmail.hint =  resources.getString(R.string.email_address)
                    }
                }
            }

        }
    }*/
}