package com.app.blockaat.Filter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.blockaat.Filter.adapter.FilterDetailsAdapter
import com.app.blockaat.Filter.interfaces.SortFilterInterface
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import kotlinx.android.synthetic.main.activity_filter_detail.*
import kotlinx.android.synthetic.main.toolbar_default.*
import kotlinx.android.synthetic.main.toolbar_default.txtHead
import kotlinx.android.synthetic.main.toolbar_filter.*

class FilterDetailActivity : BaseActivity(), SortFilterInterface {
    private var isFrom: String = ""
    private var adapterFilterDetail: FilterDetailsAdapter? = null
    private var selectedFilterPosition = -1 //this is main filter click position - means page one
    private var arrListFilterDataApi: ArrayList<ProductListingFilterModel?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_detail)

        initializeToolbar()
        initializeFields()
        setFonts()
        onClickListeners()
        setData()
    }

    //initializing toolbar
    private fun initializeToolbar() {
        txtHead.text = resources.getString(R.string.filter)
        txtHead.visibility = View.VISIBLE
        ivBackArrow.visibility = View.VISIBLE

        if (intent.hasExtra("strTitle")) {
            txtHead.text = intent.getStringExtra("strTitle")
        }
        if (intent.hasExtra("isFrom")) {
            isFrom = intent.extras?.getString("isFrom").toString()
        }
        if (!Global.isEnglishLanguage(this@FilterDetailActivity)) {
            ivBackArrow.rotation = 180f
        }
    }

    private fun initializeFields() {
        if (intent.hasExtra("selectedPosition")) {
            selectedFilterPosition = intent.extras?.getString("selectedPosition").toString().toInt()
        }
        arrListFilterDataApi =
            intent.getSerializableExtra("filterData") as? ArrayList<ProductListingFilterModel?>
        rvFilterDataList.layoutManager =
            LinearLayoutManager(this@FilterDetailActivity, LinearLayoutManager.VERTICAL, false)


    }

    private fun setFonts() {
        txtHead.typeface = Global.fontNavBar
        //txtClear.typeface = Global.fontLight
        txtClear.typeface = Global.fontMedium

        txtShowAllResults.typeface = Global.fontBtn
    }

    private fun onClickListeners() {
        ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        txtClear.setOnClickListener {
//            txtClear.setTextColor(resources.getColor(R.color.secondary_text_color))

            for (d in 0 until (arrListFilterDataApi?.get(selectedFilterPosition)?.filter_values?.size
                ?: 0)) {
                arrListFilterDataApi?.get(selectedFilterPosition)?.filter_values?.get(d)?.isSelected =
                    false
            }

            if (adapterFilterDetail != null) {
                adapterFilterDetail?.notifyDataSetChanged()
            }
        }

        txtShowAllResults.setOnClickListener {
            val intent = Intent()
            intent.putExtra("filterData", arrListFilterDataApi)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


    }

    private fun setData() {
        if (arrListFilterDataApi?.get(selectedFilterPosition)?.filter_values != null && arrListFilterDataApi?.get(
                selectedFilterPosition
            )?.filter_values?.isNotEmpty() == true
        ) {
            adapterFilterDetail = FilterDetailsAdapter(
                this,
                selectedFilterPosition,
                this,
                true,
                arrListFilterDataApi as ArrayList<ProductListingFilterModel?>
            )
            rvFilterDataList.adapter = adapterFilterDetail

//            setClearTextColor()
        } else {
            //no data
            //txtClear.setTextColor(resources.getColor(R.color.secondary_text_color))

        }
    }

    private fun setClearTextColor() {
        var isFound = false
        for (x in 0 until (arrListFilterDataApi?.get(selectedFilterPosition)?.filter_values?.size
            ?: 0)) {
            if (arrListFilterDataApi?.get(selectedFilterPosition)?.filter_values?.get(x)?.isSelected == true) {
                isFound = true
                break
            }
        }

        if (isFound) {
            txtClear.setTextColor(resources.getColor(R.color.primary_text_color))
        } else {
            txtClear.setTextColor(resources.getColor(R.color.secondary_text_color))
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("filterData", arrListFilterDataApi)
        setResult(Activity.RESULT_OK, intent)
        this.finish()

    }

    override fun onItemSelect(type: String, position: Int) {

    }

}