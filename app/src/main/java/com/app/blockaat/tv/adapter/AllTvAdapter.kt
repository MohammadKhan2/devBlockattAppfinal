package com.app.blockaat.tv.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productlisting.ProductListActivity
import com.app.blockaat.productlisting.model.CelebrityTvList
import com.app.blockaat.productlisting.model.Tvs
import kotlinx.android.synthetic.main.item_list_video.view.*
import java.util.*

class AllTvAdapter(private val dataList: ArrayList<Tvs>, private val context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0

    private var bannerWidth: Int = 0
    private var bannerHeight: Int = 0
    private var gridCount = 2
    var VIEW_TYPE_ITEM = 2
    var VIEW_TYPE_HEADER = 1

    init {
        val metrics = context?.resources!!.displayMetrics
        bannerWidth = (metrics.widthPixels)
        bannerHeight = Global.getDimenVallue(context, 160.0).toInt()
        width = (metrics.widthPixels) / 2
        height = Global.getDimenVallue(context, 85.0).toInt()
    }

    override fun getItemViewType(position: Int): Int {

        val type: Int

        if (position == 0) type = VIEW_TYPE_HEADER
        else type = VIEW_TYPE_ITEM

        return type

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (getItemViewType(position)) {
            VIEW_TYPE_HEADER -> {

                val headerViewHolder = holder as HeaderViewHolder

                val params = RelativeLayout.LayoutParams(bannerWidth, bannerHeight)
                headerViewHolder.imgHeaderVideo.layoutParams = params
                val paramsMain =
                    RelativeLayout.LayoutParams(bannerWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
                headerViewHolder.lnrHeaderMain.layoutParams = paramsMain

                headerViewHolder.relGradient.layoutParams = params

                headerViewHolder.txtHeaderName.typeface = Global.fontSemiBold
                headerViewHolder.txtWatch.typeface = Global.fontMedium
                headerViewHolder.txtHeaderName.text =
                    Global.toCamelCase(dataList[position].name.toString())

                if (context != null) {

                    Glide.with(context)
                        .load(dataList[position].image_name)
                        .override(bannerWidth, bannerHeight)
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
                        .into(headerViewHolder.imgHeaderVideo)

                }

                headerViewHolder.lnrHeaderMain.setOnClickListener() {

                    val tvModel = CelebrityTvList(
                        dataList[position]
                    )
                    val intent = Intent(context, ProductListActivity::class.java)
                    intent.putExtra(
                        "header_text",
                        tvModel?.tvs?.name
                    )
                    intent.putExtra(
                        "categoryID",
                        Global.getPreferenceCategory(context as Activity)
                    )
                    intent.putExtra("parent_type", "home")
                    intent.putExtra("isFrom", "home")
                    intent.putExtra("has_subcategory", "yes")
                    intent.putExtra("tv_id", tvModel?.tvs?.id.toString())
                    intent.putExtra("tvData", tvModel?.tvs)
                    context?.startActivity(intent)
                }

            }

            VIEW_TYPE_ITEM -> {

                val itemViewHolder = holder as ItemHolder
                val params = RelativeLayout.LayoutParams(width, height)
                // params.setMargins(Global.getDimenVallue(context!!,2.0).toInt(),0,0,0)
                itemViewHolder.imgVideo.layoutParams = params
                val paramsMain =
                    RelativeLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                // paramsMain.setMargins(Global.getDimenVallue(context!!,2.0).toInt(),0,0,0)
                itemViewHolder.lnrMain.layoutParams = paramsMain

                itemViewHolder.txtName.typeface = Global.fontRegular
                itemViewHolder.txtName.text = dataList[position].name

                //itemViewHolder.imgPlay.setColorFilter(context!!.resources.getColor(R.color.golden_color))
                if (gridCount == 3) {
                    itemViewHolder.itemView.linData.visibility = View.GONE
                } else {
                    itemViewHolder.itemView.linData.visibility = View.VISIBLE
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
                        .into(itemViewHolder.imgVideo)

                }


                itemViewHolder.lnrMain.setOnClickListener() {
                    val tvModel = CelebrityTvList(
                        dataList[position]
                    )
                    val intent = Intent(context, ProductListActivity::class.java)
                    intent.putExtra(
                        "header_text",
                        tvModel?.tvs?.name
                    )
                    intent.putExtra(
                        "categoryID",
                        Global.getPreferenceCategory(context as Activity)
                    )
                    intent.putExtra("parent_type", "home")
                    intent.putExtra("isFrom", "home")
                    intent.putExtra("has_subcategory", "yes")
                    intent.putExtra("tv_id", tvModel?.tvs?.id.toString())
                    intent.putExtra("tvData", tvModel?.tvs)
                    context?.startActivity(intent)
                }
            }
        }

    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            VIEW_TYPE_ITEM -> {
                val v1 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_video, parent, false)
                viewHolder = ItemHolder(v1)

            }

            VIEW_TYPE_HEADER -> {
                val v2 = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_list_video, parent, false)
                viewHolder = HeaderViewHolder(v2)
            }
        }

        return viewHolder!!

    }

    fun gridCount(i: Int) {
        gridCount = i
    }


    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView
        val imgVideo: ImageView
        val lnrMain: LinearLayout
        val imgPlay: ImageView

        init {
            txtName = view.findViewById(R.id.txtName) as TextView
            imgVideo = view.findViewById(R.id.imgVideo) as ImageView
            lnrMain = view.findViewById(R.id.lnrMain) as LinearLayout
            imgPlay = view.findViewById(R.id.imgPlay) as ImageView
        }
    }


    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtHeaderName: TextView
        val imgHeaderVideo: ImageView
        val lnrHeaderMain: LinearLayout
        val relGradient: RelativeLayout
        val txtWatch: TextView

        init {
            txtHeaderName = view.findViewById(R.id.txtHeaderName) as TextView
            txtWatch = view.findViewById(R.id.txtWatch) as TextView
            imgHeaderVideo = view.findViewById(R.id.imgHeaderVideo) as ImageView
            lnrHeaderMain = view.findViewById(R.id.lnrHeaderMain) as LinearLayout
            relGradient = view.findViewById(R.id.relGradient) as RelativeLayout

        }
    }

}