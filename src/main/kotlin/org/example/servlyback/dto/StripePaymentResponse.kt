package org.example.servlyback.dto

data class StripePaymentResponse(
    val clientSecret: String,
    val paymentIntentId: String
)
