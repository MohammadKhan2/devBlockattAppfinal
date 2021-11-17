package com.app.blockaat.addressListingCart.adapter

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.app.blockaat.R
import com.app.blockaat.address.AddAddressActivity
import com.app.blockaat.address.interfaceaddress.UpdateAddressItemEvent
import com.app.blockaat.address.model.AddressListingDataModel
import com.app.blockaat.addressListingCart.interfaceaddresscart.UpdateAddressFromCartItemEvent
import com.app.blockaat.helper.Constants
import com.app.blockaat.helper.Global
import com.app.blockaat.helper.NetworkUtil
import com.app.blockaat.apimanager.WebServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.lv_item_address_cart.view.*


class AddressFromCartAdapter(
    private val activity: AppCompatActivity,
    private val arrListAddressListData: ArrayList<AddressListingDataModel>,
    private val onAddressUpdateClicked: UpdateAddressFromCartItemEvent,
    private val onAddressDeleteClicked: UpdateAddressItemEvent
) : RecyclerView.Adapter<AddressFromCartAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(
            activity,
            arrListAddressListData,
            position,
            onAddressUpdateClicked,
            onAddressDeleteClicked
        )


    override fun getItemCount(): Int = arrListAddressListData.count()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lv_item_address_cart, parent, false)
        return ViewHolder(
            view,
            parent.context
        )
    }

    class ViewHolder(view: View, val activity: Context) : RecyclerView.ViewHolder(view) {

        fun bind(
            activity: AppCompatActivity,
            arrListAddressListData: ArrayList<AddressListingDataModel>,
            position: Int,
            onAddressUpdateClicked: UpdateAddressFromCartItemEvent,
            onAddressDeleteClicked: UpdateAddressItemEvent
        ) {
            //set data
            val v = arrListAddressListData[position]

            if (!v.mobile_number.isNullOrEmpty()) {
                itemView.txtPhoneNumber!!.text = v.mobile_number
                itemView.linPhoneNumber.visibility = View.VISIBLE
            } else {
                itemView.linPhoneNumber.visibility = View.GONE
            }


            if (!v.area_name.isNullOrEmpty()) {
                itemView.txtArea!!.text = v.area_name
                itemView.linArea.visibility = View.VISIBLE
            } else {
                itemView.linArea.visibility = View.GONE
            }

            if (!v.block_name.isNullOrEmpty()) {
                itemView.txtBlock!!.text = v.block_name
                itemView.linBlock.visibility = View.VISIBLE
            } else {
                itemView.linBlock.visibility = View.GONE
            }

            if (!v.jaddah.isNullOrEmpty()) {
                itemView.txtJaddah!!.text = v.jaddah
                itemView.linJaddah.visibility = View.VISIBLE
            } else {
                itemView.linJaddah.visibility = View.GONE
            }

            if (!v.street.isNullOrEmpty()) {
                itemView.txtStreet!!.text = v.street
                itemView.linStreet.visibility = View.VISIBLE
            } else {
                itemView.linStreet.visibility = View.GONE
            }

            if (!v.country_name.isNullOrEmpty()) {
                itemView.txtCountry!!.text = v.country_name
                itemView.linCountry.visibility = View.VISIBLE
            } else {
                itemView.linCountry.visibility = View.GONE
            }

            if (v.is_default.toString().toLowerCase() == "yes") {
                itemView.txtRemove.isEnabled = false
                itemView.txtRemove.setTextColor(activity.resources.getColor(R.color.secondary_text_color))
                itemView.ivRemove.setColorFilter(activity.resources.getColor(R.color.secondary_text_color))
            } else {
                itemView.txtRemove.setTextColor(activity.resources.getColor(R.color.primary_text_color))
                itemView.ivRemove.setColorFilter(null)
                itemView.txtRemove.isEnabled = true
            }
            //Handling selected address
            if (Global.strDeliveryAddressId == "") {
                if (v.is_default.toString().toLowerCase() == "yes") {
                    itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_checked)
                } else {
                    itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_unchecked)
                }
            } else {

                if (v.address_id.toString() == Global.strDeliveryAddressId) {
                    itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_checked)
                } else {
                    itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_unchecked)
                }
            }

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
            itemView.txtRemove.typeface = Global.fontRegular
            itemView.txtEdit.typeface = Global.fontRegular

            //Setting click listener
            itemView.relAddress.setOnClickListener {
                Global.strDeliveryAddressId = v.address_id.toString()
                onAddressUpdateClicked.onAddressUpdateClicked(position, "")
            }

            itemView.relRemove.setOnClickListener {
                if (v.is_default.toString().toLowerCase() != "yes") {
                    deleteAddress(v, onAddressDeleteClicked)
                }

            }


            itemView.relEdit.setOnClickListener {
                val i = Intent(activity, AddAddressActivity::class.java)
                i.putExtra("isEditAddress", "yes")
                i.putExtra("addressData", v)
                activity.startActivity(i)
            }

        }

        @SuppressLint("CheckResult")
        private fun deleteAddress(
            v: AddressListingDataModel,
            onAddressUpdateClicked: UpdateAddressItemEvent
        ) {
            if (NetworkUtil.getConnectivityStatus(activity) != 0) {
                Global.apiService.deleteAddress(
                    Global.getStringFromSharedPref(
                        activity,
                        Constants.PREFS_USER_ID
                    ),
                    v.address_id.toString(),
                    WebServices.DeleteAddressWs + Global.getStringFromSharedPref(
                        activity as AppCompatActivity,
                        Constants.PREFS_LANGUAGE
                    )
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { result ->
                            println("RESPONSE - DeleteAddress Ws :   " + Gson().toJson(result))
                            if (result != null) {
                                if (result.status == 200) {
                                    if (result.data != null) {
                                        if (v.is_default.toString().toLowerCase() == "yes") {
                                            itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_checked)
                                        } else {
                                            itemView.ivDeliverToThisAdd.setImageResource(R.drawable.ic_radio_unchecked)
                                        }
                                        onAddressUpdateClicked.onAddressUpdateClicked(
                                            result.data,
                                            "deleteAddress"
                                        )
                                    }
                                } else {
                                    Global.showSnackbar(activity, result.message!!)
                                }
                            } else {
                                //if ws not working
                                Global.showSnackbar(
                                    activity,
                                    activity.resources.getString(R.string.error)
                                )
                            }
                        },
                        { error ->
                            println("ERROR - DeleteAddress Ws :   " + error.localizedMessage)
                            Global.showSnackbar(
                                activity,
                                activity.resources.getString(R.string.error)
                            )
                        }
                    )
            }
        }

    }
}