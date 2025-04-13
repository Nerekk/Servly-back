package org.example.servlyback.entities

import org.locationtech.jts.geom.Point
import jakarta.persistence.*
import org.example.servlyback.dto.ProviderInfo

@Entity
@Table(name = "providers")
data class Provider(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "provider_id")
    val providerId: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_uid", nullable = false, unique = true)
    val user: User,

    @Column(nullable = false)
    var name: String,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @Column(nullable = false)
    var address: String,

    @Column(name = "range_in_km", nullable = false)
    var rangeInKm: Double,

    @Column(nullable = true, columnDefinition = "geography(Point, 4326)")
    var location: Point,

    @Column(nullable = true)
    var rating: Double? = null,

    @Column(name = "about_me", nullable = false, columnDefinition = "TEXT")
    var aboutMe: String = ""

) {
    fun toDto(): ProviderInfo {
        return ProviderInfo(
            providerId,
            name,
            phoneNumber,
            address,
            rangeInKm,
            longitude = location.x,
            latitude = location.y,
            rating = rating,
            aboutMe = aboutMe
        )
    }
}
