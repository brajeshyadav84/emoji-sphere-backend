package com.emojisphere.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    
    private Long id;
    private String name;
    private String mobile;
    private String email;
    private Integer age;
    private String location;
    private String gender;
    private Boolean isVerified;
    private Boolean isActive;
    private LocalDateTime createdAt;
}