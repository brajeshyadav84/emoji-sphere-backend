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
    private String status; // Changed from GroupRole to String to match entity
    private LocalDateTime joinedAt;
    private Boolean isActive;
    
    // User information
    private String userId; // Changed from Long to String to match mobile number
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String profilePicture;
    
    // Group information (for when showing user's groups)
    private Long groupId;
    private String groupName;
}