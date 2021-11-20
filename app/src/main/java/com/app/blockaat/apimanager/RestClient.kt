import com.app.blockaat.apimanager.WebServices
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.app.blockaat.brands.model.BrandsResponseModel
import com.app.blockaat.changestores.model.StoreResponseModel
import com.app.blockaat.checkout.model.CancelCheckoutResponseModel
import com.app.blockaat.cms.model.CmsResponseModel
import com.app.blockaat.contactus.model.ContactUsResponseModel
import com.app.blockaat.faq.model.FaqResponseModel
import com.app.blockaat.forgotpassword.model.ForgotPasswordResponse
import com.app.blockaat.cart.model.*
import com.app.blockaat.category.model.CategoryResponseDataModel
import com.app.blockaat.category.model.SubCategoryResponseModel
import com.app.blockaat.home.model.HomeResponseModel
import com.app.blockaat.home.model.RootCategoriesModel
import com.app.blockaat.intro.model.IntroResponseModel
import com.app.blockaat.login.model.LoginResponseModel
import com.app.blockaat.celebrities.model.InfluenceResponseModel
import com.app.blockaat.helper.Constants
import com.app.blockaat.orders.model.CheckoutItemResponseModel
import com.app.blockaat.orders.model.CheckoutResponseModel
import com.app.blockaat.orders.model.OrderDetailsResponseModel
import com.app.blockaat.orders.model.OrderResponseModel
import com.app.blockaat.payment.model.RedeemCouponRequest
import com.app.blockaat.productdetails.model.*
import com.app.blockaat.productlisting.model.ProductListingResponseModel
import com.app.blockaat.register.model.RegisterResponse
import com.app.blockaat.search.model.SearchResponseModel
import com.app.blockaat.tv.model.TvProductResponseModel
import com.app.blockaat.tv.model.TvRequestModel
import com.app.blockaat.tv.model.TvResponseModel
import com.app.blockaat.wishlist.modelclass.WishListResponseModel

import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

interface RestClient {

    @POST
    fun addToCart(
        @Url url: String,
        @Body body: AddCartRequestApi
    ): Observable<AddToCartResponseModel>

    @POST
    @FormUrlEncoded
    fun loginUser(
        @Field("email") email: String,
        @Field("password") user_password: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("device_model") device_model: String,
        @Field("app_version") app_version: String,
        @Field("os_version") os_version: String,
        @Url url: String
    ): Observable<LoginResponseModel>

    @POST
    @FormUrlEncoded
    fun guestLoginUser(
        @Field("email") email: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("device_model") device_model: String,
        @Field("app_version") app_version: String,
        @Field("os_version") os_version: String,
        @Url url: String
    ): Observable<LoginResponseModel>

    @POST
    @FormUrlEncoded
    fun Socialregisteration(
        @Field("email") email: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("phone") phone: String,
        @Field("social_register_type") social_register_type: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("device_model") device_model: String,
        @Field("app_version") app_version: String,
        @Field("os_version") os_version: String,
        @Field("facebook_id") facebook_id: String,
        @Field("is_social_register") is_social_register: String,
        @Url url: String
    ): Observable<LoginResponseModel>

    @POST
    @FormUrlEncoded
    fun registerUser(
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("phone") phone: String,
        @Field("gender") gender: String,
        @Field("device_token") device_token: String,
        @Field("device_type") device_type: String,
        @Field("app_version") app_version: String,
        @Field("os_version") os_version: String,
        @Field("newsletter_subscribed") newsletter_subscribed: String,
        @Field("device_model") device_model: String,
        @Url url: String
    ): Observable<RegisterResponse>


    @POST
    @FormUrlEncoded
    fun forgotpasswordUser(
        @Field("email") email: String,
        @Url url: String
    ): Observable<ForgotPasswordResponse>

    @POST
    @FormUrlEncoded
    fun accDetailsUser(
        @Field("user_id") user_id: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("old_password") old_password: String,
        @Field("new_password") password: String,
        @Field("phone_code") phone_code: String,
        @Field("phone") phone: String,
        @Field("image") image: String,
        @Field("newsletter_subscribed") newsletter_subscribed: String,
        @Url url: String
    ): Observable<com.app.blockaat.account.model.EditProfileRespModel>

