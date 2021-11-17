package com.app.blockaat.productlisting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.Global.getDimenVallue
import com.app.blockaat.helper.Global.loadImagesUsingGlide
import com.app.blockaat.productlisting.interfaces.OnTvClickListener
import com.app.blockaat.productlisting.model.Tvs
import kotlinx.android.synthetic.main.item_tv.view.*
import kotlinx.android.synthetic.main.item_products_banner.view.*

class TvListAdapter(
    private val activity: Activity,
    private val arrListTv: ArrayList<Tvs>?,
    private val onTvClickListener: OnTvClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_BANNER = 0
    private val TYPE_ITEM = 1
    var width = 0.0

    init {
        width = ((Global.getDeviceWidth(activity) - activity.resources.getDimension(R.dimen._30sdp)
            .toDouble()) / 2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        var view: View? = null
        when (viewType) {
            TYPE_ITEM -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_tv, parent, false)
                return TvListViewHolder(view)
            }

            else -> {
                view = LayoutInflater.from(activity)
                    .inflate(R.layout.item_products_banner, parent, false)
                return BannerHolder(view)
            }

        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val tv = arrListTv?.get(position)

        when (getItemViewType(position)) {
            TYPE_ITEM -> {
                (holder as TvListViewHolder).itemView.clMainHolder.layoutParams.width =
                    width.toInt()
                holder.itemView.txtName.typeface = Global.fontRegular
                (holder.itemView.clMainHolder.layoutParams as RecyclerView.LayoutParams).marginStart =
                    activity.resources.getDimension(R.dimen._10sdp).toInt()

                (holder.itemView.clMainHolder.layoutParams as RecyclerView.LayoutParams).topMargin =
                    activity.resources.getDimension(R.dimen._10sdp).toInt()

                holder.itemView.clMainHolder.setBackgroundColor(activity.resources.getColor(R.color.white))
                Global.loadImagesUsingGlide(activity, tv?.image_name, holder.itemView.ivTvImage)
                holder.itemView.txtName.text = tv?.name

                holder.itemView.setOnClickListener {
                    onTvClickListener.onTvClickListener("itemClick", position, position)
                }
            }

            TYPE_BANNER -> {
                if (!tv?.banner.isNullOrEmpty()) {
                    (holder as BannerHolder).itemView.linBannerImage.setVisibility(View.VISIBLE)
                    println("image view" + tv?.banner)
                    (holder as BannerHolder).itemView.ivBanner.setLayoutParams(
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            getDimenVallue(
                                holder.itemView.getContext(),
                                135.0
                            ).toInt()
                        )
                    )

                    loadImagesUsingGlide(
                        (holder as BannerHolder).itemView.context,
                        tv?.banner,
                        (holder as BannerHolder).itemView.ivBanner
                    )
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return arrListTv?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_BANNER
        } else {
            TYPE_ITEM
        }
    }


    class TvListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

    class BannerHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}