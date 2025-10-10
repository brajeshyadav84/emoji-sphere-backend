package com.emojisphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    
    @Size(min = 2, max = 100)
    private String fullName;
    
    @Size(max = 50)
    @Email
    private String email;
    
    private Integer age;
    
    @Size(max = 100)
    private String country;
    
    @Size(max = 10)
    private String gender;
    
    @Size(max = 255)
    private String schoolName; // Optional field

    // Backward compatibility getters
    public String getName() {
        return this.fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }
}