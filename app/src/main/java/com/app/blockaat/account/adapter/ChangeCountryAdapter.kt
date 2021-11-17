package com.app.blockaat.account.adapter

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.account.interfaces.ChangeCountryInterface
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.lv_item_change_country.view.*
import java.util.*

class ChangeCountryAdapter(
    private val arrListCountry: ArrayList<StoreDataModel?>,
    private val changeCountryInterface: ChangeCountryInterface

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_change_country, parent, false)
        return ItemViewHolder(itemView, parent.context)
    }

    override fun getItemCount(): Int {
        return arrListCountry.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            holder.bind(arrListCountry, position, changeCountryInterface)
        }
    }

    class ItemViewHolder(view: View, private val context: Context) : RecyclerView.ViewHolder(view) {

        fun bind(
            arrListCountry: ArrayList<StoreDataModel?>,
            position: Int,
            changeCountryInterface: ChangeCountryInterface
        ) {
            if ( arrListCountry[position]!!.isSelected==null &&  arrListCountry[0]!!.isSelected == true){
                arrListCountry[0]!!.isSelected = true

            }

            val a = arrListCountry[position]

            if (Global.isEnglishLanguage(context as Activity)) {
                itemView.txtCountry.text = a?.name_en
            } else {
                itemView.txtCountry.text = a?.name_ar
            }

            Global.loadImagesUsingGlide(context, a?.flag, itemView.imgCountry)

            if (position == arrListCountry.size - 1) {
                itemView.viewDivLine.visibility = View.GONE
            } else {
                itemView.viewDivLine.visibility = View.VISIBLE
            }

            itemView.rbCountry.isChecked = a?.isSelected == true
            if (itemView.rbCountry.isChecked) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    itemView.rbCountry.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.black))
                }
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    itemView.rbCountry.buttonTintList = ColorStateList.valueOf(context.resources.getColor(R.color.divider_line_color_dark))
                }
            }

            //set font
            itemView.txtCountry.typeface = Global.fontRegular

            //set click
            itemView.relMainHolder.setOnClickListener {
                changeCountryInterface.onCountryClick("countryClick", position)
            }
        }
    }
}