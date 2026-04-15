package com.first.fintech.data.model.subscriptions

data class SubscriptionsResponse(
    val data: List<Subscription>? = null,
    val message: String? = null
)