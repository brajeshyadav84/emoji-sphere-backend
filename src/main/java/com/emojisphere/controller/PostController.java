package com.emojisphere.controller;

import com.emojisphere.dto.CreatePostRequest;
import com.emojisphere.dto.PostRequest;
import com.emojisphere.dto.PostResponse;
import com.emojisphere.service.PostService;
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
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        String currentMobile = authentication != null ? authentication.getName() : null;
        Page<PostResponse> posts = postService.getAllPublicPosts(pageable, currentMobile);
        
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id, Authentication authentication) {
        String currentMobile = authentication != null ? authentication.getName() : null;
        PostResponse post = postService.getPostById(id, currentMobile);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{id}/with-comments")
    public ResponseEntity<PostResponse> getPostWithComments(@PathVariable Long id, Authentication authentication) {
        String currentMobile = authentication != null ? authentication.getName() : null;
        PostResponse post = postService.getPostWithComments(id, currentMobile);
        return ResponseEntity.ok(post);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest, Authentication authentication) {
        String currentMobile = authentication != null ? authentication.getName() : null;
        PostResponse post = postService.createPost(postRequest, currentMobile);
        return ResponseEntity.ok(post);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id, 
                                                   @Valid @RequestBody PostRequest postRequest,
                                                   Authentication authentication) {
        PostResponse post = postService.updatePost(id, postRequest, authentication.getName());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication authentication) {
        postService.deletePost(id, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{mobile}")
    public ResponseEntity<Page<PostResponse>> getPostsByUser(
            @PathVariable String mobile,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String currentMobile = authentication != null ? authentication.getName() : null;
        Page<PostResponse> posts = postService.getPostsByUser(mobile, pageable, currentMobile);
        
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PostResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        String currentMobile = authentication != null ? authentication.getName() : null;
        Page<PostResponse> posts = postService.searchPosts(keyword, pageable, currentMobile);
        
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<PostResponse>> getTrendingPosts(Authentication authentication) {
        String currentMobile = authentication != null ? authentication.getName() : null;
        List<PostResponse> posts = postService.getTrendingPosts(currentMobile);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/share")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<PostResponse> sharePost(@Valid @RequestBody CreatePostRequest createPostRequest, 
                                                  Authentication authentication) {
        // Convert CreatePostRequest to PostRequest for backward compatibility
        PostRequest postRequest = new PostRequest();
        postRequest.setTitle(""); // Social posts might not have titles
        postRequest.setContent(createPostRequest.getContent());
        postRequest.setEmojiContent(createPostRequest.getEmojiContent());
        postRequest.setImageUrl(createPostRequest.getImageUrl());
        postRequest.setIsPublic(createPostRequest.getIsPublic());
        postRequest.setCategoryId(createPostRequest.getCategoryId());
        postRequest.setTags(createPostRequest.getTags());
        
        PostResponse post = postService.createPost(postRequest, authentication.getName());
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> toggleLike(@PathVariable Long postId, Authentication authentication) {
        String userMobile = authentication.getName();
        boolean liked = postService.toggleLike(postId, userMobile);
        return ResponseEntity.ok().body(liked ? "liked" : "unliked");
    }
}