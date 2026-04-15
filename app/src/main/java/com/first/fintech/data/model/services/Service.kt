package com.first.fintech.data.model.services

data class Service(
    val id: Int,
    val serviceName: String,
    val pricing: Double,
    val discountPercent: Double
) {
    val isDiscounted: Boolean
        get() = discountPercent > 0

    val discountedPrice: Double?
        get() = if (isDiscounted)
            pricing - (pricing * discountPercent / 100)
        else null
}