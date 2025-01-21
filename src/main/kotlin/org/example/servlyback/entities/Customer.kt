package org.example.servlyback.entities

import org.locationtech.jts.geom.Point
import jakarta.persistence.*

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
    var city: String,

    @Column(nullable = false)
    var street: String,

    @Column(name = "house_number", nullable = true)
    var houseNumber: String? = null,

    @Column(nullable = true)
    var location: Point? = null
)
