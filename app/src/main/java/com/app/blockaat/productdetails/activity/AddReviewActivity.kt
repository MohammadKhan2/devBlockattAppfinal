package com.app.blockaat.productdetails.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.app.blockaat.R
import com.app.blockaat.helper.*
import com.app.blockaat.productdetails.model.AddReviewRequestModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_review.*
import kotlinx.android.synthetic.main.toolbar_default.*

class AddReviewActivity : BaseActivity() {
    private val ADD_REVIEW_RESULT: Int = 401
    private var productId: String = ""
    private var dialog: CustomProgressBar? = null
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_review)

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        txtHead.text = getString(R.string.add_review)
    }

    private fun initializeFields() {
        dialog = CustomProgressBar(this)
        if (intent.hasExtra("productId")) {
            productId = intent?.extras?.getString("productId").toString()
        }
        val drawable: Drawable? =
            resources?.getDrawable(R.drawable.ic_star_empty)
        drawable?.mutate()
            ?.setColorFilter(
                resources.getColor(R.color.dark_gray),
                PorterDuff.Mode.SRC_IN
            )
        drawable?.let { simpleRatingBar.setEmptyDrawable(it) }
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtNote.typeface = Global.fontSemiBold
        txtSubmit.typeface = Global.fontBtn
        edtTitle.typeface = Global.fontRegular
        edtComments.typeface = Global.fontRegular
    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        txtSubmit.setOnClickListener {
            if (edtTitle.text.toString().isEmpty()) {
                Global.hideKeyboard(this)
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.please_enter_title)
                )
            } else if (simpleRatingBar.rating.toDouble() <= 0.0) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.please_give_rating)
                )
            } else if (edtComments.text.toString().isEmpty()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.error_comment)
                )
            } else {
                submitReview()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun submitReview() {
        if (NetworkUtil.getConnectivityStatus(this) != 0) {
            showProgressDialog()
            val requestModel = AddReviewRequestModel(
                comment = edtComments.text.toString(),
                product_id = productId,
                rating = simpleRatingBar.rating.toString(),
                title = edtTitle.text.toString(),
                user_id = Global.getUserId(this)
            )
            disposable = Global.apiService.submitReview(
                requestModel,
                com.app.blockaat.apimanager.WebServices.SubmitReviewWs + Global.getStoreCode(
                    this
                )
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        hideProgressDialog()
                        if (result.status == 200 && result.data != null) {

                            val intent = Intent()
                            setResult(ADD_REVIEW_RESULT, intent)
                            finish()
                        } else {
                            Global.showSnackbar(
                                this,
                                result.message.toString()
                            )
                        }
                    },
                    { error ->
                        hideProgressDialog()
                        Global.showSnackbar(
                            this,
                            resources.getString(R.string.error)
                        )
                    })
        }
    }

    private fun showProgressDialog() {
        dialog?.showDialog()
    }

    private fun hideProgressDialog() {
        dialog?.hideDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}