package org.example.servlyback.features.websocket_chat.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.servlyback.features.job_requests.JobRequestRepository
import org.example.servlyback.features.websocket_chat.ChatService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder

@Component
class ChatWebSocketHandler(
    private val chatService: ChatService,
    private val jobRequestRepository: JobRequestRepository
) : TextWebSocketHandler() {

    private val jobRequestSessions = mutableMapOf<Long, MutableList<WebSocketSession>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val uid = session.attributes["uid"] as? String ?: return session.close(CloseStatus.BAD_DATA)

        val queryParams = UriComponentsBuilder.fromUriString(session.uri.toString()).build().queryParams
        val jobRequestId = queryParams["jobRequestId"]?.firstOrNull()?.toLongOrNull()

        if (jobRequestId == null) {
            println("WEBSOCKET afterConnectionEstablished: BAD DATA - JOBREQUESTID is NULL")
            session.close(CloseStatus.BAD_DATA)
            return
        }

        val jobRequest = jobRequestRepository.findById(jobRequestId).orElse(null)
        if (jobRequest == null || (jobRequest.provider.user.uid != uid && jobRequest.jobPosting.customer.user.uid != uid)) {
            println("WEBSOCKET afterConnectionEstablished: BAD DATA - JOBREQUEST")
            session.close(CloseStatus.BAD_DATA)
            return
        }

        session.attributes["jobRequestId"] = jobRequestId
        println("WEBSOCKET afterConnectionEstablished: computing session $session")
        synchronized(jobRequestSessions) {
            jobRequestSessions.computeIfAbsent(jobRequestId) { mutableListOf() }.add(session)
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        try {
            println("WEBSOCKET handleTextMessage: session $session")
            println("WEBSOCKET handleTextMessage: session.attributes ${session.attributes}")

            val uid = session.attributes["uid"] as? String ?: return
            val jobRequestId = session.attributes["jobRequestId"] as? Long ?: return

            val jsonNode = ObjectMapper().readTree(message.payload)
            val content = jsonNode["content"].asText()

            if (content.length > 1000) {
                session.sendMessage(TextMessage("{\"error\": \"Message too long\"}"))
                return
            }

            val chatMessage = chatService.saveMessage(uid, jobRequestId, content)

            val response = ObjectMapper().writeValueAsString(
                mapOf(
                    "userUid" to chatMessage.user.uid,
                    "content" to chatMessage.content,
                    "createdAt" to chatMessage.createdAt.toString()
                )
            )

            println("WEBSOCKET handleTextMessage: sending response $response")

            synchronized(jobRequestSessions) {
                jobRequestSessions[jobRequestId]?.forEach { it.sendMessage(TextMessage(response)) }
            }
        } catch (e: Exception) {
            session.sendMessage(TextMessage("{\"error\": \"Invalid message format\"}"))
            println(e.message)
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        println("WEBSOCKET afterConnectionClosed: session closing $session")

        val jobRequestId = session.attributes["jobRequestId"] as? Long ?: return
        synchronized(jobRequestSessions) {
            jobRequestSessions[jobRequestId]?.remove(session)
            if (jobRequestSessions[jobRequestId]?.isEmpty() == true) {
                jobRequestSessions.remove(jobRequestId)
            }
        }
    }
}

