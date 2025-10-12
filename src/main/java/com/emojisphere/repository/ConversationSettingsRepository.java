package com.emojisphere.repository;

import com.emojisphere.entity.ConversationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationSettingsRepository extends JpaRepository<ConversationSettings, Long> {

    // Find settings for a user in a specific conversation
    Optional<ConversationSettings> findByConversationIdAndUserId(Long conversationId, Long userId);

    // Check if conversation is archived for user
    @Query("SELECT cs.archived FROM ConversationSettings cs WHERE cs.conversationId = :conversationId AND cs.userId = :userId")
    Optional<Boolean> isConversationArchived(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    // Check if notifications are enabled for user in conversation
    @Query("SELECT cs.notificationsEnabled FROM ConversationSettings cs WHERE cs.conversationId = :conversationId AND cs.userId = :userId")
    Optional<Boolean> areNotificationsEnabled(@Param("conversationId") Long conversationId, @Param("userId") Long userId);
}