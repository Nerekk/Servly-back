package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.PaymentStatus

data class JobPaymentInfo(
    val id: Long? = null,
    val jobRequestId: Long,
    val totalAmount: Long,
    val depositAmount: Long? = null,
    val stripePaymentId: String? = null,
    val stripeDepositPaymentId: String? = null,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING
)
