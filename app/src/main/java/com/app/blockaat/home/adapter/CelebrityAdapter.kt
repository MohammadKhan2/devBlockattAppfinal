package com.app.blockaat.home.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.app.blockaat.home.model.Influencer
import com.app.blockaat.navigation.NavigationActivity
import kotlinx.android.synthetic.main.item_home_influencer.view.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class CelebrityAdapter(
    private val dataList: ArrayList<Influencer?>,
    private val context: Context?,
    private val categoryId: String
) :
    RecyclerView.Adapter<CelebrityAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0
    private var screenWidth: Int = 0

    init {
      
        val metrics = context?.resources?.displayMetrics
        screenWidth = metrics!!.widthPixels
        width = (((screenWidth - context?.resources?.getDimension(R.dimen.forty_dp)!!) / 2).toInt())
        height = width
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val params = LinearLayout.LayoutParams(
            Global.getDimenVallue(context as Activity, 86.0).toInt(),
            Global.getDimenVallue(context as Activity, 70.0).toInt()
        )
        holder.itemView.imgInfluence.layoutParams = params
        holder.itemView.txtName.typeface = Global.fontRegular
        holder.itemView.txtName.text = dataList[position]?.title
        (holder.itemView.lnrMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
            context?.resources?.getDimension(R.dimen.five_dp)?.toInt() as Int


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
                .into(holder.itemView.imgInfluence)

        }

        holder.itemView.lnrMain.setOnClickListener {
            dataList[position]?.categoryId = categoryId
            EventBus.getDefault().post(dataList[position])
            if (Global.isUserLoggedIn(context)){
                CustomEvents.contentViewed(context as Activity,Global.getUserId(context),dataList[position]?.id,
                    dataList[position]?.title
                )
            }
        }
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_influencer, parent, false)

        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}