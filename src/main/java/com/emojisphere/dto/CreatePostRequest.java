package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CreatePostRequest {
    
    @NotBlank
    @Size(max = 500)
    private String content;
    
    private String emojiContent;
    
    private String imageUrl;
    
    private Boolean isPublic = true;
    
    private Long categoryId;
    
    private Set<String> tags;
    
    private List<String> mentions; // For mentioning other users
}