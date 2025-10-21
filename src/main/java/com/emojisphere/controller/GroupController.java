package com.emojisphere.controller;

import com.emojisphere.dto.group.*;
import com.emojisphere.entity.Group;
import com.emojisphere.entity.User;
import com.emojisphere.repository.GroupRepository;
import com.emojisphere.repository.UserRepository;
import com.emojisphere.service.GroupMemberService;
import com.emojisphere.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.emojisphere.dto.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroupController {
    
    private final GroupService groupService;
    private final GroupMemberService groupMemberService;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    
    // Group Management APIs
    
    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createGroup(@Valid @RequestBody GroupCreateRequest request, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            
            // Only ADMIN role users can create groups
            if (!hasAdminRole(userMobile)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Only administrators can create groups", HttpStatus.FORBIDDEN.value()));
            }
            
            GroupResponse response = groupService.createGroup(request, userMobile);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response, "Group created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Object>> getGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupResponse response = groupService.getGroup(groupId, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Object>> updateGroup(@PathVariable Long groupId, 
                                       @Valid @RequestBody GroupUpdateRequest request, 
                                       Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            
            // Only ADMIN role users or group creators can update groups
            if (!hasAdminRoleOrGroupCreatorPermission(userMobile, groupId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Only administrators or group creators can update groups", HttpStatus.FORBIDDEN.value()));
            }
            
            GroupResponse response = groupService.updateGroup(groupId, request, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response, "Group updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Object>> deleteGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            
            // Only ADMIN role users or group creators can delete groups
            if (!hasAdminRoleOrGroupCreatorPermission(userMobile, groupId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Only administrators or group creators can delete groups", HttpStatus.FORBIDDEN.value()));
            }
            
            groupService.deleteGroup(groupId, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Group deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/my-groups")
    public ResponseEntity<ApiResponse<Object>> getUserGroups(Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupResponse> response = groupService.getUserGroups(userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/created-by-me")
    public ResponseEntity<ApiResponse<Object>> getGroupsCreatedByUser(Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupResponse> response = groupService.getGroupsCreatedByUser(userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> searchGroups(@RequestParam(defaultValue = "") String q,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.searchGroups(q, page, size, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/discover")
    public ResponseEntity<ApiResponse<Object>> discoverPublicGroups(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.discoverPublicGroups(page, size, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response, "Public groups available for joining"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<Object>> getPopularGroups(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupResponse> response = groupService.getPopularGroups(page, size, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response, "Popular groups based on member count"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    // Membership Management APIs
    
    @GetMapping("/{groupId}/can-join")
    public ResponseEntity<ApiResponse<Object>> canJoinGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Map<String, Object> response = groupService.canUserJoinGroup(groupId, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @PostMapping("/join")
    public ResponseEntity<ApiResponse<Object>> joinGroup(@Valid @RequestBody GroupJoinRequest request, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            GroupMemberResponse response = groupService.joinGroup(request, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response, "Successfully joined the group"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Object>> leaveGroup(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupService.leaveGroup(groupId, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Successfully left the group"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error(e.getMessage(), 500));
        }
    }
    
    @GetMapping("/recommendations")
    public ResponseEntity<ApiResponse<Object>> getGroupRecommendations(@RequestParam(defaultValue = "10") int limit,
                                                    Authentication authentication) {
        try {
            String userMobile = (authentication == null) ? null : authentication.getName();
            List<GroupResponse> response = groupService.getGroupRecommendations(userMobile, limit);
            return ResponseEntity.ok(ApiResponse.ok(response, "Groups recommended based on your profile"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    // Member Management APIs
    
    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<Object>> getGroupMembers(@PathVariable Long groupId,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size,
                                           Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupMemberResponse> response = groupMemberService.getGroupMembers(groupId, page, size, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @GetMapping("/{groupId}/members/search")
    public ResponseEntity<ApiResponse<Object>> searchGroupMembers(@PathVariable Long groupId,
                                              @RequestParam(defaultValue = "") String q,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                              Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            Page<GroupMemberResponse> response = groupMemberService.searchGroupMembers(groupId, q, page, size, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @GetMapping("/{groupId}/admins")
    public ResponseEntity<ApiResponse<Object>> getGroupAdmins(@PathVariable Long groupId, Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            List<GroupMemberResponse> response = groupMemberService.getGroupAdmins(groupId, userMobile);
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Object>> removeMember(@PathVariable Long groupId, 
                                        @PathVariable Long memberId,
                                        Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.removeMember(groupId, memberId, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Member removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @PostMapping("/members/remove-multiple")
    public ResponseEntity<ApiResponse<Object>> removeMultipleMembers(@Valid @RequestBody MemberDeleteRequest request, 
                                                  Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.removeMultipleMembers(request, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Members removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @PostMapping("/{groupId}/members/{memberId}/promote")
    public ResponseEntity<ApiResponse<Object>> promoteToAdmin(@PathVariable Long groupId, 
                                          @PathVariable String memberId, 
                                          Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.promoteToAdmin(groupId, memberId, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Member promoted to admin successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    @PostMapping("/{groupId}/members/{memberId}/demote")
    public ResponseEntity<ApiResponse<Object>> demoteFromAdmin(@PathVariable Long groupId, 
                                           @PathVariable String memberId, 
                                           Authentication authentication) {
        try {
            String userMobile = authentication.getName();
            groupMemberService.demoteFromAdmin(groupId, memberId, userMobile);
            return ResponseEntity.ok(ApiResponse.successMessage("Admin demoted to member successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(ApiResponse.error(e.getMessage(), 400));
        }
    }
    
    /**
     * Check if user has admin role
     */
    private boolean hasAdminRole(String userMobile) {
        try {
            User user = userRepository.findByMobileNumber(userMobile).orElse(null);
            if (user == null) {
                return false;
            }
            
            return "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Check if user has admin role or is group creator
     */
    private boolean hasAdminRoleOrGroupCreatorPermission(String userMobile, Long groupId) {
        try {
            User user = userRepository.findByMobileNumber(userMobile).orElse(null);
            if (user == null) {
                return false;
            }
            
            // Check if user has admin role
            if ("ADMIN".equals(user.getRole())) {
                return true;
            }
            
            // Check if user is the group creator
            if (groupId != null) {
                Group group = groupRepository.findById(groupId).orElse(null);
                if (group != null && group.getCreatedBy().equals(userMobile)) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}