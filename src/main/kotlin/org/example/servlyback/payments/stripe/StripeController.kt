package org.example.servlyback.payments.stripe

import com.stripe.Stripe
import com.stripe.model.terminal.ConnectionToken
import com.stripe.param.terminal.ConnectionTokenCreateParams
import org.example.servlyback.dto.StripePaymentRequest
import org.example.servlyback.dto.StripePaymentResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/stripe")
class StripeController(private val stripeService: StripeService) {

    @PostMapping("/intent")
    fun createIntent(@RequestBody stripePaymentRequest: StripePaymentRequest): ResponseEntity<StripePaymentResponse> {
        return stripeService.createIntent(stripePaymentRequest)
    }
}