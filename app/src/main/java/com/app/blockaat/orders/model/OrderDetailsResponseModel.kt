package com.app.blockaat.orders.model

data class OrderDetailsResponseModel(
    val `data`: OrderDetailsData? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
)

data class OrderDetailsData(
    val baseCurrencyName: String? = null,
    val cod_cost: String? = null,
    val created_date: String? = null,
    val delivery_charges: String? = null,
    val id: Int? = null,
    val is_cancel_active: Int? = null,
    val is_return_active: Int? = null,
    val items: List<CheckoutItemItemModel>? = null,
    val order_number: Int? = null,
    val payment_details: OrderDetailsPaymentDetails? = null,
    val payment_mode: String? = null,
    val recipient_name: String? = null,
    val recipient_phone: String? = null,
    val shipping_address: OrderDetailsShippingAddress? = null,
    val status: String? = null,
    val status_color: String? = null,
    val status_id: Int? = null,
    val sub_total: String? = null,
    val total: String? = null,
    val vat_charges: String? = null,
    val discount_price: String? = null,
    val is_coupon_applied: Int? = 0,
    val vat_pct: String? = null,
    val paymentDetails: OrderDetailsPaymentDetails? = null,
    val coupon: Coupon? = null
)


data class OrderDetailsConfigurableOption(
    val attribute_code: String? = null,
    val attribute_id: String? = null,
    val attributes: List<OrderDetailsAttribute?>? = null,
    val type: String? = null
)

data class OrderDetailsAttribute(
    val color: String? = null,
    val option_id: Int? = null,
    val value: String? = null
)


class OrderDetailsPaymentDetails(
    val amount: Double? = null,
    val auth: String? = null,
    val id: Int? = null,
    val paymentDate: String? = null,
    val paymentID: String? = null,
    val paymode: String? = null,
    val ref: String? = null,
    val result: String? = null,
    val track_id: String? = null,
    val transaction_id: String? = null
)

data class OrderDetailsShippingAddress(
    val addressline_1: String? = null,
    val alt_phone_number: String? = null,
    val area_name: String? = null,
    val block_name: String? = null,
    val building: String? = null,
    val country_name: String? = null,
    val first_name: String? = null,
    val governorate_name: String? = null,
    val last_name: String? = null,
    val location_type: Any? = null,
    val mobile_number: String? = null,
    val notes: String? = null,
    val phonecode: String? = null,
    val street: String? = null,
    val jaddah: String? = null,
    val zone: String? = null,
    val flat_no: String? = null,
    val floor_no: String? = null
)

