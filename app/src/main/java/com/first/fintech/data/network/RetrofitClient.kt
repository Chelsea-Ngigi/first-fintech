package com.first.fintech.data.network

import android.content.Context
import android.util.Log
import com.first.fintech.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.first.fintech.BuildConfig
import okhttp3.Interceptor


object RetrofitClient {
    private const val BASE_URL = "https://mawingu.cbaloop.com/cba/api/v1/"

    fun create(context: Context): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .removeHeader("Authorization")
                    .build()
                chain.proceed(request)
            }

            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
//        val okHttpClient = OkHttpClient.Builder()
//            .addInterceptor(loggingInterceptor)
//            .addInterceptor { chain ->
//                val token = SessionManager.getToken(context)
//                val request = if (token.isNotEmpty()) {
//                    chain.request().newBuilder()
//                        .addHeader("Authorization", "Bearer $token")
//                        .build()
//                } else {
//                    chain.request()
//                }
//                chain.proceed(request)
//            }
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//    }
//}