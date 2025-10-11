package com.emojisphere.repository;

import com.emojisphere.dto.friendship.FriendRequestResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

@Repository
public class FriendshipRepositoryImpl implements FriendshipRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FriendRequestResult sendFriendRequestUsingProcedure(Long requesterId, Long targetUserId) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("send_friend_request");
            
            query.registerStoredProcedureParameter("p_requester_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_target_user_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_result", String.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_friendship_id", Long.class, ParameterMode.OUT);
            
            query.setParameter("p_requester_id", requesterId);
            query.setParameter("p_target_user_id", targetUserId);
            
            query.execute();
            
            String result = (String) query.getOutputParameterValue("p_result");
            Long friendshipId = (Long) query.getOutputParameterValue("p_friendship_id");
            
            if (result != null && result.startsWith("SUCCESS")) {
                return FriendRequestResult.success(friendshipId);
            } else {
                return FriendRequestResult.error(result != null ? result : "Unknown error occurred");
            }
            
        } catch (Exception e) {
            return FriendRequestResult.error("ERROR: " + e.getMessage());
        }
    }
}