package org.example.servlyback.entities

import org.locationtech.jts.geom.Point
import jakarta.persistence.*
import org.example.servlyback.dto.CustomerInfo

@Entity
@Table(name = "customers")
data class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    val customerId: Long? = null,

    @OneToOne
    @JoinColumn(name = "user_uid", nullable = false, unique = true)
    val user: User,

    @Column(nullable = false)
    var name: String,

    @Column(name = "phone_number", nullable = false)
    var phoneNumber: String,

    @Column(nullable = false)
    var address: String,

    @Column(nullable = false, columnDefinition = "geography(Point, 4326)")
    var location: Point,

    @Column(nullable = true)
    var rating: Double? = null
) {
    fun toDto(): CustomerInfo {
        return CustomerInfo(
            customerId,
            name,
            phoneNumber,
            address,
            longitude = location.x,
            latitude = location.y,
            rating = rating
        )
    }
}
