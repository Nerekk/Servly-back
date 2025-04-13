package org.example.servlyback.features.reviews

import jakarta.persistence.EntityNotFoundException
import org.example.servlyback.dto.ReviewInfo
import org.example.servlyback.entities.Customer
import org.example.servlyback.entities.CustomerReview
import org.example.servlyback.entities.Provider
import org.example.servlyback.entities.ProviderReview
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.example.servlyback.features._customer.CustomerRepository
import org.example.servlyback.features._provider.ProviderRepository
import org.example.servlyback.features.job_requests.JobRequestRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ReviewService(
    private val jobRequestRepository: JobRequestRepository,
    private val providerReviewRepository: ProviderReviewRepository,
    private val customerReviewRepository: CustomerReviewRepository,
    private val customerRepository: CustomerRepository,
    private val providerRepository: ProviderRepository
) {

    fun createCustomerReview(reviewInfo: ReviewInfo): ResponseEntity<ReviewInfo> {
        val jobRequest = jobRequestRepository.findById(reviewInfo.jobRequestId).orElseThrow {
            EntityNotFoundException("JobRequest with ID ${reviewInfo.jobRequestId} not found")
        }

        if (
            jobRequest.jobRequestStatus != JobRequestStatus.COMPLETED &&
            jobRequest.jobRequestStatus != JobRequestStatus.CANCELED_IN_PROGRESS_BY_PROVIDER
            ) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val review = CustomerReview(
            jobRequest = jobRequest,
            rating = reviewInfo.rating,
            review = reviewInfo.review
        )
        val customerReview = customerReviewRepository.save(review)
        updateRating(provider = customerReview.jobRequest.provider)

        return ResponseEntity.ok(customerReview.toDto())
    }

    fun createProviderReview(reviewInfo: ReviewInfo): ResponseEntity<ReviewInfo> {
        val jobRequest = jobRequestRepository.findById(reviewInfo.jobRequestId).orElseThrow {
            EntityNotFoundException("JobRequest with ID ${reviewInfo.jobRequestId} not found")
        }

        if (
            jobRequest.jobRequestStatus != JobRequestStatus.COMPLETED &&
            jobRequest.jobRequestStatus != JobRequestStatus.CANCELED_IN_PROGRESS_BY_CUSTOMER
        ) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val review = ProviderReview(
            jobRequest = jobRequest,
            rating = reviewInfo.rating,
            review = reviewInfo.review
        )
        val providerReview = providerReviewRepository.save(review)
        updateRating(customer = providerReview.jobRequest.jobPosting.customer)

        return ResponseEntity.ok(providerReview.toDto())
    }

    fun getCustomerReviews(customerId: Long, pageable: Pageable): ResponseEntity<Page<ReviewInfo>> {
        return ResponseEntity.ok(providerReviewRepository.findByJobRequestJobPostingCustomerCustomerId(customerId, pageable).map { it.toDto() })
    }

    fun getCustomerReview(jobRequestId: Long): ResponseEntity<ReviewInfo> {
        val jobRequest = jobRequestRepository.findById(jobRequestId).orElseThrow {
            EntityNotFoundException("JobRequest with ID $jobRequestId not found")
        }

        return if (jobRequest.customerReview != null) {
            ResponseEntity.ok(jobRequest.customerReview!!.toDto())
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    fun getProviderReviews(providerId: Long, pageable: Pageable): ResponseEntity<Page<ReviewInfo>> {
        return ResponseEntity.ok(customerReviewRepository.findByJobRequestProviderProviderId(providerId, pageable).map { it.toDto() })
    }

    fun getProviderReview(jobRequestId: Long): ResponseEntity<ReviewInfo> {
        val jobRequest = jobRequestRepository.findById(jobRequestId).orElseThrow {
            EntityNotFoundException("JobRequest with ID $jobRequestId not found")
        }

        return if (jobRequest.providerReview != null) {
            ResponseEntity.ok(jobRequest.providerReview!!.toDto())
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    fun deleteProviderReview(reviewId: Long): ResponseEntity<Unit> {
        if (providerReviewRepository.existsById(reviewId)) {
            val review = providerReviewRepository.findById(reviewId).getOrNull()
            providerReviewRepository.delete(review!!)
            updateRating(provider = review.jobRequest.provider)

            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    fun deleteCustomerReview(reviewId: Long): ResponseEntity<Unit> {
        if (customerReviewRepository.existsById(reviewId)) {
            val review = customerReviewRepository.findById(reviewId).getOrNull()
            customerReviewRepository.delete(review!!)
            updateRating(customer = review.jobRequest.jobPosting.customer)

            return ResponseEntity(HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    private fun updateRating(customer: Customer) {
//        val reviews = customerReviewRepository.findByJobRequestJobPostingCustomerCustomerId(customer.customerId!!)
//        customer.rating = reviews.sumOf { customerReview -> customerReview.rating }.toDouble() / reviews.count()

        val averageRating = customerReviewRepository.findAverageRatingByCustomerId(customer.customerId!!)
        println("AVERAGE RATING FOR CUSTOMER: $averageRating")
        customer.rating = averageRating

        customerRepository.save(customer)
    }

    private fun updateRating(provider: Provider) {
//        val reviews = providerReviewRepository.findByJobRequestProviderProviderId(provider.providerId!!)
//        provider.rating = reviews.sumOf { providerReview -> providerReview.rating }.toDouble() / reviews.count()

        val averageRating = providerReviewRepository.findAverageRatingByProviderId(provider.providerId!!)
        println("AVERAGE RATING FOR PROVIDER: $averageRating")
        provider.rating = averageRating

        providerRepository.save(provider)
    }
}