    @POST
    @FormUrlEncoded
    fun changePassowrd(
        @Field("user_id") user_id: String,
        @Field("first_name") first_name: String,
        @Field("last_name") last_name: String,
        @Field("gender") gender: String,
        @Field("dob") dob: String,
        @Field("old_password") old_password: String,
        @Field("new_password") password: String,
        @Field("phone_code") phone_code: String,
        @Field("phone") phone: String,
        @Field("image") image: String,
        @Field("newsletter_subscribed") newsletter_subscribed: String,
        @Url url: String
    ): Observable<com.app.blockaat.account.model.ChangePasswordResponseModel>

    @POST
    @FormUrlEncoded
    fun checkConfigOptions(
        @Field("product_id") product_id: String,
        @Field("attribute_id") attribute_id: String,
        @Field("option_id") option_id: String,
        @Url url: String
    ): Observable<ConfigurationOptionResponseModel>

    @POST
    @FormUrlEncoded
    fun deleteWishlist(
        @Field("user_id") user_id: String,
        @Field("product_id") id: String,
        @Url url: String
    ): Observable<WishListResponseModel>

    @POST
    @FormUrlEncoded
    fun addToWishlist(
        @Field("user_id") user_id: String,
        @Field("product_id") product_id: String,
        @Url url: String
    ): Observable<WishListResponseModel>

    @POST
    @FormUrlEncoded
    fun setAsDefaultAddress(
        @Field("user_id") user_id: String,
        @Field("id") id: String,
        @Url url: String
    ): Observable<com.app.blockaat.address.model.AddressListingModel>

    @POST
    fun addAddressUser(
        @Body body: com.app.blockaat.address.model.AddAddressRequest,
        @Url url: String
    ): Observable<com.app.blockaat.address.model.AddAddressModel>

    @POST
    @FormUrlEncoded
    fun editAddress(
        @Field("shipping_address_id") shipping_address_id: String,
        @Field("user_id") user_id: String,
        @Field("address_name") addressName: String,
        @Field("country_id") country_id: Int,
        @Field("state_id") state_id: String,
        @Field("area_id") area_id: String,
        @Field("area_name") area_name: String,
        @Field("block_id") block_id: String,
        @Field("block_name") block_name: String,
        @Field("mobile_number") mobile_number: String,
        @Field("street") street: String,
        @Field("jaddah") jaddah: String,
        /* @Field("zone") zone: String,*/
        @Field("building") building: String,
        @Field("floor_no") floor_no: String,
        @Field("flat_no") flat_no: String,
        @Field("notes") notes: String,
        @Field("address_type") addressType: String,
        @Field("is_default") is_default: String,
        @Url url: String
    ): Observable<com.app.blockaat.address.model.EditAddressResponseModel>


    @POST
    @FormUrlEncoded
    fun checkout(
        @Field("user_id") user_id: String,
        @Field("order_id") order_id: String,
        @Field("pay_mode") pay_mode: String,
        @Field("shipping_address_id") shipping_address_id: String,
        @Field("device_type") device_type: String,
        @Field("device_model") device_model: String,
        @Field("app_version") app_version: String,
        @Field("os_version") os_version: String,
        @Field("device_token") device_token: String,
        @Field("delivery_option") delivery_option: String,
        @Url url: String
    ): Observable<CheckoutResponseModel>

    @POST
    @FormUrlEncoded
    fun contactUs(
        @Field("topic") topic: String,
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("message") message: String,
        @Url url: String
    ): Observable<ContactUsResponseModel>

    @GET
    fun getHomeData(@Url url: String): Observable<HomeResponseModel>

    @GET
    fun getBrandData(@Url url: String): Observable<BrandsResponseModel>

    @POST
    fun getAlltTv(@Body body: TvRequestModel, @Url url: String): Observable<TvResponseModel>

    @GET
    fun getInfluencer(@Url url: String): Observable<InfluenceResponseModel>

    @GET
    fun getCategories(@Url url: String): Observable<CategoryResponseDataModel>

    @POST
    fun getSubCategories(
        @Body jsonObject: JsonObject,
        @Url url: String
    ): Observable<SubCategoryResponseModel>

    @POST
    fun getProducts(
        @Body jsonObject: JsonObject,
        @Url url: String
    ): Observable<ProductListingResponseModel>

    @POST
    fun getTvProducts(
        @Body jsonObject: JsonObject,
        @Url url: String
    ): Observable<TvProductResponseModel>

    @GET
    fun getStores(@Url url: String): Observable<StoreResponseModel>

