package com.app.blockaat.faq.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.faq.interfaces.FaqInterface
import com.app.blockaat.faq.model.FaqDataModel
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.lv_item_faq_listing.view.*

class FaqAdapter(
    private val faqDataModel: List<FaqDataModel>,
    private val activity: Context
   /* private val faqInterface: FaqInterface*/
) :
    RecyclerView.Adapter<FaqAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
     /*   holder.bind(faqDataModel, position, faqInterface)*/
      holder.bind(faqDataModel, position)

    override fun getItemCount(): Int = faqDataModel.count()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_faq_listing, parent, false)
     /*   return ViewHolder(view, parent.context, faqInterface)*/
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context/*, faqInterface: FaqInterface*/) :
        RecyclerView.ViewHolder(view) {

        fun bind(faqDataModel: List<FaqDataModel>, position: Int/*, faqInterface: FaqInterface*/) {

            //set data
            itemView.txtQuestion.text = faqDataModel[position].question
            itemView.txtAnswer.text = faqDataModel[position].answer

            //set fontsx
            itemView.txtQuestion.typeface = Global.fontMedium
            itemView.txtAnswer.typeface = Global.fontRegular

            itemView.ivArrow.setColorFilter(activity.resources.getColor(R.color.arrow_tint))
            //set click listeners

            if (!Global.isEnglishLanguage(activity as Activity)) {
                itemView.ivArrow.rotation = 180F
            }

            itemView.relQuestion.setOnClickListener {
                if (itemView.expandableAnswer.isExpanded) {
                    itemView.expandableAnswer.collapse()

                    itemView.ivArrow.rotationX = 0f
                } else {
                    itemView.expandableAnswer.expand()

                    itemView.ivArrow.rotationX = 180f
                }

               /* faqInterface.onItemClick(position, "openFaq")*/

            }

        }
    }
}