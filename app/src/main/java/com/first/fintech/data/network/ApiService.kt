package com.first.fintech.data.network

import com.first.fintech.data.model.login.LoginRequest
import com.first.fintech.data.model.login.LoginResponse
import com.first.fintech.data.model.register.RegisterRequest
import com.first.fintech.data.model.register.RegisterResponse
import com.first.fintech.data.model.services.ServicesResponse
import com.first.fintech.data.model.subscriptions.SubscriptionsResponse
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeRequest
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response


interface ApiService {

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("access/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("service/services")
    suspend fun getServices(): Response<ServicesResponse>

    @POST("subscription/subscribe")
    suspend fun subscribe(@Body request: SubscribeRequest): Response<SubscribeResponse>

    @GET("subscription/subscriptions/{email}")
    suspend fun getSubscriptions(
        @Path(value = "email", encoded = true) email: String
    ): Response<SubscriptionsResponse>
//    @GET("subscription/subscriptions/{email}")
//    suspend fun getSubscriptions(@Path("email") email: String): Response<SubscriptionsResponse>
}