-- Migration to add email column to otp_verifications table
-- Run this SQL script on your database

-- Add email column to otp_verifications table if it doesn't exist
ALTER TABLE otp_verifications 
ADD COLUMN IF NOT EXISTS email VARCHAR(255) NULL;

-- Add index for better performance on email lookups
CREATE INDEX IF NOT EXISTS idx_otp_verifications_email ON otp_verifications(email);

-- Add index for email and verified status
CREATE INDEX IF NOT EXISTS idx_otp_verifications_email_verified ON otp_verifications(email, verified);

-- Add index for email and expiry time
CREATE INDEX IF NOT EXISTS idx_otp_verifications_email_expires ON otp_verifications(email, expires_at);