package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.ScheduleInfo
import org.example.servlyback.entities.custom_fields.ScheduleStatus
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "schedules")
data class Schedule(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "job_request_id", nullable = false, unique = true)
    val jobRequest: JobRequest,

    @Column(nullable = false)
    var startAt: LocalDate,
    @Column
    var updatedStartAt: LocalDate? = null,

    @Column(nullable = false)
    var endAt: LocalDate,
    @Column
    var updatedEndAt: LocalDate? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    var title: String,
    @Column(columnDefinition = "TEXT")
    var updatedTitle: String? = null,

    @Column(columnDefinition = "TEXT")
    var description: String = "",
    @Column(columnDefinition = "TEXT")
    var updatedDescription: String? = null,

    @Column
    var price: Int = 0,
    @Column
    var updatedPrice: Int? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = false)
    var scheduleStatus: ScheduleStatus = ScheduleStatus.WAITING_FOR_CUSTOMER_APPROVAL,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDto(): ScheduleInfo {
        return ScheduleInfo(
            id = id,
            jobRequestId = jobRequest.id!!,
            scheduleStatus = scheduleStatus,
            updatedAt = updatedAt,

            startAt = startAt,
            endAt = endAt,
            title = title,
            description = description,
            price = price,

            updatedStartAt = updatedStartAt,
            updatedEndAt = updatedEndAt,
            updatedTitle = updatedTitle,
            updatedDescription = updatedDescription,
            updatedPrice = updatedPrice
        )
    }
}