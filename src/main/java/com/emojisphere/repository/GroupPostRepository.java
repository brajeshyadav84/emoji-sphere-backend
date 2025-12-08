package com.emojisphere.repository;

import com.emojisphere.entity.GroupPost;
import com.emojisphere.entity.Post;
import com.emojisphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupPostRepository extends JpaRepository<GroupPost, Long> {

    Page<GroupPost> findByIsPublicTrue(Pageable pageable);

    Page<GroupPost> findByUser(User user, Pageable pageable);

    Page<GroupPost> findByUserAndIsPublicTrue(User user, Pageable pageable);

    @Query("SELECT p FROM GroupPost p WHERE p.isPublic = true AND p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<GroupPost> searchPublicPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM GroupPost p WHERE p.category.id = :categoryId AND p.isPublic = true")
    Page<GroupPost> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM GroupPost p JOIN p.tags t WHERE t.id = :tagId AND p.isPublic = true")
    Page<GroupPost> findByTag(@Param("tagId") Long tagId, Pageable pageable);

    @Query("SELECT p FROM GroupPost p WHERE p.isPublic = true AND p.createdAt >= :startDate ORDER BY p.likesCount DESC")
    List<GroupPost> findTrendingPosts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT p FROM GroupPost p WHERE p.isPublic = true ORDER BY p.createdAt DESC")
    Page<GroupPost> findRecentPosts(Pageable pageable);

    @Query(value = "CALL sp_get_group_posts_with_details_json2(:groupId, :offset, :limit)", nativeQuery = true)
    List<Object> getGroupPostsWithDetails(@Param("groupId") Long groupId, @Param("offset") int offset, @Param("limit") int limit);

    // Count total number of group posts
    long count();
    
    // Count total number of public group posts
    long countByIsPublicTrue();
    
    // Count total number of group posts by user
    long countByUser(User user);
    
    // Count total number of posts by group_id
    @Query("SELECT COUNT(p) FROM GroupPost p WHERE p.groupId = :groupId")
    long countByGroupId(@Param("groupId") Long groupId);
}