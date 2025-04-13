package org.example.servlyback.features.geocoding

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "google")
class GeocodingProperties {
    lateinit var apiKey: String
}