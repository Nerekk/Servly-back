package org.example.servlyback.features_customer

import org.example.servlyback.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {
    fun findByUserUid(uid: String): Customer?
}