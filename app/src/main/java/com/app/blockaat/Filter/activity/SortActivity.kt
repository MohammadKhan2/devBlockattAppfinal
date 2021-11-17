package com.app.blockaat.Filter.activity

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.activity_sort.*

class SortActivity : BaseActivity() {
    var strSortBy: String = "1"
    var strSortName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort)

        Global.setLocale(this@SortActivity)
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        val upArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        upArrow.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
        upArrow.setVisible(true, true)
        supportActionBar!!.setHomeAsUpIndicator(upArrow)
    }

    private fun initializeFields() {
        strSortBy = intent.getStringExtra("sortBy").toString()
      //  println("Here i am sort value is        " + strSortBy)

        if (strSortBy == "1") {
            txtDivRecommended.visibility = View.VISIBLE
            strSortName = txtRecommended.text.toString()
        } else if (strSortBy == "2") {
            txtDivNewIn.visibility = View.VISIBLE
            strSortName = txtNewIn.text.toString()
        } else if (strSortBy == "3") {
            txtDivPriceHighToLow.visibility = View.VISIBLE
            strSortName = txtPriceHighToLow.text.toString()
        } else if (strSortBy == "4") {
            txtDivPriceLowToHigh.visibility = View.VISIBLE
            strSortName = txtPriceLowToHigh.text.toString()
        }
    }

    private fun onClickListener() {
        relPriceHighToLow.setOnClickListener {
            if (txtDivPriceHighToLow.visibility != View.VISIBLE) {
                txtDivPriceHighToLow.visibility = View.VISIBLE
                txtDivPriceLowToHigh.visibility = View.INVISIBLE
                txtDivRecommended.visibility = View.INVISIBLE
                txtDivNewIn.visibility = View.INVISIBLE
                strSortBy = "3"
                strSortName = txtPriceHighToLow.text.toString()
            }
        }

        relPriceLowToHigh.setOnClickListener {
            if (txtDivPriceLowToHigh.visibility != View.VISIBLE) {
                txtDivPriceLowToHigh.visibility = View.VISIBLE
                txtDivPriceHighToLow.visibility = View.INVISIBLE
                txtDivRecommended.visibility = View.INVISIBLE
                txtDivNewIn.visibility = View.INVISIBLE
                strSortBy = "4"
                strSortName = txtPriceLowToHigh.text.toString()
            }
        }


        relRecommended.setOnClickListener {
            if (txtDivRecommended.visibility != View.VISIBLE) {
                txtDivRecommended.visibility = View.VISIBLE
                txtDivPriceLowToHigh.visibility = View.INVISIBLE
                txtDivPriceHighToLow.visibility = View.INVISIBLE
                txtDivNewIn.visibility = View.INVISIBLE
                strSortBy = "1"
                strSortName = txtRecommended.text.toString()
            }
        }


        relNewIn.setOnClickListener {
            if (txtDivNewIn.visibility != View.VISIBLE) {
                txtDivNewIn.visibility = View.VISIBLE
                txtDivPriceLowToHigh.visibility = View.INVISIBLE
                txtDivRecommended.visibility = View.INVISIBLE
                txtDivPriceHighToLow.visibility = View.INVISIBLE
                strSortBy = "2"
                strSortName = txtNewIn.text.toString()
            }
        }

        txtDone.setOnClickListener {
            val intent = Intent()
            intent.putExtra("sortBy", strSortBy)
            intent.putExtra("sortName", strSortName)
            setResult(AppCompatActivity.RESULT_OK, intent)
            finish()
        }
    }

    private fun setFonts() {
        txtDone.typeface = Global.fontLight
        txtPriceHighToLow.typeface = Global.fontRegular
        txtPriceLowToHigh.typeface = Global.fontRegular
        txtRecommended.typeface = Global.fontRegular
        txtNewIn.typeface = Global.fontRegular
        txtDivPriceHighToLow.typeface = Global.fontRegular
        txtDivPriceLowToHigh.typeface = Global.fontRegular
        txtDivRecommended.typeface = Global.fontRegular
        txtDivNewIn.typeface = Global.fontRegular
        txtSortBy.typeface = Global.fontMedium
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
