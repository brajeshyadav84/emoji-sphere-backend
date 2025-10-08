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
        group.setDescription(request.getDescription());
        group.setPrivacy(request.getPrivacy());
        group.setCreatedBy(creator);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setIsActive(true);
        
        Group savedGroup = groupRepository.save(group);
        
        // Add creator as admin
        GroupMember adminMember = new GroupMember(savedGroup, creator, GroupRole.ADMIN);
        groupMemberRepository.save(adminMember);
        
        return convertToGroupResponse(savedGroup, creator);
    }
    
    public GroupResponse updateGroup(Long groupId, GroupUpdateRequest request, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, user)) {
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
        
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        
        if (request.getPrivacy() != null) {
            group.setPrivacy(request.getPrivacy());
        }
        
        group.setUpdatedAt(LocalDateTime.now());
        Group updatedGroup = groupRepository.save(group);
        
        return convertToGroupResponse(updatedGroup, user);
    }
    
    public void deleteGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, user)) {
            throw new RuntimeException("Only admins can delete groups");
        }
        
        // Soft delete
        group.setIsActive(false);
        group.setUpdatedAt(LocalDateTime.now());
        groupRepository.save(group);
    }
    
    public GroupResponse getGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!group.getIsActive()) {
            throw new RuntimeException("Group not found");
        }
        
        return convertToGroupResponse(group, user);
    }
    
    public List<GroupResponse> getUserGroups(String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Group> userGroups = groupRepository.findGroupsByMember(user);
        return userGroups.stream()
                .map(group -> convertToGroupResponse(group, user))
                .collect(Collectors.toList());
    }
    
    public List<GroupResponse> getGroupsCreatedByUser(String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Group> createdGroups = groupRepository.findGroupsCreatedByUser(user);
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
            groups = groupRepository.findPublicGroups(pageable);
        } else {
            groups = groupRepository.searchGroups(searchTerm.trim(), pageable);
        }
        
        return groups.map(group -> convertToGroupResponse(group, user));
    }
    
    public Page<GroupResponse> discoverPublicGroups(int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Group> publicGroups = groupRepository.findPublicGroups(pageable);
        
        return publicGroups.map(group -> convertToGroupResponse(group, user));
    }
    
    public Page<GroupResponse> getPopularGroups(int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Pageable pageable = PageRequest.of(page, size);
        // Get public groups and sort by member count (we'll implement this in repository)
        Page<Group> popularGroups = groupRepository.findPopularPublicGroups(pageable);
        
        return popularGroups.map(group -> convertToGroupResponse(group, user));
    }
    
    public GroupMemberResponse joinGroup(GroupJoinRequest request, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        if (!group.getIsActive()) {
            throw new RuntimeException("Group is not active");
        }
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            throw new RuntimeException("You are already a member of this group");
        }
        
        // Create membership
        GroupMember member = new GroupMember(group, user, GroupRole.MEMBER);
        GroupMember savedMember = groupMemberRepository.save(member);
        
        return convertToGroupMemberResponse(savedMember);
    }
    
    public void leaveGroup(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member
        if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            throw new RuntimeException("You are not a member of this group");
        }
        
        // Check if user is the only admin
        long adminCount = groupMemberRepository.countByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN);
        boolean isUserAdmin = groupMemberRepository.isUserAdminOfGroup(group, user);
        
        if (isUserAdmin && adminCount == 1) {
            throw new RuntimeException("Cannot leave group as you are the only admin. Transfer admin rights first or delete the group.");
        }
        
        // Remove membership
        groupMemberRepository.removeMemberFromGroup(group, user);
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
        
        if (!group.getIsActive()) {
            result.put("canJoin", false);
            result.put("reason", "Group is not active");
            return result;
        }
        
        if (groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            result.put("canJoin", false);
            result.put("reason", "You are already a member of this group");
            return result;
        }
        
        if (group.getPrivacy() == GroupPrivacy.PUBLIC) {
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
        // This is a simple recommendation - can be enhanced with ML algorithms
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        Page<Group> publicGroups = groupRepository.findPublicGroups(pageable);
        
        return publicGroups.getContent().stream()
                .filter(group -> !groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user))
                .map(group -> convertToGroupResponse(group, user))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private GroupResponse convertToGroupResponse(Group group, User currentUser) {
        GroupResponse response = modelMapper.map(group, GroupResponse.class);
        
        // Set creator info
        User creator = group.getCreatedBy();
        response.setCreatedById(creator.getId());
        response.setCreatedByName(creator.getFirstName() + " " + creator.getLastName());
        response.setCreatedByMobile(creator.getMobileNumber());
        
        // Set statistics
        response.setMemberCount(groupMemberRepository.countByGroupAndIsActiveTrue(group));
        response.setAdminCount(groupMemberRepository.countByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN));
        
        // Set user's relationship to group
        response.setIsUserMember(groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, currentUser));
        response.setIsUserAdmin(groupMemberRepository.isUserAdminOfGroup(group, currentUser));
        
        return response;
    }
    
    private GroupMemberResponse convertToGroupMemberResponse(GroupMember member) {
        GroupMemberResponse response = modelMapper.map(member, GroupMemberResponse.class);
        
        // Set user info
        User user = member.getUser();
        response.setUserId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMobileNumber(user.getMobileNumber());
        response.setEmail(user.getEmail());
        response.setProfilePicture(user.getProfilePicture());
        
        // Set group info
        Group group = member.getGroup();
        response.setGroupId(group.getId());
        response.setGroupName(group.getName());
        
        return response;
    }
}