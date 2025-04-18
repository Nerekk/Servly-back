package org.example.servlyback.payments.stripe

import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import org.example.servlyback.dto.StripePaymentRequest
import org.example.servlyback.dto.StripePaymentResponse
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class StripeService(private val stripeProperties: StripeProperties) {

    init {
        Stripe.apiKey = stripeProperties.secretKey
    }

    fun createIntent(stripePaymentRequest: StripePaymentRequest): ResponseEntity<StripePaymentResponse> {
        val params = PaymentIntentCreateParams.builder()
            .setAmount(stripePaymentRequest.amount)
            .setCurrency("pln")
            .putMetadata("jobRequestId", stripePaymentRequest.jobRequestId.toString())
            .build()

        val intent = PaymentIntent.create(params)

        return ResponseEntity.ok(StripePaymentResponse(intent.clientSecret, intent.id))
    }
}