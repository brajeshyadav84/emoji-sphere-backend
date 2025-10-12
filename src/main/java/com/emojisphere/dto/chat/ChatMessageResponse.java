package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {

    private Long id;
    private Long conversationId;
    private Long senderId;
    private Long receiverId;
    private String messageText;
    private String messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Sender details
    private UserBasicInfo sender;
    private UserBasicInfo receiver;

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