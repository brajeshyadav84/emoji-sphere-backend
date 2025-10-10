package com.emojisphere.service;

import com.emojisphere.dto.CommentRequest;
import com.emojisphere.dto.CommentResponse;
import com.emojisphere.dto.UserResponse;
import com.emojisphere.entity.Comment;
import com.emojisphere.entity.Post;
import com.emojisphere.entity.User;
import com.emojisphere.exception.ResourceNotFoundException;
import com.emojisphere.exception.UnauthorizedException;
import com.emojisphere.repository.CommentRepository;
import com.emojisphere.repository.LikeRepository;
import com.emojisphere.repository.PostRepository;
import com.emojisphere.repository.UserRepository;
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
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    public Page<CommentResponse> getCommentsByPost(Long postId, Pageable pageable, String currentUserMobile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Page<Comment> comments = commentRepository.findTopLevelCommentsByPost(post, pageable);
        
        return comments.map(comment -> convertToCommentResponse(comment, currentUserMobile));
    }

    public CommentResponse createComment(Long postId, CommentRequest commentRequest, String userMobile) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(user.getId());
        comment.setCommentText(commentRequest.getContent());
        comment.setParentCommentId(commentRequest.getParentCommentId());

        Comment savedComment = commentRepository.save(comment);
        return convertToCommentResponse(savedComment, userMobile);
    }

    public CommentResponse updateComment(Long commentId, CommentRequest commentRequest, String userMobile) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        if (!comment.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You can only update your own comments");
        }

        comment.setCommentText(commentRequest.getContent());
        Comment savedComment = commentRepository.save(comment);
        
        return convertToCommentResponse(savedComment, userMobile);
    }

    public void deleteComment(Long commentId, String userMobile) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        if (!comment.getUserId().equals(user.getId())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getReplies(Long parentCommentId, String currentUserMobile) {
        List<Comment> replies = commentRepository.findRepliesByParentId(parentCommentId);
        return replies.stream()
                .map(comment -> convertToCommentResponse(comment, currentUserMobile))
                .collect(Collectors.toList());
    }

    public boolean toggleCommentLike(Long commentId, String userMobile) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with mobile: " + userMobile));

        return likeRepository.findByUserAndComment(user, comment)
                .map(like -> {
                    likeRepository.delete(like);
                    return false; // unliked
                })
                .orElseGet(() -> {
                    com.emojisphere.entity.Like like = new com.emojisphere.entity.Like(user, comment);
                    likeRepository.save(like);
                    return true; // liked
                });
    }

    private CommentResponse convertToCommentResponse(Comment comment, String currentUserMobile) {
        CommentResponse response = new CommentResponse();
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

        // Set likes count
        Long likesCount = likeRepository.countByComment(comment);
        response.setLikesCount(likesCount.intValue());

        // Check if current user liked this comment
        if (currentUserMobile != null) {
            User currentUser = userRepository.findByMobileNumber(currentUserMobile).orElse(null);
            if (currentUser != null) {
                Boolean isLiked = likeRepository.existsByUserAndComment(currentUser, comment);
                response.setIsLikedByCurrentUser(isLiked);
            }
        }

        // Get replies (limit to avoid deep nesting)
        if (comment.getParentCommentId() == null) {
            List<Comment> replies = commentRepository.findRepliesByParentId(comment.getId());
            List<CommentResponse> replyResponses = replies.stream()
                    .map(reply -> convertToCommentResponse(reply, currentUserMobile))
                    .collect(Collectors.toList());
            response.setReplies(replyResponses);
        } else {
            response.setReplies(new ArrayList<>());
        }

        return response;
    }
}