package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.JobRequestStatus

data class JobRequestInfo(
    val id: Long? = null,
    val jobPostingInfo: JobPostingInfo? = null,
    val provider: ProviderInfo? = null,
    val schedule: ScheduleInfo? = null,
    val customerReview: ReviewInfo? = null,
    val providerReview: ReviewInfo? = null,
    val jobRequestStatus: JobRequestStatus = JobRequestStatus.ACTIVE
)