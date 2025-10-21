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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
// @CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    /**
     * Send a message to another user
     */
    @PostMapping("/send")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        try {
            Long senderId = getCurrentUserId();
            ChatMessageResponse response = chatService.sendMessage(senderId, request);
            
            log.info("Message sent successfully from user {} to user {}", senderId, request.getReceiverId());
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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
     * Mark messages as read in a conversation
     */
    @PostMapping("/conversation/{conversationId}/mark-read")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> markMessagesAsRead(@PathVariable Long conversationId) {
        try {
            Long userId = getCurrentUserId();
            chatService.markMessagesAsRead(userId, conversationId);
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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
     */
    @PostMapping("/start/{friendId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> startConversation(@PathVariable Long friendId) {
        try {
            Long userId = getCurrentUserId();
            
            // Send an initial message to create/get conversation
            SendMessageRequest initialMessage = new SendMessageRequest();
            initialMessage.setReceiverId(friendId);
            // initialMessage.setMessageText("👋 Hi there!");
            initialMessage.setMessageType("EMOJI");
            
            ChatMessageResponse response = chatService.sendMessage(userId, initialMessage);
            
            return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "conversationId", response.getConversationId(),
                "initialMessage", response
            ), "Conversation started"));
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