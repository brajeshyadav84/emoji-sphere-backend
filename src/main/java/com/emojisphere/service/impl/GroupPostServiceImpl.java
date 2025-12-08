
package com.emojisphere.service.impl;

import com.emojisphere.dto.*;
import com.emojisphere.entity.*;
import com.emojisphere.repository.*;
import com.emojisphere.service.GroupPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupPostServiceImpl implements GroupPostService {
    @Autowired
    private GroupPostRepository groupPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private GroupCommentRepository groupCommentRepository;

    @Autowired
    private GroupLikeRepository groupLikeRepository;

    @Autowired
    private ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    public GroupPostServiceImpl() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Page<GroupPostResponse> getAllGroupPosts(Pageable pageable,  String currentMobile) {
        // return groupPostRepository.findAll(pageable).map(this::toResponse);
        Page<GroupPost> posts = groupPostRepository.findByIsPublicTrue(pageable);
        return posts.map(post -> convertToResponse(post, currentMobile));
    }

    @Override
    public GroupPostResponse getGroupPostById(Long id) {
        GroupPost post = groupPostRepository.findById(id).orElseThrow();
        return toResponse(post);
    }

    @Override
    public GroupPostResponse createGroupPost(GroupPostRequest request, String mobile) {
        User author = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GroupPost post = new GroupPost();
        post.setUserId(author.getId());
        post.setUser(author);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setMediaUrl(request.getImageUrl());
        post.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : true);
        post.setCategoryId(request.getCategoryId());
        post.setLikesCount(0L);
        post.setGroupId(request.getGroupId());

        // Handle tags if provided
        // if (request.getTags() != null && !request.getTags().isEmpty()) {
        //     Set<Tag> tags = new HashSet<>();
        //     for (String tagName : request.getTags()) {
        //         Tag tag = tagRepository.findByName(tagName.toLowerCase())
        //                 .orElseGet(() -> {
        //                     Tag newTag = new Tag();
        //                     newTag.setName(tagName.toLowerCase());
        //                     return tagRepository.save(newTag);
        //                 });
        //         tags.add(tag);
        //     }
        //     post.setTags(tags);
        // }

        post = groupPostRepository.save(post);
        return toResponse(post);
    }

    @Override
    public GroupPostResponse updateGroupPost(Long id, GroupPostRequest request, Long userId) {
        GroupPost post = groupPostRepository.findById(id).orElseThrow();
        if (!post.getUserId().equals(userId)) throw new RuntimeException("Unauthorized");
        BeanUtils.copyProperties(request, post);
        post.setUpdatedAt(java.time.LocalDateTime.now());
        post = groupPostRepository.save(post);
        return toResponse(post);
    }

    @Override
    public void deleteGroupPost(Long id, String mobile) {
        GroupPost post = groupPostRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        // Check if user is the author
        if (!post.getUser().getMobileNumber().equals(mobile)) {
            throw new RuntimeException("You can only delete your own posts");
        }
        groupPostRepository.delete(post);
    }

    @Override
    public boolean toggleGroupLike(Long postId, String userMobile) {
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Group post not found"));
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<GroupLike> existingLike = groupLikeRepository.findByUserAndPost(user, post);
        if (existingLike.isPresent()) {
            groupLikeRepository.delete(existingLike.get());
            // Decrease like count
            post.setLikesCount(Math.max(0L, post.getLikesCount() - 1));
            groupPostRepository.save(post);
            return false; // Unliked
        } else {
            GroupLike like = new GroupLike(user, post);
            groupLikeRepository.save(like);
            // Increase like count
            post.setLikesCount(post.getLikesCount() + 1);
            groupPostRepository.save(post);
            return true; // Liked
        }
    }

    private GroupPostResponse toResponse(GroupPost post) {
        GroupPostResponse resp = new GroupPostResponse();
        BeanUtils.copyProperties(post, resp);
        return resp;
    }

    private GroupPostResponse convertToResponse(GroupPost post, String currentMobile) {
        GroupPostResponse response = new GroupPostResponse();

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
        Long commentsCount = groupCommentRepository.countByPost(post);
        response.setCommentsCount(commentsCount != null ? commentsCount.intValue() : 0);

        // Check if current user liked this post
        if (currentMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentMobile).orElse(null);
            if (currentUser != null) {
                // response.setIsLikedByCurrentUser(
                //         groupLikeRepository.existsByUserAndPost(currentUser, post)
                // );
            } else {
                response.setIsLikedByCurrentUser(false);
            }
        } else {
            response.setIsLikedByCurrentUser(false);
        }

        return response;
    }

    public Page<PostWithDetailsResponse> getGroupPostsWithDetails(Long id, Pageable pageable) {
        int offset = (int) pageable.getOffset();
        int limit = pageable.getPageSize();

        try {
            System.out.println("Calling stored procedure with offset=" + offset + ", limit=" + limit);
            List<Object> results = groupPostRepository.getGroupPostsWithDetails(id, offset, limit);

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
            long total = groupPostRepository.countByGroupId(id); // posts.size() + offset; // This is an approximation

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

    private Page<PostWithDetailsResponse> getPostsWithDetailsRegular(Pageable pageable) {
        System.out.println("Using fallback regular query method");

        // Get posts
        Page<GroupPost> posts = groupPostRepository.findByIsPublicTrue(pageable);

        List<PostWithDetailsResponse> postResponses = posts.getContent().stream()
                .map(this::convertPostToDetailedResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(postResponses, pageable, posts.getTotalElements());
    }

    private PostWithDetailsResponse convertPostToDetailedResponse(GroupPost post) {
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
        List<GroupComment> topLevelComments = groupCommentRepository.findTopLevelCommentsByPost(
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

    private PostWithDetailsResponse.CommentWithDetailsResponse convertCommentToDetailedResponse(GroupComment comment) {
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
        List<GroupComment> replies = groupCommentRepository.findByParentComment(comment);
        List<PostWithDetailsResponse.ReplyWithDetailsResponse> replyResponses =
                replies.stream()
                        .map(this::convertReplyToDetailedResponse)
                        .collect(Collectors.toList());

        response.setReplies(replyResponses);

        return response;
    }

    private PostWithDetailsResponse.ReplyWithDetailsResponse convertReplyToDetailedResponse(GroupComment reply) {
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




}
