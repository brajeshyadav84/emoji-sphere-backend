package com.emojisphere.service;

import com.emojisphere.dto.group.*;
import com.emojisphere.entity.*;
import com.emojisphere.repository.GroupMemberRepository;
import com.emojisphere.repository.GroupRepository;
import com.emojisphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {
    
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    public GroupResponse createGroup(GroupCreateRequest request, String userMobile) {
    // Find user
    User creator = userRepository.findByMobileNumber(userMobile)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Check if group name already exists
    if (groupRepository.existsByNameIgnoreCase(request.getName())) {
        throw new RuntimeException("Group with this name already exists");
    }

    // Create group
    Group group = new Group();
    group.setName(request.getName());
    group.setEmoji(request.getEmoji());
    group.setDescription(request.getDescription());
    group.setPrivacy(request.getPrivacy());
    group.setCreatedBy(creator.getId()); // Now expects Long (user id)
    group.setCreatedAt(LocalDateTime.now());

    Group savedGroup = groupRepository.save(group);

    // Add creator as admin
    GroupMember adminMember = new GroupMember(savedGroup.getId(), creator.getId(), creator.getDob(), "ADMIN");
    groupMemberRepository.save(adminMember);

    return convertToGroupResponse(savedGroup, creator);
    }
    
    public GroupResponse updateGroup(Long groupId, GroupUpdateRequest request, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin (simplified check)
        boolean isAdmin = group.getCreatedBy().equals(user.getId());
        if (!isAdmin) {
            throw new RuntimeException("Only admins can update group details");
        }
        
        // Update fields if provided
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            if (!group.getName().equalsIgnoreCase(request.getName()) && 
                groupRepository.existsByNameIgnoreCase(request.getName())) {
                throw new RuntimeException("Group with this name already exists");
            }
            group.setName(request.getName());
        }
        
        if (request.getEmoji() != null) {
            group.setEmoji(request.getEmoji());
        }
        
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        
        if (request.getPrivacy() != null) {
            group.setPrivacy(request.getPrivacy());
        }
        
        Group updatedGroup = groupRepository.save(group);
        
        return convertToGroupResponse(updatedGroup, user);
    }
    
    public void deleteGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin (simplified check)
        boolean isAdmin = group.getCreatedBy().equals(user.getId());
        if (!isAdmin) {
            throw new RuntimeException("Only admins can delete groups");
        }
        
        // Delete the group
        groupRepository.delete(group);
    }
    
    public GroupResponse getGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        return convertToGroupResponse(group, user);
    }
    
    public List<GroupResponse> getUserGroups(String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Find groups where user is a member
        List<GroupMember> userMemberships = groupMemberRepository.findByUserId(user.getId());
        List<Long> groupIds = userMemberships.stream().map(GroupMember::getGroupId).collect(Collectors.toList());
        
        if (groupIds.isEmpty()) {
            return List.of();
        }
        
        List<Group> userGroups = groupRepository.findAllById(groupIds);
        return userGroups.stream()
                .map(group -> convertToGroupResponse(group, user))
                .collect(Collectors.toList());
    }
    
    public List<GroupResponse> getGroupsCreatedByUser(String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Group> createdGroups = groupRepository.findByCreatedBy(user.getId());
        return createdGroups.stream()
                .map(group -> convertToGroupResponse(group, user))
                .collect(Collectors.toList());
    }
    
    public Page<GroupResponse> searchGroups(String searchTerm, int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Group> groups;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            groups = groupRepository.findByPrivacy("PUBLIC", pageable);
        } else {
            groups = groupRepository.findByNameContainingIgnoreCaseAndPrivacy(searchTerm.trim(), "PUBLIC", pageable);
        }
        
        return groups.map(group -> convertToGroupResponse(group, user));
    }
    
    public Page<GroupResponse> discoverPublicGroups(int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Group> publicGroups = groupRepository.findByPrivacy("PUBLIC", pageable);
        
        return publicGroups.map(group -> convertToGroupResponse(group, user));
    }
    
    public Page<GroupResponse> getPopularGroups(int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        // Get public groups (simplified - can be enhanced later)
        Page<Group> popularGroups = groupRepository.findByPrivacy("PUBLIC", pageable);
        
        return popularGroups.map(group -> convertToGroupResponse(group, user));
    }
    
    public GroupMemberResponse joinGroup(GroupJoinRequest request, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You are already a member of this group");
        }

        // Create membership
        GroupMember member = new GroupMember(group.getId(), user.getId(), user.getDob(), "MEMBER");
        GroupMember savedMember = groupMemberRepository.save(member);

        return convertToGroupMemberResponse(savedMember);
    }
    
    public void leaveGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member
        if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You are not a member of this group");
        }

        // Check if user is the creator
        if (group.getCreatedBy().equals(user.getId())) {
            throw new RuntimeException("Cannot leave group as you are the creator. Delete the group instead.");
        }

        // Remove membership
        groupMemberRepository.deleteByGroupIdAndUserId(group.getId(), user.getId());
    }
    
    public Map<String, Object> canUserJoinGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        Map<String, Object> result = new HashMap<>();
        result.put("groupId", groupId);
        result.put("groupName", group.getName());
        result.put("privacy", group.getPrivacy());
        
        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            result.put("canJoin", false);
            result.put("reason", "You are already a member of this group");
            return result;
        }
        
        if ("PUBLIC".equals(group.getPrivacy())) {
            result.put("canJoin", true);
            result.put("reason", "Public group - can join directly");
        } else {
            result.put("canJoin", false);
            result.put("reason", "Private group - requires invitation from admin");
        }
        
        return result;
    }
    
    public List<GroupResponse> getGroupRecommendations(String userMobile, int limit) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get public groups that user is not already a member of
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Group> publicGroups = groupRepository.findByPrivacy("PUBLIC", pageable);
        
    return publicGroups.getContent().stream()
        .filter(group -> !groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId()))
        .map(group -> convertToGroupResponse(group, user))
        .limit(limit)
        .collect(Collectors.toList());
    }
    
    private GroupResponse convertToGroupResponse(Group group, User currentUser) {
        GroupResponse response = modelMapper.map(group, GroupResponse.class);
        
        // Set creator info
    User creator = userRepository.findById(group.getCreatedBy()).orElse(null);
        if (creator != null) {
            response.setCreatedById(creator.getMobileNumber());
            response.setCreatedByName(creator.getFullName());
            response.setCreatedByMobile(creator.getMobileNumber());
        }
        
        // Set statistics
        response.setMemberCount(groupMemberRepository.countByGroupId(group.getId()));
        response.setAdminCount(groupMemberRepository.countByGroupIdAndStatus(group.getId(), "ADMIN"));
        
        // Set user's relationship to group
    response.setIsUserMember(groupMemberRepository.existsByGroupIdAndUserId(group.getId(), currentUser.getId()));
    response.setIsUserAdmin(group.getCreatedBy().equals(currentUser.getId()) || 
                   groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), currentUser.getId(), "ADMIN"));
        
        return response;
    }
    
    private GroupMemberResponse convertToGroupMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(member.getId());
        response.setStatus(member.getStatus());
        response.setJoinedAt(member.getJoinedAt());
        response.setIsActive(true); // or set based on your logic if available
        // response.setAge(member.getDob());
        response.setGroupId(member.getGroupId());

        // Set user info
        User user = userRepository.findById(member.getUserId()).orElse(null);
        if (user != null) {
            response.setUserId(user.getMobileNumber()); // userId as String (mobile number)
            response.setMobileNumber(user.getMobileNumber());
            response.setFirstName(user.getFullName() != null ? user.getFullName().split(" ")[0] : "");
            response.setLastName(user.getFullName() != null && user.getFullName().contains(" ") ? user.getFullName().substring(user.getFullName().indexOf(" ") + 1) : "");
            response.setEmail(user.getEmail());
            // response.setProfilePicture(user.getProfilePicture());
        }

        // Set group info
        Group group = groupRepository.findById(member.getGroupId()).orElse(null);
        if (group != null) {
            response.setGroupName(group.getName());
        }

        return response;
    }
}