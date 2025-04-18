package org.example.servlyback.payments

import org.example.servlyback.dto.JobPaymentInfo
import org.example.servlyback.entities.JobPayment
import org.example.servlyback.features.job_requests.JobRequestRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val jobRequestRepository: JobRequestRepository
) {

    fun getPayment(paymentId: Long): ResponseEntity<JobPaymentInfo> {
        val jobPayment = paymentRepository.findById(paymentId).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity.ok(jobPayment.toDto())
    }

    @Transactional
    fun createPayment(jobPaymentInfo: JobPaymentInfo): ResponseEntity<JobPaymentInfo> {
        println("JOBPAYMENTINFO: $jobPaymentInfo")
        val jobRequest = jobRequestRepository.findById(jobPaymentInfo.jobRequestId).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val jobPayment = JobPayment(
            jobRequest = jobRequest,
            totalAmount = jobPaymentInfo.totalAmount,
            depositAmount = jobPaymentInfo.depositAmount,
            stripePaymentId = jobPaymentInfo.stripePaymentId,
            stripeDepositPaymentId = jobPaymentInfo.stripeDepositPaymentId,
            paymentStatus = jobPaymentInfo.paymentStatus
        )

        return ResponseEntity.ok(paymentRepository.save(jobPayment).toDto())
    }

    @Transactional
    fun updatePayment(jobPaymentInfo: JobPaymentInfo): ResponseEntity<JobPaymentInfo> {
        if (jobPaymentInfo.id == null) return ResponseEntity(HttpStatus.NOT_FOUND)
        val jobPayment = paymentRepository.findById(jobPaymentInfo.id).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        jobPayment.depositAmount = jobPaymentInfo.depositAmount
        jobPayment.stripePaymentId = jobPaymentInfo.stripePaymentId
        jobPayment.stripeDepositPaymentId = jobPaymentInfo.stripeDepositPaymentId
        jobPayment.paymentStatus = jobPaymentInfo.paymentStatus

        return ResponseEntity.ok(paymentRepository.save(jobPayment).toDto())
    }
}