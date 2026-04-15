package com.first.fintech.data.repository.services

import android.content.Context
import android.util.Log
import com.first.fintech.data.model.services.ServicesResponse
import com.first.fintech.data.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response


class ServiceRepository(private val context: Context) {
    private val api = RetrofitClient.create(context)

    suspend fun getServices(): Response<ServicesResponse> {
        Log.d("ServiceRepository", "FETCHING SERVICES")
        val response = api.getServices()
        Log.d("ServiceRepository", "SERVICES RESPONSE: ${Gson().toJson(response.body())}")
        return response
    }
}