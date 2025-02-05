package org.example.servlyback.features_customer.job_requests

import org.example.servlyback.dto.JobPostingInfo
import org.example.servlyback.entities.custom_fields.JobStatus
import org.example.servlyback.util.ControllerMappings
import org.example.servlyback.util.SortType
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/" + ControllerMappings.JOB_POSTING)
class JobPostingController(private val jobPostingService: JobPostingService) {

    @PostMapping
    fun createJobPosting(@RequestBody dto: JobPostingInfo): ResponseEntity<JobPostingInfo> {
        return jobPostingService.createJobRequest(dto)
    }

    @GetMapping("/user")
    fun getJobPostings(
        @RequestParam(required = false) status: JobStatus,
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<JobPostingInfo>> {
        val pageable: Pageable = PageRequest.of(page, size)
        return jobPostingService.getJobRequests(status, sortType, pageable)
    }

    @GetMapping("/user/ended")
    fun getJobPostings(
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<JobPostingInfo>> {
        val pageable: Pageable = PageRequest.of(page, size)
        return jobPostingService.getJobRequestsEnded(sortType, pageable)
    }

    @GetMapping("/global")
    fun getActiveJobPostings(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<JobPostingInfo>> {
        val pageable: Pageable = PageRequest.of(page, size)
        return jobPostingService.getActiveJobRequests(pageable)
    }

    @PatchMapping("/{jobRequestId}/status")
    fun updateJobStatus(
        @PathVariable jobRequestId: Long,
        @RequestParam status: JobStatus
    ): ResponseEntity<JobPostingInfo> {
        return jobPostingService.updateJobStatus(jobRequestId, status)
    }
}