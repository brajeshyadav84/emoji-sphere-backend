-- Migration to make mobile column optional in otp_verifications table
-- This allows OTP verification to work with either mobile OR email
-- Run this SQL script on your database

-- Remove NOT NULL constraint from mobile column
ALTER TABLE otp_verifications 
MODIFY COLUMN mobile VARCHAR(20) NULL;

-- Update the data.sql schema for future deployments
-- Note: Also update the data.sql file to reflect this change