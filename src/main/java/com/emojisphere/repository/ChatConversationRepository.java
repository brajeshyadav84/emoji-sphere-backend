package com.emojisphere.repository;

import com.emojisphere.entity.ChatConversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    // Find conversation between two users (ordered)
    @Query("SELECT c FROM ChatConversation c WHERE " +
           "((c.userOneId = :userId1 AND c.userTwoId = :userId2) OR " +
           "(c.userOneId = :userId2 AND c.userTwoId = :userId1))")
    Optional<ChatConversation> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get all conversations for a user (ordered by last message)
    @Query("SELECT c FROM ChatConversation c " +
           "LEFT JOIN ChatMessage m ON c.id = m.conversationId " +
           "WHERE (c.userOneId = :userId OR c.userTwoId = :userId) " +
           "GROUP BY c.id, c.userOneId, c.userTwoId, c.createdAt, c.updatedAt " +
           "ORDER BY COALESCE(MAX(m.createdAt), c.createdAt) DESC")
    Page<ChatConversation> findUserConversations(@Param("userId") Long userId, Pageable pageable);

    // Check if conversation exists between two users
    @Query("SELECT COUNT(c) > 0 FROM ChatConversation c WHERE " +
           "((c.userOneId = :userId1 AND c.userTwoId = :userId2) OR " +
           "(c.userOneId = :userId2 AND c.userTwoId = :userId1))")
    boolean existsBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Find user's conversations with unread messages count
    @Query("SELECT c, " +
           "(SELECT COUNT(m) FROM ChatMessage m WHERE m.conversationId = c.id AND m.receiverId = :userId AND m.isRead = false) as unreadCount " +
           "FROM ChatConversation c " +
           "WHERE (c.userOneId = :userId OR c.userTwoId = :userId) " +
           "ORDER BY c.updatedAt DESC")
    Page<Object[]> findUserConversationsWithUnreadCount(@Param("userId") Long userId, Pageable pageable);
}