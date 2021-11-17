package com.app.blockaat.productdetails.adapter

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.app.blockaat.R
import com.app.blockaat.productdetails.interfaces.OnProductDetailsBannerClick
import kotlinx.android.synthetic.main.item_product_banner.view.*

class ProductBannerAdapter internal constructor(private var mContext: Context,
                                                private var arrListProductDetailsBanner: ArrayList<String>, private val onBannerClickInterface: OnProductDetailsBannerClick) : PagerAdapter() {

    private val inflater: LayoutInflater = (mContext as AppCompatActivity).layoutInflater

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return arrListProductDetailsBanner.size
    }

    override fun instantiateItem(view: View, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.item_product_banner, null)
        Glide.with(mContext).load(arrListProductDetailsBanner[position]).into(imageLayout.imgBanner)
        (view as ViewPager).addView(imageLayout)
        imageLayout.imgBanner.setOnClickListener {
          onBannerClickInterface.onBannerClicked(position,arrListProductDetailsBanner)
        }
        return imageLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}