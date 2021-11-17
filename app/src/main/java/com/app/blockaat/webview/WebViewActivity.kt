package com.app.blockaat.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.app.blockaat.R
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.activity_webview.*
import kotlinx.android.synthetic.main.toolbar_default.*


class WebViewActivity : BaseActivity() {
    private var dialog: CustomProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()
    }


    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow,txtHead,view)

        /*setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        var upArrow = ContextCompat.getDrawable(this@WebViewActivity, R.drawable.ic_back_arrow)
        if(!Global.isEnglishLanguage(this@WebViewActivity))
        {
            upArrow = ContextCompat.getDrawable(this@WebViewActivity, R.drawable.ic_back_arrow_ar)
        }
        upArrow?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow?.setVisible(true, true)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)*/
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun initializeFields() {
        AppController.instance.setLocale()
        txtHead.text = intent.getStringExtra("text_header")
        webView.loadUrl(intent.getStringExtra("strUrl")!!)
        handleWebView()
        showProgressDialog(this@WebViewActivity)
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun handleWebView() {
        val settings = webView.settings
        settings.builtInZoomControls = false
        settings.useWideViewPort = true
        settings.javaScriptEnabled = true
        settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
        settings.loadsImagesAutomatically = true
        settings.lightTouchEnabled = true
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true

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
        dialog = CustomProgressBar(activity)
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        if (dialog != null) {
            dialog?.hideDialog()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            this.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}