package com.first.fintech.data.repository.subscriptions

import android.content.Context
import android.util.Log
import com.first.fintech.data.model.subscriptions.SubscriptionsResponse
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeRequest
import com.first.fintech.data.model.subscriptions.subscribe.SubscribeResponse
import com.first.fintech.data.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response


class SubscriptionRepository(private val context: Context) {
    private val api = RetrofitClient.create(context)

    suspend fun subscribe(request: SubscribeRequest): Response<SubscribeResponse> {
        Log.d("SubscriptionRepository", "SUBSCRIBE REQUEST: ${Gson().toJson(request)}")
        val response = api.subscribe(request)
        Log.d("SubscriptionRepository", "SUBSCRIBE RESPONSE: ${Gson().toJson(response.body())}")
        return response
    }

    suspend fun getSubscriptions(email: String): Response<SubscriptionsResponse> {
        Log.d("SubscriptionRepository", "FETCHING SUBSCRIPTIONS FOR: $email")
        val response = api.getSubscriptions(email)
        Log.d("SubscriptionRepository", "SUBSCRIPTIONS RESPONSE: ${Gson().toJson(response.body())}")
        return response
    }
}