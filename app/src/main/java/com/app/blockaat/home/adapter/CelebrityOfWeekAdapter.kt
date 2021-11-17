package com.app.blockaat.home.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.home.model.Influencer
import kotlinx.android.synthetic.main.item_home_influencer.view.*
import org.greenrobot.eventbus.EventBus

class CelebrityOfWeekAdapter(
    private val dataList: ArrayList<Influencer?>,
    private val context: Context?,
    private val categoryId: String
) :
    RecyclerView.Adapter<CelebrityOfWeekAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0
    private var screenWidth: Int = 0

    init {
        val metrics = context?.resources?.displayMetrics
        screenWidth = metrics!!.widthPixels
        width = ((screenWidth - context?.resources?.getDimensionPixelSize(R.dimen.forty_dp)!!) / 3)
        height = width
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val params = LinearLayout.LayoutParams(
            Global.getDimenVallue(context as Activity, 100.0).toInt(),
            Global.getDimenVallue(context, 110.0).toInt()
        )
        holder.itemView.imgInfluence.layoutParams = params

        holder.itemView.txtName.typeface = Global.fontMedium
        holder.itemView.txtName.text = dataList[position]?.title
        if (position == 0) {
            (holder.itemView.lnrMain.layoutParams as RecyclerView.LayoutParams).marginStart =
                context?.resources?.getDimension(R.dimen.five_dp)?.toInt() as Int
            (holder.itemView.lnrMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                context.resources?.getDimension(R.dimen.five_dp)?.toInt() as Int

        } else {
            (holder.itemView.lnrMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
                context?.resources?.getDimension(R.dimen.five_dp)?.toInt() as Int
        }
        if (context != null) {
            Global.loadImagesUsingGlide(
                context,
                dataList[position]?.image,
                holder.itemView.imgInfluence
            )
            /* Glide.with(context)
                 .load(dataList[position]?.image)
                 .override(width, height)
                 .listener(object : RequestListener<Drawable> {
                     override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>?,
                         isFirstResource: Boolean
                     ): Boolean {
                         return false
                     }

                     override fun onResourceReady(
                         resource: Drawable?,
                         model: Any?,
                         target: Target<Drawable>?,
                         dataSource: DataSource?,
                         isFirstResource: Boolean
                     ): Boolean {
                         return false
                     }

                 })
                 .transition(DrawableTransitionOptions.withCrossFade(800))
                 .into(holder.itemView.imgInfluence)*/

        }

        holder.itemView.lnrMain.setOnClickListener {
            /*val intent = Intent(context, ProductListActivity::class.java)
            intent.putExtra("influencerID", dataList[position]?.id.toString())
            intent.putExtra("header_text", dataList[position]?.title)
            context.startActivity(intent)*/
            dataList[position]?.categoryId = categoryId
            EventBus.getDefault().post(dataList[position])
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