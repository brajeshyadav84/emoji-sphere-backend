package com.emojisphere.service;

import com.emojisphere.dto.GroupPostRequest;
import com.emojisphere.dto.GroupPostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupPostService {
    Page<GroupPostResponse> getAllGroupPosts(Pageable pageable, String mobile);
    GroupPostResponse getGroupPostById(Long id);
    GroupPostResponse createGroupPost(GroupPostRequest request, String mobile);
    GroupPostResponse updateGroupPost(Long id, GroupPostRequest request, Long userId);
    void deleteGroupPost(Long id, String mobile);
    boolean toggleGroupLike(Long postId, String userMobile);
}
