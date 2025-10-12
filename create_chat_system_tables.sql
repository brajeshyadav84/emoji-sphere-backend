-- ===============================================
-- CHAT SYSTEM DATABASE SCHEMA
-- ===============================================
-- This script creates a comprehensive chat system with one-to-one messaging
-- Designed for high performance and scalability

USE emoji_sphere;

-- Drop existing tables if they exist (in reverse order due to foreign keys)
DROP TABLE IF EXISTS tbl_conversation_settings;
DROP TABLE IF EXISTS tbl_chat_message_status;
DROP TABLE IF EXISTS tbl_chat_user_blocklist;
DROP TABLE IF EXISTS tbl_chat_messages;
DROP TABLE IF EXISTS tbl_chat_conversations;

-- ===============================================
-- 1. CHAT CONVERSATIONS TABLE
-- ===============================================
-- Stores conversation metadata between two users
CREATE TABLE tbl_chat_conversations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_one_id BIGINT NOT NULL,  -- Always the smaller user ID for consistency
    user_two_id BIGINT NOT NULL,  -- Always the larger user ID for consistency
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_conversations_user_order CHECK (user_one_id < user_two_id),
    UNIQUE KEY uk_conversation_pair (user_one_id, user_two_id),
    
    -- Foreign Keys
    FOREIGN KEY (user_one_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    FOREIGN KEY (user_two_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_conversations_user_one_id (user_one_id),
    INDEX idx_conversations_user_two_id (user_two_id),
    INDEX idx_conversations_updated_at (updated_at)
);

-- ===============================================
-- 2. CHAT MESSAGES TABLE
-- ===============================================
-- Stores individual chat messages
CREATE TABLE tbl_chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    message_text VARCHAR(1000) NOT NULL,
    message_type ENUM('TEXT', 'EMOJI', 'IMAGE', 'FILE') NOT NULL DEFAULT 'TEXT',
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (conversation_id) REFERENCES tbl_chat_conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_messages_conversation_id (conversation_id),
    INDEX idx_messages_sender_id (sender_id),
    INDEX idx_messages_receiver_id (receiver_id),
    INDEX idx_messages_is_read (is_read),
    INDEX idx_messages_created_at (created_at),
    INDEX idx_messages_conversation_created (conversation_id, created_at)
);

-- ===============================================
-- 3. MESSAGE STATUS TABLE
-- ===============================================
-- Tracks delivery and read status of messages
CREATE TABLE tbl_chat_message_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL UNIQUE,
    delivered_at DATETIME NULL,
    read_at DATETIME NULL,
    
    -- Foreign Key
    FOREIGN KEY (message_id) REFERENCES tbl_chat_messages(id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_status_message_id (message_id),
    INDEX idx_status_delivered_at (delivered_at),
    INDEX idx_status_read_at (read_at)
);

-- ===============================================
-- 4. USER BLOCKLIST TABLE
-- ===============================================
-- Manages blocked users for chat
CREATE TABLE tbl_chat_user_blocklist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocker_id BIGINT NOT NULL,
    blocked_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (blocker_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Constraints
    UNIQUE KEY uk_block_pair (blocker_id, blocked_id),
    CONSTRAINT chk_blocklist_no_self_block CHECK (blocker_id != blocked_id),
    
    -- Indexes
    INDEX idx_blocklist_blocker_id (blocker_id),
    INDEX idx_blocklist_blocked_id (blocked_id)
);

-- ===============================================
-- 5. CONVERSATION SETTINGS TABLE
-- ===============================================
-- Per-user settings for conversations (notifications, etc.)
CREATE TABLE tbl_conversation_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    notifications_enabled TINYINT(1) NOT NULL DEFAULT 1,
    archived TINYINT(1) NOT NULL DEFAULT 0,
    muted_until DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (conversation_id) REFERENCES tbl_chat_conversations(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Constraints
    UNIQUE KEY uk_conversation_user (conversation_id, user_id),
    
    -- Indexes
    INDEX idx_settings_conversation_id (conversation_id),
    INDEX idx_settings_user_id (user_id),
    INDEX idx_settings_archived (archived),
    INDEX idx_settings_muted_until (muted_until)
);

-- ===============================================
-- 6. HELPFUL STORED PROCEDURES
-- ===============================================

DELIMITER //

-- Get or create conversation between two users
CREATE PROCEDURE GetOrCreateConversation(
    IN p_user1_id BIGINT,
    IN p_user2_id BIGINT
)
BEGIN
    DECLARE v_conversation_id BIGINT DEFAULT NULL;
    DECLARE v_user_one_id BIGINT;
    DECLARE v_user_two_id BIGINT;
    
    -- Order user IDs consistently (smaller first)
    IF p_user1_id < p_user2_id THEN
        SET v_user_one_id = p_user1_id;
        SET v_user_two_id = p_user2_id;
    ELSE
        SET v_user_one_id = p_user2_id;
        SET v_user_two_id = p_user1_id;
    END IF;
    
    -- Try to find existing conversation
    SELECT id INTO v_conversation_id
    FROM tbl_chat_conversations
    WHERE user_one_id = v_user_one_id AND user_two_id = v_user_two_id;
    
    -- Create conversation if it doesn't exist
    IF v_conversation_id IS NULL THEN
        INSERT INTO tbl_chat_conversations (user_one_id, user_two_id)
        VALUES (v_user_one_id, v_user_two_id);
        
        SET v_conversation_id = LAST_INSERT_ID();
        
        -- Create default settings for both users
        INSERT INTO tbl_conversation_settings (conversation_id, user_id)
        VALUES 
            (v_conversation_id, v_user_one_id),
            (v_conversation_id, v_user_two_id);
    END IF;
    
    SELECT v_conversation_id as conversation_id;
