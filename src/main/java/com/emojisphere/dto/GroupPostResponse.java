package com.emojisphere.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class GroupPostResponse {
    private Long id;
    private String title;
    private String content;
    private String emojiContent;
    private String imageUrl;
    private Boolean isPublic;
    private Integer likesCount;
    private Integer commentsCount;
    private UserResponse author;
    private CategoryResponse category;
    private Set<TagResponse> tags;
    private Boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For detailed view with comments
    private List<CommentResponse> recentComments; // Latest 3 comments
    private Boolean hasMoreComments;
}
