package org.example.servlyback.features._customer

import org.example.servlyback.dto.CustomerInfo
import org.example.servlyback.entities.Customer
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
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val geocodingService: GeocodingService
) {

    fun getCustomerInfo(): ResponseEntity<CustomerInfo> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val customer = customerRepository.findByUserUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val info = customer.toDto()

        return ResponseEntity(info, HttpStatus.OK)
    }

    fun getCustomerInfoById(id: Long): ResponseEntity<CustomerInfo> {
        val customer = customerRepository.findById(id).getOrNull() ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        return ResponseEntity.ok(customer.toDto())
    }

    fun createCustomer(customerInfo: CustomerInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val user = userRepository.findByUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

//        val address = geocodingService.getAddressFromCoordinates(customerInfo.latitude, customerInfo.longitude)
//            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        val customer = Customer(
            user = user,
            name = customerInfo.name,
            phoneNumber = customerInfo.phoneNumber,
            address = customerInfo.address,
            location = GeocodingUtils.createPoint(customerInfo.latitude!!, customerInfo.longitude!!)
        )

        customerRepository.save(customer)

        userService.addUserRole(Role.CUSTOMER)

        return ResponseEntity(HttpStatus.OK)
    }

    fun updateCustomer(customerInfo: CustomerInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val customer = customerRepository.findByUserUid(uid) ?: return createCustomer(customerInfo)

//        val address = geocodingService.getAddressFromCoordinates(customerInfo.latitude, customerInfo.longitude)
//            ?: return ResponseEntity(HttpStatus.BAD_REQUEST)

        customer.name = customerInfo.name
        customer.phoneNumber = customerInfo.phoneNumber
        customer.address = customerInfo.address
        customer.location = GeocodingUtils.createPoint(customerInfo.latitude!!, customerInfo.longitude!!)

        customerRepository.save(customer)

        return ResponseEntity(HttpStatus.OK)
    }
}