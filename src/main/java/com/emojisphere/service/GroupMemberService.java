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
        if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You must be a member to view group members");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").ascending());
        Page<GroupMember> members = groupMemberRepository.findByGroupId(group.getId(), pageable);
        
        return members.map(this::convertToGroupMemberResponse);
    }
    
    public Page<GroupMemberResponse> searchGroupMembers(Long groupId, String searchTerm, int page, int size, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member of the group
        if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You must be a member to search group members");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("joinedAt").ascending());
        Page<GroupMember> members = groupMemberRepository.findByGroupId(group.getId(), pageable);
        
        return members.map(this::convertToGroupMemberResponse);
    }
    
    public List<GroupMemberResponse> getGroupAdmins(Long groupId, String userMobile) {
        User user = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if user is member of the group
        if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new RuntimeException("You must be a member to view group admins");
        }
        
        List<GroupMember> admins = groupMemberRepository.findByGroupIdAndStatus(group.getId(), "ADMIN");
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
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), admin.getId(), admin.getRole())) {
            // Remove member
            groupMemberRepository.deleteByGroupIdAndUserId(group.getId(), memberToRemove.getId());
            // throw new RuntimeException("Only admins can remove members");
        }
        
        // Check if member exists in group
        // if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), memberToRemove.getId())) {
        //     throw new RuntimeException("User is not a member of this group");
        //}
        
        // Cannot remove yourself as admin if you're the only admin
        if (admin.getMobileNumber().equals(memberId)) {
            long adminCount = groupMemberRepository.countByGroupIdAndStatus(group.getId(), "ADMIN");
            if (adminCount == 1) {
                throw new RuntimeException("Cannot remove yourself as you are the only admin");
            }
        }
        
        // Remove member
        groupMemberRepository.deleteByGroupIdAndUserId(group.getId(), memberToRemove.getId());
    }
    
    public void removeMultipleMembers(MemberDeleteRequest request, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), admin.getId(), "ADMIN")) {
            throw new RuntimeException("Only admins can remove members");
        }
        
        // Check if admin is trying to remove themselves and they're the only admin
        if (request.getUserIds().contains(admin.getMobileNumber())) {
            long adminCount = groupMemberRepository.countByGroupIdAndStatus(group.getId(), "ADMIN");
            if (adminCount == 1) {
                throw new RuntimeException("Cannot remove yourself as you are the only admin");
            }
        }
        
        // Validate all users are members
        for (String userId : request.getUserIds()) {
            User userToRemove = userRepository.findByMobileNumber(userId)
                    .orElseThrow(() -> new RuntimeException("User with mobile " + userId + " not found"));

            if (!groupMemberRepository.existsByGroupIdAndUserId(group.getId(), userToRemove.getId())) {
                throw new RuntimeException("User " + userToRemove.getFullName() + " is not a member of this group");
            }
        }

        // Remove members
        for (String userId : request.getUserIds()) {
            User userToRemove = userRepository.findByMobileNumber(userId)
                    .orElseThrow(() -> new RuntimeException("User with mobile " + userId + " not found"));
            groupMemberRepository.deleteByGroupIdAndUserId(group.getId(), userToRemove.getId());
        }
    }
    
    public void promoteToAdmin(Long groupId, String memberId, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User memberToPromote = userRepository.findByMobileNumber(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), admin.getId(), "ADMIN")) {
            throw new RuntimeException("Only admins can promote members");
        }
        
        // Get member record
        GroupMember member = groupMemberRepository.findByGroupIdAndUserId(group.getId(), memberToPromote.getId())
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if ("ADMIN".equals(member.getStatus())) {
            throw new RuntimeException("User is already an admin");
        }
        
        // Promote to admin
        member.setStatus("ADMIN");
        groupMemberRepository.save(member);
    }
    
    public void demoteFromAdmin(Long groupId, String memberId, String userMobile) {
        User admin = userRepository.findByMobileNumber(userMobile)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User memberToDemote = userRepository.findByMobileNumber(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        // Check if requesting user is admin
        if (!groupMemberRepository.existsByGroupIdAndUserIdAndStatus(group.getId(), admin.getId(), "ADMIN")) {
            throw new RuntimeException("Only admins can demote members");
        }
        
        // Cannot demote if only admin left
        long adminCount = groupMemberRepository.countByGroupIdAndStatus(group.getId(), "ADMIN");
        if (adminCount == 1) {
            throw new RuntimeException("Cannot demote the only admin. Promote another member first.");
        }
        
        // Get member record
    GroupMember member = groupMemberRepository.findByGroupIdAndUserId(group.getId(), memberToDemote.getId())
        .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if ("MEMBER".equals(member.getStatus())) {
            throw new RuntimeException("User is already a regular member");
        }
        
        // Demote to member
        member.setStatus("MEMBER");
        groupMemberRepository.save(member);
    }
    
    private GroupMemberResponse convertToGroupMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();

        // Set user info
    User user = userRepository.findById(member.getUserId()).orElse(null);
        if (user != null) {
            response.setId(user.getId());
            response.setUserId(user.getMobileNumber());
            response.setFirstName(user.getFullName().split(" ")[0]);
            response.setLastName(user.getFullName().contains(" ") ? user.getFullName().substring(user.getFullName().indexOf(" ") + 1) : "");
            response.setMobileNumber(user.getMobileNumber());
            response.setEmail(user.getEmail());
        }
        
        // Set group info
        Group group = groupRepository.findById(member.getGroupId()).orElse(null);
        if (group != null) {
            response.setGroupId(group.getId());
            response.setGroupName(group.getName());
        }

        response.setGroupId(member.getGroupId());
        response.setGroupName("");
        response.setJoinedAt(member.getJoinedAt());
        
        return response;
    }
}