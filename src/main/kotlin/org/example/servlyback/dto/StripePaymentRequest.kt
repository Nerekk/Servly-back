package org.example.servlyback.dto

data class StripePaymentRequest(
    val jobRequestId: Long,
    val amount: Long
)
