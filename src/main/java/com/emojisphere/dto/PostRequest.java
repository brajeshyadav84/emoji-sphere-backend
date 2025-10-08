package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class PostRequest {
    
    @Size(max = 200)
    private String title; // Made optional for social posts
    
    private String content;
    
    private String emojiContent;
    
    private String imageUrl;
    
    private Boolean isPublic = true;
    
    private Long categoryId;
    
    private Set<String> tags;
}