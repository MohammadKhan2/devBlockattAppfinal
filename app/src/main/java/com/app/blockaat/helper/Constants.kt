package com.app.blockaat.helper

import android.os.Build

object Constants {
    var PREFS_USER_ID = "id"
    var PREFS_USER_FIRST_NAME = "first_name"
    var PREFS_USER_LAST_NAME = "last_name"
    var PEFS_USER_SURNAME = "last_name"
    var PREFS_USER_GENDER = "M"
    var PREFS_CATEGORY = "pref_category"
    var CATEGORY_LIST = "category_list"
    var ADD_ADDRESS = "add_address"

    var PREFS_USER_IMAGE = "user_image"
    var PREFS_USER_DOB = "1990-04-25"
    var PREFS_USER_EMAIL = "email"
    var PREFS_USER_CIVIL_ID = ""

    var PREFS_STATE_ID = 1
    var PREFS_NOTES = ""
    var PREFS_COUNTRY_ID = "1"
    var PREFS_BLOCK_ID = "1"
    var PREFS_AREA_ID = "1"
    var PREFS_ADDRESS_ID = "1"
    var PREFS_ADDRESS_NAME = ""
    var PREFS_INFLUENCER_ID = ""

    var PREFS_ALT_PHONE_NUMBER = "1"
    var LOCATION_TYPE = "1"

    var PREFS_USER_PHONE_CODE = "phone_code"
    var PREFS_USER_PHONE = ""
    var PREFS_USER_CODE = "code"
    var PREFS_isPHONE_VERIFIED = "is_phone_verified"
    var PREFS_isEMAIL_VERIFIED = "is_email_verified"
    var PREFS_isSOCIAL_REGISTER = "is_social_register"
    var PREFS_SOCIAL_REGISTER_TYPE = "social_register_type"

    @JvmField
    var PREFS_isUSER_LOGGED_IN = "no"
    var DEVICE_TOKEN = ""
    var NEWSLETTER_SUBSCRIBED = "1"
    var PREFS_DEVIDE_MULTIPLIER = "DEVICE_MULTIPLIER"
    var DEVICE_DENSITY = 0.0


    //deeplink
    const val PREFS_DEEPLINK_TARGET = "deeplink_target"
    const val PREFS_DEFERREDPLINK_TARGET = "deferredlink_target"
    const val PREFS_isFROM_DEEPLINK = "is_from_deeplink"
    const val PREFS_isFROM_DEFERRED = "is_from_deferred"
    const val PREFS_DEEPLINK_ID = "deeplink_id"
    const val PREFS_DEFERREDLINK_ID = "deferredlink_id"
    const val PREFS_DEEPLINK_NAME = "deeplink_name"
    const val PREFS_DEFERREDLINK_NAME = "deferredlink_name"
    const val PREFS_DEEPLINK_CREATOR_ID = "deeplink_creator_id"

    const val DEVICE_TYPE = "A"
    val DEVICE_MODEL = Build.MODEL
    const val APP_VERSION = "1.0"
    val OS_VERSION = Build.VERSION.RELEASE
    var PREFS_USER_PUSH_ENABLED = "push_enabled"
    val PREFS_LANGUAGE = "app_language"
    val PREFS_PHONE_NUMBER = "0000000000"
    var PREFS_HIDE_TITLE = "0"
    var PREFS_CURRENCY_EN = "currency_en"
    val PREFS_CURRENCY_AR = "currency_ar"
    val PREFS_COUNTRY_EN = "country_en"
    val PREFS_COUNTRY_FLAG = "country_flag"
    val PREFS_CURRENCY_FLAG = "currency_flag"
    val PREFS_COUNTRY_AR = "country_ar"
    val PREFS_STORE_CODE = "KW"
    val PREFS_INTRO_SHOWN = "yes"
    val PUSH_RECEIVE_EVENTS = "push_receive_events"
    val HEADER = "8f29b11513c639762124cc32051f698a6bd986651a4ac5a0c222a775076c4d14"
}
