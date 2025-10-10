package com.emojisphere.dto.friendship;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FriendResponseDto {
    
    @NotNull(message = "Friendship ID is required")
    private Long friendshipId;
    
    @NotNull(message = "Response is required")
    @Pattern(regexp = "ACCEPTED|DECLINED|BLOCKED", message = "Response must be ACCEPTED, DECLINED, or BLOCKED")
    private String response;
}