package com.emojisphere.dto.group;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    
    @NotEmpty(message = "At least one user ID is required")
    private List<Long> userIds;
}