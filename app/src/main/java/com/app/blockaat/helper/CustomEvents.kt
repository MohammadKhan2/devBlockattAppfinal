package com.app.blockaat.helper

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.pushwoosh.inapp.PushwooshInApp
import com.pushwoosh.tags.TagsBundle


object CustomEvents {
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun setFirebaseAnalytics(context: Context) {
        firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    }

    private fun getFirebaseAnalyticsObject(): FirebaseAnalytics? {
        return firebaseAnalytics
    }

    fun eventAddToCart(
        activity: Activity,
        entityID: String?,
        productName: String?,
        brandName: String?,
        finalPrice: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
//        parameters.putString("currency", Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, entityID)
        parameters.putString("product_name", productName)
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, brandName)
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
            finalPrice?.toDouble() ?: 0.0,
            parameters
        )

        //firebase
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, entityID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, brandName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        getFirebaseAnalyticsObject()?.logEvent(FirebaseAnalytics.Event.ADD_TO_CART, bundle)
        getFirebaseAnalyticsObject()?.setUserProperty("user_id", Global.getUserID(activity))


        //pushwoosh event
        val attributes = TagsBundle.Builder().putString("product_name", productName)
            .putInt("product_id", entityID.toString().toInt()).build()
        PushwooshInApp.getInstance().postEvent("ProductAdd", attributes)

    }

    fun eventAddToWishlist(
        activity: Activity,
        entityID: String?,
        productName: String?,
        brandName: String?,
        finalPrice: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
//        parameters.putString("currency", Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, entityID)
        parameters.putString("product_name", productName)
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, brandName)
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_ADDED_TO_WISHLIST, finalPrice?.toDouble()
                ?: 0.0, parameters
        )

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, entityID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, brandName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle)

    }

    fun addToWishList(
        activity: Activity,
        entityID: String?,
        productName: String?,
        brandName: String?,
        finalPrice: String?
    ) {
        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, entityID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, brandName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle)

        // Adjust
        val adjustEvent = AdjustEvent("h4rfam")
        Adjust.trackEvent(adjustEvent)

    }

    fun addToWishList(
        mFirebaseAnalytics: FirebaseAnalytics,
        entityID: String?,
        productName: String?,
        brandName: String?,
        finalPrice: String?
    ) {
        //firebase
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, entityID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, brandName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ADD_TO_WISHLIST, bundle)

    }

    fun itemViewed(
        activity: Activity,
        productID: String?,
        productName: String?,
        finalPrice: String?,
        productBrand: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
//        parameters.putString("currency", Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, productBrand)
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productID)
        parameters.putString("product_name", productName)
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, finalPrice?.toDouble()
                ?: 0.0, parameters
        )

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, productBrand)
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun eventCompletedRegistration(
        activity: Activity,
        userID: Int?,
        name: String?,
        email: String?,
        userType: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
        parameters.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, userType)
        parameters.putString("id", userID?.toString())
        parameters.putString("name", name)
        parameters.putString("email", email)
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, parameters)

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, userType)
        bundle.putString("id", userID?.toString())
        bundle.putString("name", name)
        bundle.putString("email", email)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)
    }

    fun registrationComplete(
        activity: Activity,
        userID: Int?,
        name: String?,
        email: String?,
        userType: String?
    ) {

        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SIGN_UP_METHOD, userType)
        bundle.putString("id", userID?.toString())
        bundle.putString("name", name)
        bundle.putString("email", email)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

        // Adjust
        val adjustEvent = AdjustEvent("dqv9zp")
        Adjust.trackEvent(adjustEvent)
    }

    fun userLogin(
        activity: Activity,
        userID: Int?,
        name: String?,
        email: String?,
    ) {

        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("id", userID?.toString())
        bundle.putString("name", name)
        bundle.putString("email", email)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        // Adjust
        val adjustEvent = AdjustEvent("qpnn7n")
        Adjust.trackEvent(adjustEvent)
    }

    fun eventContentViewed(
        activity: Activity,
        productID: String?,
        productName: String?,
        finalPrice: String?,
        productBrand: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
//        parameters.putString("currency", Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, productBrand)
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, productID)
        parameters.putString("product_name", productName)
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, finalPrice?.toDouble()
                ?: 0.0, parameters
        )

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, productBrand)
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, productID)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, productName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, finalPrice)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun contentViewed(
        activity: Activity,
        userId: String?,
        itemId: Int?,
        contentName: String?
    ) {

        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        if (mFirebaseAnalytics!=null){
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
            bundle.putString("user_id", userId)
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, itemId.toString())
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
        }

        // Adjust
        val adjustEvent = AdjustEvent("wjoqmh")
        Adjust.trackEvent(adjustEvent)
    }

    fun contentViewed(
        activity: Activity,
        userId: String?,
        contentName: String?
    ) {

        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, contentName)
        bundle.putString("user_id", userId)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun eventSearch(
        activity: Activity,
        strProductID: String?,
        strProductName: String?,
        strProductTotal: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
        parameters.putString(AppEventsConstants.EVENT_PARAM_SUCCESS, "1")
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, strProductID)
        parameters.putString(AppEventsConstants.EVENT_PARAM_SEARCH_STRING, strProductName)
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_SEARCHED,
            strProductTotal?.toDouble() ?: 0.0,
            parameters
        )

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, strProductID)
        bundle.putString(FirebaseAnalytics.Param.SEARCH_TERM, strProductName)
        bundle.putString(FirebaseAnalytics.Param.PRICE, strProductTotal)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SEARCH, bundle)

    }

    fun eventInitiatedCheckout(
        activity: Activity,
        orderID: Int?,
        qty: String?,
        grandTotal: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, orderID?.toString())
        parameters.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "Checkout")
