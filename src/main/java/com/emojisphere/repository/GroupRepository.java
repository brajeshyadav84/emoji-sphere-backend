package com.emojisphere.repository;

import com.emojisphere.entity.Group;
import com.emojisphere.entity.GroupPrivacy;
import com.emojisphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    
    // Find groups by name (case-insensitive)
    List<Group> findByNameContainingIgnoreCase(String name);
    
    // Find groups by creator
    List<Group> findByCreatedByOrderByCreatedAtDesc(User creator);
    
    // Find groups by privacy setting
    List<Group> findByPrivacy(GroupPrivacy privacy);
    
    // Find active groups
    List<Group> findByIsActiveTrue();
    
    // Find public groups for discovery
    @Query("SELECT g FROM Group g WHERE g.privacy = 'PUBLIC' AND g.isActive = true ORDER BY g.createdAt DESC")
    Page<Group> findPublicGroups(Pageable pageable);
    
    // Find popular public groups by member count
    @Query("SELECT g FROM Group g WHERE g.privacy = 'PUBLIC' AND g.isActive = true " +
           "ORDER BY (SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group = g AND gm.isActive = true) DESC")
    Page<Group> findPopularPublicGroups(Pageable pageable);
    
    // Search groups by name and description
    @Query("SELECT g FROM Group g WHERE g.isActive = true AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(g.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Group> searchGroups(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Check if group name exists
    boolean existsByNameIgnoreCase(String name);
    
    // Find groups where user is a member
    @Query("SELECT DISTINCT g FROM Group g " +
           "JOIN g.members gm " +
           "WHERE gm.user = :user AND gm.isActive = true AND g.isActive = true")
    List<Group> findGroupsByMember(@Param("user") User user);
    
    // Find groups created by user
    @Query("SELECT g FROM Group g WHERE g.createdBy = :user AND g.isActive = true ORDER BY g.createdAt DESC")
    List<Group> findGroupsCreatedByUser(@Param("user") User user);
    
    // Count members in a group
    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group = :group AND gm.isActive = true")
    Long countMembersByGroup(@Param("group") Group group);
}