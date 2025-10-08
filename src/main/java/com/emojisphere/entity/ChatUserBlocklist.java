package com.emojisphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_chat_user_blocklist")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserBlocklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blocker_id", nullable = false)
    private String blockerId;

    @Column(name = "blocked_id", nullable = false)
    private String blockedId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id", referencedColumnName = "mobile_number", insertable = false, updatable = false)
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id", referencedColumnName = "mobile_number", insertable = false, updatable = false)
    private User blocked;

    public ChatUserBlocklist(String blockerId, String blockedId) {
        this.blockerId = blockerId;
        this.blockedId = blockedId;
    }
}