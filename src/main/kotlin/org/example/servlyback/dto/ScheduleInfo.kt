package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.ScheduleStatus
import java.time.LocalDate
import java.time.LocalDateTime

data class ScheduleInfo(
    val id: Long? = null,
    val jobRequestId: Long,
    val scheduleStatus: ScheduleStatus,
    val updatedAt: LocalDateTime? = null,

    val startAt: LocalDate,
    val endAt: LocalDate,
    val title: String,
    val description: String,
    val price: Int,

    val updatedStartAt: LocalDate? = null,
    val updatedEndAt: LocalDate? = null,
    val updatedTitle: String? = null,
    val updatedDescription: String? = null,
    val updatedPrice: Int? = null,
)