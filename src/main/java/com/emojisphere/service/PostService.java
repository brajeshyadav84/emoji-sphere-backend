package com.emojisphere.service;

import com.emojisphere.dto.CommentResponse;
import com.emojisphere.dto.PostRequest;
import com.emojisphere.dto.PostResponse;
import com.emojisphere.dto.PostWithDetailsResponse;
import com.emojisphere.dto.UserResponse;
import com.emojisphere.dto.CategoryResponse;
import com.emojisphere.dto.TagResponse;
import com.emojisphere.entity.*;
import com.emojisphere.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    private final ObjectMapper objectMapper;

    public PostService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public PostResponse createPost(PostRequest postRequest, String mobile) {
        User author = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Post post = new Post();
        post.setUserId(author.getId());
        post.setUser(author);
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setMediaUrl(postRequest.getImageUrl());
        post.setIsPublic(postRequest.getIsPublic() != null ? postRequest.getIsPublic() : true);
        post.setCategoryId(postRequest.getCategoryId());
        post.setLikesCount(0L);
        
        // Handle tags if provided
        if (postRequest.getTags() != null && !postRequest.getTags().isEmpty()) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : postRequest.getTags()) {
                Tag tag = tagRepository.findByName(tagName.toLowerCase())
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName.toLowerCase());
                        return tagRepository.save(newTag);
                    });
                tags.add(tag);
            }
            post.setTags(tags);
        }
        
        Post savedPost = postRepository.save(post);
        return convertToResponse(savedPost, mobile);
    }

    public Page<PostResponse> getAllPublicPosts(Pageable pageable, String currentMobile) {
        Page<Post> posts = postRepository.findByIsPublicTrue(pageable);
        return posts.map(post -> convertToResponse(post, currentMobile));
    }

    /**
     * Get posts with detailed information using stored procedure
     * This includes complete comments, replies, and like counts in a single optimized query
     */
    public Page<PostWithDetailsResponse> getPostsWithDetails(Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();
        
        try {
            System.out.println("Calling stored procedure with offset=" + offset + ", limit=" + limit);
            List<Object> results = postRepository.getPostsWithDetailsJson(offset, limit);
            
            if (results == null || results.isEmpty()) {
                System.out.println("No results returned from stored procedure, falling back to regular query");
                return getPostsWithDetailsRegular(pageable);
            }
            
            // The stored procedure returns a single row with JSON array
            String jsonResult = (String) results.get(0);
            
            System.out.println("Raw JSON result: " + (jsonResult != null ? jsonResult.substring(0, Math.min(200, jsonResult.length())) + "..." : "null"));
            
            if (jsonResult == null || jsonResult.trim().isEmpty()) {
                System.out.println("JSON result is null or empty, falling back to regular query");
                return getPostsWithDetailsRegular(pageable);
            }
            
            // Parse JSON array - the stored procedure returns JSON_ARRAYAGG result
            JsonNode postsArray = objectMapper.readTree(jsonResult);
            
            if (postsArray == null || !postsArray.isArray()) {
                System.out.println("JSON is not an array or is null. Type: " + (postsArray != null ? postsArray.getNodeType() : "null"));
                return getPostsWithDetailsRegular(pageable);
            }
            
            List<PostWithDetailsResponse> posts = new ArrayList<>();

            for (JsonNode postNode : postsArray) {
                try {
                    PostWithDetailsResponse post = parsePostFromJson(postNode);
                    posts.add(post);
                } catch (Exception e) {
                    System.err.println("Error parsing individual post: " + e.getMessage());
                    e.printStackTrace();
                    // Continue with other posts
                }
            }

            // Sort posts by createdAt descending to guarantee correct order
            posts.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

            System.out.println("Successfully parsed " + posts.size() + " posts");

            // For simplicity, we'll use the returned list size as total.
            // In a real scenario, you might want to call another stored procedure to get the total count
            long total = postRepository.count(); // posts.size() + offset; // This is an approximation

            return new PageImpl<>(posts, pageable, total);
            
        } catch (JsonProcessingException e) {
            System.err.println("JSON Processing error: " + e.getMessage());
            e.printStackTrace();
            return getPostsWithDetailsRegular(pageable);
        } catch (Exception e) {
            System.err.println("General error in getPostsWithDetails: " + e.getMessage());
            e.printStackTrace();
            return getPostsWithDetailsRegular(pageable);
        }
    }
    
    /**
     * Fallback method to get posts with details using regular JPA queries
     */
    private Page<PostWithDetailsResponse> getPostsWithDetailsRegular(Pageable pageable) {
        System.out.println("Using fallback regular query method");
        
        // Get posts
        Page<Post> posts = postRepository.findByIsPublicTrue(pageable);
        
        List<PostWithDetailsResponse> postResponses = posts.getContent().stream()
            .map(this::convertPostToDetailedResponse)
            .collect(Collectors.toList());
        
        return new PageImpl<>(postResponses, pageable, posts.getTotalElements());
    }
    
    /**
     * Convert Post entity to PostWithDetailsResponse using regular queries
     */
    private PostWithDetailsResponse convertPostToDetailedResponse(Post post) {
        PostWithDetailsResponse response = new PostWithDetailsResponse();
        
        response.setPostId(post.getId());
        response.setUserId(post.getUserId());
        response.setContent(post.getContent());
        response.setMediaUrl(post.getMediaUrl());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Set user details
        if (post.getUser() != null) {
            response.setUserName(post.getUser().getFullName());
            response.setGender(post.getUser().getGender());
            response.setCountry(post.getUser().getCountry());
        } else {
            response.setUserName("Unknown User");
            response.setGender("Unknown");
            response.setCountry("Unknown");
        }
        
        // Set counts
        response.setLikeCount(post.getLikesCount() != null ? post.getLikesCount().intValue() : 0);
        
        // Get top-level comments
        List<Comment> topLevelComments = commentRepository.findTopLevelCommentsByPost(
            post, PageRequest.of(0, 10, Sort.by("createdAt").ascending())
        ).getContent();
        
        response.setCommentCount(topLevelComments.size());
        
        // Convert comments
        List<PostWithDetailsResponse.CommentWithDetailsResponse> commentResponses = 
            topLevelComments.stream()
                .map(this::convertCommentToDetailedResponse)
                .collect(Collectors.toList());
        
        response.setComments(commentResponses);
        
        return response;
    }
    
    /**
     * Convert Comment entity to CommentWithDetailsResponse
     */
    private PostWithDetailsResponse.CommentWithDetailsResponse convertCommentToDetailedResponse(Comment comment) {
        PostWithDetailsResponse.CommentWithDetailsResponse response = 
            new PostWithDetailsResponse.CommentWithDetailsResponse();
        
        response.setCommentId(comment.getId());
        response.setCommentText(comment.getCommentText());
        response.setCommentCreatedAt(comment.getCreatedAt());
        
        if (comment.getUser() != null) {
            response.setCommentedBy(comment.getUser().getFullName());
        } else {
            response.setCommentedBy("Unknown User");
        }
        
        // Set like count (approximate)
        response.setLikeCount(0); // You can implement this if needed
        
        // Get replies
        List<Comment> replies = commentRepository.findByParentComment(comment);
        List<PostWithDetailsResponse.ReplyWithDetailsResponse> replyResponses = 
            replies.stream()
                .map(this::convertReplyToDetailedResponse)
                .collect(Collectors.toList());
        
        response.setReplies(replyResponses);
        
        return response;
    }
    
    /**
     * Convert reply Comment entity to ReplyWithDetailsResponse
     */
    private PostWithDetailsResponse.ReplyWithDetailsResponse convertReplyToDetailedResponse(Comment reply) {
        PostWithDetailsResponse.ReplyWithDetailsResponse response = 
            new PostWithDetailsResponse.ReplyWithDetailsResponse();
        
        response.setReplyId(reply.getId());
        response.setReplyText(reply.getCommentText());
        response.setReplyCreatedAt(reply.getCreatedAt());
        
        if (reply.getUser() != null) {
            response.setRepliedBy(reply.getUser().getFullName());
        } else {
            response.setRepliedBy("Unknown User");
        }
        
        return response;
    }
    
    private PostWithDetailsResponse parsePostFromJson(JsonNode postNode) {
        PostWithDetailsResponse post = new PostWithDetailsResponse();
        
        post.setPostId(postNode.get("post_id").asLong());
        post.setUserId(postNode.get("user_id").asLong());
        post.setUserName(postNode.get("user_name").asText());
        post.setGender(postNode.get("gender").asText());
        post.setCountry(postNode.get("country").asText());
        post.setContent(postNode.get("content").asText());
        
        JsonNode mediaUrlNode = postNode.get("media_url");
        if (mediaUrlNode != null && !mediaUrlNode.isNull()) {
            post.setMediaUrl(mediaUrlNode.asText());
        }
        
        // Parse timestamps - handle different possible formats
        post.setCreatedAt(parseDateTime(postNode.get("created_at").asText()));
        post.setUpdatedAt(parseDateTime(postNode.get("updated_at").asText()));
        
        post.setLikeCount(postNode.get("like_count").asInt());
        post.setCommentCount(postNode.get("comment_count").asInt());
        
        // Parse comments
        JsonNode commentsNode = postNode.get("comments");
        if (commentsNode != null && commentsNode.isArray()) {
            List<PostWithDetailsResponse.CommentWithDetailsResponse> comments = new ArrayList<>();
            
            for (JsonNode commentNode : commentsNode) {
                PostWithDetailsResponse.CommentWithDetailsResponse comment = parseCommentFromJson(commentNode);
                comments.add(comment);
            }
            
            post.setComments(comments);
        } else {
            post.setComments(new ArrayList<>());
        }
        
        return post;
    }
    
    private PostWithDetailsResponse.CommentWithDetailsResponse parseCommentFromJson(JsonNode commentNode) {
        PostWithDetailsResponse.CommentWithDetailsResponse comment = 
            new PostWithDetailsResponse.CommentWithDetailsResponse();
        
        comment.setCommentId(commentNode.get("comment_id").asLong());
        comment.setCommentText(commentNode.get("comment_text").asText());
        comment.setCommentedBy(commentNode.get("commented_by").asText());
        comment.setCommentCreatedAt(parseDateTime(commentNode.get("comment_created_at").asText()));
        comment.setLikeCount(commentNode.get("like_count").asInt());
        
        // Parse replies
        JsonNode repliesNode = commentNode.get("replies");
        if (repliesNode != null && repliesNode.isArray()) {
            List<PostWithDetailsResponse.ReplyWithDetailsResponse> replies = new ArrayList<>();
            
            for (JsonNode replyNode : repliesNode) {
                PostWithDetailsResponse.ReplyWithDetailsResponse reply = parseReplyFromJson(replyNode);
                replies.add(reply);
            }
            
            comment.setReplies(replies);
        } else {
            comment.setReplies(new ArrayList<>());
        }
        
        return comment;
    }
    
    private PostWithDetailsResponse.ReplyWithDetailsResponse parseReplyFromJson(JsonNode replyNode) {
        PostWithDetailsResponse.ReplyWithDetailsResponse reply = 
            new PostWithDetailsResponse.ReplyWithDetailsResponse();
        
        reply.setReplyId(replyNode.get("reply_id").asLong());
        reply.setReplyText(replyNode.get("reply_text").asText());
        reply.setRepliedBy(replyNode.get("replied_by").asText());
        reply.setReplyCreatedAt(parseDateTime(replyNode.get("reply_created_at").asText()));
        
        return reply;
    }
    
    private LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return LocalDateTime.now();
        }
        
        try {
            // Try different date formats
            if (dateTimeStr.contains("T")) {
                // ISO format: 2023-10-10T10:30:00
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } else {
                // MySQL format: 2023-10-10 10:30:00
                return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        } catch (Exception e) {
            // Fallback to current time if parsing fails
            System.err.println("Failed to parse datetime: " + dateTimeStr + ", using current time. Error: " + e.getMessage());
            return LocalDateTime.now();
        }
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

    public Page<PostResponse> getPostsByUserId(Long userId, Pageable pageable, String currentMobile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<Post> posts;
        
        // If current user is authenticated, check if they are the same user
        User currentUser = null;
        if (currentMobile != null) {
            currentUser = userRepository.findByMobileNumber(currentMobile).orElse(null);
        }
        
        if (currentUser != null && currentUser.getId().equals(userId)) {
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

    public boolean toggleLike(Long postId, String userMobile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Like> existingLike = likeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            // Decrease like count
            post.setLikesCount(Math.max(0L, post.getLikesCount() - 1));
            postRepository.save(post);
            return false; // Unliked
        } else {
            Like like = new Like(user, post);
            likeRepository.save(like);
            // Increase like count
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
            return true; // Liked
        }
    }

    private PostResponse convertToResponse(Post post, String currentMobile) {
        PostResponse response = new PostResponse();
        
        // Map basic fields manually
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setImageUrl(post.getMediaUrl());
        response.setIsPublic(post.getIsPublic());
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        
        // Map author (user)
        if (post.getUser() != null) {
            response.setAuthor(modelMapper.map(post.getUser(), UserResponse.class));
        }
        
        // Map category manually to avoid conflict
        if (post.getCategory() != null) {
            response.setCategory(modelMapper.map(post.getCategory(), CategoryResponse.class));
        }
        
        // Map tags
        if (post.getTags() != null) {
            Set<TagResponse> tagResponses = post.getTags().stream()
                    .map(tag -> modelMapper.map(tag, TagResponse.class))
                    .collect(Collectors.toSet());
            response.setTags(tagResponses);
        }
        
        // Set counts - use the stored count for likes and calculate comments count from repository
        response.setLikesCount(post.getLikesCount() != null ? post.getLikesCount().intValue() : 0);
        // Use repository to count comments instead of collection size to avoid lazy loading issues
        Long commentsCount = commentRepository.countByPost(post);
        response.setCommentsCount(commentsCount != null ? commentsCount.intValue() : 0);
        
        // Check if current user liked this post
        if (currentMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentMobile).orElse(null);
            if (currentUser != null) {
                response.setIsLikedByCurrentUser(
                    likeRepository.existsByUserAndPost(currentUser, post)
                );
            } else {
                response.setIsLikedByCurrentUser(false);
            }
        } else {
            response.setIsLikedByCurrentUser(false);
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