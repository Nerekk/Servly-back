package org.example.servlyback.features.websocket_chat.config

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class FirebaseWebSocketInterceptor : HandshakeInterceptor {
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val authHeader = request.headers["Authorization"]?.firstOrNull()
        if (authHeader?.startsWith("Bearer ") == true) {
            val token = authHeader.substring(7)
            try {
                val decodedToken: FirebaseToken = FirebaseAuth.getInstance().verifyIdToken(token)
                attributes["uid"] = decodedToken.uid
                return true
            } catch (e: Exception) {
                println("Firebase Auth websocket error: ${e.message}")
                return false
            }
        }
        return false
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {}
}