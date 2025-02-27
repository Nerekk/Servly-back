package org.example.servlyback.entities

import jakarta.persistence.*
import org.example.servlyback.dto.ChatMessageInfo
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
data class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_uid", nullable = false)
    val user: User,

    @ManyToOne
    @JoinColumn(name = "job_request_id", nullable = false)
    val jobRequest: JobRequest,

    @Column(nullable = false)
    val content: String,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDto(): ChatMessageInfo {
        return ChatMessageInfo(
            id = id,
            jobRequestId = jobRequest.id!!,
            userUid = user.uid,
            content = content,
            createdAt = createdAt
        )
    }
}