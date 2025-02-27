package org.example.servlyback.features.schedule

import org.example.servlyback.dto.ScheduleInfo
import org.example.servlyback.entities.custom_fields.Role
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.YearMonth

@RestController
@RequestMapping("/api/schedule")
class ScheduleController(private val scheduleService: ScheduleService) {

    @GetMapping("/user")
    fun getSchedulesForUser(
        @RequestParam role: Role,
        @RequestParam yearMonth: YearMonth
    ): ResponseEntity<List<ScheduleInfo>> {
        return scheduleService.getSchedulesForUser(role, yearMonth)
    }

    @GetMapping("/job")
    fun getScheduleForJob(
        @RequestParam jobRequestId: Long
    ): ResponseEntity<ScheduleInfo?> {
        return scheduleService.getScheduleForJob(jobRequestId)
    }

    @PostMapping
    fun createScheduleForJob(
        @RequestBody scheduleInfo: ScheduleInfo
    ): ResponseEntity<ScheduleInfo> {
        return scheduleService.createScheduleForJob(scheduleInfo)
    }

    @PutMapping
    fun updateScheduleForJob(
        @RequestBody scheduleInfo: ScheduleInfo
    ): ResponseEntity<ScheduleInfo> {
        return scheduleService.updateScheduleForJob(scheduleInfo)
    }

    @PutMapping("/approve")
    fun approveScheduleAsCustomer(
        @RequestParam scheduleId: Long
    ): ResponseEntity<ScheduleInfo> {
        return scheduleService.approveScheduleAsCustomer(scheduleId)
    }

    @PutMapping("/reject")
    fun rejectScheduleAsCustomer(
        @RequestParam scheduleId: Long
    ): ResponseEntity<ScheduleInfo> {
        return scheduleService.rejectScheduleAsCustomer(scheduleId)
    }
}