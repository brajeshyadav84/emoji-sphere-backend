-- Migration to add likes_count column to tbl_posts and tbl_comments and initialize with correct values

-- Add likes_count column to posts if it doesn't exist
ALTER TABLE tbl_posts ADD COLUMN IF NOT EXISTS likes_count BIGINT DEFAULT 0;

-- Add likes_count column to comments if it doesn't exist
ALTER TABLE tbl_comments ADD COLUMN IF NOT EXISTS likes_count BIGINT DEFAULT 0;

-- Update existing posts with correct like counts
UPDATE tbl_posts 
SET likes_count = (
    SELECT COUNT(*) 
    FROM tbl_likes 
    WHERE tbl_likes.post_id = tbl_posts.id
);

-- Update existing comments with correct like counts
UPDATE tbl_comments 
SET likes_count = (
    SELECT COUNT(*) 
    FROM tbl_likes 
    WHERE tbl_likes.comment_id = tbl_comments.id
);

-- Ensure the columns are not null
ALTER TABLE tbl_posts ALTER COLUMN likes_count SET NOT NULL;
ALTER TABLE tbl_comments ALTER COLUMN likes_count SET NOT NULL;

-- Add indexes for better performance on likes_count queries
CREATE INDEX IF NOT EXISTS idx_posts_likes_count ON tbl_posts(likes_count);
CREATE INDEX IF NOT EXISTS idx_comments_likes_count ON tbl_comments(likes_count);

-- Verify the updates
SELECT 'Posts' as table_name, id, title as content, likes_count, 
       (SELECT COUNT(*) FROM tbl_likes WHERE post_id = tbl_posts.id) as actual_likes
FROM tbl_posts 
ORDER BY likes_count DESC 
LIMIT 5

UNION ALL

SELECT 'Comments' as table_name, id, 
       SUBSTRING(comment_text, 1, 50) as content, 
       likes_count, 
       (SELECT COUNT(*) FROM tbl_likes WHERE comment_id = tbl_comments.id) as actual_likes
FROM tbl_comments 
ORDER BY likes_count DESC 
LIMIT 5;