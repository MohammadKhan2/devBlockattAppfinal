package com.app.blockaat.payment

import android.app.Activity
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.widget.Toolbar
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.orders.OrderSummaryActivity
import com.app.blockaat.orders.model.CheckoutDataModel
import kotlinx.android.synthetic.main.activity_payment_webview.*
import kotlinx.android.synthetic.main.toolbar_default.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class PaymentWebActivity : BaseActivity() {
    private var mToolbar: Toolbar? = null
    private lateinit var orderSummaryData: CheckoutDataModel
    private var strPaymentType = ""
    private var dialog: CustomProgressBar? = null
    private var isActivityLoaded: Boolean? = false
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_webview)

        Global.setLocale(this@PaymentWebActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {

            onBackPressed()
        }
    }

    //initializing toolbar
    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)

        txtHead.text = resources.getString(R.string.payment_gateway)
    }


    private fun initializeFields() {
        dialog = CustomProgressBar(this@PaymentWebActivity)
        AppController.instance.setLocale()
        orderSummaryData = intent.getSerializableExtra("orderSummaryData") as CheckoutDataModel

        wvPayment.settings.builtInZoomControls = false
        wvPayment.settings.useWideViewPort = true
        wvPayment.settings.javaScriptEnabled = true
        wvPayment.settings.setSupportMultipleWindows(true)
        wvPayment.settings.javaScriptCanOpenWindowsAutomatically = true
        wvPayment.settings.loadsImagesAutomatically = true
        wvPayment.settings.domStorageEnabled = true
        wvPayment.settings.loadWithOverviewMode = true
        wvPayment.loadUrl(orderSummaryData.payment_url!!)

        handleWebView()

        if (intent.hasExtra("paymentType")) {
            strPaymentType = intent.getStringExtra("paymentType")!!
        }

    }


    private fun handleWebView() {
        wvPayment.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                url: String
            ): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                relMainHolder.visibility = View.INVISIBLE
                showProgressDialog(this@PaymentWebActivity)
                hideProgressDialog()

            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                println("HERE IS URL " + url)

                if (url.contains(orderSummaryData.success_url.toString()) || url.contains(
                        orderSummaryData.error_url.toString()
                    )
                ) {
                    relMainHolder.visibility = View.INVISIBLE
                    val separated1 =
                        url.split("\\?".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
/*
                    separated1[0].trim { it <= ' ' }
                        .equals(orderSummaryData.error_url, ignoreCase = true)
*/
                    val obj = JSONObject()
                    if (separated1.size > 1) {
                        val separated =
                            separated1[1].split("&".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        var strJsonData: Array<String?>
                        for (i in separated.indices) {
                            strJsonData =
                                separated[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }
                                    .toTypedArray()
                            try {
                                obj.put(strJsonData[0], strJsonData[1])
                            } catch (e: JSONException) {
                                // TODO Auto-generated catch block
                                //e.printStackTrace()
                            } catch (e: Exception) {
                                // TODO: handle exception
                            }
                        }
                    }

                    val i = Intent(this@PaymentWebActivity, OrderSummaryActivity::class.java)
                    i.putExtra("orderSummaryData", orderSummaryData)
                    i.putExtra("paymentDetail", obj.toString())
                    i.putExtra("isFromVisaKNET", "yes")
                    i.putExtra("paymentType", strPaymentType)
                    hideProgressDialog()
                    startActivity(i)
                    finish()
                } else {

                    hideProgressDialog()
                    relMainHolder.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun showProgressDialog(activity: Activity) {

        if(!isFinishing())
        {
            dialog?.showDialog()
        }

    }

    private fun hideProgressDialog() {

        if(!isFinishing())
        {
            dialog?.hideDialog()
        }

    }


/*    private fun showProgressDialog(activity: Activity) {
        val v = activity.layoutInflater.inflate(R.layout.dialog_loading, null)
        //v.findViewById<TextView>(R.id.txtLoading).typeface = Global.fontBold
        progressDialog = Dialog(activity, R.style.MyTheme)
        progressDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog?.setContentView(v)
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (progressDialog != null && progressDialog?.isShowing == false) {
            progressDialog?.show()
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
        }
    }

    private fun hideProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }*/

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

    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config = newBase.resources.configuration
            //Update your config with the Locale i. e. saved in SharedPreferences
            config.setLocale(
                Locale(
                    Global.getStringFromSharedPref(this, Constants.PREFS_LANGUAGE).toLowerCase()
                )
            )
            newBase = newBase.createConfigurationContext(config)
        }
        super.attachBaseContext(newBase)
    }

    override fun onDestroy() {
      //  hideProgressDialog()
        super.onDestroy()
    }
}