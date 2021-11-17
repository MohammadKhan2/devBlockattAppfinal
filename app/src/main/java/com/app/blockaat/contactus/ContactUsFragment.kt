package com.app.blockaat.contactus

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson

import com.app.blockaat.R
import com.app.blockaat.address.CountryListActivity
import com.app.blockaat.helper.*
import com.app.blockaat.navigation.NavigationActivity
import com.app.blockaat.webview.WebViewActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_contact_us.btnSend
import kotlinx.android.synthetic.main.fragment_contact_us.edtComments
import kotlinx.android.synthetic.main.fragment_contact_us.edtEmail
import kotlinx.android.synthetic.main.fragment_contact_us.edtName
import kotlinx.android.synthetic.main.fragment_contact_us.edtPhone
import kotlinx.android.synthetic.main.fragment_contact_us.edtPhoneCode
import kotlinx.android.synthetic.main.fragment_contact_us.txtEmail
import kotlinx.android.synthetic.main.fragment_contact_us.txtGetInTouch
import kotlinx.android.synthetic.main.fragment_contact_us.txtLabelEmail
import kotlinx.android.synthetic.main.fragment_contact_us.txtLabelFollowUs
import kotlinx.android.synthetic.main.fragment_contact_us.txtLabelPhone
import kotlinx.android.synthetic.main.fragment_contact_us.txtPhone
import kotlinx.android.synthetic.main.fragment_contact_us.txtTopic
import kotlinx.android.synthetic.main.fragment_contact_us.view.*


