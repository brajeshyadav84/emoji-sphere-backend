package com.emojisphere.repository;

import com.emojisphere.entity.Group;
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
    List<Group> findByCreatedBy(String createdBy);
    
    // Find groups by privacy setting
    List<Group> findByPrivacy(String privacy);
    
    // Find groups by privacy with pagination
    Page<Group> findByPrivacy(String privacy, Pageable pageable);
    
    // Find groups by name and privacy
    Page<Group> findByNameContainingIgnoreCaseAndPrivacy(String name, String privacy, Pageable pageable);
    
    // Check if group name exists
    boolean existsByNameIgnoreCase(String name);
    
    // Find groups created by user
    List<Group> findByCreatedByOrderByCreatedAtDesc(String createdBy);
}