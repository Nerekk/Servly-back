package org.example.servlyback.features.schedule

import jakarta.persistence.EntityNotFoundException
import org.example.servlyback.dto.DateRange
import org.example.servlyback.dto.ScheduleInfo
import org.example.servlyback.dto.ScheduleSummary
import org.example.servlyback.entities.Schedule
import org.example.servlyback.entities.custom_fields.JobRequestStatus
import org.example.servlyback.entities.custom_fields.Role
import org.example.servlyback.entities.custom_fields.ScheduleStatus
import org.example.servlyback.features.job_requests.JobRequestRepository
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.security.firebase.handleJobRequestNotification
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

@Service
class ScheduleService(
    private val scheduleRepository: ScheduleRepository,
    private val jobRequestRepository: JobRequestRepository
) {
    fun getSchedulesForUser(role: Role, yearMonth: YearMonth): ResponseEntity<List<ScheduleInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val schedules = when (role) {
            Role.CUSTOMER -> scheduleRepository.findByJobRequestJobPostingCustomerUserUid(uid)
            Role.PROVIDER -> scheduleRepository.findByJobRequestProviderUserUid(uid)
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }.filter { schedule ->
            (schedule.startAt.year < yearMonth.year || (schedule.startAt.year == yearMonth.year && schedule.startAt.monthValue <= yearMonth.monthValue)) &&
                    (schedule.endAt.year > yearMonth.year || (schedule.endAt.year == yearMonth.year && schedule.endAt.monthValue >= yearMonth.monthValue))
        }

        return ResponseEntity(schedules.map { it.toDto() }, HttpStatus.OK)
    }

    fun getSchedulesSummaryForUser(role: Role, yearMonth: YearMonth): ResponseEntity<ScheduleSummary> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val schedules = when (role) {
            Role.CUSTOMER -> scheduleRepository.findByJobRequestJobPostingCustomerUserUid(uid)
            Role.PROVIDER -> scheduleRepository.findByJobRequestProviderUserUid(uid)
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }.filter { schedule ->
            (schedule.startAt.year < yearMonth.year || (schedule.startAt.year == yearMonth.year && schedule.startAt.monthValue <= yearMonth.monthValue)) &&
                    (schedule.endAt.year > yearMonth.year || (schedule.endAt.year == yearMonth.year && schedule.endAt.monthValue >= yearMonth.monthValue))
        }

        val dateRanges = schedules.map { schedule ->
            DateRange(schedule.startAt, schedule.endAt)
        }

        val response = ScheduleSummary(
            totalSchedules = schedules.size,
            dateRanges = dateRanges
        )

        return ResponseEntity(response, HttpStatus.OK)
    }

    fun getSchedulesForDay(role: Role, day: LocalDate): ResponseEntity<List<ScheduleInfo>> {
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val schedules = when (role) {
            Role.CUSTOMER -> scheduleRepository.findByJobRequestJobPostingCustomerUserUid(uid)
            Role.PROVIDER -> scheduleRepository.findByJobRequestProviderUserUid(uid)
            else -> return ResponseEntity(HttpStatus.BAD_REQUEST)
        }.filter { schedule ->
            (!day.isBefore(schedule.startAt) && !day.isAfter(schedule.endAt))
        }

        return ResponseEntity(schedules.map { it.toDto() }, HttpStatus.OK)
    }

    fun getScheduleForJob(jobRequestId: Long): ResponseEntity<ScheduleInfo?> {
        val schedule = scheduleRepository.findByJobRequestId(jobRequestId)

        return if (schedule != null) {
            ResponseEntity.ok(schedule.toDto())
        } else {
            ResponseEntity.status(HttpStatus.NO_CONTENT).body(null)
        }
    }

    @Transactional
    fun createScheduleForJob(scheduleInfo: ScheduleInfo): ResponseEntity<ScheduleInfo> {
        val jobRequest = jobRequestRepository.findById(scheduleInfo.jobRequestId).orElseThrow { RuntimeException("JobRequest not found") }

        if (jobRequest.jobRequestStatus != JobRequestStatus.IN_PROGRESS || scheduleRepository.existsByJobRequest(jobRequest))
            return ResponseEntity(HttpStatus.BAD_REQUEST)


        val schedule = Schedule(
            jobRequest = jobRequest,
            scheduleStatus = ScheduleStatus.WAITING_FOR_CUSTOMER_APPROVAL,
            startAt = scheduleInfo.updatedStartAt!!,
            endAt = scheduleInfo.updatedEndAt!!,
            title = scheduleInfo.updatedTitle!!,
            description = scheduleInfo.updatedDescription ?: "",
            price = scheduleInfo.updatedPrice ?: 0
        )


        val finalSchedule = scheduleRepository.save(schedule)

        handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "Usługodawca utworzył harmonogram pracy")

        return ResponseEntity(finalSchedule.toDto(), HttpStatus.OK)
    }

    @Transactional
    fun updateScheduleForJob(scheduleInfo: ScheduleInfo): ResponseEntity<ScheduleInfo> {
        val jobRequest = jobRequestRepository.findById(scheduleInfo.jobRequestId).orElseThrow { RuntimeException("JobRequest not found") }

        if (jobRequest.jobRequestStatus != JobRequestStatus.IN_PROGRESS) return ResponseEntity(HttpStatus.BAD_REQUEST)


        val schedule = scheduleRepository.findById(scheduleInfo.id!!).orElseThrow { RuntimeException("Schedule not found") }

        val updatedStatus = if (schedule.scheduleStatus == ScheduleStatus.REJECTED) {
            ScheduleStatus.WAITING_FOR_CUSTOMER_APPROVAL
        } else {
            ScheduleStatus.UPDATED_WAITING_FOR_CUSTOMER_APPROVAL
        }


        schedule.apply {
            this.scheduleStatus = updatedStatus
            this.updatedAt = LocalDateTime.now()

            this.updatedStartAt = scheduleInfo.updatedStartAt
            this.updatedEndAt = scheduleInfo.updatedEndAt
            this.updatedTitle = scheduleInfo.updatedTitle
            this.updatedDescription = scheduleInfo.updatedDescription
            this.updatedPrice = scheduleInfo.updatedPrice
        }

        val finalSchedule = scheduleRepository.save(schedule)

        handleJobRequestNotification(jobRequest.jobPosting.customer.user, jobRequest.jobPosting.title, "Usługodawca zaktualizował harmonogram pracy")

        return ResponseEntity(finalSchedule.toDto(), HttpStatus.OK)
    }

    @Transactional
    fun approveScheduleAsCustomer(scheduleId: Long): ResponseEntity<ScheduleInfo> {
        val schedule = scheduleRepository.findById(scheduleId).orElseThrow { EntityNotFoundException("Schedule not found with id $scheduleId") }
        val jobRequest = schedule.jobRequest

        handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "Klient zaakceptował harmonogram")

        return updateScheduleAsCustomer(scheduleId, ScheduleStatus.APPROVED)
    }

    @Transactional
    fun rejectScheduleAsCustomer(scheduleId: Long): ResponseEntity<ScheduleInfo> {
        val schedule = scheduleRepository.findById(scheduleId).orElseThrow { EntityNotFoundException("Schedule not found with id $scheduleId") }
        val jobRequest = schedule.jobRequest

        handleJobRequestNotification(jobRequest.provider.user, jobRequest.jobPosting.title, "Klient odrzucił harmonogram")

        return updateScheduleAsCustomer(scheduleId, ScheduleStatus.REJECTED)
    }

    private fun updateScheduleAsCustomer(scheduleId: Long, updatedStatus: ScheduleStatus): ResponseEntity<ScheduleInfo> {
        val schedule = scheduleRepository.findById(scheduleId).orElseThrow { RuntimeException("Schedule not found") }

        if (schedule.jobRequest.jobRequestStatus != JobRequestStatus.IN_PROGRESS) return ResponseEntity(HttpStatus.BAD_REQUEST)

        val updatedSchedule = when (updatedStatus) {
            ScheduleStatus.APPROVED -> {
                updateSchedule(schedule, updatedStatus)
                schedule
            }
            ScheduleStatus.REJECTED -> {
                when (schedule.scheduleStatus) {
                    ScheduleStatus.WAITING_FOR_CUSTOMER_APPROVAL -> {
                        updateSchedule(schedule, updatedStatus)
                        schedule
                    }
                    ScheduleStatus.UPDATED_WAITING_FOR_CUSTOMER_APPROVAL -> {
                        schedule.apply {
                            this.scheduleStatus = ScheduleStatus.APPROVED

                            this.updatedStartAt = null
                            this.updatedEndAt = null
                            this.updatedTitle = null
                            this.updatedDescription = null
                            this.updatedPrice = null
                        }
                        schedule
                    }
                    else -> { null }
                }
            }
            else -> { null }
        }

        if (updatedSchedule != null) {
            val savedSchedule = scheduleRepository.save(updatedSchedule)
            return ResponseEntity(savedSchedule.toDto(), HttpStatus.OK)
        } else {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    private fun updateSchedule(
        schedule: Schedule,
        updatedStatus: ScheduleStatus
    ) {
        schedule.apply {
            this.scheduleStatus = updatedStatus

            this.updatedStartAt?.let {
                this.startAt = it
            }
            this.updatedEndAt?.let {
                this.endAt = it
            }
            this.updatedTitle?.let {
                this.title = it
            }
            this.updatedDescription?.let {
                this.description = it
            }
            this.updatedPrice?.let {
                this.price = it
            }

            this.updatedStartAt = null
            this.updatedEndAt = null
            this.updatedTitle = null
            this.updatedDescription = null
            this.updatedPrice = null
        }
    }

}