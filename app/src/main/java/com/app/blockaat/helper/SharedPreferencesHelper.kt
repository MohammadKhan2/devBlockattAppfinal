package com.app.blockaat.helper

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesHelper {

    private const val SHARED_PREFS_NAME = "boutikey"
    private var sharedPreferences: SharedPreferences? = null

    /**
     * Write string to shared preferences.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true if value was written successfully
     */
    fun writeString(context: Context, key: String, value: String): Boolean {
        if (context == null) {
            return false
        }
        getSharedPreferencesEditor(context)!!.putString(key, value).apply()
        return true
    }

    /**
     * Write int to shared preferences.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true if value was written successfully
     */
    fun writeInt(context: Context, key: String, value: Int): Boolean {
        if (context == null) {
            return false
        }
        getSharedPreferencesEditor(context)!!.putInt(key, value).apply()
        return true
    }

    /**
     * Write boolean to shared preferences.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true if value was written successfully
     */
    fun writeBoolean(context: Context, key: String, value: Boolean): Boolean {
        if (context == null) {
            return false
        }
        getSharedPreferencesEditor(context)!!.putBoolean(key, value).apply()
        return true
    }

    /**
     * Write long to shared preferences.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true if value was written successfully
     */
    fun writeLong(context: Context, key: String, value: Long): Boolean {
        if (context == null) {
            return false
        }
        getSharedPreferencesEditor(context)!!.putLong(key, value).apply()
        return true
    }

    /**
     * Write float to shared preferences.
     *
     * @param context the context
     * @param key     the key
     * @param value   the value
     * @return true if value was written successfully
     */
    fun writeFloat(context: Context, key: String, value: Float): Boolean {
        if (context == null) {
            return false
        }
        getSharedPreferencesEditor(context)!!.putFloat(key, value).apply()
        return true
    }

    /**
     * Get string to shared preferences.
     *
     * @param context      the context
     * @param key          the key
     * @param defaultValue the default value
     * @return string for this key, or defaultValue if not found
     */
    fun getString(context: Context, key: String, defaultValue: String): String? {
        return if (context == null) {
            defaultValue
        } else getSharedPreferences(context)!!.getString(key, defaultValue)
    }

    /**
     * Get int to shared preferences.
     *
     * @param context      the context
     * @param key          the key
     * @param defaultValue the default value
     * @return int for this key, or defaultValue if not found
     */
    fun getInt(context: Context, key: String, defaultValue: Int): Int {
        return if (context == null) {
            defaultValue
        } else getSharedPreferences(context)!!.getInt(key, defaultValue)
    }

    /**
     * Get long to shared preferences.
     *
     * @param context      the context
     * @param key          the key
     * @param defaultValue the default value
     * @return long for this key, or defaultValue if not found
     */
    fun getLong(context: Context, key: String, defaultValue: Long): Long {
        return if (context == null) {
            defaultValue
        } else getSharedPreferences(context)!!.getLong(key, defaultValue)
    }

    /**
     * Get float to shared preferences.
     *
     * @param context      the context
     * @param key          the key
     * @param defaultValue the default value
     * @return float for this key, or defaultValue if not found
     */
    fun getFloat(context: Context, key: String, defaultValue: Float): Float {
        return if (context == null) {
            defaultValue
        } else getSharedPreferences(context)!!.getFloat(key, defaultValue)
    }

    /**
     * Get boolean to shared preferences.
     *
     * @param context      the context
     * @param key          the key
     * @param defaultValue the default value
     * @return boolean for this key, or defaultValue if not found
     */
    fun getBoolean(context: Context, key: String, defaultValue: Boolean): Boolean {
        return if (context == null) {
            defaultValue
        } else getSharedPreferences(context)!!.getBoolean(key, defaultValue)
    }

    /**
     * Gets shared preferences for [Constants].SHARED_PREFS_NAME
     * Preferences are encrypted for release builds using
     * https://github.com/scottyab/secure-preferences
     * and plain text for debug builds
     *
     * @param context the context
     * @return either [SharedPreferences] or []
     * based on [Constants].IS_DEBUG
     * for [Constants].SHARED_PREFS_NAME
     */
    private fun getSharedPreferences(context: Context): SharedPreferences? {

        if (context == null) {
            return null
        }

        if (sharedPreferences == null) {

            //            if (Constants.IS_DEBUG)
            run { sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE) }
            //            else {
            //                sharedPreferences = new SecurePreferences(context, BuildConfig.APPLICATION_ID + "!sp0rtk1x#b0mb4y#r0b1n@!", SHARED_PREFS_NAME);
            //            }
        }
        return sharedPreferences
    }

    /**
     * Gets shared preferences editor.
     *
     * @param context the context
     * @return the shared preferences editor
     */
    private fun getSharedPreferencesEditor(context: Context): SharedPreferences.Editor? {

        return if (context == null) {
            null
        } else getSharedPreferences(context)!!.edit()
    }

    fun clearSharedPreferences() {
        sharedPreferences!!.edit().clear().commit()
    }

}
