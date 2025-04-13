package org.example.servlyback.dto

data class CustomerInfo(
    val customerId: Long? = null,

    val name: String,

    val phoneNumber: String,

    val address: String,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val rating: Double? = null
)
