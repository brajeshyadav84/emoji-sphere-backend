package com.emojisphere.dto;

import lombok.Data;

@Data
public class TagResponse {
    
    private Long id;
    private String name;
    private String description;
    private Integer usageCount;
}