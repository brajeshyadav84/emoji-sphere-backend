package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    
    @NotBlank
    private String mobile;
    
    @NotBlank
    private String otp;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String newPassword;
    
    @NotBlank
    @Size(min = 6, max = 40)
    private String confirmPassword;
}