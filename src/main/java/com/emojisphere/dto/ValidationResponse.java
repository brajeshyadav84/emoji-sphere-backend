package com.emojisphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResponse {
    
    private boolean success;
    private String message;
    private Object data;

    public ValidationResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}