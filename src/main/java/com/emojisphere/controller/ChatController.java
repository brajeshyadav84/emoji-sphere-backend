package com.emojisphere.controller;

import com.emojisphere.dto.ApiResponse;
import com.emojisphere.dto.chat.*;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.ChatService;
import com.emojisphere.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

/**
 * REST Controller for chat operations
 * Provides HTTP endpoints with optional WebSocket notification support
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
// @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Send a message to another user via REST API
     * Also sends WebSocket notification to recipient if connected
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            Long senderId = getCurrentUserId();
            ChatMessageResponse response = chatService.sendMessage(senderId, request);
            
            log.info("REST: Message sent successfully from user {} to user {}", senderId, request.getReceiverId());
            
            // Send WebSocket notification to recipient
            try {
                messagingTemplate.convertAndSendToUser(
                    String.valueOf(request.getReceiverId()),
                    "/queue/messages",
                    response
                );
                
                // Also broadcast to conversation topic
                messagingTemplate.convertAndSend(
                    "/topic/conversation/" + response.getConversationId(),
                    response
                );
                
                log.debug("WebSocket notification sent for message {}", response.getId());
            } catch (Exception wsError) {
                log.warn("Failed to send WebSocket notification: {}", wsError.getMessage());
                // Continue even if WebSocket notification fails
            }
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    /**
     * Get messages for a conversation
     */
    @GetMapping("/conversation/{conversationId}/messages")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            Long userId = getCurrentUserId();
            MessagesResponse response = chatService.getMessages(userId, conversationId, page, size);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            log.error("Error getting messages for conversation {}: {}", conversationId, e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get user's conversations
     */
    @GetMapping("/conversations")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getConversations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Long userId = getCurrentUserId();
            ConversationListResponse response = chatService.getConversations(userId, page, size);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            log.error("Error getting conversations for user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Mark messages as read in a conversation via REST API
     * Also sends read receipt via WebSocket to senders
     */
    @PostMapping("/conversation/{conversationId}/mark-read")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> markMessagesAsRead(@PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();
            chatService.markMessagesAsRead(userId, conversationId);
            
            // Send read receipt via WebSocket
            try {
                MessageReadReceipt receipt = new MessageReadReceipt(conversationId, userId, null);
                messagingTemplate.convertAndSend(
                    "/topic/conversation/" + conversationId + "/read",
                    receipt
                );
                log.debug("WebSocket read receipt sent for conversation {}", conversationId);
            } catch (Exception wsError) {
                log.warn("Failed to send WebSocket read receipt: {}", wsError.getMessage());
            }
            
            return ResponseEntity.ok(ApiResponse.successMessage("Messages marked as read"));
        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get unread message count
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUnreadMessageCount() {
        try {
            Long userId = getCurrentUserId();
            Long unreadCount = chatService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(ApiResponse.ok(Map.of("unreadCount", unreadCount)));
        } catch (Exception e) {
            log.error("Error getting unread message count: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Block a user
     */
    @PostMapping("/block/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> blockUser(@PathVariable Long userId) {
        try {
            Long blockerId = getCurrentUserId();
            chatService.blockUser(blockerId, userId);
            return ResponseEntity.ok(ApiResponse.successMessage("User blocked successfully"));
        } catch (Exception e) {
            log.error("Error blocking user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Unblock a user
     */
    @PostMapping("/unblock/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> unblockUser(@PathVariable Long userId) {
        try {
            Long blockerId = getCurrentUserId();
            chatService.unblockUser(blockerId, userId);
            return ResponseEntity.ok(ApiResponse.successMessage("User unblocked successfully"));
        } catch (Exception e) {
            log.error("Error unblocking user: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Start a conversation with a friend
     * Creates or retrieves existing conversation without sending a message
     */
    @PostMapping("/start/{friendId}")
    @PreAuthorize("hasRole('USER') or hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> startConversation(@PathVariable Long friendId) {
        try {
            Long userId = getCurrentUserId();
            
            // Get or create conversation
            ConversationResponse conversation = chatService.getOrCreateConversationResponse(userId, friendId);
            
            log.info("Conversation started/retrieved: {} between users {} and {}", 
                    conversation.getId(), userId, friendId);
            
            return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "conversationId", conversation.getId(),
                "conversation", conversation
            ), "Conversation ready"));
        } catch (Exception e) {
            log.error("Error starting conversation: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    /**
     * Get current user ID from Spring Security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        
        // Get the mobile number from the principal
        Long mobile = userDetails.getId(); // This is actually the mobile number
        
        // Look up the user by mobile number to get the actual user ID
        User user = userRepository.findById(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getId();
    }
}