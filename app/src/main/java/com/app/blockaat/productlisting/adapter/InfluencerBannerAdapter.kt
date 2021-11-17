package com.app.blockaat.productlisting.adapter

import android.app.Activity
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.home.interfaces.HomeItemClickInterface
import com.app.blockaat.home.model.Banner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import kotlinx.android.synthetic.main.item_home_banner.view.*

class InfluencerBannerAdapter internal constructor(
    var mContext: Context,
    private val arrListBanner: List<Banner>,
    private val homeItemClickInterface: HomeItemClickInterface
) :
    PagerAdapter() {
    private val inflater: LayoutInflater = (mContext as Activity).layoutInflater
    private var screenWidth: Int = 0
    private var height: Double = 0.0

    init {
        val metrics = mContext.resources.displayMetrics
        screenWidth = (metrics.widthPixels)
        height = Global.getDimenVallue(mContext!!, 135.0)

    }

    override fun destroyItem(container: View, position: Int, `object`: Any) {
        (container as ViewPager).removeView(`object` as View?)
    }

    override fun finishUpdate(container: View) {}

    override fun getCount(): Int {
        return arrListBanner.size
    }


    override fun instantiateItem(view: View, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.item_influencer_banner, null)

        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height.toInt())
        imageLayout.imgBanner.layoutParams = params
        val a = arrListBanner[position]

        Glide.with(mContext)
            .load(arrListBanner[position].image)
            .override(screenWidth, height.toInt())
            .transition(DrawableTransitionOptions.withCrossFade(800))
            .into(imageLayout.imgBanner)

        (view as ViewPager).addView(imageLayout)

        imageLayout.relImage.setOnClickListener {
            if (!a?.link_type.isNullOrEmpty() && !a?.link_id.isNullOrEmpty()) {
                homeItemClickInterface.onClickItem(position,"openItem",a?.link_type?:"",a?.link_id?:"",a?.name?:"")
            }
        }


        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


    override fun startUpdate(container: View) {}

}