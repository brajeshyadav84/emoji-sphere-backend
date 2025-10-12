# Emoji Sphere Social Media API Documentation

## Overview
Complete social media API with mobile-based authentication, posts, comments, likes, and real-time social interactions.

## Base URL
```
http://localhost:8081/api
```

## Authentication
All authenticated endpoints require a Bearer token in the Authorization header:
```
Authorization: Bearer <JWT_TOKEN>
```

---

## 🔐 Authentication Endpoints

### Send OTP
```http
POST /auth/send-otp
Content-Type: application/json

{
  "mobile": "+1 (555) 123-4567"
}
```

### Verify OTP
```http
POST /auth/verify-otp
Content-Type: application/json

{
  "mobile": "+1 (555) 123-4567",
  "otp": "123456"
}
```

### Register User
```http
POST /auth/signup
Content-Type: application/json

{
  "name": "John Doe",
  "mobile": "+1 (555) 123-4567",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "age": 25,
  "location": "United States",
  "gender": "male"
}
```

### Login
```http
POST /auth/signin
Content-Type: application/json

{
  "mobile": "+1 (555) 123-4567",
  "password": "password123"
}
```

### Forgot Password
```http
POST /auth/forgot-password
Content-Type: application/json

{
  "mobile": "+1 (555) 123-4567"
}
```

### Reset Password
```http
POST /auth/reset-password
Content-Type: application/json

{
  "mobile": "+1 (555) 123-4567",
  "otp": "123456",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

---

## 📝 Post Endpoints

### Share/Create Post (Social Style)
```http
POST /posts/share
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Just finished my rainbow painting! 🌈 It has all my favorite colors. What's your favorite color? 💙💚💛🧡💜❤️",
  "emojiContent": "🌈💙💚💛🧡💜❤️",
  "imageUrl": "https://example.com/image.jpg",
  "isPublic": true,
  "categoryId": 3,
  "tags": ["art", "painting", "rainbow"]
}
```

### Get All Posts (Feed)
```http
GET /posts?page=0&size=10&sortBy=createdAt&sortDir=desc
```

**Response:**
```json
{
  "content": [
    {
      "id": 1,
      "title": "Just finished my rainbow painting!...",
      "content": "Just finished my rainbow painting! 🌈 It has all my favorite colors. What's your favorite color? 💙💚💛🧡💜❤️",
      "emojiContent": "🌈💙💚💛🧡💜❤️",
      "imageUrl": "https://example.com/image.jpg",
      "isPublic": true,
      "likesCount": 24,
      "commentsCount": 8,
      "author": {
        "id": 1,
        "name": "Alex the Artist",
        "mobile": "+1234567890"
      },
      "category": {
        "id": 3,
        "name": "Art",
        "icon": "🎨"
      },
      "isLikedByCurrentUser": false,
      "createdAt": "2025-10-03T14:30:00",
      "updatedAt": "2025-10-03T14:30:00"
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "number": 0,
  "size": 10
}
```

### Get Post with Comments
```http
GET /posts/1/with-comments
```

**Response includes recent comments:**
```json
{
  "id": 1,
  "content": "Just finished my rainbow painting!...",
  "likesCount": 24,
  "commentsCount": 8,
  "recentComments": [
    {
      "id": 1,
      "content": "Beautiful work! I love the colors 🎨",
      "user": {
        "name": "Art Lover",
        "mobile": "+1987654321"
      },
      "likesCount": 5,
      "isLikedByCurrentUser": false,
      "createdAt": "2025-10-03T15:00:00",
      "replies": []
    }
  ],
  "hasMoreComments": true
}
```

### Like/Unlike Post
```http
POST /posts/1/like
Authorization: Bearer <token>
```

### Get User's Posts
```http
GET /posts/user/+1234567890?page=0&size=10
```

### Search Posts
```http
GET /posts/search?keyword=rainbow&page=0&size=10
```

### Get Trending Posts
```http
GET /posts/trending
```

---

## 💬 Comment Endpoints

### Create Comment
```http
POST /comments/posts/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Beautiful work! I love the colors 🎨"
}
```

### Reply to Comment
```http
POST /comments/posts/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Thank you! 😊",
  "parentCommentId": 1
}
```

### Get Comments for Post
```http
GET /comments/posts/1?page=0&size=10
```

**Response with nested replies:**
```json
{
  "content": [
    {
      "id": 1,
      "content": "Beautiful work! I love the colors 🎨",
      "user": {
        "id": 2,
        "name": "Art Lover",
        "mobile": "+1987654321"
      },
      "postId": 1,
      "parentCommentId": null,
      "likesCount": 5,
      "isLikedByCurrentUser": false,
      "createdAt": "2025-10-03T15:00:00",
      "replies": [
        {
          "id": 2,
          "content": "Thank you! 😊",
          "user": {
            "id": 1,
            "name": "Alex the Artist",
            "mobile": "+1234567890"
          },
          "postId": 1,
          "parentCommentId": 1,
          "likesCount": 2,
          "isLikedByCurrentUser": true,
          "createdAt": "2025-10-03T15:05:00",
          "replies": []
        }
      ]
    }
  ]
}
```

### Get Replies for Comment
```http
GET /comments/1/replies
```

### Like/Unlike Comment
```http
POST /comments/1/like
Authorization: Bearer <token>
```

### Update Comment
```http
PUT /comments/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Updated comment content 😊"
}
```

### Delete Comment
```http
DELETE /comments/1
Authorization: Bearer <token>
```

---

## 📂 Category Endpoints

### Get All Categories
```http
GET /categories
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "General",
    "description": "General posts and discussions",
    "color": "#3B82F6",
    "icon": "💬",
    "isActive": true
  },
  {
    "id": 2,
    "name": "Humor",
    "description": "Funny posts and memes",
    "color": "#F59E0B",
    "icon": "😂",
    "isActive": true
  }
]
```

---

## 🚀 Frontend Integration Guide

### 1. User Registration Flow
```javascript
// Step 1: Send OTP
const sendOtp = async (mobile) => {
  const response = await fetch('/api/auth/send-otp', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ mobile })
  });
  return response.json();
};

