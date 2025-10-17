package com.emojisphere.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponse {
    
    private String id;
    private String fullName;
    private String mobile;
    private String email;
    private Integer age;
    private String dob;
    private String country;
    private String gender;
    private Boolean isVerified;
    private String role;
    private LocalDateTime createdAt;

    // Backward compatibility getter
    public String getName() {
        return this.fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }
}