package org.example.servlyback.features.reviews

import org.example.servlyback.entities.CustomerReview
import org.example.servlyback.entities.ProviderReview
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProviderReviewRepository: JpaRepository<ProviderReview, Long> {
    fun findByJobRequestProviderProviderId(providerId: Long): List<ProviderReview>
    fun findByJobRequestProviderProviderId(providerId: Long, pageable: Pageable): Page<ProviderReview>

    fun findByJobRequestJobPostingCustomerCustomerId(customerId: Long, pageable: Pageable): Page<ProviderReview>


//    @Query("SELECT AVG(r.rating) FROM ProviderReview r WHERE r.jobRequest.provider.providerId = :providerId")
//    fun findAverageRatingByProviderId(@Param("providerId") providerId: Long): Double?


    @Query("""
    SELECT AVG(r.rating) 
    FROM CustomerReview r 
    WHERE r.jobRequest.provider.providerId = :providerId 
    AND r.jobRequest.jobRequestStatus = :status
""")
    fun findAverageRatingByProviderId(
        @Param("providerId") providerId: Long,
        @Param("status") status: JobRequestStatus = JobRequestStatus.COMPLETED
    ): Double?
}