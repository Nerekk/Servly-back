package org.example.servlyback.features.reviews

import org.example.servlyback.dto.ReviewInfo
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.util.SortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/review")
class ReviewController(private val reviewService: ReviewService) {

    @PostMapping("/customer")
    fun createCustomerReview(@RequestBody reviewInfo: ReviewInfo): ResponseEntity<ReviewInfo> {
        return reviewService.createCustomerReview(reviewInfo)
    }

    @PostMapping("/provider")
    fun createProviderReview(@RequestBody reviewInfo: ReviewInfo): ResponseEntity<ReviewInfo> {
        return reviewService.createProviderReview(reviewInfo)
    }

    @GetMapping("/customer/list/{customerId}")
    fun getCustomerReviews(
        @PathVariable customerId: Long,
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<ReviewInfo>> {
        val sortDirection = if (sortType == SortType.ASCENDING) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        return reviewService.getCustomerReviews(customerId, pageable)
    }

    @GetMapping("/customer/{jobRequestId}")
    fun getCustomerReview(@PathVariable jobRequestId: Long): ResponseEntity<ReviewInfo> {
        return reviewService.getCustomerReview(jobRequestId)
    }

    @GetMapping("/provider/list/{providerId}")
    fun getProviderReviews(
        @PathVariable providerId: Long,
        @RequestParam sortType: SortType,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<ReviewInfo>> {
        val sortDirection = if (sortType == SortType.ASCENDING) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        return reviewService.getProviderReviews(providerId, pageable)
    }

    @GetMapping("/provider/{jobRequestId}")
    fun getProviderReview(@PathVariable jobRequestId: Long): ResponseEntity<ReviewInfo> {
        return reviewService.getProviderReview(jobRequestId)
    }

    @DeleteMapping("/customer/{reviewId}")
    fun deleteProviderReview(@PathVariable reviewId: Long): ResponseEntity<Unit> {
        return reviewService.deleteProviderReview(reviewId)
    }

    @DeleteMapping("/provider/{reviewId}")
    fun deleteCustomerReview(@PathVariable reviewId: Long): ResponseEntity<Unit> {
        return reviewService.deleteCustomerReview(reviewId)
    }

}