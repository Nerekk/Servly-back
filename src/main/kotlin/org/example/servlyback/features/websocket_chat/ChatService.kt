package org.example.servlyback.features.websocket_chat

import org.example.servlyback.dto.ChatInfo
import org.example.servlyback.entities.ChatMessage
import org.example.servlyback.features._customer.CustomerRepository
import org.example.servlyback.features._provider.ProviderRepository
import org.example.servlyback.features.job_requests.JobRequestRepository
import org.example.servlyback.security.firebase.TokenManager
import org.example.servlyback.user.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository,
    private val userRepository: UserRepository,
    private val customerRepository: CustomerRepository,
    private val providerRepository: ProviderRepository,
    private val jobRequestRepository: JobRequestRepository
) {

    fun getChatDetails(jobRequestId: Long): ResponseEntity<ChatInfo> {
        val jobRequest = jobRequestRepository.findById(jobRequestId).getOrNull() ?: return ResponseEntity(HttpStatus.BAD_REQUEST)
        val firebaseToken = TokenManager.getFirebaseToken()
        val uid = firebaseToken.uid

        val customerBoolean = jobRequest.jobPosting.customer.user.uid == uid
        val providerBoolean = jobRequest.provider.user.uid == uid

        val secondUid = if (customerBoolean || providerBoolean) {
            if (customerBoolean) {
                jobRequest.provider.user.uid
            } else {
                jobRequest.jobPosting.customer.user.uid
            }
        } else {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        val secondName = if (customerBoolean) {
            jobRequest.provider.name
        } else {
            jobRequest.jobPosting.customer.name
        }

        return ResponseEntity.ok(
            ChatInfo(
                jobRequestId = jobRequestId,
                myUid = uid,
                secondUid = secondUid,
                secondName = secondName
            )
        )
    }

    @Transactional
    fun saveMessage(uid: String, jobRequestId: Long, content: String): ChatMessage {
        val jobRequest = jobRequestRepository.findById(jobRequestId).orElseThrow()
        val sender = userRepository.findByUid(uid) ?: throw Exception("User not found")

        val chatMessage = ChatMessage(
            user = sender,
            jobRequest = jobRequest,
            content = content,
            createdAt = LocalDateTime.now()
        )
        return chatMessageRepository.save(chatMessage)
    }
}