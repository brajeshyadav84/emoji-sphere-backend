package com.emojisphere.service;

import com.emojisphere.dto.CommentRequest;
import com.emojisphere.dto.CommentResponse;
import com.emojisphere.dto.UserResponse;
import com.emojisphere.dto.group.GroupCommentResponse;
import com.emojisphere.entity.*;
import com.emojisphere.exception.ResourceNotFoundException;
import com.emojisphere.exception.UnauthorizedException;
import com.emojisphere.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GroupCommentService {

    @Autowired
    private GroupCommentRepository groupCommentRepository;

    @Autowired
    private GroupPostRepository groupPostRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupLikeRepository groupLikeRepository;

    public Page<GroupCommentResponse> getCommentsByPost(Long postId, Pageable pageable, String currentUserMobile) {
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Page<GroupComment> comments = groupCommentRepository.findTopLevelCommentsByPost(post, pageable);
        
        return comments.map(comment -> convertToCommentResponse(comment, currentUserMobile));
    }

    public GroupCommentResponse createComment(Long postId, CommentRequest commentRequest, String userMobile) {
        GroupPost post = groupPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        GroupComment comment = new GroupComment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setCommentText(commentRequest.getContent());
        comment.setParentCommentId(commentRequest.getParentCommentId());

        GroupComment savedComment = groupCommentRepository.save(comment);
        return convertToCommentResponse(savedComment, userMobile);
    }

    public GroupCommentResponse updateComment(Long commentId, CommentRequest commentRequest, String userMobile) {
        GroupComment comment = groupCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        if (!comment.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update your own comments");
        }

        comment.setCommentText(commentRequest.getContent());
        GroupComment savedComment = groupCommentRepository.save(comment);
        
        return convertToCommentResponse(savedComment, userMobile);
    }

    public void deleteComment(Long commentId, String userMobile) {
        GroupComment comment = groupCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        if (!comment.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        groupCommentRepository.delete(comment);
    }

    public List<GroupCommentResponse> getReplies(Long parentCommentId, String currentUserMobile) {
        List<GroupComment> replies = groupCommentRepository.findRepliesByParentId(parentCommentId);
        return replies.stream()
                .map(comment -> convertToCommentResponse(comment, currentUserMobile))
                .collect(Collectors.toList());
    }

    public boolean toggleCommentLike(Long commentId, String userMobile) {
        GroupComment comment = groupCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        return groupLikeRepository.findByUserAndComment(user, comment)
                .map(like -> {
                    groupLikeRepository.delete(like);
                    // Decrease like count
                    comment.setLikesCount(Math.max(0L, comment.getLikesCount() - 1));
                    groupCommentRepository.save(comment);
                    return false; // unliked
                })
                .orElseGet(() -> {
                    com.emojisphere.entity.GroupLike like = new com.emojisphere.entity.GroupLike(user, comment);
                    groupLikeRepository.save(like);
                    // Increase like count
                    comment.setLikesCount(comment.getLikesCount() + 1);
                    groupCommentRepository.save(comment);
                    return true; // liked
                });
    }

    private GroupCommentResponse convertToCommentResponse(GroupComment comment, String currentUserMobile) {
        GroupCommentResponse response = new GroupCommentResponse();
        response.setId(comment.getId());
        response.setCommentText(comment.getCommentText());
        response.setPostId(comment.getPostId());
        response.setParentCommentId(comment.getParentCommentId());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        // Set user information
        if (comment.getUser() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(comment.getUser().getId().toString());
            userResponse.setMobile(comment.getUser().getMobileNumber());
            userResponse.setFullName(comment.getUser().getFullName());
            userResponse.setEmail(comment.getUser().getEmail());
            userResponse.setAge(comment.getUser().getAge());
            userResponse.setCountry(comment.getUser().getCountry());
            userResponse.setGender(comment.getUser().getGender());
            userResponse.setIsVerified(comment.getUser().getIsVerified());
            userResponse.setRole(comment.getUser().getRole());
            userResponse.setCreatedAt(comment.getUser().getCreatedAt());
            response.setUser(userResponse);
        }

        // Set likes count - use the stored count for better performance
        response.setLikesCount(comment.getLikesCount() != null ? comment.getLikesCount().intValue() : 0);

        // Check if current user liked this comment
        if (currentUserMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentUserMobile).orElse(null);
            if (currentUser != null) {
                Boolean isLiked = groupLikeRepository.existsByUserAndComment(currentUser, comment);
                response.setIsLikedByCurrentUser(isLiked);
            }
        }

        // Get replies (limit to avoid deep nesting)
        if (comment.getParentCommentId() == null) {
            List<GroupComment> replies = groupCommentRepository.findRepliesByParentId(comment.getId());
            List<GroupCommentResponse> replyResponses = replies.stream()
                    .map(reply -> convertToCommentResponse(reply, currentUserMobile))
                    .collect(Collectors.toList());
            response.setReplies(replyResponses);
        } else {
            response.setReplies(new ArrayList<>());
        }

        return response;
    }
}