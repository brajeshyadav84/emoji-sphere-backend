# Group Management API Documentation

## Student APIs for Group Management

### Group Discovery APIs

#### 1. Discover Public Groups
**Endpoint:** `GET /api/groups/discover`
**Description:** Browse all public groups available for joining
**Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
**Response:** Paginated list of public groups

#### 2. Search Groups
**Endpoint:** `GET /api/groups/search`
**Description:** Search groups by name or description
**Parameters:**
- `q` (optional): Search term
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
**Response:** Paginated list of matching groups

#### 3. Get Popular Groups
**Endpoint:** `GET /api/groups/popular`
**Description:** Get popular groups sorted by member count
**Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
**Response:** Paginated list of popular groups

#### 4. Get Group Recommendations
**Endpoint:** `GET /api/groups/recommendations`
**Description:** Get personalized group recommendations
**Parameters:**
- `limit` (optional, default: 10): Number of recommendations
**Response:** List of recommended groups

### Group Membership APIs

#### 5. Check if Can Join Group
**Endpoint:** `GET /api/groups/{groupId}/can-join`
**Description:** Check if current user can join a specific group
**Response:** Information about whether user can join and why

#### 6. Join Group
**Endpoint:** `POST /api/groups/join`
**Description:** Join a public group or request to join private group
**Request Body:**
```json
{
    "groupId": 123
}
```
**Response:** Group membership details

#### 7. Leave Group
**Endpoint:** `POST /api/groups/{groupId}/leave`
**Description:** Leave a group you're currently a member of
**Response:** Success confirmation

#### 8. Get My Groups
**Endpoint:** `GET /api/groups/my-groups`
**Description:** Get all groups where current user is a member
**Response:** List of user's groups

### Group Information APIs

#### 9. Get Group Details
**Endpoint:** `GET /api/groups/{groupId}`
**Description:** Get detailed information about a specific group
**Response:** Group details including member count, admins, etc.

#### 10. Get Group Members
**Endpoint:** `GET /api/groups/{groupId}/members`
**Description:** Get list of group members (only if you're a member)
**Parameters:**
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
**Response:** Paginated list of group members

#### 11. Search Group Members
**Endpoint:** `GET /api/groups/{groupId}/members/search`
**Description:** Search within group members
**Parameters:**
- `q` (optional): Search term
- `page` (optional, default: 0): Page number
- `size` (optional, default: 10): Page size
**Response:** Paginated list of matching members

#### 12. Get Group Admins
**Endpoint:** `GET /api/groups/{groupId}/admins`
**Description:** Get list of group administrators
**Response:** List of group admins

## Admin-Only APIs

### Group Management
- `POST /api/groups` - Create new group
- `PUT /api/groups/{groupId}` - Update group details
- `DELETE /api/groups/{groupId}` - Delete group
- `GET /api/groups/created-by-me` - Get groups created by current user

### Member Management
- `DELETE /api/groups/{groupId}/members/{memberId}` - Remove member
- `POST /api/groups/members/remove-multiple` - Remove multiple members
- `POST /api/groups/{groupId}/members/{memberId}/promote` - Promote to admin
- `POST /api/groups/{groupId}/members/{memberId}/demote` - Demote from admin

## Authentication
All APIs require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Error Responses
All APIs return standardized error responses:
```json
{
    "success": false,
    "message": "Error description"
}
```

## Success Responses
All APIs return standardized success responses:
```json
{
    "success": true,
    "message": "Operation description",
    "data": { /* response data */ }
}
```