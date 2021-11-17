package com.app.blockaat.Filter.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.tv.adapter.FilterListAdapter
import com.app.blockaat.tv.adapter.FilterListValueAdapter
import com.app.blockaat.tv.interfaces.OnFilterTypeClicked
import com.app.blockaat.tv.interfaces.OnFilterValueSelected
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.activity_filter.txtApply
import kotlinx.android.synthetic.main.toolbar_default.*

class FilterActivity : BaseActivity(), OnFilterTypeClicked, OnFilterValueSelected {

    private var arrListFilter: ArrayList<ProductListingFilterModel>? = null
    private var arrListFilterValue: ArrayList<ProductListingFilterValueModel>? = null
    private var adapterFilterList: FilterListAdapter? = null
    private var adapterFilterValue: FilterListValueAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        Global.setLocale(this@FilterActivity)
        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
    }

    private fun initializeToolbar() {
       Global.setBackArrow(this,ivBackArrow,txtHead,view)
        txtHead.text = resources.getString(R.string.filter)
    }

    private fun initializeFields()
    {
        arrListFilter = intent.getParcelableArrayListExtra("filterData")
        adapterFilterList = FilterListAdapter(arrListFilter as ArrayList<ProductListingFilterModel>,this@FilterActivity,this)
        rvFiler.layoutManager = LinearLayoutManager(this)
        rvFiler.adapter = adapterFilterList
        adapterFilterList?.notifyDataSetChanged()

        arrListFilterValue = ArrayList()
        if(!arrListFilter.isNullOrEmpty())
        {
            arrListFilterValue?.addAll(arrListFilter?.get(0)?.filter_values as Collection<ProductListingFilterValueModel>)
        }
        adapterFilterValue = FilterListValueAdapter(arrListFilterValue as ArrayList<ProductListingFilterValueModel>,this@FilterActivity,this)
        rvFilterValue.layoutManager = LinearLayoutManager(this)
        rvFilterValue.adapter = adapterFilterValue

    }

    private fun onClickListeners() {

        ivBackArrow.setOnClickListener {
            finish()
        }

        relClearAll.setOnClickListener {
            for(i in 0 until arrListFilter?.size as Int)
            {
                for(values in arrListFilter!![i].filter_values!!)
                {
                    values.isSelected = false
                }

            }

            adapterFilterValue?.notifyDataSetChanged()
        }

        relApply.setOnClickListener {
             val intent = Intent()
             intent.putParcelableArrayListExtra("filterData",arrListFilter)
             setResult(Activity.RESULT_OK,intent)
             finish()
        }

    }

    private fun setFonts() {

        txtHead.typeface = Global.fontNavBar
        txtApply.typeface = Global.fontMedium
        txtClearAll.typeface = Global.fontMedium
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

    override fun onFilterClicked(position: Int) {
        arrListFilterValue?.clear()
        arrListFilter?.get(position)?.filter_values?.let { arrListFilterValue?.addAll(it) }
        adapterFilterList?.notifyDataSetChanged()
        adapterFilterValue?.notifyDataSetChanged()
    }

    override fun OnFilterValueSelected(filterValue: ProductListingFilterValueModel) {

        for(value in arrListFilterValue!!)
        {
            if(value.id == filterValue.id)
            {
                value.isSelected = !value.isSelected
                break
            }
        }
            adapterFilterValue?.notifyDataSetChanged()
    }

}
