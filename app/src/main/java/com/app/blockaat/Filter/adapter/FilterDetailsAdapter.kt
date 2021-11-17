package com.app.blockaat.Filter.adapter

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.Filter.interfaces.SortFilterInterface
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import kotlinx.android.synthetic.main.item_filter.view.*
import kotlinx.android.synthetic.main.item_filter.view.relMain
import kotlinx.android.synthetic.main.item_filter.view.txtTitle
import kotlinx.android.synthetic.main.lv_item_filter_detail_data.view.*

class FilterDetailsAdapter(
    private val activity: Activity,
    private val selectedFilterPosition: Int,
    private val onFilterItemClicked: SortFilterInterface,
    private val isFromFildetail: Boolean,
    private val arrListFilterDataApi: ArrayList<ProductListingFilterModel?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //selectedFilterPosition - //this is main filter click position - means page one
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        selectedFilterItemPosition: Int
    ) {
        if (holder is ItemViewFragmentHolders) {
            holder.bind(
                activity,
                selectedFilterItemPosition,
                selectedFilterPosition,
                onFilterItemClicked,
                isFromFildetail,
                arrListFilterDataApi
            )
        }
    }

    override fun getItemCount(): Int =
        arrListFilterDataApi.get(selectedFilterPosition)?.filter_values?.count()
            ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_filter_detail_data, parent, false)
        return ItemViewFragmentHolders(itemView, parent.context)
    }
}

class ItemViewFragmentHolders(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun bind(
        activity: Activity,
        selectedFilterItemPosition: Int,
        selectedFilterPosition: Int,
        onFilterItemClicked: SortFilterInterface,
        isFromFildetail: Boolean,
        arrListFilterDataApi: ArrayList<ProductListingFilterModel?>
    ) {
        val x = arrListFilterDataApi.get(selectedFilterPosition)?.filter_values?.get(
            selectedFilterItemPosition
        )


        //set data
        itemView.txtTitle.text = x?.value?.toString()
        if (x?.isSelected == true) {
            itemView.txtTitle.typeface = Global.fontBold
            itemView.checkFilter.background =
                activity.resources.getDrawable(R.drawable.ic_checkbox_checked)
            itemView.checkFilter.backgroundTintList =
                ColorStateList.valueOf(activity.resources.getColor(R.color.black))

        } else {
            itemView.checkFilter.background =
                activity.resources.getDrawable(R.drawable.ic_checkbox_unchecked)
            itemView.txtTitle.typeface = Global.fontRegular
            itemView.checkFilter.backgroundTintList =
                ColorStateList.valueOf(activity.resources.getColor(R.color.filter_icon))

        }
        val mainParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )


        if (!isFromFildetail) {
            itemView.viewDivLine.visibility = GONE
            mainParams.topMargin = Global.getDimension(activity, 4)
            mainParams.bottomMargin = Global.getDimension(activity, 4)
        } else {
            itemView.viewDivLine.visibility = VISIBLE
            mainParams.topMargin = Global.getDimension(activity, 12)
            mainParams.bottomMargin = Global.getDimension(activity, 12)

        }
        itemView.relMain.layoutParams = mainParams

        //set fonts

        //set click listeners
        itemView.linMainHolder.setOnClickListener {
            if (x?.isSelected == true) {
                itemView.txtTitle.typeface = Global.fontRegular
                itemView.checkFilter.background =
                    activity.resources.getDrawable(R.drawable.ic_checkbox_unchecked)
                x.isSelected = false
                itemView.checkFilter.backgroundTintList =
                    ColorStateList.valueOf(activity.resources.getColor(R.color.filter_icon))

            } else {
                itemView.checkFilter.background =
                    activity.resources.getDrawable(R.drawable.ic_checkbox_checked)
                itemView.txtTitle.typeface = Global.fontBold
                x?.isSelected = true
                itemView.checkFilter.backgroundTintList =
                    ColorStateList.valueOf(activity.resources.getColor(R.color.black))

            }

            onFilterItemClicked.onItemSelect("filter", selectedFilterItemPosition)
        }
    }
}