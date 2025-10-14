package com.emojisphere.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_group_likes",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"user_id", "post_id"}),
           @UniqueConstraint(columnNames = {"user_id", "comment_id"})
       })
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "comment_id")
    private Long commentId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private GroupPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", insertable = false, updatable = false)
    private GroupComment comment;

    public GroupLike(User user, GroupPost post) {
        this.userId = user.getId();
        this.postId = post.getId();
        this.user = user;
        this.post = post;
    }

    public GroupLike(User user, GroupComment comment) {
        this.userId = user.getId();
        this.commentId = comment.getId();
        this.user = user;
        this.comment = comment;
    }
}