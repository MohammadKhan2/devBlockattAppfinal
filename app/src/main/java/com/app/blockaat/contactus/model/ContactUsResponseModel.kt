package com.app.blockaat.contactus.model


data class ContactUsResponseModel(
		val success: Boolean,
		val status: Int,
		val message: String,
		val data: List<Any>
)