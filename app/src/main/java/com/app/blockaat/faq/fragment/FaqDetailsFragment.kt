package com.app.blockaat.faq.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.blockaat.R
import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.Global
import com.app.blockaat.navigation.NavigationActivity
import kotlinx.android.synthetic.main.fragment_faq_details.*

class FaqDetailsFragment : Fragment() {
    private var faqModel: FaqDataModel? = null
    private lateinit var mActvity: NavigationActivity
    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActvity = activity as NavigationActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_faq_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeFields()
        setOnClickListener()
        setFont()
    }

    private fun initializeFields() {
//        dialog = CustomProgressBar(mActvity as AppCompatActivity)
//        showProgressDialog(mActvity as AppCompatActivity)
//        handleWebView()
    }

    private fun setOnClickListener() {

    }

    private fun setFont() {
        txtQuestion.typeface = Global.fontMedium
    }

    fun handleWebView() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return !(url.startsWith("http:") || url.startsWith("https:"))
            }

            override fun onReceivedError(
                view: WebView,
                errorCode: Int,
                description: String,
                failingUrl: String
            ) {
                hideProgressDialog()
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {

            }

            override fun onPageFinished(view: WebView, lk: String) {
                hideProgressDialog()
            }
        }
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    private fun setFaqDetails() {
        txtQuestion.text = faqModel?.question ?: ""

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = MyWebViewClient(mActvity as NavigationActivity)
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            Global.getWebViewData(mActvity as NavigationActivity, faqModel?.answer ?: ""),
            "text/html",
            "UTF-8",
            null
        )
      // if(isVisible) mActvity.txtHead.text = faqModel?.question ?: ""
//        txtAnswer.text = Global.getWebViewData(mActvity as AppCompatActivity,faqModel?.answer?:"")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (arguments?.containsKey("faqModel") == true) {
                faqModel = arguments?.get("faqModel") as FaqDataModel
                setFaqDetails()
            }
        }
    }

    private class MyWebViewClient(var cmsActivity: NavigationActivity) : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

            return false

        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

            //cmsActivity.showProgressDialog("F")
        }

        override fun onPageFinished(view: WebView?, url: String?) {

            // cmsActivity.hideProgressDialog()
        }
    }

}