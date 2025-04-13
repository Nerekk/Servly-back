package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.JobRequestStatus
import java.time.LocalDateTime

data class ReviewInfo(
    val id: Long? = null,
    val reviewerName: String? = null,
    val reviewerRating: Double? = null,
    val jobRequestId: Long,
    val jobRequestStatus: JobRequestStatus? = null,
    val rating: Int,
    val review: String,
    val createdAt: LocalDateTime? = null,
)
