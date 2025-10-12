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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_chat_conversations",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_one_id", "user_two_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_one_id", nullable = false)
    private Long userOneId;

    @NotNull
    @Column(name = "user_two_id", nullable = false)
    private Long userTwoId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChatMessage> messages = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_one_id", insertable = false, updatable = false)
    private User userOne;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_two_id", insertable = false, updatable = false)
    private User userTwo;

    // Constructor for creating new conversation with ordered user IDs
    public ChatConversation(Long userId1, Long userId2) {
        if (userId1 < userId2) {
            this.userOneId = userId1;
            this.userTwoId = userId2;
        } else {
            this.userOneId = userId2;
            this.userTwoId = userId1;
        }
    }

    // Helper methods
    public boolean isUserInvolved(Long userId) {
        return userOneId.equals(userId) || userTwoId.equals(userId);
    }

    public Long getOtherUserId(Long userId) {
        if (userOneId.equals(userId)) {
            return userTwoId;
        } else if (userTwoId.equals(userId)) {
            return userOneId;
        }
        return null;
    }

    public boolean canUserAccess(Long userId) {
        return isUserInvolved(userId);
    }

    // Static helper method to create ordered conversation
    public static ChatConversation createOrderedConversation(Long userId1, Long userId2) {
        return new ChatConversation(userId1, userId2);
    }
}