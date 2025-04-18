package org.example.servlyback.payments.stripe

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "stripe")
class StripeProperties {
    lateinit var secretKey: String
}