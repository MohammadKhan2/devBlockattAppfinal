package com.app.blockaat.productlisting.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.Filter.interfaces.SortFilterInterface
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.interfaces.OnFilterValueClicked
import com.app.blockaat.productlisting.interfaces.OnSortClickListener
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import kotlinx.android.synthetic.main.item_filter.view.*
import kotlinx.android.synthetic.main.item_filter.view.txtTitle
import kotlinx.android.synthetic.main.lv_item_sort.view.*


class FilterListAdapter(
    val activity: Activity,
    private val arrListFilter: ArrayList<ProductListingFilterModel>,
    private val onSortFilterInterface: SortFilterInterface
) : RecyclerView.Adapter<FilterListAdapter.ViewHolder>() {
    private var arrListFilterValuesFull: List<ProductListingFilterModel>? = null

    init {
        arrListFilterValuesFull = ArrayList(arrListFilter)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activity, arrListFilter, position, onSortFilterInterface)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {

        fun bind(
            activity: Activity,
            arrListFilterValues: ArrayList<ProductListingFilterModel>,
            position: Int,
            onSortFilterInterface: SortFilterInterface
        ) {

            val filter = arrListFilterValues[position]

            if (position == arrListFilterValues.size - 1) {
                itemView.view_filter.visibility = View.GONE
            } else {
                itemView.view_filter.visibility = View.VISIBLE
            }

            if (filter?.filter_type == "price") {
                itemView.txtSubTitle.text = filter.filter_value
            } else {
                val selectedItems = filter.filter_values?.filter { it.isSelected }
                itemView.txtSubTitle.text = selectedItems?.joinToString { it.value.toString() }
            }

            itemView.txtTitle.text = Global.toCamelCase(filter.filter_name.toString())
            if (!Global.isEnglishLanguage(activity)) {
                itemView.ivFilterArrow.rotation = 180f
            }

            //set fonts
            itemView.txtSubTitle.typeface = Global.fontRegular
            itemView.txtTitle.typeface = Global.fontRegular

            itemView.setOnClickListener {
                onSortFilterInterface.onItemSelect("filter", position)
            }
        }

    }

    override fun getItemCount(): Int {
        return arrListFilter.size
    }

}