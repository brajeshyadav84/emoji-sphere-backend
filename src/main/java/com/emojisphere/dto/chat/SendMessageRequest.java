package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;

    @NotBlank(message = "Message text is required")
    @Size(max = 1000, message = "Message text cannot exceed 1000 characters")
    private String messageText;

    private String messageType = "TEXT"; // TEXT, EMOJI, IMAGE, FILE
}