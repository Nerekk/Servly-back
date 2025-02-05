package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.entities.custom_fields.JobStatus
import org.locationtech.jts.geom.Point
import java.time.LocalDateTime

@Entity
@Table(name = "job_postings")
data class JobPosting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    val customer: Customer,

    @Column(nullable = false)
    val title: String,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    val category: Category,

    @Column(nullable = false)
    val city: String,

    @Column(nullable = false)
    val street: String,

    @Column(name = "house_number", nullable = true)
    val houseNumber: String? = null,

    @Column(nullable = true)
    val location: Point? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: JobStatus = JobStatus.ACTIVE,

    @OneToMany(mappedBy = "jobPosting", cascade = [CascadeType.ALL], orphanRemoval = true)
    val answers: MutableList<JobAnswer> = mutableListOf(),

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)