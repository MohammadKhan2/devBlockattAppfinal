package com.app.blockaat.Filter.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.BuildConfig
import com.app.blockaat.Filter.adapter.SortAdapter
import com.app.blockaat.Filter.interfaces.SortFilterInterface
import com.app.blockaat.Filter.model.SortModel

import com.app.blockaat.R
import com.app.blockaat.helper.AppController
import com.app.blockaat.helper.Constants
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.adapter.FilterListAdapter
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import kotlinx.android.synthetic.main.activity_sort_filter.*
import kotlinx.android.synthetic.main.lv_item_state_list_header.view.*
import kotlinx.android.synthetic.main.toolbar_default.*
import java.util.ArrayList


class SortFilterActivity : AppCompatActivity(), SortFilterInterface {
    private val filterDetailsCode: Int = 100
    private lateinit var sortAdapter: SortAdapter
    private lateinit var filterAdapter: FilterListAdapter
    private val arrListSort: ArrayList<SortModel> = arrayListOf()
    private var arrListFilter: ArrayList<ProductListingFilterModel> = arrayListOf()
    private var arrListFilterValue: ArrayList<ProductListingFilterValueModel> = arrayListOf()
    private var strPriceRange: String = ""
    private var maxPrice: String = ""
    private var strSortBy: String = "1"
    private var minPrice: String = ""
    private var isFrom: String = ""
    private val PRICE_VALUE_REQUEST_CODE: Int = 200
    private var noOfFilteredApplied = 0
    private var strOriginalMaxPrice: String = "0.0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sort_filter)

