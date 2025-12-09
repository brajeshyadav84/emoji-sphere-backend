package com.emojisphere.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for message read receipts
 * Used to notify senders when their messages have been read
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageReadReceipt {
    
    private Long conversationId;
    private Long userId;
    private List<Long> messageIds;
}
