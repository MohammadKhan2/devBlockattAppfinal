package com.app.blockaat.payment.model

data class RedeemCouponRequest (
    val user_id: String? = null,
    val order_id: String? = null,
    val coupon_code: String? = null,
    val shipping_address_id: String? = null
)