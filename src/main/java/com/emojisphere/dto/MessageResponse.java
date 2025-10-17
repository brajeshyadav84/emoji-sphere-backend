package com.emojisphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
    private String code = "00";

    public MessageResponse(String message) {
        this.message = message;
    }
}