package com.app.blockaat.orders.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.model.MyOrderItemModel
import kotlinx.android.synthetic.main.lv_items_orderlist.view.*

class ItemImageAdapter(
    private val activity: Activity,
    private val arrListItems: ArrayList<MyOrderItemModel>
) : RecyclerView.Adapter<ItemImageAdapter.ImageHolder>() {

    private var screenWidth = 0
    private var width = 0
    private var height = 0

    init {
        screenWidth = Global.getDeviceWidth(activity)
        width =
            (((screenWidth - (5 * activity.resources.getDimension(R.dimen.ten_dp))) / 4).toInt())
        height = width
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val itemView =
            LayoutInflater.from(activity).inflate(R.layout.lv_items_orderlist, parent, false)
        return ImageHolder(itemView)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val i = arrListItems[position]
        holder.itemView.cardMain.layoutParams.width = width
        holder.itemView.cardMain.layoutParams.height = height
        Global.loadImagesUsingGlide(activity, i.image, holder.itemView.imgProducts)
        if (position == 0) {
            (holder.itemView.cardMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                activity.resources.getDimension(R.dimen.ten_dp).toInt()
            (holder.itemView.cardMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                activity.resources.getDimension(R.dimen.ten_dp).toInt()
        } else {
            (holder.itemView.cardMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                activity.resources.getDimension(R.dimen.ten_dp).toInt()
        }
        (holder.itemView.cardMain.layoutParams as RecyclerView.LayoutParams).topMargin =
            activity.resources.getDimension(R.dimen.ten_dp).toInt()
        (holder.itemView.cardMain.layoutParams as RecyclerView.LayoutParams).bottomMargin =
            activity.resources.getDimension(R.dimen.ten_dp).toInt()
        if (position == 3 && arrListItems.size > 4) {
            holder.itemView.relTransparent.visibility = View.VISIBLE
            holder.itemView.txtCount.visibility = View.VISIBLE
            val count = arrListItems.size - 4
            holder.itemView.txtCount.text =
                "$count +\n" + activity.resources.getString(R.string.more)
        } else {
            holder.itemView.relTransparent.visibility = View.GONE
            holder.itemView.txtCount.visibility = View.GONE
        }
        holder.itemView.txtCount.typeface = Global.fontSemiBold
    }

    override fun getItemCount(): Int {
        return if (arrListItems.size > 4) 4 else arrListItems.size
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

}