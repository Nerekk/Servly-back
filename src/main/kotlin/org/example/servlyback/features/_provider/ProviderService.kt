package org.example.servlyback.features._provider

import org.example.servlyback.dto.ProviderInfo
import org.example.servlyback.entities.Provider
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.features.geocoding.GeocodingService
import org.example.servlyback.features.geocoding.GeocodingUtils
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.user.UserRepository
import org.example.servlyback.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ProviderService(
    private val providerRepository: ProviderRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val geocodingService: GeocodingService
) {
    fun getProviderInfo(): ResponseEntity<ProviderInfo> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val provider = providerRepository.findByUserUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val info = provider.toDto()

        return ResponseEntity(info, HttpStatus.OK)
    }

    fun getProviderInfoById(id: Long): ResponseEntity<ProviderInfo> {
        val provider = providerRepository.findById(id).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity(provider.toDto(), HttpStatus.OK)
    }

    fun createProvider(providerInfo: ProviderInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val user = userRepository.findByUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

//        val address = geocodingService.getAddressFromCoordinates(providerInfo.latitude, providerInfo.longitude)
//            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val provider = Provider(
            user = user,
            name = providerInfo.name,
            phoneNumber = providerInfo.phoneNumber,
            address = providerInfo.address,
            location = GeocodingUtils.createPoint(providerInfo.latitude!!, providerInfo.longitude!!),
            rangeInKm = providerInfo.rangeInKm,
            aboutMe = providerInfo.aboutMe
        )

        providerRepository.save(provider)

        userService.addUserRole(Role.PROVIDER)

        return ResponseEntity(HttpStatus.OK)
    }

    fun updateProvider(providerInfo: ProviderInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val provider = providerRepository.findByUserUid(uid) ?: return createProvider(providerInfo)

//        val address = geocodingService.getAddressFromCoordinates(providerInfo.latitude, providerInfo.longitude)
//            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        println(providerInfo)
        provider.name = providerInfo.name
        provider.phoneNumber = providerInfo.phoneNumber
        provider.address = providerInfo.address
        provider.location = GeocodingUtils.createPoint(providerInfo.latitude!!, providerInfo.longitude!!)
        provider.rangeInKm = providerInfo.rangeInKm
        provider.aboutMe = providerInfo.aboutMe

        providerRepository.save(provider)

        return ResponseEntity(HttpStatus.OK)
    }

}