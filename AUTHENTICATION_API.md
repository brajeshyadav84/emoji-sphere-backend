# Authentication API Documentation

This document provides comprehensive information about all authentication endpoints for the Emoji Sphere application.

## Base URL
```
http://localhost:8081/api/auth
```

## Endpoints

### 1. Send OTP
Send OTP to a mobile number for verification.

**Endpoint:** `POST /send-otp`

**Request Body:**
```json
{
  "mobile": "9876543210"
}
```

**Response:**
```json
{
  "message": "OTP sent successfully to 9876543210"
}
```

**Error Response:**
```json
{
  "message": "Error: Failed to send OTP. [error details]"
}
```

---

### 2. Verify OTP
Verify the OTP sent to the mobile number.

**Endpoint:** `POST /verify-otp`

**Request Body:**
```json
{
  "mobile": "9876543210",
  "otp": "123456"
}
```

**Success Response:**
```json
{
  "message": "OTP verified successfully"
}
```

**Error Response:**
```json
{
  "message": "Error: Invalid or expired OTP"
}
```

---

### 3. User Registration (Sign Up)
Register a new user after OTP verification.

**Endpoint:** `POST /signup`

**Request Body:**
```json
{
  "fullName": "John Doe",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "age": 25,
  "country": "India",
  "gender": "Male"
}
```

**Success Response:**
```json
{
  "message": "User registered successfully!"
}
```

**Error Responses:**
```json
{
  "message": "Error: Password and confirm password do not match!"
}
```
```json
{
  "message": "Error: Mobile number is already registered!"
}
```
```json
{
  "message": "Error: Email is already in use!"
}
```
```json
{
  "message": "Error: Please verify your mobile number with OTP first!"
}
```

---

### 4. User Login (Sign In)
Authenticate user and get JWT token.

**Endpoint:** `POST /signin`

**Request Body:**
```json
{
  "mobile": "9876543210",
  "password": "password123"
}
```

**Success Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "fullName": "John Doe",
  "mobile": "9876543210",
  "email": "john.doe@example.com",
  "role": "USER",
  "roles": ["USER"]
}
```

**Error Response:**
```json
{
  "message": "Error: Invalid credentials"
}
```

---

### 5. Forgot Password
Send OTP for password reset.

**Endpoint:** `POST /forgot-password`

**Request Body:**
```json
{
  "mobile": "9876543210"
}
```

**Success Response:**
```json
{
  "message": "Password reset OTP sent successfully to 9876543210"
}
```

**Error Response:**
```json
{
  "message": "Error: No account found with this mobile number!"
}
```

---

### 6. Reset Password
Reset password using OTP verification.

**Endpoint:** `POST /reset-password`

**Request Body:**
```json
{
  "mobile": "9876543210",
  "otp": "123456",
  "newPassword": "newpassword123",
  "confirmPassword": "newpassword123"
}
```

**Success Response:**
```json
{
  "message": "Password reset successfully!"
}
```

**Error Responses:**
```json
{
  "message": "Error: Invalid or expired OTP"
}
```
```json
{
  "message": "Error: Password and confirm password do not match!"
}
```

---

## Testing Endpoints

For testing purposes, the following endpoints are available:

### Base URL for Testing
```
http://localhost:8081/api/test
```

### 1. Create Test User
**Endpoint:** `GET /create-test-user`

Creates a test user with:
- Mobile: 9876543210
- Password: password123
- Name: John Doe

### 2. Simulate OTP Generation
**Endpoint:** `POST /simulate-otp`

**Request Body:**
```json
{
  "mobile": "9876543210"
}
```

Returns the actual OTP for testing purposes.

### 3. Get All Users
**Endpoint:** `GET /users`

Returns all users in the database.

### 4. Get User by Mobile
**Endpoint:** `GET /user-by-mobile/{mobile}`

Returns user details for the given mobile number.

---

## Authentication Flow

### Complete Registration Flow:
1. **Send OTP**: `POST /auth/send-otp`
2. **Verify OTP**: `POST /auth/verify-otp`
3. **Register**: `POST /auth/signup`

### Login Flow:
1. **Login**: `POST /auth/signin`
2. Use the returned JWT token in Authorization header: `Bearer {token}`

### Password Reset Flow:
1. **Forgot Password**: `POST /auth/forgot-password`
2. **Verify OTP**: `POST /auth/verify-otp`
3. **Reset Password**: `POST /auth/reset-password`

---

## Database Schema

The refactored entities now match the database schema shown in the screenshot:

### tbl_users
- `id` (Primary Key)
- `full_name`
- `age`
- `gender`
- `country`
- `email`
- `mobile_number`
- `password_hash`
- `is_verified`
- `role`
- `created_at`

### Other Tables
- `tbl_chat_conversations`
- `tbl_chat_messages`
- `tbl_chat_user_blocklist`
- `tbl_posts`
- `tbl_comments`
- `tbl_replies`
- `tbl_likes`
- `tbl_groups`
- `tbl_group_members`

---

## Error Codes

- **400 Bad Request**: Invalid input data or business logic error
- **401 Unauthorized**: Authentication failed
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Server error

---

## Notes

1. All OTPs expire after 5 minutes
2. JWT tokens expire after 24 hours (86400000 ms)
3. Mobile numbers must be unique
4. Email addresses must be unique (if provided)
5. Passwords must be at least 6 characters long
6. OTP verification is required before registration
7. The system uses mobile number as the primary identifier for authentication

---

## Running the Application

1. Ensure MySQL is running with the database `emoji_sphere`
2. Update database credentials in `application.properties`
3. Run the Spring Boot application: `mvn spring-boot:run`
4. The API will be available at: `http://localhost:8081/api`