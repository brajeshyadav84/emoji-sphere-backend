-- Create teacher_meetings table
-- This table stores all meeting information created by teachers

CREATE TABLE IF NOT EXISTS teacher_meetings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_id BIGINT NOT NULL,
    subject_title VARCHAR(255) NOT NULL,
    subject_description TEXT,
    meeting_url VARCHAR(500),
    meeting_id VARCHAR(100),
    passcode VARCHAR(100),
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    time_zone VARCHAR(100) NOT NULL DEFAULT 'America/New_York',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key to users table (assuming teachers are users with role='TEACHER')
    CONSTRAINT fk_teacher_meetings_teacher FOREIGN KEY (teacher_id) 
        REFERENCES tbl_users(id) ON DELETE CASCADE,
    
    -- Indexes for better query performance
    INDEX idx_teacher_id (teacher_id),
    INDEX idx_start_time (start_time),
    INDEX idx_end_time (end_time),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Add comments to table and columns for documentation
ALTER TABLE teacher_meetings 
    COMMENT = 'Stores teacher meeting information including Zoom, Google Meet, and other virtual meeting platforms';

-- Optionally add some sample data for testing
-- INSERT INTO teacher_meetings (teacher_id, subject_title, subject_description, meeting_url, meeting_id, passcode, start_time, end_time, time_zone)
-- VALUES 
-- (1, 'Advanced Mathematics - Calculus', 'Introduction to derivatives and integrals', 'https://zoom.us/j/1234567890', '123 456 7890', 'Math2024', '2024-12-09 10:00:00', '2024-12-09 11:30:00', 'America/New_York');
