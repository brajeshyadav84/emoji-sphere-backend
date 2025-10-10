package com.emojisphere.repository;

import com.emojisphere.entity.Friendship;
import com.emojisphere.entity.Friendship.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    // Check if friendship exists between two users
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user1Id = :userId1 AND f.user2Id = :userId2) OR " +
           "(f.user1Id = :userId2 AND f.user2Id = :userId1)")
    Optional<Friendship> findFriendshipBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get all friendships for a user with specific status
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user1Id = :userId OR f.user2Id = :userId) AND f.status = :status")
    Page<Friendship> findFriendshipsByUserAndStatus(@Param("userId") Long userId, 
                                                    @Param("status") FriendshipStatus status, 
                                                    Pageable pageable);

    // Get all friendships for a user (any status)
    @Query("SELECT f FROM Friendship f WHERE f.user1Id = :userId OR f.user2Id = :userId")
    Page<Friendship> findAllFriendshipsByUser(@Param("userId") Long userId, Pageable pageable);

    // Get pending friend requests sent by a user
    @Query("SELECT f FROM Friendship f WHERE f.requesterId = :userId AND f.status = 'PENDING'")
    Page<Friendship> findPendingRequestsSentByUser(@Param("userId") Long userId, Pageable pageable);

    // Get pending friend requests received by a user
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user1Id = :userId OR f.user2Id = :userId) AND " +
           "f.requesterId != :userId AND f.status = 'PENDING'")
    Page<Friendship> findPendingRequestsReceivedByUser(@Param("userId") Long userId, Pageable pageable);

    // Get all accepted friendships for a user (friends list)
    @Query("SELECT f FROM Friendship f WHERE " +
           "(f.user1Id = :userId OR f.user2Id = :userId) AND f.status = 'ACCEPTED'")
    Page<Friendship> findAcceptedFriendships(@Param("userId") Long userId, Pageable pageable);

    // Get count of friends for a user
    @Query("SELECT COUNT(f) FROM Friendship f WHERE " +
           "(f.user1Id = :userId OR f.user2Id = :userId) AND f.status = 'ACCEPTED'")
    Long countFriendsByUser(@Param("userId") Long userId);

    // Get count of pending requests for a user
    @Query("SELECT COUNT(f) FROM Friendship f WHERE " +
           "(f.user1Id = :userId OR f.user2Id = :userId) AND " +
           "f.requesterId != :userId AND f.status = 'PENDING'")
    Long countPendingRequestsByUser(@Param("userId") Long userId);

    // Check if users are friends (accepted status)
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
           "((f.user1Id = :userId1 AND f.user2Id = :userId2) OR " +
           "(f.user1Id = :userId2 AND f.user2Id = :userId1)) AND " +
           "f.status = 'ACCEPTED'")
    boolean areFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Check if friendship exists (any status)
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
           "(f.user1Id = :userId1 AND f.user2Id = :userId2) OR " +
           "(f.user1Id = :userId2 AND f.user2Id = :userId1)")
    boolean friendshipExists(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Get mutual friends between two users
    @Query("SELECT DISTINCT f1.user1Id, f1.user2Id FROM Friendship f1 " +
           "JOIN Friendship f2 ON " +
           "((f1.user1Id = f2.user1Id OR f1.user1Id = f2.user2Id OR f1.user2Id = f2.user1Id OR f1.user2Id = f2.user2Id) AND " +
           "(f1.user1Id = :userId1 OR f1.user2Id = :userId1) AND " +
           "(f2.user1Id = :userId2 OR f2.user2Id = :userId2) AND " +
           "f1.user1Id != :userId1 AND f1.user2Id != :userId1 AND " +
           "f2.user1Id != :userId2 AND f2.user2Id != :userId2 AND " +
           "f1.status = 'ACCEPTED' AND f2.status = 'ACCEPTED')")
    List<Object[]> findMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Find users who might know each other (have mutual friends)
    @Query("SELECT f2.user1Id, f2.user2Id, COUNT(*) as mutualCount FROM Friendship f1 " +
           "JOIN Friendship f2 ON " +
           "((f1.user1Id = f2.user1Id OR f1.user1Id = f2.user2Id OR f1.user2Id = f2.user1Id OR f1.user2Id = f2.user2Id) AND " +
           "f1.id != f2.id) " +
           "WHERE (f1.user1Id = :userId OR f1.user2Id = :userId) AND " +
           "f1.status = 'ACCEPTED' AND f2.status = 'ACCEPTED' AND " +
           "f2.user1Id != :userId AND f2.user2Id != :userId " +
           "GROUP BY f2.user1Id, f2.user2Id " +
           "ORDER BY mutualCount DESC")
    List<Object[]> findFriendSuggestions(@Param("userId") Long userId, Pageable pageable);

    // Delete friendship (for blocking or removing friends)
    @Query("DELETE FROM Friendship f WHERE " +
           "((f.user1Id = :userId1 AND f.user2Id = :userId2) OR " +
           "(f.user1Id = :userId2 AND f.user2Id = :userId1))")
    void deleteFriendshipBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
}