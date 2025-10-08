package com.emojisphere.repository;

import com.emojisphere.entity.Post;
import com.emojisphere.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByIsPublicTrue(Pageable pageable);
    
    Page<Post> findByUser(User user, Pageable pageable);
    
    Page<Post> findByUserAndIsPublicTrue(User user, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isPublic = true AND p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    Page<Post> searchPublicPosts(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.isPublic = true")
    Page<Post> findByCategory(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.id = :tagId AND p.isPublic = true")
    Page<Post> findByTag(@Param("tagId") Long tagId, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isPublic = true AND p.createdAt >= :startDate ORDER BY SIZE(p.likes) DESC")
    List<Post> findTrendingPosts(@Param("startDate") LocalDateTime startDate, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.isPublic = true ORDER BY p.createdAt DESC")
    Page<Post> findRecentPosts(Pageable pageable);
}