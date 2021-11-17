package com.app.blockaat.contactus

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.address.CountryListActivity
import com.app.blockaat.helper.*
import com.app.blockaat.webview.WebViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contact_us.*
import kotlinx.android.synthetic.main.toolbar_default.*

class ContactUsActivity : BaseActivity(), View.OnFocusChangeListener {
    private val PHONE_CODE_REQUEST: Int = 103
    private var dialog: CustomProgressBar? = null
    private var mToolbar: Toolbar? = null
    private var strName = ""
    private var strEmail = ""
    private var strComment = ""
    private var strPhone = ""
    private var strPhoneCode = ""
    private var strTopic: String = ""
    private var arrListTopic = ArrayList<String>(0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)
        initializeToolbar()
        init()
        setFont()
        setOnClickListener()
        setHints()
    }

    private fun setHints() {
        edtName.onFocusChangeListener = this
        edtEmail.onFocusChangeListener = this
        edtPhone.onFocusChangeListener = this
        edtComments.onFocusChangeListener = this
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        relTopic.setOnClickListener {
            openTopicPicker()
        }
        btnSend.setOnClickListener {
            validation()
        }
        edtPhoneCode.setOnClickListener {
            val i = Intent(this, CountryListActivity::class.java)
            startActivityForResult(i, PHONE_CODE_REQUEST)
        }



        ivInstagram.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.instagram))
            intent.putExtra("strUrl", Global.INSTAGRAM_LINK)
            startActivity(intent)
        }

        ivFaceBook.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.facebook))
            intent.putExtra("strUrl", Global.FACEBOOK_LINK)
            startActivity(intent)
        }

        ivTwitter.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.twitter))
            intent.putExtra("strUrl", Global.TWITTER_LINK)
            startActivity(intent)
        }

        ivYoutube.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.youtube))
            intent.putExtra("strUrl", Global.YOUTUBE_LINK)
            startActivity(intent)
        }

        ivGooglePlus.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.google_plus))
            intent.putExtra("strUrl", Global.GOOGLE_PLUS_LINK)
            startActivity(intent)
        }

        ivSnapchat.setOnClickListener {
            val intent = Intent(this@ContactUsActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.snapchat))
            intent.putExtra("strUrl", Global.SNAPCHAT_LINK)
            startActivity(intent)
        }


    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.contact_us)
        strPhoneCode = getString(R.string.phone_code_hint)
    }

    private fun init() {

        dialog = CustomProgressBar(this@ContactUsActivity)
        txtEmail.text = Global.strSupportEmail
        txtPhone.text = Global.strSupportPhone
//        edtPhoneCode.isEnabled = false
        if (!Global.isEnglishLanguage(this)) {
            txtPhone.gravity = View.TEXT_ALIGNMENT_TEXT_START
        }
        arrListTopic =
            resources?.getStringArray(R.array.topic_array)?.toList() as ArrayList<String>

    }


    private fun openTopicPicker() {
        try {
            val parentView = layoutInflater.inflate(R.layout.layout_bottom_dialog_attribute, null)
            val bottomSheerDialog = BottomSheetDialog(this)
            bottomSheerDialog.setContentView(parentView)
            bottomSheerDialog.setCanceledOnTouchOutside(false)
            bottomSheerDialog.setCancelable(false)
            val wheelView =
                parentView.findViewById(R.id.wheelView) as com.app.blockaat.helper.WheelView
            val txtDone = parentView.findViewById(R.id.txtDone) as TextView
            val txtCancel = parentView.findViewById(R.id.txtCancel) as TextView
            txtCancel.typeface = Global.fontMedium
            txtDone.typeface = Global.fontMedium

            if (arrListTopic.isNotEmpty()) {
                wheelView.setItems(arrListTopic, Global.getDimenVallue(this, 5.0).toInt())
                txtCancel.setOnClickListener {
                    bottomSheerDialog.dismiss()
                }

                txtDone.setOnClickListener {
                    bottomSheerDialog.dismiss()
                    strTopic = arrListTopic[wheelView.seletedIndex]?.toString() ?: ""
                    txtTopic.text = arrListTopic[wheelView.seletedIndex]
                }
                bottomSheerDialog.show()
            }
        } catch (e: Exception) {
           // println("error" + e.localizedMessage)
        }
    }

    private fun validation() {
        strEmail = edtEmail.text.toString()
        strName = edtName.text.toString()
        strPhone = edtPhone.text.toString()
        strComment = edtComments.text.toString()
        if (strTopic.isEmpty()) {
            Global.showSnackbar(
                this@ContactUsActivity,
                resources.getString(R.string.please_select_topic)
            )
        } else if (strName.isEmpty()) {
            Global.showSnackbar(
                this@ContactUsActivity,
                resources.getString(R.string.please_enter_your_name)
            )
        } else if (strEmail.isEmpty()) {
            Global.showSnackbar(this@ContactUsActivity, resources.getString(R.string.empty_email))
        } else if (!Global.isValidEmail(strEmail)) {
            Global.showSnackbar(
                this@ContactUsActivity,
                resources.getString(R.string.please_enter_valid_email_id)
            )
        } else if (strPhone.isEmpty()) {
            Global.showSnackbar(
                this@ContactUsActivity,
                resources.getString(R.string.enter_phone_number)
            )
        } else if (strPhone.length < 8) {
            Global.showSnackbar(
                this@ContactUsActivity,
                resources.getString(R.string.error_phone_length)
            )
        } else if (strComment.isEmpty()) {
            Global.showSnackbar(this@ContactUsActivity, resources.getString(R.string.error_comment))
        } else {
            contactUsApi()
        }
    }

    private fun setFont() {
        txtHead.typeface = Global.fontNavBar
        txtLabelEmail.typeface = Global.fontSemiBold
        txtEmail.typeface = Global.fontSemiBold
        txtLabelPhone.typeface = Global.fontSemiBold
        txtPhone.typeface = Global.fontSemiBold
        txtLabelFollowUs.typeface = Global.fontSemiBold

        txtGetInTouch.typeface = Global.fontMedium

        txtTopic.typeface = Global.fontRegular
        edtName.typeface = Global.fontRegular
        edtPhone.typeface = Global.fontRegular
        edtEmail.typeface = Global.fontRegular
        edtPhoneCode.typeface = Global.fontRegular
        edtComments.typeface = Global.fontRegular
        btnSend.typeface = Global.fontBtn
    }

    @SuppressLint("CheckResult")
    private fun contactUsApi() {
        if (NetworkUtil.getConnectivityStatus(this@ContactUsActivity) != 0) {
            //loading
            showProgressDialog(this@ContactUsActivity)

            Global.apiService.contactUs(
                strTopic,
                strName,
                strEmail,
                strPhoneCode + strPhone,
                strComment,
                com.app.blockaat.apimanager.WebServices.ContactUsWs
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->

                        if (result != null) {
                         //   println("RESPONSE - Contact Ws :   " + Gson().toJson(result))
                            if (result.status == 200) {
                                hideProgressDialog()
                                edtName.setText("")
                                edtEmail.setText("")
                                edtPhone.setText("")
                                edtComments.setText("")
                                txtTopic.text = ""
                                strEmail = ""
                                strName = ""
                                strComment = ""
                                strPhone = ""
                                strTopic = ""
                                Global.showSnackbar(
                                    this,
                                    resources.getString(R.string.contact_us_thank_you)
                                )

                            } else {
                                //some error in response like some field missing or any other status code
                                hideProgressDialog()
                                Global.showSnackbar(this@ContactUsActivity, result.message)
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(
                                this@ContactUsActivity,
                                resources.getString(R.string.error)
                            )
                        }
                    },
                    { error ->
                      //  println("ERROR - Login Ws :   " + error.localizedMessage)
                        hideProgressDialog()                                                             //error
                        Global.showSnackbar(
                            this@ContactUsActivity,
                            resources.getString(R.string.error)
                        )
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
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
            R.id.edtComments -> {
                if (hasFocus) {
                    txtInputComment.hint = resources.getString(R.string.comment).toUpperCase()
                } else {
                    if (edtComments.text.toString().isNotEmpty()) {
                        txtInputComment.hint =
                            resources.getString(R.string.comment).toUpperCase()
                    } else {
                        txtInputComment.hint = resources.getString(R.string.comment)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHONE_CODE_REQUEST) {
            if (data != null) {
                val phoneCode = data?.extras?.getString("phoneCode").toString()
                edtPhoneCode.setText(getString(R.string.phone_code, phoneCode))
                strPhoneCode = edtPhoneCode.text.toString()
            }
        }
    }

}
