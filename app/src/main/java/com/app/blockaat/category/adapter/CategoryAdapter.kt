package com.app.blockaat.category.adapter

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.app.blockaat.R
import com.app.blockaat.category.interfaces.OnCategorySelectListener
import com.app.blockaat.category.model.Subcategory
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.item_category_header.view.*


class CategoryAdapter(
    private val arrListCategory: ArrayList<Subcategory>,
    private val activity: Activity,
    private val onCategorySelectListener: OnCategorySelectListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryHolder>() {

    private var width = 0
    private var height = 0

    init {
        val metrics: DisplayMetrics = activity.resources.displayMetrics
        val screenWidth = metrics.widthPixels
        width = screenWidth
        height = Global.getDimenVallue(activity, 110.0).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_header, parent, false)
        return CategoryHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        val catModel = arrListCategory[position]

        holder.itemView.txtName.text = catModel?.name ?: ""
        Glide.with(activity)
            .load(catModel.image)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progressBar.visibility = View.GONE
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.progressBar.visibility = View.GONE
                    return false
                }
            })

            .into(holder.itemView.imgCategory)


        holder.itemView.setOnClickListener {
            onCategorySelectListener.onCategorySelected(position)
        }

        //set fonts
        holder.itemView.txtName.typeface = Global.fontMedium

    }

    override fun getItemCount(): Int {
        return arrListCategory.size
    }


    class CategoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}
}