package org.example.servlyback.features_provider

import org.example.servlyback.dto.ProviderInfo
import org.example.servlyback.entities.Provider
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.user.UserRepository
import org.example.servlyback.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ProviderService(
    private val providerRepository: ProviderRepository,
    private val userRepository: UserRepository,
    private val userService: UserService
) {
    fun getProviderInfo(): ResponseEntity<ProviderInfo> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val provider = providerRepository.findByUserUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val info = ProviderInfo(
            name = provider.name,
            phoneNumber = provider.phoneNumber,
            city = provider.city,
            rangeInKm = provider.rangeInKm,
            longitude = provider.location?.x,
            latitude = provider.location?.y
        )

        return ResponseEntity(info, HttpStatus.OK)
    }

    fun createProvider(providerInfo: ProviderInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val user = userRepository.findByUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val provider = Provider(
            user = user,
            name = providerInfo.name,
            phoneNumber = providerInfo.phoneNumber,
            city = providerInfo.city,
            rangeInKm = providerInfo.rangeInKm
        )

        providerRepository.save(provider)

        userService.addUserRole(Role.PROVIDER)

        return ResponseEntity(HttpStatus.OK)
    }

    fun updateProvider(providerInfo: ProviderInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val provider = providerRepository.findByUserUid(uid) ?: return createProvider(providerInfo)

        provider.name = providerInfo.name
        provider.phoneNumber = provider.phoneNumber
        provider.city = providerInfo.city
        provider.rangeInKm = providerInfo.rangeInKm

        providerRepository.save(provider)

        return ResponseEntity(HttpStatus.OK)
    }

}