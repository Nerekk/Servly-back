package org.example.servlyback.features.job_postings

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.example.servlyback.entities.JobPosting
import org.example.servlyback.entities.custom_fields.JobStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

interface JobPostingRepository : JpaRepository<JobPosting, Long> , JpaSpecificationExecutor<JobPosting> {
    fun findByCustomerUserUid(uid: String, pageable: Pageable): Page<JobPosting>
    fun findByCustomerUserUidAndStatus(uid: String, status: JobStatus, pageable: Pageable): Page<JobPosting>

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