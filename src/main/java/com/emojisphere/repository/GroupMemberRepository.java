package com.emojisphere.repository;

import com.emojisphere.entity.Group;
import com.emojisphere.entity.GroupMember;
import com.emojisphere.entity.GroupRole;
import com.emojisphere.entity.User;
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
    
    // Find member by group and user
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
    
    // Find active member by group and user
    Optional<GroupMember> findByGroupAndUserAndIsActiveTrue(Group group, User user);
    
    // Find all active members of a group
    List<GroupMember> findByGroupAndIsActiveTrueOrderByJoinedAt(Group group);
    
    // Find members with pagination
    Page<GroupMember> findByGroupAndIsActiveTrue(Group group, Pageable pageable);
    
    // Find members by role
    List<GroupMember> findByGroupAndRoleAndIsActiveTrue(Group group, GroupRole role);
    
    // Search members by user details
    @Query("SELECT gm FROM GroupMember gm " +
           "WHERE gm.group = :group AND gm.isActive = true AND " +
           "(LOWER(gm.user.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(gm.user.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "gm.user.mobileNumber LIKE CONCAT('%', :searchTerm, '%'))")
    Page<GroupMember> searchMembersByUserDetails(@Param("group") Group group, 
                                                 @Param("searchTerm") String searchTerm, 
                                                 Pageable pageable);
    
    // Check if user is member of group
    boolean existsByGroupAndUserAndIsActiveTrue(Group group, User user);
    
    // Check if user is admin of group
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END " +
           "FROM GroupMember gm " +
           "WHERE gm.group = :group AND gm.user = :user AND gm.role = 'ADMIN' AND gm.isActive = true")
    boolean isUserAdminOfGroup(@Param("group") Group group, @Param("user") User user);
    
    // Count active members in group
    long countByGroupAndIsActiveTrue(Group group);
    
    // Count admins in group
    long countByGroupAndRoleAndIsActiveTrue(Group group, GroupRole role);
    
    // Remove member (soft delete)
    @Modifying
    @Transactional
    @Query("UPDATE GroupMember gm SET gm.isActive = false WHERE gm.group = :group AND gm.user = :user")
    int removeMemberFromGroup(@Param("group") Group group, @Param("user") User user);
    
    // Remove multiple members (soft delete)
    @Modifying
    @Transactional
    @Query("UPDATE GroupMember gm SET gm.isActive = false WHERE gm.group = :group AND gm.user.id IN :userIds")
    int removeMembersFromGroup(@Param("group") Group group, @Param("userIds") List<Long> userIds);
    
    // Get all groups where user is member
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user = :user AND gm.isActive = true")
    List<GroupMember> findActiveGroupMembershipsByUser(@Param("user") User user);
    
    // Find admins of a group
    @Query("SELECT gm FROM GroupMember gm WHERE gm.group = :group AND gm.role = 'ADMIN' AND gm.isActive = true")
    List<GroupMember> findAdminsByGroup(@Param("group") Group group);
}