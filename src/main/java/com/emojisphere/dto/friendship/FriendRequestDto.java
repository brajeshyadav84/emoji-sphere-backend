package com.emojisphere.dto.friendship;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FriendRequestDto {
    
    @NotNull(message = "Target user ID is required")
    private Long targetUserId;
    
    // Explicit getter in case Lombok annotation processing isn't working
    public Long getTargetUserId() {
        return targetUserId;
    }
    
    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}