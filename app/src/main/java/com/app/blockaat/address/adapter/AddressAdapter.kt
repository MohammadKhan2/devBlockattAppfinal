package com.app.blockaat.address.adapter

import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.blockaat.R
import com.app.blockaat.helper.Global
import kotlinx.android.synthetic.main.lv_item_address.view.*

class AddressAdapter(
    private val activity: AppCompatActivity,
    private val arrListAddressListData: ArrayList<com.app.blockaat.address.model.AddressListingDataModel>,
    private val isFromCheckout: Boolean,
    private val selectedAddressId: String,
    private val addressInterface: com.app.blockaat.address.interfaceaddress.AddressInterface
) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) =
        holder.bind(
            activity,
            arrListAddressListData,
            position,
            isFromCheckout,
            selectedAddressId,
            addressInterface
        )

    private var isFromRefresh: Boolean? = false

    override fun getItemCount(): Int = arrListAddressListData.count()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lv_item_address, parent, false)
        return ViewHolder(
            view,
            parent.context,
            addressInterface
        )
    }


    class ViewHolder(
        view: View,
        val activity: Context,
        addressInterface: com.app.blockaat.address.interfaceaddress.AddressInterface
    ) :
        RecyclerView.ViewHolder(view) {

        fun bind(
            activity: AppCompatActivity,
            arrListAddressListData: ArrayList<com.app.blockaat.address.model.AddressListingDataModel>,
            position: Int,
            isFromCheckout: Boolean,
            selectedAddressId: String,
            addressInterface: com.app.blockaat.address.interfaceaddress.AddressInterface
        ) {
            //set data
            val v = arrListAddressListData[position]

            if (!v.mobile_number.isNullOrEmpty()) {
                itemView.txtMobile.text =/* "+" +*/ v.phonecode+" "+v.mobile_number
                itemView.txtMobile.visibility = VISIBLE
                itemView.txtPlus.visibility = VISIBLE
            } else {
                itemView.txtMobile.visibility = GONE
                itemView.txtPlus.visibility = GONE
            }

            //0 if address type is home
            if (v?.address_type == "0") {
                itemView.imgType.setImageResource(R.drawable.ic_home_address)
            } else {
                itemView.imgType.setImageResource(R.drawable.ic_office)
            }

            /* if (selectedAddressId == v.address_id) {
                 itemView.ivCheck.visibility = VISIBLE
             } else {
                 itemView.ivCheck.visibility = GONE
             }*/

            itemView.txtName.text = v.address_name
            itemView.txtAddress.text = Global.getFormattedAddressForListingWithLabel(
                activity = activity,
                notes = v?.notes ?: "",
                flat = v?.flat_no ?: "",
                floor = v.floor_no ?: "",
                building_no = v?.building ?: "",
                street = v?.street ?: "",
                jaddah = v?.jaddah ?: "",
                block = v?.block_name ?: "",
                area = v?.area_name ?: "",
                governorate = v?.governorate_name ?: "",
                country = v?.country_name ?: ""
            )

            if (v.is_default.toString().toLowerCase() == "yes") {
                itemView.txtSetDefault.visibility = GONE
                itemView.txtDefault.visibility = VISIBLE
                itemView.imgDelete.visibility = GONE
                itemView.viewEdit.visibility = GONE
            } else {
                itemView.imgDelete.visibility = VISIBLE
                itemView.viewEdit.visibility = VISIBLE
                //Set as Default text Visible if - (isFromCheckout) VISIBLE else VISIBLE
                itemView.txtSetDefault.visibility = if (isFromCheckout) GONE else VISIBLE
            }

            //set fonts
            itemView.txtName.typeface = Global.fontMedium
            itemView.txtMobile.typeface = Global.fontRegular
            itemView.txtAddress.typeface = Global.fontRegular
            itemView.txtSetDefault.typeface = Global.fontBold
            itemView.txtDefault.typeface = Global.fontRegular

            itemView.setOnClickListener {
                addressInterface.onItemClick(position, "selectAddress")
            }

            itemView.imgDelete.setOnClickListener {
                if (v.is_default.toString().toLowerCase() != "yes") {
                    addressInterface.onItemClick(position, "deleteAddress")
                }
            }

            itemView.txtSetDefault.setOnClickListener {
                if (v.is_default.toString().toLowerCase() != "yes") {
                    addressInterface.onItemClick(position, "defaultAddress")
                }
            }

            itemView.imgEdit.setOnClickListener {
                addressInterface.onItemClick(position, "editAddress")
            }
        }

    }
}
