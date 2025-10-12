# Chat System Testing Guide

## Prerequisites

1. **Database Setup**: Run the chat system SQL script
```bash
mysql -u root -p emoji_sphere < create_chat_system_tables.sql
```

2. **Backend Setup**: Make sure the backend is running on port 8081
```bash
cd emoji-sphere-backend
mvn spring-boot:run
```

3. **Frontend Setup**: Make sure the frontend is running on port 5173
```bash
cd emoji-sphere
npm run dev
```

## Testing Steps

### 1. Database Verification

```sql
-- Check if tables were created
USE emoji_sphere;
SHOW TABLES LIKE 'tbl_chat%';

-- Should show:
-- tbl_chat_conversations
-- tbl_chat_messages  
-- tbl_chat_message_status
-- tbl_chat_user_blocklist

-- Check table structure
DESCRIBE tbl_chat_conversations;
DESCRIBE tbl_chat_messages;

-- Verify stored procedures
SHOW PROCEDURE STATUS WHERE db = 'emoji_sphere';
```

### 2. Backend API Testing

Use a tool like Postman or curl to test the endpoints:

#### Authentication (Get JWT Token)
```bash
curl -X POST http://localhost:8081/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"mobile":"YOUR_MOBILE", "password":"YOUR_PASSWORD"}'
```

#### Send Message
```bash
curl -X POST http://localhost:8081/api/chat/send \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "receiverId": 2,
    "messageText": "Hello! 👋",
    "messageType": "TEXT"
  }'
```

#### Get Conversations
```bash
curl -X GET http://localhost:8081/api/chat/conversations \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Get Messages
```bash
curl -X GET "http://localhost:8081/api/chat/conversation/1/messages?page=0&size=50" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### Mark as Read
```bash
curl -X POST http://localhost:8081/api/chat/conversation/1/mark-read \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Frontend Testing

1. **Login** to the application
2. **Add Friends** (required before chatting)
3. **Navigate to Chat Page** (`/chat`)
4. **Select a Friend** from the friends list
5. **Send Messages** - try text and emojis
6. **Check Real-time Updates** - messages should appear immediately
7. **Test Mobile View** - sidebar should collapse/expand properly

### 4. Integration Testing

#### Test Chat Workflow
1. Login as User A
2. Send message to User B
3. Login as User B 
4. Check unread count
5. Open conversation with User A
6. Messages should be marked as read
7. Reply to User A
8. Verify real-time updates

#### Test Edge Cases
1. **Friend Relationship** - Can only chat with friends
2. **Blocked Users** - Block/unblock functionality
3. **Empty States** - No friends, no messages
4. **Error Handling** - Network errors, invalid data

### 5. Performance Testing

#### Database Performance
```sql
-- Check query performance
EXPLAIN SELECT * FROM tbl_chat_messages WHERE conversation_id = 1 ORDER BY created_at DESC LIMIT 50;

-- Check indexes
SHOW INDEX FROM tbl_chat_messages;
```

#### Load Testing
- Test with multiple concurrent users
- Send high volume of messages
- Monitor database and API response times

## Expected Results

### Database
- ✅ All chat tables created successfully
- ✅ Foreign key constraints working
- ✅ Stored procedures created
- ✅ Indexes created for performance

### Backend APIs
- ✅ Authentication required for all endpoints
- ✅ Send message creates conversation if needed
- ✅ Messages retrieved in correct order
- ✅ Read status updated correctly
- ✅ Friend validation working
- ✅ Block/unblock functionality

### Frontend
- ✅ Friends list loads from API
- ✅ Chat interface responsive
- ✅ Real-time message updates
- ✅ Emoji picker working
- ✅ Mobile sidebar collapsible
- ✅ Message sending with proper types

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Check MySQL is running
   - Verify database credentials in application.properties
   - Ensure database exists

2. **JWT Token Issues**
   - Check token is included in headers
   - Verify token is not expired
   - Ensure user exists and is active

3. **Frontend API Errors**
   - Check CORS configuration
   - Verify API base URL
   - Check browser console for errors

4. **Messages Not Appearing**
   - Check friendship exists between users
   - Verify user is not blocked
   - Check database data integrity

### Debug Queries

```sql
-- Check conversations for a user
SELECT * FROM tbl_chat_conversations WHERE user_one_id = 1 OR user_two_id = 1;

-- Check messages in a conversation
SELECT * FROM tbl_chat_messages WHERE conversation_id = 1 ORDER BY created_at;

-- Check friendships
SELECT * FROM tbl_friendships WHERE (user1_id = 1 OR user2_id = 1) AND status = 'ACCEPTED';

-- Check blocks
SELECT * FROM tbl_chat_user_blocklist WHERE blocker_id = 1 OR blocked_id = 1;
```

## Performance Benchmarks

### Target Performance
- **Message Send**: < 200ms
- **Message Retrieval**: < 100ms  
- **Conversation List**: < 150ms
- **Friends List**: < 100ms

### Database Optimization
- Proper indexes on foreign keys
- Composite indexes for common queries
- Stored procedures for complex operations
- Connection pooling configured

## Security Checklist

- ✅ JWT authentication on all endpoints
- ✅ User authorization (only access own data)
- ✅ Friend relationship validation
- ✅ SQL injection prevention (JPA)
- ✅ Input validation and sanitization
- ✅ CORS properly configured
- ✅ Block functionality for safety