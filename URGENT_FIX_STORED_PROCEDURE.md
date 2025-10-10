# URGENT FIX: Stored Procedure Column Name Error

## Problem
The stored procedure is failing with error:
```
JDBC exception executing SQL [CALL sp_get_posts_with_details_json1(?, ?)] 
[Unknown column 'r.content' in 'field list'] [n/a]; SQL [n/a]
```

## Root Cause
The stored procedure was trying to access `c.content` and `r.content` but the `tbl_comments` table has a column named `comment_text`, not `content`.

## Quick Fix Steps

### 1. Execute the Fixed SQL Script
Run the corrected stored procedure script:
```bash
mysql -u your_username -p your_database_name < create_posts_details_stored_procedure_fixed.sql
```

### 2. Key Changes Made
- Changed `c.content` to `c.comment_text` in comments query
- Changed `r.content` to `r.comment_text` in replies query  
- Added DROP statements to remove any existing corrupted procedures
- Fixed procedure name (was corrupted to `emoji_sphere.1`)

### 3. Alternative Quick Test
If you want to test immediately, you can temporarily use the simple version by changing the repository call in `PostRepository.java`:

```java
@Query(value = "CALL sp_get_posts_with_details_json_simple(:offset, :limit)", nativeQuery = true)
List<Object[]> getPostsWithDetailsJson(@Param("offset") int offset, @Param("limit") int limit);
```

The simple version returns posts without comments to avoid the column name issue.

### 4. Verify Table Structure
Make sure your `tbl_comments` table has these columns:
- `id`
- `post_id` 
- `user_id`
- `comment_text` (NOT `content`)
- `parent_comment_id`
- `created_at`
- `updated_at`

## Files Changed
1. **create_posts_details_stored_procedure_fixed.sql** - Corrected stored procedure
2. **PostService.java** - Already fixed to use `commentText` field
3. **CommentRepository.java** - Already fixed with proper methods

## Test After Fix
1. Restart your application
2. Call the endpoint: `GET /posts/with-details?page=0&size=10`
3. Check application logs for success messages

The error should be resolved after executing the corrected SQL script.