        if (!Global.isEnglishLanguage(this@SortFilterActivity)) {
            relSort.layoutDirection = View.LAYOUT_DIRECTION_RTL;
        } else {
            relSort.layoutDirection = View.LAYOUT_DIRECTION_LTR;
        }
        initializeToolbar()
        initializeFields()
        onClickListener()
        setFont()
    }

    private fun initializeToolbar() {
        Global.setBackArrow(this, ivBackArrow, txtHead, view)
        ivBackArrow.visibility = GONE
        imgClose.visibility = VISIBLE
        txtClose.visibility = VISIBLE

        if (!Global.isEnglishLanguage(this@SortFilterActivity)) {
            txtHead.text = "رتب"
            txtClose.text = "مسح الكل"
            txtSortBy.text = "ترتيب حسب"
            txtFilterByLabel.text = "تصنيف حسب"
        }else{
            txtHead.text = "Refine"
            txtClose.text = "Clear all"
            txtSortBy.text = "Sort by"
            txtFilterByLabel.text = "Filter by"

            /* txtHead.text = getString(R.string.refine)
             txtClose.text = getString(R.string.clear_all)*/
        }

        txtClose.setTextColor(resources.getColor(R.color.blue))

    }

    private fun initializeFields() {


        if (intent.hasExtra("filterData")) {
            arrListFilter =
                intent?.getSerializableExtra("filterData") as ArrayList<ProductListingFilterModel>
        }
        if (intent.hasExtra("strPriceRange")) {
            strPriceRange =
                intent.extras?.getString("strPriceRange").toString()
        }
        if (intent.hasExtra("sortBy")) {
            strSortBy =
                intent.extras?.getString("sortBy").toString()
        }

        if (intent.hasExtra("originalMaxPrice")) {
            strOriginalMaxPrice = intent.extras?.getString("originalMaxPrice").toString()
        }
        if (intent.hasExtra("maxPrice")) {
            maxPrice = intent.extras?.getString("maxPrice").toString()
        }
        if (intent.hasExtra("minPrice")) {
            minPrice = intent.extras?.getString("minPrice").toString()
        }
        if (intent.hasExtra("isFrom")) {
            isFrom = intent.extras?.getString("isFrom").toString()
        }

        //sort
        rvSort.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        sortAdapter = SortAdapter(this, arrListSort, this)
        rvSort.adapter = sortAdapter

        if (!Global.isEnglishLanguage(this@SortFilterActivity)) {

            arrListSort.add(SortModel("اختياراتنا", false, "1"))
            arrListSort.add(SortModel("منتجات جديدة", false, "2"))
            arrListSort.add(SortModel("السعر من الأعلى الى الأدنى", false, "3"))
            arrListSort.add(SortModel("السعر من الأدنى الى الأعلى", false, "4"))
            txtApply.text="تطبيق/تنفيذ"
        }else{

            arrListSort.add(SortModel(getString(R.string.our_picks), false, "1"))
            arrListSort.add(SortModel(getString(R.string.new_items), false, "2"))
            arrListSort.add(SortModel(getString(R.string.price_high_to_low), false, "3"))
            arrListSort.add(SortModel(getString(R.string.price_low_to_high), false, "4"))
        }



        for (i in 0 until arrListSort.size) {
            if (strSortBy == arrListSort[i].value) {
                arrListSort[i].isSelected = true
            }
        }
        sortAdapter?.notifyDataSetChanged()


        //filter
        rvFilter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        filterAdapter = FilterListAdapter(this, arrListFilter, this)
        rvFilter.adapter = filterAdapter
        filterAdapter?.notifyDataSetChanged()

    }

    private fun onClickListener() {
        imgClose.setOnClickListener {
            onBackPressed()
        }
        txtClose.setOnClickListener {
            for (i in 0 until arrListFilter?.size as Int) {
                for (values in arrListFilter!![i].filter_values!!) {
                    values.isSelected = false
                }
                arrListFilter[i].filter_value = ""
            }
            filterAdapter?.notifyDataSetChanged()


            for (i in 0 until arrListSort.size) {
                if (strSortBy == arrListSort[i].value) {
                    arrListSort[i].isSelected = false
                }
            }
            sortAdapter?.notifyDataSetChanged()
        }
        txtApply.setOnClickListener { applyFilter() }

    }


    private fun setFont() {
        txtClose.typeface = Global.fontRegular
        txtHead.typeface = Global.fontNavBar
        txtSortBy.typeface = Global.fontMedium
        txtApply.typeface = Global.fontBtn
        txtFilterByLabel.typeface = Global.fontMedium


    }

    override fun onItemSelect(type: String, position: Int) {
        if (type == "sort") {
            for (i in 0 until arrListSort.size) {
                arrListSort[i].isSelected = false
            }
            strSortBy = arrListSort[position]?.value.toString()
            arrListSort[position].isSelected = true
            sortAdapter?.notifyDataSetChanged()
        } else if (type == "filter") {
            val a = arrListFilter[position]
            if (a?.filter_type == "price") {
                val i = Intent(this, FilterPrice::class.java)
                i.putExtra("originalMaxPrice", strOriginalMaxPrice)
                i.putExtra("maxPrice", maxPrice)
                i.putExtra("minPrice", minPrice)
                i.putExtra("priceRange", strPriceRange)

                startActivityForResult(i, PRICE_VALUE_REQUEST_CODE)
            } else {
                val intent = Intent(this, FilterDetailActivity::class.java)
                intent.putExtra("strTitle", arrListFilter.get(position)?.filter_name ?: "")
                intent.putExtra("selectedPosition", position.toString())
                intent.putExtra("filterData", arrListFilter)
//            intent.putExtra(
//                "isFrom",
//            )
                startActivityForResult(intent, filterDetailsCode)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == filterDetailsCode && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                arrListFilter.clear()
                val newList =
                    data.getSerializableExtra("filterData") as ArrayList<ProductListingFilterModel>
//                setSelectedFilterValues()
                newList.let { arrListFilter.addAll(it) }
                filterAdapter.notifyDataSetChanged()
            }
        }
        if (requestCode == PRICE_VALUE_REQUEST_CODE && data != null) {
            strPriceRange = data.extras?.getString("priceRange") as String
            maxPrice = data.extras?.getString("maxPrice") as String
            minPrice = data.extras?.getString("minPrice") as String
            strOriginalMaxPrice = data.extras?.getString("originalMaxPrice") as String
            if (arrListFilter[arrListFilter.size - 1]?.filter_type == "price") {
                arrListFilter[arrListFilter.size - 1]?.filter_value = strPriceRange
            }

            filterAdapter.notifyDataSetChanged()

        }
    }


    fun applyFilter() {

        for (i in 0 until arrListFilter.size) {
            for (j in 0 until (arrListFilter[i]?.filter_values?.size ?: 0)) {
                if (arrListFilter[i]?.filter_values?.get(j)?.isSelected == true) {
                    noOfFilteredApplied += 1
                }
            }
            if (strPriceRange.isNotEmpty()) {
                noOfFilteredApplied += 1
            }

        }

        val i = Intent()
        i.putExtra("applyFilter", "yes")
        i.putExtra("noFilterApplied", noOfFilteredApplied)
        i.putExtra("filterData", arrListFilter)
        i.putExtra("minPrice", minPrice)
        i.putExtra("maxPrice", maxPrice)
        i.putExtra("sortBy", strSortBy)
        i.putExtra("priceRange", strPriceRange)
        i.putExtra("originalMaxPrice", strOriginalMaxPrice)
        setResult(Activity.RESULT_OK, i)
        finish()
    }

}