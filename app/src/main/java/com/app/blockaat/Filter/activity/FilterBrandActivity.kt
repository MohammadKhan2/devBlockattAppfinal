package com.app.blockaat.Filter.activity

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.Filter.adapter.FilterBrandsAdapter
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import kotlinx.android.synthetic.main.activity_filter_brand.*


class FilterBrandActivity : BaseActivity() {
    private var filterAttributes: ArrayList<ProductListingFilterValueModel>? = null
    private var adapterFilterBrands: FilterBrandsAdapter? = null
    private var brandsPositionInFilterArray: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_brand)

        Global.setLocale(this@FilterBrandActivity)
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFonts()
        setData()
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
        filterAttributes = intent.getSerializableExtra("filterValues") as ArrayList<ProductListingFilterValueModel>
        brandsPositionInFilterArray = intent.getStringExtra("brandsPositionInFilterArray").toString()

        val layoutManager = LinearLayoutManager(this@FilterBrandActivity)
        rvFilterBrands.layoutManager = layoutManager
        rvFilterBrands.isNestedScrollingEnabled = false

        for (i in 0 until filterAttributes!!.size) {
            if (filterAttributes!![i].isSelected) {
                txtClear.text = resources.getString(R.string.clear)
                ivTick.visibility = View.GONE
                break
            } else {
                txtClear.text = resources.getString(R.string.all)
                ivTick.visibility = View.VISIBLE
            }
        }
    }

    private fun setFonts() {
        txtHeader.typeface = Global.fontRegular
        txtClear.typeface = Global.fontSemiBold
        txtResetAll.typeface = Global.fontLight
        txtNoBrand.typeface = Global.fontRegular
        txtBrandEmptyDetail.typeface = Global.fontRegular
        txtShowResult.typeface = Global.fontBtn
    }

    private fun setData() {
        txtHeader.text = intent.getStringExtra("toolbarHeader")
        if (filterAttributes != null && filterAttributes!!.isNotEmpty()) {
            relMainHolder.visibility = View.VISIBLE
            adapterFilterBrands =
                FilterBrandsAdapter(
                    this,
                    brandsPositionInFilterArray
                )
            rvFilterBrands.adapter = adapterFilterBrands


            relMainHolder.visibility = View.VISIBLE
            linNoItems.visibility = View.GONE
            relAll.visibility = View.GONE
            txtResetAll.visibility = View.VISIBLE
        } else {
            relMainHolder.visibility = View.GONE
            linNoItems.visibility = View.VISIBLE
            relAll.visibility = View.GONE
            txtResetAll.visibility = View.GONE
        }
    }

    private fun onClickListener() {
        relAll.setOnClickListener {
            if (txtClear.text == resources.getString(R.string.all)) {
                for (i in 0 until filterAttributes!!.size) {
                    filterAttributes!![i].isSelected = true
                    Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![i].isSelected = true
                }
                txtClear.text = resources.getString(R.string.clear)
                ivTick.visibility = View.GONE
            } else {
                for (i in 0 until filterAttributes!!.size) {
                    filterAttributes!![i].isSelected = false
                    Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![i].isSelected = false
                }
                txtClear.text = resources.getString(R.string.all)
                ivTick.visibility = View.VISIBLE
            }
            adapterFilterBrands!!.notifyDataSetChanged()
        }

        txtResetAll.setOnClickListener {
            for (i in 0 until filterAttributes!!.size) {
                filterAttributes!![i].isSelected = false
                Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![i].isSelected = false
            }
            adapterFilterBrands!!.notifyDataSetChanged()
        }
        txtShowResult.setOnClickListener {
            val i = Intent()
            i.putExtra("arrListSelected", Global.arrListFilter!!)
            setResult(AppCompatActivity.RESULT_OK, i)
            finish()
        }
    }


    //below is inbuilt function of android to manage activity toolbar arrow
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            val view = this.currentFocus
            if (view != null) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val i = Intent()
        i.putExtra("takeToListing", "no")
        setResult(1, i)
        finish()
    }
}
