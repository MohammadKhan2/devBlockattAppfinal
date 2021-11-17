package com.app.blockaat.Filter.adapter

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.lv_item_filter_brand.view.*

class FilterBrandsAdapter(
    val activity: AppCompatActivity,
    private val brandsPositionInFilterArray: String
) : RecyclerView.Adapter<FilterBrandsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activity, brandsPositionInFilterArray, position)
    }

    override fun getItemCount(): Int =
        Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!!.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_filter_brand, parent, false)
        return ViewHolder(
            view,
            parent.context
        )
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {

        fun bind(activity: AppCompatActivity, brandsPositionInFilterArray: String, position: Int) {
            val v =
                Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![position]
            itemView.txtTitle.text = v.value!!
            itemView.txtDivTitle.text = v.value

            if (v.isSelected) {
                itemView.txtDivTitle.visibility = View.VISIBLE
            } else {
                itemView.txtDivTitle.visibility = View.GONE
            }

            //set fonts
            itemView.txtTitle.typeface = Global.fontRegular
            itemView.txtDivTitle.typeface = Global.fontRegular

            //set click listeners
            itemView.relMainHolder.setOnClickListener {
                if (v.isSelected) {
                    itemView.txtDivTitle.visibility = View.GONE
                    v.isSelected = false

                    for (t in 0 until Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!!.size) {
                        if (Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![t].id == v.id) {
                            Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![t].isSelected =
                                false  //as we are storing filter in global and passing that and changing its value on selection & un selection
                        }
                    }
                } else {
                    itemView.txtDivTitle.visibility = View.VISIBLE
                    v.isSelected = true

                    for (t in 0 until Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!!.size) {
                        if (Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![t].id == v.id) {
                            Global.arrListFilter!![brandsPositionInFilterArray.toInt()].filter_values!![t].isSelected =
                                true //as we are storing filter in global and passing that and changing its value on selection & un selection
                        }
                    }
                }

                // (activity as FilterBrandActivity).setClearAllBtnText()
            }
        }


    }
}