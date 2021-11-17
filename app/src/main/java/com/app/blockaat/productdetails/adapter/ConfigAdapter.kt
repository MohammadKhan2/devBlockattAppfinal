package com.app.blockaat.productdetails.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productdetails.interfaces.OnConfigOptionSelected
import com.app.blockaat.productdetails.model.ProductDetailAttributeModel
import kotlinx.android.synthetic.main.custom_config.view.*


class ConfigAdapter(
    private val arrListAttribute: ArrayList<ProductDetailAttributeModel>?,
    private val context: Context?,
    private val config1: OnConfigOptionSelected,
    private val type: Int = 2
) : RecyclerView.Adapter<ConfigAdapter.ConfigViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfigViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.custom_config, parent, false)
        return ConfigViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrListAttribute?.size as Int
    }

    override fun onBindViewHolder(holder: ConfigViewHolder, position: Int) {
        val attribute = arrListAttribute?.get(position)

        holder.itemView.txtValue.text = attribute?.value

        if (attribute?.isSelected == true) {
            holder.itemView.clMain.background =
                ContextCompat.getDrawable(
                    context as Context,
                    R.drawable.rounded_selected_attribute
                )
        } else {
            holder.itemView.clMain.background = ContextCompat.getDrawable(
                context as Context,
                R.drawable.rounded_unselected_attribute
            )
        }
        if (!attribute?.color.isNullOrEmpty()) {
            val color: Int = Color.parseColor(attribute?.color)
            val drawable: Drawable? =
                (context as Context)?.resources?.getDrawable(R.drawable.rounded_selected_attribute)
            drawable?.mutate()?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            holder.itemView.txtValue.background = drawable
            holder.itemView.txtValue.text = ""
        }

        (holder.itemView.clMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
            context.resources.getDimension(R.dimen.ten_dp).toInt()
        holder.itemView.txtValue.typeface = Global.fontRegular

        if (type == 1) {
            holder.itemView.txtValue.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.primary_text_color
                )
            )
        } else {
            holder.itemView.txtValue.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.divider_line_color
                )
            )
        }
        if (type == 2) {

            if (arrListAttribute?.get(position)?.isEnabled == true) {
                holder.itemView.txtValue.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.primary_text_color
                    )
                )
//                holder.itemView.txtValue.setTextColor(ContextCompat.getColor(context,R.color.primary_text_color))
            } else {
                holder.itemView.txtValue.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.divider_line_color
                    )
                )
//                holder.itemView.txtValue.background=context.resources.getColor(R.color.)
            }
        }

        holder.itemView.setOnClickListener {
            config1.onConfigOptionChange(
                arrListAttribute?.get(position) as ProductDetailAttributeModel,
                position
            )
        }
    }


    class ConfigViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}