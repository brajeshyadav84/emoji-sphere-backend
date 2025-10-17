package com.emojisphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    
    private Long id;
    private String mobileNumber;
    private String fullName;
    private String dob;
    private String gender;
    private String country;
    private String schoolName;
    private String email;
    private Boolean isVerified;
    private Boolean isActive;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Backward compatibility getters
    public String getName() {
        return this.fullName;
    }

    public String getMobile() {
        return this.mobileNumber;
    }
}