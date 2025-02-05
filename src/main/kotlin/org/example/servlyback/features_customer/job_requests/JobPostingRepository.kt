package org.example.servlyback.features_customer.job_requests

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.example.servlyback.entities.JobPosting
import org.example.servlyback.entities.custom_fields.JobStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface JobPostingRepository : JpaRepository<JobPosting, Long> {
    fun findByCustomerUserUid(uid: String, pageable: Pageable): Page<JobPosting>
    fun findByCustomerUserUidAndStatus(uid: String, status: JobStatus, pageable: Pageable): Page<JobPosting>

    fun findByCustomerUserUidAndStatusOrderByCreatedAtDesc(
        uid: String,
        status: JobStatus,
        pageable: Pageable
    ): Page<JobPosting>
    fun findByCustomerUserUidAndStatusOrderByCreatedAtAsc(
        uid: String,
        status: JobStatus,
        pageable: Pageable
    ): Page<JobPosting>

    fun findByCustomerUserUidAndStatusInOrderByCreatedAtDesc(
        uid: String,
        statuses: List<JobStatus>,
        pageable: Pageable
    ): Page<JobPosting>
    fun findByCustomerUserUidAndStatusInOrderByCreatedAtAsc(
        uid: String,
        statuses: List<JobStatus>,
        pageable: Pageable
    ): Page<JobPosting>

    fun findByStatus(status: JobStatus, pageable: Pageable): Page<JobPosting>
    fun findByIdAndCustomerUserUid(jobPostingId: Long, uid: String): Optional<JobPosting>
}