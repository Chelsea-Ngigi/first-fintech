package com.first.fintech.data.repository.auth

import android.content.Context
import android.util.Log
import com.first.fintech.data.model.login.LoginRequest
import com.first.fintech.data.model.login.LoginResponse
import com.first.fintech.data.model.register.RegisterRequest
import com.first.fintech.data.model.register.RegisterResponse
import com.first.fintech.data.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Response


class AuthRepository(private val context: Context) {
    private val api = RetrofitClient.create(context)

    suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        Log.d("AuthRepository", "REGISTER REQUEST: ${Gson().toJson(request)}")
        val response = api.register(request)
        Log.d("AuthRepository", "REGISTER RESPONSE: ${Gson().toJson(response.body())}")
        return response
    }

    suspend fun login(request: LoginRequest): Response<LoginResponse> {
        Log.d("AuthRepository", "LOGIN REQUEST: ${Gson().toJson(request)}")
        val response = api.login(request)
        Log.d("AuthRepository", "LOGIN RESPONSE: ${Gson().toJson(response.body())}")
        return response
    }
}