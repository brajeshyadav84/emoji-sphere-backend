package com.emojisphere.repository;

import com.emojisphere.dto.friendship.FriendRequestResult;

public interface FriendshipRepositoryCustom {
    FriendRequestResult sendFriendRequestUsingProcedure(Long requesterId, Long targetUserId);
}