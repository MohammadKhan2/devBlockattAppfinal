package com.app.blockaat.addressListingCart.interfaceaddresscart

interface UpdateAddressFromCartItemEvent {
    fun onAddressUpdateClicked(position: Int, type: String)
}