package com.app.blockaat.helper

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

object NetworkUtil {
    private val TYPE_WIFI = 1
    private val TYPE_MOBILE = 2
    private val TYPE_NOT_CONNECTED = 0

    @SuppressLint("MissingPermission")
    fun getConnectivityStatus(context: Context): Int {
        val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (null != activeNetwork) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI
            if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE
        } else {
            Global.showToastShort(context, "Please check internet connection")
        }
        return TYPE_NOT_CONNECTED
    }

}