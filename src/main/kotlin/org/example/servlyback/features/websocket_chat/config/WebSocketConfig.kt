package org.example.servlyback.features.websocket_chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@Configuration
@EnableWebSocket
class WebSocketConfig(
    private val firebaseWebSocketInterceptor: FirebaseWebSocketInterceptor,
    private val chatWebSocketHandler: ChatWebSocketHandler
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
            .setAllowedOrigins("*")
            .addInterceptors(firebaseWebSocketInterceptor)
    }
}
