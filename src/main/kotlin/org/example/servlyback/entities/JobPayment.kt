package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.JobPaymentInfo
import org.example.servlyback.entities.custom_fields.PaymentStatus

@Entity
@Table(name = "job_payments")
data class JobPayment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "job_request_id", nullable = false)
    val jobRequest: JobRequest,

    @Column(name = "total_amount", nullable = false)
    val totalAmount: Long,

    @Column(name = "deposit_amount")
    var depositAmount: Long? = null,

    @Column(name = "stripe_payment_id")
    var stripePaymentId: String? = null,

    @Column(name = "stripe_deposit_payment_id")
    var stripeDepositPaymentId: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    var paymentStatus: PaymentStatus = PaymentStatus.PENDING
) {
    fun toDto(): JobPaymentInfo {
        return JobPaymentInfo(
            id,
            jobRequest.id!!,
            totalAmount,
            depositAmount,
            stripePaymentId,
            stripeDepositPaymentId,
            paymentStatus
        )
    }
}
