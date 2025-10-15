
package com.emojisphere.service.impl;

import com.emojisphere.dto.*;
import com.emojisphere.entity.*;
import com.emojisphere.repository.*;
import com.emojisphere.service.GroupPostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
    private ModelMapper modelMapper;

    @Autowired
    private GroupCommentRepository groupCommentRepository;

    @Autowired
    private GroupLikeRepository groupLikeRepository;

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
}
