package org.example.servlyback.dto

import java.time.LocalDateTime

data class ChatMessageInfo(
    val id: Long? = null,
    val jobRequestId: Long,
    val userUid: String,
    val content: String,
    val createdAt: LocalDateTime
)
