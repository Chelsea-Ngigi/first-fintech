package com.first.fintech.data.model.subscriptions.subscribe

data class SubscribeRequest(
    val subscriberEmail: String,
    val serviceName: String,
    val amountPaid: Double
)
