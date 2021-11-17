package com.app.blockaat.register.model

data class RegisterResponse(
    val success: Boolean?,
    val status: Int?,
    val message: String?,
    val data: RegisterData?
)

data class RegisterData(
    val id: String?,
    val first_name: String?,
    val last_name: String?,
    val gender: String?,
    val dob: String?,
    val email: String?,
    val image: String?,
    val phone_code: String?,
    val phone: String?,
    val code: String?,
    val is_phone_verified: Int?,
    val is_email_verified: Int?,
    val is_social_register: Int?,
    val social_register_type: String?,
    val device_token: String?,
    val device_type: String?,
    val device_model: String?,
    val app_version: String?,
    val os_version: String?,
    val push_enabled: String?,
    val newsletter_subscribed: Int?,
    val create_date: String?
)