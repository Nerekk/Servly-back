package org.example.servlyback.features.job_postings

import org.example.servlyback.entities.JobAnswer
import org.springframework.data.jpa.repository.JpaRepository

interface JobAnswerRepository : JpaRepository<JobAnswer, Long>