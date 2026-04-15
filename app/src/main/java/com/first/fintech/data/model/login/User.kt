package com.first.fintech.data.model.login

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val msisdn: String,
    val token: String
)