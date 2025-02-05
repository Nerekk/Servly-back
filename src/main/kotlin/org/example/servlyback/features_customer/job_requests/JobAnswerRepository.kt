package org.example.servlyback.features_customer.job_requests

import org.example.servlyback.entities.JobAnswer
import org.springframework.data.jpa.repository.JpaRepository

interface JobAnswerRepository : JpaRepository<JobAnswer, Long>