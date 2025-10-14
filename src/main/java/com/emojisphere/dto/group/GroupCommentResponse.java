package com.emojisphere.dto.group;

import com.emojisphere.dto.UserResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupCommentResponse {
    
    private Long id;
    private String commentText;
    private UserResponse user;
    private Long postId;
    private Long parentCommentId;
    private Integer likesCount;
    private Boolean isLikedByCurrentUser;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<GroupCommentResponse> replies; // For nested replies
}