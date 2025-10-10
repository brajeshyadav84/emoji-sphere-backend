-- ===============================================
-- Create Stored Procedure for Posts with Details - FIXED VERSION
-- ===============================================

-- First, drop existing procedures if they exist
DROP PROCEDURE IF EXISTS emoji_sphere.sp_get_posts_with_details_json;
DROP PROCEDURE IF EXISTS emoji_sphere.sp_get_posts_with_details_json_simple;
DROP PROCEDURE IF EXISTS emoji_sphere.sp_get_posts_with_details_json1;

-- First, create a simpler version for testing
DELIMITER //

CREATE PROCEDURE emoji_sphere.sp_get_posts_with_details_json_simple(
    IN p_offset INT,
    IN p_limit INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
        SELECT CONCAT('ERROR: ', @p1, ' - ', @p2) as error_message;
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'post_id', p.id,
            'user_id', p.user_id,
            'user_name', COALESCE(u.full_name, 'Unknown User'),
            'gender', COALESCE(u.gender, 'Unknown'),
            'country', COALESCE(u.country, 'Unknown'),
            'content', COALESCE(p.content, ''),
            'media_url', p.media_url,
            'created_at', DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s'),
            'updated_at', DATE_FORMAT(COALESCE(p.updated_at, p.created_at), '%Y-%m-%d %H:%i:%s'),
            'like_count', 0,
            'comment_count', 0,
            'comments', JSON_ARRAY()
        )
    ) AS posts_json
    FROM tbl_posts p
    LEFT JOIN tbl_users u ON p.user_id = u.id
    WHERE p.is_public = 1
    ORDER BY p.created_at DESC
    LIMIT p_limit OFFSET p_offset;

END //

DELIMITER ;

-- Now create the full version with comments and replies
DELIMITER //

CREATE PROCEDURE emoji_sphere.sp_get_posts_with_details_json(
    IN p_offset INT,
    IN p_limit INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1
            @p1 = RETURNED_SQLSTATE, @p2 = MESSAGE_TEXT;
        SELECT CONCAT('ERROR: ', @p1, ' - ', @p2) as error_message;
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT JSON_ARRAYAGG(
        JSON_OBJECT(
            'post_id', p.id,
            'user_id', p.user_id,
            'user_name', COALESCE(u.full_name, 'Unknown User'),
            'gender', COALESCE(u.gender, 'Unknown'),
            'country', COALESCE(u.country, 'Unknown'),
            'content', COALESCE(p.content, ''),
            'media_url', p.media_url,
            'created_at', DATE_FORMAT(p.created_at, '%Y-%m-%d %H:%i:%s'),
            'updated_at', DATE_FORMAT(COALESCE(p.updated_at, p.created_at), '%Y-%m-%d %H:%i:%s'),
            'like_count', COALESCE(like_counts.like_count, 0),
            'comment_count', COALESCE(comment_counts.comment_count, 0),
            'comments', COALESCE(post_comments.comments_json, JSON_ARRAY())
        )
    ) AS posts_json
    FROM tbl_posts p
    LEFT JOIN tbl_users u ON p.user_id = u.id
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(*) as like_count
        FROM tbl_likes 
        WHERE post_id IS NOT NULL
        GROUP BY post_id
    ) like_counts ON p.id = like_counts.post_id
    LEFT JOIN (
        SELECT 
            post_id, 
            COUNT(*) as comment_count
        FROM tbl_comments 
        WHERE parent_comment_id IS NULL
        GROUP BY post_id
    ) comment_counts ON p.id = comment_counts.post_id
    LEFT JOIN (
        SELECT 
            c.post_id,
            JSON_ARRAYAGG(
                JSON_OBJECT(
                    'comment_id', c.id,
                    'comment_text', COALESCE(c.comment_text, ''),
                    'commented_by', COALESCE(cu.full_name, 'Unknown User'),
                    'comment_created_at', DATE_FORMAT(c.created_at, '%Y-%m-%d %H:%i:%s'),
                    'like_count', COALESCE(cl.like_count, 0),
                    'replies', COALESCE(cr.replies_json, JSON_ARRAY())
                )
            ) as comments_json
        FROM tbl_comments c
        LEFT JOIN tbl_users cu ON c.user_id = cu.id
        LEFT JOIN (
            SELECT 
                comment_id, 
                COUNT(*) as like_count
            FROM tbl_likes 
            WHERE comment_id IS NOT NULL
            GROUP BY comment_id
        ) cl ON c.id = cl.comment_id
        LEFT JOIN (
            SELECT 
                r.parent_comment_id,
                JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'reply_id', r.id,
                        'reply_text', COALESCE(r.comment_text, ''),
                        'replied_by', COALESCE(ru.full_name, 'Unknown User'),
                        'reply_created_at', DATE_FORMAT(r.created_at, '%Y-%m-%d %H:%i:%s')
                    )
                ) as replies_json
            FROM tbl_comments r
            LEFT JOIN tbl_users ru ON r.user_id = ru.id
            WHERE r.parent_comment_id IS NOT NULL
            GROUP BY r.parent_comment_id
        ) cr ON c.id = cr.parent_comment_id
        WHERE c.parent_comment_id IS NULL
        GROUP BY c.post_id
    ) post_comments ON p.id = post_comments.post_id
    WHERE p.is_public = 1
    ORDER BY p.created_at DESC
    LIMIT p_limit OFFSET p_offset;

END //

DELIMITER ;

-- Grant execute permission (replace 'your_username' with actual username)
-- GRANT EXECUTE ON PROCEDURE emoji_sphere.sp_get_posts_with_details_json TO 'your_username'@'%';
-- GRANT EXECUTE ON PROCEDURE emoji_sphere.sp_get_posts_with_details_json_simple TO 'your_username'@'%';