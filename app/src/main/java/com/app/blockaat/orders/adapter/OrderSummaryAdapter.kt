package com.app.blockaat.orders.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import com.app.blockaat.orders.OrderedItemsListingActivity
import com.app.blockaat.orders.model.CheckoutDataModel
import kotlinx.android.synthetic.main.lv_item_order_summary.view.*
import kotlinx.android.synthetic.main.lv_item_order_summary_footer.view.*
import kotlinx.android.synthetic.main.lv_item_order_summary_header.view.*
import org.json.JSONObject

class OrderSummaryAdapter(private val myOrderItemDetailData: CheckoutDataModel, private val paymentDataJson: JSONObject?, private val strPaymentType: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_HEADER: Int = 0
    private val TYPE_ITEM: Int = 1
    private val TYPE_FOOTER: Int = 2
    // myOrderDataModel = contains all data
    // myOrderItemDetailData particular item in that position

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderHolderView) {
            holder.bind(myOrderItemDetailData, paymentDataJson, strPaymentType)
        } else if (holder is ItemViewHolder) {
            holder.bind(myOrderItemDetailData, strPaymentType)
        } else if (holder is FooterHolderView)
            holder.bind(myOrderItemDetailData, paymentDataJson, strPaymentType)
    }

    //Have to show 3 items only, i.e. header, item and footer
    override fun getItemCount(): Int = 3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> {
                val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_order_summary, parent, false)
                ItemViewHolder(itemView, parent.context)
            }
            TYPE_FOOTER -> {
                val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_order_summary_footer, parent, false)
                FooterHolderView(itemView, parent.context)
            }
            else -> {
                val headView: View = LayoutInflater.from(parent.context).inflate(R.layout.lv_item_order_summary_header, parent, false)
                HeaderHolderView(headView, parent.context)
            }
        }
    }


    private fun isPositionHeader(position: Int): Boolean {
        return position == TYPE_HEADER
    }

    private fun isPositionFooter(position: Int): Boolean {
        return position == TYPE_FOOTER
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            isPositionHeader(position) -> TYPE_HEADER
            isPositionFooter(position) -> TYPE_FOOTER
            else -> TYPE_ITEM
        }
    }

    class HeaderHolderView(itemView: View?, val activity: Context) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(myOrderItemDetailData: CheckoutDataModel, paymentDataJson: JSONObject?, strPaymentType: String) {
            //set header data

            //Set fonts
            itemView.txtOrderStatus.typeface = Global.fontSemiBold
            itemView.txtOrderNo.typeface = Global.fontRegular
            itemView.txtOrderStatusMsg.typeface = Global.fontRegular

            if (strPaymentType == "C") {
                itemView.txtOrderStatus.visibility = View.VISIBLE
                itemView.txtOrderStatusMsg.visibility = View.VISIBLE
            } else {
                if (paymentDataJson != null && paymentDataJson.toString().isNotEmpty()) {
                    //it means its from k-net
                    if (paymentDataJson.has("Result")) {
                        val a = Global.checkNull(paymentDataJson.getString("Result"))
                        itemView.txtOrderStatus.visibility = View.VISIBLE
                        itemView.txtOrderStatusMsg.visibility = View.VISIBLE
                        if (!a.isNullOrEmpty() && a!!.replace("%20", " ") == "NOT CAPTURED") {
                            itemView.txtOrderStatus.text = activity.resources.getString(R.string.transaction_failed)
                            itemView.txtOrderStatusMsg.text =
                                activity.resources.getString(R.string.transaction_cancelled)
                        } else if (a == "CANCEL") {
                            itemView.txtOrderStatus.text = activity.resources.getString(R.string.transaction_failed)
                            itemView.txtOrderStatusMsg.text =
                                activity.resources.getString(R.string.transaction_cancelled)
                        } else {
                            itemView.txtOrderStatus.text = activity.resources.getString(R.string.transaction_failed)
                            itemView.txtOrderStatusMsg.text =
                                activity.resources.getString(R.string.transaction_cancelled)
                        }
                    } else {
                        itemView.txtOrderStatus.text = activity.resources.getString(R.string.transaction_failed)
                        itemView.txtOrderStatusMsg.text = activity.resources.getString(R.string.transaction_cancelled)
                    }
                } else {
                    itemView.txtOrderStatus.text = activity.resources.getString(R.string.transaction_failed)
                    itemView.txtOrderStatusMsg.text = activity.resources.getString(R.string.transaction_cancelled)
                }
            }
        }

    }

    class FooterHolderView(itemView: View?, val activity: Context) : RecyclerView.ViewHolder(itemView!!) {
        fun bind(myOrderItemDetailData: CheckoutDataModel, paymentDataJson: JSONObject?, strPaymentType: String) {

            val v = myOrderItemDetailData.shipping_address

            //set fonts
            itemView.txtFirstName.typeface = Global.fontRegular
            itemView.txtLabelFirstName.typeface = Global.fontRegular
            itemView.txtLabelLastName.typeface = Global.fontRegular
            itemView.txtLastName.typeface = Global.fontRegular
            itemView.txtLabelBlock.typeface = Global.fontRegular
            itemView.txtBlock.typeface = Global.fontRegular
            itemView.txtLabelStreet.typeface = Global.fontRegular
            itemView.txtStreet.typeface = Global.fontRegular
            itemView.txtLabelNotes.typeface = Global.fontRegular
            itemView.txtNotes.typeface = Global.fontRegular
            itemView.txtCountry.typeface = Global.fontRegular
            itemView.txtLabelCountry.typeface = Global.fontRegular
            itemView.txtLabelArea.typeface = Global.fontRegular
            itemView.txtArea.typeface = Global.fontRegular
            itemView.txtLabelPhoneNumber.typeface = Global.fontRegular
            itemView.txtPhoneNumber.typeface = Global.fontRegular
            itemView.txtShippingAddress.typeface = Global.fontSemiBold
            itemView.txtContactUs.typeface = Global.fontSemiBold

           /* itemView.txtItems.text = activity.resources.getString(R.string.items_ordered, myOrderItemDetailData.cart.items!!.size.toString())
            itemView.txtItems.typeface = Global.fontSemiBold

            //Passing all items ordered to next page to listing



            //delivery charges
            if (myOrderItemDetailData.delivery_charges.isNotEmpty() && myOrderItemDetailData.delivery_charges.toDouble() > 0) {
                itemView.relDeliverCharges.visibility = View.VISIBLE
                itemView.txtDeliveryCharges.text = Global.setPriceWithCurrency(activity as Activity, myOrderItemDetailData.delivery_charges.toString())
            } else {
                itemView.relDeliverCharges.visibility = View.VISIBLE
                itemView.txtDeliveryCharges.text = activity.resources.getString(R.string.free)
            }

            //cod cost
            if (myOrderItemDetailData.cod_cost.isNotEmpty() && myOrderItemDetailData.cod_cost.toDouble() > 0) {
                itemView.relCodCost.visibility = View.VISIBLE
                itemView.txtCodCost.text = Global.setPriceWithCurrency(activity as Activity, myOrderItemDetailData.cod_cost.toString())
            } else {
                itemView.relCodCost.visibility = View.GONE
            }

            //vat charges
            if (myOrderItemDetailData.vat_charges.isNotEmpty() && myOrderItemDetailData.vat_charges.toDouble() > 0) {
                itemView.relVatCharges.visibility = View.VISIBLE
                itemView.txtVatCharges.text = Global.setPriceWithCurrency(activity as Activity, myOrderItemDetailData.vat_charges.toString())
            } else {
                itemView.relVatCharges.visibility = View.GONE
            }

            //total
            itemView.txtTotal.text = Global.setPriceWithCurrency(activity as Activity, myOrderItemDetailData.total.toString())
            //subtotal
            itemView.txtSubTotal.text = Global.setPriceWithCurrency(activity, myOrderItemDetailData.sub_total.toString())

*//*
            itemView.rltMainHolder.setOnClickListener {
                val intent = Intent(activity, OrderedItemsListingActivity::class.java)
                intent.putExtra("orderedItems", myOrderItemDetailData.cart)
                activity.startActivity(intent)
            }
*//*

            itemView.txtCustomerSupport.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("support@project-s.com")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.putExtra(Intent.EXTRA_SUBJECT, "Send us your issue!")
                intent.putExtra(Intent.EXTRA_CC, "support@project-s.com")
                intent.type = "text/html"
                intent.setPackage("com.google.android.gm")
                activity.startActivity(Intent.createChooser(intent, "Send mail"))

            }


            //Setting payment modes based on payment type received from checkout page
            *//* if (strPaymentType == "C") {
                 itemView.txtPaymentType.text = activity.resources.getString(R.string.cash_on_delivery)
             } else if (strPaymentType == "K") {
                 itemView.txtPaymentType.text = activity.resources.getString(R.string.knet)
             } else if (strPaymentType == "V") {
                 itemView.txtPaymentType.text = activity.resources.getString(R.string.visa)
             }*//*





            //set footer font

            itemView.txtLabelSubTotal.typeface = Global.fontMedium
            itemView.txtSubTotal.typeface = Global.fontMedium
            itemView.txtLabelDeliverCharges.typeface = Global.fontMedium
            itemView.txtDeliveryCharges.typeface = Global.fontMedium
            itemView.txtLabelCodCost.typeface = Global.fontMedium
            itemView.txtCodCost.typeface = Global.fontMedium
            itemView.txtLabelVatCharges.typeface = Global.fontMedium
            itemView.txtVatCharges.typeface = Global.fontMedium
            itemView.txtLabelTotal.typeface = Global.fontBold
            itemView.txtTotal.typeface = Global.fontBold
            // itemView.txtPaymentType.typeface = Global.fontRegular
            itemView.txtPaymentSummary.typeface = Global.fontMedium
            //itemView.txtPaymentSummary.typeface = Global.fontSemiBold
            itemView.txtNeedHelp.typeface = Global.fontRegular
            itemView.txtCustomerSupport.typeface = Global.fontRegular



            //   itemView.txtLabelPaymentType.typeface = Global.fontRegular
            // itemView.txtPaymentType.typeface = Global.fontRegular
            itemView.txtLabelPaymentInfo.typeface = Global.fontMedium
            //END - Summary for COD
            val DATE_PATTERN = "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)"


            //START - Summary for VISA/K-NET -  this is extra payment detail on summary page where we will show ref id, payment id etc....
            if (paymentDataJson != null && paymentDataJson.toString().isNotEmpty()) {
                itemView.linPaymentInfo.visibility = View.VISIBLE
                itemView.relPaymentInfo.visibility = View.VISIBLE
                itemView.linDivPayment.visibility = View.GONE

                if (paymentDataJson.has("Result") && paymentDataJson.getString("Result") != null) {
                    itemView.txtResult.text = (Global.checkNull(paymentDataJson.getString("Result")))!!.replace("%20", " ")
                } else {
                    itemView.relResult.visibility = View.VISIBLE
                    itemView.txtResult.text = "NOT CAPTURED"
                }
                if (paymentDataJson.has("PaymentID") && paymentDataJson.getString("PaymentID") != null) {
                    itemView.txtPaymentId.text = Global.checkNull(paymentDataJson.getString("PaymentID"))
                    if (!paymentDataJson.has("Result")) {
                        itemView.viewPaymentId.visibility = View.VISIBLE
                    }
                } else {
                    itemView.viewPaymentId.visibility = View.GONE
                    itemView.relPaymentId.visibility = View.GONE
                }
                if (paymentDataJson.has("TranID") && paymentDataJson.getString("TranID") != null) {
                    itemView.txtTransaction.text = Global.checkNull(paymentDataJson.getString("TranID"))
                } else {
                    itemView.relTransaction.visibility = View.GONE
                }

                if (paymentDataJson.has("PostDate") && paymentDataJson.getString("PostDate") != null && paymentDataJson.getString("PostDate").contains("-")) {
                    itemView.txtPaymentDate.text = (Global.checkNull(Global.getFormattedLocalDateTime("yyyy-MM-dd hh:mm:ss", "dd MMM yyyy hh:mm aa", paymentDataJson.getString("PostDate").replace("%20", " ")))!!)
                } else {
                    itemView.relPaymentDate.visibility = View.GONE
                }

                if (paymentDataJson.has("Auth") && paymentDataJson.getString("Auth") != null) {
                    itemView.txtAuthId.text = Global.checkNull(paymentDataJson.getString("Auth"))
                } else {
                    itemView.relAuthId.visibility = View.GONE
                }
                if (paymentDataJson.has("Ref") && paymentDataJson.getString("Ref") != null) {
                    itemView.txtReferenceId.text = Global.checkNull(paymentDataJson.getString("Ref"))
                } else {
                    itemView.relReferenceID.visibility = View.GONE
                }
                if (paymentDataJson.has("TrackID") && paymentDataJson.getString("TrackID") != null) {
                    itemView.txtTrackID.text = Global.checkNull(paymentDataJson.getString("TrackID"))
                } else {
                    itemView.relTrackID.visibility = View.GONE
                }

                itemView.txtResult.typeface = Global.fontRegular
                itemView.txtPaymentId.typeface = Global.fontRegular
                itemView.txtTransaction.typeface = Global.fontRegular
                itemView.txtAuthId.typeface = Global.fontRegular
                itemView.txtReferenceId.typeface = Global.fontRegular
                itemView.txtTrackID.typeface = Global.fontRegular
                itemView.txtPaymentDate.typeface = Global.fontRegular

                itemView.txtLabelResult.typeface = Global.fontRegular
                itemView.txtLabelPaymentId.typeface = Global.fontRegular
                itemView.txtLabelTransaction.typeface = Global.fontRegular
                itemView.txtLabelAuthId.typeface = Global.fontRegular
                itemView.txtLabelReferenceId.typeface = Global.fontRegular
                itemView.txtLabelTrackID.typeface = Global.fontRegular
                itemView.txtLabelPaymentDate.typeface = Global.fontRegular

            } else {
                itemView.linPaymentInfo.visibility = View.GONE
                itemView.relPaymentInfo.visibility = View.GONE
                itemView.linDivPayment.visibility = View.GONE
            }
            //END - Summary for VISA/K-NET*/

        }
    }

    class ItemViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {
        //Set item data
        fun bind(myOrderItemDetailData: CheckoutDataModel, strPaymentType: String) {
            //set footer data

            itemView.btnViewOrders.setOnClickListener {
                val intent = Intent(activity, OrderedItemsListingActivity::class.java)
                intent.putExtra("orderedItems", myOrderItemDetailData.cart)
                activity.startActivity(intent)
            }

            itemView.txtLabelTip.typeface = Global.fontSemiBold
            itemView.txtTip.typeface = Global.fontRegular
            itemView.btnViewOrders.typeface = Global.fontBtn
        }
    }
}