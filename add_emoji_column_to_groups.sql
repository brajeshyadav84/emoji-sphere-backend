-- Add emoji column to tbl_groups table
-- This migration adds support for emoji field in groups

-- Add emoji column to groups table
ALTER TABLE tbl_groups 
ADD COLUMN emoji VARCHAR(10) NULL;

-- Update existing groups with default emoji if needed
UPDATE tbl_groups 
SET emoji = '🌟' 
WHERE emoji IS NULL OR emoji = '';

-- Add comment to document the column
COMMENT ON COLUMN tbl_groups.emoji IS 'Group emoji icon (up to 10 characters)';

-- Verify the change
SELECT column_name, data_type, character_maximum_length, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'tbl_groups' AND column_name = 'emoji';