# Chat System API Documentation

## Overview

The Chat System provides real-time one-to-one messaging capabilities between friends in the Emoji Sphere application. All APIs require JWT authentication and follow RESTful principles.

## Base URL
```
http://localhost:8081/api/chat
```

## Authentication

All endpoints require a valid JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## API Endpoints

### 1. Send Message

Send a message to a friend.

**Endpoint:** `POST /send`

**Request Body:**
```json
{
  "receiverId": 2,
  "messageText": "Hello! How are you? 😊",
  "messageType": "TEXT"
}
```

**Request Fields:**
- `receiverId` (required): ID of the user to send message to
- `messageText` (required): The message content (max 1000 characters)
- `messageType` (optional): Message type - TEXT, EMOJI, IMAGE, FILE (default: TEXT)

**Response:**
```json
{
  "id": 123,
  "conversationId": 45,
  "senderId": 1,
  "receiverId": 2,
  "messageText": "Hello! How are you? 😊",
  "messageType": "TEXT",
  "isRead": false,
  "createdAt": "2025-10-12T10:30:00",
  "updatedAt": "2025-10-12T10:30:00",
  "sender": {
    "id": 1,
    "fullName": "John Doe",
    "gender": "male",
    "isActive": true
  },
  "receiver": {
    "id": 2,
    "fullName": "Jane Smith", 
    "gender": "female",
    "isActive": true
  }
}
```

**Business Rules:**
- Users can only send messages to friends
- Sender cannot be blocked by receiver
- Message will create conversation if it doesn't exist

---

### 2. Get Conversations

Get list of user's conversations.

**Endpoint:** `GET /conversations`

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "conversations": [
    {
      "id": 45,
      "userOneId": 1,
      "userTwoId": 2,
      "createdAt": "2025-10-12T09:00:00",
      "updatedAt": "2025-10-12T10:30:00",
      "otherUserId": 2,
      "otherUser": {
        "id": 2,
        "fullName": "Jane Smith",
        "gender": "female",
        "isActive": true
      },
      "unreadCount": 3,
      "lastMessage": "Hello! How are you? 😊",
      "lastMessageTime": "2025-10-12T10:30:00",
      "notificationsEnabled": true,
      "archived": false,
      "mutedUntil": null
    }
  ],
  "currentPage": 0,
  "totalPages": 1,
  "totalItems": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

---

### 3. Get Messages

Get messages for a specific conversation.

**Endpoint:** `GET /conversation/{conversationId}/messages`

**Path Parameters:**
- `conversationId`: ID of the conversation

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 50)

