package org.example.servlyback.dto

data class ProviderInfo(
    val name: String,

    val phoneNumber: String,

    val city: String,

    val rangeInKm: Double,

    val latitude: Double? = null,

    val longitude: Double? = null
)