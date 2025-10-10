package com.emojisphere.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostWithDetailsResponse {
    
    private Long postId;
    private Long userId;
    private String userName;
    private String gender;
    private String country;
    private String content;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer likeCount;
    private Integer commentCount;
    private List<CommentWithDetailsResponse> comments;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentWithDetailsResponse {
        private Long commentId;
        private String commentText;
        private String commentedBy;
        private LocalDateTime commentCreatedAt;
        private Integer likeCount;
        private List<ReplyWithDetailsResponse> replies;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyWithDetailsResponse {
        private Long replyId;
        private String replyText;
        private String repliedBy;
        private LocalDateTime replyCreatedAt;
    }
}