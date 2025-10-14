package com.emojisphere.repository;

import com.emojisphere.entity.GroupMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    
    // Find members by group ID
    List<GroupMember> findByGroupId(Long groupId);
    
    // Find members by user ID
    List<GroupMember> findByUserId(Long userId);
    
    // Find member by group and user
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    
    // Find members by status
    List<GroupMember> findByGroupIdAndStatus(Long groupId, String status);
    
    // Find members with pagination
    Page<GroupMember> findByGroupId(Long groupId, Pageable pageable);
    
    // Check if user is member of group
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    
    // Check if user has specific status in group
    boolean existsByGroupIdAndUserIdAndStatus(Long groupId, Long userId, String status);
    
    // Count members in group
    long countByGroupId(Long groupId);
    
    // Count members by status
    long countByGroupIdAndStatus(Long groupId, String status);
    
    // Remove member (hard delete)
    @Modifying
    @Transactional
    void deleteByGroupIdAndUserId(Long groupId, Long userId);
}