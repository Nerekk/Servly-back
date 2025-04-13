package org.example.servlyback.features.geocoding

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class GeocodingService(
    private val restTemplate: RestTemplate,
    private val geocodingProperties: GeocodingProperties
) {
    private val objectMapper = jacksonObjectMapper()

    fun getAddressFromCoordinates(lat: Double?, lng: Double?): String? {
        if (lat == null || lng == null) return null

        val url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=$lat,$lng&key=${geocodingProperties.apiKey}"
        val response = restTemplate.getForObject(url, String::class.java)
        val root: JsonNode = objectMapper.readTree(response)

        return if (root["status"].asText() == "OK") {
            root["results"].firstOrNull()?.get("formatted_address")?.asText()
        } else null
    }
}