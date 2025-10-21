-- Migration: create feedbacks table
CREATE TABLE IF NOT EXISTS feedbacks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  type VARCHAR(100),
  subject VARCHAR(255),
  message TEXT,
  status VARCHAR(50) DEFAULT 'pending',
  admin_response TEXT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_feedback_user (user_id),
  CONSTRAINT fk_feedback_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);
