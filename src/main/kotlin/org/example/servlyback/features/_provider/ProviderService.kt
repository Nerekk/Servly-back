package org.example.servlyback.features._provider

import org.example.servlyback.dto.ProviderInfo
import org.example.servlyback.entities.Provider
import org.example.servlyback.entities.custom_fields.Role
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
    private val userService: UserService
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
        val provider = Provider(
            user = user,
            name = providerInfo.name,
            phoneNumber = providerInfo.phoneNumber,
            city = providerInfo.city,
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
        println(providerInfo)
        provider.name = providerInfo.name
        provider.phoneNumber = providerInfo.phoneNumber
        provider.city = providerInfo.city
        provider.rangeInKm = providerInfo.rangeInKm
        provider.aboutMe = providerInfo.aboutMe

        providerRepository.save(provider)

        return ResponseEntity(HttpStatus.OK)
    }

}