// Step 2: Verify OTP
const verifyOtp = async (email, otp) => {
  const response = await fetch('/api/auth/verify-otp', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, otp })
  });
  return response.json();
};

// Step 3: Register User
const registerUser = async (userData) => {
  const response = await fetch('/api/auth/signup', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(userData)
  });
  return response.json();
};
```

### 2. Social Feed Implementation
```javascript
// Get posts with pagination
const getPosts = async (page = 0, size = 10) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`/api/posts?page=${page}&size=${size}`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};

// Share a post
const sharePost = async (postData) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch('/api/posts/share', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(postData)
  });
  return response.json();
};

// Like a post
const likePost = async (postId) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`/api/posts/${postId}/like`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

### 3. Comment System
```javascript
// Add comment
const addComment = async (postId, content, parentCommentId = null) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`/api/comments/posts/${postId}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify({ content, parentCommentId })
  });
  return response.json();
};

// Get comments
const getComments = async (postId) => {
  const response = await fetch(`/api/comments/posts/${postId}`);
  return response.json();
};

// Like comment
const likeComment = async (commentId) => {
  const token = localStorage.getItem('authToken');
  const response = await fetch(`/api/comments/${commentId}/like`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.json();
};
```

---

## 🔧 Error Handling

All endpoints return consistent error responses:

```json
{
  "message": "Error description",
  "timestamp": "2025-10-03T15:30:00Z",
  "status": 400
}
```

### Common HTTP Status Codes:
- `200` - Success
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid/missing token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `500` - Internal Server Error

---

## 📱 Mobile Number Format
The API accepts mobile numbers in various formats:
- `+1 (555) 123-4567`
- `+15551234567`
- `15551234567`
- `5551234567`

---

## 🎯 Real-time Features (Future Enhancement)
- WebSocket support for real-time comments
- Live notification system
- Real-time like updates
- Online user presence

This API provides a complete social media backend that matches your frontend design perfectly! 🚀