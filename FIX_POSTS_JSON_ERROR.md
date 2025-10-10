# Fix for "Failed to parse posts JSON" Error

## Problem
The error `java.lang.RuntimeException: Failed to parse posts JSON` occurs because the stored procedure `sp_get_posts_with_details_json` does not exist in your database, but your Java code is trying to call it.

## Solution

### Step 1: Create the Missing Stored Procedure

Execute the SQL script located at: `d:\CodeBase\emoji-sphere-backend\create_posts_details_stored_procedure.sql`

This script contains two stored procedures:

1. **sp_get_posts_with_details_json_simple** - A simple version for testing
2. **sp_get_posts_with_details_json** - The full version with comments and replies

### Step 2: Execute the SQL Script

Connect to your MySQL database and run the SQL script:

```bash
mysql -u your_username -p your_database_name < create_posts_details_stored_procedure.sql
```

Or execute the SQL commands manually in your MySQL client.

### Step 3: Grant Permissions (if needed)

If you encounter permission issues, uncomment and modify the GRANT statements at the end of the SQL file:

```sql
GRANT EXECUTE ON PROCEDURE emoji_sphere.sp_get_posts_with_details_json TO 'your_username'@'%';
GRANT EXECUTE ON PROCEDURE emoji_sphere.sp_get_posts_with_details_json_simple TO 'your_username'@'%';
```

Replace `'your_username'` with your actual database username.

## Code Changes Made

### 1. Enhanced Error Handling
- Added comprehensive logging to identify where the parsing fails
- Added fallback mechanism to use regular JPA queries if stored procedure fails

### 2. Fixed DateTime Parsing
- Corrected the `parseDateTime` method to properly handle ISO format dates
- Added better error handling for date parsing failures

### 3. Added Fallback Methods
- `getPostsWithDetailsRegular()` - Falls back to regular JPA queries
- `convertPostToDetailedResponse()` - Converts Post entities to response DTOs
- `convertCommentToDetailedResponse()` - Handles comment conversion
- `convertReplyToDetailedResponse()` - Handles reply conversion

## Testing

### Test the Simple Version First
You can test with the simple stored procedure by temporarily changing the repository method name in `PostRepository.java`:

```java
@Query(value = "CALL sp_get_posts_with_details_json_simple(:offset, :limit)", nativeQuery = true)
List<Object[]> getPostsWithDetailsJson(@Param("offset") int offset, @Param("limit") int limit);
```

### Verify Database Schema
Ensure your database has these tables with the correct structure:
- `tbl_posts`
- `tbl_users` 
- `tbl_comments`
- `tbl_likes`

### Check Table Structure
The stored procedure expects these columns:
- `tbl_posts`: id, user_id, content, media_url, created_at, updated_at, is_public
- `tbl_users`: id, full_name, gender, country
- `tbl_comments`: id, post_id, user_id, content, created_at, parent_comment_id
- `tbl_likes`: post_id, comment_id (for counting likes)

## Debugging

The enhanced code now includes extensive logging. Check your application logs for:

1. "Calling stored procedure with offset=X, limit=Y"
2. "Raw JSON result: ..."
3. "Successfully parsed X posts"
4. Any error messages about JSON parsing or database calls

If the stored procedure still fails, the application will automatically fall back to the regular query method and log: "Using fallback regular query method"

## Alternative Solution

If you prefer not to use stored procedures, you can modify the `PostController.java` to always use the fallback method by changing:

```java
Page<PostWithDetailsResponse> detailedPosts = postService.getPostsWithDetails(
    PageRequest.of(page, size)
);
```

To:

```java
Page<PostWithDetailsResponse> detailedPosts = postService.getPostsWithDetailsRegular(
    PageRequest.of(page, size)
);
```

But you'll need to make the `getPostsWithDetailsRegular` method public in the service class.

## Next Steps

1. Execute the SQL script to create the stored procedure
2. Restart your application
3. Test the `/posts/with-details` endpoint
4. Check the application logs for any remaining issues
5. If problems persist, the fallback mechanism should provide basic functionality