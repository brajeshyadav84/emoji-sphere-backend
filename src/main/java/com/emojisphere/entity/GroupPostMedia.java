package com.emojisphere.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_group_post_media")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupPostMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_id", nullable = false)
    private Long postId;

    @NotBlank
    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @NotBlank
    @Column(name = "media_type", nullable = false)
    private String mediaType; // IMAGE, VIDEO, AUDIO, DOCUMENT

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private GroupPost post;

    // Constructor
    public GroupPostMedia(Long postId, String mediaUrl, String mediaType) {
        this.postId = postId;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
    }
}
