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
@Table(name = "tbl_friendships",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user1_id", "user2_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @NotNull
    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendshipStatus status = FriendshipStatus.PENDING;

    @NotNull
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "responder_id")
    private Long responderId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // JPA relationships (fetch lazily to avoid performance issues)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", insertable = false, updatable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", insertable = false, updatable = false)
    private User user2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", insertable = false, updatable = false)
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", insertable = false, updatable = false)
    private User responder;

    // Enum for friendship status
    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        BLOCKED
    }

    // Constructor for creating new friendship request
    public Friendship(Long user1Id, Long user2Id, Long requesterId) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.requesterId = requesterId;
        this.status = FriendshipStatus.PENDING;
    }

    // Helper methods
    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isDeclined() {
        return status == FriendshipStatus.DECLINED;
    }

    public boolean isBlocked() {
        return status == FriendshipStatus.BLOCKED;
    }

    public boolean isUserInvolved(Long userId) {
        return user1Id.equals(userId) || user2Id.equals(userId);
    }

    public Long getOtherUserId(Long userId) {
        if (user1Id.equals(userId)) {
            return user2Id;
        } else if (user2Id.equals(userId)) {
            return user1Id;
        }
        return null;
    }

    public boolean canRespond(Long userId) {
        return isPending() && !requesterId.equals(userId) && isUserInvolved(userId);
    }

    // Static helper method to order user IDs consistently
    public static Friendship createOrderedFriendship(Long userId1, Long userId2, Long requesterId) {
        if (userId1 < userId2) {
            return new Friendship(userId1, userId2, requesterId);
        } else {
            return new Friendship(userId2, userId1, requesterId);
        }
    }
}