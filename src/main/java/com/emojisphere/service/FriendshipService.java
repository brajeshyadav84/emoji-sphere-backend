package com.emojisphere.service;

import com.emojisphere.dto.friendship.FriendshipResponse;
import com.emojisphere.entity.Friendship;
import com.emojisphere.entity.Friendship.FriendshipStatus;
import com.emojisphere.entity.User;
import com.emojisphere.repository.FriendshipRepository;
import com.emojisphere.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;

    @Autowired
    private UserRepository userRepository;

    public FriendshipResponse sendFriendRequest(Long requesterId, Long targetUserId) {
        // Validate users exist and are active
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new RuntimeException("Requester user not found"));
        
        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));

        if (!requester.getIsActive()) {
            throw new RuntimeException("Requester account is not active");
        }

        if (!target.getIsActive()) {
            throw new RuntimeException("Target user account is not active");
        }

        // Cannot send friend request to yourself
        if (requesterId.equals(targetUserId)) {
            throw new RuntimeException("Cannot send friend request to yourself");
        }

        // Check if friendship already exists
        Optional<Friendship> existingFriendship = friendshipRepository
                .findFriendshipBetweenUsers(requesterId, targetUserId);
        
        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            switch (friendship.getStatus()) {
                case PENDING:
                    throw new RuntimeException("Friend request already sent");
                case ACCEPTED:
                    throw new RuntimeException("Users are already friends");
                case DECLINED:
                    throw new RuntimeException("Friend request was previously declined");
                case BLOCKED:
                    throw new RuntimeException("Cannot send friend request - blocked");
            }
        }

        // Create new friendship request with ordered user IDs
        Friendship friendship = Friendship.createOrderedFriendship(requesterId, targetUserId, requesterId);
        friendship = friendshipRepository.save(friendship);

        return convertToFriendshipResponse(friendship, requesterId);
    }

    public FriendshipResponse respondToFriendRequest(Long friendshipId, Long responderId, String response) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friendship request not found"));

        // Validate responder
        if (!friendship.canRespond(responderId)) {
            throw new RuntimeException("Cannot respond to this friend request");
        }

        // Update friendship status
        FriendshipStatus newStatus;
        try {
            newStatus = FriendshipStatus.valueOf(response.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid response: " + response);
        }

        friendship.setStatus(newStatus);
        friendship.setResponderId(responderId);
        friendship.setRespondedAt(LocalDateTime.now());

        friendship = friendshipRepository.save(friendship);

        return convertToFriendshipResponse(friendship, responderId);
    }

    public Page<FriendshipResponse> getFriends(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Friendship> friendships = friendshipRepository.findAcceptedFriendships(userId, pageable);
        
        return friendships.map(friendship -> convertToFriendshipResponse(friendship, userId));
    }

    public Page<FriendshipResponse> getPendingRequestsReceived(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Friendship> friendships = friendshipRepository.findPendingRequestsReceivedByUser(userId, pageable);
        
        return friendships.map(friendship -> convertToFriendshipResponse(friendship, userId));
    }

    public Page<FriendshipResponse> getPendingRequestsSent(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Friendship> friendships = friendshipRepository.findPendingRequestsSentByUser(userId, pageable);
        
        return friendships.map(friendship -> convertToFriendshipResponse(friendship, userId));
    }

    public Page<FriendshipResponse> getAllFriendships(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Friendship> friendships = friendshipRepository.findAllFriendshipsByUser(userId, pageable);
        
        return friendships.map(friendship -> convertToFriendshipResponse(friendship, userId));
    }

    public void removeFriend(Long userId, Long friendId) {
        // Check if they are friends
        Optional<Friendship> friendship = friendshipRepository.findFriendshipBetweenUsers(userId, friendId);
        
        if (friendship.isEmpty()) {
            throw new RuntimeException("Friendship not found");
        }

        if (!friendship.get().isAccepted()) {
            throw new RuntimeException("Users are not friends");
        }

        // Delete the friendship
        friendshipRepository.delete(friendship.get());
    }

    public void blockUser(Long userId, Long userToBlockId) {
        Optional<Friendship> existingFriendship = friendshipRepository
                .findFriendshipBetweenUsers(userId, userToBlockId);

        if (existingFriendship.isPresent()) {
            Friendship friendship = existingFriendship.get();
            friendship.setStatus(FriendshipStatus.BLOCKED);
            friendship.setResponderId(userId);
            friendship.setRespondedAt(LocalDateTime.now());
            friendshipRepository.save(friendship);
        } else {
            // Create a blocked relationship
            Friendship friendship = Friendship.createOrderedFriendship(userId, userToBlockId, userId);
            friendship.setStatus(FriendshipStatus.BLOCKED);
            friendship.setResponderId(userId);
            friendship.setRespondedAt(LocalDateTime.now());
            friendshipRepository.save(friendship);
        }
    }

    public void unblockUser(Long userId, Long userToUnblockId) {
        Optional<Friendship> friendship = friendshipRepository.findFriendshipBetweenUsers(userId, userToUnblockId);
        
        if (friendship.isEmpty() || !friendship.get().isBlocked()) {
            throw new RuntimeException("User is not blocked");
        }

        // Remove the blocked relationship
        friendshipRepository.delete(friendship.get());
    }

    public boolean areFriends(Long userId1, Long userId2) {
        return friendshipRepository.areFriends(userId1, userId2);
    }

    public boolean friendshipExists(Long userId1, Long userId2) {
        return friendshipRepository.friendshipExists(userId1, userId2);
    }

    public Long getFriendsCount(Long userId) {
        return friendshipRepository.countFriendsByUser(userId);
    }

    public Long getPendingRequestsCount(Long userId) {
        return friendshipRepository.countPendingRequestsByUser(userId);
    }

    private FriendshipResponse convertToFriendshipResponse(Friendship friendship, Long currentUserId) {
        FriendshipResponse response = new FriendshipResponse();
        
        response.setId(friendship.getId());
        response.setUser1Id(friendship.getUser1Id());
        response.setUser2Id(friendship.getUser2Id());
        response.setStatus(friendship.getStatus().name());
        response.setRequesterId(friendship.getRequesterId());
        response.setResponderId(friendship.getResponderId());
        response.setCreatedAt(friendship.getCreatedAt());
        response.setUpdatedAt(friendship.getUpdatedAt());
        response.setRespondedAt(friendship.getRespondedAt());

        // Set helper fields
        response.setOtherUserId(friendship.getOtherUserId(currentUserId));
        response.setCanRespond(friendship.canRespond(currentUserId));
        response.setSentByCurrentUser(friendship.getRequesterId().equals(currentUserId));

        // Load user details
        User user1 = userRepository.findById(friendship.getUser1Id()).orElse(null);
        User user2 = userRepository.findById(friendship.getUser2Id()).orElse(null);
        User requester = userRepository.findById(friendship.getRequesterId()).orElse(null);
        User responder = friendship.getResponderId() != null ? 
                userRepository.findById(friendship.getResponderId()).orElse(null) : null;

        if (user1 != null) {
            response.setUser1(convertToUserBasicInfo(user1));
        }
        if (user2 != null) {
            response.setUser2(convertToUserBasicInfo(user2));
        }
        if (requester != null) {
            response.setRequester(convertToUserBasicInfo(requester));
        }
        if (responder != null) {
            response.setResponder(convertToUserBasicInfo(responder));
        }

        // Set other user info
        Long otherUserId = friendship.getOtherUserId(currentUserId);
        if (otherUserId != null) {
            User otherUser = userRepository.findById(otherUserId).orElse(null);
            if (otherUser != null) {
                response.setOtherUser(convertToUserBasicInfo(otherUser));
            }
        }

        return response;
    }

    private FriendshipResponse.UserBasicInfo convertToUserBasicInfo(User user) {
        return new FriendshipResponse.UserBasicInfo(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                user.getCountry(),
                user.getSchoolName(),
                user.getAge(),
                user.getGender(),
                user.getIsActive()
        );
    }
}