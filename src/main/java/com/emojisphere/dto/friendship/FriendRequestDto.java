package com.emojisphere.dto.friendship;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendRequestDto {
    
    @NotNull(message = "Target user ID is required")
    private Long targetUserId;
}