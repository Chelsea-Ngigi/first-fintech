package com.first.fintech.data.model.subscriptions

data class Subscription(
    val id: Int,
    val subscriberEmail: String,
    val serviceName: String,
    val amountPaid: Double
)
