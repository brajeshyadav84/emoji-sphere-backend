package com.emojisphere.controller;

import com.emojisphere.dto.CommentRequest;
import com.emojisphere.dto.CommentResponse;
import com.emojisphere.dto.group.GroupCommentResponse;
import com.emojisphere.service.CommentService;
import com.emojisphere.service.GroupCommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import com.emojisphere.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/group-posts")
public class GroupCommentController {

    @Autowired
    private GroupCommentService commentService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<ApiResponse<Object>> getCommentsByPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
        String currentUserMobile = authentication != null ? authentication.getName() : null;
        Page<GroupCommentResponse> comments = commentService.getCommentsByPost(postId, pageable, currentUserMobile);
        
        return ResponseEntity.ok(ApiResponse.ok(comments));
    }

    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {
        
        GroupCommentResponse comment = commentService.createComment(postId, commentRequest, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(comment));
    }

    @PutMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest,
            Authentication authentication) {

        GroupCommentResponse comment = commentService.updateComment(commentId, commentRequest, authentication.getName());
        return ResponseEntity.ok(ApiResponse.ok(comment));
    }

    @DeleteMapping("/comments/{commentId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        
        commentService.deleteComment(commentId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.successMessage("Comment deleted successfully"));
    }

    @GetMapping("/comments/{parentCommentId}/replies")
    public ResponseEntity<ApiResponse<Object>> getReplies(
            @PathVariable Long parentCommentId,
            Authentication authentication) {
        
        String currentUserMobile = authentication != null ? authentication.getName() : null;
        List<GroupCommentResponse> replies = commentService.getReplies(parentCommentId, currentUserMobile);
        
        return ResponseEntity.ok(ApiResponse.ok(replies));
    }

    @PostMapping("/comments/{commentId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> toggleCommentLike(
            @PathVariable Long commentId,
            Authentication authentication) {
        
        boolean liked = commentService.toggleCommentLike(commentId, authentication.getName());
        
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("status", liked ? "liked" : "unliked");
        response.put("message", liked ? "Comment liked successfully" : "Comment unliked successfully");
        
        return ResponseEntity.ok(ApiResponse.ok(response, (String) response.get("message")));
    }
}