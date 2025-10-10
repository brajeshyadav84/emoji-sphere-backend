package com.emojisphere.controller;

import com.emojisphere.service.PostService;
import com.emojisphere.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> togglePostLike(
            @PathVariable Long postId,
            Authentication authentication) {
        
        boolean liked = postService.toggleLike(postId, authentication.getName());
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("message", liked ? "Post liked successfully" : "Post unliked successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleCommentLike(
            @PathVariable Long commentId,
            Authentication authentication) {
        
        boolean liked = commentService.toggleCommentLike(commentId, authentication.getName());
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("message", liked ? "Comment liked successfully" : "Comment unliked successfully");
        
        return ResponseEntity.ok(response);
    }
}