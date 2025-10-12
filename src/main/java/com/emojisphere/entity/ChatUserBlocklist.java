package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_chat_user_blocklist",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserBlocklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "blocker_id", nullable = false)
    private Long blockerId;

    @NotNull
    @Column(name = "blocked_id", nullable = false)
    private Long blockedId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", insertable = false, updatable = false)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", insertable = false, updatable = false)
    private User blocked;

    public ChatUserBlocklist(Long blockerId, Long blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }

    // Helper methods
    public boolean isBlocked(Long blockerUserId, Long blockedUserId) {
        return blockerId.equals(blockerUserId) && blockedId.equals(blockedUserId);
    }
}