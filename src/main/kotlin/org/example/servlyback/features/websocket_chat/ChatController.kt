package org.example.servlyback.features.websocket_chat

import org.example.servlyback.dto.ChatInfo
import org.example.servlyback.dto.ChatMessageInfo
import org.example.servlyback.util.SortType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatService: ChatService
) {
    @GetMapping("/history/{jobRequestId}")
    fun getChatHistory(
        @PathVariable jobRequestId: Long,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam sortType: SortType,
        @RequestParam(required = false) lastMessageTimestamp: String?
    ): Page<ChatMessageInfo> {
        val sortDirection = if (sortType == SortType.ASCENDING) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable: Pageable = PageRequest.of(0, size, Sort.by(sortDirection, "createdAt"))

        return if (lastMessageTimestamp != null) {
            val lastDate = LocalDateTime.parse(lastMessageTimestamp)
            chatMessageRepository.findByJobRequestIdAndCreatedAtBefore(jobRequestId, lastDate, pageable).map { it.toDto() }
        } else {
            chatMessageRepository.findByJobRequestIdAndCreatedAtBefore(jobRequestId, LocalDateTime.now(), pageable).map { it.toDto() }
        }
    }

    @GetMapping("/{jobRequestId}")
    fun getChatDetails(
        @PathVariable("jobRequestId") jobRequestId: Long
    ): ResponseEntity<ChatInfo> {
        return chatService.getChatDetails(jobRequestId)
    }
}