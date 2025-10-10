package com.emojisphere.repository;

import com.emojisphere.entity.Comment;
import com.emojisphere.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByPost(Post post, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.post = :post AND c.parentComment IS NULL")
    Page<Comment> findTopLevelCommentsByPost(@Param("post") Post post, Pageable pageable);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :parentId")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Comment c WHERE c.parentComment = :parentComment")
    List<Comment> findByParentComment(@Param("parentComment") Comment parentComment);
    
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    Long countByPost(@Param("post") Post post);
}