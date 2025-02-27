package org.example.servlyback.features.job_requests

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.example.servlyback.entities.JobPosting
import org.example.servlyback.entities.JobRequest
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.springframework.data.jpa.repository.JpaRepository

interface JobRequestRepository: JpaRepository<JobRequest, Long> {
    fun countByJobPosting(jobPosting: JobPosting): Int
    fun findByProviderUserUidAndJobRequestStatusIn(uid: String, statuses: List<JobRequestStatus>, pageable: Pageable): Page<JobRequest>
    fun findByJobPostingId(jobPostingId: Long): List<JobRequest>
    fun findByJobPostingIdAndProviderProviderId(jobPostingId: Long, providerId: Long): JobRequest?
    fun findByJobPostingIdAndJobRequestStatusNotIn(jobPostingId: Long, statuses: List<JobRequestStatus>): JobRequest?
    fun findByJobPostingIdAndJobRequestStatus(jobPostingId: Long, jobRequestStatus: JobRequestStatus): List<JobRequest>
}