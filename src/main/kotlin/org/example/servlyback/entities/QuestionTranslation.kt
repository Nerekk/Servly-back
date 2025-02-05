package org.example.servlyback.entities

import jakarta.persistence.*

@Entity
@Table(name = "question_translations")
data class QuestionTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    val question: Question,

    @Column(name = "language_code", nullable = false)
    val languageCode: String,

    @Column(name = "text", nullable = false)
    val text: String
)