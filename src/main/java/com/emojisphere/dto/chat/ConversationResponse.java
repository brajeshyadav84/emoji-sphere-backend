package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {

    private Long id;
    private Long userOneId;
    private Long userTwoId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Other user details (for current user context)
    private Long otherUserId;
    private UserBasicInfo otherUser;

    // Conversation metadata
    private Integer unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageTime;

    // Settings
    private Boolean notificationsEnabled;
    private Boolean archived;
    private LocalDateTime mutedUntil;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo {
        private Long id;
        private String fullName;
        private String gender;
        private Boolean isActive;
    }
}