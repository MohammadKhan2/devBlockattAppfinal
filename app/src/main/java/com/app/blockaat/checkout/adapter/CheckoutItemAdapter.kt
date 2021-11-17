package com.app.blockaat.checkout.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.model.CheckoutItemItemModel
import kotlinx.android.synthetic.main.item_checkout_product.view.*

class CheckoutItemAdapter(
    private val context: Context,
    private val arrListItems: ArrayList<CheckoutItemItemModel>
) : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_product, parent, false)
        return CheckoutItemHolder(view)
    }

    override fun getItemCount(): Int {
        return arrListItems.size
    }

    override fun onBindViewHolder(holder: CheckoutItemHolder, position: Int) {
        //set data
        holder.bind(arrListItems, position, context)
    }

    class CheckoutItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            arrListItems: java.util.ArrayList<CheckoutItemItemModel>,
            position: Int,
            context: Context
        ) {
            val a = arrListItems[position]
            itemView.txtProductName.text = a.name.toString()
            itemView.txtFinalPrice.text =
                Global.setPriceWithCurrency(context as Activity, a.final_price.toString())
            //itemView.txtRegularPrice.text = Global.setPriceWithCurrency(activity, a.regular_price.toString())
            Global.loadImagesUsingGlide(context, a.image, itemView.ivProduct)
            itemView.txtQuantity.text = "x " + a.quantity.toString()

            if (position == arrListItems.size - 1) {
                itemView.viewDivLine.visibility = GONE
            }
            try {
                if (!a?.configurable_option.isNullOrEmpty()) {
                    itemView.linAttribute.visibility = View.VISIBLE
                    if (a?.configurable_option?.size == 1) {
                        itemView.linAttributeOne.visibility = View.VISIBLE
                        itemView.linAttributeTwo.visibility = View.GONE
                        if (!a?.configurable_option[0]?.type.isNullOrEmpty()) {
                            itemView.txtAttr1.text =
                                a?.configurable_option?.get(0)?.type + ":"
                            itemView.txtAttr1Value.text =
                                a?.configurable_option?.get(0)?.attributes?.get(0)?.value
                        }
                    } else {
                        itemView.linAttributeOne.visibility = View.VISIBLE
                        itemView.linAttributeTwo.visibility = View.VISIBLE
                        itemView.txtAttr1.text =
                            a?.configurable_option?.get(0)?.type + ":"
                        itemView.txtAttr2.text =
                            a?.configurable_option?.get(1)?.type + ":"
                        itemView.txtAttr1Value.text =
                            a?.configurable_option?.get(0)?.attributes?.get(0)?.value
                        itemView.txtAttr2Value.text =
                            a?.configurable_option?.get(1)?.attributes?.get(0)?.value
                    }
                } else {
                    itemView.linAttribute.visibility = View.INVISIBLE
                }
            } catch (e: Exception) {
            }


            //set fonts
            itemView.txtProductName.typeface = Global.fontRegular
            itemView.txtFinalPrice.typeface = Global.fontNavBar
            itemView.txtQuantity.typeface = Global.fontMedium
            itemView.txtAttr1.typeface = Global.fontRegular
            itemView.txtAttr2.typeface = Global.fontRegular
            itemView.txtAttr1Value.typeface = Global.fontMedium
            itemView.txtAttr2Value.typeface = Global.fontMedium
        }

    }
}