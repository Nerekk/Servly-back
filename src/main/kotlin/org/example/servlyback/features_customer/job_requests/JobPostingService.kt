package org.example.servlyback.features_customer.job_requests

import org.example.servlyback.dto.JobAnswerInfo
import org.example.servlyback.dto.JobPostingInfo
import org.example.servlyback.entities.JobAnswer
import org.example.servlyback.entities.JobPosting
import org.example.servlyback.entities.custom_fields.JobStatus
import org.example.servlyback.features_customer.CustomerRepository
import org.example.servlyback.features_customer.category.CategoryRepository
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.util.SortType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
    private val categoryRepository: CategoryRepository,
    private val customerRepository: CustomerRepository
) {

    @Transactional
    fun createJobRequest(dto: JobPostingInfo): ResponseEntity<JobPostingInfo> {
        val customer = dto.customerId?.let { id ->
            customerRepository.findById(id)
                .orElseThrow { IllegalArgumentException("Customer not found") }
        } ?: run {
            val firebaseToken = TokenManager.getFirebaseToken() ?: throw IllegalArgumentException("Unauthorized")
            val uid = firebaseToken.uid

            customerRepository.findByUserUid(uid) ?: throw IllegalArgumentException("Customer not found")
        }

        val category = categoryRepository.findById(dto.categoryId)
            .orElseThrow { IllegalArgumentException("Category not found") }

        val jobPosting = JobPosting(
            customer = customer,
            title = dto.title,
            category = category,
            city = dto.city,
            street = dto.street,
            houseNumber = dto.houseNumber,
            location = null,
            status = dto.status
        )

        dto.answers.forEach { answerDto ->
            val question = category.questions.find { it.id == answerDto.id }
                ?: throw IllegalArgumentException("Question not found in category")

            val jobAnswer = JobAnswer(
                jobPosting = jobPosting,
                question = question,
                answerText = answerDto.answer
            )
            jobPosting.answers.add(jobAnswer)
        }

        return ResponseEntity(jobPostingRepository.save(jobPosting).toJobPostingInfo(), HttpStatus.OK)
    }

    @Transactional
    fun getJobRequests(status: JobStatus, sortType: SortType, pageable: Pageable): ResponseEntity<Page<JobPostingInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val jobRequests = if (sortType == SortType.ASCENDING) {
            jobPostingRepository.findByCustomerUserUidAndStatusOrderByCreatedAtAsc(uid, status, pageable)
        } else {
            jobPostingRepository.findByCustomerUserUidAndStatusOrderByCreatedAtDesc(uid, status, pageable)
        }
        val jobRequestInfo = jobRequests.map { it.toJobPostingInfo() }

        return ResponseEntity(jobRequestInfo, HttpStatus.OK)
    }

    @Transactional
    fun getJobRequestsEnded(sortType: SortType, pageable: Pageable): ResponseEntity<Page<JobPostingInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val jobRequests = if (sortType == SortType.ASCENDING) {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtAsc(uid, listOf(JobStatus.DONE, JobStatus.CANCELED), pageable)
        } else {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtDesc(uid, listOf(JobStatus.DONE, JobStatus.CANCELED), pageable)
        }
        val jobRequestInfo = jobRequests.map { it.toJobPostingInfo() }

        return ResponseEntity(jobRequestInfo, HttpStatus.OK)
    }

    @Transactional
    fun getActiveJobRequests(pageable: Pageable): ResponseEntity<Page<JobPostingInfo>> {
        val jobRequests = jobPostingRepository.findByStatus(JobStatus.ACTIVE, pageable)
        val jobRequestInfo = jobRequests.map { it.toJobPostingInfo() }

        return ResponseEntity(jobRequestInfo, HttpStatus.OK)
    }

    @Transactional
    fun updateJobStatus(jobRequestId: Long, status: JobStatus): ResponseEntity<JobPostingInfo> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val jobRequest = jobPostingRepository.findByIdAndCustomerUserUid(jobRequestId, uid)
            .orElseThrow { IllegalArgumentException("JobRequest not found with id: $jobRequestId") }

        jobRequest.status = status
        return ResponseEntity(jobPostingRepository.save(jobRequest).toJobPostingInfo(), HttpStatus.OK)
    }


    fun JobPosting.toJobPostingInfo() = JobPostingInfo(
        id = this.id!!,
        customerId = this.customer.customerId!!,
        title = this.title,
        categoryId = this.category.id,
        city = this.city,
        street = this.street,
        houseNumber = this.houseNumber,
        latitude = this.location?.y,
        longitude = this.location?.x,
        status = this.status,
        answers = this.answers.map { it.toJobAnswerInfo() }
    )

    fun JobAnswer.toJobAnswerInfo() = JobAnswerInfo(
        id = this.question.id,
        answer = this.answerText
    )
}