package org.example.servlyback.features.job_postings

import jakarta.persistence.criteria.Predicate
import org.example.servlyback.dto.JobPostingInfo
import org.example.servlyback.entities.Category
import org.example.servlyback.entities.JobAnswer
import org.example.servlyback.entities.JobPosting
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.example.servlyback.entities.custom_fields.JobStatus
import org.example.servlyback.features._customer.CustomerRepository
import org.example.servlyback.features.category.CategoryRepository
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.util.SortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class JobPostingService(
    private val jobPostingRepository: JobPostingRepository,
    private val categoryRepository: CategoryRepository,
    private val customerRepository: CustomerRepository
) {

    @Transactional
    fun createJob(dto: JobPostingInfo): ResponseEntity<JobPostingInfo> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: throw IllegalArgumentException("Unauthorized")
        val uid = firebaseToken.uid

        val customer = customerRepository.findByUserUid(uid) ?: throw IllegalArgumentException("Customer not found")


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
    fun getJob(id: Long): ResponseEntity<JobPostingInfo> {
        return jobPostingRepository.findById(id)
            .map { ResponseEntity.ok(it.toJobPostingInfo()) }
            .orElseGet { ResponseEntity.notFound().build() }
    }

    @Transactional
    fun getJobs(statuses: List<JobStatus>, sortType: SortType, pageable: Pageable): ResponseEntity<Page<JobPostingInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val jobRequests = if (sortType == SortType.ASCENDING) {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtAsc(uid, statuses, pageable)
        } else {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtDesc(uid, statuses, pageable)
        }
        val jobRequestInfo = jobRequests.map { it.toJobPostingInfo() }

        return ResponseEntity(jobRequestInfo, HttpStatus.OK)
    }

    @Transactional
    fun getJobsEnded(sortType: SortType, pageable: Pageable): ResponseEntity<Page<JobPostingInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken() ?: return ResponseEntity(HttpStatus.UNAUTHORIZED)
        val uid = firebaseToken.uid

        val jobRequests = if (sortType == SortType.ASCENDING) {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtAsc(uid, listOf(JobStatus.COMPLETED, JobStatus.CANCELED), pageable)
        } else {
            jobPostingRepository.findByCustomerUserUidAndStatusInOrderByCreatedAtDesc(uid, listOf(JobStatus.COMPLETED, JobStatus.CANCELED), pageable)
        }
        val jobRequestInfo = jobRequests.map { it.toJobPostingInfo() }

        return ResponseEntity(jobRequestInfo, HttpStatus.OK)
    }

    @Transactional
    fun getActiveJobs(
        pageable: Pageable,
        search: String?,
        categories: List<Long>?,
        days: Long?
    ): ResponseEntity<Page<JobPostingInfo>> {
        val spec = getSpecification(search, categories, days)

        val jobPostings = jobPostingRepository.findAll(spec, pageable)

        val jobPostingsInfo = jobPostings.map { it.toJobPostingInfo() }
        return ResponseEntity(jobPostingsInfo, HttpStatus.OK)
    }

    private fun getSpecification(
        search: String?,
        categories: List<Long>?,
        days: Long?
    ): Specification<JobPosting> {
        val spec = Specification<JobPosting> { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            predicates.add(cb.equal(root.get<JobStatus>("status"), JobStatus.ACTIVE))

            if (!search.isNullOrBlank()) {
                if (search.startsWith("\"") && search.endsWith("\"")) {
                    val unquotedSearch = search.substring(1, search.length - 1)
                    val likePredicate = cb.like(
                        cb.lower(root.get("title")),
                        "%${unquotedSearch.lowercase()}%"
                    )
                    predicates.add(likePredicate)
                } else {
                    val likePredicate = cb.like(cb.lower(root.get("title")), "%${search.lowercase()}%")
                    val similarityFunction = cb.function(
                        "similarity",
                        Double::class.java,
                        root.get<String>("title"),
                        cb.literal(search)
                    )
                    val similarityPredicate = cb.greaterThanOrEqualTo(similarityFunction, 0.1)

                    val combinedPredicate = cb.or(
                        likePredicate,
                        cb.and(cb.not(likePredicate), similarityPredicate)
                    )
                    predicates.add(combinedPredicate)
                }
            }

            if (!categories.isNullOrEmpty()) {
                val categoryIdPath = root.get<Category>("category").get<Long>("id")
                predicates.add(categoryIdPath.`in`(categories))
            }

            if (days != null) {
                val fromDate = LocalDateTime.now().minusDays(days)
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate))
            }

            cb.and(*predicates.toTypedArray())
        }
        return spec
    }

    @Transactional
    fun updateJobStatus(jobPostingId: Long, status: JobStatus): ResponseEntity<JobPostingInfo> {
        val invalidStatuses = setOf(
            JobRequestStatus.IN_PROGRESS,
            JobRequestStatus.WAITING_FOR_PROVIDER_COMPLETE,
            JobRequestStatus.WAITING_FOR_CUSTOMER_COMPLETE,
            JobRequestStatus.COMPLETED,
            JobRequestStatus.CANCELED_IN_PROGRESS_BY_CUSTOMER,
            JobRequestStatus.CANCELED_IN_PROGRESS_BY_PROVIDER
        )

        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val jobPosting = jobPostingRepository.findByIdAndCustomerUserUid(jobPostingId, uid)
            .orElseThrow { IllegalArgumentException("JobRequest not found with id: $jobPostingId") }

        if (jobPosting.jobRequests.any { it.jobRequestStatus in invalidStatuses }) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        jobPosting.status = status
        if (status == JobStatus.CANCELED) {
            jobPosting.jobRequests.forEach { it.jobRequestStatus = JobRequestStatus.WITHDRAWN }
        }

        return ResponseEntity(jobPostingRepository.save(jobPosting).toJobPostingInfo(), HttpStatus.OK)
    }


//    fun JobPosting.toJobPostingInfo() = JobPostingInfo(
//        id = this.id!!,
//        customerId = this.customer.customerId!!,
//        customerName = this.customer.name,
//        title = this.title,
//        categoryId = this.category.id,
//        city = this.city,
//        street = this.street,
//        houseNumber = this.houseNumber,
//        latitude = this.location?.y,
//        longitude = this.location?.x,
//        status = this.status,
//        answers = this.answers.map { it.toJobAnswerInfo() }
//    )

//    fun JobAnswer.toJobAnswerInfo() = JobAnswerInfo(
//        id = this.question.id,
//        answer = this.answerText
//    )
}