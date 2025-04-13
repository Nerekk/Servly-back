package org.example.servlyback.features.geocoding

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(GeocodingProperties::class)
class GeocodingConfig {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}