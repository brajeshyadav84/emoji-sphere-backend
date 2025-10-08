package com.emojisphere.controller;

import com.emojisphere.dto.group.*;
import com.emojisphere.service.GroupMemberService;
import com.emojisphere.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GroupController {
    
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    
    // Group Management APIs
    
    @PostMapping
    public ResponseEntity<?> createGroup(@Valid @RequestBody GroupCreateRequest request, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupResponse response = groupService.createGroup(request, userMobile);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Group created successfully",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupResponse response = groupService.getGroup(groupId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(@PathVariable Long groupId, 
                                       @Valid @RequestBody GroupUpdateRequest request, 
                                       Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupResponse response = groupService.updateGroup(groupId, request, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Group updated successfully",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupService.deleteGroup(groupId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Group deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/my-groups")
    public ResponseEntity<?> getUserGroups(Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupResponse> response = groupService.getUserGroups(userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/created-by-me")
    public ResponseEntity<?> getGroupsCreatedByUser(Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupResponse> response = groupService.getGroupsCreatedByUser(userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchGroups(@RequestParam(defaultValue = "") String q,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.searchGroups(q, page, size, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/discover")
    public ResponseEntity<?> discoverPublicGroups(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.discoverPublicGroups(page, size, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Public groups available for joining"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/popular")
    public ResponseEntity<?> getPopularGroups(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.getPopularGroups(page, size, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Popular groups based on member count"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Membership Management APIs
    
    @GetMapping("/{groupId}/can-join")
    public ResponseEntity<?> canJoinGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Map<String, Object> response = groupService.canUserJoinGroup(groupId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@Valid @RequestBody GroupJoinRequest request, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupMemberResponse response = groupService.joinGroup(request, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully joined the group",
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<?> leaveGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupService.leaveGroup(groupId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully left the group"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<?> getGroupRecommendations(@RequestParam(defaultValue = "10") int limit,
                                                    Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupResponse> response = groupService.getGroupRecommendations(userMobile, limit);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response,
                "message", "Groups recommended based on your profile"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    // Member Management APIs
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<?> getGroupMembers(@PathVariable Long groupId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupMemberResponse> response = groupMemberService.getGroupMembers(groupId, page, size, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{groupId}/members/search")
    public ResponseEntity<?> searchGroupMembers(@PathVariable Long groupId,
                                              @RequestParam(defaultValue = "") String q,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupMemberResponse> response = groupMemberService.searchGroupMembers(groupId, q, page, size, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/{groupId}/admins")
    public ResponseEntity<?> getGroupAdmins(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupMemberResponse> response = groupMemberService.getGroupAdmins(groupId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId, 
                                        @PathVariable Long memberId, 
                                        Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.removeMember(groupId, memberId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Member removed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/members/remove-multiple")
    public ResponseEntity<?> removeMultipleMembers(@Valid @RequestBody MemberDeleteRequest request, 
                                                  Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.removeMultipleMembers(request, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Members removed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{groupId}/members/{memberId}/promote")
    public ResponseEntity<?> promoteToAdmin(@PathVariable Long groupId, 
                                          @PathVariable Long memberId, 
                                          Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.promoteToAdmin(groupId, memberId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Member promoted to admin successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/{groupId}/members/{memberId}/demote")
    public ResponseEntity<?> demoteFromAdmin(@PathVariable Long groupId, 
                                           @PathVariable Long memberId, 
                                           Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.demoteFromAdmin(groupId, memberId, userMobile);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin demoted to member successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
}