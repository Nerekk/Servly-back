package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.JobRequestInfo
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import java.time.LocalDateTime

@Entity
@Table(name = "job_requests")
data class JobRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    val jobPosting: JobPosting,

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    val provider: Provider,

    @Enumerated(EnumType.STRING)
    @Column(name = "job_request_status", nullable = false)
    var jobRequestStatus: JobRequestStatus = JobRequestStatus.ACTIVE,

    @OneToOne(mappedBy = "jobRequest", cascade = [CascadeType.ALL], orphanRemoval = true)
    var payment: JobPayment? = null,

    @OneToOne(mappedBy = "jobRequest", cascade = [CascadeType.ALL], orphanRemoval = true)
    var customerReview: CustomerReview? = null,

    @OneToOne(mappedBy = "jobRequest", cascade = [CascadeType.ALL], orphanRemoval = true)
    var providerReview: ProviderReview? = null,

    @OneToOne(mappedBy = "jobRequest", cascade = [CascadeType.ALL], orphanRemoval = true)
    var schedule: Schedule? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDto() = JobRequestInfo(
        id = this.id!!,
        jobPostingInfo = this.jobPosting.toJobPostingInfo(),
        provider = this.provider.toDto(),
        schedule = schedule?.toDto(),
        customerReview = customerReview?.toDto(),
        providerReview = providerReview?.toDto(),
        jobRequestStatus = this.jobRequestStatus,
        jobPayment = payment?.toDto()
    )
}
