package com.app.blockaat.productlisting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.interfaces.OnTvClickListener
import com.app.blockaat.productlisting.model.Tvs
import kotlinx.android.synthetic.main.item_tv.view.*

class CategoryTvAdapter(
    private val activity: Activity,
    private val arrListTv: ArrayList<Tvs>?,
    private val onTvClickListener: OnTvClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var width = 0.0

    init {
        width = ((Global.getDeviceWidth(activity) - activity.resources.getDimension(R.dimen._30sdp)
            .toDouble()) / 2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view: View? = null
        view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv, parent, false)
        return TvListViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tv = arrListTv?.get(position)
        (holder as TvListViewHolder).itemView.clMainHolder.layoutParams.width =
            width.toInt()
        holder.itemView.txtName.typeface = Global.fontRegular
        (holder.itemView.clMainHolder.layoutParams as RecyclerView.LayoutParams).marginStart =
            activity.resources.getDimension(R.dimen._10sdp).toInt()

        (holder.itemView.clMainHolder.layoutParams as RecyclerView.LayoutParams).topMargin =
            activity.resources.getDimension(R.dimen._10sdp).toInt()


        Global.loadImagesUsingGlide(activity, tv?.image_name, holder.itemView.ivTvImage)
        holder.itemView.txtName.text = tv?.name

        holder.itemView.setOnClickListener {
            onTvClickListener.onTvClickListener("itemClick", 0, position)

        }


    }

    override fun getItemCount(): Int {
        return arrListTv?.size ?: 0
    }


    class TvListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

}