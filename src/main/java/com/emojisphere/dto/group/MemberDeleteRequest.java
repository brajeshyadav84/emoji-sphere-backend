package com.emojisphere.dto.group;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberDeleteRequest {
    
    @NotNull(message = "Group ID is required")
    private Long groupId;
    
    @NotNull(message = "User IDs list cannot be null")
    @NotEmpty(message = "At least one user ID is required")
    @Size(min = 1, message = "At least one user ID must be provided")
    private List<String> userIds;
}