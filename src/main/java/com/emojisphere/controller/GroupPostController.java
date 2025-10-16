package com.emojisphere.controller;

import com.emojisphere.dto.*;
import com.emojisphere.service.GroupPostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/group-posts")
public class GroupPostController {

    @Autowired
    private GroupPostService groupPostService;

    @GetMapping("/group/{groupId}")
    public ResponseEntity<Page<GroupPostResponse>> getAllGroupPosts(
        @PathVariable("groupId") Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir, Authentication authentication) {

        Page<PostWithDetailsResponse> detailedPosts = groupPostService.getGroupPostsWithDetails(groupId,
                PageRequest.of(page, size)
        );

        // Convert PostWithDetailsResponse to PostResponse for compatibility
        List<GroupPostResponse> posts = detailedPosts.getContent().stream()
                .map(this::convertDetailedToSimplePost)
                .collect(java.util.stream.Collectors.toList());

        Page<GroupPostResponse> result = new PageImpl<>(posts,
                PageRequest.of(page, size), detailedPosts.getTotalElements());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupPostResponse> getGroupPostById(@PathVariable Long id) {
        GroupPostResponse post = groupPostService.getGroupPostById(id);
        return ResponseEntity.ok(post);
    }

    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<GroupPostResponse> createGroupPost(@Valid @RequestBody GroupPostRequest request, Authentication authentication) {
        String currentMobile = authentication != null ? authentication.getName() : null;
        GroupPostResponse post = groupPostService.createGroupPost(request, currentMobile);
        return ResponseEntity.ok(post);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<GroupPostResponse> updateGroupPost(@PathVariable Long id,
                                                   @Valid @RequestBody GroupPostRequest request,
                                                   Authentication authentication) {
        Long userId = Long.valueOf(authentication.getName());
        GroupPostResponse post = groupPostService.updateGroupPost(id, request, userId);
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteGroupPost(@PathVariable Long id, Authentication authentication) {
        groupPostService.deleteGroupPost(id, authentication.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> groupToggleLike(@PathVariable Long postId, Authentication authentication) {
        String userMobile = authentication.getName();
        boolean liked = groupPostService.toggleGroupLike(postId, userMobile);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("status", liked ? "liked" : "unliked");
        response.put("message", liked ? "Group post liked successfully" : "Group post unliked successfully");

        return ResponseEntity.ok(response);
    }

    private GroupPostResponse convertDetailedToSimplePost(PostWithDetailsResponse detailed) {
        GroupPostResponse simple = new GroupPostResponse();
        simple.setId(detailed.getPostId());
        simple.setContent(detailed.getContent());
        simple.setImageUrl(detailed.getMediaUrl());
        simple.setCreatedAt(detailed.getCreatedAt());
        simple.setUpdatedAt(detailed.getUpdatedAt());
        simple.setLikesCount(detailed.getLikeCount());
        simple.setCommentsCount(detailed.getCommentCount());
        simple.setIsPublic(true); // Since stored procedure only returns public posts

        // Create a basic UserResponse from available data
        UserResponse author = new UserResponse();
        author.setId(String.valueOf(detailed.getUserId()));
        author.setFullName(detailed.getUserName());
        author.setGender(detailed.getGender());
        author.setCountry(detailed.getCountry());
        simple.setAuthor(author);

        // Set default values for fields not available in stored procedure
        simple.setIsLikedByCurrentUser(false);
        simple.setHasMoreComments(detailed.getComments().size() > 3);

        return simple;
    }
}
