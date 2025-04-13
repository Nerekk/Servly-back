package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.entities.custom_fields.Role
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @Column(nullable = false, unique = true)
    var uid: String,

    @Column(name = "fcm_token")
    var fcmToken: String = "",

    @Column(nullable = false, unique = true)
    var email: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.NONE,

    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)