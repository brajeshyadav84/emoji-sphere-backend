package com.emojisphere.dto.group;

import com.emojisphere.entity.GroupRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberResponse {
    
    private Long id;
    private GroupRole role;
    private LocalDateTime joinedAt;
    private Boolean isActive;
    
    // User information
    private Long userId;
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String profilePicture;
    
    // Group information (for when showing user's groups)
    private Long groupId;
    private String groupName;
}