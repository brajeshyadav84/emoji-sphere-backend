package com.emojisphere.dto.group;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupJoinRequestWithMessage {
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message; // Optional message to admin when requesting to join private group
}