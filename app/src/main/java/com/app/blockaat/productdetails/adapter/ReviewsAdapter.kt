package com.app.blockaat.productdetails.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.productdetails.model.ReviewsModel
import kotlinx.android.synthetic.main.item_reviews.view.*

class ReviewsAdapter(
    private val arrListReview: ArrayList<ReviewsModel>,
    private val activity: Context
) : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reviews, parent, false)

        return ViewHolder(view, parent.context)
    }

    override fun getItemCount(): Int {
        return arrListReview.size ?: 0
    }

    override fun onBindViewHolder(holder: ReviewsAdapter.ViewHolder, position: Int) {
        holder.bind(
            arrListReview,
            activity,
            position
        )
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        fun bind(
            arrListReview: ArrayList<ReviewsModel>,
            activity: Context,
            position: Int
        ) {
            //set data
            val a = arrListReview[position]
            itemView.txtComment.text = a.comment ?: ""
            itemView.txtName.text = a.name ?: ""
            itemView.simpleRatingBar.rating = a.rating.toFloat()

            //set fonts
            itemView.txtName.typeface = Global.fontMedium
            itemView.txtComment.typeface = Global.fontRegular
        }
    }
}