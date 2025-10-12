-- Create database
CREATE DATABASE IF NOT EXISTS emoji_sphere;
USE emoji_sphere;

-- Create roles table and insert default roles
CREATE TABLE IF NOT EXISTS roles (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE
);

INSERT IGNORE INTO roles (name) VALUES 
('ROLE_USER'),
('ROLE_MODERATOR'),
('ROLE_ADMIN');

-- Create users table with mobile-based authentication
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(50),
    password VARCHAR(120) NOT NULL,
    age INTEGER,
    location VARCHAR(100),
    gender VARCHAR(10),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create OTP verifications table
CREATE TABLE IF NOT EXISTS otp_verifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mobile VARCHAR(20) NULL,
    email VARCHAR(255) NULL,
    otp VARCHAR(6) NOT NULL,
    verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_mobile (mobile),
    INDEX idx_email (email),
    INDEX idx_expires_at (expires_at)
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    color VARCHAR(100),
    icon VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE
);

-- Insert admin and user credentials
INSERT INTO users (name, mobile, email, password, age, location, gender, role, is_verified, is_active)
VALUES ('AdminUser', '+9108061984', NULL, 'WelcomeDivRey@2025', NULL, NULL, NULL, 'ROLE_ADMIN', TRUE, TRUE);

INSERT INTO users (name, mobile, email, password, age, location, gender, role, is_verified, is_active)
VALUES ('UserCredentials', '+6582238253', NULL, 'Welcome@1', NULL, NULL, NULL, 'ROLE_USER', TRUE, TRUE);

INSERT IGNORE INTO categories (name, description, color, icon, is_active) VALUES 
('General', 'General posts and discussions', '#3B82F6', '💬', TRUE),
('Humor', 'Funny posts and memes', '#F59E0B', '😂', TRUE),
('Art', 'Creative and artistic content', '#EF4444', '🎨', TRUE),
('Technology', 'Tech-related discussions', '#10B981', '💻', TRUE),
('Games', 'Gaming content and discussions', '#8B5CF6', '🎮', TRUE),
('Food', 'Food and cooking related posts', '#F97316', '🍕', TRUE),
('Travel', 'Travel experiences and tips', '#06B6D4', '✈️', TRUE),
('Music', 'Music and audio content', '#EC4899', '🎵', TRUE),
('Sports', 'Sports and fitness', '#84CC16', '⚽', TRUE),
('Education', 'Learning and educational content', '#6366F1', '📚', TRUE);

-- Create some default tags
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(255),
    usage_count INT DEFAULT 0
);

INSERT IGNORE INTO tags (name, description, usage_count) VALUES 
('fun', 'Fun and entertaining content', 0),
('tutorial', 'How-to guides and tutorials', 0),
('question', 'Questions and Q&A', 0),
('news', 'News and current events', 0),
('emoji', 'Emoji-related content', 0),
('beginner', 'Beginner-friendly content', 0),
('advanced', 'Advanced level content', 0),
('discussion', 'Open discussions', 0),
('help', 'Help and support requests', 0),
('showcase', 'Showcasing work or projects', 0);