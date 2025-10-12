package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationListResponse {

    private List<ConversationResponse> conversations;
    private Integer currentPage;
    private Integer totalPages;
    private Long totalItems;
    private Boolean hasNext;
    private Boolean hasPrevious;
}