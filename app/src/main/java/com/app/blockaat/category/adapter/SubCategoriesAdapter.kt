package com.app.blockaat.category.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.interfaces.OnSubcategorySelectListener
import kotlinx.android.synthetic.main.lv_item_sub_categories.view.*
import org.greenrobot.eventbus.EventBus


class SubCategoriesAdapter(
    private val activity: Activity,
    private val arrListSubCategories: java.util.ArrayList<Subcategory>,
    private val onSelectListener: OnSubcategorySelectListener
) : RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(arrListSubCategories, position, onSelectListener)


    override fun getItemCount(): Int = arrListSubCategories.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_sub_categories, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(
            arrListSubCategories: ArrayList<Subcategory>,
            position: Int,
            onSelectListener: OnSubcategorySelectListener
        ) {
            val a = arrListSubCategories[position]
            //set data
            if (!Global.isEnglishLanguage(activity as Activity)) {
                itemView.imgArrow.rotation = 180f
            }
            itemView.txtSubCategoryName.text = a.name.toString()
            itemView.txtSubCategoryName.typeface = Global.fontMedium
            if (position == (arrListSubCategories.size - 1)) {
                itemView.viewDivider.visibility = View.GONE
            } else {
                itemView.viewDivider.visibility = View.VISIBLE
            }

            //set click listeners
            itemView.linMainHolder.setOnClickListener {

                //onSelectListener.onSubcategorySelected(a)
                EventBus.getDefault().post(arrListSubCategories[position])
            }
        }
    }
}
