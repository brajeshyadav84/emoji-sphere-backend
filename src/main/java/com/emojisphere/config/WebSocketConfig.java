package com.emojisphere.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

/**
 * WebSocket configuration for real-time chat functionality
 * Uses SockJS for fallback support and STOMP protocol for messaging
 */
@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure message broker for pub/sub messaging
     * - /topic: for broadcasting to all subscribers
     * - /queue: for point-to-point messaging
     * - /user: for user-specific destinations
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple in-memory message broker
        config.enableSimpleBroker("/topic", "/queue", "/user");
        
        // Set application destination prefix for @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Register STOMP endpoints with SockJS fallback
     * Endpoint: /ws
     * Allowed origins: localhost development servers
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                    "http://localhost:8080",
                    "http://localhost:5173",
                    "http://localhost:3000"
                )
                .withSockJS();
    }

    /**
     * Configure client inbound channel with interceptor for authentication
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extract userId from connection headers
                    List<String> userIdHeaders = accessor.getNativeHeader("userId");
                    
                    if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
                        String userId = userIdHeaders.get(0);
                        
                        // Store userId in session attributes
                        accessor.getSessionAttributes().put("userId", Long.parseLong(userId));
                        
                        log.info("WebSocket CONNECT: User {} connecting with session {}", 
                                userId, accessor.getSessionId());
                    } else {
                        log.warn("WebSocket CONNECT: No userId provided in headers");
                    }
                }
                
                return message;
            }
        });
    }
}
