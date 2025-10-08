package com.emojisphere.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpVerifyRequest {
    
    @NotBlank
    private String mobile;
    
    @NotBlank
    private String otp;
}