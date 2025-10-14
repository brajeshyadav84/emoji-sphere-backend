package com.emojisphere.repository;

import com.emojisphere.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupLikeRepository extends JpaRepository<GroupLike, Long> {
    
    Optional<GroupLike> findByUserAndPost(User user, GroupPost post);
    
    Optional<GroupLike> findByUserAndComment(User user, GroupComment comment);
    
    @Query("SELECT COUNT(l) FROM GroupLike l WHERE l.post = :post")
    Long countByPost(@Param("post") GroupPost post);
    
    @Query("SELECT COUNT(l) FROM GroupLike l WHERE l.comment = :comment")
    Long countByComment(@Param("comment") GroupComment comment);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM GroupLike l WHERE l.user = :user AND l.post = :post")
    Boolean existsByUserAndPost(@Param("user") User user, @Param("post") GroupPost post);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM GroupLike l WHERE l.user = :user AND l.comment = :comment")
    Boolean existsByUserAndComment(@Param("user") User user, @Param("comment") GroupComment comment);
    
    void deleteByUserAndPost(User user, GroupPost post);
    
    void deleteByUserAndComment(User user, GroupComment comment);
}