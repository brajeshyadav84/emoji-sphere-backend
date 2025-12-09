package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for typing indicator events
 * Used to notify users when someone is typing in a conversation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicator {
    
    private Long userId;
    private Long conversationId;
    private Boolean isTyping;
}
