package org.example.servlyback.entities

import jakarta.persistence.*

@Entity
@Table(name = "category_translations")
data class CategoryTranslation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,

    @Column(name = "language_code", nullable = false)
    val languageCode: String,

    @Column(name = "name", nullable = false)
    val name: String
)