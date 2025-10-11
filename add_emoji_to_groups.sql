-- Add emoji column to groups table
ALTER TABLE tbl_groups ADD COLUMN emoji VARCHAR(10);

-- Update existing groups with default emoji
UPDATE tbl_groups SET emoji = '🌟' WHERE emoji IS NULL;