package com.emojisphere.controller;

import com.emojisphere.dto.MessageResponse;
import com.emojisphere.dto.friendship.FriendRequestDto;
import com.emojisphere.dto.friendship.FriendResponseDto;
import com.emojisphere.dto.friendship.FriendshipResponse;
import com.emojisphere.service.FriendshipService;
import com.emojisphere.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @PostMapping("/send-request")
    public ResponseEntity<?> sendFriendRequest(@Valid @RequestBody FriendRequestDto request) {
        try {
            Long currentUserId = getCurrentUserId();
            FriendshipResponse response = friendshipService.sendFriendRequest(currentUserId, request.getTargetUserId());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/respond")
    public ResponseEntity<?> respondToFriendRequest(@Valid @RequestBody FriendResponseDto request) {
        try {
            Long currentUserId = getCurrentUserId();
            FriendshipResponse response = friendshipService.respondToFriendRequest(
                    request.getFriendshipId(), 
                    currentUserId, 
                    request.getResponse()
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/friends")
    public ResponseEntity<?> getFriends(
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
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/pending/received")
    public ResponseEntity<?> getPendingRequestsReceived(
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
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/pending/sent")
    public ResponseEntity<?> getPendingRequestsSent(
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
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFriendships(
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
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{friendId}")
    public ResponseEntity<?> removeFriend(@PathVariable Long friendId) {
        try {
            Long currentUserId = getCurrentUserId();
            friendshipService.removeFriend(currentUserId, friendId);
            
            return ResponseEntity.ok(new MessageResponse("Friend removed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/block/{userId}")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            friendshipService.blockUser(currentUserId, userId);
            
            return ResponseEntity.ok(new MessageResponse("User blocked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/unblock/{userId}")
    public ResponseEntity<?> unblockUser(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            friendshipService.unblockUser(currentUserId, userId);
            
            return ResponseEntity.ok(new MessageResponse("User unblocked successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{userId}")
    public ResponseEntity<?> getFriendshipStatus(@PathVariable Long userId) {
        try {
            Long currentUserId = getCurrentUserId();
            
            Map<String, Object> status = new HashMap<>();
            status.put("areFriends", friendshipService.areFriends(currentUserId, userId));
            status.put("friendshipExists", friendshipService.friendshipExists(currentUserId, userId));
            
            return ResponseEntity.ok(status);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/counts")
    public ResponseEntity<?> getFriendshipCounts() {
        try {
            Long currentUserId = getCurrentUserId();
            
            Map<String, Object> counts = new HashMap<>();
            counts.put("friendsCount", friendshipService.getFriendsCount(currentUserId));
            counts.put("pendingRequestsCount", friendshipService.getPendingRequestsCount(currentUserId));
            
            return ResponseEntity.ok(counts);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsServiceImpl.UserPrincipal userDetails = (UserDetailsServiceImpl.UserPrincipal) authentication.getPrincipal();
        
        // Convert mobile number to user ID by looking up in database
        // This is a simplified approach - in a real app, you might want to store user ID in JWT
        return Long.parseLong(userDetails.getId());
    }
}