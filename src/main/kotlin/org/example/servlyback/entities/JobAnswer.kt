package org.example.servlyback.entities

import jakarta.persistence.*

@Entity
@Table(name = "job_answers")
data class JobAnswer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "job_request_id", nullable = false)
    val jobPosting: JobPosting,

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @Column(nullable = false, columnDefinition = "TEXT")
    val answerText: String
)