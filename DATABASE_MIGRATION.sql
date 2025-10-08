-- Database Schema Migration Script
-- This script updates the existing database to match the refactored entity structure

-- ===============================================
-- 1. UPDATE EXISTING TABLES
-- ===============================================

-- Update users table to use auto-increment ID
ALTER TABLE tbl_users 
ADD COLUMN id BIGINT AUTO_INCREMENT PRIMARY KEY FIRST,
ADD UNIQUE KEY uk_mobile_number (mobile_number),
ADD COLUMN updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Update categories table name (if using old name)
RENAME TABLE categories TO tbl_categories;

-- Update tags table name (if using old name)  
RENAME TABLE tags TO tbl_tags;

-- ===============================================
-- 2. CREATE NEW TABLES
-- ===============================================

-- Daily Questions Table
CREATE TABLE IF NOT EXISTS tbl_daily_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_date DATE NOT NULL UNIQUE,
    category_id BIGINT,
    difficulty VARCHAR(10) NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    youtube_video_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES tbl_categories(id) ON DELETE SET NULL,
    INDEX idx_question_date (question_date),
    INDEX idx_difficulty (difficulty)
);

-- Post Media Table (for handling multiple media files per post)
CREATE TABLE IF NOT EXISTS tbl_post_media (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    media_url VARCHAR(500) NOT NULL,
    media_type VARCHAR(20) NOT NULL, -- IMAGE, VIDEO, AUDIO, DOCUMENT
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES tbl_posts(id) ON DELETE CASCADE,
    INDEX idx_post_id (post_id),
    INDEX idx_media_type (media_type)
);

-- Chat Message Status Table
CREATE TABLE IF NOT EXISTS tbl_chat_message_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message_id BIGINT NOT NULL UNIQUE,
    delivered_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (message_id) REFERENCES tbl_chat_messages(id) ON DELETE CASCADE
);

-- Grades Table
CREATE TABLE IF NOT EXISTS tbl_grades (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Quizzes Table
CREATE TABLE IF NOT EXISTS tbl_quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    grade_id BIGINT,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grade_id) REFERENCES tbl_grades(id) ON DELETE SET NULL,
    INDEX idx_grade_id (grade_id)
);

-- Quiz Questions Table
CREATE TABLE IF NOT EXISTS tbl_quiz_questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question TEXT NOT NULL,
    option1 VARCHAR(500),
    option2 VARCHAR(500),
    option3 VARCHAR(500),
    option4 VARCHAR(500),
    correct_option VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES tbl_quizzes(id) ON DELETE CASCADE,
    INDEX idx_quiz_id (quiz_id)
);

-- Holiday Assignments Table
CREATE TABLE IF NOT EXISTS tbl_holiday_assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    grade_id BIGINT,
    holiday_type VARCHAR(50) NOT NULL,
    due_date DATE,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (grade_id) REFERENCES tbl_grades(id) ON DELETE SET NULL,
    INDEX idx_grade_id (grade_id),
    INDEX idx_holiday_type (holiday_type),
    INDEX idx_due_date (due_date)
);

-- Daily Challenges Table
CREATE TABLE IF NOT EXISTS tbl_daily_challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    grade_level VARCHAR(50),
    challenge_date DATE NOT NULL,
    points INT NOT NULL DEFAULT 0,
    description VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_challenge_date (challenge_date),
    INDEX idx_grade_level (grade_level)
);

-- Jokes Table
CREATE TABLE IF NOT EXISTS tbl_jokes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL
);

-- ===============================================
-- 3. INSERT DEFAULT DATA
-- ===============================================

-- Insert default grades
INSERT IGNORE INTO tbl_grades (name) VALUES 
('Grade 1'), ('Grade 2'), ('Grade 3'), ('Grade 4'), ('Grade 5'),
('Grade 6'), ('Grade 7'), ('Grade 8'), ('Grade 9'), ('Grade 10'),
('Grade 11'), ('Grade 12'), ('University'), ('Adult');

-- Insert sample jokes
INSERT IGNORE INTO tbl_jokes (content) VALUES 
('Why don''t scientists trust atoms? Because they make up everything! 😄'),
('What do you call a fake noodle? An impasta! 🍝'),
('Why did the math book look so sad? Because it had too many problems! 📚'),
('What do you call a sleeping bull? A bulldozer! 😴🐂'),
('Why don''t eggs tell jokes? They''d crack each other up! 🥚😂');

-- ===============================================
-- 4. UPDATE FOREIGN KEY REFERENCES (if needed)
-- ===============================================

-- Note: You may need to update foreign key references in application code
-- to use the new ID field instead of mobile_number for user references

-- ===============================================
-- 5. CREATE INDEXES FOR PERFORMANCE
-- ===============================================

-- Add indexes on frequently queried columns
CREATE INDEX IF NOT EXISTS idx_users_mobile ON tbl_users(mobile_number);
CREATE INDEX IF NOT EXISTS idx_users_email ON tbl_users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON tbl_users(role);
CREATE INDEX IF NOT EXISTS idx_users_verified ON tbl_users(is_verified);

CREATE INDEX IF NOT EXISTS idx_posts_user_id ON tbl_posts(user_id);
CREATE INDEX IF NOT EXISTS idx_posts_category_id ON tbl_posts(category_id);
CREATE INDEX IF NOT EXISTS idx_posts_created_at ON tbl_posts(created_at);

CREATE INDEX IF NOT EXISTS idx_comments_post_id ON tbl_comments(post_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON tbl_comments(user_id);
CREATE INDEX IF NOT EXISTS idx_comments_parent_id ON tbl_comments(parent_comment_id);

CREATE INDEX IF NOT EXISTS idx_likes_user_id ON tbl_likes(user_id);
CREATE INDEX IF NOT EXISTS idx_likes_post_id ON tbl_likes(post_id);
CREATE INDEX IF NOT EXISTS idx_likes_comment_id ON tbl_likes(comment_id);

CREATE INDEX IF NOT EXISTS idx_groups_created_by ON tbl_groups(created_by);
CREATE INDEX IF NOT EXISTS idx_groups_privacy ON tbl_groups(privacy);

CREATE INDEX IF NOT EXISTS idx_chat_messages_conversation ON tbl_chat_messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_sender ON tbl_chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_receiver ON tbl_chat_messages(receiver_id);
CREATE INDEX IF NOT EXISTS idx_chat_messages_read ON tbl_chat_messages(is_read);