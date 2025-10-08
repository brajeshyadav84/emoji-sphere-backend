package com.emojisphere.dto.group;

import com.emojisphere.entity.GroupPrivacy;
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
    private GroupPrivacy privacy;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Creator information
    private Long createdById;
    private String createdByName;
    private String createdByMobile;
    
    // Group statistics
    private Long memberCount;
    private Long adminCount;
    
    // User's relationship to this group
    private Boolean isUserMember;
    private Boolean isUserAdmin;
}