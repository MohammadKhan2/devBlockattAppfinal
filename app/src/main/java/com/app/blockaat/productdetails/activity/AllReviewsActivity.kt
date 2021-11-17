package com.app.blockaat.productdetails.activity

import android.content.Intent
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.productdetails.adapter.ReviewsAdapter
import com.app.blockaat.productdetails.model.ReviewsModel
import kotlinx.android.synthetic.main.activity_all_review.*
import kotlinx.android.synthetic.main.toolbar_default.*

class AllReviewsActivity : BaseActivity() {

    private val ADD_REVIEW_REQUEST: Int = 400
    private val ADD_REVIEW_RESULT: Int = 401
    private lateinit var adapterReviewListing: ReviewsAdapter
    private var arrListReviews: ArrayList<ReviewsModel> = arrayListOf()
    private var productId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_all_review)

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        ivAdd.visibility = VISIBLE
        txtHead.text = getString(R.string.all_reviews)
    }

    private fun initializeFields() {
        if (intent.hasExtra("reviews")) {
            arrListReviews = intent.extras?.get("reviews") as ArrayList<ReviewsModel>
        }
        if (intent.hasExtra("productId")) {
            productId = intent.extras?.getString("productId").toString()
        }
        adapterReviewListing = ReviewsAdapter(
            arrListReviews,
            this
        )
        val layoutManager = LinearLayoutManager(this)
        rvReviews.layoutManager = layoutManager
        rvReviews?.adapter = adapterReviewListing
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }
        ivAdd.setOnClickListener {
            val i = Intent(this, AddReviewActivity::class.java)
            i.putExtra("productId", productId)
            startActivityForResult(i, ADD_REVIEW_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_REVIEW_REQUEST && resultCode == ADD_REVIEW_RESULT) {
            val intent = Intent()
            setResult(ADD_REVIEW_RESULT, intent)
            finish()
        }
    }

}