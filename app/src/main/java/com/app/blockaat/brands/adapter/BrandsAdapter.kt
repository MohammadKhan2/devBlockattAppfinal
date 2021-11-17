package com.app.blockaat.brands.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.brands.interfaces.OnBrandClickListener
import com.app.blockaat.brands.model.BrandsDataModel
import com.app.blockaat.helper.Global
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.havrylyuk.alphabetrecyclerview.BaseAlphabeticalAdapter

import java.util.*

class BrandsAdapter(
    private var listBrands: ArrayList<BrandsDataModel>,
    val activity: Activity,
    val onBrandClick: OnBrandClickListener
) : BaseAlphabeticalAdapter<BrandsDataModel?>(activity) {
    public override fun initHeadersLetters() {
        val characters = TreeSet<Char>()
        if (null != entityList) {
            for (country in entityList) {
                if (!TextUtils.isEmpty(country?.name)) {
                    country?.name?.get(0)?.let { characters.add(it) }
                }
            }
            headersLetters = characters
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_sticky_children, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        //set data
        val viewHolder = holder as ItemViewHolder
        viewHolder.txtTitle.text = (entityList[position]?.name ?: "")
        Glide.with(activity)
            .load(entityList[position]?.image_name)
            .into(viewHolder.imgBrand)

        if (!Global.isEnglishLanguage(activity)) {
            viewHolder.ivCheck.rotation = 180f
        }
        //set fonts
        viewHolder.txtTitle.typeface = Global.fontMedium

        //set click listeners
        viewHolder.relMainHolder.setOnClickListener {
            onBrandClick.onBrandClick("areaClick", position, entityList[position])
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_sticky_header, parent, false)
        return HeaderViewHolder(itemView)
    }

    override fun onBindHeaderViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val headerViewHolder = viewHolder as HeaderViewHolder
        headerViewHolder.txtListHeader.text = entityList[position]?.name?.get(0).toString()

        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )

        lp.topMargin = if (position == 0) {
            activity.resources.getDimension(R.dimen._20sdp).toInt()
        } else {
           activity.resources.getDimension(R.dimen._25sdp).toInt()
        }

        lp.bottomMargin = activity.resources.getDimension(R.dimen._10sdp).toInt()

        if (Global.isEnglishLanguage(activity)) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_START)
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_END)
        }
        viewHolder.txtListHeader.layoutParams = lp

        //set fonts
        viewHolder.txtListHeader.typeface = Global.fontBold
    }

    override fun getHeaderId(position: Int): Long {
        return entityList[position]?.name?.get(0)?.toLong()!!
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById<View>(R.id.txtTitle) as TextView
        val ivCheck: ImageView = itemView.findViewById<View>(R.id.ivCheck) as ImageView
        val imgBrand: ImageView = itemView.findViewById<View>(R.id.imgBrand) as ImageView

        val relMainHolder: RelativeLayout =
            itemView.findViewById(R.id.relMainHolder) as RelativeLayout
    }

    class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
        val txtListHeader: TextView = headerView.findViewById<View>(R.id.txtListHeader) as TextView
    }
}
