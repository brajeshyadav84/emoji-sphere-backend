package com.emojisphere.repository;

import com.emojisphere.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Get messages for a conversation (paginated, ordered by creation time)
    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId ORDER BY m.createdAt ASC")
    Page<ChatMessage> findByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    // Get recent messages for a conversation
    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC")
    List<ChatMessage> findRecentMessagesByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    // Count unread messages for a user in a conversation
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.conversationId = :conversationId AND m.receiverId = :userId AND m.isRead = false")
    Long countUnreadMessagesInConversation(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    // Count all unread messages for a user
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.receiverId = :userId AND m.isRead = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);

    // Mark messages as read
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true, m.updatedAt = :readTime WHERE m.conversationId = :conversationId AND m.receiverId = :userId AND m.isRead = false")
    int markMessagesAsRead(@Param("conversationId") Long conversationId, @Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);

    // Get latest message for a conversation
    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId ORDER BY m.createdAt DESC")
    List<ChatMessage> findLatestMessageByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    // Find messages between two users
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) " +
           "ORDER BY m.createdAt DESC")
    Page<ChatMessage> findMessagesBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2, Pageable pageable);

    // Check if user is blocked from sending messages
    @Query("SELECT COUNT(b) > 0 FROM ChatUserBlocklist b WHERE b.blockerId = :receiverId AND b.blockedId = :senderId")
    boolean isUserBlockedFromSending(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    // Get messages after a specific time (for real-time updates)
    @Query("SELECT m FROM ChatMessage m WHERE m.conversationId = :conversationId AND m.createdAt > :afterTime ORDER BY m.createdAt ASC")
    List<ChatMessage> findMessagesAfterTime(@Param("conversationId") Long conversationId, @Param("afterTime") LocalDateTime afterTime);
}