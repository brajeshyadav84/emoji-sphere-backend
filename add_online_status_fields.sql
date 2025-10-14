-- Add online status fields to users table
ALTER TABLE tbl_users 
ADD COLUMN is_online TINYINT(1) DEFAULT 0,
ADD COLUMN last_seen TIMESTAMP NULL,
ADD COLUMN online_status VARCHAR(20) DEFAULT 'offline';

-- Add index for online status queries
CREATE INDEX idx_users_online_status ON tbl_users(is_online, last_seen);