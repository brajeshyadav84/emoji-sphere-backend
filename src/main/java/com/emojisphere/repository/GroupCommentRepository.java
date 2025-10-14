package com.emojisphere.repository;


import com.emojisphere.entity.GroupComment;
import com.emojisphere.entity.GroupPost;
import com.emojisphere.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupCommentRepository extends JpaRepository<GroupComment, Long> {
    
    Page<GroupComment> findByPost(Post post, Pageable pageable);
    
    @Query("SELECT c FROM GroupComment c WHERE c.post = :post AND c.parentComment IS NULL")
    Page<GroupComment> findTopLevelCommentsByPost(@Param("post") GroupPost post, Pageable pageable);
    
    @Query("SELECT c FROM GroupComment c WHERE c.parentComment.id = :parentId")
    List<GroupComment> findRepliesByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM GroupComment c WHERE c.parentComment = :parentComment")
    List<GroupComment> findByParentComment(@Param("parentComment") GroupComment parentComment);
    
    @Query("SELECT COUNT(c) FROM GroupComment c WHERE c.post = :post")
    Long countByPost(@Param("post") GroupPost post);
}