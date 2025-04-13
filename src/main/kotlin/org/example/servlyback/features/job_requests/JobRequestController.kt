package org.example.servlyback.features.job_requests

import org.example.servlyback.dto.JobRequestInfo
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.example.servlyback.util.ControllerMappings
import org.example.servlyback.util.SortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/" + ControllerMappings.JOB_REQUEST)
class JobRequestController(private val jobRequestService: JobRequestService) {

    @PostMapping
    fun createJobRequest(
        @RequestParam jobPostingId: Long,
    ): ResponseEntity<JobRequestInfo> {
        return jobRequestService.createJobRequest(jobPostingId)
    }

    @GetMapping("/user")
    fun getJobRequestsUser(
        @RequestParam jobRequestStatuses: List<JobRequestStatus>,
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): Page<JobRequestInfo> {
        val sortDirection = if (sortType == SortType.ASCENDING) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        return jobRequestService.getJobRequestsUser(jobRequestStatuses, pageable)
    }

    @GetMapping("/posting/{jobPostingId}")
    fun getJobRequestsPosting(
        @PathVariable jobPostingId: Long
    ): List<JobRequestInfo> {
        return jobRequestService.getJobRequestPosting(jobPostingId)
    }

    @GetMapping("/provider/{jobPostingId}")
    fun getJobRequestForProvider(
        @PathVariable jobPostingId: Long,
        @RequestParam providerId: Long
    ): ResponseEntity<JobRequestInfo> {
        return jobRequestService.getJobRequestForProvider(jobPostingId, providerId)
    }

    @GetMapping("/customer/{jobPostingId}")
    fun getJobRequestSelectedForCustomer(
        @PathVariable jobPostingId: Long
    ): ResponseEntity<JobRequestInfo> {
        return jobRequestService.getJobRequestSelectedForCustomer(jobPostingId)
    }

    @PutMapping
    fun updateJobRequestStatus(
        @RequestParam jobRequestId: Long,
        @RequestParam jobRequestStatus: JobRequestStatus
    ): ResponseEntity<JobRequestInfo> {
        return jobRequestService.updateJobRequestStatus(jobRequestId, jobRequestStatus)
    }

}