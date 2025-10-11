package com.emojisphere.dto.friendship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendRequestResult {
    private String result;
    private Long friendshipId;
    private boolean success;
    
    public static FriendRequestResult success(Long friendshipId) {
        return new FriendRequestResult("SUCCESS: Friend request sent successfully", friendshipId, true);
    }
    
    public static FriendRequestResult error(String message) {
        return new FriendRequestResult(message, null, false);
    }
}