package org.example.servlyback.dto

data class CustomerInfo(
    val name: String,

    val phoneNumber: String,

    val city: String,

    val street: String,

    val houseNumber: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null
)
