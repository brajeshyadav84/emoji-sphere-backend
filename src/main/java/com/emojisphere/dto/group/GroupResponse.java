package com.emojisphere.dto.group;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupResponse {
    
    private Long id;
    private String name;
    private String description;
    private String privacy; // Changed from enum to String for consistency
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Creator information
    private String createdById; // Changed from Long to String for mobile number
    private String createdByName;
    private String createdByMobile;
    
    // Group statistics
    private Long memberCount;
    private Long adminCount;
    
    // User's relationship to this group
    private Boolean isUserMember;
    private Boolean isUserAdmin;
}