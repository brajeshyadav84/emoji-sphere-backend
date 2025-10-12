package com.emojisphere.service;

import com.emojisphere.dto.chat.*;
import com.emojisphere.entity.*;
import com.emojisphere.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final ChatUserBlocklistRepository blocklistRepository;
    private final ConversationSettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    /**
     * Send a message to another user
     */
    public ChatMessageResponse sendMessage(Long senderId, SendMessageRequest request) {
        log.info("Sending message from user {} to user {}", senderId, request.getReceiverId());

        // Validate users exist and are active
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        if (!sender.getIsActive() || !receiver.getIsActive()) {
            throw new RuntimeException("One or both users are inactive");
        }

        // Check if users are friends
        if (!friendshipRepository.areFriends(senderId, request.getReceiverId())) {
            throw new RuntimeException("You can only send messages to your friends");
        }

        // Check if sender is blocked by receiver
        if (blocklistRepository.isUserBlocked(request.getReceiverId(), senderId)) {
            throw new RuntimeException("You cannot send messages to this user");
        }

        // Get or create conversation
        ChatConversation conversation = getOrCreateConversation(senderId, request.getReceiverId());

        // Create and save message
        ChatMessage.MessageType messageType;
        try {
            messageType = ChatMessage.MessageType.valueOf(request.getMessageType().toUpperCase());
        } catch (IllegalArgumentException e) {
            messageType = ChatMessage.MessageType.TEXT;
        }

        ChatMessage message = new ChatMessage(
                conversation.getId(),
                senderId,
                request.getReceiverId(),
                request.getMessageText(),
                messageType
        );

        ChatMessage savedMessage = messageRepository.save(message);
        
        // Update conversation's updated_at timestamp
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        log.info("Message sent successfully: ID {}", savedMessage.getId());
        return convertToMessageResponse(savedMessage);
    }

    /**
     * Get messages for a conversation
     */
    @Transactional(readOnly = true)
    public MessagesResponse getMessages(Long userId, Long conversationId, int page, int size) {
        log.info("Getting messages for conversation {} (user: {}, page: {}, size: {})", 
                conversationId, userId, page, size);

        // Verify user has access to this conversation
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.canUserAccess(userId)) {
            throw new RuntimeException("Access denied to this conversation");
        }

        // Get messages with pagination
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        Page<ChatMessage> messagesPage = messageRepository.findByConversationId(conversationId, pageable);

        List<ChatMessageResponse> messageResponses = messagesPage.getContent().stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());

        return new MessagesResponse(
                messageResponses,
                messagesPage.getNumber(),
                messagesPage.getTotalPages(),
                messagesPage.getTotalElements(),
                messagesPage.hasNext(),
                messagesPage.hasPrevious()
        );
    }

    /**
     * Get user's conversations
     */
    @Transactional(readOnly = true)
    public ConversationListResponse getConversations(Long userId, int page, int size) {
        log.info("Getting conversations for user {} (page: {}, size: {})", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatConversation> conversationsPage = conversationRepository.findUserConversations(userId, pageable);

        List<ConversationResponse> conversationResponses = conversationsPage.getContent().stream()
                .map(conversation -> convertToConversationResponse(conversation, userId))
                .collect(Collectors.toList());

        return new ConversationListResponse(
                conversationResponses,
                conversationsPage.getNumber(),
                conversationsPage.getTotalPages(),
                conversationsPage.getTotalElements(),
                conversationsPage.hasNext(),
                conversationsPage.hasPrevious()
        );
    }

    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(Long userId, Long conversationId) {
        log.info("Marking messages as read for user {} in conversation {}", userId, conversationId);

        // Verify user has access to this conversation
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!conversation.canUserAccess(userId)) {
            throw new RuntimeException("Access denied to this conversation");
        }

        int updatedCount = messageRepository.markMessagesAsRead(conversationId, userId, LocalDateTime.now());
        log.info("Marked {} messages as read", updatedCount);
    }

    /**
     * Get unread message count for user
     */
    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesForUser(userId);
    }

    /**
     * Block a user
     */
    public void blockUser(Long blockerId, Long blockedId) {
        log.info("User {} blocking user {}", blockerId, blockedId);

        if (blockerId.equals(blockedId)) {
            throw new RuntimeException("Cannot block yourself");
        }

        // Check if already blocked
        if (blocklistRepository.isUserBlocked(blockerId, blockedId)) {
            throw new RuntimeException("User is already blocked");
        }

        // Verify users exist
        if (!userRepository.existsById(blockerId) || !userRepository.existsById(blockedId)) {
            throw new RuntimeException("One or both users not found");
        }

        ChatUserBlocklist block = new ChatUserBlocklist(blockerId, blockedId);
        blocklistRepository.save(block);

        log.info("User {} successfully blocked user {}", blockerId, blockedId);
    }

    /**
     * Unblock a user
     */
    public void unblockUser(Long blockerId, Long blockedId) {
        log.info("User {} unblocking user {}", blockerId, blockedId);

        if (!blocklistRepository.isUserBlocked(blockerId, blockedId)) {
            throw new RuntimeException("User is not blocked");
        }

        blocklistRepository.deleteByBlockerIdAndBlockedId(blockerId, blockedId);
        log.info("User {} successfully unblocked user {}", blockerId, blockedId);
    }

    /**
     * Get or create conversation between two users
     */
    private ChatConversation getOrCreateConversation(Long userId1, Long userId2) {
        Optional<ChatConversation> existingConversation = conversationRepository.findByUserIds(userId1, userId2);
        
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        // Create new conversation with ordered user IDs
        ChatConversation newConversation = ChatConversation.createOrderedConversation(userId1, userId2);
        ChatConversation savedConversation = conversationRepository.save(newConversation);

        // Create default settings for both users
        ConversationSettings settings1 = new ConversationSettings(savedConversation.getId(), savedConversation.getUserOneId());
        ConversationSettings settings2 = new ConversationSettings(savedConversation.getId(), savedConversation.getUserTwoId());
        
        settingsRepository.save(settings1);
        settingsRepository.save(settings2);

        log.info("Created new conversation {} between users {} and {}", 
                savedConversation.getId(), userId1, userId2);
        
        return savedConversation;
    }

    /**
     * Convert ChatMessage to ChatMessageResponse
     */
    private ChatMessageResponse convertToMessageResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversationId());
        response.setSenderId(message.getSenderId());
        response.setReceiverId(message.getReceiverId());
        response.setMessageText(message.getMessageText());
        response.setMessageType(message.getMessageType().name());
        response.setIsRead(message.getIsRead());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());

        // Add user details if available
        if (message.getSender() != null) {
            response.setSender(convertToUserBasicInfo(message.getSender()));
        }
        if (message.getReceiver() != null) {
            response.setReceiver(convertToUserBasicInfo(message.getReceiver()));
        }

        return response;
    }

    /**
     * Convert ChatConversation to ConversationResponse
     */
    private ConversationResponse convertToConversationResponse(ChatConversation conversation, Long currentUserId) {
        ConversationResponse response = new ConversationResponse();
        response.setId(conversation.getId());
        response.setUserOneId(conversation.getUserOneId());
        response.setUserTwoId(conversation.getUserTwoId());
        response.setCreatedAt(conversation.getCreatedAt());
        response.setUpdatedAt(conversation.getUpdatedAt());

        // Set other user details
        Long otherUserId = conversation.getOtherUserId(currentUserId);
        response.setOtherUserId(otherUserId);

        // Load other user details
        userRepository.findById(otherUserId).ifPresent(otherUser -> {
            response.setOtherUser(convertToConversationUserBasicInfo(otherUser));
        });

        // Get unread count
        Long unreadCount = messageRepository.countUnreadMessagesInConversation(conversation.getId(), currentUserId);
        response.setUnreadCount(unreadCount.intValue());

        // Get last message
        Pageable lastMessagePageable = PageRequest.of(0, 1);
        List<ChatMessage> lastMessages = messageRepository.findLatestMessageByConversationId(
                conversation.getId(), lastMessagePageable);
        
        if (!lastMessages.isEmpty()) {
            ChatMessage lastMessage = lastMessages.get(0);
            response.setLastMessage(lastMessage.getMessageText());
            response.setLastMessageTime(lastMessage.getCreatedAt());
        }

        // Get settings
        settingsRepository.findByConversationIdAndUserId(conversation.getId(), currentUserId)
                .ifPresent(settings -> {
                    response.setNotificationsEnabled(settings.getNotificationsEnabled());
                    response.setArchived(settings.getArchived());
                    response.setMutedUntil(settings.getMutedUntil());
                });

        return response;
    }

    /**
     * Convert User to UserBasicInfo for ChatMessageResponse
     */
    private ChatMessageResponse.UserBasicInfo convertToUserBasicInfo(User user) {
        return new ChatMessageResponse.UserBasicInfo(
                user.getId(),
                user.getFullName(),
                user.getGender(),
                user.getIsActive()
        );
    }

    /**
     * Convert User to UserBasicInfo for ConversationResponse
     */
    private ConversationResponse.UserBasicInfo convertToConversationUserBasicInfo(User user) {
        return new ConversationResponse.UserBasicInfo(
                user.getId(),
                user.getFullName(),
                user.getGender(),
                user.getIsActive()
        );
    }
}