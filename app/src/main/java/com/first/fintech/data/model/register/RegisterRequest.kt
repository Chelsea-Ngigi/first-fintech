package com.first.fintech.data.model.register

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val msisdn: String,
    val credentials: String
)
