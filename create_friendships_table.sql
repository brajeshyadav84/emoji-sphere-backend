-- Create friendships table for managing friend relationships and statuses
-- This table stores friendship relationships between users with status tracking

CREATE TABLE emoji_sphere.tbl_friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- User IDs (enforced ordering: user1_id < user2_id to avoid duplicate pairs)
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    
    -- Friendship status
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
    
    -- Who initiated the friendship request
    requester_id BIGINT NOT NULL,
    
    -- Who responded to the friendship request (NULL if still pending)
    responder_id BIGINT NULL,
    
    -- Timestamps
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Response timestamp (when friendship was accepted/declined/blocked)
    responded_at DATETIME NULL,
    
    -- Ensure user1_id is always less than user2_id to avoid duplicate pairs
    CONSTRAINT chk_user_order CHECK (user1_id < user2_id),
    
    -- Ensure requester is either user1 or user2
    CONSTRAINT chk_requester CHECK (requester_id IN (user1_id, user2_id)),
    
    -- Ensure responder is either user1 or user2 (when not null)
    CONSTRAINT chk_responder CHECK (responder_id IS NULL OR responder_id IN (user1_id, user2_id)),
    
    -- Ensure requester and responder are different users
    CONSTRAINT chk_different_users CHECK (requester_id != responder_id OR responder_id IS NULL),
    
    -- Foreign key constraints
    CONSTRAINT fk_friendship_user1 FOREIGN KEY (user1_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_user2 FOREIGN KEY (user2_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_requester FOREIGN KEY (requester_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friendship_responder FOREIGN KEY (responder_id) REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Unique constraint to ensure only one friendship record per user pair
    UNIQUE KEY uk_friendship_pair (user1_id, user2_id),
    
    -- Indexes for better query performance
    INDEX idx_user1_status (user1_id, status),
    INDEX idx_user2_status (user2_id, status),
    INDEX idx_requester (requester_id),
    INDEX idx_status_created (status, created_at),
    INDEX idx_created_at (created_at),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Table to store friendship relationships between users with status tracking';

-- Create a view for easier friendship queries
CREATE OR REPLACE VIEW emoji_sphere.v_friendships AS
SELECT 
    f.id,
    f.user1_id,
    f.user2_id,
    f.status,
    f.requester_id,
    f.responder_id,
    f.created_at,
    f.updated_at,
    f.responded_at,
    u1.full_name AS user1_name,
    u1.email AS user1_email,
    u1.mobile_number AS user1_mobile,
    u2.full_name AS user2_name,
    u2.email AS user2_email,
    u2.mobile_number AS user2_mobile,
    ur.full_name AS requester_name,
    CASE 
        WHEN f.responder_id IS NOT NULL THEN urs.full_name 
        ELSE NULL 
    END AS responder_name
FROM tbl_friendships f
JOIN tbl_users u1 ON f.user1_id = u1.id
JOIN tbl_users u2 ON f.user2_id = u2.id
JOIN tbl_users ur ON f.requester_id = ur.id
LEFT JOIN tbl_users urs ON f.responder_id = urs.id;

-- Function to get ordered user IDs (ensures user1_id < user2_id)
DELIMITER //
CREATE FUNCTION emoji_sphere.get_ordered_user_ids(
    p_user_id1 BIGINT, 
    p_user_id2 BIGINT
) 
RETURNS JSON
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE result JSON;
    
    IF p_user_id1 < p_user_id2 THEN
        SET result = JSON_OBJECT('user1_id', p_user_id1, 'user2_id', p_user_id2);
    ELSE
        SET result = JSON_OBJECT('user1_id', p_user_id2, 'user2_id', p_user_id1);
    END IF;
    
    RETURN result;
END //
DELIMITER ;

-- Stored procedure to send friend request
DELIMITER //
CREATE PROCEDURE emoji_sphere.send_friend_request(
    IN p_requester_id BIGINT,
    IN p_target_user_id BIGINT,
    OUT p_result VARCHAR(255),
    OUT p_friendship_id BIGINT
)
proc_label: BEGIN
    DECLARE v_user1_id BIGINT;
    DECLARE v_user2_id BIGINT;
    DECLARE v_existing_count INT DEFAULT 0;
    DECLARE v_requester_exists INT DEFAULT 0;
    DECLARE v_target_exists INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR: Database error occurred';
        SET p_friendship_id = NULL;
    END;
    
    START TRANSACTION;
    
    -- Validate users exist
    SELECT COUNT(*) INTO v_requester_exists FROM tbl_users WHERE id = p_requester_id AND is_active = 1;
    SELECT COUNT(*) INTO v_target_exists FROM tbl_users WHERE id = p_target_user_id AND is_active = 1;
    
    IF v_requester_exists = 0 THEN
        SET p_result = 'ERROR: Requester user not found or inactive';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    IF v_target_exists = 0 THEN
        SET p_result = 'ERROR: Target user not found or inactive';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Cannot send friend request to yourself
    IF p_requester_id = p_target_user_id THEN
        SET p_result = 'ERROR: Cannot send friend request to yourself';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Get ordered user IDs
    IF p_requester_id < p_target_user_id THEN
        SET v_user1_id = p_requester_id;
        SET v_user2_id = p_target_user_id;
    ELSE
        SET v_user1_id = p_target_user_id;
        SET v_user2_id = p_requester_id;
    END IF;
    
    -- Check if friendship already exists
    SELECT COUNT(*) INTO v_existing_count 
    FROM tbl_friendships 
    WHERE user1_id = v_user1_id AND user2_id = v_user2_id;
    
    IF v_existing_count > 0 THEN
        SET p_result = 'ERROR: Friendship relationship already exists';
        SET p_friendship_id = NULL;
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Create friendship request
    INSERT INTO tbl_friendships (user1_id, user2_id, status, requester_id)
    VALUES (v_user1_id, v_user2_id, 'PENDING', p_requester_id);
    
    SET p_friendship_id = LAST_INSERT_ID();
    SET p_result = 'SUCCESS: Friend request sent successfully';
    
    COMMIT;
END //
DELIMITER ;

-- Stored procedure to respond to friend request
DELIMITER //
CREATE PROCEDURE emoji_sphere.respond_to_friend_request(
    IN p_friendship_id BIGINT,
    IN p_responder_id BIGINT,
    IN p_response ENUM('ACCEPTED', 'DECLINED', 'BLOCKED'),
    OUT p_result VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_requester_id BIGINT;
    DECLARE v_user1_id BIGINT;
    DECLARE v_user2_id BIGINT;
    DECLARE v_current_status VARCHAR(20);
    DECLARE v_valid_responder INT DEFAULT 0;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result = 'ERROR: Database error occurred';
    END;
    
    START TRANSACTION;
    
    -- Get friendship details
    SELECT user1_id, user2_id, requester_id, status 
    INTO v_user1_id, v_user2_id, v_requester_id, v_current_status
    FROM tbl_friendships 
    WHERE id = p_friendship_id;
    
    -- Check if friendship exists
    IF v_requester_id IS NULL THEN
        SET p_result = 'ERROR: Friendship request not found';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Check if request is still pending
    IF v_current_status != 'PENDING' THEN
        SET p_result = 'ERROR: Friendship request is no longer pending';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Validate responder (must be the other user, not the requester)
    IF (p_responder_id = v_user1_id AND p_responder_id != v_requester_id) OR 
       (p_responder_id = v_user2_id AND p_responder_id != v_requester_id) THEN
        SET v_valid_responder = 1;
    END IF;
    
    IF v_valid_responder = 0 THEN
        SET p_result = 'ERROR: Invalid responder for this friendship request';
        ROLLBACK;
        LEAVE proc_label;
    END IF;
    
    -- Update friendship status
    UPDATE tbl_friendships 
    SET status = p_response,
        responder_id = p_responder_id,
        responded_at = CURRENT_TIMESTAMP
    WHERE id = p_friendship_id;
    
    SET p_result = CONCAT('SUCCESS: Friendship request ', LOWER(p_response));
    
    COMMIT;
END //
DELIMITER ;

-- Sample queries to test the table structure
/*
-- Test data insertion (uncomment to test)
INSERT INTO emoji_sphere.tbl_users (full_name, mobile_number, password_hash, gender) VALUES 
('John Doe', '+1234567890', 'hash1', 'Male'),
('Jane Smith', '+1234567891', 'hash2', 'Female'),
('Bob Johnson', '+1234567892', 'hash3', 'Male');

-- Test friend request
CALL emoji_sphere.send_friend_request(1, 2, @result, @friendship_id);
SELECT @result, @friendship_id;

-- Test responding to friend request
CALL emoji_sphere.respond_to_friend_request(@friendship_id, 2, 'ACCEPTED', @response_result);
SELECT @response_result;

-- Query friendships for a user
SELECT * FROM emoji_sphere.v_friendships WHERE user1_id = 1 OR user2_id = 1;
*/

-- Verify table creation
DESCRIBE emoji_sphere.tbl_friendships;