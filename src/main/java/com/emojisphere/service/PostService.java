package com.emojisphere.service;

import com.emojisphere.dto.CommentResponse;
import com.emojisphere.dto.PostRequest;
import com.emojisphere.dto.PostResponse;
import com.emojisphere.entity.*;
import com.emojisphere.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ModelMapper modelMapper;

    public PostResponse createPost(PostRequest postRequest, String mobile) {
        User author = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUser(author);
        post.setContent(postRequest.getContent());
        post.setMediaUrl(postRequest.getImageUrl());
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost, mobile);
    }

    public Page<PostResponse> getAllPublicPosts(Pageable pageable, String currentMobile) {
        Page<Post> posts = postRepository.findByIsPublicTrue(pageable);
        return posts.map(post -> convertToResponse(post, currentMobile));
    }

    public PostResponse getPostById(Long id, String currentMobile) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return convertToResponse(post, currentMobile);
    }

    public PostResponse updatePost(Long id, PostRequest postRequest, String mobile) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is the author
        if (!post.getUser().getMobileNumber().equals(mobile)) {
            throw new RuntimeException("You can only update your own posts");
        }

        post.setContent(postRequest.getContent());
        post.setMediaUrl(postRequest.getImageUrl());

        Post updatedPost = postRepository.save(post);
        return convertToResponse(updatedPost, mobile);
    }

    public void deletePost(Long id, String mobile) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Check if user is the author
        if (!post.getUser().getMobileNumber().equals(mobile)) {
            throw new RuntimeException("You can only delete your own posts");
        }

        postRepository.delete(post);
    }

    public Page<PostResponse> getPostsByUser(String mobile, Pageable pageable, String currentMobile) {
        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Post> posts;
        if (mobile.equals(currentMobile)) {
            // Show all posts for the owner
            posts = postRepository.findByUser(user, pageable);
        } else {
            // Show only public posts for others
            posts = postRepository.findByUserAndIsPublicTrue(user, pageable);
        }

        return posts.map(post -> convertToResponse(post, currentMobile));
    }

    public Page<PostResponse> searchPosts(String keyword, Pageable pageable, String currentMobile) {
        Page<Post> posts = postRepository.searchPublicPosts(keyword, pageable);
        return posts.map(post -> convertToResponse(post, currentMobile));
    }

    public List<PostResponse> getTrendingPosts(String currentMobile) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Post> trendingPosts = postRepository.findTrendingPosts(oneWeekAgo, Pageable.ofSize(10));
        return trendingPosts.stream()
                .map(post -> convertToResponse(post, currentMobile))
                .collect(Collectors.toList());
    }

    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return false; // Unliked
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like);
            return true; // Liked
        }
    }

    private PostResponse convertToResponse(Post post, String currentMobile) {
        PostResponse response = modelMapper.map(post, PostResponse.class);
        
        // Check if current user liked this post
        if (currentMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentMobile).orElse(null);
            if (currentUser != null) {
                response.setIsLikedByCurrentUser(
                    likeRepository.existsByUserAndPost(currentUser, post)
                );
            }
        }
        
        return response;
    }

    public PostResponse getPostWithComments(Long id, String currentMobile) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        
        PostResponse response = convertToResponse(post, currentMobile);
        
        // Get recent comments (latest 3)
        Pageable commentPageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        Page<Comment> recentCommentsPage = commentRepository.findTopLevelCommentsByPost(post, commentPageable);
        
        List<CommentResponse> recentComments = recentCommentsPage.getContent().stream()
                .map(comment -> convertCommentToResponse(comment, currentMobile))
                .collect(Collectors.toList());
        
        response.setRecentComments(recentComments);
        response.setHasMoreComments(recentCommentsPage.getTotalElements() > 3);
        
        return response;
    }

    private CommentResponse convertCommentToResponse(Comment comment, String currentMobile) {
        CommentResponse commentResponse = modelMapper.map(comment, CommentResponse.class);
        
        // Set additional fields
        commentResponse.setPostId(comment.getPost().getId());
        if (comment.getParentComment() != null) {
            commentResponse.setParentCommentId(comment.getParentComment().getId());
        }
        
        // Check if current user liked this comment
        if (currentMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentMobile).orElse(null);
            if (currentUser != null) {
                commentResponse.setIsLikedByCurrentUser(
                    likeRepository.existsByUserAndComment(currentUser, comment)
                );
            }
        }
        
        return commentResponse;
    }
}