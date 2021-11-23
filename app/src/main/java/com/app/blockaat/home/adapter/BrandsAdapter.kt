package com.app.blockaat.home.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.blockaat.R
import com.app.blockaat.helper.CustomEvents
import com.app.blockaat.helper.Global
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.home.model.Brands
import kotlinx.android.synthetic.main.item_home_brands.view.*
import java.util.ArrayList

class BrandsAdapter(
    private val dataList: ArrayList<Brands?>,
    private val context: Context?,
    private val homeItemClickInterface: HomeItemClickInterface
) : RecyclerView.Adapter<BrandsAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0
    private var screenWidth: Int = 0

    init {
        /*width = Global.getDimenVallue(context!!, 98.0).toInt()
        height = Global.getDimenVallue(context, 110.0).toInt()*/
        val metrics = context?.resources?.displayMetrics
        screenWidth = metrics!!.widthPixels
        width = (((screenWidth - context?.resources?.getDimension(R.dimen.forty_dp)!!) / 3).toInt())
        height = (width * 60) / 111
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.lnrMain.layoutParams.width = width
        holder.itemView.lnrMain.layoutParams.height = height

        (holder.itemView.lnrMain.layoutParams as GridLayoutManager.LayoutParams).marginEnd =
            context?.resources?.getDimension(R.dimen.ten_dp)?.toInt() as Int

        if (position == 0 || position == 1) {
            (holder.itemView.lnrMain.layoutParams as GridLayoutManager.LayoutParams).marginStart =
                context.resources?.getDimension(R.dimen.ten_dp)?.toInt() as Int
        }
        if (position + 1 % 2 == 0) {
            (holder.itemView.lnrMain.layoutParams as GridLayoutManager.LayoutParams).topMargin =
                context.resources?.getDimension(R.dimen.ten_dp)?.toInt() as Int

        }

        if (context != null) {

            Glide.with(context)
                .load(dataList[position]?.image_name)
                .override(width, height)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade(800))
                .into(holder.itemView.imgBrand)
        }
        holder.itemView.lnrMain.setOnClickListener {
            homeItemClickInterface.onClickItem(
                position,
                "openItem",
                "br",
                dataList[position]?.id.toString(),
                dataList[position]?.name ?: ""
            )
//            val intent = Intent(context, ProductListActivity::class.java)
//            intent.putExtra("brandID", dataList[position]?.id.toString())
//            intent.putExtra("header_text", dataList[position]?.name)
            /*intent.putExtra("banner",dataList[position]?.banner)*/
//            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_brands, parent, false)

        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}