END //

-- Get conversation details with user info
CREATE PROCEDURE GetConversationDetails(
    IN p_conversation_id BIGINT,
    IN p_current_user_id BIGINT
)
BEGIN
    SELECT 
        c.id,
        c.user_one_id,
        c.user_two_id,
        c.created_at,
        c.updated_at,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN c.user_two_id
            ELSE c.user_one_id
        END as other_user_id,
        u1.full_name as user_one_name,
        u1.gender as user_one_gender,
        u2.full_name as user_two_name,
        u2.gender as user_two_gender,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN u2.full_name
            ELSE u1.full_name
        END as other_user_name,
        CASE 
            WHEN c.user_one_id = p_current_user_id THEN u2.gender
            ELSE u1.gender
        END as other_user_gender,
        cs.notifications_enabled,
        cs.archived,
        cs.muted_until,
        -- Count unread messages
        (SELECT COUNT(*) 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         AND m.receiver_id = p_current_user_id 
         AND m.is_read = 0) as unread_count,
        -- Get last message
        (SELECT m.message_text 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         ORDER BY m.created_at DESC 
         LIMIT 1) as last_message,
        -- Get last message time
        (SELECT m.created_at 
         FROM tbl_chat_messages m 
         WHERE m.conversation_id = c.id 
         ORDER BY m.created_at DESC 
         LIMIT 1) as last_message_time
    FROM tbl_chat_conversations c
    LEFT JOIN tbl_users u1 ON c.user_one_id = u1.id
    LEFT JOIN tbl_users u2 ON c.user_two_id = u2.id
    LEFT JOIN tbl_conversation_settings cs ON c.id = cs.conversation_id AND cs.user_id = p_current_user_id
    WHERE c.id = p_conversation_id;
END //

-- Mark messages as read
CREATE PROCEDURE MarkMessagesAsRead(
    IN p_conversation_id BIGINT,
    IN p_user_id BIGINT
)
BEGIN
    UPDATE tbl_chat_messages 
    SET is_read = 1, updated_at = CURRENT_TIMESTAMP
    WHERE conversation_id = p_conversation_id 
    AND receiver_id = p_user_id 
    AND is_read = 0;
    
    -- Update message status table
    INSERT INTO tbl_chat_message_status (message_id, read_at)
    SELECT m.id, CURRENT_TIMESTAMP
    FROM tbl_chat_messages m
    WHERE m.conversation_id = p_conversation_id 
    AND m.receiver_id = p_user_id 
    AND m.is_read = 1
    ON DUPLICATE KEY UPDATE read_at = CURRENT_TIMESTAMP;
END //

DELIMITER ;

-- ===============================================
-- 7. CREATE VIEWS FOR EASIER QUERIES
-- ===============================================

-- View for conversation list with user details
CREATE OR REPLACE VIEW v_conversation_list AS
SELECT 
    c.id as conversation_id,
    c.user_one_id,
    c.user_two_id,
    c.created_at,
    c.updated_at,
    u1.full_name as user_one_name,
    u1.gender as user_one_gender,
    u1.email as user_one_email,
    u2.full_name as user_two_name,
    u2.gender as user_two_gender,
    u2.email as user_two_email
FROM tbl_chat_conversations c
LEFT JOIN tbl_users u1 ON c.user_one_id = u1.id
LEFT JOIN tbl_users u2 ON c.user_two_id = u2.id;

-- ===============================================
-- 8. PERFORMANCE OPTIMIZATION INDEXES
-- ===============================================

-- Composite indexes for common query patterns
CREATE INDEX idx_chat_messages_conversation_time ON tbl_chat_messages(conversation_id, created_at DESC);
CREATE INDEX idx_chat_messages_user_unread ON tbl_chat_messages(receiver_id, is_read, conversation_id);
CREATE INDEX idx_chat_conversations_user_updated ON tbl_chat_conversations(user_one_id, user_two_id, updated_at DESC);

-- ===============================================
-- 9. SAMPLE DATA (Optional - for testing)
-- ===============================================

-- Insert sample conversation (only if you want test data)
-- INSERT INTO tbl_chat_conversations (user_one_id, user_two_id) 
-- SELECT 1, 2 WHERE NOT EXISTS (SELECT 1 FROM tbl_chat_conversations WHERE user_one_id = 1 AND user_two_id = 2);

-- ===============================================
-- 10. VERIFICATION QUERIES
-- ===============================================

-- Check table creation
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    DATA_LENGTH,
    INDEX_LENGTH
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'emoji_sphere' 
AND TABLE_NAME LIKE 'tbl_chat%'
ORDER BY TABLE_NAME;

-- Check indexes
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = 'emoji_sphere' 
AND TABLE_NAME LIKE 'tbl_chat%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

COMMIT;