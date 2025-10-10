# Friendship API Documentation

This document describes the Friendship APIs that allow users to manage friend relationships, send friend requests, respond to requests, and maintain friendship statuses.

## Table of Contents
- [Database Structure](#database-structure)
- [Authentication](#authentication)
- [API Endpoints](#api-endpoints)
  - [Send Friend Request](#send-friend-request)
  - [Respond to Friend Request](#respond-to-friend-request)
  - [Get Friends List](#get-friends-list)
  - [Get Pending Requests Received](#get-pending-requests-received)
  - [Get Pending Requests Sent](#get-pending-requests-sent)
  - [Get All Friendships](#get-all-friendships)
  - [Remove Friend](#remove-friend)
  - [Block User](#block-user)
  - [Unblock User](#unblock-user)
  - [Get Friendship Status](#get-friendship-status)
  - [Get Friendship Counts](#get-friendship-counts)
- [Data Models](#data-models)
- [Business Rules](#business-rules)
- [Error Handling](#error-handling)

## Database Structure

### tbl_friendships Table
The friendship table maintains relationships between users with the following structure:

```sql
CREATE TABLE emoji_sphere.tbl_friendships (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id BIGINT NOT NULL,
    user2_id BIGINT NOT NULL,
    status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
    requester_id BIGINT NOT NULL,
    responder_id BIGINT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    responded_at DATETIME NULL,
    
    -- Constraints
    CONSTRAINT chk_user_order CHECK (user1_id < user2_id),
    UNIQUE KEY uk_friendship_pair (user1_id, user2_id),
    -- Foreign keys and indexes...
);
```

### Key Features:
- **Ordered User IDs**: `user1_id` is always less than `user2_id` to prevent duplicate pairs
- **Status Tracking**: PENDING → ACCEPTED/DECLINED/BLOCKED
- **Requester/Responder**: Track who initiated and who responded
- **Timestamps**: Creation, update, and response timestamps

## Authentication
All friendship APIs require authentication using JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## API Endpoints

### Send Friend Request

Send a friend request to another user.

**Endpoint:** `POST /api/friendships/send-request`

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "targetUserId": 123
}
```

**Response:**
```json
{
  "id": 1,
  "user1Id": 100,
  "user2Id": 123,
  "status": "PENDING",
  "requesterId": 100,
  "responderId": null,
  "createdAt": "2025-10-10T10:30:00",
  "updatedAt": "2025-10-10T10:30:00",
  "respondedAt": null,
  "user1": {
    "id": 100,
    "fullName": "John Doe",
    "email": "john@example.com",
    "mobileNumber": "+1234567890",
    "country": "USA",
    "schoolName": "Harvard University",
    "age": 25,
    "gender": "Male",
    "isActive": true
  },
  "user2": {
    "id": 123,
    "fullName": "Jane Smith",
    "email": "jane@example.com",
    "mobileNumber": "+1234567891",
    "country": "USA",
    "schoolName": "MIT",
    "age": 24,
    "gender": "Female",
    "isActive": true
  },
  "requester": {
    "id": 100,
    "fullName": "John Doe",
    // ... other user fields
  },
  "responder": null,
  "otherUserId": 123,
  "otherUser": {
    "id": 123,
    "fullName": "Jane Smith",
    // ... other user fields
  },
  "canRespond": false,
  "isSentByCurrentUser": true
}
```

**Status Codes:**
- `200 OK` - Friend request sent successfully
- `400 Bad Request` - Validation error or business rule violation
- `401 Unauthorized` - Invalid or missing JWT token

### Respond to Friend Request

Accept, decline, or block a friend request.

**Endpoint:** `POST /api/friendships/respond`

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "friendshipId": 1,
  "response": "ACCEPTED"
}
```

**Valid responses:** `ACCEPTED`, `DECLINED`, `BLOCKED`

**Response:** Same structure as Send Friend Request with updated status and responder information.

### Get Friends List

Get all accepted friendships (friends) for the current user.

**Endpoint:** `GET /api/friendships/friends?page=0&size=10`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Query Parameters:**
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)

**Response:**
```json
{
  "friends": [
    {
      "id": 1,
      "user1Id": 100,
      "user2Id": 123,
      "status": "ACCEPTED",
      "requesterId": 100,
      "responderId": 123,
      "createdAt": "2025-10-10T10:30:00",
      "updatedAt": "2025-10-10T11:00:00",
      "respondedAt": "2025-10-10T11:00:00",
      "otherUserId": 123,
      "otherUser": {
        "id": 123,
        "fullName": "Jane Smith",
        // ... other user fields
      },
      "canRespond": false,
      "isSentByCurrentUser": true
    }
  ],
  "currentPage": 0,
  "totalItems": 5,
  "totalPages": 1,
  "hasNext": false,
  "hasPrevious": false
}
```

### Get Pending Requests Received

Get friend requests that the current user has received and needs to respond to.

**Endpoint:** `GET /api/friendships/pending/received?page=0&size=10`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:** Same structure as Get Friends List, but with pending requests where `canRespond` is `true`.

### Get Pending Requests Sent

Get friend requests that the current user has sent and are still pending.

**Endpoint:** `GET /api/friendships/pending/sent?page=0&size=10`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:** Same structure as Get Friends List, but with pending requests where `isSentByCurrentUser` is `true`.

### Get All Friendships

Get all friendship relationships for the current user (all statuses).

**Endpoint:** `GET /api/friendships/all?page=0&size=10`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:** Same structure as Get Friends List, but includes all friendship statuses.

### Remove Friend

Remove/unfriend an accepted friendship.

**Endpoint:** `DELETE /api/friendships/remove/{friendId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `friendId`: ID of the friend to remove

**Response:**
```json
{
  "message": "Friend removed successfully"
}
```

### Block User

Block a user (creates or updates friendship to BLOCKED status).

**Endpoint:** `POST /api/friendships/block/{userId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `userId`: ID of the user to block

**Response:**
```json
{
  "message": "User blocked successfully"
}
```

### Unblock User

Remove a block on a user (deletes the blocked friendship).

**Endpoint:** `POST /api/friendships/unblock/{userId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `userId`: ID of the user to unblock

**Response:**
```json
{
  "message": "User unblocked successfully"
}
```

### Get Friendship Status

Check the friendship status between current user and another user.

**Endpoint:** `GET /api/friendships/status/{userId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Path Parameters:**
- `userId`: ID of the user to check status with

**Response:**
```json
{
  "areFriends": true,
  "friendshipExists": true
}
```

### Get Friendship Counts

Get counts of friends and pending requests for the current user.

**Endpoint:** `GET /api/friendships/counts`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "friendsCount": 15,
  "pendingRequestsCount": 3
}
```

## Data Models

### FriendshipResponse
```json
{
  "id": "Long",
  "user1Id": "Long",
  "user2Id": "Long", 
  "status": "String (PENDING|ACCEPTED|DECLINED|BLOCKED)",
  "requesterId": "Long",
  "responderId": "Long|null",
  "createdAt": "DateTime",
  "updatedAt": "DateTime",
  "respondedAt": "DateTime|null",
  "user1": "UserBasicInfo",
  "user2": "UserBasicInfo",
  "requester": "UserBasicInfo",
  "responder": "UserBasicInfo|null",
  "otherUserId": "Long",
  "otherUser": "UserBasicInfo",
  "canRespond": "Boolean",
  "isSentByCurrentUser": "Boolean"
}
```

### UserBasicInfo
```json
{
  "id": "Long",
  "fullName": "String",
  "email": "String",
  "mobileNumber": "String",
  "country": "String",
  "schoolName": "String",
  "age": "Integer",
  "gender": "String",
  "isActive": "Boolean"
}
```

## Business Rules

### Friendship Creation
1. Users cannot send friend requests to themselves
2. Only one friendship record can exist between any two users
3. User IDs are automatically ordered (user1_id < user2_id) to prevent duplicates
4. Both users must be active to create friendships

### Friendship Status Flow
```
PENDING → ACCEPTED (friend accepts request)
PENDING → DECLINED (friend declines request)
PENDING → BLOCKED (friend blocks requester)
ACCEPTED → [deleted] (unfriend action)
ANY_STATUS → BLOCKED (block action)
BLOCKED → [deleted] (unblock action)
```

### Response Rules
1. Only the non-requester can respond to a pending request
2. Only pending requests can be responded to
3. Accepted friendships can be removed by either user
4. Any user can block any other user (creates or updates to BLOCKED)

### Data Integrity
1. user1_id is always less than user2_id
2. requester_id must be either user1_id or user2_id
3. responder_id (when not null) must be either user1_id or user2_id
4. requester_id and responder_id must be different

## Error Handling

### Common Error Responses
```json
{
  "message": "Error: [specific error message]"
}
```

### Error Scenarios
- **400 Bad Request**: Validation errors, business rule violations
- **401 Unauthorized**: Missing or invalid JWT token
- **404 Not Found**: Friendship or user not found
- **500 Internal Server Error**: Database or server errors

### Specific Error Messages
- "Cannot send friend request to yourself"
- "Friend request already sent"
- "Users are already friends"
- "Friend request was previously declined"
- "Cannot send friend request - blocked"
- "Cannot respond to this friend request"
- "Friendship request not found"
- "Users are not friends"
- "User is not blocked"
- "Invalid response: [response]"

## Usage Examples

### Send Friend Request
```bash
curl -X POST "http://localhost:8080/api/friendships/send-request" \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"targetUserId": 123}'
```

### Accept Friend Request
```bash
curl -X POST "http://localhost:8080/api/friendships/respond" \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"friendshipId": 1, "response": "ACCEPTED"}'
```

### Get Friends List
```bash
curl -X GET "http://localhost:8080/api/friendships/friends?page=0&size=10" \
  -H "Authorization: Bearer <jwt_token>"
```

### Block User
```bash
curl -X POST "http://localhost:8080/api/friendships/block/123" \
  -H "Authorization: Bearer <jwt_token>"
```

## Database Queries Examples

### Get User's Friends
```sql
SELECT * FROM emoji_sphere.v_friendships 
WHERE (user1_id = ? OR user2_id = ?) 
AND status = 'ACCEPTED';
```

### Get Pending Requests for User
```sql
SELECT * FROM emoji_sphere.v_friendships 
WHERE (user1_id = ? OR user2_id = ?) 
AND requester_id != ? 
AND status = 'PENDING';
```

### Check if Users are Friends
```sql
SELECT COUNT(*) > 0 FROM tbl_friendships 
WHERE ((user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)) 
AND status = 'ACCEPTED';
```