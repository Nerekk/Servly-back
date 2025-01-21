package org.example.servlyback.features_customer

import org.example.servlyback.dto.CustomerInfo
import org.example.servlyback.entities.Customer
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.user.UserRepository
import org.example.servlyback.user.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class CustomerService(
    private val customerRepository: CustomerRepository,
    private val userRepository: UserRepository,
    private val userService: UserService
) {

    fun getCustomerInfo(): ResponseEntity<CustomerInfo> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val customer = customerRepository.findByUserUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val info = CustomerInfo(
            name = customer.name,
            phoneNumber = customer.phoneNumber,
            city = customer.city,
            street = customer.street,
            houseNumber = customer.houseNumber,
            longitude = customer.location?.x,
            latitude = customer.location?.y,
        )

        return ResponseEntity(info, HttpStatus.OK)
    }

    fun createCustomer(customerInfo: CustomerInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val user = userRepository.findByUid(uid) ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val customer = Customer(
            user = user,
            name = customerInfo.name,
            phoneNumber = customerInfo.phoneNumber,
            city = customerInfo.city,
            street = customerInfo.street,
            houseNumber = customerInfo.houseNumber
        )

        customerRepository.save(customer)

        userService.addUserRole(Role.CUSTOMER)

        return ResponseEntity(HttpStatus.OK)
    }

    fun updateCustomer(customerInfo: CustomerInfo): ResponseEntity<Unit> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val customer = customerRepository.findByUserUid(uid) ?: return createCustomer(customerInfo)

        customer.name = customerInfo.name
        customer.phoneNumber = customerInfo.phoneNumber
        customer.city = customerInfo.city
        customer.street = customerInfo.street
        customer.houseNumber = customerInfo.houseNumber

        customerRepository.save(customer)

        return ResponseEntity(HttpStatus.OK)
    }
}