    @GET
    fun getStoresAsync(@Url url: String): Call<StoreResponseModel>

    @GET
    fun getProductDetail(@Url url: String): Observable<ProductDetailResponseModel>

    @GET
    fun getAddressList(@Url url: String): Observable<com.app.blockaat.address.model.AddressListingModel>

    @GET
    fun getCountryList(@Url url: String): Observable<com.app.blockaat.address.model.CountryListResponseModel>

    @GET
    fun getStateList(@Url url: String): Observable<com.app.blockaat.address.model.StateListResponseModel>

    @GET
    fun getAreaList(@Url url: String): Observable<com.app.blockaat.address.model.AreaListResponseModel>

    @GET
    fun getBlockList(@Url url: String): Observable<com.app.blockaat.address.model.BlockListResponseModel>

    @GET
    fun getAddressData(@Url url: String): Observable<com.app.blockaat.address.model.AddressListingModel>

    @GET
    fun getCartList(@Url url: String): Observable<GetCartListResponseModel>

    @POST
    @FormUrlEncoded
    fun deleteAddress(
        @Field("user_id") user_id: String,
        @Field("id") id: String,
        @Url url: String
    ): Observable<com.app.blockaat.address.model.AddressListingModel>

    @POST
    @FormUrlEncoded
    fun moveToWishList(
        @Field("user_id") user_id: String,
        @Field("order_id") order_id: String,
        @Field("product_id") product_id: String,
        @Url url: String
    ): Observable<GetCartListResponseModel>

    @POST
    fun deleteCartItem(
        @Body body: DeleteCartRequest,
        @Url url: String
    ): Observable<GetCartListResponseModel>

    @POST
    fun updateCartItem(
        @Body body: UpdateCartRequest,
        @Url url: String
    ): Observable<GetCartListResponseModel>

    @POST
    fun checkItemStock(
        @Body body: CheckItemStockRequest,
        @Url url: String
    ): Observable<CheckoutItemResponseModel>

    @GET
    fun getWishList(@Url url: String): Observable<WishListResponseModel>

    @GET
    fun cancelCheckout(@Url url: String): Observable<CancelCheckoutResponseModel>

    @GET
    fun getCmsData(@Url url: String): Observable<CmsResponseModel>

    @GET
    fun getOrdersData(@Url url: String): Observable<OrderResponseModel>

    @GET
    fun getFaqData(@Url url: String): Observable<FaqResponseModel>

    //Search page Api
    @GET
    fun getSearchListData(@Url url: String): Observable<SearchResponseModel>

    @GET
    fun getRootCategories(@Url url: String): Observable<RootCategoriesModel>

    @GET
    fun getMyOrderDetail(@Url url: String): Observable<OrderDetailsResponseModel>

    @GET
    fun getLandingImages(@Url url: String): Observable<IntroResponseModel>

    @POST
    fun notifyMe(
        @Body body: NotifyMeRequestModel,
        @Url url: String
    ): Observable<NotifyMeResponseModel>

    @POST
    fun submitReview(
        @Body body: AddReviewRequestModel,
        @Url url: String
    ): Observable<AddReviewResponseModel>

    @POST
    fun redeemCoupon(
        @Body body: RedeemCouponRequest,
        @Url url: String
    ): Observable<CheckoutItemResponseModel>

    companion object {
        fun create(): RestClient {
            val okHttpBuilder = OkHttpClient.Builder()

            okHttpBuilder.connectTimeout(4, TimeUnit.MINUTES)
            okHttpBuilder.readTimeout(4, TimeUnit.MINUTES).build()
            okHttpBuilder.writeTimeout(4, TimeUnit.MINUTES)

            val logging = HttpLoggingInterceptor() // Live
            logging.level = HttpLoggingInterceptor.Level.BODY // Live
            val httpClient =
                okHttpBuilder //here we can add Interceptor for dynamical adding headers
                    .addNetworkInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response {
                            val request: Request =
                                chain.request().newBuilder()
                                    .addHeader("authtoken", Constants.HEADER)
                                    .build()
                            return chain.proceed(request)
                        }
                    }) //here we adding Interceptor for full level logging
                    .addNetworkInterceptor(logging)
                    .build()


            val gson = GsonBuilder().setLenient().create()

            val retrofit =
                Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(WebServices.DOMAIN).client(httpClient).build()
            return retrofit.create(RestClient::class.java)
        }

    }
}