class ContactUsFragment : Fragment(), View.OnFocusChangeListener {
    private var rootView: View? = null
    private var strTopic: String = ""
    private var arrListTopic = ArrayList<String>(0)
    private var dialog: CustomProgressBar? = null
    private lateinit var mActivity: NavigationActivity
    private var strName = ""
    private var strEmail = ""
    private var strComment = ""
    private var strPhone = ""
    private var strPhoneCode = ""
    private val PHONE_CODE_REQUEST: Int = 103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as NavigationActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        rootView = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        onClickListener(view)
        setFont(view)
        setHints(view)

    }

    private fun setHints(view: View) {
        view.edtName.onFocusChangeListener = this
        view.edtEmail.onFocusChangeListener = this
        view.edtPhone.onFocusChangeListener = this
        view.edtComments.onFocusChangeListener = this
    }

    private fun init(rootView: View) {
        rootView.txtEmail.text = Global.strSupportEmail
        rootView.txtPhone.text = Global.strSupportPhone
//        rootView.edtPhoneCode.isEnabled = false
        dialog = CustomProgressBar(mActivity)
        rootView.edtComments.imeOptions = EditorInfo.IME_ACTION_DONE
        rootView.edtComments.setRawInputType(InputType.TYPE_CLASS_TEXT)
        arrListTopic =
            mActivity?.resources?.getStringArray(R.array.topic_array)?.toList() as ArrayList<String>
        if (!Global.isEnglishLanguage(mActivity)) {
            txtPhone.gravity = View.TEXT_ALIGNMENT_TEXT_START
        }
    }


    private fun setFont(rootView: View) {
        txtLabelEmail.typeface = Global.fontSemiBold
        txtEmail.typeface = Global.fontSemiBold
        txtLabelPhone.typeface = Global.fontSemiBold
        txtPhone.typeface = Global.fontSemiBold
        txtLabelFollowUs.typeface = Global.fontSemiBold
        txtTopic.typeface = Global.fontRegular

        txtGetInTouch.typeface = Global.fontMedium
        edtName.typeface = Global.fontRegular
        edtPhone.typeface = Global.fontRegular
        edtEmail.typeface = Global.fontRegular
        edtPhoneCode.typeface = Global.fontRegular
        edtComments.typeface = Global.fontRegular
        btnSend.typeface = Global.fontBtn

    }

    private fun onClickListener(rootView: View) {
        rootView.btnSend.setOnClickListener {
            validation(rootView)
        }

        rootView.relTopic.setOnClickListener {
            openTopicPicker()
        }
        rootView.edtPhoneCode.setOnClickListener {
            val i = Intent(mActivity, CountryListActivity::class.java)
            startActivityForResult(i, PHONE_CODE_REQUEST)
        }
        rootView.ivInstagram.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.instagram))
            intent.putExtra("strUrl", Global.INSTAGRAM_LINK)
            startActivity(intent)
        }

        rootView.ivFaceBook.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.facebook))
            intent.putExtra("strUrl", Global.FACEBOOK_LINK)
            startActivity(intent)
        }

        rootView.ivTwitter.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.twitter))
            intent.putExtra("strUrl", Global.TWITTER_LINK)
            startActivity(intent)
        }

        rootView.ivYoutube.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.youtube))
            intent.putExtra("strUrl", Global.YOUTUBE_LINK)
            startActivity(intent)
        }

        rootView.ivGooglePlus.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.google_plus))
            intent.putExtra("strUrl", Global.GOOGLE_PLUS_LINK)
            startActivity(intent)
        }

        rootView.ivSnapchat.setOnClickListener {
            val intent = Intent(mActivity, WebViewActivity::class.java)
            intent.putExtra("text_header", resources.getString(R.string.snapchat))
            intent.putExtra("strUrl", Global.SNAPCHAT_LINK)
            startActivity(intent)
        }
    }

    private fun openTopicPicker() {
        try {
            val parentView = layoutInflater.inflate(R.layout.layout_bottom_dialog_attribute, null)
            val bottomSheerDialog = BottomSheetDialog(mActivity)
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
                wheelView.setItems(
                    arrListTopic,
                    Global.getDimenVallue(mActivity, 5.0).toInt()
                )
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
         //   println("error" + e.localizedMessage)
        }
    }


    private fun validation(rootView: View) {
        strEmail = rootView.edtEmail.text.toString()
        strName = rootView.edtName.text.toString()
        strPhone = rootView.edtPhone.text.toString()
        strComment = rootView.edtComments.text.toString()

        if (strTopic.isEmpty()) {
            Global.showSnackbar(mActivity!!, resources.getString(R.string.please_select_topic))
        } else if (strName.isEmpty()) {
            Global.showSnackbar(mActivity!!, resources.getString(R.string.please_enter_your_name))
        } else if (strEmail.isEmpty()) {
            Global.showSnackbar(
                mActivity!!,
                resources.getString(R.string.Please_enter_email_address)
            )
        } else if (!Global.isValidEmail(strEmail)) {
            Global.showSnackbar(
                mActivity!!,
                resources.getString(R.string.please_enter_a_valid_email_id)
            )
        } else if (strPhone.isEmpty()) {
            Global.showSnackbar(mActivity!!, resources.getString(R.string.error_phone))
        } else if (strComment.isEmpty()) {
            Global.showSnackbar(mActivity!!, resources.getString(R.string.error_comment))
        } else {
            contactUsApi(rootView)
        }
    }


    @SuppressLint("CheckResult")
    private fun contactUsApi(rootView: View) {
        if (NetworkUtil.getConnectivityStatus(mActivity!!) != 0) {
            //loading
            showProgressDialog(mActivity!!)

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
                                //successfully logged in
//                                showSuccessAlert(mActivity!!, rootView)
                                rootView.edtName.setText("")
                                rootView.edtEmail.setText("")
                                rootView.edtPhone.setText("")
                                rootView.edtComments.setText("")
                                rootView.txtTopic.text = ""
                                strEmail = ""
                                strName = ""
                                strComment = ""
                                strPhone = ""
                                strTopic = ""
                                Global.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.contact_us_thank_you)
                                )
                            } else {
                                //some error in response like some field missing or any other status code
                                hideProgressDialog()
                                Global.showSnackbar(mActivity!!, result.message)
                            }
                        } else {
                            //if ws not working
                            hideProgressDialog()
                            Global.showSnackbar(mActivity!!, resources.getString(R.string.error))
                        }
                    },
                    { error ->
                       // println("ERROR - Login Ws :   " + error.localizedMessage)
                        hideProgressDialog()                                                             //error
                        Global.showSnackbar(mActivity!!, resources.getString(R.string.error))
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        txtTopic.text = ""
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        when (v?.id) {
            R.id.edtName -> {
                if (hasFocus) {
                    rootView?.txtInputName?.hint =
                        resources.getString(R.string.full_name).toUpperCase()
                } else {
                    if (rootView?.edtName?.text.toString().isNotEmpty()) {
                        rootView?.txtInputName?.hint =
                            resources.getString(R.string.full_name).toUpperCase()
                    } else {
                        rootView?.txtInputName?.hint = resources.getString(R.string.full_name)
                    }
                }
            }
            R.id.edtEmail -> {
                if (hasFocus) {
                    rootView?.txtInputEmail?.hint =
                        resources.getString(R.string.email_address).toUpperCase()
                } else {
                    if (rootView?.edtEmail?.text.toString().isNotEmpty()) {
                        rootView?.txtInputEmail?.hint =
                            resources.getString(R.string.email_address).toUpperCase()
                    } else {
                        rootView?.txtInputEmail?.hint = resources.getString(R.string.email_address)
                    }
                }
            }
            R.id.edtPhone -> {
                if (hasFocus) {
                    rootView?.txtInputMobile?.hint =
                        resources.getString(R.string.phone_number).toUpperCase()
                } else {
                    if (rootView?.edtPhone?.text.toString().isNotEmpty()) {
                        rootView?.txtInputMobile?.hint =
                            resources.getString(R.string.phone_number).toUpperCase()
                    } else {
                        rootView?.txtInputMobile?.hint = resources.getString(R.string.phone_number)
                    }
                }
            }
            R.id.edtComments -> {
                if (hasFocus) {
                    rootView?.txtInputComment?.hint =
                        resources.getString(R.string.comment).toUpperCase()
                } else {
                    if (rootView?.edtComments?.text.toString().isNotEmpty()) {
                        rootView?.txtInputComment?.hint =
                            resources.getString(R.string.comment).toUpperCase()
                    } else {
                        rootView?.txtInputComment?.hint = resources.getString(R.string.comment)
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
                rootView?.edtPhoneCode?.setText(getString(R.string.phone_code, phoneCode))
                strPhoneCode = edtPhoneCode.text.toString()
            }
        }
    }
}
