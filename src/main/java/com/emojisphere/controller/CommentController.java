package com.emojisphere.controller;

import com.emojisphere.dto.CommentRequest;
import com.emojisphere.dto.CommentResponse;
import com.emojisphere.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        String currentUserMobile = authentication != null ? authentication.getName() : null;
        Page<CommentResponse> comments = commentService.getCommentsByPost(postId, pageable, currentUserMobile);
        
        return ResponseEntity.ok(comments);
    }

    @PostMapping("/posts/{postId}/comments")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        
        CommentResponse comment = commentService.createComment(postId, commentRequest, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        
        CommentResponse comment = commentService.updateComment(commentId, commentRequest, authentication.getName());
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comments/{parentCommentId}/replies")
    public ResponseEntity<List<CommentResponse>> getReplies(
            @PathVariable Long parentCommentId,
            Authentication authentication) {
        
        String currentUserMobile = authentication != null ? authentication.getName() : null;
        List<CommentResponse> replies = commentService.getReplies(parentCommentId, currentUserMobile);
        
        return ResponseEntity.ok(replies);
    }

    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> toggleCommentLike(
            @PathVariable Long commentId,
            Authentication authentication) {
        
        boolean liked = commentService.toggleCommentLike(commentId, authentication.getName());
        return ResponseEntity.ok().body(liked ? "liked" : "unliked");
    }
}