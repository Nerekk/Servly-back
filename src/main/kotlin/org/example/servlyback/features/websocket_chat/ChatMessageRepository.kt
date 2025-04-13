package org.example.servlyback.features.websocket_chat

import org.example.servlyback.entities.ChatMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByJobRequestId(jobRequestId: Long, pageable: Pageable): Page<ChatMessage>
    fun findByJobRequestIdAndCreatedAtBefore(jobRequestId: Long, createdAt: LocalDateTime, pageable: Pageable): Page<ChatMessage>
}