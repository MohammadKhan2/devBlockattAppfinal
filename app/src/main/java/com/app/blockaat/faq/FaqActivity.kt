package com.app.blockaat.faq


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.view.View

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.blockaat.R
import com.app.blockaat.faq.adapter.FaqAdapter

import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.faq.model.FaqResponseModel
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.CustomProgressBar
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.NetworkUtil

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_faq.*

import kotlinx.android.synthetic.main.toolbar_default.*

class FaqActivity : BaseActivity() {
    private val arrListFaq: ArrayList<FaqDataModel> = arrayListOf()
    private var dialog: CustomProgressBar? = null

    private var rcyFaq: RecyclerView? = null
    private var isFromRefresh = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_faq)
        Global.setLocale(this@FaqActivity)
        initializeToolbar()
        setFont()
        initializeFields()
        setOnClickListener()

    }

    private fun setFont() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = resources.getString(R.string.faqs_title)

    }
    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this@FaqActivity)
        rcyFaq = findViewById(R.id.rcyFaq)
        swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            isFromRefresh = true
            swipeRefreshLayout.isRefreshing = true
           swipeRefreshLayout.postDelayed(Runnable {
                swipeRefreshLayout.isRefreshing = false

                if (NetworkUtil.getConnectivityStatus(this@FaqActivity!!) != 0) {
                    getFaq()
                } else {
                    Global.showSnackbar(this@FaqActivity!!, resources.getString(R.string.connection_error))
                }
            }, 1000)
        })
        getFaq()
    }
    ////getting faq page data
    @SuppressLint("CheckResult")
    private fun getFaq() {
        if (NetworkUtil.getConnectivityStatus(this@FaqActivity) != 0) {
            //loading
            if (!isFromRefresh)
                showProgressDialog(this@FaqActivity)

            Global.apiService.getFaqData(
                com.app.blockaat.apimanager.WebServices.FaqWs + "lang=" + Global.getLanguage(
                    this@FaqActivity
                ) + "&store=" + Global.getStoreCode(
                    this@FaqActivity
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse, this::handleError)
        } else {
            Global.showSnackbar(this@FaqActivity, resources.getString(R.string.connection_error))
        }
    }
    ////

    ///handling success response
    private fun handleResponse(model: FaqResponseModel?) {

        if (!isFromRefresh)
            hideProgressDialog()

       // println("Success")

        if (model != null) {

            if (model.status == 200) {
                arrListFaq.clear()
                    /*arrListFaq.add(FaqDataModel(1, "Shipping and delivery", "Orders are processed between 1-2 business days after receipt of payment. For example, if an order is placed after 9am, we will aim to process and dispatch your order the following day. Once dispatched, delivery can be expected between 2-3 business days for major centers. For outlying areas, please allow a further 2-5 business days depending."))
                    arrListFaq.add(FaqDataModel(2, "Returns and refunds", "Orders are processed between 1-2 business days after receipt of payment. For example, if an order is placed after 9am, we will aim to process and dispatch your order the following day. Once dispatched, delivery can be expected between 2-3 business days for major centers. For outlying areas, please allow a further 2-5 business days depending."))
                    arrListFaq.add(FaqDataModel(3, "payment, pricing and promotions", "Orders are processed between 1-2 business "))*/
                model.data.let { arrListFaq.addAll(it) }
                rcyFaq?.visibility = View.VISIBLE
                val layoutManager = GridLayoutManager(this@FaqActivity, 1)
                val adapter = FaqAdapter(arrListFaq, this@FaqActivity)
                rcyFaq?.layoutManager = layoutManager
                rcyFaq?.adapter = adapter

            } else {
                rcyFaq?.visibility = View.GONE
            }
        }
    }

    ///handling error
    private fun handleError(error: Throwable) {

        if (!isFromRefresh)
            hideProgressDialog()

      //  println("Error : " + error.localizedMessage)
    }
    ////
    private fun showProgressDialog(activity: AppCompatActivity) {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}