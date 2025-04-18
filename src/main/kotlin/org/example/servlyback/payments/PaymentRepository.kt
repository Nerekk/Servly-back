package org.example.servlyback.payments

import org.example.servlyback.entities.JobPayment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository: JpaRepository<JobPayment, Long> {
}