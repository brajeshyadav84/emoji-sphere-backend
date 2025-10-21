package com.emojisphere.controller;

import com.emojisphere.dto.MessageResponse;
import com.emojisphere.dto.friendship.FriendRequestDto;
import com.emojisphere.dto.friendship.FriendResponseDto;
import com.emojisphere.dto.friendship.FriendshipResponse;
import com.emojisphere.entity.User;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.FriendshipService;
import com.emojisphere.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.emojisphere.dto.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send-request")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> sendFriendRequest(@Valid @RequestBody FriendRequestDto request) {
        try {
            Long currentUserId = getCurrentUserId();
            FriendshipResponse response = friendshipService.sendFriendRequest(currentUserId, request.getTargetUserId());
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @PostMapping("/send-request-by-id/{targetUserId}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> sendFriendRequestById(@PathVariable Long targetUserId) {
        try {
            Long currentUserId = getCurrentUserId();
            FriendshipResponse response = friendshipService.sendFriendRequest(currentUserId, targetUserId);
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @PostMapping("/respond")
    public ResponseEntity<ApiResponse<Object>> respondToFriendRequest(@Valid @RequestBody FriendResponseDto request) {
        try {
            Long currentUserId = getCurrentUserId();
            FriendshipResponse response = friendshipService.respondToFriendRequest(
                    request.getFriendshipId(), 
                    currentUserId, 
                    request.getResponse()
            );
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 400));
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<Object>> getFriends(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            Page<FriendshipResponse> friends = friendshipService.getFriends(currentUserId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("friends", friends.getContent());
            response.put("currentPage", friends.getNumber());
            response.put("totalItems", friends.getTotalElements());
            response.put("totalPages", friends.getTotalPages());
            response.put("hasNext", friends.hasNext());
            response.put("hasPrevious", friends.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @GetMapping("/pending/received")
    public ResponseEntity<ApiResponse<Object>> getPendingRequestsReceived(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            Page<FriendshipResponse> requests = friendshipService.getPendingRequestsReceived(currentUserId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests.getContent());
            response.put("currentPage", requests.getNumber());
            response.put("totalItems", requests.getTotalElements());
            response.put("totalPages", requests.getTotalPages());
            response.put("hasNext", requests.hasNext());
            response.put("hasPrevious", requests.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @GetMapping("/pending/sent")
    public ResponseEntity<ApiResponse<Object>> getPendingRequestsSent(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            Page<FriendshipResponse> requests = friendshipService.getPendingRequestsSent(currentUserId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("requests", requests.getContent());
            response.put("currentPage", requests.getNumber());
            response.put("totalItems", requests.getTotalElements());
            response.put("totalPages", requests.getTotalPages());
            response.put("hasNext", requests.hasNext());
            response.put("hasPrevious", requests.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Object>> getAllFriendships(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Long currentUserId = getCurrentUserId();
            Page<FriendshipResponse> friendships = friendshipService.getAllFriendships(currentUserId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("friendships", friendships.getContent());
            response.put("currentPage", friendships.getNumber());
            response.put("totalItems", friendships.getTotalElements());
            response.put("totalPages", friendships.getTotalPages());
            response.put("hasNext", friendships.hasNext());
            response.put("hasPrevious", friendships.hasPrevious());
            
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<ApiResponse<Object>> removeFriend(@PathVariable Long friendId) {
        try {
            Long currentUserId = getCurrentUserId();
            String message = friendshipService.removeFriend(currentUserId, friendId);
            
            return ResponseEntity.ok(ApiResponse.successMessage(message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<ApiResponse<Object>> blockUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            friendshipService.blockUser(currentUserId, userId);
            
            return ResponseEntity.ok(ApiResponse.successMessage("User blocked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @PostMapping("/unblock/{userId}")
    public ResponseEntity<ApiResponse<Object>> unblockUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            friendshipService.unblockUser(currentUserId, userId);
            
            return ResponseEntity.ok(ApiResponse.successMessage("User unblocked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<ApiResponse<Object>> getFriendshipStatus(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            
            // Get detailed friendship information
            FriendshipResponse friendship = friendshipService.getFriendshipStatus(currentUserId, userId);
            
            Map<String, Object> status = new HashMap<>();
            status.put("areFriends", friendshipService.areFriends(currentUserId, userId));
            status.put("friendshipExists", friendshipService.friendshipExists(currentUserId, userId));
            
            if (friendship != null) {
                Map<String, Object> friendshipDetails = new HashMap<>();
                friendshipDetails.put("id", friendship.getId());
                friendshipDetails.put("status", friendship.getStatus());
                friendshipDetails.put("canRespond", friendship.isCanRespond());
                friendshipDetails.put("isSentByCurrentUser", friendship.isSentByCurrentUser());
                
                status.put("friendship", friendshipDetails);
            }
            
            return ResponseEntity.ok(ApiResponse.ok(status));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    @GetMapping("/counts")
    public ResponseEntity<ApiResponse<Object>> getFriendshipCounts() {
        try {
            Long currentUserId = getCurrentUserId();
            
            Map<String, Object> counts = new HashMap<>();
            counts.put("friendsCount", friendshipService.getFriendsCount(currentUserId));
            counts.put("pendingRequestsCount", friendshipService.getPendingRequestsCount(currentUserId));
            
            return ResponseEntity.ok(ApiResponse.ok(counts));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        
        // Get the mobile number from the principal
        Long mobile = userDetails.getId(); // This is actually the mobile number
        
        // Look up the user by mobile number to get the actual user ID
        User user = userRepository.findById(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        return user.getId();
    }
}