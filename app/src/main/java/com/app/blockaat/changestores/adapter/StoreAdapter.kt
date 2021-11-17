package com.app.blockaat.changestores.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.changestores.interfaces.UpdateStoreItemEvent
import com.app.blockaat.changestores.model.StoreDataModel
import com.app.blockaat.helper.Constants
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.item_stores.view.*

class StoreAdapter(
    val activity: Activity,
    private val arrListStore: ArrayList<StoreDataModel>?,
    private var onStoreUpdateClicked: UpdateStoreItemEvent
) : RecyclerView.Adapter<StoreAdapter.ViewHolder>() {
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        val a = arrListStore?.get(position)
        Global.loadImagesUsingGlide(
            activity,
            arrListStore?.get(position)?.flag.toString(),
            viewHolder.ivFlag
        )
        viewHolder.itemView.txtTitle.text =
            if (Global.isEnglishLanguage(activity)) arrListStore?.get(position)?.name_en else arrListStore?.get(
                position
            )?.name_ar
        viewHolder.rbStore.isChecked = a?.isSelected == true

        //set fonts
        viewHolder.txtTitle.typeface =
            if (a?.isSelected == true) Global.fontMedium else Global.fontRegular

        //set click listeners
        viewHolder.itemView.relMainHolder.setOnClickListener {
            onStoreUpdateClicked.onStoreUpdateClicked(
                position,
                "",
                arrListStore?.get(position)
            )
        }

    }

    override fun getItemCount(): Int {
        return arrListStore?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stores, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val ivFlag: ImageView = itemView.findViewById(R.id.ivFlag)
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val rbStore: RadioButton = itemView.findViewById(R.id.rbStore)
    }
}
