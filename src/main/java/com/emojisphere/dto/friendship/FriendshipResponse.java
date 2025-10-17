package com.emojisphere.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {
    
    private Long id;
    private Long user1Id;
    private Long user2Id;
    private String status;
    private Long requesterId;
    private Long responderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime respondedAt;
    
    // User details
    private UserBasicInfo user1;
    private UserBasicInfo user2;
    private UserBasicInfo requester;
    private UserBasicInfo responder;
    
    // Helper fields
    private Long otherUserId; // The other user in the friendship (for current user context)
    private UserBasicInfo otherUser;
    private boolean canRespond;
    private boolean isSentByCurrentUser;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserBasicInfo {
        private Long id;
        private String fullName;
        private String email;
        private String mobileNumber;
        private String country;
        private String schoolName;
        private Integer age;
        private String dob;
        private String gender;
        private Boolean isActive;

        // Online status fields
        private Boolean isOnline;
        private LocalDateTime lastSeen;
        private String onlineStatus;
    }
}