package com.app.blockaat.orders.adapter

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.model.CheckoutItemItemModel
import kotlinx.android.synthetic.main.lv_item_ordered.view.*

class OrderedItemsAdapter(private val activity: AppCompatActivity, private val arrListOrderedItems: ArrayList<CheckoutItemItemModel>) : RecyclerView.Adapter<OrderedItemsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(activity, arrListOrderedItems, position)
    }

    override fun getItemCount(): Int {
      //  println("Here i am arraylist count is       " + arrListOrderedItems.count())
        return arrListOrderedItems.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_ordered, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(activity: AppCompatActivity, arrListOrderedItems: ArrayList<CheckoutItemItemModel>, position: Int) {
            //set data
            val d = arrListOrderedItems[position]
          //  println("Here i am arraylisting size in adapter is " + arrListOrderedItems.size)

            //set fonts
            itemView.txtFinalPrice.typeface = Global.fontMedium
            itemView.txtTitle.typeface = Global.fontBold
            itemView.txtColor.typeface = Global.fontRegular
            itemView.txtSize.typeface = Global.fontRegular
            itemView.txtQty.typeface = Global.fontRegular
            itemView.txtLabelColor.typeface = Global.fontRegular
            itemView.txtLabelSize.typeface = Global.fontRegular
            itemView.txtRegularPrice.typeface = Global.fontRegular
            itemView.txtProductDiscount.typeface = Global.fontRegular

            itemView.txtRegularPrice.paintFlags = itemView.txtRegularPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            if (position == arrListOrderedItems.size) {
                itemView.viewItemDivLine.visibility = View.GONE
            } else {
                itemView.viewItemDivLine.visibility = View.VISIBLE
            }


        }
    }

}