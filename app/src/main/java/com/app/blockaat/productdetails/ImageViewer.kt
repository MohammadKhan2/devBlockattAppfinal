package com.app.blockaat.productdetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.app.blockaat.R
import com.app.blockaat.helper.BaseActivity
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.TouchImageView
import kotlinx.android.synthetic.main.activity_image_viewer.*


class ImageViewer : BaseActivity() {

    private var imageViewPagerAdapter: ImageViewPagerAdapter? = null
    private var currentItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        initializeFields()
    }

    private fun initializeFields() {
        val itemImages = intent.getSerializableExtra("arrayOfImages") as ArrayList<String>
        currentItem =  intent.getIntExtra("currentItem",0)
        imageViewPagerAdapter = ImageViewPagerAdapter(this@ImageViewer, itemImages)
        autoPager.adapter = imageViewPagerAdapter
        imgClose.setColorFilter(resources.getColor(R.color.black))

        imgClose.setOnClickListener {
            finish()
        }
        autoPager.currentItem = currentItem
    }

    class ImageViewPagerAdapter(internal var context: Context, private var images: ArrayList<String>) : PagerAdapter() {
        private var layoutInflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun getCount(): Int {
            return images.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as LinearLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val itemView = layoutInflater.inflate(R.layout.item_image_viewer, container, false)
            val imageView = itemView.findViewById(R.id.imgViewerDetail) as TouchImageView
            Global.loadImagesUsingGlide(context, images[position], imageView)
            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as LinearLayout)
        }
    }
}

