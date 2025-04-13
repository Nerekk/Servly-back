package org.example.servlyback.dto

data class ScheduleSummary(
    val totalSchedules: Int,
    val dateRanges: List<DateRange>
)
