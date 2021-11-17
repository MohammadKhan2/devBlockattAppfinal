package com.app.blockaat.Filter.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.Filter.interfaces.SortFilterInterface
import com.app.blockaat.Filter.model.SortModel
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.lv_item_orders.view.*
import kotlinx.android.synthetic.main.lv_item_sort.view.*

class SortAdapter(
    private val activity: Activity,
    private val arrayList: ArrayList<SortModel>,
    private val sortFilterInterface: SortFilterInterface
) : RecyclerView.Adapter<SortAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_sort, parent, false)
        return ViewHolder(
            view,
            parent.context
        )
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: SortAdapter.ViewHolder, position: Int) {
        holder.bind(activity, arrayList, position, sortFilterInterface)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(
            activity: Activity,
            arrayList: ArrayList<SortModel>,
            position: Int,
            sortFilterInterface: SortFilterInterface
        ) {
            //set data
            val a = arrayList[position]
            itemView.rbSort.isChecked  = a?.isSelected

            itemView.txtTitle.text = a?.title

            if (position == arrayList.size - 1) {
                itemView.view_refine.visibility = View.GONE
            } else {
                itemView.view_refine.visibility = View.VISIBLE
            }
            if (a?.isSelected){
                itemView.txtTitle.typeface = Global.fontBold

                itemView.txtTitle.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.edt_txt_border_color
                    )
                )

            }else{
                itemView.txtTitle.typeface = Global.fontRegular
                itemView.txtTitle.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.black
                    )
                )
            }

           /* itemView.txtTitle.typeface =
                if (a?.isSelected) Global.fontBold else Global.fontRegular*/

            //set fonts
//            itemView.txtTitle.typeface = Global.fontRegular


            //set click listener
            itemView.relSortItem.setOnClickListener {
                println("Sort_Filter" + position)
                sortFilterInterface.onItemSelect("sort", position)

            }


        }
    }
}
