package com.app.blockaat.celebrities.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.celebrities.interfaces.OnInfluencerClickListener
import com.app.blockaat.celebrities.model.InfluencerList
import com.app.blockaat.helper.Global

import kotlinx.android.synthetic.main.item_list_influencer.view.*
import kotlinx.android.synthetic.main.item_list_influencer.view.imgInfluence
import kotlinx.android.synthetic.main.item_list_influencer.view.txtName
import java.util.*

class InfluencerListAdapter(
    private val dataList: ArrayList<InfluencerList?>,
    private val context: Context?,
    private val onInfluencerClickListener: OnInfluencerClickListener
) : RecyclerView.Adapter<InfluencerListAdapter.ViewHolder>() {

    private var width: Int = 0
    private var height: Int = 0
    private var remainder: Int = 0
    private var screenWidth: Int = 0
    private val mRandom = Random(System.currentTimeMillis())


    init {
       /* val metrics = context?.resources!!.displayMetrics
        val screenWidth =
            (metrics.widthPixels) / 3
        width = screenWidth
        height = screenWidth
        remainder = dataList.size % 3*/

        val metrics = context?.resources?.displayMetrics
        screenWidth = metrics!!.widthPixels
        width = (((screenWidth - context?.resources?.getDimension(R.dimen.forty_dp)!!) / 2).toInt())
        height = width
    }

    override fun getItemViewType(position: Int): Int {

        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

       /* val layoutParams = ConstraintLayout.LayoutParams(width, height)
        holder.itemView.imgInfluence.layoutParams = layoutParams*/
        val params = LinearLayout.LayoutParams(
            Global.getDimenVallue(context as Activity, 140.0).toInt(),
            Global.getDimenVallue(context as Activity, 100.0).toInt()
        )
        params.setMargins(0, 0, 0, Global.getDimension(context as Activity, 5))


        holder.itemView.imgInfluence.layoutParams = params

        holder.itemView.txtName.typeface = Global.fontMedium
        holder.itemView.txtName.text = dataList[position]?.title
      /* (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginEnd =
            context?.resources?.getDimension(R.dimen.ten_dp)?.toInt() as Int
        (holder.itemView.mcMain.layoutParams as RecyclerView.LayoutParams).marginStart =
            context?.resources?.getDimension(R.dimen.ten_dp)?.toInt() as Int
*/
        if (position%2 == 0){
            params.marginStart =  Global.getDimension(context as Activity, 5)
            //layoutParams.marginEnd = Global.getDimension(activity as Activity, 5)
        } else {
            params.marginStart = Global.getDimension(context as Activity, 5)
            params.marginEnd = 0
        }


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

        holder.itemView.mcMain.setOnClickListener {
            onInfluencerClickListener.onInfluencerClicked(position)
        }
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_influencer, parent, false)

        return ViewHolder(view, parent.context)
    }

    private fun generateRandomColor(): Int {
        val baseColor = Color.WHITE

        val baseRed = Color.red(baseColor)
        val baseGreen = Color.green(baseColor)
        val baseBlue = Color.blue(baseColor)

        val red: Int = (baseRed + mRandom.nextInt(256)) / 2
        val green: Int = (baseGreen + mRandom.nextInt(256)) / 2
        val blue: Int = (baseBlue + mRandom.nextInt(256)) / 2

        return Color.argb(256/6, red, green, blue)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view)
}