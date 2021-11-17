package com.app.blockaat.home.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.home.model.Videos
import com.app.blockaat.productlisting.ProductListActivity
import kotlinx.android.synthetic.main.item_home_video.view.*
import java.util.ArrayList

class VideoAdapter(private val dataList: ArrayList<Videos>, private val context: Context?) :
    RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0

    init {
        val metrics = context?.resources!!.displayMetrics
        val screenWidth =
            (metrics.widthPixels) - 2 * context?.resources.getDimension(R.dimen.ten_dp).toInt()
        width = Global.getDimenVallue(context, 150.0).toInt()
        height = Global.getDimenVallue(context, 85.0).toInt()
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val params = RelativeLayout.LayoutParams(width, height)
        holder.itemView.imgVideo.layoutParams = params


        val paramsMain = RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        holder.itemView.lnrMain.layoutParams = paramsMain

        holder.itemView.txtName.typeface = Global.fontSemiBold
        holder.itemView.txtName.text = dataList[position]!!.name


        if (context != null) {

            Glide.with(context)
                .load(dataList[position]?.image)
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
                .into(holder.itemView.imgVideo)

        }


        holder.itemView.lnrMain.setOnClickListener() {

            val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra("tvID", dataList[position]?.id.toString())
            intent.putExtra("header_text", dataList[position]?.name)
            intent.putExtra("video", dataList[position]?.youtube_video_id)
            intent.putExtra("has_subcategory", "yes")
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_home_video, parent, false)

        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}