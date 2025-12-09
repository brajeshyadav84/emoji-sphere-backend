package com.emojisphere.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;

/**
 * WebSocket event listener to handle connection and disconnection events
 * Manages user sessions and authentication
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    /**
     * Handle WebSocket connection events
     * Extract and store userId from connection headers
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        try {
            // Get the CONNECT message that was sent during connection
            GenericMessage<?> connectMessage = (GenericMessage<?>) headerAccessor.getHeader("simpConnectMessage");
            if (connectMessage != null) {
                StompHeaderAccessor connectHeaders = StompHeaderAccessor.wrap(connectMessage);
                
                // Extract userId from headers
                List<String> userIdHeaders = connectHeaders.getNativeHeader("userId");
                if (userIdHeaders != null && !userIdHeaders.isEmpty()) {
                    String userId = userIdHeaders.get(0);
                    
                    // Store userId in session attributes for later use
                    if (headerAccessor.getSessionAttributes() != null) {
                        headerAccessor.getSessionAttributes().put("userId", Long.parseLong(userId));
                        log.info("WebSocket Connected: User {} with session {}", userId, headerAccessor.getSessionId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket connection: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle WebSocket disconnection events
     * Clean up user session and update online status
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        try {
            Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
            
            if (userId != null) {
                log.info("WebSocket Disconnected: User {} with session {}", userId, headerAccessor.getSessionId());
                
                // TODO: Update user's online status to offline
                // This could be done by injecting UserRepository and updating the user's isOnline field
                // However, be careful about users with multiple connections (multiple tabs/devices)
            } else {
                log.debug("WebSocket Disconnected: Anonymous session {}", headerAccessor.getSessionId());
            }
        } catch (Exception e) {
            log.error("Error handling WebSocket disconnection: {}", e.getMessage(), e);
        }
    }
}
