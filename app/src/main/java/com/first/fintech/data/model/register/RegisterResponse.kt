package com.first.fintech.data.model.register

import com.first.fintech.data.model.errors.validation.Error

data class RegisterResponse(
    val message: String? = null,
    val errors: List<Error>? = null
)


