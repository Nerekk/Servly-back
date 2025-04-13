package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.ReviewInfo
import java.time.LocalDateTime

@Entity
@Table(name = "provider_reviews")
data class ProviderReview(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "job_request_id", nullable = false, unique = true)
    val jobRequest: JobRequest,

    @Column(nullable = false)
    val rating: Int,

    @Column(nullable = false, columnDefinition = "TEXT")
    val review: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDto(): ReviewInfo {
        return ReviewInfo(
            id = id!!,
            reviewerName = jobRequest.provider.name,
            reviewerRating = jobRequest.provider.rating,
            jobRequestId = jobRequest.id!!,
            jobRequestStatus = jobRequest.jobRequestStatus,
            rating = rating,
            review = review,
            createdAt = createdAt
        )
    }
}