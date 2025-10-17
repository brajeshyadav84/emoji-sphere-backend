package com.emojisphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {
    
    @NotBlank
    @Size(min = 2, max = 100)
    private String fullName;
    
    @NotBlank
    @Size(max = 20)
    private String mobile;
    
    @Size(max = 50)
    @Email
    private String email; // Optional field
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String password;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String confirmPassword;
    
    private Integer age;

    private String dob;
    
    @Size(max = 100)
    private String country;
    
    @Size(max = 255)
    private String schoolName; // Optional field
    
    @NotBlank
    @Size(max = 10)
    private String gender;
    
    private Set<String> role;

    // Backward compatibility getters
    public String getName() {
        return this.fullName;
    }

    public void setName(String name) {
        this.fullName = name;
    }
}