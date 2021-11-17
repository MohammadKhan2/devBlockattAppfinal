package com.app.blockaat.intro.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.home.model.RootCategoriesData
import com.app.blockaat.intro.interfaces.OnRootCategoryClickListener
import kotlinx.android.synthetic.main.root_category_layout.view.*

class RootCategoryAdapter(private val activity: Activity, private val arrListRootCategory: ArrayList<RootCategoriesData?>?, private val onRootCategoryClickListener: OnRootCategoryClickListener): RecyclerView.Adapter<RootCategoryAdapter.RootCategoryHolder>() {

    var width = 0.0

      init {

            width = (Global.getDeviceWidth(activity).toDouble() - (4*activity.resources.getDimension(R.dimen._10sdp)).toDouble()).toDouble()/2

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RootCategoryHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.root_category_layout, parent, false)

        return RootCategoryHolder(view)
    }

    override fun onBindViewHolder(holder: RootCategoryHolder, position: Int) {

        val cat = arrListRootCategory?.get(position)
        holder.itemView.txtMenEn.typeface = Global.fontMedium
        holder.itemView.txtMenAr.typeface = Global.fontArMedium

        holder.itemView.txtMenEn.text = cat?.name
        holder.itemView.linMen.layoutParams.width = width.toInt()
        (holder.itemView.linMen.layoutParams as RecyclerView.LayoutParams).marginStart =
            activity.resources.getDimension(R.dimen._10sdp).toInt()
        (holder.itemView.linMen.layoutParams as RecyclerView.LayoutParams).marginEnd =
            activity.resources.getDimension(R.dimen._10sdp).toInt()
        if(cat?.name.equals("Men"))
        {
            holder.itemView.txtMenAr.text = activity.resources.getString(R.string.men_ar)
        }else
        {
            holder.itemView.txtMenAr.text = activity.resources.getString(R.string.women_ar)

        }
        if(cat?.name.equals("رجالي"))
        {
            holder.itemView.txtMenAr.text = cat?.name
            holder.itemView.txtMenEn.text = "Men"
        }

        if(cat?.name.equals("نسائي"))
        {
            holder.itemView.txtMenAr.text = cat?.name
            holder.itemView.txtMenEn.text = "Women"

        }

        holder.itemView.setOnClickListener {
            onRootCategoryClickListener.onRootCategoryClicked(position)
        }
    }


    override fun getItemCount(): Int {
        return arrListRootCategory?.size ?: 0
    }


    class RootCategoryHolder(itemView: View): RecyclerView.ViewHolder(itemView)

}