package org.example.servlyback.features.job_postings

import org.example.servlyback.dto.JobPostingInfo
import org.example.servlyback.entities.custom_fields.JobStatus
import org.example.servlyback.util.ControllerMappings
import org.example.servlyback.util.SortType
import org.springframework.web.bind.annotation.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/" + ControllerMappings.JOB_POSTING)
class JobPostingController(private val jobPostingService: JobPostingService) {

    @PostMapping
    fun createJobPosting(@RequestBody dto: JobPostingInfo): ResponseEntity<JobPostingInfo> {
        return jobPostingService.createJob(dto)
    }

    @GetMapping("/user/{id}")
    fun getJobPosting(
        @PathVariable id: Long
    ): ResponseEntity<JobPostingInfo> {
        return jobPostingService.getJob(id)
    }

    @GetMapping("/user")
    fun getJobPostings(
        @RequestParam(required = false) statuses: List<JobStatus>,
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<JobPostingInfo>> {
        val pageable: Pageable = PageRequest.of(page, size)
        return jobPostingService.getJobs(statuses, sortType, pageable)
    }

    @GetMapping("/user/ended")
    fun getJobPostings(
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<JobPostingInfo>> {
        val pageable: Pageable = PageRequest.of(page, size)
        return jobPostingService.getJobsEnded(sortType, pageable)
    }

    @GetMapping("/global")
    fun getActiveJobPostings(
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(required = false) search: String?,
        @RequestParam(required = false) categories: List<Long>?,
        @RequestParam(required = false) days: Long?
    ): ResponseEntity<Page<JobPostingInfo>> {
        val sortDirection = if (sortType == SortType.ASCENDING) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        return jobPostingService.getActiveJobs(pageable, search, categories, days)
    }

    @PutMapping("/{jobPostingId}/status")
    fun updateJobStatus(
        @PathVariable jobPostingId: Long,
        @RequestParam status: JobStatus
    ): ResponseEntity<JobPostingInfo> {
        return jobPostingService.updateJobStatus(jobPostingId, status)
    }
}