//        parameters.putString("currency", Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        parameters.putString(AppEventsConstants.EVENT_PARAM_NUM_ITEMS, qty)
        parameters.putString(AppEventsConstants.EVENT_PARAM_PAYMENT_INFO_AVAILABLE, "0")
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, grandTotal?.toDouble()
                ?: 0.0, parameters
        )


        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, orderID?.toString())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Checkout")
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.QUANTITY, qty)
        bundle.putString(FirebaseAnalytics.Param.PRICE, grandTotal)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

    fun initiatedCheckout(
        activity: Activity,
        userId: String?,
        orderID: String?,
        grandTotal: String?
    ) {
        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, orderID?.toString())
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Checkout")
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.PRICE, grandTotal)
        bundle.putString("User_id", userId)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
    }

    fun eventPurchased(
        activity: Activity,
        order_date: String?,
        order_id: String?,
        paymentMethod: String?,
        grandTotal: String?,
        email: String?,
        userId: String?
    ) {
        //fb
        val parameters = Bundle()
        val logger = AppEventsLogger.newLogger(activity)
        parameters.putString("order_date", order_date)
        parameters.putString("order_id", order_id)
        parameters.putString("User ID", userId)
        parameters.putString("total_value", grandTotal)
        parameters.putString("email", email)
        parameters.putString("payment_method", paymentMethod)
        parameters.putString("currency", Global.getIso3(activity, ""))
//        parameters.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, Global.getIso3(activity, ""))
        logger.logEvent(
            AppEventsConstants.EVENT_NAME_PURCHASED, grandTotal?.toDouble()
                ?: 0.0, parameters
        )

        //firebase
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        val bundle = Bundle()
        bundle.putString("order_date", order_date)
        bundle.putString("order_id", order_id)
        bundle.putString("payment_method", paymentMethod)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.PRICE, grandTotal)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)

//        //pushwoosh event
        val attributes = TagsBundle.Builder().build()
        PushwooshInApp.getInstance().postEvent("CheckoutSuccess", attributes)

    }

    fun itemPurchased(
        activity: Activity,
        order_date: String?,
        order_id: String?,
        paymentMethod: String?,
        grandTotal: String?,
        email: String?,
        userId: String?
    ) {
        //firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("order_date", order_date)
        bundle.putString("order_id", order_id)
        bundle.putString("payment_method", paymentMethod)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, Global.getIso3(activity, ""))
        bundle.putString(FirebaseAnalytics.Param.PRICE, grandTotal)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, bundle)

        // Adjust
        val adjustEvent = AdjustEvent("og11ue")
        Adjust.trackEvent(adjustEvent)

    }

    fun eventClearCart(activity: Activity) {
        //pushwoosh event
        val attributes = TagsBundle.Builder().build()
        PushwooshInApp.getInstance().postEvent("CartCleared", attributes)

    }

    fun screenViewed(activity: Activity, userId: String?,activityName: String){
        //Firebase
        var mFirebaseAnalytics = FirebaseAnalytics.getInstance(activity)
        mFirebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putString("screen_name", activityName)
        bundle.putString("user_id",userId)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        // Adjust
        val adjustEvent = AdjustEvent("5oz3t9")
        Adjust.trackEvent(adjustEvent)
    }
}