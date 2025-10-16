package com.emojisphere.service;

import com.emojisphere.dto.GroupPostRequest;
import com.emojisphere.dto.GroupPostResponse;
import com.emojisphere.dto.PostWithDetailsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupPostService {
    Page<GroupPostResponse> getAllGroupPosts(Pageable pageable, String mobile);
    GroupPostResponse getGroupPostById(Long id);
    Page<PostWithDetailsResponse> getGroupPostsWithDetails(Long id, Pageable pageable);
    GroupPostResponse createGroupPost(GroupPostRequest request, String mobile);
    GroupPostResponse updateGroupPost(Long id, GroupPostRequest request, Long userId);
    void deleteGroupPost(Long id, String mobile);
    boolean toggleGroupLike(Long postId, String userMobile);
}