**Response:**
```json
{
  "messages": [
    {
      "id": 123,
      "conversationId": 45,
      "senderId": 1,
      "receiverId": 2,
      "messageText": "Hello! How are you? 😊",
      "messageType": "TEXT",
      "isRead": true,
      "createdAt": "2025-10-12T10:30:00",
      "updatedAt": "2025-10-12T10:35:00",
      "sender": {
        "id": 1,
        "fullName": "John Doe",
        "gender": "male",
        "isActive": true
      },
      "receiver": {
        "id": 2,
        "fullName": "Jane Smith",
        "gender": "female", 
        "isActive": true
      }
    }
  ],
  "currentPage": 0,
  "totalPages": 1,
  "totalItems": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

**Notes:**
- Messages are ordered by creation time (oldest first)
- User must be part of the conversation to access messages

---

### 4. Mark Messages as Read

Mark all unread messages in a conversation as read.

**Endpoint:** `POST /conversation/{conversationId}/mark-read`

**Path Parameters:**
- `conversationId`: ID of the conversation

**Response:**
```json
{
  "message": "Messages marked as read"
}
```

---

### 5. Get Unread Message Count

Get total unread message count for the current user.

**Endpoint:** `GET /unread-count`

**Response:**
```json
{
  "unreadCount": 5
}
```

---

### 6. Block User

Block a user from sending messages.

**Endpoint:** `POST /block/{userId}`

**Path Parameters:**
- `userId`: ID of the user to block

**Response:**
```json
{
  "message": "User blocked successfully"
}
```

**Business Rules:**
- Cannot block yourself
- Blocked user cannot send messages to blocker
- Existing conversations remain accessible

---

### 7. Unblock User

Unblock a previously blocked user.

**Endpoint:** `POST /unblock/{userId}`

**Path Parameters:**
- `userId`: ID of the user to unblock

**Response:**
```json
{
  "message": "User unblocked successfully"
}
```

---

### 8. Start Conversation

Start a conversation with a friend by sending an initial message.

**Endpoint:** `POST /start/{friendId}`

**Path Parameters:**
- `friendId`: ID of the friend to start conversation with

**Response:**
```json
{
  "message": "Conversation started",
  "conversationId": 45,
  "initialMessage": {
    "id": 123,
    "conversationId": 45,
    "senderId": 1,
    "receiverId": 2,
    "messageText": "👋 Hi there!",
    "messageType": "EMOJI",
    "isRead": false,
    "createdAt": "2025-10-12T10:30:00",
    "updatedAt": "2025-10-12T10:30:00"
  }
}
```

**Notes:**
- Automatically sends a friendly initial message
- Creates conversation if it doesn't exist
- Returns existing conversation if already exists

---

## Data Models

### ChatMessage
```typescript
interface ChatMessage {
  id: number;
  conversationId: number;
  senderId: number;
  receiverId: number;
  messageText: string;
  messageType: 'TEXT' | 'EMOJI' | 'IMAGE' | 'FILE';
  isRead: boolean;
  createdAt: string;
  updatedAt: string;
  sender?: UserBasicInfo;
  receiver?: UserBasicInfo;
}
```

### Conversation
```typescript
interface Conversation {
  id: number;
  userOneId: number;
  userTwoId: number;
  createdAt: string;
  updatedAt: string;
  otherUserId: number;
  otherUser?: UserBasicInfo;
  unreadCount: number;
  lastMessage?: string;
  lastMessageTime?: string;
  notificationsEnabled: boolean;
  archived: boolean;
  mutedUntil?: string;
}
```

### UserBasicInfo
```typescript
interface UserBasicInfo {
  id: number;
  fullName: string;
  gender: string;
  isActive: boolean;
}
```

---

## Error Handling

### Error Response Format
```json
{
  "error": "Error message describing what went wrong"
}
```

### Common Error Scenarios

#### 400 Bad Request
- Invalid request data
- Missing required fields
- User not friends with recipient
- User is blocked by recipient
- Cannot send message to yourself

#### 401 Unauthorized
- Missing or invalid JWT token
- Token expired

#### 404 Not Found
- Conversation not found
- User not found
- Friend not found

#### 500 Internal Server Error
- Database connection issues
- Unexpected server errors

---

## Rate Limiting

No specific rate limiting is currently implemented, but consider implementing:
- Message sending: 60 messages per minute per user
- API calls: 1000 requests per hour per user

---

## Real-time Updates

The current implementation uses polling for updates. For real-time messaging, consider implementing:
- WebSocket connections
- Server-Sent Events (SSE)
- Push notifications

---

## Performance Considerations

### Database Optimization
- Indexed columns for fast queries
- Stored procedures for complex operations
- Connection pooling

### API Performance
- Pagination for large result sets
- Efficient queries with proper joins
- Caching for frequently accessed data

### Frontend Integration
- Debounced search inputs
- Lazy loading of conversations
- Optimistic UI updates

---

## Security Features

### Authentication & Authorization
- JWT token validation on all endpoints
- User can only access own conversations
- Friend relationship validation

### Data Protection
- Input validation and sanitization
- SQL injection prevention through JPA
- XSS protection through proper encoding

### Privacy Controls
- Block/unblock functionality
- Conversation settings (mute, archive)
- Friend-only messaging

---

## Testing

See `CHAT_SYSTEM_TESTING_GUIDE.md` for comprehensive testing instructions.

---

## Future Enhancements

### Planned Features
- Group messaging
- Message reactions
- File/image attachments
- Voice messages
- Message search
- Message threads/replies
- Read receipts
- Typing indicators
- Message encryption

### Scalability Improvements
- Message archiving for old conversations
- Database partitioning
- Microservice architecture
- Real-time WebSocket implementation
- Push notification service