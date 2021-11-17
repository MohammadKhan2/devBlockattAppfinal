package com.app.blockaat.orders.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.interfaces.OnOrderClickListener
import com.app.blockaat.orders.model.MyOrderDataModel
import com.app.blockaat.orders.model.MyOrderItemModel
import kotlinx.android.synthetic.main.lv_item_orders.view.*


class OrdersAdapter(
    private val arrListMyOrderList: ArrayList<MyOrderDataModel>,
    private val activity: Activity,
    private val onOrderClick: OnOrderClickListener
) : RecyclerView.Adapter<OrdersAdapter.ViewHolder>() {

    private val PENDING = 1
    private val ACCEPTED = 2
    private val IN_PROGRESS = 3
    private val OUT_FOR_DELIVERY = 4
    private val DELIVERED = 5
    private val CANCELLED = 6
    private val READY_FOR_DELIVERY = 8


    @SuppressLint("StringFormatInvalid")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val u = arrListMyOrderList[position]

        //set data
        holder.itemView.txtOrderDateLabel.text =
            activity.resources.getString(R.string.placed_on) + " " + Global.getFormattedDate(
                "yyyy-MM-dd hh:mm:ss",
                "dd MMM, yyyy", u.created_date.toString()
            )
        holder.itemView.txtOrderNumber.text =
            activity.resources.getString(R.string.order_num_hash, u.order_number.toString())

        holder.itemView.txtStatus.text = Global.toCamelCase(u.status?.trim().toString())
        if (!u.items.isNullOrEmpty()) {
            holder.itemView.rvImages.layoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

            val itemAdapter = ItemImageAdapter(activity, u.items as ArrayList<MyOrderItemModel>)
            holder.itemView.rvImages.adapter = itemAdapter

        }
        holder.itemView.txtPrice.text = Global.setPriceWithCurrency(activity, u.total ?: "")
        when (u.status_id) {
            PENDING -> {
                //circle color
                holder.itemView.relPending.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)

                //text color
                holder.itemView.txtPending.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

                //view color
                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewPending.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewConfirmed1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )


            }
            ACCEPTED -> {
                //circle color
                holder.itemView.relPending.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)

                //text color
                holder.itemView.txtPending.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

                //view color
                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPending.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewConfirmed1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

            }

            IN_PROGRESS -> {
                //circle color
                holder.itemView.relPending.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)

                //text color
                holder.itemView.txtPending.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

                //view color
                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPending.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewConfirmed1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

            }

            OUT_FOR_DELIVERY -> {
                //circle color
                holder.itemView.relPending.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)

                //text color
                holder.itemView.txtPending.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.secondary_text_color
                    )
                )

                //view color
                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPending.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewConfirmed1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )

            }

            DELIVERED -> {
                //circle color
                holder.itemView.relPending.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)

                //text color
                holder.itemView.txtPending.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )

                //view color
                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPending.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewConfirmed1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )

            }

            CANCELLED -> {
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_selected)

                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtDelivered.text = activity.resources.getString(R.string.cancelled)

                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.circular_unslected
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.circular_unslected
                    )
                )

            }

            READY_FOR_DELIVERY -> {
                holder.itemView.relConfirmed.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_unselected)
                holder.itemView.relPreparing.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_selected)
                holder.itemView.relShipped.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)
                holder.itemView.relDelivered.background =
                    ContextCompat.getDrawable(activity, R.drawable.circular_status_neutral)


                holder.itemView.txtConfirmed.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtPreparing.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtShipped.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.txtDelivered.setTextColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )

                holder.itemView.viewConfirmed.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.circular_unslected
                    )
                )
                holder.itemView.viewPreparing1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.circular_unslected
                    )
                )
                holder.itemView.viewPreparing2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewShipped2.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )
                holder.itemView.viewDelivered1.setBackgroundColor(
                    ContextCompat.getColor(
                        activity,
                        R.color.primary_button_color
                    )
                )

            }
        }

        holder.itemView.txtDetails.setOnClickListener {
            onOrderClick.onOrderClicked(position)
        }

        //set fonts
        /*   holder.itemView.txtOrderNumberLabel.typeface = Global.fontRegular*/
        /*  holder.itemView.txtOrderLabel.typeface = Global.fontMedium*/
        holder.itemView.txtOrderNumber.typeface = Global.fontMedium
        holder.itemView.txtOrderDateLabel.typeface = Global.fontRegular
        holder.itemView.txtDetails.typeface = Global.fontNavBar
        holder.itemView.txtPrice.typeface = Global.fontPriceBold
        holder.itemView.txtPending.typeface = if (u.status_id == PENDING) Global.fontMedium else Global.fontRegular
        holder.itemView.txtConfirmed.typeface = if (u.status_id == ACCEPTED) Global.fontMedium else Global.fontRegular
        holder.itemView.txtPreparing.typeface = if (u.status_id == IN_PROGRESS) Global.fontMedium else Global.fontRegular
        holder.itemView.txtShipped.typeface = if (u.status_id == OUT_FOR_DELIVERY) Global.fontMedium else Global.fontRegular
        holder.itemView.txtDelivered.typeface = if (u.status_id == DELIVERED) Global.fontMedium else Global.fontRegular
        holder.itemView.txtStatus.typeface = Global.fontMedium
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int = arrListMyOrderList.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_orders, parent, false)
        return ViewHolder(view, parent.context)
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {

    }

}