package org.example.servlyback.dto


data class ProviderInfo(
    val providerId: Long? = null,

    val name: String,

    val phoneNumber: String,

    val address: String,

    val rangeInKm: Double,

    val latitude: Double? = null,

    val longitude: Double? = null,

    val rating: Double? = null,

    val aboutMe: String = ""
)