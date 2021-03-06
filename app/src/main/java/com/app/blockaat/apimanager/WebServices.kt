package com.app.blockaat.apimanager

import com.app.blockaat.helper.Constants
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object WebServices {

    /*Old Url*/
   // var DOMAIN = "https://dev-cp.blockatapp.com/"
   // var IMAGE_DOMAIN = "https://dev-cp.blockatapp.com/"

    /*Live  */
    var DOMAIN = "https://admin-cp.blockatapp.com/api/ver1bloc/"
    var DOMAIN1 = "http://v2api.blockatapp.com/api/ver1bloc/"
    var IMAGE_DOMAIN = "https://admin-cp.blockatapp.com/"

//    var DOMAIN1 = "http://v2api.blockatapp.com/api/ver1bloc/"
//    var IMAGE_DOMAIN1 = "http://v2api.blockatapp.com/"

    val HomeWs = "home?lang="
    const val CategoryWs = "child-categories?lang="
    const val BrandsWs = "all-brands?lang="
    const val AddAddressWs = "add-address?lang=en"
    const val LoginWs = "login"
    const val GuestLoginWs = "guest-checkout"
    const val RegisterWs = "register"
    const val AccDetailsWs = "edit-profile"
    const val SearchWs = "search?lang="
    const val CmsWs = "cms?page="
    const val ForgotPasswordWs = "forgot-password"
    const val AddressListingWs = "user-address?lang="
    const val OrderListingWs = "user-orders?lang="
    const val MyOrdersDetailWs = "order-details?lang="
    const val ProductDetailWs = "product-details?product_id="
    const val CountryListWs = "country?lang="
    const val StateListWs = "state?lang="
    const val AreaListWs = "area?lang="
    const val BlockListWs = "sector?lang="
    const val ConfigurationOptionWs = "configurable-options?lang="
    const val FaqWs = "faq?"
    const val AddToCartWs = "add-to-cart?lang="
    const val GetCartListWs = "cart-items?lang="
    const val UpdateCartItemWs = "update-cart?lang="
    const val DeleteCartItemWs = "delete-from-cart?lang="
    const val WishListWs = "user-wishlist?lang="
    const val DeleteWishlistItemWs = "remove-wishlist?lang="
    const val AddToWishlistWs = "add-wishlist?lang="
    const val MoveToWishlistWs = "save-for-later?lang="
    const val CheckItemStockWs = "check-item-stock?lang="
    const val CheckoutWs = "checkout?lang="
    const val CancelCheckoutWs = "cancel-checkout?order_id="
    const val EditAddressWs = "update-address?lang="
    const val DeleteAddressWs = "delete-address?lang="
    const val setAsDefaultAddressWs = "make-default-address?lang="
    const val StoreWs = "stores?lang="
    const val PushEnableDisableWs = "toggle-push?user_id="
    const val SuggestionWs = "suggestions?q="
    const val ReorderWs = "reorder?order="
    const val CancelOrder = "cancel-order?id="
    const val SocialLoginWs = "social-register"
    const val CountryWs = "country-details?iso_code="
    const val AllInfluencersWs = "influencers?lang="
    const val AllTV = "tv-video?lang="
    const val ProductsTv = "tv-product?lang="
    const val ContactUsWs = "contact-us"
    const val RootCategoriesWs = "root-categories?lang="
    const val LandingPageWs = "landing-page-ads?lang="
    const val NotifyMe = "notify-user-requests?lang="
    const val SubmitReviewWs = "add-review?store="
    const val RedeemCoupon = "redeem-coupon?store="


}