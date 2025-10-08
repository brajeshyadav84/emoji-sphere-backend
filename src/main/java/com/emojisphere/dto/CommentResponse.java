package com.emojisphere.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentResponse {
    
    private Long id;
    private String commentText;
    private UserResponse user;
    private Long postId;
    private Long parentCommentId;
    private Integer likesCount;
    private Boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> replies; // For nested replies
}