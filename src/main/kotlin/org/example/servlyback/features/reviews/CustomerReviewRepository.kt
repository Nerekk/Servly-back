package org.example.servlyback.features.reviews

import org.example.servlyback.dto.ReviewInfo
import org.example.servlyback.entities.CustomerReview
import org.example.servlyback.entities.ProviderReview
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CustomerReviewRepository: JpaRepository<CustomerReview, Long> {
    fun findByJobRequestJobPostingCustomerCustomerId(customerId: Long): List<CustomerReview>
    fun findByJobRequestJobPostingCustomerCustomerId(customerId: Long, pageable: Pageable): Page<CustomerReview>

    fun findByJobRequestProviderProviderId(providerId: Long, pageable: Pageable): Page<CustomerReview>


//    @Query("SELECT AVG(r.rating) FROM CustomerReview r WHERE r.jobRequest.jobPosting.customer.customerId = :customerId")
//    fun findAverageRatingByCustomerId(@Param("customerId") customerId: Long): Double?

    @Query("""
    SELECT AVG(r.rating) 
    FROM ProviderReview r 
    WHERE r.jobRequest.jobPosting.customer.customerId = :customerId 
    AND r.jobRequest.jobRequestStatus = :status
""")
    fun findAverageRatingByCustomerId(
        @Param("customerId") customerId: Long,
        @Param("status") status: JobRequestStatus = JobRequestStatus.COMPLETED
    ): Double?
}