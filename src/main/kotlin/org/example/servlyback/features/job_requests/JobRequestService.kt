package org.example.servlyback.features.job_requests

import jakarta.persistence.EntityNotFoundException
import org.example.servlyback.dto.JobRequestInfo
import org.example.servlyback.entities.JobRequest
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.example.servlyback.entities.custom_fields.JobStatus
import org.example.servlyback.features._provider.ProviderRepository
import org.example.servlyback.features.job_postings.JobPostingRepository
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.security.firebase.TokenManager.Companion.verifyCustomer
import org.example.servlyback.security.firebase.TokenManager.Companion.verifyProvider
import org.example.servlyback.security.firebase.handleJobRequestNotification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JobRequestService(
    private val jobRequestRepository: JobRequestRepository,
    private val jobPostingRepository: JobPostingRepository,
    private val providerRepository: ProviderRepository
)
{
    @Transactional
    fun createJobRequest(jobPostingId: Long): ResponseEntity<JobRequestInfo> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val provider = providerRepository.findByUserUid(uid)
            ?: return ResponseEntity(HttpStatus.FORBIDDEN)

        val jobPosting = jobPostingRepository.findById(jobPostingId).orElseThrow {
            EntityNotFoundException("JobPosting with ID $jobPostingId not found")
        }

        val jobRequestCount = jobRequestRepository.countByJobPosting(jobPosting)
        if (jobRequestCount >= 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }

        val jobRequest = JobRequest(
            jobPosting = jobPosting,
            provider = provider,
            jobRequestStatus = JobRequestStatus.ACTIVE
        )

        val savedJobRequest = jobRequestRepository.save(jobRequest)

        handleJobRequestNotification(jobPosting.customer.user, jobPosting.title, "${provider.name} jest zainteresowany zleceniem")

        return ResponseEntity.ok(savedJobRequest.toDto())
    }

    fun getJobRequestsUser(statuses: List<JobRequestStatus>, pageable: Pageable): Page<JobRequestInfo> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        return jobRequestRepository.findByProviderUserUidAndJobRequestStatusIn(uid, statuses, pageable).map { it.toDto() }
    }

    fun getJobRequestPosting(jobPostingId: Long): List<JobRequestInfo> {
        val jobRequests = jobRequestRepository.findByJobPostingId(jobPostingId)
        return jobRequests.map { it.toDto() }
    }

    fun getJobRequestForProvider(jobPostingId: Long, providerId: Long): ResponseEntity<JobRequestInfo> {

        val jobRequest = jobRequestRepository.findByJobPostingIdAndProviderProviderId(jobPostingId, providerId)
        jobRequest?.let {
            return ResponseEntity.ok(it.toDto())
        } ?: run {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    fun getJobRequestSelectedForCustomer(jobPostingId: Long): ResponseEntity<JobRequestInfo> {
        val jobRequest = jobRequestRepository.findByJobPostingIdAndJobRequestStatusNotIn(jobPostingId, listOf(
            JobRequestStatus.ACTIVE, JobRequestStatus.WITHDRAWN, JobRequestStatus.WAITING_FOR_PROVIDER_APPROVE
        ))

        jobRequest?.let {
            return ResponseEntity.ok(it.toDto())
        } ?: run {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    // not my proudest function
    @Transactional
    fun updateJobRequestStatus(jobRequestId: Long, jobRequestStatus: JobRequestStatus): ResponseEntity<JobRequestInfo> {
        val jobRequest = jobRequestRepository.findById(jobRequestId).orElseThrow {
            EntityNotFoundException("JobRequest with ID $jobRequestId not found")
        }

        var finalStatus: JobRequestStatus? = null

        finalStatus = when (jobRequest.jobRequestStatus) {
            JobRequestStatus.ACTIVE -> {
                if (jobRequestStatus == JobRequestStatus.WAITING_FOR_PROVIDER_APPROVE && verifyCustomer(jobRequest)) {
                    handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "${jobRequest.jobPosting.customer.name} wysłał(a) prośbę współpracy!")
                    JobRequestStatus.WAITING_FOR_PROVIDER_APPROVE
                } else if (jobRequestStatus == JobRequestStatus.WITHDRAWN && verifyProvider(jobRequest)) {
                    handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "${jobRequest.provider.name} wycofał(a) zainteresowanie zleceniem")
                    JobRequestStatus.WITHDRAWN
                } else {
                    null
                }
            }
            JobRequestStatus.WAITING_FOR_PROVIDER_APPROVE -> {
                if (jobRequestStatus == JobRequestStatus.IN_PROGRESS && verifyProvider(jobRequest)) {
                    val otherActiveRequests = jobRequestRepository.findByJobPostingIdAndJobRequestStatus(
                        jobRequest.jobPosting.id!!, JobRequestStatus.ACTIVE
                    )
                    otherActiveRequests.forEach {
                        it.jobRequestStatus = JobRequestStatus.REJECTED
                    }
                    jobRequestRepository.saveAll(otherActiveRequests)

                    jobRequest.jobPosting.status = JobStatus.IN_PROGRESS
                    handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "${jobRequest.provider.name} zaakceptował(a) zlecenie!")

                    JobRequestStatus.IN_PROGRESS
                } else if (jobRequestStatus == JobRequestStatus.WITHDRAWN && verifyProvider(jobRequest)) {
                    JobRequestStatus.WITHDRAWN
                } else {
                    null
                }
            }
            JobRequestStatus.IN_PROGRESS -> {
                if (jobRequestStatus == JobRequestStatus.WAITING_FOR_PROVIDER_COMPLETE && verifyCustomer(jobRequest)) {
                    handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "${jobRequest.jobPosting.customer.name} chce ukończyć zlecenie")
                    JobRequestStatus.WAITING_FOR_PROVIDER_COMPLETE
                } else if (jobRequestStatus == JobRequestStatus.WAITING_FOR_CUSTOMER_COMPLETE && verifyProvider(jobRequest)) {
                    handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "${jobRequest.provider.name} chce ukończyć zlecenie")
                    JobRequestStatus.WAITING_FOR_CUSTOMER_COMPLETE
                } else if (jobRequestStatus == JobRequestStatus.CANCELED_IN_PROGRESS_BY_PROVIDER && verifyProvider(jobRequest)) {
                    handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "${jobRequest.provider.name} anulował zlecenie w trakcie")
                    jobRequest.jobPosting.status = JobStatus.CANCELED
                    JobRequestStatus.CANCELED_IN_PROGRESS_BY_PROVIDER
                } else if (jobRequestStatus == JobRequestStatus.CANCELED_IN_PROGRESS_BY_CUSTOMER && verifyCustomer(jobRequest)) {
                    handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "${jobRequest.jobPosting.customer.name} anulował zlecenie w trakcie")
                    jobRequest.jobPosting.status = JobStatus.CANCELED
                    JobRequestStatus.CANCELED_IN_PROGRESS_BY_CUSTOMER
                } else {
                    null
                }
            }
            JobRequestStatus.WAITING_FOR_CUSTOMER_COMPLETE -> {
                if (jobRequestStatus == JobRequestStatus.COMPLETED && verifyCustomer(jobRequest)) {
                    handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "Zlecenie zostało ukończone")
                    jobRequest.jobPosting.status = JobStatus.COMPLETED
                    JobRequestStatus.COMPLETED
                } else if (jobRequestStatus == JobRequestStatus.IN_PROGRESS && verifyCustomer(jobRequest)) {
                    JobRequestStatus.IN_PROGRESS
                } else {
                    null
                }
            }
            JobRequestStatus.WAITING_FOR_PROVIDER_COMPLETE -> {
                if (jobRequestStatus == JobRequestStatus.COMPLETED && verifyProvider(jobRequest)) {
                    handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "Zlecenie zostało ukończone")
                    jobRequest.jobPosting.status = JobStatus.COMPLETED
                    JobRequestStatus.COMPLETED
                } else if (jobRequestStatus == JobRequestStatus.IN_PROGRESS && verifyProvider(jobRequest)) {
                    JobRequestStatus.IN_PROGRESS
                } else {
                    null
                }
            }
            else -> {
                null
            }
        }
        if (finalStatus == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }

        jobRequest.jobRequestStatus = finalStatus
        val updatedJobRequest = jobRequestRepository.save(jobRequest)

        return ResponseEntity.ok(updatedJobRequest.toDto())
    }

}