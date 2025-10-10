# User Profile API Documentation

This document describes the User Profile APIs that allow users to view and update their profile information, including the new `school_name` field.

## Table of Contents
- [Get User Profile](#get-user-profile)
- [Update User Profile](#update-user-profile)
- [Get Public User Profile](#get-public-user-profile)
- [Updated Signup API](#updated-signup-api)

## Authentication
All profile APIs (except public profile) require authentication using JWT token in the Authorization header:
```
Authorization: Bearer <jwt_token>
```

## Get User Profile

Retrieves the current user's complete profile information.

**Endpoint:** `GET /api/user/profile`

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Response:**
```json
{
  "id": 1,
  "mobileNumber": "+1234567890",
  "fullName": "John Doe",
  "age": 25,
  "gender": "Male",
  "country": "United States",
  "schoolName": "Harvard University",
  "email": "john.doe@example.com",
  "isVerified": true,
  "isActive": true,
  "role": "USER",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-20T15:45:00"
}
```

**Status Codes:**
- `200 OK` - Profile retrieved successfully
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - User not found

## Update User Profile

Updates the current user's profile information. All fields are optional.

**Endpoint:** `PUT /api/user/profile`

**Headers:**
```
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "John Smith",
  "email": "john.smith@example.com",
  "age": 26,
  "country": "Canada",
  "gender": "Male",
  "schoolName": "University of Toronto"
}
```

**Request Body Fields:**
- `fullName` (string, optional): User's full name (2-100 characters)
- `email` (string, optional): User's email address (valid email format, max 50 characters)
- `age` (integer, optional): User's age
- `country` (string, optional): User's country (max 100 characters)
- `gender` (string, optional): User's gender (max 10 characters)
- `schoolName` (string, optional): User's school name (max 255 characters)

**Response:**
```json
{
  "id": 1,
  "mobileNumber": "+1234567890",
  "fullName": "John Smith",
  "age": 26,
  "gender": "Male",
  "country": "Canada",
  "schoolName": "University of Toronto",
  "email": "john.smith@example.com",
  "isVerified": true,
  "isActive": true,
  "role": "USER",
  "createdAt": "2025-01-15T10:30:00",
  "updatedAt": "2025-01-20T16:00:00"
}
```

**Status Codes:**
- `200 OK` - Profile updated successfully
- `400 Bad Request` - Validation error or email already in use
- `401 Unauthorized` - Invalid or missing JWT token
- `404 Not Found` - User not found

**Error Response Examples:**
```json
{
  "message": "Error: Email is already in use by another user!"
}
```

## Get Public User Profile

Retrieves limited public profile information for any user by their ID.

**Endpoint:** `GET /api/user/profile/{userId}`

**Headers:**
```
Content-Type: application/json
```

**Path Parameters:**
- `userId` (integer): The ID of the user whose profile to retrieve

**Response:**
```json
{
  "id": 1,
  "fullName": "John Smith",
  "age": 26,
  "gender": "Male",
  "country": "Canada",
  "schoolName": "University of Toronto",
  "isActive": true,
  "createdAt": "2025-01-15T10:30:00"
}
```

**Note:** This endpoint returns only public information. Sensitive data like email, mobile number, verification status, etc., are not included.

**Status Codes:**
- `200 OK` - Profile retrieved successfully
- `404 Not Found` - User not found

## Updated Signup API

The signup API has been updated to include the new `school_name` field.

**Endpoint:** `POST /auth/signup`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "fullName": "John Doe",
  "mobile": "+1234567890",
  "email": "john.doe@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123",
  "age": 25,
  "country": "United States",
  "gender": "Male",
  "schoolName": "Harvard University"
}
```

**Request Body Fields:**
- `fullName` (string, required): User's full name (2-100 characters)
- `mobile` (string, required): User's mobile number (max 20 characters)
- `email` (string, optional): User's email address (valid email format, max 50 characters)
- `password` (string, required): User's password (6-40 characters)
- `confirmPassword` (string, required): Password confirmation (must match password)
- `age` (integer, optional): User's age
- `country` (string, optional): User's country (max 100 characters)
- `gender` (string, required): User's gender (max 10 characters)
- `schoolName` (string, optional): User's school name (max 255 characters) - **NEW FIELD**

**Response:**
```json
{
  "message": "User registered successfully!"
}
```

**Status Codes:**
- `200 OK` - User registered successfully
- `400 Bad Request` - Validation error, mobile/email already registered, or OTP not verified

## Error Handling

All APIs return error responses in the following format:
```json
{
  "message": "Error description"
}
```

Common error scenarios:
- **401 Unauthorized**: JWT token is missing, invalid, or expired
- **400 Bad Request**: Validation errors, duplicate email/mobile, etc.
- **404 Not Found**: Requested user not found
- **500 Internal Server Error**: Server-side errors

## Validation Rules

### Field Validation:
- **fullName**: 2-100 characters, required for signup
- **mobile**: Max 20 characters, required and unique
- **email**: Valid email format, max 50 characters, unique
- **password**: 6-40 characters, required for signup
- **age**: Integer value
- **country**: Max 100 characters
- **gender**: Max 10 characters, required for signup
- **schoolName**: Max 255 characters, optional

### Security Notes:
- Mobile numbers and emails must be unique across all users
- Passwords are encrypted before storage
- JWT tokens are required for authenticated endpoints
- OTP verification is required before signup
- Email uniqueness is checked during profile updates

## Usage Examples

### Update only school name:
```bash
curl -X PUT "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{"schoolName": "MIT"}'
```

### Update multiple fields:
```bash
curl -X PUT "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer <jwt_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Jane Smith",
    "email": "jane.smith@example.com",
    "schoolName": "Stanford University",
    "country": "United States"
  }'
```

### Get current user profile:
```bash
curl -X GET "http://localhost:8080/api/user/profile" \
  -H "Authorization: Bearer <jwt_token>"
```