package org.example.servlyback.dto

import org.example.servlyback.entities.custom_fields.JobStatus

data class JobPostingInfo(
    val id: Long? = null,
    val customerId: Long? = null,
    val title: String,
    val categoryId: Long,
    val city: String,
    val street: String,
    val houseNumber: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val answers: List<JobAnswerInfo>,
    val status: JobStatus = JobStatus.ACTIVE
)