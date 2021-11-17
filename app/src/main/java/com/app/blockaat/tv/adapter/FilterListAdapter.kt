package com.app.blockaat.tv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterModel
import com.app.blockaat.tv.interfaces.OnFilterTypeClicked
import kotlinx.android.synthetic.main.item_filter_list.view.*

class FilterListAdapter(
    private val arrListFilter: ArrayList<ProductListingFilterModel>,
    private val context: Context?,
    private val onFilterClicked: OnFilterTypeClicked
) : RecyclerView.Adapter<FilterListAdapter.FilterViewHolder>() {
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_filter_list, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = arrListFilter[position]
        holder.itemView.txtFilter.text = filter.filter_name
        holder.itemView.txtFilter.typeface = Global.fontMedium
        if (position == selectedPosition) {
            holder.itemView.clMain.setBackgroundColor(
                ContextCompat.getColor(
                    context as Context,
                    R.color.edt_txt_border_color
                )
            )
        } else {
            holder.itemView.clMain.background = null
        }

        holder.itemView.setOnClickListener {
            selectedPosition = position
            onFilterClicked.onFilterClicked(position)
        }

    }

    override fun getItemCount(): Int {
        return arrListFilter.size
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}