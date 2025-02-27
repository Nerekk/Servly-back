package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.ReviewInfo
import java.time.LocalDateTime

@Entity
@Table(name = "customer_reviews")
data class CustomerReview(
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
            reviewerName = jobRequest.jobPosting.customer.name,
            reviewerRating = jobRequest.jobPosting.customer.rating,
            jobRequestId = jobRequest.id!!,
            jobRequestStatus = jobRequest.jobRequestStatus,
            rating = rating,
            review = review,
            createdAt = createdAt
        )
    }
}