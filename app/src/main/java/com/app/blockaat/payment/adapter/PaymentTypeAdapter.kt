package com.app.blockaat.payment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.model.CheckoutItemPaymentTypeModel
import com.app.blockaat.payment.interfaces.PaymentClickListener
import kotlinx.android.synthetic.main.lv_item_checkout_payment.view.*
import kotlinx.android.synthetic.main.lv_item_sort.view.*

class PaymentTypeAdapter(
    private val context: Context,
    private val arrListPayment: ArrayList<CheckoutItemPaymentTypeModel>,
    private val paymentInterface: PaymentClickListener
) : RecyclerView.Adapter<PaymentTypeAdapter.PaymentHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_checkout_payment, parent, false)
        return PaymentHolder(view)
    }

    override fun getItemCount(): Int {
        return arrListPayment.size
    }

    override fun onBindViewHolder(holder: PaymentHolder, position: Int) {
        val payment = arrListPayment[position]
        holder.itemView.txtPaymentMethod.text = Global.toCamelCase(payment.type?.trim().toString())
        Global.loadImagesUsingGlide(context, payment.icon, holder.itemView.imgLogo)
        holder.itemView.rbPayment.isChecked = payment.isSelected as Boolean



        /*if (position == arrListPayment.size - 1) {
            holder.itemView.lnrDivider.visibility = View.GONE
        } else {
            holder.itemView.lnrDivider.visibility = View.VISIBLE
        }*/


        holder.itemView.setOnClickListener {
            paymentInterface.onPaymentClicked(position)
        }
    }


    class PaymentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}