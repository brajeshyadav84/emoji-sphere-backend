package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpValidationRequest {
    
    @NotBlank
    @Size(max = 20)
    private String mobile;
    
    @NotBlank
    @Size(min = 4, max = 6)
    private String otp;
}