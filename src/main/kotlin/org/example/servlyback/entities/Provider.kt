package org.example.servlyback.entities

import org.locationtech.jts.geom.Point
import jakarta.persistence.*

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
    var city: String,

    @Column(name = "range_in_km", nullable = false)
    var rangeInKm: Double,

    @Column(nullable = true)
    var location: Point? = null
)
