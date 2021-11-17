package com.app.blockaat.Filter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Constants
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.activity_filter_price.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.text.DecimalFormat

class FilterPrice : BaseActivity() {

    private var maxPrice: String = "0.00"
    private var minPrice: String = "0.00"
    private var strOriginalMaxPrice: String = "0.00"

    private var strRange: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_price)
        initializeToolbar()
        initializeFields()
        setFonts()
        setOnClickListener()
    }

    private fun initializeToolbar() {
        txtHead.text = resources.getString(R.string.filter)
        txtHead.visibility = View.VISIBLE
        ivBackArrow.visibility = View.VISIBLE
        txtClose.visibility = View.VISIBLE
        txtClose.text = getString(R.string.clear_all)
    }


    private fun initializeFields() {
        if (!Global.isEnglishLanguage(this)) {
            txtMinKD.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_CURRENCY_AR).toString()
            txtMaxKD.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_CURRENCY_AR).toString()
        } else {
            txtMinKD.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_CURRENCY_EN).toString()
            txtMaxKD.text =
                Global.getStringFromSharedPref(this, Constants.PREFS_CURRENCY_EN).toString()
        }

        if (intent.hasExtra("maxPrice")) {
            if (intent.extras?.getString("maxPrice") != null) {
                maxPrice = intent.extras?.getString("maxPrice") as String
                println("Max price: " + maxPrice)
            }
        }
        if (intent.hasExtra("minPrice")) {
            if (intent.extras?.getString("minPrice") != null) {
                minPrice = intent.extras?.getString("minPrice") as String
                println("Min price: " + minPrice)
            }
        }
        if (intent.hasExtra("originalMaxPrice")) {
            if (intent.extras?.getString("originalMaxPrice") != null) {
                strOriginalMaxPrice = intent.extras?.getString("originalMaxPrice") as String
                println("Original price: " + strOriginalMaxPrice)
            }
        }

        if (intent.hasExtra("priceRange")) {
            if (intent.extras?.getString("priceRange") != null) {
                strRange = intent.extras?.getString("priceRange") as String
                println("Original price: " + strOriginalMaxPrice)
            }
        }

        if (!maxPrice.isNullOrEmpty() && maxPrice.toDouble() > 0) {


            edtMaxPrice.setText(maxPrice)
        } else {
            val price = DecimalFormat("##").format(strOriginalMaxPrice.toDouble())
            val newPrice =price.toInt()
          //  println("edtMaxPrice " + newPrice.toString())
            edtMaxPrice.setText(newPrice.toString())

        }

        edtMinPrice.setText(minPrice)
    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        txtClose.typeface = Global.fontRegular
        edtMinPrice.typeface = Global.fontRegular
        edtMaxPrice.typeface = Global.fontRegular
        txtMinKD.typeface = Global.fontRegular
        txtMaxKD.typeface = Global.fontRegular
        txtApply.typeface = Global.fontBtn
    }

    private fun setOnClickListener() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        edtMinPrice.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewMinPrice.visibility = View.VISIBLE
                //viewMaxPrice.visibility = View.GONE
            } else {
                viewMinPrice.visibility = View.GONE
            }
        }

        edtMaxPrice.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewMaxPrice.visibility = View.VISIBLE
                //viewMinPrice.visibility = View.GONE
            } else {
                viewMaxPrice.visibility = View.GONE
            }
        }

        txtApply.setOnClickListener {
            if (edtMinPrice.text?.isEmpty()!!) {
                Global.showSnackbar(this, getString(R.string.min_price_error))
                return@setOnClickListener
            } else if (edtMaxPrice.text?.isEmpty()!!) {
                Global.showSnackbar(this, getString(R.string.max_price_error))
                return@setOnClickListener
            } else /*if (edtMaxPrice.text.toString().toDouble() > strOriginalMaxPrice.toDouble()) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.max_price_less_error) + maxPrice

                )
                return@setOnClickListener
            } else*/ if (edtMaxPrice.text.toString().toDouble() < edtMinPrice.text.toString()
                    .toDouble()
            ) {
                Global.showSnackbar(
                    this,
                    resources.getString(R.string.max_less_then_min)
                )
                return@setOnClickListener
            } else {
                strRange = edtMinPrice.text.toString() + "-" + edtMaxPrice.text.toString()
            }
            maxPrice = edtMaxPrice.text.toString()
            minPrice = edtMinPrice.text.toString()

            val i = Intent()
            i.putExtra("priceRange", strRange)
            i.putExtra("maxPrice", maxPrice)
            i.putExtra("minPrice", minPrice)
            i.putExtra("originalMaxPrice", strOriginalMaxPrice)
            setResult(Activity.RESULT_OK, i)
            finish()
        }

        txtClose.setOnClickListener {
            edtMinPrice.text?.clear()
            edtMaxPrice.text?.clear()
        }

    }
}
