package com.emojisphere.repository;

import com.emojisphere.entity.ChatUserBlocklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatUserBlocklistRepository extends JpaRepository<ChatUserBlocklist, Long> {

    // Check if user is blocked
    @Query("SELECT COUNT(b) > 0 FROM ChatUserBlocklist b WHERE b.blockerId = :blockerId AND b.blockedId = :blockedId")
    boolean isUserBlocked(@Param("blockerId") Long blockerId, @Param("blockedId") Long blockedId);

    // Find block record
    Optional<ChatUserBlocklist> findByBlockerIdAndBlockedId(Long blockerId, Long blockedId);

    // Get all users blocked by a user
    @Query("SELECT b FROM ChatUserBlocklist b WHERE b.blockerId = :userId")
    List<ChatUserBlocklist> findBlockedUsers(@Param("userId") Long userId);

    // Get all users who blocked a user
    @Query("SELECT b FROM ChatUserBlocklist b WHERE b.blockedId = :userId")
    List<ChatUserBlocklist> findBlockingUsers(@Param("userId") Long userId);

    // Delete block relationship
    void deleteByBlockerIdAndBlockedId(Long blockerId, Long blockedId);
}