package org.example.servlyback.features._provider

import org.example.servlyback.dto.ProviderInfo
import org.example.servlyback.util.ControllerMappings
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/" + ControllerMappings.PROVIDER)
class ProviderController(private val providerService: ProviderService) {

    @GetMapping
    fun getProvider(): ResponseEntity<ProviderInfo> {
        return providerService.getProviderInfo()
    }

    @GetMapping("/{id}")
    fun getProviderById(
        @PathVariable("id") id: Long
    ): ResponseEntity<ProviderInfo> {
        return providerService.getProviderInfoById(id)
    }

    @PostMapping
    fun createProvider(@RequestBody providerInfo: ProviderInfo): ResponseEntity<Unit> {
        return providerService.createProvider(providerInfo)
    }

    @PutMapping
    fun updateProvider(@RequestBody providerInfo: ProviderInfo): ResponseEntity<Unit> {
        return providerService.updateProvider(providerInfo)
    }
}