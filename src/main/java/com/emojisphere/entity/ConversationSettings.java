package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_conversation_settings",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"conversation_id", "user_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "notifications_enabled", nullable = false)
    private Boolean notificationsEnabled = true;

    @Column(name = "archived", nullable = false)
    private Boolean archived = false;

    @Column(name = "muted_until")
    private LocalDateTime mutedUntil;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", insertable = false, updatable = false)
    private ChatConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public ConversationSettings(Long conversationId, Long userId) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.notificationsEnabled = true;
        this.archived = false;
    }

    // Helper methods
    public boolean isMuted() {
        return mutedUntil != null && mutedUntil.isAfter(LocalDateTime.now());
    }

    public boolean shouldReceiveNotifications() {
        return notificationsEnabled && !isMuted() && !archived;
    }
}