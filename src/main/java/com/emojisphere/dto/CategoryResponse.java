package com.emojisphere.dto;

import lombok.Data;

@Data
public class CategoryResponse {
    
    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private Boolean isActive;
}