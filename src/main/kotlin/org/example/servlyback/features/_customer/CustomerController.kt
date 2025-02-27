package org.example.servlyback.features._customer

import org.example.servlyback.dto.CustomerInfo
import org.example.servlyback.util.ControllerMappings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/" + ControllerMappings.CUSTOMER)
class CustomerController(private val customerService: CustomerService) {

    @GetMapping
    fun getCustomer(): ResponseEntity<CustomerInfo> {
        return customerService.getCustomerInfo()
    }

    @GetMapping("/{id}")
    fun getCustomerById(
        @PathVariable("id") id: Long
    ): ResponseEntity<CustomerInfo> {
        return customerService.getCustomerInfoById(id)
    }

    @PostMapping
    fun createCustomer(@RequestBody customerInfo: CustomerInfo): ResponseEntity<Unit> {
        return customerService.createCustomer(customerInfo)
    }

    @PutMapping
    fun updateCustomer(@RequestBody customerInfo: CustomerInfo): ResponseEntity<Unit> {
        return customerService.updateCustomer(customerInfo)
    }
}