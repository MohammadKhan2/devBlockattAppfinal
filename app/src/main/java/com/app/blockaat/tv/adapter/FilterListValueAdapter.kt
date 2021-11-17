package com.app.blockaat.tv.adapter

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.model.ProductListingFilterValueModel
import com.app.blockaat.tv.interfaces.OnFilterValueSelected
import kotlinx.android.synthetic.main.item_filter_value.view.*

class FilterListValueAdapter(
    private val arrListFilter: ArrayList<ProductListingFilterValueModel>,
    private val context: Context?,
    private val onFilterValue: OnFilterValueSelected
) : RecyclerView.Adapter<FilterListValueAdapter.FilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_filter_value, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = arrListFilter[position]
        holder.itemView.txtFilter.text = filter.value
        holder.itemView.txtFilter.typeface = Global.fontMedium
        holder.itemView.rbFilter.isChecked = filter.isSelected
        if (filter.isSelected) {
            val drawable: Drawable? =
                (context as Context)?.resources?.getDrawable(R.drawable.circle_selected)
            drawable?.mutate()
                ?.setColorFilter(
                    context.resources.getColor(R.color.edt_txt_border_color),
                    PorterDuff.Mode.SRC_IN
                )
            holder.itemView.rbFilter.background=drawable
        }
        else{
            holder.itemView.rbFilter.background= (context as Context)?.resources?.getDrawable(R.drawable.unchecked_radiobutton)
        }
        holder.itemView.setOnClickListener {
            onFilterValue.OnFilterValueSelected(filter)
        }


    }

    override fun getItemCount(): Int {
        return arrListFilter.size
    }

    class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}