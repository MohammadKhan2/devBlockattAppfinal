package com.app.blockaat.address.interfaceaddress

import java.util.ArrayList

interface UpdateAddressItemEvent {
    fun onAddressUpdateClicked(data: ArrayList<com.app.blockaat.address.model.AddressListingDataModel>, type: String)
}
