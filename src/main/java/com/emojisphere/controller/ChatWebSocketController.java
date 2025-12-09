package com.emojisphere.controller;

import com.emojisphere.dto.chat.*;
import com.emojisphere.entity.ChatConversation;
import com.emojisphere.entity.ChatMessage;
import com.emojisphere.entity.User;
import com.emojisphere.repository.ChatConversationRepository;
import com.emojisphere.repository.ChatMessageRepository;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.List;

/**
 * WebSocket controller for real-time chat functionality
 * Handles STOMP message mappings for chat operations
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final ChatMessageRepository messageRepository;
    private final ChatConversationRepository conversationRepository;
    private final UserRepository userRepository;

    /**
     * Handle incoming chat messages via WebSocket
     * Destination: /app/chat.send
     * 
     * @param request The message request containing message details
     * @param headerAccessor Access to STOMP headers (for authentication)
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, 
                           SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Get sender ID from session attributes (set during WebSocket connection)
            Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
            
            if (senderId == null) {
                // Try to get from header
                String userIdHeader = headerAccessor.getFirstNativeHeader("userId");
                if (userIdHeader != null) {
                    senderId = Long.parseLong(userIdHeader);
                    headerAccessor.getSessionAttributes().put("userId", senderId);
                }
            }
            
            if (senderId == null) {
                log.error("Cannot send message: User not authenticated");
                return;
            }

            log.info("WebSocket: Sending message from user {} to user {}", senderId, request.getReceiverId());

            // Save message using ChatService
            ChatMessageResponse savedMessage = chatService.sendMessage(senderId, request);

            // Send to recipient's personal queue
            messagingTemplate.convertAndSendToUser(
                String.valueOf(request.getReceiverId()),
                "/queue/messages",
                savedMessage
            );

            // Also send to sender for confirmation
            messagingTemplate.convertAndSendToUser(
                String.valueOf(senderId),
                "/queue/messages",
                savedMessage
            );

            // Broadcast to conversation topic (for multiple devices)
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + savedMessage.getConversationId(),
                savedMessage
            );

            log.info("WebSocket: Message sent successfully, ID: {}", savedMessage.getId());

        } catch (Exception e) {
            log.error("WebSocket: Error sending message: {}", e.getMessage(), e);
            
            // Send error to sender
            Long senderId = (Long) headerAccessor.getSessionAttributes().get("userId");
            if (senderId != null) {
                messagingTemplate.convertAndSendToUser(
                    String.valueOf(senderId),
                    "/queue/errors",
                    "Failed to send message: " + e.getMessage()
                );
            }
        }
    }

    /**
     * Handle typing indicators via WebSocket
     * Destination: /app/chat.typing
     * 
     * @param typing The typing indicator containing userId, conversationId, and typing status
     */
    @MessageMapping("/chat.typing")
    public void sendTypingIndicator(@Payload TypingIndicator typing) {
        try {
            log.debug("WebSocket: Typing indicator - User {} in conversation {}: {}", 
                     typing.getUserId(), typing.getConversationId(), typing.getIsTyping());

            // Get conversation to find the other user
            ChatConversation conversation = conversationRepository.findById(typing.getConversationId())
                    .orElse(null);
            
            if (conversation == null) {
                log.warn("WebSocket: Conversation {} not found for typing indicator", typing.getConversationId());
                return;
            }

            // Determine recipient (the other user in the conversation)
            Long recipientId = conversation.getOtherUserId(typing.getUserId());

            // Send typing indicator to recipient's personal queue
            messagingTemplate.convertAndSendToUser(
                String.valueOf(recipientId),
                "/queue/typing",
                typing
            );

            // Also broadcast to conversation topic
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + typing.getConversationId() + "/typing",
                typing
            );

        } catch (Exception e) {
            log.error("WebSocket: Error sending typing indicator: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle read receipts via WebSocket
     * Destination: /app/chat.read
     * 
     * @param receipt The read receipt containing conversationId, userId, and message IDs
     */
    @MessageMapping("/chat.read")
    public void handleReadReceipt(@Payload MessageReadReceipt receipt) {
        try {
            log.info("WebSocket: Read receipt for conversation {} from user {}, messages: {}", 
                    receipt.getConversationId(), receipt.getUserId(), receipt.getMessageIds());

            // Mark messages as read in database
            if (receipt.getMessageIds() != null && !receipt.getMessageIds().isEmpty()) {
                for (Long messageId : receipt.getMessageIds()) {
                    messageRepository.findById(messageId).ifPresent(message -> {
                        if (message.getReceiverId().equals(receipt.getUserId()) && !message.getIsRead()) {
                            message.setIsRead(true);
                            message.setUpdatedAt(LocalDateTime.now());
                            messageRepository.save(message);
                            
                            log.debug("WebSocket: Marked message {} as read", messageId);
                        }
                    });
                }
            } else {
                // If no specific message IDs, mark all unread messages in conversation as read
                chatService.markMessagesAsRead(receipt.getUserId(), receipt.getConversationId());
            }

            // Get messages to notify senders
            List<ChatMessage> messages = messageRepository.findAllById(receipt.getMessageIds());
            
            // Send read receipts to each sender
            for (ChatMessage message : messages) {
                messagingTemplate.convertAndSendToUser(
                    String.valueOf(message.getSenderId()),
                    "/queue/read",
                    receipt
                );
            }

            // Broadcast to conversation topic
            messagingTemplate.convertAndSend(
                "/topic/conversation/" + receipt.getConversationId() + "/read",
                receipt
            );

            log.info("WebSocket: Read receipts sent successfully");

        } catch (Exception e) {
            log.error("WebSocket: Error handling read receipt: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle user presence updates (online/offline status)
     * Destination: /app/chat.presence
     * 
     * @param presence User presence information
     */
    @MessageMapping("/chat.presence")
    public void handlePresence(@Payload UserPresence presence) {
        try {
            log.info("WebSocket: Presence update for user {}: {}", 
                    presence.getUserId(), presence.getIsOnline());

            // Update user's online status in database
            userRepository.findById(presence.getUserId()).ifPresent(user -> {
                user.setIsOnline(presence.getIsOnline());
                user.setLastSeen(LocalDateTime.now());
                userRepository.save(user);
            });

            // Broadcast presence to all user's conversations
            // Note: This could be optimized by maintaining a list of user's friends
            messagingTemplate.convertAndSend(
                "/topic/presence/" + presence.getUserId(),
                presence
            );

        } catch (Exception e) {
            log.error("WebSocket: Error handling presence: {}", e.getMessage(), e);
        }
    }

    /**
     * Inner class for user presence information
     */
    public static class UserPresence {
        private Long userId;
        private Boolean isOnline;

        public UserPresence() {}

        public UserPresence(Long userId, Boolean isOnline) {
            this.userId = userId;
            this.isOnline = isOnline;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Boolean getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(Boolean isOnline) {
            this.isOnline = isOnline;
        }
    }
}
