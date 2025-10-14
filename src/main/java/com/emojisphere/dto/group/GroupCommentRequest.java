package com.emojisphere.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GroupCommentRequest {
    
    @NotBlank
    @Size(max = 500)
    private String content;
    
    private Long parentCommentId; // For replies
}