-- Fix foreign key constraint issue in tbl_groups
-- Drop the existing foreign key constraint
ALTER TABLE tbl_groups DROP FOREIGN KEY tbl_groups_ibfk_1;

-- Modify the created_by column to be varchar
ALTER TABLE tbl_groups MODIFY COLUMN created_by VARCHAR(255) NOT NULL;

-- Add the new foreign key constraint referencing mobile_number
ALTER TABLE tbl_groups 
ADD CONSTRAINT tbl_groups_created_by_fk 
FOREIGN KEY (created_by) REFERENCES tbl_users(mobile_number);