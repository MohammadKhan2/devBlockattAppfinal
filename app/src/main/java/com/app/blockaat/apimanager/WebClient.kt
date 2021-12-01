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

class WebClient {


    companion object {



        fun create(): WebClient {


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

            val   retrofit =
                Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(WebServices.DOMAIN).client(httpClient).build()
            return retrofit.create(WebClient::class.java)
        }

//        fun create1(): RestClient {
//            val okHttpBuilder = OkHttpClient.Builder()
//
//            okHttpBuilder.connectTimeout(4, TimeUnit.MINUTES)
//            okHttpBuilder.readTimeout(4, TimeUnit.MINUTES).build()
//            okHttpBuilder.writeTimeout(4, TimeUnit.MINUTES)
//
//            val logging = HttpLoggingInterceptor() // Live
//            logging.level = HttpLoggingInterceptor.Level.BODY // Live
//            val httpClient =
//                okHttpBuilder //here we can add Interceptor for dynamical adding headers
//                    .addNetworkInterceptor(object : Interceptor {
//                        @Throws(IOException::class)
//                        override fun intercept(chain: Interceptor.Chain): Response {
//                            val request: Request =
//                                chain.request().newBuilder()
//                                    .addHeader("authtoken", Constants.HEADER)
//                                    .build()
//                            return chain.proceed(request)
//                        }
//                    }) //here we adding Interceptor for full level logging
//                    .addNetworkInterceptor(logging)
//                    .build()
//
//
//            val gson = GsonBuilder().setLenient().create()
//
//            val retrofit =
//                Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create(gson))
//                    .baseUrl(WebServices.DOMAIN1).client(httpClient).build()
//            return retrofit.create(RestClient::class.java)
//        }

    }
}