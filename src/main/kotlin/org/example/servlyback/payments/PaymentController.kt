package org.example.servlyback.payments

import org.example.servlyback.dto.JobPaymentInfo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payments")
class PaymentController(private val paymentService: PaymentService) {

    @GetMapping("/{paymentId}")
    fun getPayment(@PathVariable paymentId: Long): ResponseEntity<JobPaymentInfo> {
        return paymentService.getPayment(paymentId)
    }

    @PostMapping
    fun createPayment(@RequestBody jobPaymentInfo: JobPaymentInfo): ResponseEntity<JobPaymentInfo> {
        return paymentService.createPayment(jobPaymentInfo)
    }

    @PutMapping
    fun updatePayment(@RequestBody jobPaymentInfo: JobPaymentInfo): ResponseEntity<JobPaymentInfo> {
        return paymentService.updatePayment(jobPaymentInfo)
    }
}