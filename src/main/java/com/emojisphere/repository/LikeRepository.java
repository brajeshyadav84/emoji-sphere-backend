package com.emojisphere.repository;

import com.emojisphere.entity.Comment;
import com.emojisphere.entity.Like;
import com.emojisphere.entity.Post;
import com.emojisphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    
    Optional<Like> findByUserAndPost(User user, Post post);
    
    Optional<Like> findByUserAndComment(User user, Comment comment);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post = :post")
    Long countByPost(@Param("post") Post post);
    
    @Query("SELECT COUNT(l) FROM Like l WHERE l.comment = :comment")
    Long countByComment(@Param("comment") Comment comment);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user = :user AND l.post = :post")
    Boolean existsByUserAndPost(@Param("user") User user, @Param("post") Post post);
    
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.user = :user AND l.comment = :comment")
    Boolean existsByUserAndComment(@Param("user") User user, @Param("comment") Comment comment);
    
    void deleteByUserAndPost(User user, Post post);
    
    void deleteByUserAndComment(User user, Comment comment);
}