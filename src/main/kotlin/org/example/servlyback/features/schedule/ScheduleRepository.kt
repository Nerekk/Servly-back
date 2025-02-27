package org.example.servlyback.features.schedule

import org.example.servlyback.entities.JobRequest
import org.example.servlyback.entities.Schedule
import org.example.servlyback.entities.custom_fields.ScheduleStatus
import org.springframework.data.jpa.repository.JpaRepository

interface ScheduleRepository: JpaRepository<Schedule, Long> {
    fun findByJobRequestJobPostingCustomerUserUid(uid: String): List<Schedule>
    fun findByJobRequestProviderUserUid(uid: String): List<Schedule>
    fun findByJobRequestId(jobRequestId: Long): Schedule?
    fun existsByJobRequestAndScheduleStatusIn(jobRequest: JobRequest, statuses: List<ScheduleStatus>): Boolean
    fun existsByJobRequest(jobRequest: JobRequest): Boolean
    fun findByJobRequestIdAndScheduleStatusIn(jobRequestId: Long, statuses: List<ScheduleStatus>): Schedule?
    fun findByJobRequestIdAndScheduleStatus(jobRequestId: Long, status: ScheduleStatus): Schedule?
    fun countByJobRequestId(jobRequestId: Long): Long
}