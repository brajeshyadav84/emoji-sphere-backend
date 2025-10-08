package com.emojisphere.service;

import com.emojisphere.dto.group.GroupMemberResponse;
import com.emojisphere.dto.group.MemberDeleteRequest;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberService {
    
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    
    public Page<GroupMemberResponse> getGroupMembers(Long groupId, int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member of the group to view members
        if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            throw new RuntimeException("You must be a member to view group members");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").ascending());
        Page<GroupMember> members = groupMemberRepository.findByGroupAndIsActiveTrue(group, pageable);
        
        return members.map(this::convertToGroupMemberResponse);
    }
    
    public Page<GroupMemberResponse> searchGroupMembers(Long groupId, String searchTerm, int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member of the group
        if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            throw new RuntimeException("You must be a member to search group members");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").ascending());
        Page<GroupMember> members;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            members = groupMemberRepository.findByGroupAndIsActiveTrue(group, pageable);
        } else {
            members = groupMemberRepository.searchMembersByUserDetails(group, searchTerm.trim(), pageable);
        }
        
        return members.map(this::convertToGroupMemberResponse);
    }
    
    public List<GroupMemberResponse> getGroupAdmins(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member of the group
        if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
            throw new RuntimeException("You must be a member to view group admins");
        }
        
        List<GroupMember> admins = groupMemberRepository.findByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN);
        return admins.stream()
                .map(this::convertToGroupMemberResponse)
                .collect(Collectors.toList());
    }
    
    public void removeMember(Long groupId, Long memberId, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User memberToRemove = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, admin)) {
            throw new RuntimeException("Only admins can remove members");
        }
        
        // Check if member exists in group
        if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, memberToRemove)) {
            throw new RuntimeException("User is not a member of this group");
        }
        
        // Cannot remove yourself as admin if you're the only admin
        if (admin.getId().equals(memberId)) {
            long adminCount = groupMemberRepository.countByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN);
            if (adminCount == 1) {
                throw new RuntimeException("Cannot remove yourself as you are the only admin");
            }
        }
        
        // Remove member
        groupMemberRepository.removeMemberFromGroup(group, memberToRemove);
    }
    
    public void removeMultipleMembers(MemberDeleteRequest request, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, admin)) {
            throw new RuntimeException("Only admins can remove members");
        }
        
        // Check if admin is trying to remove themselves and they're the only admin
        if (request.getUserIds().contains(admin.getId())) {
            long adminCount = groupMemberRepository.countByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN);
            if (adminCount == 1) {
                throw new RuntimeException("Cannot remove yourself as you are the only admin");
            }
        }
        
        // Validate all users are members
        List<User> usersToRemove = userRepository.findAllById(request.getUserIds());
        if (usersToRemove.size() != request.getUserIds().size()) {
            throw new RuntimeException("One or more users not found");
        }
        
        for (User user : usersToRemove) {
            if (!groupMemberRepository.existsByGroupAndUserAndIsActiveTrue(group, user)) {
                throw new RuntimeException("User " + user.getFirstName() + " " + user.getLastName() + " is not a member of this group");
            }
        }
        
        // Remove members
        groupMemberRepository.removeMembersFromGroup(group, request.getUserIds());
    }
    
    public void promoteToAdmin(Long groupId, Long memberId, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User memberToPromote = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, admin)) {
            throw new RuntimeException("Only admins can promote members");
        }
        
        // Get member record
        GroupMember member = groupMemberRepository.findByGroupAndUserAndIsActiveTrue(group, memberToPromote)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (member.getRole() == GroupRole.ADMIN) {
            throw new RuntimeException("User is already an admin");
        }
        
        // Promote to admin
        member.setRole(GroupRole.ADMIN);
        groupMemberRepository.save(member);
    }
    
    public void demoteFromAdmin(Long groupId, Long memberId, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User memberToDemote = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.isUserAdminOfGroup(group, admin)) {
            throw new RuntimeException("Only admins can demote members");
        }
        
        // Cannot demote if only admin left
        long adminCount = groupMemberRepository.countByGroupAndRoleAndIsActiveTrue(group, GroupRole.ADMIN);
        if (adminCount == 1) {
            throw new RuntimeException("Cannot demote the only admin. Promote another member first.");
        }
        
        // Get member record
        GroupMember member = groupMemberRepository.findByGroupAndUserAndIsActiveTrue(group, memberToDemote)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (member.getRole() == GroupRole.MEMBER) {
            throw new RuntimeException("User is already a regular member");
        }
        
        // Demote to member
        member.setRole(GroupRole.MEMBER);
        groupMemberRepository.save(member);
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