package com.emojisphere.controller;

import com.emojisphere.dto.GroupPostRequest;
import com.emojisphere.dto.GroupPostResponse;
import com.emojisphere.service.GroupPostService;
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

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/group-posts")
public class GroupPostController {

    @Autowired
    private GroupPostService groupPostService;

    @GetMapping
    public ResponseEntity<Page<GroupPostResponse>> getAllGroupPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir, Authentication authentication) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String currentMobile = authentication != null ? authentication.getName() : null;

        Page<GroupPostResponse> posts = groupPostService.getAllGroupPosts(pageable, currentMobile);
        return ResponseEntity.ok(posts);
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
}
