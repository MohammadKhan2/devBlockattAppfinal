package com.app.blockaat.cms

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AbsoluteLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import com.app.blockaat.R
import com.app.blockaat.helper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cms.*
import kotlinx.android.synthetic.main.toolbar_default.*

class CmsActivity : BaseActivity() {
    private var disposable: Disposable? = null
    private var dialog: CustomProgressBar? = null
    private var mToolbar: Toolbar? = null
    private var strID: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cms)

        Global.setLocale(this@CmsActivity)
        initializeToolbar()
        initializeFields()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    //initializing toolbar
    private fun initializeToolbar() {
       /* mToolbar = toolbar_actionbar as Toolbar?
        setSupportActionBar(mToolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        var upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow)
        if (!Global.isEnglishLanguage(this)) {
            upArrow = ContextCompat.getDrawable(this, R.drawable.ic_back_arrow_ar)
        }
        upArrow?.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow?.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
*/
        Global.setBackArrow(this, ivBackArrow,txtHead,view)

        if (intent.hasExtra("header")) {
            txtHead.text = intent.getStringExtra("header")
        }

        if (intent.hasExtra("id")) {
            strID = intent.getStringExtra("id")!!
        }
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        AppController.instance!!.setLocale()
        txtHead.typeface = Global.fontNavBar


        wvAbout.getSettings().setJavaScriptEnabled(true);

        wvAbout.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                // Inject CSS on PageFinished
                injectCSS()
                super.onPageFinished(view, url)
            }
        })
        cmsApi(
            com.app.blockaat.apimanager.WebServices.CmsWs +strID /*intent.getStringExtra("id")*/ + "&lang=" + Global.getStringFromSharedPref(
                this@CmsActivity,
                Constants.PREFS_LANGUAGE
            )
        )
    }

    private fun cmsApi(strUrl: String) {
        if (NetworkUtil.getConnectivityStatus(this@CmsActivity) != 0) {
            showProgressDialog(this@CmsActivity)
            disposable = Global.apiService.getCmsData(strUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                      //  println("RESPONSE - CMS Ws :   " + result.data)
                        handleResponse(result.data.content)
                    },
                    { error ->
                        hideProgressDialog()
                        Global.showSnackbar(this@CmsActivity, resources.getString(R.string.error))
                       // println("ERROR - CMS Ws :   " + error.localizedMessage)
                    }
                )
        }
    }

    private fun handleResponse(strData: String) {

        wvAbout.settings.javaScriptEnabled = true
        wvAbout.webViewClient = MyWebViewClient(this@CmsActivity)
        wvAbout.loadDataWithBaseURL(
            "file:///android_asset/",
            Global.getWebViewData(this@CmsActivity, strData ?: ""),
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    //below is inbuilt function of android to manage activity toolbar arrow
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

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    private class MyWebViewClient(var cmsActivity: CmsActivity) : WebViewClient() {

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
    private fun injectCSS() {
        wvAbout.loadUrl(
            "javascript:document.body.style.margin=\"2%\"; void 0"
           /* "javascript:document.body.style.setProperty(\"color\", \"white\");"*/
        )

    }
}